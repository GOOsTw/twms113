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
package client;

import java.io.Serializable;

public enum MapleBuffStat implements Serializable {

    //DICE = 0x100000, the roll is determined in the long-ass packet itself as buffedvalue
    //ENERGY CHARGE = 0x400000
    //teleport mastery = 0x800000
    //PIRATE'S REVENGE = 0x4000000
    //DASH = 0x8000000 and 0x10000000
    //SPEED INFUSION = 0x40000000
    //MONSTER RIDER = 0x20000000
    //COMBAT ORDERS = 0x1000000

    WATK(0),
    WDEF(1),
    MATK(2),
    MDEF(3),
    ACC(4),
    AVOID(5),
    HANDS(6),
    SPEED(7),
    JUMP(8),
    MAGIC_GUARD(9),
    DARKSIGHT(10),
    BOOSTER(11),
    POWERGUARD(12),
    MAXHP(13),
    MAXMP(14),
    INVINCIBLE(15),
    SOULARROW(16),
    COMBO(21),
    SUMMON(21), //hack buffstat for summons ^.- (does/should not increase damage... hopefully <3)
    WK_CHARGE(22),
    DRAGONBLOOD(23),
    HOLY_SYMBOL(24),
    MESOUP(25),
    SHADOWPARTNER(26),
    PICKPOCKET(27),
    PUPPET(28), // HACK - shares buffmask with pickpocket - odin special ^.-
    MESOGUARD(29),
    // Mask 1
    MORPH(33), // 33
    RECOVERY(34), // 34
    MAPLE_WARRIOR(35),
    STANCE(36),
    SHARP_EYES(37),
    MANA_REFLECTION(38),
    DRAGON_ROAR(39), // Stuns the user
    SPIRIT_CLAW(40),
    INFINITY(41),
    HOLY_SHIELD(42),
    HAMSTRING(43),
    BLIND(44),
    CONCENTRATE(45),
    ECHO_OF_HERO(47),
    UNKNOWN3(48),
    GHOST_MORPH(49),
    ARIANT_COSS_IMU(50), // The white ball around you
    DROP_RATE(52),
    MESO_RATE(53),
    EXPRATE(54),
    ACASH_RATE(55),
    GM_HIDE(56),
    UNKNOWN7(57),
    ILLUSION(58),
    BERSERK_FURY(59),
    DIVINE_BODY(60),
    SPARK(61),
    ARIANT_COSS_IMU2(62), // no idea, seems the same
    FINALATTACK(63),
    ELEMENT_RESET(65, true),
    SOUL_STONE(73), //same as pyramid_pq
    ENERGY_CHARGE(75, true),
    DASH_SPEED(76),
    DASH_JUMP(77, true),
    MONSTER_RIDING(78, true),
    SPEED_INFUSION(79, true),
    HOMING_BEACON(80),
    SOARING(82),
    LIGHTNING_CHARGE(84),
    MIRROR_IMAGE(85),
    OWL_SPIRIT(86),
    ARAN_COMBO(92, true),
    COMBO_DRAIN(93),
    COMBO_BARRIER(94),
    BODY_PRESSURE(95),
    //POST BB
    //DUMMY_STAT0     (0x8000000L, true), //appears on login
    //DUMMY_STAT1     (0x10000000L, true),
    //DUMMY_STAT2     (0x20000000L, true),
    //DUMMY_STAT3     (0x40000000L, true),
    //DUMMY_STAT4     (0x80000000L, true),
    //db stuff
    FINAL_CUT(88),
    THORNS(89),
  
    ENHANCED_MAXHP(93),
    ENHANCED_MAXMP(94),
    ENHANCED_WATK(95),
    ENHANCED_WDEF(96),
    ENHANCED_MDEF(97),
    PERFECT_ARMOR(98),
    SATELLITESAFE_PROC(99),
    SATELLITESAFE_ABSORB(100),
    CRITICAL_RATE_BUFF(102),
    MP_BUFF(103),
    DAMAGE_TAKEN_BUFF(104),
    DODGE_CHANGE_BUFF(105),
    CONVERSION(106),
    REAPER(107),
    MECH_CHANGE(109), //determined in packet by [skillLevel or something] [skillid] 1E E0 58 52???
    DARK_AURA(111),
    BLUE_AURA(112),
    YELLOW_AURA(113),;
    
   

    private static final long serialVersionUID = 0L;
    private final int buffstat;
    private final int first;

    private MapleBuffStat(int buffstat) {
        this.buffstat = 1 << (buffstat % 32);
        this.first = (int) Math.floor(buffstat / 32);
    }

    private MapleBuffStat(int buffstat, boolean stacked) {
        this.buffstat = 1 << ((buffstat % 32));
        this.first = (int) Math.floor(buffstat / 32);
    }

    public final int getPosition() {
        return 3 - first;
    }

    public final int getValue() {
        return buffstat;
    }
}
