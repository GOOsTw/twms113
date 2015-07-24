/*
	NPC Name: 		Mark of the Squad
	Map(s): 		Entrance to Horned Tail's Cave
	Description: 		Horntail Battle starter
*/
var status = -1;

function start() {
		if (cm.getPlayer().getLevel() < 80) {
			cm.sendOk("There is a level requirement of 80 to attempt Horntail.");
			cm.dispose();
			return;
		}
		if (cm.getPlayer().getClient().getChannel() != 5) {
			cm.sendOk("闇黑龍王只有在頻道 5可以挑戰");
			cm.dispose();
			return;
		}
    var em = cm.getEventManager("HorntailBattle");

    if (em == null) {
	cm.sendOk("The event isn't started, please contact a GM.");
	cm.dispose();
	return;
    }
    var prop = em.getProperty("state");

    if (prop == null || prop.equals("0")) {
	var squadAvailability = cm.getSquadAvailability("Horntail");
	if (squadAvailability == -1) {
	    status = 0;
	    cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");

	} else if (squadAvailability == 1) {
	    // -1 = Cancelled, 0 = not, 1 = true
	    var type = cm.isSquadLeader("Horntail");
	    if (type == -1) {
		cm.sendOk("The squad has ended, please re-register.");
		cm.dispose();
	    } else if (type == 0) {
		var memberType = cm.isSquadMember("Horntail");
		if (memberType == 2) {
		    cm.sendOk("You been banned from the squad.");
		    cm.dispose();
		} else if (memberType == 1) {
		    status = 5;
		    cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		} else if (memberType == -1) {
		    cm.sendOk("The squad has ended, please re-register.");
		    cm.dispose();
		} else {
		    status = 5;
		    cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		}
	    } else { // Is leader
		status = 10;
		cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#踢除隊員#l \r\n#b#L2#修改遠征隊清單#l \r\n#r#L3#進入地圖#l");
	    // TODO viewing!
	    }
	} else {
		var props = em.getProperty("leader");
		if (props != null && props.equals("true")) {
			var eim = cm.getDisconnected("HorntailBattle");
			if (eim == null) {
				cm.sendOk("The squad's battle against the boss has already begun.");
				cm.safeDispose();
			} else {
				cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
				status = 1;
			}
		} else {
			cm.sendOk("Your leader has left the battle, so you may not return.");
			cm.safeDispose();
		}
	}
    } else {
		var props = em.getProperty("leader");
		if (props != null && props.equals("true")) {
			var eim = cm.getDisconnected("HorntailBattle");
			if (eim == null) {
				cm.sendOk("The battle against the boss has already begun.");
				cm.safeDispose();
			} else {
				cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
				status = 1;
			}
		} else {
			cm.sendOk("Your leader has left the battle, so you may not return.");
			cm.safeDispose();
		}
    }
}

function action(mode, type, selection) {
    switch (status) {
	case 0:
	    	if (mode == 1) {
			if (cm.registerSquad("Horntail", 5, "已成為闇黑龍王遠征隊長，想要參加遠征隊的玩家請開始進行申請。")) {
				cm.sendOk("You have been named the Leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
			} else {
				cm.sendOk("An error has occurred adding your squad.");
			}
	    	}
	    cm.dispose();
	    break;
	case 1:
		if (!cm.reAdd("HorntailBattle", "Horntail")) {
			cm.sendOk("Error... please try again.");
		}
		cm.safeDispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("Horntail", 0)) {
		    cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("Horntail", true);
		if (ba == 2) {
		    cm.sendOk("The squad is currently full, please try again later.");
		} else if (ba == 1) {
		    cm.sendOk("You have joined the squad successfully");
		} else {
		    cm.sendOk("You are already part of the squad.");
		}
	    } else {// withdraw
		var baa = cm.addMember("Horntail", false);
		if (baa == 1) {
		    cm.sendOk("You have withdrawed from the squad successfully");
		} else {
		    cm.sendOk("You are not part of the squad.");
		}
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (mode == 1) {
		if (selection == 0) {
		    if (!cm.getSquadList("Horntail", 0)) {
			cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList("Horntail", 1)) {
			cm.sendOk("Due to an unknown error, the request for squad has been denied.");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList("Horntail", 2)) {
			cm.sendOk("Due to an unknown error, the request for squad has been denied.");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    if (cm.getSquad("Horntail") != null) {
			var dd = cm.getEventManager("HorntailBattle");
			dd.startInstance(cm.getSquad("Horntail"), cm.getMap());
		    } else {
			cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		    }
		    cm.dispose();
		}
	    } else {
		cm.dispose();
	    }
	    break;
	case 11:
	    cm.banMember("Horntail", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("Horntail", selection);
	    }
	    cm.dispose();
	    break;
	default:
	    cm.dispose();
	    break;
    }
}