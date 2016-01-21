package client.anticheat;

import client.MapleBuffStat;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import constants.GameConstants;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import static constants.ServerConstants.BANTYPE_DC;
import handling.world.World;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.AutobanManager;
import server.Timer.CheatTimer;
import tools.MaplePacketCreator;
import tools.StringUtil;
import static constants.ServerConstants.BANTYPE_ENABLE;

public class CheatTracker {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock(), wL = lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<CheatingOffense, CheatingOffenseEntry>();
    private final WeakReference<MapleCharacter> chr;

    /**
     * 偵測攻擊速度變數
     */
    private int lastAttackTickCount = 0;
    private byte attackResetCount = 0;

    private long serverClientAttackTickCountDiff = 0;
    private long lastDamage = 0;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0;
    private ScheduledFuture<?> invalidationTask;

    private int lastTickCount = 0, tickSame = 0;
    private long lastASmegaTime = 0;
    private long[] lastTime = new long[6];

    public CheatTracker(final MapleCharacter chr) {
        this.chr = new WeakReference<MapleCharacter>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    /**
     * 檢查攻擊延遲
     *
     * @param skillID
     * @param tickCoint
     */
    public final void checkAttackDelay(final int skillID, final int tickCoint) {
        short attackDelay = GameConstants.getAttackDelay(skillID);
        /**
         * 檢查客戶端傳回的攻擊時間
         */
        if (chr.get().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
            attackDelay = (short)(int)(attackDelay / 1.5);
        }
        if (chr.get().getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
            attackDelay = (short)(attackDelay / 6);
        }
        if (chr.get().getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null) {
            attackDelay = (short)(int)(attackDelay / 1.35);
        }
        if (GameConstants.isAran(chr.get().getJob())) {
              attackDelay = (short)(int)(attackDelay / 1.3);
        }
        if ((tickCoint - lastAttackTickCount) < attackDelay) {
            registerOffense(CheatingOffense.攻擊速度過快_客戶端);
        }
        /**
         * 檢查伺服器端的攻擊時間，阻擋更改客戶端時間加速
         *
         * ServerClientAttackTickCountDiff 伺服器與客戶端時間差距
         */
        final long STime_TC = System.currentTimeMillis() - tickCoint; // hack = - more
        if (serverClientAttackTickCountDiff - STime_TC > 300) { // 250 is the ping, TODO
            registerOffense(CheatingOffense.攻擊速度過快_伺服器端);
        }

//	System.out.println("Delay [" + skillId + "] = " + (tickcount - lastAttackTickCount) + ", " + (Server_ClientAtkTickDiff - STime_TC));
        /**
         * attackResetCount 如果正常攻擊多少次，就將檢測數值歸零
         */
        attackResetCount++; // Without this, the difference will always be at 100
        if (attackResetCount >= (attackDelay <= 200 ? 2 : 4)) {
            attackResetCount = 0;
            serverClientAttackTickCountDiff = STime_TC;
        }
        chr.get().updateTick(tickCoint);
        lastAttackTickCount = tickCoint;
    }

    /**
     * 檢查角色受到傷害
     *
     * @param damage
     */
    public final void checkTakeDamage(final int damage) {
        numSequentialDamage++;
        lastDamageTakenTime = System.currentTimeMillis();

        // System.out.println("tb" + timeBetweenDamage);
        // System.out.println("ns" + numSequentialDamage);
        // System.out.println(timeBetweenDamage / 1500 + "(" + timeBetweenDamage / numSequentialDamage + ")");
        if (lastDamageTakenTime - takingDamageSince / 500 < numSequentialDamage) {
//            registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
        }
        if (lastDamageTakenTime - takingDamageSince > 4500) {
            takingDamageSince = lastDamageTakenTime;
            numSequentialDamage = 0;
        }
        /*	(non-thieves)
         Min Miss Rate: 2%
         Max Miss Rate: 80%
         (thieves)
         Min Miss Rate: 5%
         Max Miss Rate: 95%*/
        if (damage == 0) {
            numZeroDamageTaken++;
            if (numZeroDamageTaken >= 35) { // Num count MSEA a/b players
                numZeroDamageTaken = 0;
                registerOffense(CheatingOffense.迴避過高);
            }
        } else if (damage != -1) {
            numZeroDamageTaken = 0;
        }
    }

    /**
     * 檢查相同傷害
     *
     * @param dmg
     * @param expected
     */
    public final void checkSameDamage(final int dmg, final double expected) {
        if (dmg > 2000 && lastDamage == dmg && chr.get() != null && (chr.get().getLevel() < 175 || dmg > expected * 2)) {
            numSameDamage++;
            if (numSameDamage > 5) {
                numSameDamage = 0;
                registerOffense(CheatingOffense.攻擊數值異常相同, numSameDamage + " 次, 攻擊傷害: " + dmg + ", 預計傷害: " + expected + " [等級: " + chr.get().getLevel() + ", 職業: " + chr.get().getJob() + "]");
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    /**
     * 檢查異常移動怪物
     *
     * @param pos
     */
    public final void checkMoveMonster(final Point pos) {

        double dis = Math.abs(pos.distance(lastMonsterMove));

        if (pos == lastMonsterMove) {
            monsterMoveCount++;
            if (monsterMoveCount > 15) {
                registerOffense(CheatingOffense.異常移動怪物);
            }
        } else if (dis > 1500) {
            monsterMoveCount++;
            if (monsterMoveCount > 15) {
                registerOffense(CheatingOffense.異常移動怪物);
            }
        } else {
            lastMonsterMove = pos;
            monsterMoveCount = 1;
        }
    }

    /**
     * 清除召喚獸檢測
     */
    public final void resetSummonAttack() {
        summonSummonTime = System.currentTimeMillis();
        numSequentialSummonAttack = 0;
    }

    /**
     * 檢查召喚獸攻擊
     *
     * @return
     */
    public final boolean checkSummonAttack() {
        numSequentialSummonAttack++;
        if ((System.currentTimeMillis() - summonSummonTime) / (2000 + 1) < numSequentialSummonAttack) {
            registerOffense(CheatingOffense.召喚獸無延遲);
            return false;
        }
        return true;
    }

    /**
     *
     */
    public final void checkDrop() {
        checkDrop(false);
    }

    /**
     * 檢查掉落
     *
     * @param dc
     */
    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 200) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && chr.get() != null) {
                if (dc) {
                    chr.get().getClient().disconnect(true, false);
                } else {
                    chr.get().getClient().setMonitored(true);
                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        if (lastASmegaTime + 10000 > System.currentTimeMillis() && chr.get() != null && !chr.get().isGM()) {
            return false;
        }
        lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public synchronized boolean GMSpam(int limit, int type) {
        if (type < 0 || lastTime.length < type) {
            type = 1; // default xD
        }
        if (System.currentTimeMillis() < limit + lastTime[type]) {
            return true;
        }
        lastTime[type] = System.currentTimeMillis();
        return false;
    }

    public final void checkMsg() { //ALL types of msg. caution with number of  msgsPerSecond
        if ((System.currentTimeMillis() - lastMsgTime) < 1000) { //luckily maplestory has auto-check for too much msging
            msgsPerSecond++;
            /*            if (msgsPerSecond > 10 && chr.get() != null) {
             chr.get().getClient().getSession().close();
             }*/
        } else {
            msgsPerSecond = 0;
        }
        lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            this.attacksWithoutHit++;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public final CheatingOffenseEntry getOffense(CheatingOffense offense) {
        rL.lock();
        try {
            return offenses.get(offense);
        } finally {
            rL.unlock();
        }
    }

    public final void putOffense(CheatingOffense offens, CheatingOffenseEntry entry) {
        wL.lock();
        try {
            offenses.put(offens, entry);
        } finally {
            wL.unlock();
        }
    }

    public final void registerOffense(final CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffense offense, final String message) {
        final MapleCharacter cheater = chr.get();
        if (cheater == null || !offense.isEnabled() || cheater.isClone() || cheater.isGM()) {
            return;
        }
        CheatingOffenseEntry entry = getOffense(offense);

        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, cheater.getId());
        }
        if (message != null) {
            entry.setParam(message);
        }

        // 違規+1點
        entry.incrementCount();

        // 檢查是否該鎖定了
        if (offense.shouldAutoban(entry.getCount())) {

            final byte type = offense.getBanType();
            if (type == BANTYPE_ENABLE) {
                AutobanManager.getInstance().autoban(cheater.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == BANTYPE_DC) {
                cheater.getClient().disconnect(true, false);
            }
            putOffense(offense, entry);
            return;
        }

        switch (offense) {
            case 魔法攻擊過高_1:
            case 魔法攻擊過高_2:
            case 攻擊過高_1:
            case 攻擊過高_2:
            case 攻擊距離過遠:
            case 召喚獸攻擊距離過遠:
            case 攻擊數值異常相同:
                System.out.println(MapleCharacterUtil.makeMapleReadable(cheater.getName()) + "疑似使用外掛");
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[封號系統] " + MapleCharacterUtil.makeMapleReadable(cheater.getName()) + " 偵測到 " + StringUtil.makeEnumHumanReadable(offense.name()) + (message == null ? "" : (" - " + message))).getBytes());
                AutobanManager.getInstance().autoban(cheater.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick == lastTickCount) { //definitely packet spamming
/*	    if (tickSame >= 5) {
             chr.get().getClient().getSession().close(); //i could also add a check for less than, but i'm not too worried at the moment :)
             } else {*/
            tickSame++;
//	    }
        } else {
            tickSame = 0;
        }
        lastTickCount = newTick;
    }

    public final void expireEntry(final CheatingOffenseEntry coe) {
        wL.lock();
        try {
            offenses.remove(coe.getOffense());
        } finally {
            wL.unlock();
        }
    }

    public final int getPoints() {
        int ret = 0;
        CheatingOffenseEntry[] offenses_copy;
        rL.lock();
        try {
            offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
        } finally {
            rL.unlock();
        }
        for (final CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(offenses);
    }

    public final String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<CheatingOffenseEntry>();
        rL.lock();
        try {
            for (final CheatingOffenseEntry entry : offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {

            @Override
            public final int compare(final CheatingOffenseEntry o1, final CheatingOffenseEntry o2) {
                final int thisVal = o1.getPoints();
                final int anotherVal = o2.getPoints();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        final int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public final void dispose() {
        if (invalidationTask != null) {
            invalidationTask.cancel(false);
        }
        invalidationTask = null;
    }

    private final class InvalidationTask implements Runnable {

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            rL.lock();
            try {
                offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            } finally {
                rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                dispose();
            }
        }
    }
}
