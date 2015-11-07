/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import tools.Pair;

/**
 *
 * @author Flower
 */
public class FishingRewardFactory {

    private final List<Pair<Integer, Integer>> rewards;
    private final List<Long> accumulated;
    private Long total = 0L;
    private final Random rand;
    private final int[] typesChance = {40, 40, 20};
    private final int[] typesChanceAcc = {40, 80, 100};
    private final int typesChanceTotal = 100;

    private static final FishingRewardFactory instance = new FishingRewardFactory();

    public FishingRewardFactory() {
        System.out.println("【讀取中】 FishingRewardFactory :::");
        this.rewards = new LinkedList<>();
        this.accumulated = new LinkedList<>();
        this.rand = new Random();
        this.loadItems();
    }

    public static FishingRewardFactory getInstance() {
        return instance;
    }

    public int getNextRewardType() {
        Integer n = rand.nextInt(this.typesChanceTotal);
        for (int i = 0; i < 3; i++) {
            if (n <= this.typesChanceAcc[i]) {
                return i;
            }
        }
        return 0;
    }

    public int getNextRewardItemId() {
        if (this.rewards.isEmpty()) {
            this.loadItems();
        }
        Iterator<Pair<Integer, Integer>> iterator = this.rewards.iterator();

        Long n = rand.nextLong() % total;
        while (iterator.hasNext()) {
            if (n <= iterator.next().right) {
                return iterator.next().left;
            }
        }
        return 0;
    }
    
    public void reloadItems() {
        this.loadItems();
    }

    private void loadItems() {
        Connection con = DatabaseConnection.getConnection();
        rewards.clear();
        Long acc = 0L;
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM fishing_rewards ORDER BY chance ASC"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int itemid = rs.getInt("itemid");
                    int chance = rs.getInt("chance");
                    rewards.add(new Pair<>(itemid, chance));
                    acc += chance;
                    accumulated.add(acc);
                }
            }
        } catch (SQLException e) {

        }
        total = acc;
    }

}
