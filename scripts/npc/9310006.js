/*
	公安C - 上海灘 - 通道 (701010323)
**/

function start() {
    if (cm.getParty() == null) { // No Party
	cm.sendOk("請組隊再來找我");
    } else if (!cm.isLeader()) { // Not Party Leader
	cm.sendOk("請叫你的隊長來找我!");
    } else {
	var party = cm.getParty().getMembers();
	var mapId = cm.getMapId();
	var next = true;
	var levelValid = 0;
	var inMap = 0;

	var it = party.iterator();
	while (it.hasNext()) {
	    var cPlayer = it.next();
	    if ((cPlayer.getLevel() >= 25 && cPlayer.getLevel() <= 200) || cPlayer.getJobId() == 900) {
		levelValid += 1;
	    } else {
		next = false;
	    }
	    if (cPlayer.getMapid() == mapId) {
		inMap += (cPlayer.getJobId() == 900 ? 1 : 1);
	    }
	}
	if (party.size() > 4 || inMap < 1) {
	    next = false;
	}
	if (next) {
	    var em = cm.getEventManager("WuGongPQ");
	    if (em == null) {
		cm.sendOk("This PQ is not currently available.");
	    } else {
		var prop = em.getProperty("state");
		if (prop.equals("0") || prop == null) {
		    em.startInstance(cm.getParty(),cm.getMap());
	            cm.dispose();
		    return;
		} else {
		    cm.sendOk("Someone is already attempting on the quest.");
		}
	    }
	} else {
	    cm.sendOk("裡面已經有人了0.0");
	}
    }
    cm.dispose();
}

function action(mode, type, selection) {
}