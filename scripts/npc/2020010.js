/* Rene
	Bowman 3rd job advancement
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
 if ((cm.getJob() == 311 || cm.getJob() == 321 || cm.getJob() == 312 || cm.getJob() == 322)) {	
	    cm.sendOk("您屬於弓箭部,但是您已經成功三轉了,已經超越了教官的強度了!");
	    cm.dispose();
	    return;
}
	if (!(cm.getJob() == 310 || cm.getJob() == 320)) {
	    cm.sendOk("請找您的轉職教官,您不屬於弓箭部的滾吧!");
	    cm.dispose();
	    return;
}

	if ((cm.getJob() == 310 || // HUNTER
	    cm.getJob() == 320) && // CROSSBOWMAN
	cm.getPlayerStat("LVL") >= 70 &&
	    cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    cm.sendNext("你的確是一個強大的弓箭手即將轉職");
	} else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    if (cm.getJob() == 310) { // HUNTER
		cm.changeJob(311); // RANGER
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的遊俠了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-遊俠讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 320) { // CROSSBOWMAN
		cm.changeJob(321); // SNIPER
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的狙擊手了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-狙擊手讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    }
	    } else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
		cm.dispose();
	    }
    }
}