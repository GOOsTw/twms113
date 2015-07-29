/*
 * Cygnus 3rd Job advancement - Soul Warrior
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 0 && status == 1) {
	qm.sendNext("你還沒有準備好。");
	qm.dispose();
	return;
    } else if (mode == 0) {
	status--;
    } else {
	status++;
    }

    if (status == 0) {
	qm.sendNext("你所帶回來的寶石是神獸的眼淚，它擁有非常強大的力量。如果被黑磨法師給得手了，那我們全部都可能要倒大楣了....");
    } else if (status == 1) {
	qm.sendYesNo("女皇為了報答你的努力，將任命你為皇家騎士團的上級騎士，你準備好了嘛？");
    } else if (status == 2) {
	if (qm.getPlayerStat("RSP") > (qm.getPlayerStat("LVL") - 70) * 3) {
	    qm.sendNext("You still have way too much #bSP#k with you. You can't earn a new title like that. I strongly urge you to use more SP on your 1st and second level skills.");
	} else {
	    if (qm.canHold(1142068)) {
		qm.gainItem(1142068, 1);
		qm.changeJob(1111);
		qm.gainAp(5);
		qm.sendOk(", as of this moment, you are now the Knight Sergeant. From this moment on, you shall carry yourself with dignity and respect befitting your new title The Knight Sergeant of Knights of cygnus. May your glory shines as bright as it is right now.");
	    } else {
		qm.sendOk("請先把道具欄空出一些空間哦。");
	    }
	}
	qm.dispose();
    }
}

function end(mode, type, selection) {
}
