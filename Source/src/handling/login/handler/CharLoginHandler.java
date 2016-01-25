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
package handling.login.handler;

import java.util.List;
import java.util.Calendar;

import client.inventory.IItem;
import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;
import tools.KoreanDateUtil;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharLoginHandler {

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 5;
    }

    public static final void handleWelcome(final MapleClient c) {
        c.sendPing();
    }

    public static final void handleLogout(final SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getLoginState() > 0) {
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        }
    }

    public static final void handleLogin(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        if (slea.available() <= 4) {
            c.getSession().close(true);
            return;
        }

        final String account = slea.readMapleAsciiString();
        final String password = slea.readMapleAsciiString();

        c.setAccountName(account);
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        if (LoginServer.autoRegister) {
            if (AutoRegister.autoRegister && !AutoRegister.getAccountExists(account) && (!c.hasBannedIP() || !c.hasBannedMac())) {
                if (password.equalsIgnoreCase("fixlogged")) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "這個密碼是解卡密碼，請換其他密碼。"));
                    c.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                    return;
                }
                if (account.length() >= 12) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "您的帳號長度太長了唷!\r\n請重新輸入."));
                    c.getSession().write(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                    return;
                }
                AutoRegister.createAccount(account, password, c.getSession().getRemoteAddress().toString());
                if (AutoRegister.success && AutoRegister.ip) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "帳號創建成功,請重新登入!"));
                    c.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                    return;
                } else if (!AutoRegister.ip) {
                    c.sendPacket(MaplePacketCreator.serverNotice(1, "無法註冊過多的帳號密碼唷!"));
                    c.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                    AutoRegister.success = false;
                    AutoRegister.ip = true;
                    return;
                }
            }
        }

        int loginok = c.login(account, password, ipBan || macBan);
        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + account, false, 4, false);
            }
        } else if (loginok == 0 && (c.getGender() == 10 || c.getSecondPassword() == null)) {
            // 防止第一次按取消後卡角問題
            c.sendPacket(LoginPacket.getGenderNeeded(c));
            return;
        }

        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.sendPacket(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            }
        } else {

            /* Clear all connected client */
            boolean check = false;

            for (ChannelServer ch : ChannelServer.getAllInstances()) {
                List<MapleCharacter> list = ch.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : list) {
                    if (chr.getAccountID() == c.getAccID()) {
                        if (chr.getMap() != null) {
                            chr.getMap().removePlayer(chr);
                        }
                        ch.removePlayer(chr);
                        break;
                    }
                }
            }

            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }
    }

    public static final void SetGenderRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        if (c.getAccountName().equals(username)) {
            c.setGender(slea.readByte());
            c.setSecondPassword(password);
            c.update2ndPassword();
            c.updateGender();
            c.sendPacket(LoginPacket.getGenderChanged(c));
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN, c.getSessionIPAddress());
        } else {
            c.getSession().close();
            return;
        }
    }

    public static final void ServerListRequest(final MapleClient c) {
        c.sendPacket(LoginPacket.getServerList(0, LoginServer.getServerName(), LoginServer.getLoad()));
        //c.sendPacket(MaplePacketCreator.getServerList(1, "Scania", LoginServer.getInstance().getChannels(), 1200));
        //c.sendPacket(MaplePacketCreator.getServerList(2, "Scania", LoginServer.getInstance().getChannels(), 1200));
        //c.sendPacket(MaplePacketCreator.getServerList(3, "Scania", LoginServer.getInstance().getChannels(), 1200));
        c.sendPacket(LoginPacket.getEndOfServerList());
    }

    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();
        final int server = slea.readByte();
        final int channel = slea.readByte() + 1;
        final int userLimit = LoginServer.getUserLimit();

        c.setWorld(server);
        c.setChannel(channel);
        c.setWorld(server);

        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close(true);
        }
    }

    public static final void checkCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void handleCreateCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final String name = slea.readMapleAsciiString();
        final int JobType = slea.readInt(); // 1 = Adventurer, 0 = Cygnus, 2 = Aran

        final short db = 0; //whether dual blade = 1 or adventurer = 0
        final int face = slea.readInt();
        final int hair = slea.readInt();
        final int hairColor = 0;
        final byte skinColor = 0;
        final int top = slea.readInt();
        final int bottom = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getGender();

        if (gender == 0 && (JobType == 1 || JobType == 0)) {
            if (face != 20100 && face != 20401 && face != 20402) {
                return;
            }
            if (hair != 30030 && hair != 30027 && hair != 30000) {
                return;
            }
            if (top != 1040002 && top != 1040006 && top != 1040010) {
                return;
            }
            if (bottom != 1060002 && bottom != 1060006) {
                return;
            }
            if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
                return;
            }
            if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
                return;
            }

        } else if (gender == 1 && (JobType == 1 || JobType == 0)) {
            if (face != 21002 && face != 21700 && face != 21201) {
                return;
            }
            if (hair != 31002 && hair != 31047 && hair != 31057) {
                return;
            }
            if (top != 1041002 && top != 1041006 && top != 1041010 && top != 1041011) {
                return;
            }
            if (bottom != 1061002 && bottom != 1061008) {
                return;
            }
            if (shoes != 1072001 && shoes != 1072005 && shoes != 1072037 && shoes != 1072038) {
                return;
            }
            if (weapon != 1302000 && weapon != 1322005 && weapon != 1312004) {
                return;
            }

        } else if (JobType == 2) {

            if (gender == 0) {
                if (face != 20100 && face != 20401 && face != 20402) {
                    return;
                }
                if (hair != 30030 && hair != 30027 && hair != 30000) {
                    return;
                }
            } else if (gender == 1) {
                if (face != 21002 && face != 21700 && face != 21201) {
                    return;
                }
                if (hair != 31002 && hair != 31047 && hair != 31057) {
                    return;
                }
            }
            if (top != 1042167) {
                return;
            }
            if (bottom != 1062115) {
                return;
            }
            if (shoes != 1072383) {
                return;
            }
            if (weapon != 1442079) {
                return;
            }
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c, JobType);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);

        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        IItem item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        item = li.getEquipById(bottom);
        item.setPosition((byte) -6);
        equip.addFromDB(item);

        item = li.getEquipById(shoes);
        item.setPosition((byte) -7);
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) -11);
        equip.addFromDB(item);

        //blue/red pots
        switch (JobType) {
            case 0: // Cygnus
                newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh

                newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte) 1, null); //>_>_>_> ugh
                newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte) 1, null); //>_>_>_> ugh

                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case 1: // Adventurer
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case 2: // Aran
                newchar.setSkinColor((byte) 11);
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
            case 3: //Evan
                newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
                break;
        }

        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1 && db == 0);
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.sendPacket(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static final void handleDeleteCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();

        String _2ndPassword;
        _2ndPassword = slea.readMapleAsciiString();

        final int characterId = slea.readInt();
        if (!c.login_Auth(characterId)) {
            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
            return;
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (_2ndPassword == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.check2ndPassword(_2ndPassword)) { // Wrong Password
                    //state = 12;
                    state = 16;
                }
            }
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(characterId);
        }

        c.sendPacket(LoginPacket.deleteCharResponse(characterId, state));
    }

    public static final void handleSecectCharacter(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final int charId = slea.readInt();
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        byte[] ip = {127, 0, 0, 1};
        try {
            ip = InetAddress.getByName(ChannelServer.getInstance(c.getChannel()).getGatewayIP()).getAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CharLoginHandler.class.getName()).log(Level.SEVERE, "getIP Error", ex);
        }
        int port = ChannelServer.getInstance(c.getChannel()).getPort();
        c.sendPacket(MaplePacketCreator.getServerIP(ip, port, charId));
    }

}
