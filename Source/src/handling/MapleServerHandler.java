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
package handling;

import constants.ServerConstants;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import client.MapleClient;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.cashshop.handler.*;
import handling.channel.handler.*;
import handling.login.LoginServer;
import handling.login.handler.*;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import server.Randomizer;
import tools.MapleAESOFB;
import tools.packet.LoginPacket;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.Pair;

import server.MTSStorage;
import server.ServerProperties;
import tools.FilePrinter;

public class MapleServerHandler extends IoHandlerAdapter implements MapleServerHandlerMBean {

    public static final boolean isLogPackets = true;
    private int channel = -1;
    private boolean cs;
    private final List<String> BlockedIP = new ArrayList<>();
    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<>();
    private static final String nl = System.getProperty("line.separator");
    private static final File loggedIPs = new File("iplogs/logIps.txt");
    private static final HashMap<String, FileWriter> logIPMap = new HashMap<>();
    private static boolean debugMode = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.debug", "false"));
    private static final EnumSet<RecvPacketOpcode> blocked = EnumSet.noneOf(RecvPacketOpcode.class);

    static {
     
        RecvPacketOpcode[] block = new RecvPacketOpcode[]{RecvPacketOpcode.NPC_ACTION, RecvPacketOpcode.MOVE_PLAYER, RecvPacketOpcode.MOVE_PET, RecvPacketOpcode.MOVE_SUMMON, RecvPacketOpcode.MOVE_LIFE, RecvPacketOpcode.HEAL_OVER_TIME, RecvPacketOpcode.STRANGE_DATA};
        blocked.addAll(Arrays.asList(block));
    }

    //Return the Filewriter if the IP is logged. Null otherwise.
    private static FileWriter isLoggedIP(IoSession sess) {
        String a = sess.getRemoteAddress().toString();
        String realIP = a.substring(a.indexOf('/') + 1, a.indexOf(':'));
        return logIPMap.get(realIP);
    }
// <editor-fold defaultstate="collapsed" desc="Packet Log Implementation">
    private static final int packetLogMaxSize = 10000;
    private static final ArrayList<LoggedPacket> packetLog = new ArrayList<>(packetLogMaxSize);
    private static final ReentrantReadWriteLock packetLogLock = new ReentrantReadWriteLock();
    private static final File packetLogFile = new File("PacketLog.txt");

    public static void log(SeekableLittleEndianAccessor packet, RecvPacketOpcode op, MapleClient c, IoSession io) {
        if (blocked.contains(op)) {
            return;
        }
        try {
            packetLogLock.writeLock().lock();
            LoggedPacket logged = null;
            if (packetLog.size() == packetLogMaxSize) {
                logged = packetLog.remove(0);
            }
            //This way, we don't create new LoggedPacket objects, we reuse them =]
            if (logged == null) {
                logged = new LoggedPacket(packet, op, io.getRemoteAddress().toString(),
                        c == null ? -1 : c.getAccID(),
                        c == null || c.getAccountName() == null ? "[Null]" : c.getAccountName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName());
            } else {
                logged.setInfo(packet, op, io.getRemoteAddress().toString(),
                        c == null ? -1 : c.getAccID(),
                        c == null || c.getAccountName() == null ? "[Null]" : c.getAccountName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName());
            }
            packetLog.add(logged);
        } finally {
            packetLogLock.writeLock().unlock();
        }
    }

    private static class LoggedPacket {

        private static final String nl = System.getProperty("line.separator");
        private String ip, accName, accId, chrName;
        private SeekableLittleEndianAccessor packet;
        private long timestamp;
        private RecvPacketOpcode op;

        public LoggedPacket(SeekableLittleEndianAccessor p, RecvPacketOpcode op, String ip, int id, String accName, String chrName) {
            setInfo(p, op, ip, id, accName, chrName);
        }

        public final void setInfo(SeekableLittleEndianAccessor p, RecvPacketOpcode op, String ip, int id, String accName, String chrName) {
            this.ip = ip;
            this.op = op;
            packet = p;
            this.accName = accName;
            this.chrName = chrName;
            timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[IP: ").append(ip).append("] [").append(accId).append('|').append(accName).append('|').append(chrName).append("] [Time: ").append(timestamp).append(']');
            sb.append(nl);
            sb.append("[Op: ").append(op.toString()).append(']');
            sb.append(" [Data: ").append(packet.toString()).append(']');
            return sb.toString();
        }
    }

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            MapleServerHandler mbean = new MapleServerHandler();
            //The log is a static object, so we can just use this hacky method.
            mBeanServer.registerMBean(mbean, new ObjectName("handling:type=MapleServerHandler"));
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            FilePrinter.printError(FilePrinter.MapleServerHandler, e, "Error registering PacketLog MBean");
        }
    }

    @Override
    public void writeLog() {
        try {
            packetLogLock.readLock().lock();
            String newLine = System.getProperty("line.separator");
            for (LoggedPacket loggedPacket : packetLog) {
                FilePrinter.print(FilePrinter.PacketLogs, loggedPacket.toString());
            }
        } finally {
            packetLogLock.readLock().unlock();
        }
    }

    public MapleServerHandler() {
        //ONLY FOR THE MBEAN
    }

    public MapleServerHandler(final int channel, final boolean cs) {
        this.channel = channel;
        this.cs = cs;
    }

    @Override
    public void messageSent(final IoSession session, final Object message) throws Exception {
        final Runnable r = ((MaplePacket) message).getOnSend();
        if (r != null) {
            r.run();
        }
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            client.disconnect(false, cs);
        }
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        // Start of IP checking
        final String address = session.getRemoteAddress().toString().split(":")[0];

        if (BlockedIP.contains(address)) {
            session.close(true);
            return;
        }
        final Pair<Long, Byte> track = tracker.get(address);

        byte count;
        if (track == null) {
            count = 1;
        } else {
            count = track.right;

            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000) { // Less than 2 sec
                count++;
            } else if (difference > 20000) { // Over 20 sec
                count = 1;
            }
            if (count >= 10) {
                BlockedIP.add(address);
                tracker.remove(address); // Cleanup
                session.close();
                return;
            }
        }
        tracker.put(address, new Pair<>(System.currentTimeMillis(), count));
        // End of IP checking.

        if (channel > -1) {
            if (ChannelServer.getInstance(channel).isShutdown()) {
                session.close(true);
                return;
            }
        } else if (cs) {
            if (CashShopServer.isShutdown()) {
                session.close(true);
                return;
            }
        } else {
            if (LoginServer.isShutdown()) {
                session.close(true);
                return;
            }
        }
        
        byte key[] = {0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00, 0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00};
        byte ivRecv[] = {70, 114, 122, 82};
        byte ivSend[] = {82, 48, 120, 115};
        ivRecv[3] = (byte) (Math.random() * 255);
        ivSend[3] = (byte) (Math.random() * 255);

        final MapleClient client = new MapleClient(
                new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION)), // Sent Cypher
                new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION), // Recv Cypher
                session);

        client.setChannel(channel);

        session.write(LoginPacket.getHello(ServerConstants.MAPLE_VERSION, ivSend, ivRecv));
        session.setAttribute(MapleClient.CLIENT_KEY, client);

        StringBuilder sb = new StringBuilder();
        if (channel > -1) {
            sb.append("[Channel Server] Channel ").append(channel).append(" : ");
        } else if (cs) {
            sb.append("[Cash Server]");
        } else {
            sb.append("[Login Server]");
        }
        sb.append("IoSession opened ").append(address);
        //System.out.println(sb.toString());

        FileWriter fw = isLoggedIP(session);
        if (fw != null) {
            if (channel > -1) {
                fw.write("=== Logged Into Channel " + channel + " ===");
                fw.write(nl);
            } else if (cs) {
                fw.write("=== Logged Into CashShop Server ===");
                fw.write(nl);
            } else {
                fw.write("=== Logged Into Login Server ===");
                fw.write(nl);
            }
            fw.flush();
        }
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null && client.getPlayer() != null) {
            client.getPlayer().saveToDB(true, cs);
        }

        if (client != null) {
           
            try {
                FileWriter fw = isLoggedIP(session);
                if (fw != null) {
                    fw.write("=== Session Closed ===");
                    fw.write(nl);
                    fw.flush();
                }
                client.disconnect(true, cs);
            } finally {
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        DatabaseConnection.close();
     
        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {
        try {
            final SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
            if (slea.available() < 2) {
                return;
            }
            final short header_num = slea.readShort();
            // Console output part

            for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
                if (recv.getValue() == header_num) {

                    if (debugMode) {
                        final StringBuilder sb = new StringBuilder("Received data 已處理 :" + String.valueOf(recv) + "\n");
                        sb.append(tools.HexTool.toString((byte[]) message)).append("\n").append(tools.HexTool.toStringFromAscii((byte[]) message));
                        System.out.println(sb.toString());
                    }
                    final MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
                    if (!c.isReceiving()) {
                        return;
                    }
                    if (recv.NeedsChecking()) {
                        if (!c.isLoggedIn()) {
                            return;
                        }
                    }
                    if (c.getPlayer() != null && c.isMonitored()) {
                        if (!blocked.contains(recv)) {
                            FilePrinter.print("UnHandledPacketLogs.txt", String.valueOf(recv) + " (" + Integer.toHexString(header_num) + ") Handled: \r\n" + slea.toString() + "\r\n");
                        }
                    }
                    if (isLogPackets) {
                        log(slea, recv, c, session);
                    }
                    handlePacket(recv, slea, c, cs);

                    //Log after the packet is handle. You'll see why =]
                    FileWriter fw = isLoggedIP(session);
                    if (fw != null && !blocked.contains(recv)) {
                        if (recv == RecvPacketOpcode.PLAYER_LOGGEDIN) { // << This is why. Win.
                            fw.write(">> [AccountName: "
                                    + (c.getAccountName() == null ? "null" : c.getAccountName())
                                    + "] | [IGN: "
                                    + (c.getPlayer() == null || c.getPlayer().getName() == null ? "null" : c.getPlayer().getName())
                                    + "] | [Time: "
                                    + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())
                                    + "]");
                            fw.write(nl);
                        }
                        fw.write("[" + recv.toString() + "]" + slea.toString(true));
                        fw.write(nl);
                        fw.flush();
                    }
                    return;
                }
            }
            if (debugMode) {
                final StringBuilder sb = new StringBuilder("Received data 未處理 : ");
                sb.append(tools.HexTool.toString((byte[]) message)).append("\n").append(tools.HexTool.toStringFromAscii((byte[]) message));
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            FilePrinter.printError("PacketException.txt", e);
        }

    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (client != null && client.getPlayer() != null) {
            System.out.println("玩家 " + client.getPlayer().getName() + " 正在掛網");
        }
        if (client != null) {
            client.sendPing();
        } else {
            session.close(true);
            return;
        }
        super.sessionIdle(session, status);
    }

    public static final void handlePacket(final RecvPacketOpcode header, final SeekableLittleEndianAccessor slea, final MapleClient c, final boolean cs) throws Exception {
        switch (header) {
            case PONG:
                c.pongReceived();
                break;
            case STRANGE_DATA:
                // Does nothing for now, HackShield's heartbeat
                break;
            case HELLO_LOGIN:
                if(slea.available() >= 5) {
                    FilePrinter.print("38Logs.txt", slea.toString(), true);
                }
                CharLoginHandler.Welcome(c);
                // Does nothing for now, HackShield's heartbeat
                break;
            case HELLO_CHANNEL:
                CharLoginHandler.Welcome(c);
                break;
            case LOGIN_PASSWORD:
                CharLoginHandler.login(slea, c);
                break;
            case SERVERLIST_REQUEST:
                CharLoginHandler.ServerListRequest(c);
                break;
            case CHARLIST_REQUEST:
                CharLoginHandler.CharlistRequest(slea, c);
                break;
            case SERVERSTATUS_REQUEST:
                CharLoginHandler.ServerStatusRequest(c);
                break;
            case CHECK_CHAR_NAME:
                CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), c);
                break;
            case CREATE_CHAR:
                CharLoginHandler.CreateChar(slea, c);
                break;
            case DELETE_CHAR:
                CharLoginHandler.DeleteChar(slea, c);
                break;
            case CHAR_SELECT:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c);
                break;
            case SET_GENDER:
                CharLoginHandler.SetGenderRequest(slea, c);
                break;
            // END OF LOGIN SERVER
            case CHANGE_CHANNEL:
                InterServerHandler.ChangeChannel(slea, c, c.getPlayer());
                break;
            case PLAYER_LOGGEDIN:
                final int playerid = slea.readInt();
                if (cs) {
                    CashShopOperation.EnterCashShop(playerid, c);
                } else {
                    InterServerHandler.Loggedin(playerid, c);
                }
                break;
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(c, c.getPlayer(), false);
                break;
            case ENTER_MTS:
                InterServerHandler.EnterCS(c, c.getPlayer(), true);
                break;
            case MOVE_PLAYER:
                PlayerHandler.MovePlayer(slea, c, c.getPlayer());
                break;
            case CHAR_INFO_REQUEST:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.CharInfoRequest(slea.readInt(), c, c.getPlayer());
                //System.err.println("CHAR_INFO_REQUEST");
                break;
            case CLOSE_RANGE_ATTACK:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), false);
                break;
            case RANGED_ATTACK:
                PlayerHandler.rangedAttack(slea, c, c.getPlayer());
                break;
            case MAGIC_ATTACK:
                PlayerHandler.MagicDamage(slea, c, c.getPlayer());
                break;
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(slea, c, c.getPlayer());
                break;
            case PASSIVE_ENERGY:
                PlayerHandler.closeRangeAttack(slea, c, c.getPlayer(), true);
                break;
            case FACE_EXPRESSION:
                PlayerHandler.ChangeEmotion(slea.readInt(), c.getPlayer());
                break;
            case TAKE_DAMAGE:
                PlayerHandler.TakeDamage(slea, c, c.getPlayer());
                break;
            case HEAL_OVER_TIME:
                PlayerHandler.Heal(slea, c.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(slea.readInt(), c.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(slea.readInt(), c.getPlayer());
                break;
            case USE_CHAIR:
                PlayerHandler.UseChair(slea.readInt(), c, c.getPlayer());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.CancelChair(slea.readShort(), c, c.getPlayer());
                break;
            case USE_ITEMEFFECT:
            case WHEEL_OF_FORTUNE:
                PlayerHandler.UseItemEffect(slea.readInt(), c, c.getPlayer());
                break;
            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            case MESO_DROP:
                c.getPlayer().updateTick(slea.readInt());
                PlayerHandler.DropMeso(slea.readInt(), c.getPlayer());
                break;
            case MONSTER_BOOK_COVER:
                PlayerHandler.ChangeMonsterBookCover(slea.readInt(), c, c.getPlayer());
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(slea, c.getPlayer());
                break;
            case CHANGE_MAP:
                if (cs) {
                    CashShopOperation.LeaveCashShop(slea, c, c.getPlayer());
                } else {
                    PlayerHandler.ChangeMap(slea, c, c.getPlayer());
                }
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), c, c.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, c, c.getPlayer());
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(slea, c, c.getPlayer());
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(c, c.getPlayer());
                break;
            case SKILL_MACRO:
                PlayerHandler.ChangeSkillMacro(slea, c.getPlayer());
                break;
            case GIVE_FAME:
                PlayersHandler.GiveFame(slea, c, c.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(slea, c.getPlayer());
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(slea, c);
                break;
            case TOUCH_REACTOR:
                PlayersHandler.TouchReactor(slea, c);
                break;
            case CLOSE_CHALKBOARD:
                c.getPlayer().setChalkboard(null);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case ITEM_SORT:
                InventoryHandler.ItemSort(slea, c);
                break;
            case ITEM_GATHER:
                InventoryHandler.ItemGather(slea, c);
                break;
            case ITEM_MOVE:
                InventoryHandler.ItemMove(slea, c);
                break;
            case ITEM_PICKUP:
                InventoryHandler.PlayerPickup(slea, c, c.getPlayer());
                break;
            case USE_CASH_ITEM:
                InventoryHandler.UseCashItem(slea, c);
                break;
            case USE_ITEM:
                InventoryHandler.UseItem(slea, c, c.getPlayer());
                break;
            case USE_MAGNIFY_GLASS:
                InventoryHandler.UseMagnify(slea, c);
                break;
            case USE_SCRIPTED_NPC_ITEM:
                InventoryHandler.UseScriptedNPCItem(slea, c, c.getPlayer());
                break;
            case USE_RETURN_SCROLL:
                InventoryHandler.UseReturnScroll(slea, c, c.getPlayer());
                break;
            case USE_UPGRADE_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte) slea.readShort(), (byte) slea.readShort(), (byte) slea.readShort(), c, c.getPlayer());
                break;
            case USE_POTENTIAL_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte) slea.readShort(), (byte) slea.readShort(), (byte) 0, c, c.getPlayer());
                break;
            case USE_EQUIP_SCROLL:
                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseUpgradeScroll((byte) slea.readShort(), (byte) slea.readShort(), (byte) 0, c, c.getPlayer());
                break;
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(slea, c, c.getPlayer());
                break;
            case USE_TREASUER_CHEST:
                InventoryHandler.UseTreasureChest(slea, c, c.getPlayer());
                break;
            case USE_SKILL_BOOK:
//                c.getPlayer().updateTick(slea.readInt());
                InventoryHandler.UseSkillBook(slea, c, c.getPlayer());
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(slea, c, c.getPlayer());
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(slea, c, c.getPlayer());
                break;
            case REWARD_ITEM:
                InventoryHandler.UseRewardItem((byte) slea.readShort(), slea.readInt(), c, c.getPlayer());
                break;
            case HYPNOTIZE_DMG:
                MobHandler.HypnotizeDmg(slea, c.getPlayer());
                break;
            case MOB_NODE:
                MobHandler.MobNode(slea, c.getPlayer());
                break;
            case DISPLAY_NODE:
                MobHandler.DisplayNode(slea, c.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.MoveMonster(slea, c, c.getPlayer());
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(slea.readInt(), c.getPlayer());
                break;
            case FRIENDLY_DAMAGE:
                MobHandler.FriendlyDamage(slea, c.getPlayer());
                break;
            case MONSTER_BOMB:
                MobHandler.MonsterBomb(slea.readInt(), c.getPlayer());
                break;
            case NPC_SHOP:
                NPCHandler.NPCShop(slea, c, c.getPlayer());
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(slea, c, c.getPlayer());
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(slea, c);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(slea, c);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(slea, c, c.getPlayer());
                break;
            case STORAGE:
                NPCHandler.Storage(slea, c, c.getPlayer());
                break;
            case GENERAL_CHAT:
//                c.getPlayer().updateTick(slea.readInt());
                ChatHandler.GeneralChat(slea.readMapleAsciiString(), slea.readByte(), c, c.getPlayer());
                break;
            case PARTYCHAT:
                ChatHandler.Others(slea, c, c.getPlayer());
                break;
            case WHISPER:
                ChatHandler.Whisper_Find(slea, c);
                break;
            case MESSENGER:
                ChatHandler.Messenger(slea, c);
                break;
            case AUTO_ASSIGN_AP:
                StatsHandling.AutoAssignAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(slea, c, c.getPlayer());
                break;
            case DISTRIBUTE_SP:
                c.getPlayer().updateTick(slea.readInt());
                StatsHandling.DistributeSP(slea.readInt(), c, c.getPlayer());
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.PlayerInteraction(slea, c, c.getPlayer());
                break;
            case GUILD_OPERATION:
                GuildHandler.Guild(slea, c);
                break;
            case UPDATE_CHAR_INFO:
                //System.err.println("UPDATE_CHAR_INFO");
                //PlayersHandler.UpdateCharInfo(slea.readAsciiString(), c);
                PlayersHandler.UpdateCharInfo(slea, c, c.getPlayer());
                break;
            case DENY_GUILD_REQUEST:
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), c);
                break;
            case ALLIANCE_OPERATION:
                AllianceHandler.HandleAlliance(slea, c, false);
                break;
            case DENY_ALLIANCE_REQUEST:
                AllianceHandler.HandleAlliance(slea, c, true);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperatopn(slea, c);
                break;
            case PARTY_OPERATION:
                PartyHandler.PartyOperatopn(slea, c);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.DenyPartyRequest(slea, c);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperationHandler(slea, c);
                break;
            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            case CASHSHOP_OPERATION:
                CashShopOperation.BuyCashItem(slea, c, c.getPlayer());
                break;
            case COUPON_CODE:
                slea.skip(2);
                CashShopOperation.CouponCode(slea.readMapleAsciiString(), c);
                break;
            case CS_UPDATE:
                CashShopOperation.sendCashShopUpdate(c);
                break;
            case TOUCHING_MTS:
                MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
                break;
            case MTS_TAB:
                MTSOperation.MTSOperation(slea, c);
                break;
            case DAMAGE_SUMMON:
                slea.skip(4);
                SummonHandler.DamageSummon(slea, c.getPlayer());
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(slea, c.getPlayer());
                break;
            case SUMMON_ATTACK:
                SummonHandler.SummonAttack(slea, c, c.getPlayer());
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(slea, c, c.getPlayer());
                break;
            case MOVE_PET:
                PetHandler.MovePet(slea, c.getPlayer());
                break;
            case PET_CHAT:
                if (slea.available() < 12) {
                    break;
                }
                PetHandler.PetChat((int) slea.readLong(), slea.readShort(), slea.readMapleAsciiString(), c.getPlayer());
                break;
            case PET_COMMAND:
                PetHandler.PetCommand(slea, c, c.getPlayer());
                break;
            case PET_FOOD:
                PetHandler.PetFood(slea, c, c.getPlayer());
                break;
            case PET_LOOT:
                InventoryHandler.PetPickup(slea, c, c.getPlayer());
                break;
            case PET_AUTO_POT:
                PetHandler.Pet_AutoPotion(slea, c, c.getPlayer());
                break;
            case MONSTER_CARNIVAL:
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            case DUEY_ACTION:
                DueyHandler.DueyOperation(slea, c);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(slea, c);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;
            case CANCEL_DEBUFF:
                // Ignore for now
                break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, c);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, c);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, c);
                break;
            case REPAIR:
                NPCHandler.repair(slea, c);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(c);
                break;
            case GAME_POLL:
                UserInterfaceHandler.InGame_Poll(slea, c);
                break;
            case OWL:
                InventoryHandler.Owl(slea, c);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, c);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, c);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, c);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, c);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, c);
                break;
            case FOLLOW_REQUEST:
                PlayersHandler.FollowRequest(slea, c);
                break;
            case FOLLOW_REPLY:
                PlayersHandler.FollowReply(slea, c);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, c);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, c);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, c);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, c);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, c);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, c);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, c);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, c);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, c);
                break;
            default:
                System.out.println("[UNHANDLED] Recv [" + header.toString() + "] found");
                break;
        }
    }
}
