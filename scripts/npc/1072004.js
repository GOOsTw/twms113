/**
	Warrior Job Instructor - Warrior's Rocky Mountain (108000300)
**/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	if (cm.haveItem(4031013,30)) {
	    cm.removeAll(4031013);
	    cm.completeQuest(100004);
	    cm.startQuest(100005);
	    cm.sendOk("你成功了,你是一個英雄 ! 接下來請你回到村莊找武術教練.");
	} else {
	    cm.sendOk("請給我 #b30 #t4031013##k. 祝你好運.")
	    cm.dispose();
	}
    } else if (status == 1) {
	cm.gainItem(4031012, 1);
	cm.warp(102020300, 0);
	cm.dispose();
    }
}	