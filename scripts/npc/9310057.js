var rewards = Array(2000005, 1140001, 1141001, 2100005, 2100006, 2100007, 2100008, 2101000, 2101001);
var expires = Array(-1, 10, 30, 30, 30, 30, 30, 60, 60);
var quantity = Array(5, 1, 1, 1, 1, 1, 1, 1, 1);
var needed = Array(30, 60, 60, 25, 30, 35, 40, 45, 50, 55);
var gender = Array(2, 0, 1, 2, 2, 2, 2, 2, 2);
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
        for (var i = 3994059; i < 3994085; i++) {
	    cm.givePartyItems(i, 0, true);
	}
    }
    switch(cm.getPlayer().getMapId()) {
	case 100000000:
	case 101000000:
	case 102000000:
	case 103000000:
	case 104000000:
	case 120000000:
	case 220000000:
	case 200000000:
	case 740000000:
	case 741000000:
	case 742000000:
	case 800000000:
    	    if (status == 0) {
	        cm.sendSimple("安安 我是菇菇勃士 #b英文村的巨屌菇菇!\r\n\r\n#L0#我要前往英文村#l\r\n#L1#我要兌換東西#l\r\n#L2#什麼是英文村?#l");
    	    } else if (status == 1) {
	        if (selection == 0) {
		    cm.warp(702090400,0); //exit map lobby
		    cm.dispose();
		} else if (selection == 1) {
		    var selStr = "第一個勳章是男的,第二個勳章是女的 \r\n\r\n#b";
		    for (var i = 0; i < rewards.length; i++) {
			selStr += "#L" + i + "##v" + rewards[i] + "##t" + rewards[i] + "# x " + quantity[i] + " #r(" + needed[i] + " 乖寶寶印章)#b#l\r\n";
		    }
		    cm.sendSimple(selStr);
		} else if (selection == 2) {
		    cm.sendNext("#b[英文村]#k 自己#e#rGoogle#k!");
		    cm.dispose();
		}
	    } else if (status == 2) {
	        if (!cm.haveItem(4001137, needed[selection])) {
		    cm.sendNext("You don't have the needed 乖寶寶印章.");
		} else if (!cm.canHold(rewards[selection], 1)) {
		    cm.sendNext("Please make the inventory space.");
		} else {
		    cm.gainItem(4001137, -needed[selection]);
		    if (expires[selection] > 0) {
			cm.gainItemPeriod(rewards[selection], quantity[selection], expires[selection]);
		    } else {
			cm.gainItem(rewards[selection], quantity[selection]);
		    }
		}
		cm.dispose();
            }
	    break;
	case 702090400:
    	    if (status == 0) {
	        cm.sendSimple("Hello~I am Dr.P of #bEnglish School!\r\n\r\n#L0#前往英文村 - 簡單#l\r\n#L1#前往英文村 - 中級#l\r\n#L2#前往英文村 - 困難#l\r\n#L3#返回弓箭手村#l");
    	    } else if (status == 1) {
	        if (selection == 0 || selection == 1 || selection == 2) {
   		    var em = cm.getEventManager("English");
    		    if (em == null) {
			cm.sendOk("Please try again later.");
			cm.dispose();
			return;
    		    }
		    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
			cm.sendOk("The leader of the party must be here.");
		    } else {
			var party = cm.getPlayer().getParty().getMembers();
			var mapId = cm.getPlayer().getMapId();
			var next = true;
			var size = 0;
			var it = party.iterator();
			while (it.hasNext()) {
				var cPlayer = it.next();
				var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
				if (ccPlayer == null) {
					next = false;
					break;
				}
				size++;
			}	
			if (next && size >= 1) {
		    		if (em.getInstance("English" + selection) == null) {
					em.startInstance_Party("" + selection, cm.getPlayer());
		    		} else {
					cm.sendOk("Another party quest has already entered this channel.");
		    		}
			} else {
				cm.sendOk("All members of your party must be here.");
			}
		    }
		} else if (selection == 3) {
		    cm.warp(100000000,0);
		}
	        cm.dispose();
            }
	    break;
    }
}