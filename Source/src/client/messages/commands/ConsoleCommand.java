/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.ShutdownServer;
import server.Timer;
import tools.MaplePacketCreator;
import tools.StringUtil;

/**
 *
 * @author Flower
 */
public class ConsoleCommand {

    public static class Info extends ConsoleCommandExecute {

        @Override
        public int execute(String[] paramArrayOfString) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            Runtime runtime = Runtime.getRuntime();

            NumberFormat format = NumberFormat.getInstance();

            StringBuilder sb = new StringBuilder();
            Long maxMemory = runtime.maxMemory();
            Long allocatedMemory = runtime.totalMemory();
            Long freeMemory = runtime.freeMemory();
            System.out.println("------------------ 系統資訊 ------------------");
            System.out.println("線程數 :" + ((Integer) threadSet.size()).toString());
            System.out.println("SQL連接數 :" + ((Integer) DatabaseConnection.getConnectionsCount()).toString());
            System.out.println("記憶體最大限制 :" + maxMemory.toString());
            System.out.println("已申請記憶體 :" + allocatedMemory.toString());
            System.out.println("尚未使用記憶體 :" + freeMemory.toString());

            return 1;
        }

    }

    public static class Shutdown extends ConsoleCommandExecute {

        private static Thread t = null;

        @Override
        public int execute(String[] splitted) {
            System.out.println("執行關閉作業");
            for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
                cserv.closeAllMerchant();
            }
            System.out.println("精靈商人儲存完畢.");
            System.out.println("伺服器關閉中...");
            if (t == null || t.isAlive()) {
                try {
                    t = new Thread(server.ShutdownServer.getInstance());
                    t.start();

                } catch (Exception ex) {
                    Logger.getLogger(ConsoleCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("已在執行中...");
            }
            return 1;

        }
    }

    public static class ShutdownTime extends ConsoleCommandExecute {

        private int minutesLeft = 0;
        private static Thread t = null;
        private static ScheduledFuture<?> ts = null;

        public int execute(String[] splitted) {
            if (splitted.length > 1) {
                minutesLeft = Integer.parseInt(splitted[1]);
                if (ts == null && (t == null || !t.isAlive())) {
                    t = new Thread(server.ShutdownServer.getInstance());
                    ts = Timer.EventTimer.getInstance().register(new Runnable() {
                        @Override
                        public void run() {
                            if (minutesLeft == 1) {
                                for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
                                    cserv.closeAllMerchant();
                                }
                                System.out.println("精靈商人儲存完畢.");
                            } else if (minutesLeft == 0) {
                                ShutdownServer.getInstance().run();
                                t.start();
                                ts.cancel(false);
                                return;
                            }
                            World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "[楓之谷公告] 伺服器將在 " + minutesLeft + "分鐘後關閉. 請盡速關閉精靈商人 並下線.").getBytes());;
                            System.out.println("本伺服器將在 " + minutesLeft + "分鐘後關閉.");
                            minutesLeft--;
                        }
                    }, 60000);
                } else {
                    System.out.println("好吧真拿你沒辦法..伺服器關閉時間修改...請等待關閉完畢..請勿強制關閉服務器..否則後果自負!");
                }
            } else {
                System.out.println("使用規則: shutdowntime <關閉時間>");
                return 0;
            }
            return 1;
        }
    }

    public static class Dodown extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            try {
                com.mysql.jdbc.Connection dcon = (com.mysql.jdbc.Connection) DatabaseConnection.getConnection();
                com.mysql.jdbc.PreparedStatement ps = (com.mysql.jdbc.PreparedStatement) dcon.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE loggedin = 1");
                ps.executeUpdate();
                ps.close();
                System.out.println("所有帳號解卡完畢");
            } catch (SQLException ex) {
                System.out.println("解卡異常請查看MYSQL");
            }
            return 1;
        }
    }

    public static class ExpRate extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 2) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setExpRate(rate);
                    }
                } else {
                    int channel = Integer.parseInt(splitted[2]);
                    ChannelServer.getInstance(channel).setExpRate(rate);
                }
                System.out.println("經驗備率已改為 " + rate + "x");
            } else {
                System.out.println("Syntax: exprate <number> [<channel>/all]");
            }
            return 1;
        }
    }

    public static class DropRate extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 2) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted.length > 2 && splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setDropRate(rate);
                    }
                } else {
                    int channel = Integer.parseInt(splitted[2]);
                    ChannelServer.getInstance(channel).setDropRate(rate);
                }
                System.out.println("掉落備率已改為 " + rate + "x");
            } else {
                System.out.println("Syntax: droprate <number> [<channel>/all]");
            }
            return 1;
        }
    }

    public static class MesoRate extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 2) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setMesoRate(rate);
                    }
                } else {
                    int channel = Integer.parseInt(splitted[2]);
                    ChannelServer.getInstance(channel).setMesoRate(rate);
                }
                System.out.println("金錢備率已改為 " + rate + "x");
            } else {
                System.out.println("Syntax: mesorate <number> [<channel>/all]");
            }
            return 1;
        }
    }

    public static class CashRate extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 2) {
                final int rate = Integer.parseInt(splitted[1]);
                if (splitted[2].equalsIgnoreCase("all")) {
                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                        cserv.setCashRate(rate);
                    }
                } else {
                    int channel = Integer.parseInt(splitted[2]);
                    ChannelServer.getInstance(channel).setCashRate(rate);
                }
                System.out.println("現金備率已改為 " + rate + "x");
            } else {
                System.out.println("Syntax: cashrate <number> [<channel>/all]");
            }
            return 1;
        }
    }

    public static class Saveall extends ConsoleCommandExecute {

        private int p = 0;

        @Override
        public int execute(String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                List<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharactersThreadSafe();
                for (MapleCharacter chr : chrs) {
                    p++;
                    chr.saveToDB(false, true);
                 
                }
            }
            System.out.println("[保存] " + p + "個玩家數據保存到數據中.");
            return 1;
        }
    }

    public static class serverMsg extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                for(ChannelServer ch : ChannelServer.getAllInstances())
                    ch.setServerMessage(sb.toString());
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverMessage(sb.toString()).getBytes());
            } else {
                System.out.println("指令規則: !serverMsg <message>");
                return 0;
            }
            return 1;
        }
    }

    public static class ReloadChannel extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {

            if (splitted[1].equalsIgnoreCase("all")) {
                for (ChannelServer csrv : ChannelServer.getAllInstances()) {
                    csrv.closeAllMerchant();
                    csrv.shutdown();
                }
                ChannelServer.startAllChannels();
                System.out.println("所有頻道重新讀取成功");
            } else {
                try {
                    final int channel = Integer.parseInt(splitted[1]);
                    ChannelServer csrv = ChannelServer.getInstance(channel);
                    csrv.closeAllMerchant();
                    ChannelServer.startChannel(channel);
                } catch (Exception e) {
                    System.out.println("[指令用法] reloadChannle <頻道/all>");
                }
            }

            return 1;

        }

    }

    public static class ReloadMap extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            try {
                final int mapId = Integer.parseInt(splitted[1]);

                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    if (cserv.getMapFactory().isMapLoaded(mapId) && cserv.getMapFactory().getMap(mapId).getCharactersSize() > 0) {
                        System.out.println("該地圖還有人唷");
                        return 0;
                    }
                }
                for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                    if (cserv.getMapFactory().isMapLoaded(mapId)) {
                        cserv.getMapFactory().removeMap(mapId);
                    }
                }
            } catch (Exception e) {
                System.out.println("[指令用法] reloadMap <地圖ID>");
            }
            return 1;
        }
    }

    public static class help extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            System.out.println("╭〝☆指令列表〞★╮");
            System.out.println("-------------------------");
            System.out.println("exprate  經驗倍率");
            System.out.println("droprate 掉寶倍率");
            System.out.println("mesorate 金錢倍率");
            System.out.println("cashrate 點數倍率");
            System.out.println("-------------------------");
            System.out.println("shutdown 關閉伺服器");
            System.out.println("shotdowntime <時間> 倒數關閉服務器");
            System.out.println("reloadchannel 重新載入頻道");
            System.out.println("reloadmap 重新載入地圖");
            System.out.println("-------------------------");
            System.out.println("online 線上玩家");
            System.out.println("say 伺服器說話");
            System.out.println("dodown 解除所有卡帳");
            System.out.println("saveall 全服存檔");
            System.out.println("-------------------------");
            System.out.println("╰〝★指令列表〞╯");
            return 1;
        }
    }

    public static class Online extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            for (ChannelServer cserv : ChannelServer.getAllInstances()) {
                System.out.println("線上玩家： " + String.valueOf(cserv.getChannel()) + ":");
                System.out.println(cserv.getPlayerStorage().getOnlinePlayers(true));
            }
            return 1;
        }
    }

    public static class Say extends ConsoleCommandExecute {

        @Override
        public int execute(String[] splitted) {
            if (splitted.length > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("[伺服器公告] ");
                sb.append(StringUtil.joinStringFrom(splitted, 1));
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
            } else {
                System.out.println("指令規則: say <message>");
                return 0;
            }
            return 1;
        }
    }
}
