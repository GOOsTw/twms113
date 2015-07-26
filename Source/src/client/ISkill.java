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

import server.MapleStatEffect;
import server.life.Element;

/**
 * 
 * 廣泛型技能的介面
 */
public interface ISkill {

    /**
     * 取得技能ID
     * @return 
     */
    int getId();

    /**
     * 取得技能特效
     * @param skillLevel 技能等級
     * @return 技能特效資料
     */
    MapleStatEffect getEffect(int skillLevel);

    /**
     * 取得技能最大等級
     * @return 技能最大等級
     */
    byte getMaxLevel();

    /**
     * 
     * @return 
     */
    int getAnimationTime();

    public boolean canBeLearnedBy(int job);

    public boolean isFourthJob();

    public boolean getAction();

    public boolean isTimeLimited();

    public int getMasterLevel();

    public Element getElement();

    public boolean isBeginnerSkill();

    public boolean hasRequiredSkill();

    public boolean isInvisible();

    public boolean isChargeSkill();

    public int getRequiredSkillLevel();

    public int getRequiredSkillId();

    public String getName();
}
