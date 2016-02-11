
var status = -1;
var picked = 0;
var state = -1;
var item;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 2 || status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }

    if (status == 0) {
        if (!cm.isQuestFinished(29933)) {
            NewPlayer();
        }
        cm.sendSimple("歡迎來到啾咪谷~\r\n#b#L2#我要打開藍色小箱子#l\r\n#b#L4#我要解銀色鬃毛任務#l\r\n#b#L7#我要完成燈泡不能接的任務#l\r\n#b#L6#我要抽月光寶盒#k");
    } else if (status == 1) {
        if (selection == 2) {
            if (cm.haveItem(4031307, 1) == true) {
                cm.gainItem(4031307, -1);
                cm.gainItem(2020020, 100);
                cm.sendOk("#b蛋糕不要吃太多~旅遊愉快~");
                cm.dispose();
            } else {
                cm.sendOk("#b檢查一下背包有沒有藍色禮物盒哦");
                cm.dispose();
            }
        } else if (selection == 4) {
            var level = cm.getPlayerStat("LVL");
            if (cm.haveItem(4000264, 400) && cm.haveItem(4000266, 400) && cm.haveItem(4000267, 400) && (level >= 120)) {

                cm.gainItem(4000264, -400);
                cm.gainItem(4000266, -400);
                cm.gainItem(4000267, -400);
                cm.gainItem(1902001, 1);
                cm.sendOk("#b好好珍惜野豬~~");
                cm.dispose();
            } else {
                cm.sendOk("請檢查一下背包有沒有金色皮革４００個、木頭肩護帶４００個、骷髏肩護帶４００個,或者是你等級不夠");
            }
            cm.dispose();
		} else if (selection == 6) {
			cm.dispose();
			cm.openNpc(9330022);
        } else if (selection == 7) {
            if (cm.getQuestStatus(29507) == 1) {
                cm.gainItem(1142082, 1);
                cm.forceCompleteQuest(29507);
                cm.sendOk("完成任務。");
            }
            cm.forceCompleteQuest(3083);
            cm.forceCompleteQuest(8248);
            cm.forceCompleteQuest(8249);
            cm.forceCompleteQuest(8510);
            cm.forceCompleteQuest(50246);
            cm.sendOk("完成任務。");
            cm.dispose();
        }
    }
}

function NewPlayer() {
    if (!cm.haveItem(5000007, 1, true, true) && cm.canHold(5000007, 1)) {
        cm.gainPet(5000007, "黑色小豬", 1, 0, 100, 0);
    }
    if (!cm.haveItem(1002419, 1, true, true) && cm.canHold(1002419, 1)) {
        cm.gainItemPeriod(1002419, 1, 30);
    }
    if (!cm.haveItem(5030000, 1, true, true) && cm.canHold(5030000, 1)) {
        cm.gainItemPeriod(5030000, 1, 30);
    }
    if (!cm.haveItem(5100000, 1, true, true) && cm.canHold(5100000, 1)) {
        cm.gainItem(5100000, 1);
    }
    if (!cm.haveItem(5370000, 1, true, true) && cm.canHold(5370000, 1)) {
        cm.gainItemPeriod(5370000, 1, 7);
    }
    if (!cm.haveItem(5520000, 1, true, true) && cm.canHold(5520000, 1)) {
        cm.gainItem(5520000, 1);
    }
    if (!cm.haveItem(5180000, 1, true, true) && cm.canHold(5180000, 1)) {
        cm.gainItemPeriod(5180000, 1, 28);
    }
    if (!cm.haveItem(5170000, 1, true, true) && cm.canHold(5170000, 1)) {
        cm.gainItemPeriod(5170000, 1, 30);
    }
    cm.forceCompleteQuest(29933); //完成新手獎勵
    cm.sendOk("歡迎來到楓之谷 請使用 @help/@幫助 了解各式指令\r\n\r\n\r\n遊戲愉快^^");
    cm.dispose();
    return;
}
