package client.messages.commands;

import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventoryType;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMap;
import java.util.Arrays;
import tools.StringUtil;
import handling.world.World;
import server.MapleInventoryManipulator;
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
            9330082};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                        return 0;
                    }
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "你的等級必須是10等.");
                    return 0;
                }
                if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return 0;
                }
                if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return 0;
                }
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return 1;
        }
    }

    public static class DropCash extends 丟裝 {
    }

    public static class 丟裝 extends OpenNPCCommand {

        public 丟裝() {
            npc = 0;
        }
    }

    public static class event extends OpenNPCCommand {

        public event() {
            npc = 1;
        }
    }

    public static class bspq extends OpenNPCCommand {

        public bspq() {
            npc = 3;
        }
    }

    public static class save extends 存檔 {
    }

    public static class 存檔 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().saveToDB(false, true);
            c.getPlayer().dropMessage(5, "保存成功！");
            return 1;
        }
    }

    public static class expfix extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().dropMessage(5, "經驗修復完成");
            return 1;
        }
    }

    public static class ea extends 查看 {
    }

    public static class 查看 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "解卡完畢.");
            c.getPlayer().dropMessage(6, "當前時間是" + FilePrinter.getLocalDateString() + " GMT+8 | 經驗值倍率 " + (Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, 掉寶倍率 " + (Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, 楓幣倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100 + "%");
            c.getPlayer().dropMessage(6, "目前剩餘 " + c.getPlayer().getCSPoints(1) + " GASH " + c.getPlayer().getCSPoints(2) + " 楓葉點數 ");
            c.getPlayer().dropMessage(6, "當前延遲 " + c.getPlayer().getClient().getLatency() + " 毫秒");
            return 1;
        }
    }

    public static class mob extends 怪物 {
    }

    public static class 怪物 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "找不到地圖上的怪物");
            }
            return 1;
        }
    }

    public static class npc extends 萬能 {
    }

    public static class 萬能 extends OpenNPCCommand {

        public 萬能() {
            npc = 2;
        }
    }

    public static class fm extends 自由 {
    }

    public static class 自由 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if ((c.getPlayer().getMapId() >= 925020109 && c.getPlayer().getMapId() <= 925023814)) {
                    c.getPlayer().dropMessage(5, "你不能在這裡使用指令.");
                    return 0;
                }
            if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                c.getPlayer().dropMessage("你必須超過 10 等來使用此指令.");
                return 0;
            }
            if (c.getPlayer().haveItem(2030000)) { //是否有回家卷軸
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2030000, 1, true, false);
                c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET);
                MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
                c.getPlayer().changeMap(map, map.getPortal(0));
                c.getPlayer().dropMessage(5, "回到自由了");
                return 1;
            } else {
                c.getPlayer().dropMessage(5, "沒有回家卷軸,所以不能使用回自由指令!");
                return 0;
            }
        }
    }

    public static class CGM extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted[1].length() == 0) {
                c.getPlayer().dropMessage(6, "請打字謝謝.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因為你自己是GM所法使用此指令,可以嘗試!cngm <訊息> 來建立GM聊天頻道~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 5 minutes.
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "訊息已經寄送給GM了!");
            } else {
                c.getPlayer().dropMessage(6, "為了防止對GM刷屏所以每1分鐘只能發一次.");
            }
            return 1;
        }
    }

    public static class help extends 幫助 {
    }

    public static class 幫助 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "SyncMs 玩家指令");
            c.getPlayer().dropMessage(5, "@查看/@ea <解除異常+查看當前狀態>");
            c.getPlayer().dropMessage(5, "@丟裝/@DropCash <丟棄點裝>");
            c.getPlayer().dropMessage(5, "@怪物/@mob <查看身邊怪物訊息>");
            c.getPlayer().dropMessage(5, "@萬能/@npc <工具箱>");
            c.getPlayer().dropMessage(5, "@存檔/@save <存檔>");
            c.getPlayer().dropMessage(5, "@自由/@fm <回自由-需要1張回家卷軸>");
            c.getPlayer().dropMessage(5, "@event <參加活動>");
            c.getPlayer().dropMessage(5, "@CGM 訊息 <傳送訊息給GM>");
            c.getPlayer().dropMessage(5, "@bspq <BOSSPQ兌換NPC>");
            c.getPlayer().dropMessage(5, "@expfix <修復經驗假死");
            return 1;
        }
    }
}
