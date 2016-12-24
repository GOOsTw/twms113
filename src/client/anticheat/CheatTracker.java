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
import client.SkillFactory;
import constants.SkillType;
import handling.channel.handler.AttackInfo;
import handling.world.World;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.AutobanManager;
import server.MapleStatEffect;
import server.Timer.CheatTimer;
import server.life.MapleMonster;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.StaticLifeMovement;
import tools.MaplePacketCreator;
import tools.StringUtil;

public class CheatTracker {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock(), wL = lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
    private final WeakReference<MapleCharacter> player;

    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0;

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
    private int gm_message = 100;
    private int lastTickCount = 0, tickSame = 0;
    private long lastSSTickCount;
    private long lastASmegaTime = 0;
    public long[] lastTime = new long[6];
    public int atttackedMobCount = 0, 吸怪 = 0, FLY_吸怪 = 0;

    public CheatTracker(final MapleCharacter chr) {
        this.player = new WeakReference<>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    public final boolean checkAttackHitCount(AttackInfo attack, int maxHitCount) {
        MapleCharacter chr = player.get();
        if (attack.hits > maxHitCount) {
            if (chr.hasGmLevel(1)) {
                chr.dropMessage("攻擊次數異常攻擊次數 " + attack.hits + " 服務端判斷正常攻擊次數 " + maxHitCount + " 技能ID " + attack.skill);
                return true;
            } else {
//                chr.ban("攻擊次數異常攻擊次數 " + attack.hits + " 服務端判斷正常攻擊次數 " + maxHitCount + " 技能ID " + attack.skill, true, true, false);
//                chr.getClient().disconnect(true, false);
//                String reason = "使用違法程式練功";
//                World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[封鎖系統] " + chr.getName() + " 因為" + reason + "而被管理員永久停權。"));
//                World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM 密語系統] " + chr.getName() + " (等級 " + chr.getLevel() + ") 攻擊次數異常已自動封鎖。 玩家攻擊次數 " + attack.hits + " 服務端判斷正常攻擊次數 " + maxHitCount + " 技能ID " + attack.skill));
                return false;
            }
        }
        return true;
    }

    public final boolean checkAttackTargetCount(AttackInfo attack, int maxAttackTarget) {
        if (attack.targets > maxAttackTarget) {
            MapleCharacter chr = player.get();
            String reason = "打怪數量異常,技能代碼: " + attack.skill + " 封包怪物量 : " + attack.targets + " 服務端怪物量 :" + maxAttackTarget;
            if (chr.hasGmLevel(1)) {
                chr.dropMessage(reason);
                return true;
            } else {
                chr.ban(reason, true, true, false);
                chr.getClient().disconnect(true, false);
                String pubReason = "使用違法程式練功";
                World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[封鎖系統] " + chr.getName() + " 因為" + pubReason + "而被管理員永久停權。"));
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM 密語系統] " + chr.getName() + " (等級 " + chr.getLevel() + ") " + "攻擊怪物數量異常。 " + "封包怪物量 " + attack.targets + " 服務端怪物量 " + maxAttackTarget + " 技能ID " + attack.skill));
                return false;
            }
        }
        return true;
    }

    public final void checkAttack(final int skillId, final int clientTickCount) {

        long currentTick = System.currentTimeMillis();
        //時間攻擊
        if (clientTickCount < this.lastAttackTickCount) {
            player.get().getClient().disconnect(true, false);
        }

        short AtkDelay = GameConstants.getAttackDelay(skillId);

        if (player.get().getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null) {
            AtkDelay /= 6;// 使用這Buff之後 tickcount - lastAttackTickCount 可以為0...
        }
        // 攻擊加速
        if (player.get().getBuffedValue(MapleBuffStat.BOOSTER) != null) {
            AtkDelay /= 1.5;
        }
        // 最終極速
        if (player.get().getBuffedValue(MapleBuffStat.SPEED_INFUSION) != null) {
            AtkDelay /= 1.35;
        }
        // 狂郎
        if (GameConstants.isAran(player.get().getJob())) {
            AtkDelay /= 2.4;// 407
        }
        // 海盜、拳霸
        if (player.get().getJob() >= 500 && player.get().getJob() <= 512) {
            AtkDelay = 0;// 407
        }
        // 強化連擊
        if (skillId == SkillType.狂狼勇士2.強化連擊 || skillId == SkillType.格鬥家.蓄能激發) {
            AtkDelay = 0;
        }

        if (player.get().isShowDebugInfo()) {
            player.get().dropMessage(5, "CS攻擊速度檢測，間隔:" + (clientTickCount - lastAttackTickCount) + "，最低限度：" + AtkDelay);
        }

        if ((clientTickCount - lastAttackTickCount) < AtkDelay) {
            atttackedMobCount++;
            if (atttackedMobCount >= 100) {
                if (!player.get().hasGmLevel(1)) {
                    player.get().ban("攻擊速度異常，技能: " + skillId + " 間隔: " + (clientTickCount - lastAttackTickCount) + " " + "最低限度: " + AtkDelay, true, true, false);
                    player.get().sendHackShieldDetected();
                    player.get().getClient().disconnect(true, false);
                    String reason = "使用違法程式練功";
                    World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[自動封鎖系統] " + player.get().getName() + " 因為" + reason + "而被管理員永久停權。"));
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + player.get().getName() + " 攻擊無延遲自動封鎖! "));
                } else {
                    player.get().dropMessage("觸發攻擊速度封鎖");
                }
                return;
            } else {
                atttackedMobCount--;
            }

            registerOffense(CheatingOffense.攻擊速度過快_客戶端, "攻擊速度異常，技能: " + skillId + " 間隔: " + (clientTickCount - lastAttackTickCount) + " " + "最低限度: " + AtkDelay);
        }
        int latancy = player.get().getClient().getLatency() > 0 ? player.get().getClient().getLatency() : 0;
        if (player.get().isShowDebugInfo()) {
            player.get().dropMessage(5, "SS攻擊速度檢測，間隔:" + (currentTick - lastSSTickCount + latancy) + "，最低限度：" + AtkDelay);
        }

        if (((currentTick - lastSSTickCount) != 0) && (currentTick - lastSSTickCount + latancy) + latancy < AtkDelay) {
            registerOffense(CheatingOffense.攻擊速度過快_伺服器端, "攻擊速度異常，技能: " + skillId + " 間隔: " + (clientTickCount - lastAttackTickCount + latancy) + " " + "最低限度: " + AtkDelay);
        }
        this.lastSSTickCount = currentTick;
        this.lastAttackTickCount = clientTickCount;
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
                registerOffense(CheatingOffense.迴避過高, "迴避率過高 ");
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
        if (dmg > 2000 && lastDamage == dmg && player.get() != null && (player.get().getLevel() < 175 || dmg > expected * 2)) {
            numSameDamage++;
            if (numSameDamage > 5) {
                numSameDamage = 0;
                registerOffense(CheatingOffense.攻擊數值異常相同, numSameDamage + " 次, 攻擊傷害: " + dmg + ", 預計傷害: " + expected + " [等級: " + player.get().getLevel() + ", 職業: " + player.get().getJob() + "]");
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    public boolean checkAttackRange(AttackInfo attack, MapleStatEffect effect, MapleMonster monster) {
        MapleCharacter chr = player.get();
        double range = chr.getPosition().distanceSq(monster.getPosition());
        double maxSkillRange = GameConstants.getAttackRange(chr, effect, attack);
        if (range > maxSkillRange) { // 815^2 <-- the most ranged attack in the game is Flame Wheel at 815 range
            chr.getCheatTracker().registerOffense(CheatingOffense.攻擊距離過遠, "攻擊範圍異常,技能:" + attack.skill + "(" + SkillFactory.getName(attack.skill) + ")　怪物:" + monster.getId() + " 正常範圍:" + (int) maxSkillRange + " 計算範圍:" + (int) range);
            if (range > maxSkillRange * 2) {
                chr.getCheatTracker().registerOffense(CheatingOffense.攻擊距離過遠, "超大攻擊範圍,技能:" + attack.skill + "(" + SkillFactory.getName(attack.skill) + ")　怪物:" + monster.getId() + " 正常範圍:" + (int) maxSkillRange + " 計算範圍:" + (int) range);
            }
            if (chr.isGM()) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean checkMagicAttackRange(AttackInfo attack, MapleStatEffect effect, MapleMonster monster) {
        MapleCharacter chr = player.get();
        double attackDistanceSq = chr.getPosition().distanceSq(monster.getPosition());

        if (attackDistanceSq > 700000.0) {
            chr.getCheatTracker().registerOffense(CheatingOffense.攻擊距離過遠, "攻擊距離 " + attackDistanceSq + " 人物座標 (" + chr.getPosition().getX() + "," + chr.getPosition().getY() + ")"
                    + "怪物座標 (" + monster.getPosition().getX() + "," + monster.getPosition().getY() + ")");
            return false;
        }

        return true;
    }

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
        //estimated
        // System.out.println(numMPRegens + "/" + allowedRegens);
        // long time = (System.currentTimeMillis() - summonSummonTime) / (2000 + 1) + 3l;
        //  if (time < numSequentialSummonAttack) {
        //        registerOffense(CheatingOffense.FAST_SUMMON_ATTACK, chr.get().getName() + "快速召喚獸攻擊 " + time + " < " + numSequentialSummonAttack);
        //      return false;
        //  }
        return true;
    }

    public final void checkDrop() {
        checkDrop(false);
    }

    /**
     * 檢查掉落
     *
     * @param dc
     */
    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 1000) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && player.get() != null) {
                if (dc) {
                    player.get().sendHackShieldDetected();
                    player.get().getClient().disconnect(true, false);
                } else {
                    player.get().getClient().setMonitored(true);
                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        long time = 10 * 1000;
        if (player.get() != null) {
            if (player.get().getId() == 845 || player.get().getId() == 5247 || player.get().getId() == 12048) {
                time = 20 * 1000;
            }
            if (lastASmegaTime + time > System.currentTimeMillis() && !player.get().isGM()) {
                return false;
            }
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
            /*if (msgsPerSecond > 10 && chr.get() != null) {
                chr.get().sendHackShieldDetected();
                chr.get().getClient().getSession().close();
                }
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

    public final void registerOffense(final CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffense offense, final String param) {
        final MapleCharacter chr = player.get();
        if (chr == null || !offense.isEnabled() || chr.isClone()) {
            return;
        }

        CheatingOffenseEntry entry = null;
        rL.lock();
        try {
            entry = offenses.get(offense);
        } finally {
            rL.unlock();
        }

        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chr.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        if (chr.isShowDebugInfo()) {
            chr.dropMessage("註冊：" + entry.getOffense() + " 原因：" + entry.getParam() + " 鎖定容忍度：" + entry.getPoints() + "/" + entry.getOffense().getMaxPoints() + " 取消期限：" + (System.currentTimeMillis() - entry.getLastOffenseTime() + entry.getOffense().getValidityDuration()) / 1000 + " 秒後");
        }
        entry.incrementCount();
        if (offense.shouldAutoban(entry.getPoints())) {
            final byte type = offense.getBanType();
            String outputFileName;
            switch (type) {
                case 1:
                    AutobanManager.getInstance().autoban(chr.getClient(), StringUtil.makeEnumHumanReadable(offense.name()), 5000);
                    break;
                case 2:
                    outputFileName = "斷線";
                    chr.sendHackShieldDetected();
                    chr.getClient().disconnect(true, false);
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chr.getName() + " 自動斷線 類別: " + offense.toString() + " 原因: " + (param == null ? "" : (" - " + param))));
                    break;
                case 3:
                    boolean ban = true;
                    outputFileName = "封鎖";
                    String show = "使用違法程式練功";
                    String real = "";
                    if ("ITEMVAC_SERVER".equals(offense.toString())) {
                        outputFileName = "全圖吸物";
                        real = "使用全圖吸物";
                    } else if ("FAST_SUMMON_ATTACK".equals(offense.toString())) {
                        outputFileName = "召喚獸無延遲";
                        real = "使用召喚獸無延遲攻擊";
                    } else if ("MOB_VAC".equals(offense.toString())) {
                        outputFileName = "吸怪";
                        real = "使用吸怪";
                    } else if ("ATTACK_FARAWAY_MONSTER_BAN".equals(offense.toString())) {
                        outputFileName = "全圖打";
                        real = "使用全圖打";
                    } else {
                        ban = false;
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + MapleCharacterUtil.makeMapleReadable(chr.getName()) + " (編號: " + chr.getId() + " )使用外掛! " + StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" - " + param))));
                    }
                    if (player.get().hasGmLevel(1)) {
                        player.get().dropMessage("觸發違規: " + real + " param: " + (param == null ? "" : (" - " + param)));
                    } else if (ban) {
                        chr.ban(chr.getName() + real, true, true, false);
                        chr.sendHackShieldDetected();
                        chr.getClient().disconnect(true, false);
                        World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[封鎖系統] " + chr.getName() + " 因為" + show + "而被管理員永久停權。"));
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chr.getName() + " " + real + "自動封鎖! "));
                    } else {
                    }
                    break;
                default:
                    break;
            }
            gm_message = 100;
            return;
        }

        wL.lock();

        try {
            offenses.put(offense, entry);
        } finally {
            wL.unlock();
        }
        switch (offense) {
            case 召喚獸無延遲:
            case 拾取物品距離過遠_伺服器端:
            case 怪物全圖吸:
            case 魔法攻擊過高_1:
            case 魔法攻擊過高_2:
            case 攻擊過高_1:
            case 攻擊過高_2:
            case 攻擊距離過遠:
            //case ATTACK_FARAWAY_MONSTER_SUMMON:
            case 攻擊數值異常相同:
                gm_message--;
                boolean log = false;
                String out_log = "";
                String show = offense.name();
                switch (show) {
                    case "ATTACK_FARAWAY_MONSTER":
                        show = "全圖打";
                        out_log = "攻擊範圍異常";
                        log = true;
                        break;
                    case "MOB_VAC":
                        show = "使用吸怪";
                        out_log = "吸怪";
                        log = true;
                        break;
                }
                if (gm_message % 5 == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + chr.getName() + " (編號:" + chr.getId() + ")疑似外掛! " + show + (param == null ? "" : (" - " + param))));
                }
                if (gm_message == 0) {
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[封號系統] " + chr.getName() + " (編號: " + chr.getId() + " )疑似外掛！" + show + (param == null ? "" : (" - " + param))));
                    AutobanManager.getInstance().autoban(chr.getClient(), StringUtil.makeEnumHumanReadable(offense.name()), 5000);
                    gm_message = 100;
                }
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    private int attTickSame = 0;

    public void checkAttackTick(int newTick) {
        if (newTick == lastAttackTickCount) { //definitely packet spamming
            if (attTickSame >= 5) { //i could also add a check for less than, but i'm not too worried at the moment :)
                player.get().sendHackShieldDetected();
                player.get().getClient().disconnect(true, false);
            } else {
                attTickSame++;
            }
        } else if (newTick < lastAttackTickCount) {
            //chr.get().dropMessage(5, "att:" + (newTick - lastAttackTickCount));
            //chr.get().sendHackShieldDetected();
            //chr.get().getClient().disconnect(true, false);
        } else {
            attTickSame = 0;
        }
    }

    public void updateTick(int newTick) {
        if (newTick == lastTickCount) { //definitely packet spamming
            if (tickSame >= 5) { //i could also add a check for less than, but i'm not too worried at the moment :)
                player.get().sendHackShieldDetected();
                player.get().getClient().disconnect(true, false);
            } else {
                tickSame++;
            }
        } else if (newTick < lastTickCount) {
            player.get().sendHackShieldDetected();
            player.get().getClient().disconnect(true, false);
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
        final List<CheatingOffenseEntry> offenseList = new ArrayList<>();
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

    public long[] getLastGMspam() {
        return lastTime;
    }

    private long sameDirectionTimes;

    public void checkMonsterMovment(MapleMonster monster, List<LifeMovementFragment> res, Point startPos) {
        try {
            if (player.get() != null && monster.getController() != player.get()) {
                return;
            }
            boolean fly = monster.getStats().getFly();
            Point endPos = null;
            int reduce_x = 0;
            int reduce_y = 0;
            for (LifeMovementFragment move : res) {
                if ((move instanceof StaticLifeMovement)) {
                    endPos = ((LifeMovement) move).getPosition();
                    if (endPos == null) {
                        continue;
                    }
                    try {
                        reduce_x = Math.max(reduce_x, Math.abs(startPos.x - endPos.x));
                        reduce_y = Math.max(reduce_y, Math.abs(startPos.y - endPos.y));
                    } catch (Exception ex) {
                    }
                }
            }
            if (!fly) {
                int GeneallyDistance_y = 180;
                int GeneallyDistance_x = 250;
                int Check_x = 250;
                int max_x = 450;
                switch (player.get().getMapId()) {
                    case 100040001:
                    case 926013500:
                        GeneallyDistance_y = 200;
                        break;
                    case 200010300:
                        GeneallyDistance_x = 1000;
                        GeneallyDistance_y = 500;
                        break;
                    case 220010600:
                    case 926013300:
                        GeneallyDistance_x = 200;
                        break;
                    case 211040001:
                        GeneallyDistance_x = 220;
                        break;
                    case 101030105:
                        GeneallyDistance_x = 250;
                        break;
                    case 541020500:
                        Check_x = 300;
                        break;
                }
                switch (monster.getId()) {
                    case 4230100:
                        GeneallyDistance_y = 200;
                        break;
                    case 9410066:
                        Check_x = 1000;
                        break;
                }
                if (GeneallyDistance_x > max_x) {
                    max_x = GeneallyDistance_x;
                }
                if (((reduce_x > GeneallyDistance_x || reduce_y > GeneallyDistance_y) && reduce_y != 0) || (reduce_x > Check_x && reduce_y == 0) || reduce_x > max_x) {
                    吸怪++;
                    if (player.get().isShowDebugInfo()) {
                        player.get().dropMessage(5, "reduce_x = " + reduce_x + " , max_x = " + max_x);
                    }
                    if (吸怪 % 60 == 0 || reduce_x > max_x) {
                        player.get().getCheatTracker().registerOffense(CheatingOffense.怪物全圖吸, "(地圖: " + player.get().getMapId() + " 怪物數量:" + 吸怪 + ")");
                        World.Broadcast.broadcastGMMessage(MaplePacketCreator.getItemNotice("[GM密語] " + player.get().getName() + " (編號: " + player.get().getId() + ")使用吸怪(" + 吸怪 + ")! - 地圖:" + player.get().getMapId() + "(" + player.get().getMap().getMapName() + ")"));
                    }
                }
            }
            boolean moveRight = endPos.x > startPos.y;
            if (moveRight) {
                if (sameDirectionTimes > 0) {
                    ++sameDirectionTimes;
                } else {
                    sameDirectionTimes = 1;
                }
            } else if (sameDirectionTimes < 0) {
                --sameDirectionTimes;
            } else {
                sameDirectionTimes = -1;
            }

        } catch (Exception ex) {

        }
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
            if (player.get() == null) {
                dispose();
            }
        }
    }
}
