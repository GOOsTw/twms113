/** 
	Tylus: Warrior 3rd job advancement
	El Nath: Chief's Residence (211000001)

	Custom Quest 100100, 100102
*/

var status = 0;
var job;
var skills = Array(21001003, 21000000, 21100002, 21100004, 21100005, 21110002);
//polearm booster, combo ability, polearm mastery, final charge, combo smash, combo drain, full swing

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
	if (!(cm.getJob() == 110 || cm.getJob() == 120 || cm.getJob() == 130 || cm.getJob() == 2110)) {
	    if (cm.getQuestStatus(6192) == 1) {
		if (cm.getParty() != null) {
		    var ddz = cm.getEventManager("ProtectTylus");
		    if (ddz == null) {
			cm.sendOk("未知的錯誤");
		    } else {
			var prop = ddz.getProperty("state");
			if (prop == null || prop.equals("0")) {
			    ddz.startInstance(cm.getParty(), cm.getMap());
			} else {
			    cm.sendOk("別人已經在努力保護Tylus，請稍後重試了一下.");
			}
		    }
		} else {
		    cm.sendOk("請形成一方為了保護Tylus！");
		}
	    } else if (cm.getQuestStatus(6192) == 2) {
		cm.sendOk("你保護了我。謝謝。我會教你的立場技巧.");
		if (cm.getJob() == 112) {
			if (cm.getPlayer().getMasterLevel(1121002) <= 0) {
				cm.teachSkill(1121002, 0, 10);
			}
		} else if (cm.getJob() == 122) {
			if (cm.getPlayer().getMasterLevel(1221002) <= 0) {
				cm.teachSkill(1221002, 0, 10);
			}
		} else if (cm.getJob() == 132) {
			if (cm.getPlayer().getMasterLevel(1321002) <= 0) {
				cm.teachSkill(1321002, 0, 10);
			}
		}
} else if ((cm.getJob() == 111 || cm.getJob() == 121 || cm.getJob() == 131 || cm.getJob() == 112 || cm.getJob() == 122 || cm.getJob() == 132)){
	    cm.sendOk("您屬於劍士部,但是您已經成功三轉了,已經超越了教官的強度了!");
	    } else {
		cm.sendOk("請找您的轉職教官,您不屬於劍士部的滾吧!!");
	    }
	    cm.dispose();
	    return;
	}
	if ((cm.getJob() == 110 || cm.getJob() == 120 || cm.getJob() == 130 || cm.getJob() == 2110 ) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    cm.sendNext("你的確是一個強大的劍士即將轉職");
	} else {
	    cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
	    cm.safeDispose();
	}
    } else if (status == 1) {
	    if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
	    if (cm.getJob() == 110) { // FIGHTER
		cm.changeJob(111); // CRUSADER
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的十字軍了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-十字軍讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 120) { // PAGE
		cm.changeJob(121); // WHITEKNIHT
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的騎士了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-騎士讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 130) { // SPEARMAN
		cm.changeJob(131); // DRAGONKNIGHT
		cm.gainAp(5);
		cm.sendOk("恭喜你現在已經成為最帥的龍騎士了!");
                cm.worldMessage("『轉職快報』：恭喜玩家."+ cm.getChar().getName() +"  成功三轉-龍騎士讓我們熱烈的祝福他/她吧！");
		cm.dispose();
	    } else if (cm.getJob() == 2110) { // ARAN
		cm.changeJob(2111); // ARAN
		cm.gainAp(5);
		for (var i = 0; i < skills.length; i++) {
			cm.teachSkill(skills[i], cm.getPlayer().getSkillLevel(skills[i]));
		}
		cm.sendOk("恭喜你現在已經成為最帥的狂郎勇士了!");
		cm.dispose();
	    }
	    } else {
		cm.sendOk("請確認您的等級是否到達,並檢查技能點數是否使用完畢.");
		cm.dispose();
	    }
    }
}