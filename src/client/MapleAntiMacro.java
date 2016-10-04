/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.anticheat.captcha.CapchtaFactory;
import client.anticheat.captcha.Captcha;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import tools.HexTool;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author Weber
 */
public class MapleAntiMacro {

    public MapleCharacter player;
    public AntiMacroType type; // 0 = Normal, 1 = Admin Macro (Manager Skill) 
    public int attempt;
    public String tester, answer;
    public boolean inProgress, passed;

    public MapleAntiMacro(final MapleCharacter c) {
        this.player = c;
        reset();
    }

    public final boolean startAntiMacro(final String tester, final boolean isItem, final boolean anotherAttempt) {
        if (!anotherAttempt && (player.isClone() || (isPassed() && isItem) || inProgress() || attempt == 2)) {
            return false;
        }
        Captcha captcha = CapchtaFactory.getInstance().getCaptcha();
        if (captcha == null) {
            return false;
        }
        final byte[] image = captcha.getImageData();
        this.answer = captcha.getAnswer();
        this.tester = tester;
        this.inProgress = true;
        this.type = (isItem ? AntiMacroType.NORMAL : AntiMacroType.ADMIN_SKILL);
        this.attempt++;

        player.getClient().getSession().write(MaplePacketCreator.sendLieDetector(image));
        
        EtcTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isPassed() && player != null) {
                    if (attempt >= 2) {
                        final MapleCharacter testerChar = player.getMap().getCharacterByName(tester);
                        if (testerChar != null && testerChar.getId() != player.getId()) {
                            testerChar.dropMessage(5, testerChar.getName() + "沒通過測謊機測試，你將獲得7000楓幣.");
                            testerChar.gainMeso(7000, true);
                        }
                        end();
                        player.getClient().getSession().write(MaplePacketCreator.AntiMacroResponse((byte) 7, (byte) 4));
                        final MapleMap to = player.getMap().getReturnMap();
                        player.changeMap(to, to.getPortal(0));
                    } else { // can have another attempt 
                        startAntiMacro(tester, isItem, true);
                    }
                }
            }
        }, 60 * 1000);
        return true;
    }

    public final int getAttempt() {
        return attempt;
    }

    public final AntiMacroType getLastType() {
        return type;
    }

    public final String getTester() {
        return tester;
    }

    public final String getAnswer() {
        return answer;
    }

    public final boolean inProgress() {
        return inProgress;
    }

    public final boolean isPassed() {
        return passed;
    }

    public final void end() {
        this.inProgress = false;
        this.passed = true;
        this.attempt = 0;
    }

    public final void reset() { // called when change map, cc, reenter cs, or login 
        this.tester = "";
        this.answer = "";
        this.attempt = 0;
        this.inProgress = false;
        this.passed = false;
    }
}
