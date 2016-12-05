/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.anticheat;

import client.MapleClient;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Randomizer;
import tools.FilePrinter;
import tools.MaplePacketCreator;

/**
 *
 * @author Weber
 */
public class FixLoginManager {

    private static class FixLoginEntry {

        public int accountId;
        public String password;
        public String socketAddr;
        public long lastupdate;

        public FixLoginEntry(int accountId, String password, String socketAddr, long lastupdate) {
            this.accountId = accountId;
            this.password = password;
            this.socketAddr = socketAddr;
            this.lastupdate = -lastupdate;
        }
    }

    private Map<Integer, FixLoginEntry> passwords;

    private ReentrantLock lock;

    private static FixLoginManager instance;

    public static FixLoginManager getInstance() {
        if (instance == null) {
            instance = new FixLoginManager();
        }

        return instance;
    }

    public FixLoginManager() {
        this.passwords = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public boolean hasPassword(MapleClient client) {
        boolean ret = false;
        this.lock.lock();
        try {
            if (this.passwords.containsKey(client.getAccID())) {
                ret = true;
            } else {
                ret = false;
            }
        } finally {
            this.lock.unlock();
        }
        return ret;
    }

    public String getNextPassword(MapleClient client) {
        String nextPass = "";
        this.lock.lock();
        try {
            if (this.passwords.containsKey(client.getAccID())) {
                // 處理存在
                long period = System.currentTimeMillis() - this.passwords.get(client.getAccID()).lastupdate;
                if (period < 5 * 1000) {
                    client.sendPacket(MaplePacketCreator.getPopupMsg("請" + ((5000 - period) / 1000) + "再試"));
                    return "";
                }
                this.passwords.remove(client.getAccID());
            }
            int seed = 10000 + (Math.abs(Randomizer.nextInt()) % 80000);
            nextPass = String.valueOf(seed);
            this.passwords.put(client.getAccID(), new FixLoginEntry(client.getAccID(),
                    nextPass, client.getSession().getRemoteAddress().toString(), System.currentTimeMillis()));
        } finally {
            this.lock.unlock();
        }
        return nextPass;
    }

    public boolean checkFixLoginPassword(MapleClient client, String password) {
        boolean ret = false;
        this.lock.lock();
        try {
            if (this.passwords.containsKey(client.getAccID())) {
                FixLoginEntry entry = this.passwords.get(client.getAccID());
                if (entry.socketAddr.equals(client.getSession().getRemoteAddress().toString())) {
                    if (password.equals(entry.password)) {
                        ret = true;
                        this.passwords.remove(client.getAccID());
                    } else {
                        ret = false;
                    }
                } else {
                    // 用A使用B的產生的解卡密碼，(洗BUG)??
                    FilePrinter.printError("DoubleLogin.txt", "帳號:" + client.getAccountName());
                    this.passwords.remove(client.getAccID());
                    ret = false;
                }
            } else {
                ret = false;
            }
        } finally {
            this.lock.unlock();
        }
        return ret;
    }
}
