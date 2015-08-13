/*
 此文件是 風迷.OD.TW 核心服務器 -<MapleStory Server>
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@MrCoffee.de>
 Jan Christian Meyer <vimes@MrCoffee.de>

 此文件原作者為德國  OdinMS 團隊  以上是開發人員聯繫訊息 本程序遵守
 版本首要發布  GNU 協議進行修改發布 你可以無偿使用此文件或者進行修改
 但是禁止使用本程序進行一切商業行為,如有發現 根據當地法律的制度 導致
 任何法律責任，我們將不予承擔 本程序發布是免費的 並不收取額外費用 擁有
 此文件副本的人遵守 GNU 規定 但請保留修改發布人的訊息 謝謝！
 ==============================================================
 當前版本修復製作維護人員: 風迷人物
 您應該已經收到一份拷貝的GNU通用公共許可證Affero程式一起。如果不是，請參閱
 <http://www.gnu.org/licenses/>.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerProperties;

/**
 * All OdinMS servers maintain a Database Connection. This class therefore
 * "singletonices" the connection per process.
 *
 *
 * @author Frz
 */
public class DatabaseConnection {

    private static final HashMap<Integer, ConWrapper> connections
            = new HashMap();
    private final static Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    private static String dbDriver = "", dbUrl = "", dbUser = "", dbPass = "";
    private static final long connectionTimeOut = 30 * 60 * 60 * 1000;
    private static final ReentrantLock lock = new ReentrantLock();// 锁对象

    public static int getConnectionsCount() {
        return connections.size();
    }

    public static void close() {
        try {
            Thread cThread = Thread.currentThread();
            Integer threadID = (int) cThread.getId();
            ConWrapper ret = connections.get(threadID);
            if (ret != null) {
                Connection c = ret.getConnection();
                if (!c.isClosed()) {
                    c.close();
                }
                lock.lock();
                try {
                    connections.remove(threadID);
                } finally {
                    lock.unlock();
                }
            }

        } catch (SQLException ex) {
        }
    }

    public static Connection getConnection() {

        if (!isInitialized()) {
            InitDB();
        }

        Thread cThread = Thread.currentThread();
        Integer threadID = (int) cThread.getId();
        ConWrapper ret;

        ret = connections.get(threadID);

        if (ret == null) {
            Connection retCon = connectToDB();
            ret = new ConWrapper(threadID, retCon);
            lock.lock();
            try {
                connections.put(threadID, ret);
            } finally {
                lock.unlock();
            }
        }

        Connection c = ret.getConnection();
        try {
            if (c.isClosed()) {
                Connection retCon = connectToDB();
                lock.lock();
                try {
                    connections.remove(threadID);
                    connections.put(threadID, ret);
                } finally {
                    lock.unlock();
                }
                ret = new ConWrapper(threadID, retCon);
            }
        } catch (Exception e) {

        } finally {

        }

        return ret.getConnection();
    }

    static class ConWrapper {

        private final int tid;
        private long lastAccessTime;
        private Connection connection;

        public ConWrapper(int tid, Connection con) {
            this.tid = tid;
            this.lastAccessTime = System.currentTimeMillis();
            this.connection = con;
        }

        public boolean close() {
            boolean ret = false;

            if (connection == null) {
                ret = false;
            } else {
                if (expiredConnection()) {
                    lock.lock();
                    try {
                        this.connection.close();
                        connections.remove(tid);
                        ret = true;
                    } catch (SQLException ex) {
                        ret = false;
                    } finally {
                        lock.unlock();
                    }
                }
            }
            return ret;
        }

        public Connection getConnection() {
            if (expiredConnection()) {
                try { // Assume that the connection is stale
                    connection.close();
                } catch (SQLException err) {
                }
                this.connection = connectToDB();
            }
            lastAccessTime = System.currentTimeMillis(); // Record Access
            return this.connection;
        }

        /**
         * Returns whether this connection has expired
         *
         * @return
         */
        public boolean expiredConnection() {
            return System.currentTimeMillis() - lastAccessTime >= connectionTimeOut;
        }
    }

    private static Connection connectToDB() {

        try {
            Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            return con;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static boolean isInitialized() {
        return !dbUser.equals("");
    }

    public static void InitDB() {

        dbDriver = "com.mysql.jdvc.Driver";
        String db = ServerProperties.getProperty("server.settings.db.name", "twms");
        String ip = ServerProperties.getProperty("server.settings.db.ip", "localhost");
        dbUrl = "jdbc:mysql://" + ip + ":3306/" + db + "?autoReconnect=true&characterEncoding=UTF8&?connectTimeout=120000000";
        dbUser = ServerProperties.getProperty("server.settings.db.user");
        dbPass = ServerProperties.getProperty("server.settings.db.password");
    }

    public static void closeTimeout() {
        int i = 0;
        lock.lock();
        List<Integer> keys = new ArrayList(connections.keySet());
        try {
            for (Integer tid : keys) {
                ConWrapper con = connections.get(tid);
                if (con.close()) {
                    i++;
                }
            }
        } finally {
            lock.unlock();
        }
        System.out.println("[Database] 已經關閉" + String.valueOf(i) + "/" + String.valueOf(connections.size()) + "個無用連線.");
    }

    public static void closeAll() {
        synchronized (connections) {
            for (ConWrapper con : connections.values()) {
                try {
                    con.connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public final static Runnable CloseSQLConnections = new Runnable() {

        @Override
        public void run() {
            DatabaseConnection.closeTimeout();
        }
    };

}
