var status = -1;

function start(mode, type, selection) {}

function end(mode, type, selection) {
    qm.sendNext("任務完成，意外得到一個東西。");
    qm.forceCompleteQuest();
	qm.gainItem(4031571,1);
    qm.dispose();
}