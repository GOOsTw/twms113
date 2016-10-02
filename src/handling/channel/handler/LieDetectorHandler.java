/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleLieDetector;
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
public class LieDetectorHandler {

    public static void LieDetector(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean isItem) { // Person who used 
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final String target = slea.readMapleAsciiString();
        byte slot = 0;
        if (isItem) {
            slot = (byte) slea.readShort(); // 01 00 (first pos in use) 
            final int itemId = slea.readInt(); // B0 6A 21 00 
            final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
            if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemId || itemId != 2190000) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
        } else if (!chr.isGM() && chr.getJob() != 800) { // Manager using skill. Lie Detector Skill 
            c.getSession().close();
            return;
        }
        if ((FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit()) && isItem) || chr.getMap().getReturnMapId() == chr.getMapId() || chr.getMap().getReturnMapId() == 999999999) {
            chr.dropMessage(5, "You may not use the Lie Detector on this area.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        final MapleCharacter search_chr = chr.getMap().getCharacterByName(target);
        if (search_chr == null || search_chr.getId() == chr.getId() || search_chr.isGM() && !chr.isGM()) {
            chr.dropMessage(1, "The user cannot be found.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (search_chr.getEventInstance() != null) {
            chr.dropMessage(5, "You may not use the Lie Detector on this area.");
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (search_chr.getAntiMacro().inProgress()) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 3));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (search_chr.getAntiMacro().isPassed() && isItem || search_chr.getAntiMacro().getAttempt() == 2) {
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 2));
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (!search_chr.getAntiMacro().startLieDetector(chr.getName(), isItem, false)) {
            chr.dropMessage(5, "Sorry! The Captcha Server is not available now, please try again later."); //error occured, usually cannot access to captcha server 
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (isItem) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        search_chr.dropMessage(5, chr.getName() + " has used the Lie Detector Test.");
    }

    public static void LieDetectorResponse(final SeekableLittleEndianAccessor slea, final MapleClient c) { // Person who typed 
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final String answer = slea.readMapleAsciiString();

        final MapleLieDetector ld = c.getPlayer().getAntiMacro();
        if (!ld.inProgress() || (ld.isPassed() && ld.getLastType() == 0) || ld.getAnswer() == null || answer.length() <= 0) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (answer.equals(ld.getAnswer())) {
            final MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(ld.getTester());
            if (search_chr != null && search_chr.getId() != c.getPlayer().getId()) {
                search_chr.dropMessage(5, "The user have passed the Lie Detector Test.");
            }
            c.getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 9, (byte) 1));
            c.getPlayer().gainMeso(5000, true);
            ld.end();
        } else if (ld.getAttempt() < 2) { // redo again 
            ld.startLieDetector(ld.getTester(), ld.getLastType() == 0, true); // new attempt 
        } else {
            final MapleCharacter search_chr = c.getPlayer().getMap().getCharacterByName(ld.getTester());
            if (search_chr != null && search_chr.getId() != c.getPlayer().getId()) {
                search_chr.dropMessage(5, "The user has failed the Lie Detector Test. You'll be rewarded 7000 mesos from the user.");
                search_chr.gainMeso(7000, true);
            }
            ld.end();
            c.getPlayer().getClient().getSession().write(MaplePacketCreator.LieDetectorResponse((byte) 7, (byte) 4));
            final MapleMap to = c.getPlayer().getMap().getReturnMap();
            c.getPlayer().changeMap(to, to.getPortal(0));
        }
    }
}
