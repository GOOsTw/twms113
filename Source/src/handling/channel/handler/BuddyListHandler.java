/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel.handler;

import static client.BuddyList.BuddyOperation.ADDED;
import static client.BuddyList.BuddyOperation.DELETED;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.BuddyList;
import client.BuddyEntry;
import client.MapleCharacter;
import client.MapleClient;
import client.BuddyList.BuddyAddResult;
import client.BuddyList.BuddyOperation;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BuddyListHandler {

    private static void nextPendingRequest(final MapleClient c) {
        BuddyEntry pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getCharacterId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
    }

    private static int getBuddyCount(int chrId, int pending) {
        int count = 0;
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = ?")) {
            ps.setInt(1, chrId);
            ps.setInt(2, pending);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("BuddyListHandler: getBuudyCount From DB is Error.");
            } else {
                count = rs.getInt("buddyCount");
            }
            rs.close();

        } catch (SQLException ex) {
            FilePrinter.printError("BuddyListHandler.txt", ex);
        }
        return count;
    }
    
    private static int getBuddyCapacity(int charId) {
        int capacity = -1;
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT buddyCapacity FROM characters WHERE id = ?")) {
            ps.setInt(1, charId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    capacity = rs.getInt("buddyCapacity");
                }
            }
        } catch (SQLException ex) {
            FilePrinter.printError("BuddyListModifyHandler.txt", ex);
        }

        return capacity;
    }

    private static int getBuddyPending(int chrId, int buddyId) {
        int pending = -1;
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?")) {
            ps.setInt(1, chrId);
            ps.setInt(2, buddyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pending = rs.getInt("pending");
                }
            }
        } catch (SQLException ex) {
            FilePrinter.printError("BuddyListModifyHandler.txt", ex);
        }

        return pending;
    }

    private static void addBuddyToDB(MapleCharacter player, BuddyEntry buddy) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
            ps.setInt(1, buddy.getCharacterId());
            ps.setInt(2, player.getId());
            ps.setString(3, buddy.getGroup());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            FilePrinter.printError("BuddyListModifyHandler.txt", ex);
        }
    }

    public static final void BuddyOperationHandler(final SeekableLittleEndianAccessor slea,
            final MapleClient client) {

        MapleCharacter player = client.getPlayer();

        final int mode = slea.readByte();

        final BuddyList buddyList = player.getBuddylist();

        switch (mode) {
            case 1: {
                final String buddyName = slea.readMapleAsciiString();
                final String buddyGroup = slea.readMapleAsciiString();
                final BuddyEntry oldBuddy = buddyList.get(buddyName);

                if (buddyName.length() > 13 || buddyGroup.length() > 16) {
                    nextPendingRequest(client);

                    return;
                }

                /* 檢查好友是否存在 */
                if (oldBuddy != null && oldBuddy.getGroup().equals(buddyGroup)) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    nextPendingRequest(client);

                    return;
                }

                /* 如果存在，群組不一樣則改群組*/
                if (oldBuddy != null) {
                    oldBuddy.setGroup(buddyGroup);
                    client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                    nextPendingRequest(client);

                    return;
                }
                /* 檢查好友是否滿了 */
                if (buddyList.isFull()) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    return;
                }
                /* 從整個遊戲找這個名字的角色所在的頻道 */
                int buddyChannel = World.Find.findChannel(buddyName);
                MapleCharacter buddyChar = null;
                BuddyEntry buddy = null;
                BuddyAddResult reqRes = null;

                if (buddyChannel > 0) {
                    buddyChar = ChannelServer.getInstance(buddyChannel)
                            .getPlayerStorage().getCharacterByName(buddyName);
                    /* 如果是GM則無法被普通玩家加入 */
                    if (!buddyChar.isGM() || player.isGM()) {
                        buddy = new BuddyEntry(buddyChar.getName(),
                                buddyChar.getId(),
                                buddyGroup,
                                buddyChannel,
                                true,
                                buddyChar.getLevel(),
                                buddyChar.getJob());
                    }
                } else {
                    buddy = BuddyEntry.getByNameFromDB(buddyName);
                    
                }

                /* 無此角色*/
                if (buddy == null) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 15));
                    nextPendingRequest(client);
                    return;
                }

                buddy.setGroup(buddyGroup);
                buddy.setChannel(buddyChannel);
                buddy.setVisible(true);

                /* 如果好友在線上 直接傳封包過去囉*/
                if (buddyChannel > 0) {
                    reqRes = World.Buddy.requestBuddyAdd(buddyName,
                            client.getChannel(),
                            client.getPlayer().getId(),
                            client.getPlayer().getName(),
                            client.getPlayer().getLevel(),
                            client.getPlayer().getJob());
                } else {

                    final int buddyCount = getBuddyCount(buddy.getCharacterId(), 0);

                    if (buddyCount == -1) {
                        throw new RuntimeException("Result set expected");
                    } else {
                        if (buddyCount >= getBuddyCapacity(buddy.getCharacterId()) ) {
                            reqRes = BuddyAddResult.BUDDYLIST_FULL;
                        }
                    }
                    int pending = getBuddyPending(buddy.getCharacterId(), player.getId());
                    if (pending > -1) {
                        reqRes = BuddyAddResult.ALREADY_ON_LIST;
                    }
                }

                if (reqRes == BuddyAddResult.BUDDYLIST_FULL) {

                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 12));

                } else {
                    if (reqRes == BuddyAddResult.ALREADY_ON_LIST && buddyChannel > 0) {
                        notifyRemoteChannel(client, buddyChannel, buddy.getCharacterId(), buddyGroup, ADDED);
                    } else {
                        addBuddyToDB(player, buddy);
                    }
                    buddyList.put(buddy);
                    client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                }

                nextPendingRequest(client);
                break;
            }

            case 2: {
                final int buddyCharId = slea.readInt();

                if (buddyList.isFull()) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                    nextPendingRequest(client);
                    return;
                }

                final int buddyChannel = World.Find.findChannel(buddyCharId);
                BuddyEntry buddy = null;

                if (buddyChannel < 0) {
                    buddy = BuddyEntry.getByIdfFromDB(buddyCharId);
                } else {
                    final MapleCharacter buddyChar = ChannelServer.getInstance(buddyChannel).getPlayerStorage().getCharacterById(buddyCharId);
                    buddy = new BuddyEntry(
                            buddyChar.getName(),
                            buddyChar.getId(),
                            BuddyList.DEFAULT_GROUP,
                            buddyChannel,
                            true,
                            buddyChar.getLevel(),
                            buddyChar.getJob()
                    );

                }

                if (buddy == null) {
                    client.sendPacket(MaplePacketCreator.buddylistMessage((byte) 11));
                } else {
                    buddyList.put(buddy);
                    client.sendPacket(MaplePacketCreator.updateBuddylist(buddyList.getBuddies()));
                    notifyRemoteChannel(client, buddyChannel, buddyCharId, "其他", ADDED);
                }
                nextPendingRequest(client);
                break;
            }

            case 3: {
                final int buddyCharId = slea.readInt();
                final BuddyEntry buddy = buddyList.get(buddyCharId);
                if (buddy != null && buddy.isVisible()) {
                    notifyRemoteChannel(client, World.Find.findChannel(buddyCharId), buddyCharId, buddy.getGroup(), DELETED);

                }
                buddyList.remove(buddyCharId);
                client.sendPacket(MaplePacketCreator.updateBuddylist(player.getBuddylist().getBuddies()));
                nextPendingRequest(client);
                break;
            }
            default: {
                FilePrinter.printError("BuddyListHandler.txt", "Unknown Buddylist Operation " + String.valueOf(mode) + " " + slea.toString());
                break;
            }
        }

    }

    private static void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();

        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }
}
