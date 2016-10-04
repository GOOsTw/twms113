/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleAntiMacro;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Weber
 */
public class AntiMacroHandler {

    public static void LieDetector(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean isItem) { // Person who used 

        if (chr == null || chr.getMap() == null) {
            return;
        }
        final String targetName = slea.readMapleAsciiString();
        final MapleCharacter targetChar = chr.getMap().getCharacterByName(targetName);
        byte itemSlot = 0;
        if (isItem) {
            itemSlot = (byte) slea.readShort(); // 01 00 (first pos in use) 
            final int itemId = slea.readInt(); // B0 6A 21 00 
            final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(itemSlot);
            if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemId || itemId != 2190000) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        } else if (!chr.isGM() && chr.getJob() != 800) { // Manager using skill. Lie Detector Skill 
            c.getSession().close(true);
            return;
        }
        if ((FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) && isItem) || chr.getMap().getReturnMapId() == chr.getMapId() || chr.getMap().getReturnMapId() == 999999999) {
            chr.dropMessage(5, "你無法在這裡使用測謊機");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }

        if (targetChar == null || targetChar.getId() == chr.getId() || targetChar.isGM() && !chr.isGM()) {
            chr.dropMessage(1, "使用者不存在");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (targetChar.getEventInstance() != null) {
            chr.dropMessage(5, "你無法在這裡使用測謊機");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (targetChar.getAntiMacro().inProgress()) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 3));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (targetChar.getAntiMacro().isPassed() && isItem || targetChar.getAntiMacro().getAttempt() == 2) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 2));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!targetChar.getAntiMacro().startAntiMacro(chr.getName(), isItem, false)) {
            chr.dropMessage(5, "目前測謊機有狀況，無法使用"); //error occured, usually cannot access to captcha server 
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (isItem) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, itemSlot, (short) 1, false);
        }
        targetChar.dropMessage(5, chr.getName() + " 使用了測謊機");
    }

    public static void LieDetectorResponse(final SeekableLittleEndianAccessor slea, final MapleClient c) { // Person who typed 
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final String answer = slea.readMapleAsciiString();

        final MapleAntiMacro target = c.getPlayer().getAntiMacro();
        if (!target.inProgress()
                || (target.isPassed() && target.getLastType().getValue() == 0)
                || target.getAnswer() == null 
                || answer.length() <= 0) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (answer.equals(target.getAnswer())) {
            final MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(target.getTester());
            if (search_chr != null && search_chr.getId() != c.getPlayer().getId()) {
                search_chr.dropMessage(5, "The user have passed the Lie Detector Test.");
            }
            c.getSession().write(MaplePacketCreator.AntiMacroResponse((byte) 9, (byte) 1));
            c.getPlayer().gainMeso(5000, true);
            target.end();
        } else if (target.getAttempt() < 2) { // redo again 
            target.startAntiMacro(target.getTester(), target.getLastType().getValue() == 0, true); // new attempt 
        } else {
            final MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(target.getTester());
            if (search_chr != null && search_chr.getId() != c.getPlayer().getId()) {
                search_chr.dropMessage(5, "The user has failed the Lie Detector Test. You'll be rewarded 7000 mesos from the user.");
                search_chr.gainMeso(7000, true);
            }
            target.end();
            c.getPlayer().getClient().getSession().write(MaplePacketCreator.AntiMacroResponse((byte) 7, (byte) 4));
            final MapleMap to = c.getPlayer().getMap().getReturnMap();
            c.getPlayer().changeMap(to, to.getPortal(0));
        }
    }
}
