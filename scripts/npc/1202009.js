var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendOk("是人類嗎？？沒事的話趕緊離開這裡吧！");
        cm.dispose();
    }
}