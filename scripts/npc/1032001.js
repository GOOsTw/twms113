/* Grendel the Really Old
	Magician Job Advancement
	Victoria Road : Magic Library (101000003)

	Custom Quest 100006, 100008, 100100, 100101
*/

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 2) {
	cm.sendOk("請重試.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	if (cm.getJob() == 0) {
	    if (cm.getPlayerStat("LVL") >= 8) {
		cm.sendNext("你要轉職成為一位 #r法師#k ?");
	    } else {
		cm.sendOk("你還不能轉職成為 #r法師#k 蔡B8.")
		cm.dispose();
	    }
	} else {
	    if (cm.getPlayerStat("LVL") >= 30 && cm.getJob() == 200) { // MAGICIAN
		if (cm.getQuestStatus(100006) >= 1) {
		    cm.completeQuest(100008);
		    if (cm.getQuestStatus(100008) == 2) {
			status = 20;
			cm.sendNext("我看到你完成了測試. 想要繼續轉職請點下一頁!");
		    } else {
			if (!cm.haveItem(4031009)) {
			    cm.gainItem(4031009, 1);
			}
			cm.sendOk("請去找 #r法師轉職教官#k.")
			cm.dispose();
		    }
		} else {
		    status = 10;
		    cm.sendNext("你已經可以轉職了,要轉職請點下一頁.");
		}
	    } else if (cm.getQuestStatus(100100) == 1) {
		cm.completeQuest(100101);
		if (cm.getQuestStatus(100101) == 2) {
		    cm.sendOk("Alright, now take this to #bRobeira#k.");
		} else {
		    cm.sendOk("Hey, #b#h0##k! I need a #bBlack Charm#k. Go and find the Door of Dimension.");
		    cm.startQuest(100101);
		}
		cm.dispose();
	    } else {
		cm.sendOk("你好,我是法師轉職官.");
		cm.dispose();
	    }
	}
    } else if (status == 1) {
	cm.sendNextPrev("一旦轉職了就不能反悔,如果不想轉職請點上一頁.");
    } else if (status == 2) {
	cm.sendYesNo("你真的要成為一位 #r法師#k ?");
    } else if (status == 3) {
	if (cm.getJob() == 0) {
	    cm.resetStats(4, 4, 20, 4);
	    cm.expandInventory(1, 4);
	    cm.expandInventory(4, 4);
	    cm.changeJob(200); // MAGICIAN
	}
	cm.gainItem(1372005, 1);
	cm.sendOk("轉職成功 ! 請去開創天下吧.");
	cm.dispose();
    } else if (status == 11) {
	cm.sendNextPrev("你可以選擇你要轉職成為一位 #r巫師(火,毒)#k, #r巫師(冰,雷)#k 或 #r僧侶#k.");
    } else if (status == 12) {
	cm.askAcceptDecline("但是我必須先測試你,你準備好了嗎 ?");
    } else if (status == 13) {
	cm.startQuest(100006);
	cm.gainItem(4031009, 1);
	cm.sendOk("請去找 #b法師轉職教官#k . 他會幫助你的.");
	cm.dispose();
    } else if (status == 21) {
	cm.sendSimple("你想要成為什麼 ? #b\r\n#L0#巫師(火,毒)#l\r\n#L1#巫師(冰,雷)#l\r\n#L2#僧侶#l#k");
    } else if (status == 22) {
	var jobName;
	if (selection == 0) {
	    jobName = "巫師(火,毒)";
	    job = 210; // FP
	} else if (selection == 1) {
	    jobName = "巫師(冰,雷)";
	    job = 220; // IL
	} else {
	    jobName = "僧侶";
	    job = 230; // CLERIC
	}
	cm.sendYesNo("你真的要成為一位 #r" + jobName + "#k?");
    } else if (status == 23) {
	cm.changeJob(job);
	cm.gainItem(4031012, -1);
        cm.gainItem(4031009, -1); //扣信
	cm.sendOk("轉職成功 ! 請去開創天下吧.");
	cm.dispose();
    }
}	
