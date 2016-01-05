function start() {
    if (cm.getQuestStatus(8626) == 1 || cm.getQuestStatus(8626) == 2) {
        cm.warp(741020100);
        cm.dispose();
    } else {
        cm.sendOk("你沒有解玩前置任務");
        cm.dispose();
    }
}