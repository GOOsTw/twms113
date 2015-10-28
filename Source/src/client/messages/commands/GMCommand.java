package client.messages.commands;

import client.MapleCharacter;
import constants.ServerConstants.PlayerGMRank;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessorUtil;
import constants.GameConstants;
import handling.world.World;
import java.util.Map.Entry;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.ArrayMap;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.StringUtil;

/**
 *
 * @author Emilyx3
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
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

    public static class Job extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().changeJob(Integer.parseInt(splitted[1]));
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!job <職業代碼> - 更換職業").toString();
        }
    }

    public static class maxmeso extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!maxmeso - 楓幣滿").toString();
        }
    }

    public static class mesos extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                return false;
            }
            c.getPlayer().gainMeso(Integer.parseInt(splitted[1]), true);
            return true;
        }

        @Override
        public String getMessage() {
            return new StringBuilder().append("!mesos <需要的數量> - 得到楓幣").toString();
        }
    }

        public static class Item extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

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


     public static class Level extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            c.getPlayer().setLevel(Short.parseShort(splitted[1]));
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

     public static class ClearInv extends CommandExecute {

        @Override
        public boolean execute(MapleClient c, String splitted[]) {
            java.util.Map<Pair<Short, Short>, MapleInventoryType> eqs = new ArrayMap<>();
            switch (splitted[1]) {
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
}
