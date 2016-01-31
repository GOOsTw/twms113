package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.StringUtil;
import client.messages.commands.BlackConfig;

public class InternCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.INTERN;
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "Ban";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 3) {
                return false;
            }
            StringBuilder sb = new StringBuilder(c.getPlayer().getName());
            sb.append(" banned ").append(splitted[1]).append(": ").append(StringUtil.joinStringFrom(splitted, 2));
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) {
                    sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), c.getPlayer().isAdmin(), false, hellban)) {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功封鎖 " + splitted[1] + ".");
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗.");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] May not ban GMs...");
                }
            } else {
                if (MapleCharacter.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), splitted[0].equals("!hellban"))) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 成功離線封鎖 " + splitted[1] + ".");
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封鎖失敗 " + splitted[1]);
                }
            }

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!ban <玩家> <原因> - 封鎖玩家").toString();
        }
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            return "UnBan";
        }

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            byte ret;
            if (hellban) {
                ret = MapleClient.unHellban(splitted[1]);
            } else {
                ret = MapleClient.unban(splitted[1]);
            }
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL error.");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] The character does not exist.");
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully unbanned!");
            }
            byte ret_ = MapleClient.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unban <玩家> - 解鎖玩家").toString();
        }
    }

    public static class 加黑單 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = splitted[1];
            int ch = World.Find.findChannel(input);
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim.isAdmin()) {
                c.getPlayer().dropMessage("玩家:" + input + " 是GM不能加黑單！");
                return true;
            }
            if (ch <= 0) {
                c.getPlayer().dropMessage("玩家:" + input + " 不在線上。");
                return true;
            }
            int accID = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(input).getAccountID();
            BlackConfig.setBlackList(accID, input);
            String msg = "[GM 密語] GM " + c.getPlayer().getName() + " 在回報系統黑單了 " + input;
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
            FilePrinter.print("PlayerBlackList.txt", "\r\n  " + FilePrinter.getLocalDateString() + " GM :" + c.getPlayer().getName() + " 在回報系統黑單了 " + input);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!黑單 <玩家名稱> - 將玩家設定為無法回報的黑名單").toString();
        }
    }

    public static class ChangeChanel extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            int cc = Integer.parseInt(splitted[1]);
            if (c.getChannel() != cc) {
                c.getPlayer().changeChannel(cc);
            } else {
                c.getPlayer().dropMessage(5, "請輸入正確的頻道。");
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!changechannel <頻道> - 更換頻道").toString();
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {

            if (splitted.length < 2) {
                return false;
            }

            int level = 0;
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

            if (victim != null) {
                victim.getClient().disconnect(true, false);
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!dc <玩家> - 讓玩家斷線").toString();
        }
    }

    public static class spy extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "使用規則: ");
            } else {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);

                if (victim != null) {
                    if (c.getPlayer().getGMLevel() < victim.getGMLevel()) {
                        c.getPlayer().dropMessage(5, "你不能查看比你高權限的人!");
                    } else {
                        c.getPlayer().dropMessage(5, "此玩家狀態:");
                        c.getPlayer().dropMessage(5, "等級: " + victim.getLevel() + " 職業: " + victim.getJob() + " 名聲: " + victim.getFame());
                        c.getPlayer().dropMessage(5, "地圖: " + victim.getMapId() + " - " + victim.getMap().getMapName());
                        c.getPlayer().dropMessage(5, "目前HP: " + victim.getStat().getHp() + " 目前MP: " + victim.getStat().getMp());
                        c.getPlayer().dropMessage(5, "最大HP: " + victim.getStat().getMaxHp() + " 最大MP: " + victim.getStat().getMaxMp());
                        c.getPlayer().dropMessage(5, "力量: " + victim.getStat().getStr() + "  ||  敏捷: " + victim.getStat().getDex() + "  ||  智力: " + victim.getStat().getInt() + "  ||  幸運: " + victim.getStat().getLuk());
                        c.getPlayer().dropMessage(5, "物理攻擊: " + victim.getStat().getTotalWatk() + "  ||  魔法攻擊: " + victim.getStat().getTotalMagic());
                        c.getPlayer().dropMessage(5, "DPM: " + victim.getDPS());
                        c.getPlayer().dropMessage(5, "經驗倍率: " + victim.getStat().expBuff + " 金錢倍率: " + victim.getStat().mesoBuff + " 掉寶倍率: " + victim.getStat().dropBuff);
                        c.getPlayer().dropMessage(5, "擁有 " + victim.getCSPoints(1) + " GASH " + victim.getCSPoints(2) + " 楓葉點數 " + victim.getMeso() + " 楓幣　");
                        c.getPlayer().dropMessage(5, "對伺服器延遲: " + victim.getClient().getLatency());
                    }
                } else {
                    c.getPlayer().dropMessage(5, "找不到此玩家.");
                }
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!spy <玩家名字>> - 觀察玩家").toString();
        }
    }

    public static class online extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            int total = 0;
            int curConnected = c.getChannelServer().getConnectedClients();
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            c.getPlayer().dropMessage(6, new StringBuilder().append("頻道: ").append(c.getChannelServer().getChannel()).append(" 線上人數: ").append(curConnected).toString());
            total += curConnected;
            for (MapleCharacter chr : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                if (chr != null && c.getPlayer().getGMLevel() >= chr.getGMLevel()) {
                    StringBuilder ret = new StringBuilder();
                    ret.append(" 角色暱稱 ");
                    ret.append(StringUtil.getRightPaddedStr(chr.getName(), ' ', 13));
                    ret.append(" ID: ");
                    ret.append(chr.getId());
                    ret.append(" 等級: ");
                    ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr.getLevel()), ' ', 3));
                    ret.append(" 職業: ");
                    ret.append(chr.getJob());
                    if (chr.getMap() != null) {
                        ret.append(" 地圖: ");
                        ret.append(chr.getMapId()).append(" - ").append(chr.getMap().getMapName());
                        c.getPlayer().dropMessage(6, ret.toString());
                    }
                }
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前頻道總計線上人數: ").append(total).toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");
            int channelOnline = c.getChannelServer().getConnectedClients();
            int totalOnline = 0;
            /*伺服器總人數*/
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                totalOnline += cserv.getConnectedClients();
            }
            c.getPlayer().dropMessage(6, new StringBuilder().append("當前伺服器總計線上人數: ").append(totalOnline).append("個").toString());
            c.getPlayer().dropMessage(6, "-------------------------------------------------------------------------------------");

            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!online - 查看線上人數").toString();
        }
    }

    public static class WhereAmI extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!whereami - 目前地圖").toString();
        }
    }

    public static class Warp extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            String input = splitted[1];
            int ch = World.Find.findChannel(input);
            if (ch < 0) {
                int mapid = -1;
                try {
                    mapid = Integer.parseInt(input);
                } catch (Exception ex) {
                    c.getPlayer().dropMessage(6, "出問題了 " + ex.getMessage());
                }
                doMap(c, mapid);
                return true;
            }

            MapleCharacter victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    if (victim.getMapId() != c.getPlayer().getMapId()) {
                        final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                        c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                    }
                    if (victim.getClient().getChannel() != c.getChannel()) {
                        c.getPlayer().dropMessage(6, "正在改變頻道請等待");
                        c.getPlayer().changeChannel(victim.getClient().getChannel());
                    }
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    doMap(victim.getClient(), Integer.parseInt(splitted[2]));
                    return true;
                }
            } else {
                c.getPlayer().dropMessage(6, "角色不存在");
            }
            return true;
        }

        public boolean doMap(MapleClient c, int mapid) {
            MapleMap target = null;
            try {
                target = c.getChannelServer().getMapFactory().getMap(mapid);
            } catch (Exception ex) {
            }
            if (target == null) {
                c.getPlayer().dropMessage(6, "地圖不存在");
            } else {
                c.getPlayer().changeMap(target, target.getPortal(0));
            }
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!warp 玩家名稱 <地圖ID> - 移動到某個地圖或某個玩家所在的地方").toString();
        }
    }

    public static class CnGM extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(5, "<GM聊天視窗>" + "頻道" + c.getPlayer().getClient().getChannel() + " [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!cngm <訊息> - GM聊天").toString();
        }
    }

    public static class 清地板 extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "清除 " + c.getPlayer().getMap().getNumItems() + " 項物品");
            c.getPlayer().getMap().removeDrops();
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("").toString();
        }
    }

    public static class Hide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理員隱藏 = 開啟 \r\n 解除請輸入!unhide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!hide - 隱藏").toString();
        }
    }

    public static class UnHide extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理員隱藏 = 關閉 \r\n 開啟請輸入!hide");
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!unhide - 解除隱藏").toString();
        }
    }
}
