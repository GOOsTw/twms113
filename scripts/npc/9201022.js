/*
	NPC Name: 		Hera
	Map(s): 		Towns
	Description: 		Wedding Village Entrance
*/

var status = -1;

function start() {
    cm.sendSimple("啊~今天真是個好日子！這世界太美好了~！你不覺得這世界充滿了愛嗎？滿溢婚禮村的愛意都流淌到這裡來了~！ \n\r #b#L0# 我想回去結婚小鎮.#l \r\n #L1#我已經結婚了我想要領戀愛之心~");
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        switch (selection) {
            case 0:
                cm.sendNext("哦！多麼美好的一天！這個世界是多麼的美好〜！這個世界似乎是充滿愛的，不是嗎？我可以從這裡感受到愛的精神填補了婚禮!");
                break;
            case 1:
                var marr = cm.getQuestRecord(160001);
                var data = marr.getCustomData();
                if (data == null) {
                    marr.setCustomData("0");
                    data = "0";
                }
                if (cm.getPlayer().getMarriageId() <= 0 || !data.equals("3")) {
                    cm.sendOk("我很抱歉如果您想要得到這個椅子的話請先結婚~~");
                } else if (cm.canHold(3012004, 1) && !cm.haveItem(3012004, 1) && !cm.isQuestFinished(52013)) {
                    cm.gainItem(3012004, 1);
                    cm.forceCompleteQuest(52013);
                    cm.sendOk("結婚後多一份喜悅送你吧，但機會只有一次!");
                } else {
                    cm.sendOk("請確定是否裝備欄滿了或者您已經有相同的椅子了... 或者你領過了....");
                }
                cm.dispose();
                break;
        }
    } else if (status == 1) {
        cm.sendYesNo("你曾經去過的婚禮村莊？這是一個了不起的地方，愛情是無極限的。恩愛夫妻可以結婚還有，如何浪漫它是什麼？如果你想在那裡，我會告訴你的方式.");
    } else if (status == 2) {
        cm.sendNext("你做了一個正確的決定！你可以感受到愛的精神在婚禮村發揮到淋漓盡致。當你想回來，你的目的地將在這裡，所以不要擔心.");
    } else if (status == 3) {
        cm.saveLocation("AMORIA");
        cm.warp(680000000, 0);
        cm.dispose();
    }
}