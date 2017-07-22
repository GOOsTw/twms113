/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import constants.SkillConstants;
import java.util.LinkedList;
import server.Timer;
import tools.FilePrinter;

/**
 *
 * @author Windyboy
 */
public class SkillCollector {

    private final static SkillCollector instance = new SkillCollector();
    private final LinkedList<Integer> CloseRangeAttack = new LinkedList<>();
    private final LinkedList<Integer> RangeAttack = new LinkedList<>();
    private final LinkedList<Integer> MagicAttack = new LinkedList<>();
    private final LinkedList<Integer> SpecialMove = new LinkedList<>();

    public static SkillCollector getInstance() {
        return instance;
    }

    public static class run implements Runnable {

        @Override
        public void run() {
            getInstance().outputList();
        }
    }

    public void init() {
        Timer.WorldTimer.getInstance().register(new run(), 1 * 60 * 60 * 1000);
    }

    public void outputList() {
        String msgs = "\r\n";
        for (int skill : CloseRangeAttack) {
            msgs += ", " + skill;
        }
        if (!CloseRangeAttack.isEmpty()) {
            FilePrinter.print("近距離攻擊_" + FilePrinter.getChineseData() + ".txt", msgs, false);
        }
        msgs = "\r\n";
        for (int skill : RangeAttack) {
            msgs += ", " + skill;
        }
        if (!RangeAttack.isEmpty()) {
            FilePrinter.print("遠距離攻擊_" + FilePrinter.getChineseData() + ".txt", msgs, false);
        }
        msgs = "\r\n";
        for (int skill : MagicAttack) {
            msgs += ", " + skill;
        }
        if (!MagicAttack.isEmpty()) {
            FilePrinter.print("魔法攻擊_" + FilePrinter.getChineseData() + ".txt", msgs, false);
        }
        msgs = "\r\n";
        for (int skill : SpecialMove) {
            msgs += ", " + skill;
        }
        if (!SpecialMove.isEmpty()) {
            FilePrinter.print("特殊攻擊_" + FilePrinter.getChineseData() + ".txt", msgs, false);
        }

    }

    public void addSkill(int type, int skill) {
        boolean Exist = false;
        switch (type) {
            case 1:
                Exist = SkillConstants.isCloseRangedAttack(skill);
                break;
            case 2:
                Exist = SkillConstants.isRangedAttack(skill);
                break;
            case 3:
                Exist = SkillConstants.isMagicAttack(skill);
                break;
            case 4:
                Exist = SkillConstants.isSpecialMove(skill);
                break;
        }
        if (!Exist) {
            if (!getSelectType(type).contains(skill)) {
                getSelectType(type).add(skill);
            }
        }
    }

    public boolean isExistSkill(int type, int skill) {
        boolean Exist = false;
        switch (type) {
            case 1:
                Exist = SkillConstants.isCloseRangedAttack(skill);
                break;
            case 2:
                Exist = SkillConstants.isRangedAttack(skill);
                break;
            case 3:
                Exist = SkillConstants.isMagicAttack(skill);
                break;
            case 4:
                Exist = SkillConstants.isSpecialMove(skill);
                break;
        }
        return Exist;
    }

    private LinkedList<Integer> getSelectType(int type) {
        LinkedList<Integer> list_ = new LinkedList<>();
        switch (type) {
            case 1:
                list_ = CloseRangeAttack;
                break;
            case 2:
                list_ = RangeAttack;
                break;
            case 3:
                list_ = MagicAttack;
                break;
            case 4:
                list_ = SpecialMove;
                break;
        }
        return list_;
    }
}