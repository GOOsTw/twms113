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
package client.status;

import client.MapleDisease;
import java.io.Serializable;

public enum MonsterStatus implements Serializable {

    //物攻
    WATK(0),
    //物防
    WDEF(1),
    //魔攻
    MATK(2),
    //魔防
    MDEF(3),
    //命中
    ACC(4),
    //迴避
    AVOID(5),
    //速度
    SPEED(6),
    //暈眩
    STUN(7),
    //結冰
    FREEZE(8),
    //中毒
    POISON(9),
    //封印、沉默
    SEAL(10),
    //黑暗
    DARKNESS(11),
    //物理攻擊提昇
    WEAPON_ATTACK_UP(12),
    //物理防禦提昇
    WEAPON_DEFENSE_UP(13),
    //魔法攻擊提昇
    MAGIC_ATTACK_UP(14),
    //魔法防禦提昇
    MAGIC_DEFENSE_UP(15),
    //死亡
    DOOM(16),
    //影網
    SHADOW_WEB(17),
    //物攻免疫
    WEAPON_IMMUNITY(18),
    //魔攻免疫
    MAGIC_IMMUNITY(19),
    //挑釁
    SHOWDOWN(20),
    //免疫傷害
    DAMAGE_IMMUNITY(21),
    //忍者伏擊
    NINJA_AMBUSH(22),
    //
    DANAGED_ELEM_ATTR(23),
    //武器荼毒
    VENOMOUS_WEAPON(24),
    //致盲
    BLIND(25),
    //技能封印
    SEAL_SKILL(0x26),
    //
    EMPTY(27, true),
    //心靈控制
    HYPNOTIZE(28),
    //反勝物攻
    WEAPON_DAMAGE_REFLECT(29),
    //反射魔攻
    MAGIC_DAMAGE_REFLECT(30),
    //
    SUMMON(31, true),
    
    RISE_TOSS(32),
    //抵銷
    NEUTRALISE(32, false),
    //弱點
    IMPRINT(34, false),
    //怪物炸彈
    MONSTER_BOMB(35),
    //魔法無效
    MAGIC_CRASH(36),
    //恢復攻擊
    HEAL_DAMAGE(37),
    MBS38(38),
    MBS39(39),
    MBS40(40),
    MBS41(41),
    MBS42(42),
    MBS43(43),
    MBS44(44),
    //另一個咬擊[178-完成]
    ANOTHER_BITE(45),
    MBS46(46),
    
    //三角進攻
    TRIANGULATION(47),
    //減益爆炸
    STING_EXPLOSION(48),
    
    MBS49(49),
    MBS50(50),
    MBS51(51),
    MBS52(52),
    MBS53(53),
    MBS54(54),
    MBS55(55),
    MBS56(56),
    MBS57(57),
    MBS58(58, false),
    MBS59(59),
    MBS60(60, false),
    MBS61(61),
    BLEED(62, false),    
    MBS63(63, false),
    
    ;
    static final long serialVersionUID = 0L;
    private final int i;
    private final int position;
    private final boolean end;
    private final int bitNumber;

    private MonsterStatus(int i) {
        this.i = 1 << (i % 32); // 如果要變舊的，就把減31去掉，詳細請參考頂端說明
        this.position = 3 - (int) Math.floor(i / 32);
        this.end = false;
        this.bitNumber = i;
    }

    private MonsterStatus(int i, boolean end) {
        this.i = 1 << (i % 32); // 如果要變舊的，就把減31去掉，詳細請參考頂端說明
        this.position = 3 - (int) Math.floor(i / 32);
        this.end = end;
        this.bitNumber = i;
    }

    public int getPosition() {
        return  position;
    }

     public boolean isEmpty() {
        return end;
    }
    

    public long getValue() {
        return i;
    }

    public static final MapleDisease getLinkedDisease(final MonsterStatus skill) {
        switch (skill) {
            case STUN:
            case SHADOW_WEB:
                return MapleDisease.STUN;
            case POISON:
            case VENOMOUS_WEAPON:
                return MapleDisease.POISON;
            case SEAL:
                return MapleDisease.SEAL;
            case FREEZE:
                return MapleDisease.FREEZE;
            case DARKNESS:
                return MapleDisease.DARKNESS;
            case SPEED:
                return MapleDisease.SLOW;
        }
        return null;
    }
}
