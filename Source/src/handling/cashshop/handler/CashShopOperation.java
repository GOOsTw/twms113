package handling.cashshop.handler;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import constants.GameConstants;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.IItem;
import client.inventory.Item;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.util.ArrayList;
import java.util.List;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.packet.MTSCSPacket;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

public class CashShopOperation {

    public static void LeaveCashShop(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.sendPacket(MaplePacketCreator.getChannelChange(ChannelServer.getInstance(c.getChannel()).getIP(), ChannelServer.getInstance(c.getChannel()).getPort()));
        } finally {
            c.disconnect(true, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }

    public static void EnterCashShop(final int playerid, final MapleClient client) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                client.disconnect(false, false);
                return;
            }
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, client, false);

        client.setPlayer(chr);
        client.setAccID(chr.getAccountID());

        if (!client.CheckIPAddress()) { // Remote hack
            client.disconnect(false, true);
            return;
        }

        final int state = client.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(client.loadCharacterNames(client.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            client.setPlayer(null);
            client.getSession().close(true);
            return;
        }

        client.updateLoginState(MapleClient.LOGIN_LOGGEDIN, client.getSessionIPAddress());

        if (mts) {
            CashShopServer.getPlayerStorageMTS().registerPlayer(chr);
            client.getSession().write(MTSCSPacket.startMTS(chr, client));
            MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(client.getPlayer().getId()), client);
        } else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            client.sendPacket(MTSCSPacket.warpCS(client));
            sendCashShopUpdate(client);
        }
    }

    public static void sendCashShopUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.showCashShopAcc(c));
        c.sendPacket(MTSCSPacket.showGifts(c));
        RefreshCashShop(c);
        c.sendPacket(MTSCSPacket.sendShowWishList(c.getPlayer()));
    }

    public static void CouponCode(final String code, final MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;

        validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);

        if (validcode) {
            type = MapleCharacterUtil.getNXCodeType(code);
            item = MapleCharacterUtil.getNXCodeItem(code);
            if (type != 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             *
             * Type 1: A-Cash,
             * Type 2: Maple Points
             * Type 3: Item.. use SN
             * Type 4: A-Cash Coupon that can be used over and over
             * Type 5: Mesos
             */
            Map<Integer, IItem> itemz = new HashMap<>();
            int maplePoints = 0, mesos = 0;
            switch (type) {
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write(MTSCSPacket.sendCSFail(0));
                        RefreshCashShop(c);
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "");
                    if (slot <= -1) {
                        c.getSession().write(MTSCSPacket.sendCSFail(0));
                        RefreshCashShop(c);
                        return;
                    } else {
                        itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    }
                    break;
                case 4:
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    break;
                case 5:
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
            }
            c.getSession().write(MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.getSession().write(MTSCSPacket.sendCSFail(validcode ? 0xA5 : 0xA7)); //A1, 9F
        }
        RefreshCashShop(c);
    }

    public static final void BuyCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();

        switch (action) {

            case 3: {   // Buy Item
                final int useNX = slea.readByte() + 1;
                final int snCS = slea.readInt();
                CashItemInfo cItem = CashItemFactory.getInstance().getItem(snCS);

                boolean canBuy = true;
                int errorCode = 0;

                if (cItem == null || useNX < 1 || useNX > 2) {
                    canBuy = false;
                } else if (!cItem.onSale()) {
                    canBuy = false;
                    errorCode = 225;
                } else if (chr.getCSPoints(useNX) < cItem.getPrice()) {
                    if (useNX == 1) {
                        errorCode = 168;
                    } else {
                        errorCode = 225;
                    }
                    canBuy = false;
                } else if (!cItem.genderEquals(c.getPlayer().getGender())) {
                    canBuy = false;
                    errorCode = 186;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    canBuy = false;
                    errorCode = 166;
                }
                if (canBuy && cItem != null) {
                    for (int i : GameConstants.cashBlock) {
                        if (cItem.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem.getId()));
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    chr.modifyCSPoints(useNX, -cItem.getPrice(), false);
                    IItem itemz = chr.getCashInventory().toItem(cItem);
                    if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == cItem.getId() && itemz.getQuantity() == cItem.getCount()) {
                        chr.getCashInventory().addToInventory(itemz);
                        c.sendPacket(MTSCSPacket.showBoughtCashItem(itemz, cItem.getSN(), c.getAccID()));
                    } else {
                        c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                    }

                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                }

                RefreshCashShop(c);
                break;
            }
            case 4: { // gift
                final String secondPassword = slea.readMapleAsciiString();
                final int sn = slea.readInt();
                final String characterName = slea.readMapleAsciiString();
                final String message = slea.readMapleAsciiString();

                boolean canBuy = true;
                int errorCode = 0;

                CashItemInfo cItem = CashItemFactory.getInstance().getItem(sn);
                IItem item = chr.getCashInventory().toItem(cItem);

                Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(characterName, c.getPlayer().getWorld());

                if (cItem == null) {
                    canBuy = false;
                } else if (!cItem.onSale()) {
                    canBuy = false;
                    errorCode = 225;
                } else if (chr.getCSPoints(1) < cItem.getPrice()) {
                    errorCode = 168;
                    canBuy = false;
                } else if (!c.CheckSecondPassword(secondPassword)) {
                    canBuy = false;
                    errorCode = 197;
                } else if (message.getBytes().length < 1 || message.getBytes().length > 74) {
                    canBuy = false;
                    errorCode = 225;
                } else if (info == null) {
                    canBuy = false;
                    errorCode = 172;
                } else if (info.getRight().getLeft() == c.getAccID() || info.getLeft() == c.getPlayer().getId()) {
                    canBuy = false;
                    errorCode = 171;
                } else if (!cItem.genderEquals(info.getRight().getRight())) {
                    canBuy = false;
                    errorCode = 176;
                }
                if (canBuy && info != null && cItem != null) {
                    for (int i : GameConstants.cashBlock) {
                        if (cItem.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem.getId()));
                            return;
                        }
                    }
                    c.getPlayer().getCashInventory().gift(info.getLeft(), c.getPlayer().getName(), message, cItem.getSN(), MapleInventoryIdentifier.getInstance());
                    c.getPlayer().modifyCSPoints(1, -cItem.getPrice(), false);
                    c.sendPacket(MTSCSPacket.sendGift(cItem.getPrice(), cItem.getId(), cItem.getCount(), characterName));
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                }
                RefreshCashShop(c);
                break;
            }

            case 5: { //Wish List
                chr.clearWishlist();
                if (slea.available() < 40) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                int[] wishlist = new int[10];
                for (int i = 0; i < 10; i++) {
                    wishlist[i] = slea.readInt();
                }
                chr.setWishlist(wishlist);
                c.sendPacket(MTSCSPacket.setWishList(chr));
                RefreshCashShop(c);
                break;
            }
            ////////////////////
            case 6: {
                slea.skip(1);
                final boolean coupon = slea.readByte() > 0;
                if (coupon) {
                    final MapleInventoryType type = getInventoryType(slea.readInt());

                    if (chr.getCSPoints(1) >= 12000 && chr.getInventory(type).getSlotLimit() < 89) {
                        chr.modifyCSPoints(1, -12000, false);
                        chr.getInventory(type).addSlot((byte) 8);
                        chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    } else {
                        c.getSession().write(MTSCSPacket.sendCSFail(0xA4));
                    }
                } else {
                    final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());

                    if (chr.getCSPoints(1) >= 8000 && chr.getInventory(type).getSlotLimit() < 93) {
                        chr.modifyCSPoints(1, -8000, false);
                        chr.getInventory(type).addSlot((byte) 4);
                        chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    } else {
                        c.getSession().write(MTSCSPacket.sendCSFail(0xA4));
                    }
                }
                RefreshCashShop(c);
                break;
            }
            case 7: {
                if (chr.getCSPoints(1) >= 8000 && chr.getStorage().getSlots() < 45) {
                    chr.modifyCSPoints(1, -8000, false);
                    chr.getStorage().increaseSlots((byte) 4);
                    chr.getStorage().saveToDB();
                    c.getSession().write(MTSCSPacket.increasedStorageSlots(chr.getStorage().getSlots()));
                } else {
                    c.getSession().write(MTSCSPacket.sendCSFail(0xA4));
                }
                RefreshCashShop(c);
                break;
            }

            case 8: {
                slea.readByte();
                CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
                int slots = c.getCharacterSlots();
                if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || slots > 15) {
                    c.getSession().write(MTSCSPacket.sendCSFail(0));
                    RefreshCashShop(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                if (c.gainCharacterSlot()) {
                    c.getSession().write(MTSCSPacket.increasedStorageSlots(slots + 1));
                } else {
                    c.getSession().write(MTSCSPacket.sendCSFail(0));
                }
                RefreshCashShop(c);
                break;
            }

            case 13: {
                IItem item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
                if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    IItem item_ = item.copy();
                    short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                    if (pos >= 0) {
                        if (item_.getPet() != null) {
                            item_.getPet().setInventoryPosition(pos);
                            c.getPlayer().addPet(item_.getPet());
                        }
                        c.getPlayer().getCashInventory().removeFromInventory(item);
                        c.getSession().write(MTSCSPacket.confirmFromCSInventory(item_, pos));
                    } else {
                        c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                    }
                } else {
                    c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                }
                RefreshCashShop(c);
                break;
            }

            case 14: {
                int uniqueid = (int) slea.readLong();
                MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                IItem item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
                if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                    IItem item_ = item.copy();
                    MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false);
                    if (item_.getPet() != null) {
                        c.getPlayer().removePetCS(item_.getPet());
                    }
                    item_.setPosition((byte) 0);
                    c.getPlayer().getCashInventory().addToInventory(item_);
                    //warning: this d/cs
                    //c.getSession().write(MTSCSPacket.confirmToCSInventory(item, c.getAccID(), c.getPlayer().getCashInventory().getSNForItem(item)));
                } else {
                    c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                }
                RefreshCashShop(c);
                break;

            }

            case 32: { //1 meso
                RefreshCashShop(c);
                break;
            }

            case 29: // crush ring
            case 35: { // friendRing
                 /*
                 E6 00 
                 23 
                 08 00 5D 31 31 31 31 31 31 31 
                 EB E8 3E 01 
                 09 00 71 77 65 71 77 65 71 65 71 
                 04 00 58 44 44 0A
                 */
                final String secondPassword = slea.readMapleAsciiString();
                final int sn = slea.readInt();
                final String partnerName = slea.readMapleAsciiString();
                final String message = slea.readMapleAsciiString();
                final CashItemInfo cItem = CashItemFactory.getInstance().getItem(sn);
                Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());

                boolean canBuy = true;
                int errorCode = 0;

                if (cItem == null) {
                    canBuy = false;
                } else if (!cItem.onSale()) {
                    canBuy = false;
                    errorCode = 225;
                } else if (chr.getCSPoints(1) < cItem.getPrice()) {
                    errorCode = 168;
                    canBuy = false;
                } else if (!c.CheckSecondPassword(secondPassword)) {
                    canBuy = false;
                    errorCode = 197;
                } else if (message.getBytes().length < 1 || message.getBytes().length > 74) {
                    canBuy = false;
                    errorCode = 225;
                } else if (info == null) {
                    canBuy = false;
                    errorCode = 172;
                } else if (info.getRight().getLeft() == c.getAccID() || info.getLeft() == c.getPlayer().getId()) {
                    canBuy = false;
                    errorCode = 171;
                } else if (!cItem.genderEquals(info.getRight().getRight())) {
                    canBuy = false;
                    errorCode = 176;
                } else if (!GameConstants.isEffectRing(cItem.getId())) {
                    canBuy = false;
                    errorCode = 0;
                } else if (info.getRight().getRight() == c.getPlayer().getGender() && action == 29) {
                    canBuy = false;
                    errorCode = 191;
                }
                if (canBuy && info != null && cItem != null) {
                    for (int i : GameConstants.cashBlock) { //just incase hacker
                        if (cItem.getId() == i) {
                            c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(cItem.getId()));
                            RefreshCashShop(c);
                            return;
                        }
                    }
                    int err = MapleRing.createRing(cItem.getId(), c.getPlayer(), partnerName, message, info.getLeft(), cItem.getSN());
                    if (err != 1) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0)); //9E v75
                        RefreshCashShop(c);
                        return;
                    }
                    c.getPlayer().modifyCSPoints(1, -cItem.getPrice(), false);
                    c.getSession().write(MTSCSPacket.sendGift(cItem.getPrice(), cItem.getId(), cItem.getCount(), partnerName));

                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(errorCode));
                }

                RefreshCashShop(c);
                break;
            }

        }

        if (action == 31) {
            slea.skip(1);
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            List<CashItemInfo> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getPlayer().getCSPoints(1) < item.getPrice()) {
                c.getSession().write(MTSCSPacket.sendCSFail(0));
                RefreshCashShop(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(MTSCSPacket.sendCSFail(0xA6));
                RefreshCashShop(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                RefreshCashShop(c);
                return;
            }
            for (int iz : GameConstants.cashBlock) {
                if (item.getId() == iz) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    RefreshCashShop(c);
                    return;
                }
            }
            Map<Integer, IItem> ccz = new HashMap<Integer, IItem>();
            for (CashItemInfo i : ccc) {
                for (int iz : GameConstants.cashBlock) {
                    if (i.getId() == iz) {
                        continue;
                    }
                }
                IItem itemz = c.getPlayer().getCashInventory().toItem(i);
                if (itemz == null || itemz.getUniqueId() <= 0 || itemz.getItemId() != i.getId()) {
                    continue;
                }
                ccz.put(i.getSN(), itemz);
                c.getPlayer().getCashInventory().addToInventory(itemz);
            }
            chr.modifyCSPoints(1, -item.getPrice(), false);
            c.getSession().write(MTSCSPacket.showBoughtCashPackage(ccz, c.getAccID()));

        } else if (action == 33) {
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.getSession().write(MTSCSPacket.sendCSFail(0));
                RefreshCashShop(c);
                return;
            } else if (c.getPlayer().getMeso() < item.getPrice()) {
                c.getSession().write(MTSCSPacket.sendCSFail(0xB8));
                RefreshCashShop(c);
                return;
            } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                RefreshCashShop(c);
                return;
            }
            for (int iz : GameConstants.cashBlock) {
                if (item.getId() == iz) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    RefreshCashShop(c);
                    return;
                }
            }
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null);
            if (pos < 0) {
                c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
                RefreshCashShop(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.getSession().write(MTSCSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
        } else {
            c.getSession().write(MTSCSPacket.sendCSFail(0));

        }
        RefreshCashShop(c);
    }

    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200075:
                return MapleInventoryType.EQUIP;
            case 50200074:
                return MapleInventoryType.USE;
            case 50200073:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    private static final void RefreshCashShop(MapleClient c) {
        c.getSession().write(MTSCSPacket.showCashInventory(c));
        c.getSession().write(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.getSession().write(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
