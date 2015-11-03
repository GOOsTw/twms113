package client.messages.commands;

import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import constants.MapConstants;
import handling.channel.ChannelServer;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMap;
import java.util.Arrays;
import tools.StringUtil;
import handling.world.World;
import java.util.Calendar;
import server.maps.SavedLocationType;
import tools.FilePrinter;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static final int[] npcs = { //Ish yur job to make sure these are in order and correct ;(
            9010017,
            9000001,
            9000058,
            9330082,
            9209002};

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                        return true;
                    }
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "你的等級必須是10等.");
                    return true;
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return true;
                }
                if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return true;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return true;
        }
    }

    public static class 丟裝 extends DropCash {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@丟裝 - 呼叫清除現金道具npc").toString();
        }
    }

    public static class DropCash extends OpenNPCCommand {

        public DropCash() {
            npc = 0;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@dropbash - 呼叫清除現金道具npc").toString();
        }

    }

    public static class event extends OpenNPCCommand {

        public event() {
            npc = 1;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@event - 呼叫活動npc").toString();
        }
    }

    public static class npc extends 萬能 {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@npc - 呼叫萬能npc").toString();
        }
    }

    public static class 萬能 extends OpenNPCCommand {

        public 萬能() {
            npc = 2;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@萬能 - 呼叫萬能npc").toString();
        }
    }

    public static class bspq extends OpenNPCCommand {

        public bspq() {
            npc = 3;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@bspq - 呼叫Boss挑戰npc").toString();
        }
    }

    public static class pk extends 猜拳 {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@pk - 呼叫猜拳npc").toString();
        }
    }

    public static class 猜拳 extends OpenNPCCommand {

        public 猜拳() {
            npc = 4;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@猜拳 - 呼叫猜拳npc").toString();
        }
    }

    public static class save extends 存檔 {
    }

    public static class 存檔 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int res = c.getPlayer().saveToDB(true, true);
            if (res == 1) {
                c.getPlayer().dropMessage(5, "保存成功！");
            } else {
                c.getPlayer().dropMessage(5, "保存失敗！");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@save - 存檔").toString();
        }
    }

    public static class time extends CommandExecute {


        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (!c.getPlayer().getCheatTracker().GMSpam(300000, 3) && (!c.getPlayer().isGM())) { // 5 minutes.
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "『玩家』" + c.getPlayer().getName() + "使用了『報時系統』 當前時間:" + FilePrinter.getLocalDateString() + " 星期" + getDayOfWeek()).getBytes());
            } else if (c.getPlayer().isGM()) {
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "『管理員』" + c.getPlayer().getName() + "使用了『報時系統』 當前時間:" + FilePrinter.getLocalDateString() + " 星期" + getDayOfWeek()).getBytes());
            } else {
                c.getPlayer().dropMessage(6, "為了防止瘋狂報時引響其他玩家，所以5分鐘只能使用一次。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@time - 目前時間").toString();
        }

        public static String getDayOfWeek() {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
            String dd = String.valueOf(dayOfWeek);
            switch (dayOfWeek) {
                case 0:
                    dd = "日";
                    break;
                case 1:
                    dd = "一";
                    break;
                case 2:
                    dd = "二";
                    break;
                case 3:
                    dd = "三";
                    break;
                case 4:
                    dd = "四";
                    break;
                case 5:
                    dd = "五";
                    break;
                case 6:
                    dd = "六";
                    break;
            }
            return dd;
        }
    }

    public static class expfix extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().dropMessage(5, "經驗修復完成");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@expfix - 經驗歸零").toString();
        }
    }

    public static class 在線人數 extends online {

    }

    public static class online extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int channelOnline = c.getChannelServer().getConnectedClients();
            int totalOnline = 0;
            /*伺服器總人數*/
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前").append(c.getChannel()).append("頻道: ").append(channelOnline).append("人   ").append("當前伺服器總計線上人數: ").append(totalOnline).append("個").toString());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@online - 查看線上人數").toString();
        }
    }

    public static class 查看 extends ea {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@查看 - 解卡").toString();
        }
    }

    public static class ea extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "解卡完畢.");
            c.getPlayer().dropMessage(6, "經驗值倍率 " + (Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, 掉寶倍率 " + (Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, 楓幣倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100 + "%");
            c.getPlayer().dropMessage(6, "目前剩餘 " + c.getPlayer().getCSPoints(1) + " GASH " + c.getPlayer().getCSPoints(2) + " 楓葉點數 ");
            c.getPlayer().dropMessage(6, "當前延遲 " + c.getPlayer().getClient().getLatency() + " 毫秒");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@ea - 解卡").toString();
        }
    }

    public static class 怪物 extends mob {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@怪物 - 查看怪物狀態").toString();
        }
    }

    public static class mob extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            MapleMonster monster = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                monster = (MapleMonster) monstermo;
                if (monster.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + monster.toString());
                }
            }
            if (monster == null) {
                c.getPlayer().dropMessage(6, "找不到地圖上的怪物");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@mob - 查看怪物狀態").toString();
        }
    }

    public static class 卡圖 extends stocked {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@卡圖 - 解除卡圖").toString();
        }
    }

    public static class stocked extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            if (MapConstants.isCar(c.getPlayer().getMapId())) {
                c.getPlayer().clearSavedLocation(SavedLocationType.MONSTER_CARNIVAL);
                MapleMap map = c.getChannelServer().getMapFactory().getMap(100000000);
                c.getPlayer().changeMap(map, map.getPortal(0));
                c.getPlayer().dropMessage(5, "卡圖解救成功！");
            } else {
                c.getPlayer().dropMessage(1, "你並沒有卡圖啊。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@stocked - 解除卡圖").toString();
        }
    }

    public static class CGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted[1].length() == 0) {
                return false;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因為你自己是GM所法使用此指令,可以嘗試!cngm <訊息> 來建立GM聊天頻道~");
            } else {
                if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 1 minutes.
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                    c.getPlayer().dropMessage(6, "訊息已經寄送給GM了!");
                    System.out.println("[管理員幫幫忙] " + c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1));
                } else {
                    c.getPlayer().dropMessage(6, "為了防止對GM刷屏所以每1分鐘只能發一次.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@cgm - 跟GM回報").toString();
        }
    }

    public static class 幫助 extends help {

        @Override
        public String getMessage() {
            return new StringBuilder().append("@幫助 - 幫助").toString();
        }
    }

    public static class help extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropNPC("\t\t #i3994014##i3994018##i3994070##i3994061##i3994005##i3991038##i3991004#\r\t\t\t\t\t\t #i3994078##i3991040#\t\t\r\n\t\t\t #i3991018##i3994083##i3994072##i3994061##i4001126#\r\n\t\t#fMob/0100101.img/move/1##b 親愛的： #h \r\n #fMob/0100101.img/move/1##k\r\r\n\t\t#fMob/0130101.img/move/1##g[以下是SyncMs 玩家指令]#k#fMob/0130101.img/move/1#\r\n\t  #r▇▇▆▅▄▃▂#d萬用指令區#r▂▃▄▅▆▇▇\r\n\t\t#b@查看/@ea#k - #r<解除異常+查看當前狀態>#k\r\n\t\t#b@怪物/@mob#k - #r<查看身邊怪物訊息>#k\r\n\t\t#b@存檔/@save#k - #r<存檔>#k\r\n\t\t#b@卡圖/@car#k - #r<卡圖修復>#k\r\n\t\t#b@CGM <訊息>#k - #r<傳送訊息給GM>#k\r\n\t\t#b@expfix#k - #r<修復經驗假死>#k\r\n\t\t#b@time#k - #r<報時系統>#k\r\n\t\t#b@在線人數#k - #r<查詢當前伺服器人數>#k\r\n\t  #g▇▇▆▅▄▃▂#dNPＣ指令區#g▂▃▄▅▆▇▇\r\n\t\t#b@丟裝/@DropCash#k - #r<丟棄點裝>#k\r\n\t\t#b@萬能/@npc#k - #r<工具箱>#k\r\n\t\t#b@猜拳/@pk#k - #r<小遊戲>#k\r\n\t\t#b@event#k - #r<參加活動>#k\r\n\t\t#b@bspq#k - #r<BOSSPQ兌換NPC>#k");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("@help - 幫助").toString();
        }
    }

}
