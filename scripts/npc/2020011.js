/* Robeira
	Magician 3rd job advancement
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
 if ((cm.getJob() == 411 || cm.getJob() == 421 || cm.getJob() == 412 || cm.getJob() == 422)) {	
	    cm.sendOk("您屬於盜賊部,但是您已經成功三轉了,已經超越了教官的強度了!");
	    cm.dispose();
	    return;
}
	if (!(cm.getJob() == 410 || cm.getJob() == 420)) {
	    cm.sendOk("請找您的轉職教官,您不屬於盜賊部的滾吧!");
	    cm.dispose();
	    return;
	}
	if ((cm.getJob() == 410 || cm.getJob() == 420) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    cm.sendNext("你的確是一個強大的盜賊即將轉職");
	} else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    if (cm.getJob() == 410) { 
		cm.changeJob(411); 
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的暗殺者了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-暗殺者讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 420) {
		cm.changeJob(421); 
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的神偷了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-神偷讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    }
	    } else {
		cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
		cm.dispose();
	    }
    }
}