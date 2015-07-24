var status = -1;
var minLevel = 70;
var maxLevel = 200;

var minPartySize = 1;
var maxPartySize = 6;

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
	if (cm.getParty() == null) { // No Party
	    cm.sendOk("請組隊再來找我");
	} else if (!cm.isLeader()) { // Not Party Leader
	    cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.#b#l");
	} else {
	    // Check if all party members are within PQ levels
	    var party = cm.getParty().getMembers();
	    var mapId = cm.getMapId();
	    var next = true;
	    var levelValid = 0;
	    var inMap = 0;
	    var it = party.iterator();

	    while (it.hasNext()) {
		var cPlayer = it.next();
		if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
		    levelValid += 1;
		} else {
		    next = false;
		}
		if (cPlayer.getMapid() == mapId) {
		    inMap += (1);
		}
	    }
	    if (party.size() > maxPartySize || inMap < minPartySize) {
		next = false;
	    }
	    if (next) {
		var em = cm.getEventManager("ProtectPig");
		if (em == null) {
		    cm.sendSimple("The PQ has encountered an error. Please report this on the forums, with a screenshot.#b#l");
		} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
        for (var i = 4001095; i < 4001099; i++) {
	    cm.givePartyItems(i, 0, true);
	}
        for (var i = 4001100; i < 4001101; i++) {
	    cm.givePartyItems(i, 0, true);
	}
			em.startInstance(cm.getParty(), cm.getMap());
			cm.dispose();
			return;
		    } else {
			cm.sendSimple("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.#b#");
		    }
		}
	    } else {
		cm.sendSimple("Your party is invalid. Please adhere to the following requirements:\r\n\r\n#rRequirements: " + minPartySize + " Party Members, all between level " + minLevel + " and level " + maxLevel + ".#b#l");
	    }
	}
	}
}