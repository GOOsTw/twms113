/*
	NPC Name: 		Regular Cab at Lith Habour
	Map(s): 		Victoria Road : Lith Habour (104000000)
	Description: 		Lith Habour
*/
var status = 0;
var maps = Array(120000000, 102000000, 100000000, 103000000, 101000000);
var rCost = Array(1200, 1000, 1000, 1200, 1200);
var costBeginner = Array(120, 100, 100, 120, 120);
var cost = new Array("1,200", "1,000", "1,000", "1,200", "1,200");
var show;
var sCost;
var selectedMap = -1;


function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status >= 2) {
	    cm.sendNext("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.");
	    cm.safeDispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	cm.sendNext("你好! 歡迎使用尻尻谷計程車,可以帶你去想要去的地方~");
    } else if (status == 1) {
	if (!cm.haveItem(4032313)) {
	    var job = cm.getJob();
	    if (job == 0 || job == 1000 || job == 2000) {
		var selStr = "你好! 歡迎使用尻尻谷計程車,可以帶你去想要去的地方~ 我們對新手有90%的優惠,請選擇目的.#b";
		for (var i = 0; i < maps.length; i++) {
		    selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + costBeginner[i] + " 楓幣)#l";
		}
	    } else {
		var selStr = "你好! 歡迎使用尻尻谷計程車,可以帶你去想要去的地方~ 請選擇目的.#b";
		for (var i = 0; i < maps.length; i++) {
		    selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " 楓幣)#l";
		}
	    }
	    cm.sendSimple(selStr);
	} else {
	    cm.sendNextPrev("Hey, since you have a Taxi Coupon, I can take you to the town indicated on the pass for free. It looks like your destination is #bHenesys#k!");
	}
    } else if (status == 2) {
	if (!cm.haveItem(4032313)) {
	    var job = cm.getJob();
	    if (job == 0 || job == 1000 || job == 2000) {
		sCost = costBeginner[selection];
		show = costBeginner[selection];
	    } else {
		sCost = rCost[selection];
		show = cost[selection];
	    }
	    cm.sendYesNo("你確定你要前往 #b#m" + maps[selection] + "##k? 前往需要 #b" + show + " 楓幣#k.");
	    selectedMap = selection;
	} else {
	    cm.gainItem(4032313, -1);
	    cm.warp(100000000, 6);
	    cm.dispose();
	}
    } else if (status == 3) {
	if (cm.getMeso() < sCost) {
	    cm.sendNext("你沒有足夠的楓幣.");
	    cm.safeDispose();
	} else {
	    cm.gainMeso(-sCost);
	    cm.warp(maps[selectedMap]);
	    cm.dispose();
	}
    }
}