function start() {
    if (cm.getMapId() != 701010323) {
	cm.sendYesNo("你真的現在要出去嗎?");
    } else {
	cm.warp(701010320, 0);
	cm.dispose();
    }
}

function action(mode, type, selection) {
    if (mode == 1) {
	cm.warp(701010320, 0);
    }
    cm.dispose();
}