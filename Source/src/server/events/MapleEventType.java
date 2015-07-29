/*
 This file is part of the ZeroFusion MapleStory Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>
 ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
package server.events;

public enum MapleEventType {

    打果子("打果子", new int[]{109080000}), //just using one
    打瓶蓋("打瓶蓋", new int[]{109080010}), //just using one
    終極忍耐("終極忍耐", new int[]{109040000, 109040001, 109040002, 109040003, 109040004}),
    爬繩子("爬繩子", new int[]{109030001, 109030002, 109030003}),
    是非題大考驗("是非題大考驗", new int[]{109020001}),
    滾雪球("滾雪球", new int[]{109060000}); //just using one
    public String command;
    public int[] mapids;

    private MapleEventType(String comm, int[] mapids) {
        this.command = comm;
        this.mapids = mapids;
    }

    public static final MapleEventType getByString(final String splitted) {
        for (MapleEventType t : MapleEventType.values()) {
            if (t.command.equalsIgnoreCase(splitted)) {
                return t;
            }
        }
        return null;
    }
}
