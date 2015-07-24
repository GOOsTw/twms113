/**
	Fedro: Pirate 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 1) {
	cm.sendOk("等您下定決心再次找我吧.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
 if ((cm.getJob() == 511 || cm.getJob() == 521 || cm.getJob() == 512 || cm.getJob() == 522)) {	
	    cm.sendOk("您屬於海盜部,但是您已經成功三轉了,已經超越了教官的強度了!");
	    cm.dispose();
	    return;
}
	if (!(cm.getJob() == 510 || cm.getJob() == 520)) {
	    cm.sendOk("請找您的轉職教官,您不屬於海盜部的滾吧!");
	    cm.dispose();
	    return;
	}
	if ((cm.getJob() == 510 || cm.getJob() == 520) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    cm.sendNext("你的確是一個強大的海盜即將轉職");
	} else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    if (cm.getJob() == 510) {
		cm.changeJob(511);
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的拳霸了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-拳霸讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 520) {
		cm.changeJob(521);
		cm.gainAp(5);
                cm.expandInventory(2, 40);
		cm.sendOk("恭喜你現在已經成為最帥的神槍手了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-神槍手讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    }
	    } else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
		cm.dispose();
	    }
    }
}