/*
	Encrypted Slate of the Squad - Leafre Cave of life
*/

var status = -1;
var minLevel = 80; // 35
var maxLevel = 200; // 65

var minPartySize = 6;
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

//    if (cm.getMapId() == 240050400) {
    if (status == 0) {
	if (cm.getParty() == null) { // No Party
	    cm.sendOk("How about you and your party members collectively beating a quest? Here you'll find obstacles and problems where you won't be able to beat it unless with great teamwork. If you want to try it, please tell the #bleader of your party#k to talk to me.\r\n\r\n#rRequirements: " + minPartySize + " Party Members, all between level " + minLevel + " and level " + maxLevel + ".#b");
	} else if (!cm.isLeader()) { // Not Party Leader
	    cm.sendOk("If you want to try the quest, please tell the #bleader of your party#k to talk to me.#b");
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
		    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
		}
	    }
	    if (party.size() > maxPartySize || inMap < minPartySize) {
		next = false;
	    }
	    if (next) {
		var em = cm.getEventManager("HontalePQ");
		if (em == null) {
		    cm.sendSimple("The Ludibrium PQ has encountered an error. Please report this on the forums, with a screenshot.#b");
		} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			em.startInstance(cm.getParty(), cm.getMap());
			cm.removeAll(4001022);
			cm.removeAll(4001023);
			cm.dispose();
			return;
		    } else {
			cm.sendSimple("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.#b");
		    }
		}
	    } else {
		cm.sendOk("Your party is invalid. Please adhere to the following requirements:\r\n\r\n#rRequirements: " + minPartySize + " Party Members, all between level " + minLevel + " and level " + maxLevel + ".#b");
	    }
	}
/*	} else {
	if (cm.getMapId() == 240050300) {
        if (cm.isLeader() && cm.haveItem(4001092, 3)) {
             cm.showEffect(true, "quest/party/clear");
             cm.playSound(true, "Party1/Clear");
             cm.gainItem(4001093. -3);
             cm.warpParty(240050310);
	} else {
             cm.sendOk("請叫你的隊長帶著3個紅色鑰匙來找我");
              }
        }
	if (cm.getMapId() == 240050310) {
        if (cm.isLeader() && cm.haveItem(4001093, 10)) {
             cm.showEffect(true, "quest/party/clear");
             cm.playSound(true, "Party1/Clear");
             cm.gainItem(4001093. -10);
             cm.warpParty(240050500);
	} else {
             cm.sendOk("請叫你的隊長帶著6個藍色鑰匙來找我");
              }*/
        }
}