package server;

import client.SkillCollector;
import client.SkillFactory;
import client.messages.ConsoleCommandProcessor;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import static database.DatabaseConnection.CloseSQLConnections;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.world.family.MapleFamilyBuff;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.gashapon.GashaponFactory;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

public class Start {

    private static void resetAllLoginState() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("【錯誤】 請確認資料庫是否正確連接");
        }
    }

    private static void checkDatabase() throws Exception {
        try {
            Connection con = DatabaseConnection.getConnection();
            String dbname = "";

            PreparedStatement ps = con.prepareStatement("SELECT DATABASE() as dbname");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dbname = rs.getString("dbname");
            }
            rs.close();
            ps.close();

            String sql = "SELECT `AUTO_INCREMENT` as max_id \n"
                    + "FROM  INFORMATION_SCHEMA.TABLES\n"
                    + "WHERE TABLE_SCHEMA = ?\n"
                    + "AND   TABLE_NAME   = ?;";
            ps = con.prepareStatement(sql);
            ps.setString(1, dbname);

            String[] tables = {"queststatus", "questactions", "questinfo", "questrequirements", "queststatusmobs", "skillmacros", "skills", "skills_cooldowns","player_variables", "inventoryslot", "savedlocations", "achievements", "buddies", "trocklocations", "regrocklocations"};

            for (String s : tables) {
                ps.setString(2, s);
                rs = ps.executeQuery();
                if (rs.next()) {
                    int max = rs.getInt("max_id");
                    try {
                        if (max > (Integer.MAX_VALUE / 2)) {
                            throw new Exception("疑似資料庫索引值過大，請整理好再開服");
                        }
                    } catch (Exception e) {
                        throw new Exception("疑似資料庫索引值過大，請整理好再開服");
                    }
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("疑似資料庫索引值過大，請整理好再開服");
        }
    }

    public final static void main(final String args[]) throws Exception {

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        System.setProperty("file.encoding", "utf-8");

        System.out.println("【台版楓之谷模擬器】");
        System.out.println("【版本】 v113");
        System.out.println("【Build】 17092704");

        boolean adminMode = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.admin"));
        boolean autoReg = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.autoRegister"));
        boolean gmitems = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.gmitems"));

        if (adminMode) {
            System.out.println("【管理員模式】開啟");
        } else {
            System.out.println("【管理員模式】關閉");
        }

        if (autoReg) {
            System.out.println("【自動註冊】開啟");
        } else {
            System.out.println("【自動註冊】關閉");
        }

        if (gmitems) {
            System.out.println("【允許玩家使用管理員物品】開啟");
        } else {
            System.out.println("【允許玩家使用管理員物品】關閉");
        }

        resetAllLoginState();

        checkDatabase();

        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        BoatTimer.getInstance().start();
        PingTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        SkillCollector.getInstance().init();
        LoginInformationProvider.getInstance();
        FishingRewardFactory.getInstance();
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
        GashaponFactory.getInstance().reloadGashapons();
        //ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance();
        PredictCardFactory.getInstance().initialize();
        MapleGuildRanking.getInstance().getGuildRank();
        MapleFamilyBuff.getBuffEntry();
        MapleServerHandler.registerMBean();
        RankingWorker.getInstance().run();
        // MTSStorage.load();
        CashItemFactory.getInstance().initialize();
        LoginServer.setup();
        ChannelServer.startAllChannels();
        
        SendPacketOpcode.reloadValues();
        RecvPacketOpcode.reloadValues();

        System.out.println("【啟動中】 CashShop Items:::");
        CashShopServer.setup();

        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);

        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownServer.getInstance()));

        SpeedRunner.getInstance().loadSpeedRuns();

        World.registerRespawn();
        LoginServer.setOn();
        System.out.println("【伺服器開啟完畢】");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (!World.isShutDown) {
            try {
                System.out.print(">>");
                String cmd = br.readLine();
                if (cmd.equals("")) {
                    continue;
                }
                ConsoleCommandProcessor.processCommand(cmd);
            
            } catch (IOException ex) {
                //Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {

            }
        }
    }

}
