/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.inventory.MapleInventoryType;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;
import database.DatabaseConnection;

/**
 *
 * @author user
 */
public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{50100010, 50100010, 50100010, 50100010, 50100010};
    private boolean initialized = false;
    private final Map<Integer, List<Integer>> openBox = new HashMap();
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private final Map<Integer, List<CashItemInfo>> itemPackage = new HashMap<Integer, List<CashItemInfo>>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(ServerProperties.getProperty("server.wzpath")  + "/Etc.wz");

    public static final CashItemFactory getInstance() {
        return instance;
    }

    protected CashItemFactory() {
    }

    public void initialize() {
        
        System.out.println("【讀取中】 購物商城物品:::");
        
        final List<Integer> itemids = new ArrayList<>();
        for (MapleData field : data.getData("Commodity.img").getChildren()) {
            final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);

            final CashItemInfo stats = new CashItemInfo(itemId,
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0), SN,
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0);

            if (SN > 0) {
                itemStats.put(SN, stats);
            }

            if (itemId > 0) {
                itemids.add(itemId);
            }
        }
        for (int i : itemids) {
            getPackageItems(i);
        }
        for (int i : itemStats.keySet()) {
            getModInfo(i);
            getItem(i); //init the modinfo's citem
        }

        initialized = true;
    }

    public final CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(Integer.valueOf(sn));
        final CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        //hmm
        return stats;
    }

    public final Set<Integer> getAllItemSNs() {
        return itemStats.keySet();
    }

    public final List<CashItemInfo> getAllItems() {
        return new ArrayList<CashItemInfo>(itemStats.values());
    }

    public final List<CashItemInfo> getPackageItems(int itemId) {
        if (itemPackage.get(itemId) != null) {
            return itemPackage.get(itemId);
        }
        final List<CashItemInfo> packageItems = new ArrayList<CashItemInfo>();

        final MapleData b = data.getData("CashPackage.img");
        if (b == null || b.getChildByPath(itemId + "/SN") == null) {
            return null;
        }
        for (MapleData d : b.getChildByPath(itemId + "/SN").getChildren()) {
            packageItems.add(itemStats.get(Integer.valueOf(MapleDataTool.getIntConvert(d))));
        }
        itemPackage.put(itemId, packageItems);
        return packageItems;
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return this.openBox;
    }

    public final CashModInfo getModInfo(int sn) {
        CashModInfo ret = itemMods.get(sn);

        if (ret == null) {
            if (initialized) {
                return null;
            }
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                ps.setInt(1, sn);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    itemMods.put(sn, ret);

                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private void refreshAllModInfo() {
        itemMods.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer sn = rs.getInt("serial");
                CashModInfo ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                itemMods.put(sn, ret);

            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final Collection<CashModInfo> getAllModInfo() {
        if(itemMods.isEmpty())
            refreshAllModInfo();
        return itemMods.values();
    }

    public final int[] getBestItems() {
        return bestItems;
    }
}
