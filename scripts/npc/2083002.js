
/*
 Crystal of Roots - Leafre Cave of life
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status < 1 && mode == 0) {
            cm.sendOk("好，需要的時候再來找我。");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendYesNo("你想離開這裡到 #m240040700# 嗎?");
        } else if (status == 1) {
            cm.removeAll(4001087);
            cm.removeAll(4001088);
            cm.removeAll(4001089);
            cm.removeAll(4001090);
            cm.removeAll(4001091);
            cm.removeAll(4001092);
			cm.removeAll(4001093);
            cm.warp(240040700, 0);
            cm.dispose();
        }
    }
}