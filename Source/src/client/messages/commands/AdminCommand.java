package client.messages.commands;

import client.ISkill;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleDisease;
import client.MapleStat;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.ModifyInventory;
import client.messages.CommandProcessorUtil;
import client.status.MonsterStatus;
import constants.GameConstants;
import database.DatabaseConnection;
import handling.MaplePacket;
import handling.MapleServerHandler;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.world.World;
import handling.world.CheaterData;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.MapleShopFactory;
import server.MapleSquad;
import server.ShutdownServer;
import server.Timer;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.MobTimer;
import server.Timer.WorldTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MobSkillFactory;
import server.life.OverrideMonsterStats;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.maps.MapleReactorStats;
import server.quest.MapleQuest;
import tools.ArrayMap;
import tools.CPUSampler;
import tools.MaplePacketCreator;
import tools.MockIOSession;
import tools.Pair;
import tools.StringUtil;
import tools.packet.MobPacket;
import tools.packet.PlayerShopPacket;
import com.mysql.jdbc.Connection;
import java.util.concurrent.ScheduledFuture;
import scripting.NPCScriptManager;
import server.ServerProperties;
import server.maps.MapleMapItem;
import handling.login.LoginServer;

/**
 *
 * @author Emilyx3
 */
public class AdminCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.ADMIN;
    }

    public static class SavePlayerShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            c.getPlayer().dropMessage(6, "精靈商人儲存完畢.");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!savePlayerShops - 儲存精靈商人").toString();
        }
    }

    public static class Shutdown extends CommandExecute {

        private static Thread t = null;

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(6, "關閉伺服器中...");
            if (t == null || !t.isAlive()) {
                t = new Thread(server.ShutdownServer.getInstance());
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "已在執行中...");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdown - 關閉伺服器").toString();
        }
    }

    public static class ShutdownTime extends CommandExecute {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;
        private static Thread t = null;

        public boolean execute(MapleClient c, List<String> splitted) {

            if (splitted.size() < 2) {
                return false;
            }
            minutesLeft = Integer.parseInt(splitted.get(1));
            c.getPlayer().dropMessage(6, "伺服器將在 " + minutesLeft + "分鐘後關閉. 請盡速關閉精靈商人 並下線.");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = EventTimer.getInstance().register(new Runnable() {

                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().run();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        StringBuilder message = new StringBuilder();
                        message.append("[楓之谷公告] 伺服器將在 ");
                        message.append(minutesLeft);
                        message.append("分鐘後關閉. 請盡速關閉精靈商人 並下線.");
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, message.toString()).getBytes());
                        World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(message.toString()).getBytes());
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, new StringBuilder().append("伺服器關閉時間修改為 ")
                        .append(minutesLeft).append("分鐘後，清稍等伺服器關閉").toString());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shutdowntime <秒數> - 關閉伺服器").toString();
        }
    }

    public static class SaveAll extends CommandExecute {

        private int p = 0;

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, false);
                }
            }
            c.getPlayer().dropMessage("[保存] " + p + "個玩家數據保存到數據中.");
            p = 0;
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!saveall - 儲存所有角色資料").toString();
        }
    }

    public static class LowHP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getStat().setHp((short) 1);
            c.getPlayer().getStat().setMp((short) 1);
            c.getPlayer().updateSingleStat(MapleStat.HP, 1);
            c.getPlayer().updateSingleStat(MapleStat.MP, 1);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lowhp - 血魔歸ㄧ").toString();
        }
    }

    public static class Heal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!heal - 補滿血魔").toString();
        }
    }

    public static class HellBan extends Ban {

        public HellBan() {
            hellban = true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hellban <玩家名稱> <原因> - hellban").toString();
        }
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            if (hellban) {
                return "HellBan";
            } else {
                return "Ban";
            }
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" 鎖定原因 ").append(splitted.get(1)).append(": ").append(StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 2));
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (target != null) {
                if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) {
                    sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, hellban)) {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功鎖定了 " + splitted.get(1) + ".");
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 不能封鎖GM...");

                }
            } else {
                if (MapleCharacter.ban(splitted.get(1), sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted.get(0).equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線鎖定 " + splitted.get(1) + ".");
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] Failed to ban " + splitted.get(1));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ban <玩家名稱> <原因> - 封鎖玩家").toString();
        }
    }

    public static class UnHellBan extends UnBan {

        public UnHellBan() {
            hellban = true;
        }
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            if (hellban) {
                return "UnHellBan";
            } else {
                return "UnBan";
            }
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            byte ret;
            if (hellban) {
                ret = MapleClient.unHellban(splitted.get(1));
            } else {
                ret = MapleClient.unban(splitted.get(1));
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 目標玩家不存在");
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功解除鎖定");
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted.get(1));
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] IP以及Mac已成功解鎖.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unban <玩家名稱> - 解鎖玩家").toString();
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted.get(1));
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[unbanip] SQL 錯誤.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[unbanip] 角色不存在.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[unbanip] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[unbanip] IP或Mac已解鎖其中一個.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[unbanip] IP以及Mac已成功解鎖.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unbanip <玩家名稱> - 解鎖玩家").toString();
        }
    }

    public static class TempBan extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            final int reason = Integer.parseInt(splitted.get(2));
            final int numDay = Integer.parseInt(splitted.get(3));

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, numDay);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null) {
                c.getPlayer().dropMessage(6, "[tempban] 找不到目標角色");

            } else {
                victim.tempban("由" + c.getPlayer().getName() + "暫時鎖定了", cal, reason, true);
                c.getPlayer().dropMessage(6, "[tempban] " + splitted.get(1) + " 已成功被暫時鎖定至 " + df.format(cal.getTime()));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!tempban <玩家名稱> - 暫時鎖定玩家").toString();
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int level = 0;
            MapleCharacter victim;
            if (splitted.get(1).charAt(0) == '-') {
                level = StringUtil.countCharacters(splitted.get(1), 'f');
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(2));
            } else {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            }
            if (level < 2 && victim != null) {
                victim.getMap().removePlayer(victim);
                if (level >= 1) {
                    victim.getClient().disconnect(true, false);
                }

            } else {
                c.getPlayer().dropMessage(6, "[dc] 請使用 !dc -f 或是此玩家根本不存在.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dc [-f] <玩家名稱> - 斷線玩家").toString();
        }
    }

    public static class Kill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.size() < 2) {
                return false;
            }
            MapleCharacter victim;
            for (int i = 1; i < splitted.size(); i++) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(i));
                if (victim == null) {
                    c.getPlayer().dropMessage(6, "[kill] 玩家 " + splitted.get(i) + " 不存在.");
                } else {
                    if (player.allowedToTarget(victim)) {
                        victim.getStat().setHp((short) 0);
                        victim.getStat().setMp((short) 0);
                        victim.updateSingleStat(MapleStat.HP, 0);
                        victim.updateSingleStat(MapleStat.MP, 0);
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!kill <玩家名稱1> <玩家名稱2> ...  - 殺掉玩家").toString();
        }
    }

    public static class Skill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted.get(1)));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 2, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            c.getPlayer().changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!skill <技能ID> [技能等級] [技能最大等級] ...  - 學習技能").toString();
        }
    }

    public static class Fame extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.size() < 2) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            short fame = 0;
            try {
                fame = Short.parseShort(splitted.get(2));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "不合法的數字");
                return false;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            } else {
                c.getPlayer().dropMessage(6, "[fame] 角色不存在");
            }
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!fame <角色名稱> <名聲> ...  - 名聲").toString();
        }
    }

    public static class autoreg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            LoginServer.autoRegister = !LoginServer.autoRegister;
            c.getPlayer().dropMessage(0, "[autoreg] " + (LoginServer.autoRegister ? "開啟" : "關閉"));
            System.out.println("[autoreg] " + (LoginServer.autoRegister ? "開啟" : "關閉"));
            return true;
        }

        public String getMessage() {
            return new StringBuilder().append("!autoreg  - 自動註冊開關").toString();
        }
    }

    public static class HealMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            for (MapleCharacter mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp());
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp());
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                }
            }
            return true;

        }

        public String getMessage() {
            return new StringBuilder().append("!healmap  - 治癒地圖上所有的人").toString();
        }
    }

    public static class GodMode extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (player.isInvincible()) {
                player.setInvincible(false);
                player.dropMessage(6, "無敵已經關閉");
            } else {
                player.setInvincible(true);
                player.dropMessage(6, "無敵已經開啟.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!godmode  - 無敵開關").toString();
        }
    }

    public static class GiveSkill extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted.get(2)));
            byte level = (byte) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 3, 1);
            byte masterlevel = (byte) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 4, 1);

            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            victim.changeSkillLevel(skill, level, masterlevel);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!giveskill <玩家名稱> <技能ID> [技能等級] [技能最大等級] - 給予技能").toString();
        }
    }

    public static class SP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 1, 1));
            c.sendPacket(MaplePacketCreator.updateSp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sp [數量] - 增加SP").toString();
        }
    }

    public static class AP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().setRemainingAp((short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 1, 1));
            final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>();
            c.sendPacket(MaplePacketCreator.updateAp(c.getPlayer(), false));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ap [數量] - 增加AP").toString();
        }
    }

    public static class Job extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().changeJob(Integer.parseInt(splitted.get(1)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!job <職業代碼> - 更換職業").toString();
        }
    }

    public static class WhereAmI extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!whereami - 目前地圖").toString();
        }
    }

    public static class Shop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = 0;
            try {
                shopId = Integer.parseInt(splitted.get(1));
            } catch (NumberFormatException ex) {
                return false;
            }
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            } else {
                c.getPlayer().dropMessage(5, "此商店ID不存在");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!shop - 開啟商店").toString();
        }
    }

    public static class 關鍵時刻 extends CommandExecute {

        protected static ScheduledFuture<?> ts = null;

        @Override
        public boolean execute(final MapleClient c, List<String> splitted) {
            if (splitted.size() < 1) {
                return false;
            }
            if (ts != null) {
                ts.cancel(false);
                c.getPlayer().dropMessage(0, "原定的關鍵時刻已取消");
            }
            int minutesLeft = 0;
            try {
                minutesLeft = Integer.parseInt(splitted.get(1));
            } catch (NumberFormatException ex) {
                return false;
            }
            if (minutesLeft > 0) {
                ts = EventTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                            for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                                if (c.canClickNPC() && !c.getPlayer().isGM()) {
                                    NPCScriptManager.getInstance().start(mch.getClient(), 9010010);
                                }
                            }
                        }
                        ts.cancel(false);
                        ts = null;
                    }
                }, minutesLeft * 60000);
                c.getPlayer().dropMessage(0, "關鍵時刻預定已完成");
            } else {
                c.getPlayer().dropMessage(0, "設定的時間必須 > 0。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!關鍵時刻 <時間:分鐘> - 關鍵時刻").toString();
        }
    }

    public static class GainCash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            c.getPlayer().modifyCSPoints(1, Integer.parseInt(splitted.get(1)), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gaingash <數量> - 取得Gash點數").toString();
        }
    }

    public static class GainMaplePoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            c.getPlayer().modifyCSPoints(2, Integer.parseInt(splitted.get(1)), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmaplepoint <數量> - 取得楓葉點數").toString();
        }
    }

    public static class GainPoint extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            c.getPlayer().setPoints(c.getPlayer().getPoints() + Integer.parseInt(splitted.get(1)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainpoint <數量> - 取得Point").toString();
        }
    }

    public static class GainVP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            c.getPlayer().setVPoints(c.getPlayer().getVPoints() + Integer.parseInt(splitted.get(1)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainvpoint <數量> - 取得VPoint").toString();
        }
    }

    public static class LevelUp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getPlayer().getLevel() < 200) {
                c.getPlayer().gainExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) + 1, true, false, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!levelup - 等級上升").toString();
        }
    }

    public static class ClearInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<>();
            switch (splitted.get(1)) {
                case "all":
                    for (MapleInventoryType type : MapleInventoryType.values()) {
                        for (IItem item : c.getPlayer().getInventory(type)) {
                            eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), type);
                        }
                    }
                    break;
                case "eqped":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIPPED);
                    }
                    break;
                case "eqp":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.EQUIP);
                    }
                    break;
                case "use":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.USE);
                    }
                    break;
                case "setup":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                        MapleInventoryType put = eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.SETUP);
                    }
                    break;
                case "etc":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.ETC);
                    }
                    break;
                case "cash":
                    for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                        eqs.put(new Pair<>(item.getPosition(), item.getQuantity()), MapleInventoryType.CASH);
                    }
                    break;
                default:
                    return false;
            }
            for (Entry<Pair<Short, Short>, MapleInventoryType> eq : eqs.entrySet()) {
                MapleInventoryManipulator.removeFromSlot(c, eq.getValue(), eq.getKey().left, eq.getKey().right, false, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!clearinv <all/eqped/eqp/use/setup/etc/cash> - 清理道具欄").toString();
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            java.util.Map<IItem, MapleInventoryType> eqs = new ArrayMap<>();
            boolean add = false;
            if (splitted.size() < 2 || splitted.get(1).equals("全部")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (IItem item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "已經解鎖");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                            add = true;
                            c.getPlayer().reloadC();
                            c.getPlayer().dropMessage(5, "已經解鎖");
                            //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted.get(1).equals("已裝備道具")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted.get(1).equals("武器")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted.get(1).equals("消耗")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted.get(1).equals("裝飾")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted.get(1).equals("其他")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted.get(1).equals("特殊")) {
                for (IItem item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADEABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADEABLE.getValue()));
                        add = true;
                        c.getPlayer().reloadC();
                        c.getPlayer().dropMessage(5, "已經解鎖");
                        //c.sendPacket(MaplePacketCreator.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                return false;
            }

            for (Entry<IItem, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItem_NoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unlockinv <全部/已裝備道具/武器/消耗/裝飾/其他/特殊> - 解鎖道具").toString();
        }
    }

    public static class Item extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int itemId = Integer.parseInt(splitted.get(1));
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 2, 1);

            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().dropMessage(5, "很抱歉，此物品您的GM等級無法呼叫.");
                        return true;
                    }
                }
            }

            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "請從商城購買寵物.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            } else {
                IItem item;
                byte flag = 0;
                flag |= ItemFlag.LOCK.getValue();

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    item.setFlag(flag);

                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                    if (GameConstants.getInventoryType(itemId) != MapleInventoryType.USE) {
                        item.setFlag(flag);
                    }
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!item <道具ID> - 取得道具").toString();
        }
    }

    public static class Drop extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int itemId = Integer.parseInt(splitted.get(1));
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "寵物請到購物商城購買.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " - 物品不存在");
            } else {
                IItem toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                toDrop.setOwner(c.getPlayer().getName());
                toDrop.setGMLog(c.getPlayer().getName());

                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dropitem <道具ID> - 掉落道具").toString();
        }
    }

    public static class Level extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().setLevel(Short.parseShort(splitted.get(1)));
            c.getPlayer().levelUp();
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!level [等級] - 更改等級").toString();
        }
    }

    public static class serverMsg extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1));
                for (ChannelServer ch : ChannelServer.getAllInstances()) {
                    ch.setServerMessage(sb.toString());
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()).getBytes());
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!servermsg 訊息 - 更改上方黃色公告").toString();
        }
    }

    public static class Say extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(c.getPlayer().getName());
                sb.append("] ");
                sb.append(StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1));
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!say 訊息 - 伺服器公告").toString();
        }
    }

    public static class Letter extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, "指令規則: ");
                return false;
            }
            int start, nstart;
            if (splitted.get(1).equalsIgnoreCase("green")) {
                start = 3991026;
                nstart = 3990019;
            } else if (splitted.get(1).equalsIgnoreCase("red")) {
                start = 3991000;
                nstart = 3990009;
            } else {
                c.getPlayer().dropMessage(6, "未知的顏色!");
                return true;
            }
            String splitString = StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 2);
            List<Integer> chars = new ArrayList<Integer>();
            splitString = splitString.toUpperCase();
            // System.out.println(splitString);
            for (int i = 0; i < splitString.length(); i++) {
                char chr = splitString.charAt(i);
                if (chr == ' ') {
                    chars.add(-1);
                } else if ((int) (chr) >= (int) 'A' && (int) (chr) <= (int) 'Z') {
                    chars.add((int) (chr));
                } else if ((int) (chr) >= (int) '0' && (int) (chr) <= (int) ('9')) {
                    chars.add((int) (chr) + 200);
                }
            }
            final int w = 32;
            int dStart = c.getPlayer().getPosition().x - (splitString.length() / 2 * w);
            for (Integer i : chars) {
                if (i == -1) {
                    dStart += w;
                } else if (i < 200) {
                    int val = start + i - (int) ('A');
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                } else if (i >= 200 && i <= 300) {
                    int val = nstart + i - (int) ('0') - 200;
                    client.inventory.Item item = new client.inventory.Item(val, (byte) 0, (short) 1);
                    c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), item, new Point(dStart, c.getPlayer().getPosition().y), false, false);
                    dStart += w;
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append(" !letter <color (green/red)> <word> - 送信").toString();
        }

    }

    public static class Marry extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                return false;
            }
            int itemId = Integer.parseInt(splitted.get(2));
            if (!GameConstants.isEffectRing(itemId)) {
                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
            } else {
                MapleCharacter fff = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
                if (fff == null) {
                    c.getPlayer().dropMessage(6, "玩家必須上線");
                } else {
                    int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
                    try {
                        MapleCharacter[] chrz = {fff, c.getPlayer()};
                        for (int i = 0; i < chrz.length; i++) {
                            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
                            if (eq == null) {
                                c.getPlayer().dropMessage(6, "錯誤的戒指ID.");
                                return true;
                            } else {
                                eq.setUniqueId(ringID[i]);
                                MapleInventoryManipulator.addbyItem(chrz[i].getClient(), eq.copy());
                                chrz[i].dropMessage(6, "成功與  " + chrz[i == 0 ? 1 : 0].getName() + " 結婚");
                            }
                        }
                        MapleRing.addToDB(itemId, c.getPlayer(), fff.getName(), fff.getId(), ringID);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!marry <玩家名稱> <戒指ID> - 結婚").toString();
        }
    }

    public static class ItemCheck extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3 || splitted.get(1) == null || splitted.get(1).equals("") || splitted.get(2) == null || splitted.get(2).equals("")) {
                return false;
            } else {
                int item = Integer.parseInt(splitted.get(2));
                MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
                int itemamount = chr.getItemQuantity(item, true);
                if (itemamount > 0) {
                    c.getPlayer().dropMessage(6, chr.getName() + " has " + itemamount + " (" + item + ").");
                } else {
                    c.getPlayer().dropMessage(6, chr.getName() + " doesn't have (" + item + ")");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!itemcheck <playername> <itemid> - 檢查物品").toString();
        }
    }

    public static class MobVac extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (final MapleMapObject mmo : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
                final MapleMonster monster = (MapleMonster) mmo;
                c.getPlayer().getMap().broadcastMessage(MobPacket.moveMonster(false, -1, 0, 0, 0, 0, monster.getObjectId(), monster.getPosition(), c.getPlayer().getPosition(), c.getPlayer().getLastRes()));
                monster.setPosition(c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mobvac - 全圖吸怪").toString();
        }
    }

    public static class ItemVac extends CommandExecute {

        public boolean execute(MapleClient c, List<String> splitted) {
            boolean ItemVac = c.getPlayer().getItemVac();
            if (ItemVac == false) {
                c.getPlayer().stopItemVac();
                c.getPlayer().startItemVac();
            } else {
                c.getPlayer().stopItemVac();
            }
            c.getPlayer().dropMessage(6, "目前自動撿物狀態:" + (ItemVac == false ? "開啟" : "關閉"));
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mobvac - 全圖吸物開關").toString();
        }
    }

    public static class Song extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(splitted.get(1)));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!song - 播放音樂").toString();
        }
    }

    public static class 開啟自動活動 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final EventManager em = c.getChannelServer().getEventSM().getEventManager("AutomatedEvent");
            if (em != null) {
                em.scheduleRandomEvent();
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!開啟自動活動 - 開啟自動活動").toString();
        }
    }

    public static class 活動開始 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleEvent.onStartEvent(c.getPlayer());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!活動開始 - 活動開始").toString();
        }
    }

    public static class 關閉活動入口 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getChannelServer().getEvent() == c.getPlayer().getMapId()) {
                MapleEvent.setEvent(c.getChannelServer(), false);
                c.getPlayer().dropMessage(5, "已經關閉活動入口，可以使用 !活動開始 來啟動。");
                return true;
            } else {
                c.getPlayer().dropMessage(5, "您必須先使用 !選擇活動,設定當前頻道的活動。");
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!關閉活動入口 - 關閉活動入口").toString();
        }
    }

    public static class 選擇活動 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final MapleEventType type = MapleEventType.getByString(splitted.get(1));
            if (type == null) {
                final StringBuilder sb = new StringBuilder("目前開放的活動有: ");
                for (MapleEventType t : MapleEventType.values()) {
                    sb.append(t.name()).append(",");
                }
                c.getPlayer().dropMessage(5, sb.toString().substring(0, sb.toString().length() - 1));
            }
            final String msg = MapleEvent.scheduleEvent(type, c.getChannelServer());
            if (msg.length() > 0) {
                c.getPlayer().dropMessage(5, msg);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!選擇活動 - 選擇活動").toString();
        }
    }

    public static class CheckGash extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chrs == null) {
                c.getPlayer().dropMessage(5, "找不到該角色");
            } else {
                c.getPlayer().dropMessage(6, chrs.getName() + " 有 " + chrs.getCSPoints(1) + " 點數.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!checkgash <玩家名稱> - 檢查點數").toString();
        }
    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                return false;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家並不存在");
            } else {
                chr.removeAll(Integer.parseInt(splitted.get(2)));
                c.getPlayer().dropMessage(6, "所有ID為 " + splitted.get(2) + " 的道具已經從 " + splitted.get(1) + " 身上被移除了");
            }
            return true;

        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!removeitem <角色名稱> <物品ID> - 移除玩家身上的道具").toString();
        }
    }

    public static class LockItem extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                return false;
            }
            MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chr == null) {
                c.getPlayer().dropMessage(6, "此玩家並不存在");
            } else {
                int itemid = Integer.parseInt(splitted.get(2));
                MapleInventoryType type = GameConstants.getInventoryType(itemid);
                for (IItem item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                    chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                }
                if (type == MapleInventoryType.EQUIP) {
                    type = MapleInventoryType.EQUIPPED;
                    for (IItem item : chr.getInventory(type).listById(itemid)) {
                        item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                        chr.getClient().sendPacket(MaplePacketCreator.modifyInventory(false, new ModifyInventory(ModifyInventory.Types.UPDATE, item)));
                    }
                }
                c.getPlayer().dropMessage(6, "玩家 " + splitted.get(1) + "身上所有ID為 " + splitted.get(2) + " 的道具已經從鎖定了");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!lockitem <角色名稱> <物品ID> - 上鎖玩家身上的道具").toString();
        }
    }

    public static class KillMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleCharacter map : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (map != null && !map.isGM()) {
                    map.getStat().setHp((short) 0);
                    map.getStat().setMp((short) 0);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!killmap - 殺掉所有玩家").toString();
        }
    }

    public static class SpeakMega extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter victim = null;
            if (splitted.size() >= 2) {
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            }
            try {
                World.Broadcast.broadcastSmega(MaplePacketCreator.serverNotice(3, victim == null ? c.getChannel() : victim.getClient().getChannel(), victim == null ? splitted.get(1) : victim.getName() + " : " + StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 2), true).getBytes());
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmega [玩家名稱] <訊息> - 對某個玩家的頻道進行廣播").toString();
        }
    }

    public static class Speak extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + splitted.get(1));
                return false;
            } else {
                victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 2), victim.isGM(), 0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speak <玩家名稱> <訊息> - 對某個玩家傳訊息").toString();
        }
    }

    public static class SpeakMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakmap <訊息> - 對目前地圖進行傳送訊息").toString();
        }

    }

    public static class SpeakChannel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (victim.getId() != c.getPlayer().getId()) {
                    victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1), victim.isGM(), 0));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <訊息> - 對目前頻道進行傳送訊息").toString();
        }

    }

    public static class SpeakWorld extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    if (victim.getId() != c.getPlayer().getId()) {
                        victim.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim.getId(), StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1), victim.isGM(), 0));
                    }
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!speakchannel <訊息> - 對目前伺服器進行傳送訊息").toString();
        }
    }

    public static class Disease extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, "");
                return false;
            }
            int type = 0;
            MapleDisease dis = null;
            if (splitted.get(1).equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted.get(1).equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted.get(1).equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted.get(1).equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted.get(1).equalsIgnoreCase("CURSE")) {
                type = 124;
            } else if (splitted.get(1).equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted.get(1).equalsIgnoreCase("SLOW")) {
                type = 126;
            } else if (splitted.get(1).equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else if (splitted.get(1).equalsIgnoreCase("REVERSE")) {
                type = 132;
            } else if (splitted.get(1).equalsIgnoreCase("ZOMBIFY")) {
                type = 133;
            } else if (splitted.get(1).equalsIgnoreCase("POTION")) {
                type = 134;
            } else if (splitted.get(1).equalsIgnoreCase("SHADOW")) {
                type = 135;
            } else if (splitted.get(1).equalsIgnoreCase("BLIND")) {
                type = 136;
            } else if (splitted.get(1).equalsIgnoreCase("FREEZE")) {
                type = 137;
            } else {
                return false;
            }
            dis = MapleDisease.getBySkill(type);
            if (splitted.size() == 4) {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(2));
                if (victim == null) {
                    c.getPlayer().dropMessage(5, "找不到此玩家");
                } else {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 3, 1)));
                }
            } else {
                for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    victim.setChair(0);
                    victim.getClient().sendPacket(MaplePacketCreator.cancelChair(-1));
                    victim.getMap().broadcastMessage(victim, MaplePacketCreator.showChair(c.getPlayer().getId(), 0), false);
                    victim.giveDebuff(dis, MobSkillFactory.getMobSkill(type, CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[splitted.size()]), 2, 1)));
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!disease <SEAL/DARKNESS/WEAKEN/STUN/CURSE/POISON/SLOW/SEDUCE/REVERSE/ZOMBIFY/POTION/SHADOW/BLIND/FREEZE> [角色名稱] <狀態等級> - 讓人得到特殊狀態").toString();
        }

    }

    public static class SendAllNote extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {

            if (splitted.size() >= 1) {
                String text = StringUtil.joinStringFrom(splitted.toArray(new String[splitted.size()]), 1);
                for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    c.getPlayer().sendNote(mch.getName(), text);
                }
            } else {
                return false;
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!sendallnote <文字> 傳送Note給目前頻道的所有人").toString();
        }
    }

    public static class giveMeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim == null) {
                c.getPlayer().dropMessage(5, "找不到 '" + splitted.get(1));
            } else {
                victim.gainMeso(Integer.parseInt(splitted.get(2)), true);
                return true;
            }
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!gainmeso <名字> <數量> - 給玩家楓幣").toString();
        }
    }

    public static class MesoEveryone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                return false;
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                for (MapleCharacter mch : cserv.getPlayerStorage().getAllCharacters()) {
                    mch.gainMeso(Integer.parseInt(splitted.get(1)), true);
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesoeveryone <數量> - 給所有玩家楓幣").toString();
        }
    }

    public static class CloneMe extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cloneme - 產生克龍體").toString();
        }
    }

    public static class DisposeClones extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getCloneSize() + "個克龍體消失了.");
            c.getPlayer().disposeClones();
            return true;
        }
        
        @Override
        public String getMessage() {
            return new StringBuilder().append("!disposeclones - 摧毀克龍體").toString();
        }
    }

    public static class Monitor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if(splitted.size() < 2) {
                return false;
            }
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            } else {
                c.getPlayer().dropMessage(5, "找不到該玩家");
            }
            return true;
        }
        
        @Override
        public String getMessage() {
            return new StringBuilder().append("!monitor <玩家> - 記錄玩家資訊").toString();
        }
    }

    public static class PermWeather extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getPlayer().getMap().getPermanentWeather() > 0) {
                c.getPlayer().getMap().setPermanentWeather(0);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.removeMapEffect());
                c.getPlayer().dropMessage(5, "Map weather has been disabled.");
            } else {
                final int weather = CommandProcessorUtil.getOptionalIntArg(splitted, 1, 5120000);
                if (!MapleItemInformationProvider.getInstance().itemExists(weather) || weather / 10000 != 512) {
                    c.getPlayer().dropMessage(5, "Invalid ID.");
                } else {
                    c.getPlayer().getMap().setPermanentWeather(weather);
                    c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startMapEffect("", weather, false));
                    c.getPlayer().dropMessage(5, "Map weather has been enabled.");
                }
            }
            return true;
        }
    }

    public static class CharInfo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final StringBuilder builder = new StringBuilder();
            final MapleCharacter other = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (other == null) {
                builder.append("...不存在");
                c.getPlayer().dropMessage(6, builder.toString());
                return 0;
            }
            if (other.getClient().getLastPing() <= 0) {
                other.getClient().sendPing();
            }
            builder.append(MapleClient.getLogMessage(other, ""));
            builder.append(" 在 ").append(other.getPosition().x);
            builder.append(" /").append(other.getPosition().y);

            builder.append(" || 血量 : ");
            builder.append(other.getStat().getHp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxHp());

            builder.append(" || 魔量 : ");
            builder.append(other.getStat().getMp());
            builder.append(" /");
            builder.append(other.getStat().getCurrentMaxMp());

            builder.append(" || 物理攻擊力 : ");
            builder.append(other.getStat().getTotalWatk());
            builder.append(" || 魔法攻擊力 : ");
            builder.append(other.getStat().getTotalMagic());
            builder.append(" || 最高攻擊 : ");
            builder.append(other.getStat().getCurrentMaxBaseDamage());
            builder.append(" || 攻擊%數 : ");
            builder.append(other.getStat().dam_r);
            builder.append(" || BOSS攻擊%數 : ");
            builder.append(other.getStat().bossdam_r);

            builder.append(" || 力量 : ");
            builder.append(other.getStat().getStr());
            builder.append(" || 敏捷 : ");
            builder.append(other.getStat().getDex());
            builder.append(" || 智力 : ");
            builder.append(other.getStat().getInt());
            builder.append(" || 幸運 : ");
            builder.append(other.getStat().getLuk());

            builder.append(" || 全部力量 : ");
            builder.append(other.getStat().getTotalStr());
            builder.append(" || 全部敏捷 : ");
            builder.append(other.getStat().getTotalDex());
            builder.append(" || 全部智力 : ");
            builder.append(other.getStat().getTotalInt());
            builder.append(" || 全部幸運 : ");
            builder.append(other.getStat().getTotalLuk());

            builder.append(" || 經驗值 : ");
            builder.append(other.getExp());

            builder.append(" || 組隊狀態 : ");
            builder.append(other.getParty() != null);

            builder.append(" || 交易狀態: ");
            builder.append(other.getTrade() != null);
            builder.append(" || Latency: ");
            builder.append(other.getClient().getLatency());
            builder.append(" || 最後PING: ");
            builder.append(other.getClient().getLastPing());
            builder.append(" || 最後PONG: ");
            builder.append(other.getClient().getLastPong());
            builder.append(" || IP: ");
            builder.append(other.getClient().getSessionIPAddress());
            other.getClient().DebugMessage(builder);

            c.getPlayer().dropMessage(6, builder.toString());
            return true;
        }
    }

    public static class WhosThere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            StringBuilder builder = new StringBuilder("在此地圖的玩家: ");
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return true;
        }
    }

    public static class Cheaters extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            List<CheaterData> cheaters = World.getCheaters();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return true;
        }
    }

    public static class Connected extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            java.util.Map<Integer, Integer> connected = World.getConnected();
            StringBuilder conStr = new StringBuilder("已連接的客戶端: ");
            boolean first = true;
            for (int i : connected.keySet()) {
                if (!first) {
                    conStr.append(", ");
                } else {
                    first = false;
                }
                if (i == 0) {
                    conStr.append("所有: ");
                    conStr.append(connected.get(i));
                } else {
                    conStr.append("頻道 ");
                    conStr.append(i);
                    conStr.append(": ");
                    conStr.append(connected.get(i));
                }
            }
            c.getPlayer().dropMessage(6, conStr.toString());
            return true;
        }
    }

    public static class ResetQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(1))).forfeit(c.getPlayer());
            return true;
        }
    }

    public static class StartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(1))).start(c.getPlayer(), Integer.parseInt(splitted.get(2)));
            return true;
        }
    }

    public static class CompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(1))).complete(c.getPlayer(), Integer.parseInt(splitted.get(2)), Integer.parseInt(splitted[3]));
            return true;
        }
    }

    public static class FStartQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(1))).forceStart(c.getPlayer(), Integer.parseInt(splitted.get(2)), splitted.size() >= 4 ? splitted[3] : null);
            return true;
        }
    }

    public static class FCompleteQuest extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(1))).forceComplete(c.getPlayer(), Integer.parseInt(splitted.get(2)));
            return true;
        }
    }

    public static class FStartOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(2))).forceStart(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1)), Integer.parseInt(splitted[3]), splitted.size() >= 4 ? splitted[4] : null);
            return true;
        }
    }

    public static class FCompleteOther extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.getInstance(Integer.parseInt(splitted.get(2))).forceComplete(c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1)), Integer.parseInt(splitted[3]));
            return true;
        }
    }

    public static class NearestPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MaplePortal portal = c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition());
            c.getPlayer().dropMessage(6, portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());

            return true;
        }
    }

    public static class SpawnDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(6, c.getPlayer().getMap().spawnDebug());
            return true;
        }
    }

    public static class Threads extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.size() > 1) {
                filter = splitted.get(1);
            }
            for (int i = 0; i < threads.length; i++) {
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().contains(filter.toLowerCase())) {
                    c.getPlayer().dropMessage(6, i + ": " + tstring);
                }
            }
            return true;
        }
    }

    public static class ShowTrace extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                throw new IllegalArgumentException();
            }
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted.get(1))];
            c.getPlayer().dropMessage(6, t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace()) {
                c.getPlayer().dropMessage(6, elem.toString());
            }
            return true;
        }
    }

    public static class FakeRelog extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            c.sendPacket(MaplePacketCreator.getCharInfo(player));
            player.getMap().removePlayer(player);
            player.getMap().addPlayer(player);
            return true;
        }
    }

    public static class ToggleOffense extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                CheatingOffense co = CheatingOffense.valueOf(splitted.get(1));
                co.setEnabled(!co.isEnabled());
            } catch (IllegalArgumentException iae) {
                c.getPlayer().dropMessage(6, "Offense " + splitted.get(1) + " not found");
            }
            return true;
        }
    }

    public static class TDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().toggleDrops();
            return true;
        }
    }

    public static class TMegaphone extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            World.toggleMegaphoneMuteState();
            c.getPlayer().dropMessage(6, "Megaphone state : " + (c.getChannelServer().getMegaphoneMuteState() ? "Enabled" : "Disabled"));
            return true;
        }
    }

    public static class SReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted.get(1)));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted.get(1)));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }
    }

    public static class HReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().getReactorByOid(Integer.parseInt(splitted.get(1))).hitReactor(c);
            return true;
        }
    }

    public static class DReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted.get(1).equals("all")) {
                for (MapleMapObject reactorL : reactors) {
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    c.getPlayer().getMap().destroyReactor(reactor2l.getObjectId());
                }
            } else {
                c.getPlayer().getMap().destroyReactor(Integer.parseInt(splitted.get(1)));
            }
            return true;
        }
    }

    public static class ResetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }
    }

    public static class SetReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().setReactorState(Byte.parseByte(splitted.get(1)));
            return true;
        }
    }

    public static class RemoveDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(5, "Cleared " + c.getPlayer().getMap().getNumItems() + " drops");
            c.getPlayer().getMap().removeDrops();
            return true;
        }
    }

    public static class ExpRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                final int rate = Integer.parseInt(splitted.get(1));
                if (splitted.size() > 2 && splitted.get(2).equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    c.getChannelServer().setExpRate(rate);
                }
                c.getPlayer().dropMessage(6, "Exprate has been changed to " + rate + "x");
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !exprate <number> [all]");
            }
            return true;
        }
    }

    public static class DropRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                final int rate = Integer.parseInt(splitted.get(1));
                if (splitted.size() > 2 && splitted.get(2).equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                } else {
                    c.getChannelServer().setDropRate(rate);
                }
                c.getPlayer().dropMessage(6, "Drop Rate has been changed to " + rate + "x");
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !droprate <number> [all]");
            }
            return true;
        }
    }

    public static class MesoRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                final int rate = Integer.parseInt(splitted.get(1));
                if (splitted.size() > 2 && splitted.get(2).equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    c.getChannelServer().setMesoRate(rate);
                }
                c.getPlayer().dropMessage(6, "Meso Rate has been changed to " + rate + "x");
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !mesorate <number> [all]");
            }
            return true;
        }
    }

    public static class CashRate extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() > 1) {
                final int rate = Integer.parseInt(splitted.get(1));
                if (splitted.size() > 2 && splitted.get(2).equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setCashRate(rate);
                    }
                } else {
                    c.getChannelServer().setCashRate(rate);
                }
                c.getPlayer().dropMessage(6, "Cash Rate has been changed to " + rate + "x");
            } else {
                c.getPlayer().dropMessage(6, "Syntax: !cashrate <number> [all]");
            }
            return true;
        }
    }

    /*    public static class ListSquads extends CommandExecute {

     @Override
     public boolean execute(MapleClient c, List<String> splitted) {
     for (Entry<String, MapleSquad> squads : c.getChannelServer().getAllSquads().entrySet()) {
     c.getPlayer().dropMessage(5, "TYPE: " + squads.getKey() + ", Leader: " + squads.getValue().getLeader().getName() + ", status: " + squads.getValue().getStatus() + ", numMembers: " + squads.getValue().getSquadSize() + ", numBanned: " + squads.getValue().getBannedMemberSize());
     }
     return true;
     }
     }*/
    public static class ClearSquads extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final Collection<MapleSquad> squadz = new ArrayList<MapleSquad>(c.getChannelServer().getAllSquads().values());
            for (MapleSquad squads : squadz) {
                squads.clear();
            }
            return true;
        }
    }

    public static class SetInstanceProperty extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted.get(1));
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                em.setProperty(splitted.get(2), splitted[3]);
                for (EventInstanceManager eim : em.getInstances()) {
                    eim.setProperty(splitted.get(2), splitted[3]);
                }
            }
            return true;
        }
    }

    public static class ListInstanceProperty extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted.get(1));
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", eventManager: " + em.getName() + " iprops: " + eim.getProperty(splitted.get(2)) + ", eprops: " + em.getProperty(splitted.get(2)));
                }
            }
            return true;
        }
    }

    public static class ListInstances extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            EventManager em = c.getChannelServer().getEventSM().getEventManager(StringUtil.joinStringFrom(splitted, 1));
            if (em == null || em.getInstances().size() <= 0) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                for (EventInstanceManager eim : em.getInstances()) {
                    c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", charSize: " + eim.getPlayers().size() + ", dcedSize: " + eim.getDisconnected().size() + ", mobSize: " + eim.getMobs().size() + ", eventManager: " + em.getName() + ", timeLeft: " + eim.getTimeLeft() + ", iprops: " + eim.getProperties().toString() + ", eprops: " + em.getProperties().toString());
                }
            }
            return true;
        }
    }

    public static class LeaveInstance extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "You are not in one");
            } else {
                c.getPlayer().getEventInstance().unregisterPlayer(c.getPlayer());
            }
            return true;
        }
    }

    public static class StartInstance extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getPlayer().getEventInstance() != null) {
                c.getPlayer().dropMessage(5, "You are in one");
            } else if (splitted.size() > 2) {
                EventManager em = c.getChannelServer().getEventSM().getEventManager(splitted.get(1));
                if (em == null || em.getInstance(splitted.get(2)) == null) {
                    c.getPlayer().dropMessage(5, "Not exist");
                } else {
                    em.getInstance(splitted.get(2)).registerPlayer(c.getPlayer());
                }
            } else {
                c.getPlayer().dropMessage(5, "!startinstance [eventmanager] [eventinstance]");
            }
            return true;

        }
    }

    public static class EventInstance extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (c.getPlayer().getEventInstance() == null) {
                c.getPlayer().dropMessage(5, "none");
            } else {
                EventInstanceManager eim = c.getPlayer().getEventInstance();
                c.getPlayer().dropMessage(5, "Event " + eim.getName() + ", charSize: " + eim.getPlayers().size() + ", dcedSize: " + eim.getDisconnected().size() + ", mobSize: " + eim.getMobs().size() + ", eventManager: " + eim.getEventManager().getName() + ", timeLeft: " + eim.getTimeLeft() + ", iprops: " + eim.getProperties().toString() + ", eprops: " + eim.getEventManager().getProperties().toString());
            }
            return true;
        }
    }

    public static class Uptime extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(6, "Server has been up for " + StringUtil.getReadableMillis(ChannelServer.serverStartTime, System.currentTimeMillis()));
            return true;
        }
    }

    public static class DCAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int range = -1;
            if (splitted.get(1).equals("m")) {
                range = 0;
            } else if (splitted.get(1).equals("c")) {
                range = 1;
            } else if (splitted.get(1).equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 1;
            }
            if (range == 0) {
                c.getPlayer().getMap().disconnectAll();
            } else if (range == 1) {
                c.getChannelServer().getPlayerStorage().disconnectAll(true);
            } else if (range == 2) {
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    cserv.getPlayerStorage().disconnectAll(true);
                }
            }
            return true;
        }
    }

    public static class GoTo extends CommandExecute {

        private static final HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();

        static {
            gotomaps.put("gmmap", 180000000);
            gotomaps.put("southperry", 2000000);
            gotomaps.put("amherst", 1010000);
            gotomaps.put("henesys", 100000000);
            gotomaps.put("ellinia", 101000000);
            gotomaps.put("perion", 102000000);
            gotomaps.put("kerning", 103000000);
            gotomaps.put("lithharbour", 104000000);
            gotomaps.put("sleepywood", 105040300);
            gotomaps.put("florina", 110000000);
            gotomaps.put("orbis", 200000000);
            gotomaps.put("happyville", 209000000);
            gotomaps.put("elnath", 211000000);
            gotomaps.put("ludibrium", 220000000);
            gotomaps.put("aquaroad", 230000000);
            gotomaps.put("leafre", 240000000);
            gotomaps.put("mulung", 250000000);
            gotomaps.put("herbtown", 251000000);
            gotomaps.put("omegasector", 221000000);
            gotomaps.put("koreanfolktown", 222000000);
            gotomaps.put("newleafcity", 600000000);
            gotomaps.put("sharenian", 990000000);
            gotomaps.put("pianus", 230040420);
            gotomaps.put("horntail", 240060200);
            gotomaps.put("chorntail", 240060201);
            gotomaps.put("mushmom", 100000005);
            gotomaps.put("griffey", 240020101);
            gotomaps.put("manon", 240020401);
            gotomaps.put("zakum", 280030000);
            gotomaps.put("czakum", 280030001);
            gotomaps.put("papulatus", 220080001);
            gotomaps.put("showatown", 801000000);
            gotomaps.put("zipangu", 800000000);
            gotomaps.put("ariant", 260000100);
            gotomaps.put("nautilus", 120000000);
            gotomaps.put("boatquay", 541000000);
            gotomaps.put("malaysia", 550000000);
            gotomaps.put("taiwan", 740000000);
            gotomaps.put("thailand", 500000000);
            gotomaps.put("erev", 130000000);
            gotomaps.put("ellinforest", 300000000);
            gotomaps.put("kampung", 551000000);
            gotomaps.put("singapore", 540000000);
            gotomaps.put("amoria", 680000000);
            gotomaps.put("timetemple", 270000000);
            gotomaps.put("pinkbean", 270050100);
            gotomaps.put("peachblossom", 700000000);
            gotomaps.put("fm", 910000000);
            gotomaps.put("freemarket", 910000000);
            gotomaps.put("oxquiz", 109020001);
            gotomaps.put("ola", 109030101);
            gotomaps.put("fitness", 109040000);
            gotomaps.put("snowball", 109060000);
            gotomaps.put("cashmap", 741010200);
            gotomaps.put("golden", 950100000);
            gotomaps.put("phantom", 610010000);
            gotomaps.put("cwk", 610030000);
            gotomaps.put("rien", 140000000);
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !goto <mapname>");
            } else {
                if (gotomaps.containsKey(splitted.get(1))) {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get(splitted.get(1)));
                    MaplePortal targetPortal = target.getPortal(0);
                    c.getPlayer().changeMap(target, targetPortal);
                } else {
                    if (splitted.get(1).equals("locations")) {
                        c.getPlayer().dropMessage(6, "Use !goto <location>. Locations are as follows:");
                        StringBuilder sb = new StringBuilder();
                        for (String s : gotomaps.keySet()) {
                            sb.append(s).append(", ");
                        }
                        c.getPlayer().dropMessage(6, sb.substring(0, sb.length() - 2));
                    } else {
                        c.getPlayer().dropMessage(6, "Invalid command 指令規則 - Use !goto <location>. For a list of locations, use !goto locations.");
                    }
                }
            }
            return true;
        }
    }

    public static class KillAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.size() > 1) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            MapleMonster mob;
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
            }
            c.getPlayer().dropMessage("您總共殺了 " + monsters.size() + " 怪物");
            return true;
        }
    }

    public static class ResetMobs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().killAllMonsters(false);
            return true;
        }
    }

    public static class KillMonster extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted.get(1))) {
                    mob.damage(c.getPlayer(), mob.getHp(), false);
                }
            }
            return true;
        }
    }

    public static class KillMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted.get(1));
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.killMonster(monster, c.getPlayer(), false, false, (byte) 1);
            }
            return true;
        }
    }

    public static class HitMonsterByOID extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            int targetId = Integer.parseInt(splitted.get(1));
            int damage = Integer.parseInt(splitted.get(2));
            MapleMonster monster = map.getMonsterByOid(targetId);
            if (monster != null) {
                map.broadcastMessage(MobPacket.damageMonster(targetId, damage));
                monster.damage(c.getPlayer(), damage, false);
            }
            return true;
        }
    }

    public static class HitAll extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.size() > 1) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            int damage = Integer.parseInt(splitted.get(1));
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                mob.damage(c.getPlayer(), damage, false);
            }
            return true;
        }
    }

    public static class HitMonster extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            int damage = Integer.parseInt(splitted.get(1));
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.getId() == Integer.parseInt(splitted.get(2))) {
                    map.broadcastMessage(MobPacket.damageMonster(mob.getObjectId(), damage));
                    mob.damage(c.getPlayer(), damage, false);
                }
            }
            return true;
        }
    }

    public static class KillAllDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.size() > 1) {
                //&& !splitted[0].equals("!killmonster") && !splitted[0].equals("!hitmonster") && !splitted[0].equals("!hitmonsterbyoid") && !splitted[0].equals("!killmonsterbyoid")) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
            }
            return true;
        }
    }

    public static class KillAllNoSpawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            map.killAllMonsters(false);
            return true;
        }
    }

    public static class MonsterDebug extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.size() > 1) {
                //&& !splitted[0].equals("!killmonster") && !splitted[0].equals("!hitmonster") && !splitted[0].equals("!hitmonsterbyoid") && !splitted[0].equals("!killmonsterbyoid")) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                c.getPlayer().dropMessage(6, "Monster " + mob.toString());
            }
            return true;
        }
    }

    public static class NPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int npcId = Integer.parseInt(splitted.get(1));
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "You have entered an invalid Npc-Id");
                return 0;
            }
            return true;
        }
    }

    public static class RemoveNPCs extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().resetNPCs();
            return true;
        }
    }

    public static class LookNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
                MapleNPC reactor2l = (MapleNPC) reactor1l;
                c.getPlayer().dropMessage(5, "NPC: oID: " + reactor2l.getObjectId() + " npcID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " Name: " + reactor2l.getName());
            }
            return true;
        }
    }

    public static class LookReactor extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleMapObject reactor1l : c.getPlayer().getMap().getAllReactorsThreadsafe()) {
                MapleReactor reactor2l = (MapleReactor) reactor1l;
                c.getPlayer().dropMessage(5, "Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getReactorId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState() + " Name: " + reactor2l.getName());
            }
            return true;
        }
    }

    public static class LookPortals extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MaplePortal portal : c.getPlayer().getMap().getPortals()) {
                c.getPlayer().dropMessage(5, "Portal: ID: " + portal.getId() + " script: " + portal.getScriptName() + " name: " + portal.getName() + " pos: " + portal.getPosition().x + "," + portal.getPosition().y + " target: " + portal.getTargetMapId() + " / " + portal.getTarget());
            }
            return true;
        }
    }

    public static class MakePNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleCharacter chhr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted.get(1) + " is not online");
                    return 0;
                }
                int npcId = Integer.parseInt(splitted.get(2));
                PlayerNPC npc = new PlayerNPC(chhr, npcId, c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "Done");
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
    }

    public static class MakeOfflineP extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                c.getPlayer().dropMessage(6, "Making playerNPC...");
                MapleClient cs = new MapleClient(null, null, new MockIOSession());
                MapleCharacter chhr = MapleCharacter.loadCharFromDB(MapleCharacterUtil.getIdByName(splitted.get(1)), cs, false);
                if (chhr == null) {
                    c.getPlayer().dropMessage(6, splitted.get(1) + " does not exist");
                    return 0;
                }
                PlayerNPC npc = new PlayerNPC(chhr, Integer.parseInt(splitted.get(2)), c.getPlayer().getMap(), c.getPlayer());
                npc.addToServer();
                c.getPlayer().dropMessage(6, "Done");
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
    }

    public static class DestroyPNPC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                c.getPlayer().dropMessage(6, "Destroying playerNPC...");
                final MapleNPC npc = c.getPlayer().getMap().getNPCByOid(Integer.parseInt(splitted.get(1)));
                if (npc instanceof PlayerNPC) {
                    ((PlayerNPC) npc).destroy(true);
                    c.getPlayer().dropMessage(6, "Done");
                } else {
                    c.getPlayer().dropMessage(6, "!destroypnpc [objectid]");
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(6, "NPC failed... : " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
    }

    public static class MyNPCPos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            Point pos = c.getPlayer().getPosition();
            c.getPlayer().dropMessage(6, "X: " + pos.x + " | Y: " + pos.y + " | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + c.getPlayer().getFH() + "| CY:" + pos.y);
            return true;
        }
    }

    public static class Notice extends CommandExecute {

        private static int getNoticeType(String typestring) {
            if (typestring.equals("n")) {
                return 0;
            } else if (typestring.equals("p")) {
                return 1;
            } else if (typestring.equals("l")) {
                return 2;
            } else if (typestring.equals("nv")) {
                return 5;
            } else if (typestring.equals("v")) {
                return 5;
            } else if (typestring.equals("b")) {
                return 6;
            }
            return -1;
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int joinmod = 1;
            int range = -1;
            if (splitted.get(1).equals("m")) {
                range = 0;
            } else if (splitted.get(1).equals("c")) {
                range = 1;
            } else if (splitted.get(1).equals("w")) {
                range = 2;
            }

            int tfrom = 2;
            if (range == -1) {
                range = 2;
                tfrom = 1;
            }
            int type = getNoticeType(splitted.get(tfrom));
            if (type == -1) {
                type = 0;
                joinmod = 0;
            }
            StringBuilder sb = new StringBuilder();
            if (splitted.get(tfrom).equals("nv")) {
                sb.append("[Notice]");
            } else {
                sb.append("");
            }
            joinmod += tfrom;
            sb.append(StringUtil.joinStringFrom(splitted, joinmod));

            MaplePacket packet = MaplePacketCreator.serverNotice(type, sb.toString());
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet.getBytes());
            }
            return true;
        }
    }

    public static class Yellow extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int range = -1;
            if (splitted.get(1).equals("m")) {
                range = 0;
            } else if (splitted.get(1).equals("c")) {
                range = 1;
            } else if (splitted.get(1).equals("w")) {
                range = 2;
            }
            if (range == -1) {
                range = 2;
            }
            MaplePacket packet = MaplePacketCreator.yellowChat((splitted.get(0).equals("!y") ? ("[" + c.getPlayer().getName() + "] ") : "") + StringUtil.joinStringFrom(splitted, 2));
            if (range == 0) {
                c.getPlayer().getMap().broadcastMessage(packet);
            } else if (range == 1) {
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            } else if (range == 2) {
                World.Broadcast.broadcastMessage(packet.getBytes());
            }
            return true;
        }
    }

    public static class Y extends Yellow {
    }

    public static class ReloadOps extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            return true;
        }
    }

    public static class ReloadDrops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            return true;
        }
    }

    public static class ReloadPortal extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            PortalScriptManager.getInstance().clearScripts();
            return true;
        }
    }

    public static class ReloadShops extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleShopFactory.getInstance().clear();
            return true;
        }
    }

    public static class ReloadEvents extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
            return true;
        }
    }

    public static class ReloadQuests extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.clearQuests();
            return true;
        }
    }

    public static class mobstatus extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int value = 0;
            try {
                Integer.parseInt(splitted.get(0));
                for (MonsterStatus s : MonsterStatus.values()) {

                }
            } catch (Exception e) {
            }
            return true;
        }

    }

    public static class Find extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() == 1) {
                c.getPlayer().dropMessage(6, splitted.get(0) + ": <NPC> <MOB> <ITEM> <MAP> <SKILL>");
            } else if (splitted.size() == 2) {
                c.getPlayer().dropMessage(6, "Provide something to search.");
            } else {
                String type = splitted.get(1);
                String search = StringUtil.joinStringFrom(splitted, 2);
                MapleData data = null;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(ServerProperties.getProperty("server.wzpath") + "/" + "String.wz");
                c.getPlayer().dropMessage(6, "<<Type: " + type + " | Search: " + search + ">>");

                if (type.equalsIgnoreCase("NPC")) {
                    List<String> retNpcs = new ArrayList<String>();
                    data = dataProvider.getData("Npc.img");
                    List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData npcIdData : data.getChildren()) {
                        npcPairList.add(new Pair<Integer, String>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> npcPair : npcPairList) {
                        if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
                        }
                    }
                    if (retNpcs != null && retNpcs.size() > 0) {
                        for (String singleRetNpc : retNpcs) {
                            c.getPlayer().dropMessage(6, singleRetNpc);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "No NPC's Found");
                    }

                } else if (type.equalsIgnoreCase("MAP")) {
                    List<String> retMaps = new ArrayList<String>();
                    data = dataProvider.getData("Map.img");
                    List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mapAreaData : data.getChildren()) {
                        for (MapleData mapIdData : mapAreaData.getChildren()) {
                            mapPairList.add(new Pair<Integer, String>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                        }
                    }
                    for (Pair<Integer, String> mapPair : mapPairList) {
                        if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                        }
                    }
                    if (retMaps != null && retMaps.size() > 0) {
                        for (String singleRetMap : retMaps) {
                            c.getPlayer().dropMessage(6, singleRetMap);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "No Maps Found");
                    }
                } else if (type.equalsIgnoreCase("MOB")) {
                    List<String> retMobs = new ArrayList<String>();
                    data = dataProvider.getData("Mob.img");
                    List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mobIdData : data.getChildren()) {
                        mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> mobPair : mobPairList) {
                        if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                        }
                    }
                    if (retMobs != null && retMobs.size() > 0) {
                        for (String singleRetMob : retMobs) {
                            c.getPlayer().dropMessage(6, singleRetMob);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "No Mob's Found");
                    }

                } else if (type.equalsIgnoreCase("ITEM")) {
                    List<String> retItems = new ArrayList<String>();
                    for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retItems.add(itemPair.getLeft() + " - " + itemPair.getRight());
                        }
                    }
                    if (retItems != null && retItems.size() > 0) {
                        for (String singleRetItem : retItems) {
                            c.getPlayer().dropMessage(6, singleRetItem);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "No Item's Found");
                    }

                } else if (type.equalsIgnoreCase("SKILL")) {
                    List<String> retSkills = new ArrayList<String>();
                    data = dataProvider.getData("Skill.img");
                    List<Pair<Integer, String>> skillPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData skillIdData : data.getChildren()) {
                        skillPairList.add(new Pair<Integer, String>(Integer.parseInt(skillIdData.getName()), MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> skillPair : skillPairList) {
                        if (skillPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add(skillPair.getLeft() + " - " + skillPair.getRight());
                        }
                    }
                    if (retSkills != null && retSkills.size() > 0) {
                        for (String singleRetSkill : retSkills) {
                            c.getPlayer().dropMessage(6, singleRetSkill);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "No Skills Found");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "Sorry, that search call is unavailable");
                }
            }
            return true;
        }
    }

    public static class ID extends Find {
    }

    public static class LookUp extends Find {
    }

    public static class Search extends Find {
    }

    public static class ServerMessage extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv : cservs) {
                cserv.setServerMessage(outputMessage);
            }
            return true;
        }
    }

    public static class Spawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int mid = Integer.parseInt(splitted.get(1));
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);

            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }

            long newhp = 0;
            int newexp = 0;
            if (hp != null) {
                newhp = hp.longValue();
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php.doubleValue() / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (exp != null) {
                newexp = exp.intValue();
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp.doubleValue() / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }
    }

    public static class Clock extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getClock(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 60)));
            return true;
        }
    }

    public static class WarpMapTo extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(1)));
                final MapleMap from = c.getPlayer().getMap();
                for (MapleCharacter chr : from.getCharactersThreadsafe()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (Exception e) {
                c.getPlayer().dropMessage(5, "錯誤: " + e.getMessage());
                return 0; //assume drunk GM
            }
            return true;
        }
    }

    public static class WarpHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            } else {
                int ch = World.Find.findChannel(splitted.get(1));
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "找不到");
                    return 0;
                }
                victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted.get(1));
                c.getPlayer().dropMessage(5, "正在把玩家傳到這來");
                victim.dropMessage(5, "正在傳送到GM那邊");
                if (victim.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    victim.changeMap(mapp, mapp.getPortal(0));
                }
                victim.changeChannel(c.getChannel());
            }
            return true;
        }
    }

    public static class WarpAllHere extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (MapleCharacter mch : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (mch.getMapId() != c.getPlayer().getMapId()) {
                    mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
                }
            }
            return true;
        }
    }

    public static class LOLCastle extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() != 2) {
                c.getPlayer().dropMessage(6, "Syntax: !lolcastle level (level = 1-5)");
                return 0;
            }
            MapleMap target = c.getChannelServer().getEventSM().getEventManager("lolcastle").getInstance("lolcastle" + splitted.get(1)).getMapFactory().getMap(990000300, false, false);
            c.getPlayer().changeMap(target, target.getPortal(0));

            return true;
        }
    }

    public static class Map extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(1)));
                MaplePortal targetPortal = null;
                if (splitted.size() > 2) {
                    try {
                        targetPortal = target.getPortal(Integer.parseInt(splitted.get(2)));
                    } catch (IndexOutOfBoundsException e) {
                        // noop, assume the gm didn't know how many portals there are
                        c.getPlayer().dropMessage(5, "Invalid portal selected.");
                    } catch (NumberFormatException a) {
                        // noop, assume that the gm is drunk
                    }
                }
                if (targetPortal == null) {
                    targetPortal = target.getPortal(0);
                }
                c.getPlayer().changeMap(target, targetPortal);
            } catch (Exception e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }
            return true;
        }
    }

    public static class StartProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("client");
            sampler.addIncluded("constants"); //or should we do Packages.constants etc.?
            sampler.addIncluded("database");
            sampler.addIncluded("handling");
            sampler.addIncluded("provider");
            sampler.addIncluded("scripting");
            sampler.addIncluded("server");
            sampler.addIncluded("tools");
            sampler.start();
            return true;
        }
    }

    public static class StopProfiling extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.size() > 1) {
                    filename = splitted.get(1);
                }
                File file = new File(filename);
                if (file.exists()) {
                    c.getPlayer().dropMessage(6, "The entered filename already exists, choose a different one");
                    return 0;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                System.err.println("Error saving profile" + e);
            }
            sampler.reset();
            return true;
        }
    }

    public static class ReloadMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int mapId = Integer.parseInt(splitted.get(1));
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                    c.getPlayer().dropMessage(5, "There exists characters on channel " + cserv.getChannel());
                    return 0;
                }
            }
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                if (cserv.getMapFactory().isMapLoaded(mapId)) {
                    cserv.getMapFactory().removeMap(mapId);
                }
            }
            return true;
        }
    }

    public static class Respawn extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().respawn(true);
            return true;
        }
    }

    public static class ResetMap extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getMap().resetFully();
            return true;
        }
    }

}
