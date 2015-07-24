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
package handling.login;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import handling.MapleServerHandler;
import handling.mina.MapleCodecFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import server.ServerProperties;
import tools.FilePrinter;

public class LoginServer {

    public static final short PORT = 8484;
    private static InetSocketAddress InetSocketadd;
    private static IoAcceptor acceptor;
    private static Map<Integer, Integer> load = new HashMap<>();
    private static String serverName, eventMessage;
    private static byte flag;
    private static int maxCharacters, userLimit, usersOn = 0;
    private static boolean finishedShutdown = true, adminOnly = false;
    public static boolean autoRegister = false;

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static final void run_startup_configurations() {
        try {
            userLimit = Integer.parseInt(ServerProperties.getProperty("server.settings.userlimit"));
            serverName = ServerProperties.getProperty("server.settings.serverName");
            eventMessage = ServerProperties.getProperty("server.settings.eventMessage");
            flag = Byte.parseByte(ServerProperties.getProperty("server.settings.flag"));
            adminOnly = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.admin", "false"));
            maxCharacters = Integer.parseInt(ServerProperties.getProperty("server.settings.maxCharacters", "3"));
            autoRegister = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.autoRegister", "false"));
            IoBuffer.setUseDirectBuffer(false);
            IoBuffer.setAllocator(new SimpleBufferAllocator());

            acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast("codec", (IoFilter) new ProtocolCodecFilter(new MapleCodecFactory()));

            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
            acceptor.setHandler(new MapleServerHandler(-1, false));
            InetSocketadd = new InetSocketAddress("10.211.55.2",PORT);
            acceptor.bind(InetSocketadd);
            System.out.println("登入伺服器端口: " + Short.toString(PORT) + " \n\n");

        } catch (IOException ex) {
            FilePrinter.printError(FilePrinter.LoginServer, ex, "IOException");
        }

    }

    public static final void shutdown() {
        if (finishedShutdown) {
            System.out.println("登入伺服器已經關閉了...無法執行此動作");
            return;
        }
        System.out.println("登入伺服器關閉中...");
        acceptor.unbind();
        System.out.println("登入伺服器關閉完畢...");
        finishedShutdown = true; //nothing. lol
    }

    public static final String getServerName() {
        return serverName;
    }

    public static final String getEventMessage() {
        return eventMessage;
    }

    public static final byte getFlag() {
        return flag;
    }

    public static final int getMaxCharacters() {
        return maxCharacters;
    }

    public static final Map<Integer, Integer> getLoad() {
        return load;
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final void setEventMessage(final String newMessage) {
        eventMessage = newMessage;
    }

    public static final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public static final int getUserLimit() {
        return userLimit;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

    public static final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    public static final int getNumberOfSessions() {
        return acceptor.getManagedSessions().size();
    }

    public static final boolean isAdminOnly() {
        return adminOnly;
    }

    public static final boolean isShutdown() {
        return finishedShutdown;
    }

    public static final void setOn() {
        finishedShutdown = false;
    }
}
