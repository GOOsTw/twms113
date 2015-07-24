/* 
	NPC Name: 		Shanks
	Map(s): 		Maple Road : Southperry (60000)
	Description: 		Brings you to Victoria Island
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.sendOk("你還不能離開這裡,肥宅.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	cm.sendYesNo("ㄋ只要 #e1500 楓幣#n, 就能離開這ㄍ破島哦!!");
    } else if (status == 1) {
	if (cm.haveItem(4031801)) {
	    cm.sendNext("哦哦,是那ㄍ死老頭ㄉ推薦信,豪ㄅ 我就讓你免費搭一次");
	} else {
	    cm.sendNext("真ㄉ只要 #e1500 楓幣#n 就能搭船!!");
	}
    } else if (status == 2) {
	if (cm.haveItem(4031801)) {
	    cm.sendNextPrev("豪! ㄋ有死老頭ㄉ推薦信,上船ㄅ!");
	} else {
	    if (cm.getPlayerStat("LVL") >= 8) {
		if (cm.getMeso() < 1500) {
		    cm.sendOk("ㄋㄊㄇ這ㄍ死肥宅,沒有錢還妄想搭船?ㄇㄉ!");
		    cm.dispose();
		} else {
		    cm.sendNext("豪! ㄋ有#e1500#n 楓幣 窩就讓ㄋ搭船!");
		}
	    } else {
		cm.sendOk("ㄋ還太蔡ㄌ,等ㄋ8等以後再來ㄅ!");
		cm.dispose();
	    }
	}
    } else if (status == 3) {
	if (cm.haveItem(4031801)) {
	    cm.gainItem(4031801, -1);
	    cm.warp(2010000,0);
	    cm.dispose();
	} else {
	    cm.gainMeso(-1500);
	    cm.warp(2010000,0);
	    cm.dispose();
	}
    }
}