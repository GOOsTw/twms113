/* Kedrick
	Fishking King NPC
*/

var status = -1;
var sel;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
		if (status == 0) {
			cm.dispose();
			return;
		}
	status--;
    }

    if (status == 0) {
	cm.sendSimple("我能為您做什麼嗎？？#b\n\r #L1#買普通魚餌。#l \n\r #L3#使用高級的魚餌。#l#k");
    } else if (status == 1) {
	sel = selection;
	if (sel == 1) {
	    cm.sendYesNo("請問確定要花 300000 楓幣 買 120 個普通魚餌？？");
	} else if (sel == 3) {
	    if (cm.canHold(2300001,120) && cm.haveItem(5350000,1)) {
		if (!cm.haveItem(2300001)) {
		    cm.gainItem(2300001, 120);
		    cm.gainItem(5350000,-1);
		    cm.sendNext("開心釣魚吧！");
		} else {
		    cm.sendNext("真貪心！等用完再來找我！");
		}
	    } else {
		cm.sendOk("請確認是否有高級的魚餌罐頭，或者檢查您的道具欄有沒有滿了。");
	    }
	    cm.safeDispose();
	}
    } else if (status == 2) {
	if (sel == 1) {
	    if (cm.canHold(2300000,120) && cm.getMeso() >= 300000) {
		if (!cm.haveItem(2300000)) {
		    cm.gainMeso(-300000);
		    cm.gainItem(2300000, 120);
		    cm.sendNext("開心釣魚吧！");
		} else {
		    cm.sendNext("真貪心！等用完再來找我！");
		}
	    } else {
		cm.sendOk("請確認是否有足夠的封幣，或者檢查您的道具欄有沒有滿了。");
	    }
	    cm.safeDispose();
	}
    }
}