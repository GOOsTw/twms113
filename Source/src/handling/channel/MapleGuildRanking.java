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
package handling.channel;

import java.util.List;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;

public class MapleGuildRanking {

    private static MapleGuildRanking instance = new MapleGuildRanking();
    private List<GuildRankingInfo> ranks = new LinkedList<GuildRankingInfo>();
    private List<levelRankingInfo> ranks1 = new LinkedList<levelRankingInfo>();
    private List<mesoRankingInfo> ranks2 = new LinkedList<mesoRankingInfo>();
    
    public static MapleGuildRanking getInstance() {
        return instance;
    }

    public List<GuildRankingInfo> getRank() {
        if (ranks.isEmpty()) {
            reload();
        }
        return ranks;
    }
    
    public List<levelRankingInfo> getRank1() {
        if (ranks1.isEmpty()) {
            showlvl();
        }
        return ranks1;
    }
    
    public List<mesoRankingInfo> getRank2() {
        if (ranks2.isEmpty()) {
            showmeso();
        }
        return ranks2;
    }
    
    private void reload() {
        ranks.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 50");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final GuildRankingInfo rank = new GuildRankingInfo(
                        rs.getString("name"),
                        rs.getInt("GP"),
                        rs.getInt("logo"),
                        rs.getInt("logoColor"),
                        rs.getInt("logoBG"),
                        rs.getInt("logoBGColor"));

                ranks.add(rank);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error handling guildRanking");
            e.printStackTrace();
        }
    }
    private void showlvl() {
        ranks1.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 ORDER BY `level` DESC LIMIT 20");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final levelRankingInfo rank1 = new levelRankingInfo (
                        rs.getString("name"),
                        rs.getInt("level"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                        ranks1.add(rank1);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("未能顯示等級排行");
            e.printStackTrace();
        }
    }
    
     private void showmeso() {
        ranks2.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 ORDER BY `meso` DESC LIMIT 20");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final mesoRankingInfo rank2 = new mesoRankingInfo (
                        rs.getString("name"),
                        rs.getInt("meso"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                        ranks2.add(rank2);
            }
        
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("未能顯示楓幣排行");
            e.printStackTrace();
        }
    }   
   public static class mesoRankingInfo {
      private String name;
      private int meso, str, dex, intt, luk;
      
      public mesoRankingInfo(String name, int meso, int str, int dex, int intt, int luk) {
          this.name = name;
          this.meso = meso;
          this.str = str;
          this.dex =dex;
          this.intt = intt;
          this.luk = luk;
      }
      public String getName() {
          return name;
      }
      public int getmeso() {
          return meso;
      }
      public int getstr(){
          return str;
      }
      public int getdex(){
          return dex;
      }
      public int getintt(){
          return intt;
      }
      public int getluk(){
          return luk;
      }
   }
   public static class levelRankingInfo {

        private String name;
        private int level, str, dex, intt, luk;

        public levelRankingInfo(String name, int level, int str, int dex, int intt, int luk) {
            this.name = name;
            this.level = level;
            this.str = str;
            this.dex = dex;
            this.intt = intt;
            this.luk = luk;
        }
           public String getName() {
            return name;
           }
           public int getlevel(){
               return level;
           }
           public int getstr(){
               return str;
           }
           public int getdex(){
               return dex;
           }
           public int getintt(){
               return intt;
           }
           public int getluk(){
               return luk;
           }
   }
    public static class GuildRankingInfo {

        private String name;
        private int gp, logo, logocolor, logobg, logobgcolor;

        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
        }

        public String getName() {
            return name;
        }

        public int getGP() {
            return gp;
        }

        public int getLogo() {
            return logo;
        }

        public int getLogoColor() {
            return logocolor;
        }

        public int getLogoBg() {
            return logobg;
        }

        public int getLogoBgColor() {
            return logobgcolor;
        }
    }
}
