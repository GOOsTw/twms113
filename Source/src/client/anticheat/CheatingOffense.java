package client.anticheat;

import static constants.ServerConstants.BANTYPE_DISABLE;
import static constants.ServerConstants.BANTYPE_ENABLE;

public enum CheatingOffense {


    召喚獸無延遲((byte) 5, 6000, 10, (byte) 2),
    攻擊速度過快_客戶端((byte) 5, 6000, 50, (byte) 2),
    攻擊速度過快_伺服器端((byte) 5, 9000, 20, (byte) 2),
    異常移動怪物((byte) 1, 30000),
    異常回復血魔((byte) 5, 20000, 10, (byte) 2),
    攻擊數值異常相同((byte) 2, 30000, 150),
    無敵((byte) 1, 30000, 1200, (byte) 0),
    魔法攻擊過高_1((byte) 5, 30000, -1, (byte) 0),
    魔法攻擊過高_2((byte) 10, 30000, -1, (byte) 0),
    攻擊過高_1((byte) 5, 30000, -1, (byte) 0),
    攻擊過高_2((byte) 10, 30000, -1, (byte) 0),
    EXCEED_DAMAGE_CAP((byte) 5, 60000, 800, (byte) 0),
    攻擊距離過遠((byte) 5, 60000, 1500), // NEEDS A SPECIAL FORMULAR!
    召喚獸攻擊距離過遠((byte) 5, 60000, 200),
    回復血量過高((byte) 1, 30000, 1000, (byte) 2),
    回復魔量過高((byte) 1, 30000, 1000, (byte) 2),
    拾取物品距離過遠_客戶端((byte) 5, 5000, 10),
    拾取物品距離過遠_伺服器端((byte) 3, 5000, 100, (byte) 2),
    寵物拾取物品距離過遠_客戶端((byte) 5, 10000, 20),
    寵物拾取物品距離過遠_伺服器端((byte) 3, 10000, 100, (byte) 0),
    使用過遠傳點((byte) 1, 60000, 100, (byte) 0),
    AST_TAKE_DAMAG((byte) 1, 60000, 100),
    迴避過高((byte) 20, 180000, 100),
    人物移動過快((byte) 1, 60000),
    人物跳躍過高((byte) 1, 60000),
    MISMATCHING_BULLETCOUNT((byte) 1, 300000),
    楓幣炸彈異常_非金錢((byte) 1, 300000),
    人物死亡攻擊((byte) 1, 300000, -1, (byte) 0),
    USING_UNAVAILABLE_ITEM((byte) 1, 300000),
    FAMING_SELF((byte) 1, 300000), // purely for marker reasons (appears in the database)
    FAMING_UNDER_15((byte) 1, 00000),
    楓幣炸彈異常_不存在物品((byte) 1, 300000),
    SUMMON_HACK((byte) 1, 300000),
    SUMMON_HACK_MOBS((byte) 1, 300000),
    狂狼連擊異常((byte) 1, 600000, 50),
    治癒非不死系怪物((byte) 20, 30000, 100);
    
    private final int points;
    private final int maxPoints;
    private final long validityDuration;
    

    private byte banType = 0; // 0 = Disabled, 1 = Enabled, 2 = DC

    public final int getPoints() {
        return points;
    }

    public final long getValidityDuration() {
        return validityDuration;
    }

    public final boolean shouldAutoban(final int count) {
        if (maxPoints == -1) {
            return false;
        }
        return count >= maxPoints;
    }

    public final byte getBanType() {
        return banType;
    }

    public final void setEnabled(final boolean enabled) {
        banType = (byte) (enabled ? BANTYPE_ENABLE : BANTYPE_DISABLE);
    }

    public final boolean isEnabled() {
        return banType >= BANTYPE_ENABLE;
    }

    private CheatingOffense(final byte points, final long validityDuration) {
        this(points, validityDuration, -1, (byte) 1);
    }

    private CheatingOffense(final byte points, final long validityDuration, final int autobancount) {
        this(points, validityDuration, autobancount, (byte) BANTYPE_ENABLE);
    }

    private CheatingOffense(final int points, final long validityDuration, final int autobancount, final byte bantype) {
        this.points = points;
        this.validityDuration = validityDuration;
        this.maxPoints = autobancount;
        this.banType = bantype;
    }
}
