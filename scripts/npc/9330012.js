/**
楓葉武器製作NPC
35等 350萬 3000
42等 650萬 6000
64等 850萬 10000
77等 1000萬 20000
**/

importPackage(java.lang);

var status = -1;
var oldWepName;
var oldWepId;
var newWepId;
var newWepName;
var leaves;
var stimulator;
var cost;
var getNewWep;
var sel;

function start() {
    cm.sendSimple("嗨，我是#p9330012#有什麼可以幫忙的？？ \r\n\r\n#b#L0#兌換35等楓葉武器。#l \r\n\r\n#L1#兌換42等楓葉武器。#l\r\n\r\n#L2#兌換64等楓葉武器。#l\r\n\r\n#L4#兌換77等楓葉武器。#l#k");
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }
    if (status == 0) {
	sel = selection;
	if (sel == 0) {
	    cm.sendSimple("所以你想做什麼？？ \r\n#b#L0##t1302020##l \r\n#b#L1##t1332025##l \r\n#b#L2##t1382009##l \r\n#b#L3##t1452016##l \r\n#b#L4##t1462014##l \r\n#b#L5##t1472030##l \r\n#b#L6##t1492020##l \r\n#b#L7##t1482020##l \r\n#b#L8##t1092030##l");
	} else if (sel == 2) {
	    cm.sendSimple("所以你想做什麼？？ \r\n\r\n#b#L0#Maple Glory Sword (One-Handed Sword)#l\r\n#L1#Maple Soul Rohen (Two-Handed Sword)#l\r\n#L2#Maple Steel Axe (One-Handed Axe)#l\r\n#L3#Maple Demon Axe (Two-Handed Axe)#l\r\n#L4#Maple Havoc Hammer (One-Handed Mace)#l\r\n#L5#Maple Belzet (Two-Handed Mace)#l\r\n#L6#Maple Kandiva Bow (Bow)#l\r\n#L7#Maple Nishada (Crossbow)#l\r\n#L8#Maple Skanda (Claw)#l\r\n#L9#Maple Asura Dagger (Dagger)#l\r\n#L10#Maple Dark Mate (Dagger)#l\r\n#L11#Maple Soul Spear (Spear)#l\r\n#L12#Maple Karstan (Polearm)#l\r\n#L13#Maple Shine Wand (Wand)#l\r\n#L14#Maple Wisdom Staff (Staff)#l\r\n#L15#Maple Golden Claw (Knuckler)#l\r\n#L16#Maple Cannon Shooter (Gun)#l\r\n#L17#Maple Warrior Shield (Warrior Shield)#l\r\n#L18#Maple Magician Shield (Magician Shield)#l\r\n#L19#Maple Thief Shield (Thief Shield)#l\r\n#L20#Maple Cleat Katara#l");
	} else if (sel == 1) {
	    cm.sendSimple("所以你想做什麼？？ \r\n#b#L0#Maple Soul Singer#l \r\n#b#L1#Maple Lama Staff#l \r\n#b#L2#Maple Dragon Axe#l \r\n#b#L3#Maple Doom Singer#l \r\n#b#L4#Maple Impaler#l \r\n#b#L5#Maple Scorpio#l \r\n#b#L6#Maple Soul Searcher#l \r\n#b#L7#Maple Crossbow#l \r\n#b#L8#Maple Kanduyo#l \r\n#b#L9#Maple Storm Pistol#l \r\n#b#L10#Maple Storm Finger#l \r\n#b#L11#Maple Duke Katara#l");
	} else if (sel == 4) {
	    cm.sendSimple("所以你想做什麼？？ \r\n#b#L0#Maple Pyrope Sword#l \r\n#b#L1#Maple Pyrope Axe#l \r\n#b#L2#Maple Pyrope Hammer#l \r\n#b#L3#Maple Pyrope Halfmoon#l \r\n#b#L4#Maple Pyrope Wand#l \r\n#b#L5#Maple Pyrope Staff#l \r\n#b#L6#Maple Pyrope Rohen#l \r\n#b#L7#Maple Pyrope Battle Axe#l \r\n#b#L8#Maple Pyrope Maul#l \r\n#b#L9#Maple Pyrope Spear#l \r\n#b#L10#Maple Pyrope Hellslayer#l \r\n#b#L11#Maple Pyrope Bow#l \r\n#b#L12#Maple Pyrope Crow#l \r\n#b#L13#Maple Pyrope Skanda#l \r\n#b#L14#Maple Pyrope Knuckle#l \r\n#b#L15#Maple Pyrope Shooter#l \r\n#b#L16#Maple Pyrope Katara#l");
	}
    } else if (status == 1) {
	if (sel == 0) {
		//35等
	    if (selection == 0) {
		newWepName = "#t1302020#";
		newWepId = 1302020;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 1) {
		newWepName = "#t1332025#";
		newWepId = 1332025;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 2) {
		newWepName = "#t1382009#";
		newWepId = 1382009;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 3) {
		newWepName = "#t1452016#";
		newWepId = 1452016;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 4) {
		newWepName = "#t1462014#";
		newWepId = 1462014;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 5) {
		newWepName = "#t1472030#";
		newWepId = 1472030;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 6) {
		newWepName = "#t1492020#";
		newWepId = 1492020;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 7) {
		newWepName = "#t1482020#";
		newWepId = 1482020;
		leaves = 100;
		cost = 3500000;
	    } else if (selection == 8) {
		newWepName = "#t1092030#";
		newWepId = 1092030;
		leaves = 100;
		cost = 3500000;
	    }
	    cm.sendYesNo("Are you sure you want to make a #b" + newWepName + "#k? The following items and materials will be required...\r\n\#i4001126# x" + leaves + "#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/7/0# " + cost);
	} else if (sel == 2) {
		//64等
	    if (selection == 0) {
		oldWepName = "#t1302030#";
		oldWepId = 1302030;
		newWepName = "#t1302064#";
		newWepId = 1302064;
		leaves = 100;
		cost = 6500000;
		stimulator = 4130002;
	    } else if (selection == 1) {
		oldWepName = "#t1302030#";
		oldWepId = 1302030;
		newWepName = "#t1402039#";
		newWepId = 1402039;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130005;
	    } else if (selection == 2) {
		oldWepName = "#t1412011#";
		oldWepId = 1412011;
		newWepName = "#t1312032#";
		newWepId = 1312032;
		leaves = 100;
		cost = 6500000;
		stimulator = 4130003;
	    } else if (selection == 3) {
		oldWepName = "#t1412011#";
		oldWepId = 1412011;
		newWepName = "#t1412027#";
		newWepId = 1412027;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130006;
	    } else if (selection == 4) {
		oldWepName = "#t1422014#";
		oldWepId = 1422014;
		newWepName = "#t1322054#";
		newWepId = 1322054;
		leaves = 100;
		cost = 6500000;
		stimulator = 4130004;
	    } else if (selection == 5) {
		oldWepName = "#t1422014#";
		oldWepId = 1422014;
		newWepName = "#t1422029#";
		newWepId = 1422029;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130007;
	    } else if (selection == 6) {
		oldWepName = "#t1452022#";
		oldWepId = 1452022;
		newWepName = "#t1452045#";
		newWepId = 1452045;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130012;
	    } else if (selection == 7) {
		oldWepName = "#t1462019#";
		oldWepId = 1462019;
		newWepName = "#t1462040#";
		newWepId = 1462040;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130013;
	    } else if (selection == 8) {
		oldWepName = "#t1472032#";
		oldWepId = 1472032;
		newWepName = "#t1472055#";
		newWepId = 1472055;
		leaves = 200;
		cost = 6500000;
		stimulator = 4130015;
	    } else if (selection == 9 || selection == 10) {
		oldWepName = "#t1332025#";
		oldWepId = 1332025;
		if (selection == 9) {
		    newWepName = "#t1332056#";
		    newWepId = 1332056;
		} else {
		    newWepName = "#t1332055#";
		    newWepId = 1332055;
		}
		leaves = 200;
		cost = 8500000;
		stimulator = 4130014;
	    } else if (selection == 11) {
		oldWepName = "#t1432012#";
		oldWepId = 1432012;
		newWepName = "#t1432040#";
		newWepId = 1432040;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130008;
	    } else if (selection == 12) {
		oldWepName = "#t1442024#";
		oldWepId = 1442024;
		newWepName = "#t1442051#";
		newWepId = 1442051;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130009;
	    } else if (selection == 13) {
		oldWepName = "#t1382012#";
		oldWepId = 1382012;
		newWepName = "#t1372034#";
		newWepId = 1372034;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130010;
	    } else if (selection == 14) {
		oldWepName = "#t1382012#";
		oldWepId = 1382012;
		newWepName = "#t1382039#";
		newWepId = 1382039;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130011;
	    } else if (selection == 15){
		oldWepName = "#t1482021#";
		oldWepId = 1482021;
		newWepName = "#t1482022#";
		newWepId = 1482022;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130016;
	    } else if (selection == 16) {
		oldWepName = "#t1492021#";
		oldWepId = 1492021;
		newWepName = "#t1492022#";
		newWepId = 1492022;
		leaves = 200;
		cost = 8500000;
		stimulator = 4130017;
	    } else if (selection == 17) {
		oldWepName = "#t1092030#";
		oldWepId = 1092030;
		newWepName = "#t1092046#";
		newWepId = 1092046;
		leaves = 200;
		cost = 8500000;
	    } else if (selection == 18) {
		oldWepName = "#t1092030#";
		oldWepId = 1092030;
		newWepName = "#t1092045#";
		newWepId = 1092045;
		leaves = 200;
		cost = 8500000;
	    } else if (selection == 19) {
		oldWepName = "#t1092030#";
		oldWepId = 1092030;
		newWepName = "#t1092047#";
		newWepId = 1092047;
		leaves = 200;
		cost = 8500000;
	    }
	    cm.sendYesNo("Are you sure you want to make a #b" + newWepName + "#k? The following items and materials will be required...\r\n\r\n#i" + oldWepId + "# x 1\r\n#i4001126# x" + leaves + "\r\n A Stimulator can also be used if you have the required one! #r(Optional)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/7/0# " + cost + "#r楓幣#k");
	} else if (sel == 1) {
		//42等
	    if (selection == 0) {
		newWepName = "#t1302030#";
		newWepId = 1302030;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 1) {
		newWepName = "#t1382012#";
		newWepId = 1382012;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 2) {
		newWepName = "#t1412011#";
		newWepId = 1412011;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 3) {
		newWepName = "#t1422014#";
		newWepId = 1422014;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 4) {
		newWepName = "#t1432012#";
		newWepId = 1432012;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 5) {
		newWepName = "#t1442024#";
		newWepId = 1442024;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 6) {
		newWepName = "#t1452022#";
		newWepId = 1452022;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 7) {
		newWepName = "#t1462019#";
		newWepId = 1462019;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 8) {
		newWepName = "#t1472032#";
		newWepId = 1472032;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 9) {
		newWepName = "#t1492021#";
		newWepId = 1492021;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 10) {
		newWepName = "#t1482021#";
		newWepId = 1482021;
		leaves = 200;
		cost = 50000;
	    } else if (selection == 11) {
		newWepName = "#t1342026#";
		newWepId = 1342026;
		leaves = 200;
		cost = 50000;
	    }
	    cm.sendYesNo("Are you sure you want to make a #b" + newWepName + "#k? The following items and materials will be required...\r\n\#i4001126# x" + leaves + "#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/7/0# " + cost);
	} else if (sel == 4) {
		//77等
	    if (selection == 0) {
		oldWepName = "#t1302064#";
		oldWepId = 1302064;
		newWepName = "#t1302142#";
		newWepId = 1302142;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130002;
	    } else if (selection == 6) {
		oldWepName = "#t1402039#";
		oldWepId = 1402039;
		newWepName = "#t1402085#";
		newWepId = 1402085;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130005;
	    } else if (selection == 1) {
		oldWepName = "#t1312032#";
		oldWepId = 1312032;
		newWepName = "#t1312056#";
		newWepId = 1312056;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130003;
	    } else if (selection == 7) {
		oldWepName = "#t1412027#";
		oldWepId = 1412027;
		newWepName = "#t1412055#";
		newWepId = 1412055;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130006;
	    } else if (selection == 2) {
		oldWepName = "#t1322054#";
		oldWepId = 1322054;
		newWepName = "#t1322084#";
		newWepId = 1322084;
		leaves = 250;
		cost = 3000000;
		stimulator = 4130004;
	    } else if (selection == 8) {
		oldWepName = "#t1422029#";
		oldWepId = 1422029;
		newWepName = "#t1422057#";
		newWepId = 1422057;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130007;
	    } else if (selection == 11) {
		oldWepName = "#t1452045#";
		oldWepId = 1452045;
		newWepName = "#t1452100#";
		newWepId = 1452100;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130012;
	    } else if (selection == 12) {
		oldWepName = "#t1462040#";
		oldWepId = 1462040;
		newWepName = "#t1462085#";
		newWepId = 1462085;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130013;
	    } else if (selection == 13) {
		oldWepName = "#t1472055#";
		oldWepId = 1472055;
		newWepName = "#t1472111#";
		newWepId = 1472111;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130015;
	    } else if (selection == 3) {
		oldWepName = "#t1332055#";
		oldWepId = 1332055;
		newWepName = "#t1332114#";
		newWepId = 1332114;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130014;
	    } else if (selection == 9) {
		oldWepName = "#t1432040#";
		oldWepId = 1432040;
		newWepName = "#t1432075#";
		newWepId = 1432075;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130008;
	    } else if (selection == 10) {
		oldWepName = "#t1442051#";
		oldWepId = 1442051;
		newWepName = "#t1442104#";
		newWepId = 1442104;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130009;
	    } else if (selection == 4) {
		oldWepName = "#t1372034#";
		oldWepId = 1372034;
		newWepName = "#t1372071#";
		newWepId = 1372071;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130010;
	    } else if (selection == 5) {
		oldWepName = "#t1382039#";
		oldWepId = 1382039;
		newWepName = "#t1382093#";
		newWepId = 1382093;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130011;
	    } else if (selection == 14){
		oldWepName = "#t1482022#";
		oldWepId = 1482022;
		newWepName = "#t1482073#";
		newWepId = 1482073;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130016;
	    } else if (selection == 15) {
		oldWepName = "#t1492022#";
		oldWepId = 1492022;
		newWepName = "#t1492073#";
		newWepId = 1492073;
		leaves = 500;
		cost = 5000000;
		stimulator = 4130017;
	    } else if (selection == 16) {
		oldWepName = "#t1342027#";
		oldWepId = 1342027;
		newWepName = "#t1342028#";
		newWepId = 1342028;
		leaves = 500;
		cost = 5000000;
	    }
	    cm.sendYesNo("Are you sure you want to make a #b" + newWepName + "#k? The following items and materials will be required...\r\n\r\n#i" + oldWepId + "# x 1\r\n#i4001126# x" + leaves + "\r\n A Stimulator can also be used if you have the required one! #r(Optional)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/7/0# " + cost);
	}
    } else if (status == 2) {
	if (sel == 2 || sel == 4) {
	    if (mode != 1) {
		cm.sendOk("No? Maybe you should get the required items first, or make up your mind. I'll be here, waiting.");
		cm.dispose();
	    } else {
		if ((cm.getMeso() < cost) || (!cm.haveItem(oldWepId,1)) || (!cm.haveItem(4001126,leaves))) {
		    cm.sendOk("Sorry, but you don't seem to have all the items. Please get them all, and try again.");
		    cm.dispose();
		} else if (stimulator == null || !cm.haveItem(stimulator)) {
		    if (cm.canHold(newWepId)) {
			cm.gainItem(oldWepId, -1);
			cm.gainItem(4001126, -leaves);
			cm.gainMeso(-cost);
			cm.gainItem(newWepId,1);
			cm.sendOk("There, all done! That was quick, wasn't it? If you need any more items, I'll be waiting here.");
		    } else {
			cm.sendOk("It appears that you are currently in full inventory, please check.");
		    }
		    cm.dispose();
		} else {
		    status = 2;
		    cm.sendSimple("It appears that you have a #rStimulator#k for this weapon. Would you like to create the weapon with or without the #rStimulator#k? If you create without the #rStimulator#k, the item will always be #baverage#k. If you do create it with the #rStimulator#k, the item has a random chance of being #blower#k or #bhigher#k than average.\r\n#b#L20#Create weapon WITH Stimulator#l\r\n#L21#Create weapon WITHOUT Stimulator#l#k");
		}
	    }
	} else if (sel == 0 || sel == 1) {
	    if ((cm.getMeso() < cost) || !cm.haveItem(4001126,leaves)) {
		cm.sendOk("Sorry, but you don't seem to have all the items. Please get them all, and try again.");
	    } else {
		if (cm.canHold(newWepId)) {
		    cm.gainItem(4001126, -leaves);
		    cm.gainMeso(-cost);
		    cm.gainItem(newWepId, 1);
		    cm.sendOk("There, all done! That was quick, wasn't it? If you need any more items, I'll be waiting here.");
		} else {
		    cm.sendOk("It appears that you are currently in full inventory, please check.");
		}
	    }
	    cm.dispose();
	}
    } else if (status == 3) {
	if (sel == 2 || sel == 4) {
	    if (cm.canHold(newWepId)) {
		if (selection == 21) {
		    cm.gainItem(oldWepId,-1);
		    cm.gainItem(4001126,-leaves);
		    cm.gainMeso(-cost);
		    cm.gainItem(newWepId, 1);
		    cm.sendOk("There, all done! That was quick, wasn't it? If you need any more items, I'll be waiting here.");
		} else {
		    cm.gainItem(oldWepId,-1);
		    cm.gainItem(4001126,-leaves);
		    cm.gainItem(stimulator,-1);
		    cm.gainMeso(-cost);
		    cm.gainItem(newWepId,1,true);
		    cm.sendOk("There, all done! That was quick, wasn't it? If you need any more items, I'll be waiting here.");
		}
	    } else {
		cm.sendOk("It appears that you are currently in full inventory, please check.");
	    }
	    cm.dispose();
	}
	}
}