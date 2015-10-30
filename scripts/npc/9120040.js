/*
	NPC Name: 		Ponicher
	Map(s): 		Neo Tokyo 2100 : Odaiba (802000210)
	Description: 		Vergamot Battle starter
*/
var status = -1;

function start() {
    if (cm.getMapId() == 802000210) {
		if (cm.getPlayer().getClient().getChannel() != 3) {
			cm.sendOk("這個BOSS只能在3頻挑戰!!");
			cm.dispose();
			return;
		}
	var em = cm.getEventManager("Vergamot");
	
	if (em == null) {
	    cm.sendOk("找不到副本，請聯繫GM！！");
	    cm.dispose();
	    return;
	}
	//	var prop = em.getProperty("vergamotSummoned");

	//	if (((prop.equals("PQCleared") || (prop.equals("1")) && cm.getPlayerCount(802000211) == 0)) || prop.equals("0") || prop == null) {
	    	var prop = em.getProperty("state");
	if (prop == null || prop.equals("0")) {
	var squadAvailability = cm.getSquadAvailability("VERGAMOT");
	if (squadAvailability == -1) {
	    status = 0;
	    cm.sendYesNo("是否要登記貝魯加墨特遠征隊??");

	} else if (squadAvailability == 1) {
	    // -1 = Cancelled, 0 = not, 1 = true
	    var type = cm.isSquadLeader("VERGAMOT");
	    if (type == -1) {
		cm.sendOk("已經結束了申請。");
		cm.dispose();
	    } else if (type == 0) {
		var memberType = cm.isSquadMember("VERGAMOT");
		if (memberType == 2) {
		    cm.sendOk("在遠征隊的制裁名單。");
		    cm.dispose();
		} else if (memberType == 1) {
		    status = 5;
		    cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		} else if (memberType == -1) {
		    cm.sendOk("已經結束了申請。");
		    cm.dispose();
		} else {
		    status = 5;
		    cm.sendSimple("你要做什麼? \r\n#b#L0#查看遠征隊名單#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		}
	    } else { // Is leader
		status = 10;
		cm.sendSimple("你現在想做什麼？\r\n#b#L0#查看遠征隊成員。#l \r\n#b#L1#管理遠征隊成員。#l \r\n#b#L2#編輯限制列表。#l \r\n#r#L3#進入地圖。#l");
	    // TODO viewing!
	    }
	    } else {
			var eim = cm.getDisconnected("Vergamot");
			if (eim == null) {
				cm.sendOk("遠征隊的挑戰已經開始.");
				cm.safeDispose();
			} else {
				cm.sendYesNo("你要繼續進行遠征任務嗎?");
				status = 2;
			}
	    }
	} else {
			var eim = cm.getDisconnected("Vergamot");
			if (eim == null) {
				cm.sendOk("遠征隊的挑戰已經開始.");
				cm.safeDispose();
			} else {
				cm.sendYesNo("你要繼續進行遠征任務嗎?");
				status = 2;
			}
	}
    } else {
	status = 25;
	cm.sendYesNo("你要離開了嗎?");
    }
}

function action(mode, type, selection) {
    switch (status) {
	case 0:
	    if (mode == 1) {
			if (cm.registerSquad("VERGAMOT", 5, " 已經成為了遠征隊隊長。如果你想加入遠征隊，請重新打開對話申請加入遠征隊。")) {
				cm.sendOk("你已經成為了遠征隊隊長。接下來的5分鐘，請等待隊員們的申請。");
			} else {
				cm.sendOk("未知錯誤.");
			}
	    }
	    cm.dispose();
	    break;
	case 2:
		if (!cm.reAdd("Vergamot", "VERGAMOT")) {
			cm.sendOk("由於未知的錯誤，操作失敗。");
		}
		cm.safeDispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("VERGAMOT", 0)) {
		    cm.sendOk("由於未知的錯誤，操作失敗。");
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("VERGAMOT", true);
		if (ba == 2) {
		    cm.sendOk("遠征隊員已經達到30名，請稍後再試。");
		} else if (ba == 1) {
		    cm.sendOk("申請加入遠征隊成功，請等候隊長指示。");
		} else {
		    cm.sendOk("你已經參加了遠征隊，請等候隊長指示。");
		}
	    } else {// withdraw
		var baa = cm.addMember("VERGAMOT", false);
		if (baa == 1) {
		    cm.sendOk("成功退出遠征隊。");
		} else {
		    cm.sendOk("由於未知的錯誤，操作失敗。");
		}
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (mode == 1) {
		if (selection == 0) {
		    if (!cm.getSquadList("VERGAMOT", 0)) {
			cm.sendOk("由於未知的錯誤，操作失敗。");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList("VERGAMOT", 1)) {
			cm.sendOk("由於未知的錯誤，操作失敗。");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList("VERGAMOT", 2)) {
			cm.sendOk("由於未知的錯誤，操作失敗。");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    if (cm.getSquad("VERGAMOT") != null) {
			var dd = cm.getEventManager("Vergamot");
			dd.startInstance(cm.getSquad("VERGAMOT"), cm.getMap());
		    } else {
			cm.sendOk("由於未知的錯誤，操作失敗。");
		    }
		    cm.dispose();
		}
	    } else {
		cm.dispose();
	    }
	    break;
	case 11:
	    cm.banMember("VERGAMOT", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("VERGAMOT", selection);
	    }
	    cm.dispose();
	    break;
	case 25:
	    cm.warp(802000210, 0);
	    cm.dispose();
	    break;
    }
}