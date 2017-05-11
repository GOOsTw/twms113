function start() {
    if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
        cm.warpPartyInMap(105100301, 105100300);
        cm.dispose();
    } else {
        cm.sendYesNo("你確定要離開這個地圖嘛？？");
    }
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warpPartyInMap(105100100, 105100300);
    }
    cm.dispose();
}
