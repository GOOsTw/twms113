/* Author: Xterminator (Modified by RMZero213)
	NPC Name: 		Roger
	Map(s): 		Maple Road : Lower level of the Training Camp (2)
	Description: 		Quest - Roger's Apple
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	
	if (status == 0) {
	    qm.sendNext("嗨, 肥宅.\r\n你4不4想要通過新手任務RR?");
	} else if (status == 1) {
	    qm.sendAcceptDecline("豪!你只要ㄘ掉窩給ㄋㄉ蘋果,你就通過惹!");
	} else if (status == 2) {
	    if (!qm.haveItem(2010007)) {
		qm.gainItem(2010007, 1);
	    }
	    qm.sendOk("窩給ㄋ蘋果ㄌ,快給窩ㄘ下去");
	} else if (status == 3) {
	    qm.forceStartQuest();
	    qm.dispose();
	}
    }
}
function end(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	if (status == 0) {
	    if (qm.getPlayerStat("HP") < 50) {
		qm.sendOk("乾!ㄋ還沒把窩給ㄋㄉ蘋果ㄘ下去.");
		qm.dispose();
	    } else {
		qm.sendOk("很好!ㄋ把窩給ㄋㄉ蘋果ㄘ下去ㄌ!!");
	    }
	} else if (status == 1) {
	    qm.gainExp(10);
	    qm.gainItem(2010000, 3);
	    qm.gainItem(2010009, 3);
	    qm.forceCompleteQuest();
	    qm.dispose();
	}
    }
}