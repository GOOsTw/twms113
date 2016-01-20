
/*
	Name:  弓箭手村楓葉轉蛋機
	Place: 弓箭手村
*/

var status = -1;

var requireItem = 5220040; /* 楓葉轉蛋券 */

function action(mode, _type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    switch (status) {
        case 0:
            cm.sendYesNo("你好，我是弓箭手村轉蛋機，請問你要轉蛋嗎？");
            break;
        case 1: {
            if (cm.haveItem(requireItem)) {
                var gashapon = cm.getGashapon();
                if(gashapon != null) {
                    if (cm.canHold()) {
                        var gashaponItem = gashapon.generateReward();
                        if(gashaponItem != null) { 
                            cm.gainItem(gashaponItem.getItemId(), 1);
                            cm.gainItem(requireItem, -1);
                            cm.sendOk("恭喜你轉到了#b#i" + gashaponItem.getItemId() + "##k。");
                        } else {
                            cm.sendOk("轉蛋機維護中。");
                        }
                    } else {
                        cm.sendOk("請確認你的物品欄位還有空間。");
                    }
                } else {
                    cm.sendOk("轉蛋機尚未開放。");
                }
            } else {
                cm.sendOk("很抱歉由於你沒有#b#i" + requireItem + "##k，所以不能轉蛋哦。");
            }
        }
        case 2:
            cm.dispose();
    }
}
