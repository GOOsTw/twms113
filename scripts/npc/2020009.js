/* Robeira
	Magician 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = -1;
var job;

function start() {
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
 if ((cm.getJob() == 211 || cm.getJob() == 221 || cm.getJob() == 231 || cm.getJob() == 212 || cm.getJob() == 222 || cm.getJob() == 232)) {	
	    cm.sendOk("您屬於法師部,但是您已經成功三轉了,已經超越了教官的強度了!");
	    cm.dispose();
	    return;
}
	if (!(cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230)) { // CLERIC
	    cm.sendOk("請找您的轉職教官,您不屬於法師部的滾吧!");
	    cm.dispose();
	    return;
	}
	if ((cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    cm.sendNext("你的確是一個強大的法師即將轉職");
	} else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    if (cm.getJob() == 210) { // FP
		cm.changeJob(211); // FP MAGE
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的魔導士(火.毒)了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-魔導士(火.毒)讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 220) { // IL
		cm.changeJob(221); // IL MAGE
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的魔導士(冰.雷)了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-魔導士(冰.雷)讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 230) { // CLERIC
		cm.changeJob(231); // PRIEST
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的祭司了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-祭司讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    }
	    } else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
		cm.dispose();
	    }
    }
}