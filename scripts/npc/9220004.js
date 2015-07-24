var itemList = new Array(5010000, 2022468, 5010002, 2022468, 2022468, 2022468, 2022468, 5121008, 5121009, 5121010, 5010038, 5010039, 2022468, 5010041, 2022468, 5010043, 2022468, 1702100, 2022468, 5120012, 2022468, 5121007, 2022468, 5010050, 2022468, 5120014, 5010054, 5010052, 5010053, 2022468, 2022468, 5010051, 1702209, 2022468, 2022468, 5150028, 1702210, 1702166, 2022468, 1702166, 2022468, 2022468, 1452062, 1492030, 2022468, 2022468, 1472077, 1482029, 2022468, 1462056, 2022468, 1322065, 1442071, 5021012, 2022468, 5021013, 5021014, 1432050, 2022468, 2022468, 2022468, 2022468, 2022468, 2022468, 1402053, 1422039, 2022468, 2022468, 5021010, 2022468, 5120005, 2022468, 1002484, 1002485, 2022468, 5021011, 2022468, 2022468, 1412035, 1382062, 2022468, 2022468, 2022468, 2022468, 1372046, 1332081, 2022468, 2022468, 5160011, 2022468, 1002031, 1002032, 2022468, 1332032, 5120002, 2022468, 1052078, 1051131, 5120006, 2022468, 1051049, 1050119, 2022468, 1702088, 2022468, 1050019, 2022468, 2022468, 5160012, 2022468, 5160000, 2022468, 2022468, 5160013, 2022468, 5160003, 2022468, 5160004, 5160002, 1000026, 2022468, 1001036, 1002714, 1002876, 5110000, 5110000, 2022468, 5110000, 1002493, 1002495, 1002486, 1002871, 1002872, 2022468, 2022468, 1002873, 1002874, 2022468, 1002720, 2022468, 2022465, 2022468, 2022466, 2022467, 2022468, 5021000, 2022468, 5160001, 5160005, 2022436, 2022437, 2022468, 1002480, 1002724, 1002481, 1002482, 2022438, 5010035, 5010034, 5160007, 5160006, 2022468, 2022468, 5010027, 5010021, 5010021, 5010022, 2022468, 5160010, 5010023, 5160009, 5010024, 5160008, 5010025, 1002479, 2022119, 2022468, 2022122, 1302105, 1312039, 1002368, 2022428, 5160014, 5021015, 5010033, 5110000, 2022468, 2022468, 2022468);
var randNum = Math.floor(Math.random()*(itemList.length));
var randItem = itemList[randNum];
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
/*        if (status >= 2 || status == 0) {
            cm.dispose();
            return;
        }*/
        status--;
    }

    if (status == 0) {
		cm.sendSimple("#b#L1#我要兌換白色禮物盒#l\r\n#L2#我要兌換紅色禮物盒#l\r\n#L3#我要兌換藍色禮物盒#l\r\n#L4#我要兌換紫色禮物盒#l\r\n#L5#我要領取魔法手套#l\r\n#L6#我要領取一組巨型雪球(消耗欄要空一格喔)#l\r\n#L7#我要給你溫暖的雪花#l#k");
    } else if (status == 1) {
	if (selection == 1) {
            if (cm.haveItem(4000422)) {
	    cm.gainItem(4000422, -1);
            cm.gainItem(randItem, 1);
               } else {
            cm.sendOk("你沒有禮物盒-.-");
	    }
            cm.dispose();
	} else if (selection == 2) {
            if (cm.haveItem(4000423)) {
	    cm.gainItem(4000423, -1);
            cm.gainItem(randItem, 1);
               } else {
            cm.sendOk("你沒有禮物盒-.-");
	    }
            cm.dispose();
	} else if (selection == 3) {
            if (cm.haveItem(4000424)) {
	    cm.gainItem(4000424, -1);
            cm.gainItem(randItem, 1);
               } else {
            cm.sendOk("你沒有禮物盒-.-");
	    }
            cm.dispose();
	} else if (selection == 4) {
            if (cm.haveItem(4000425)) {
	    cm.gainItem(4000425, -1);
            cm.gainItem(randItem, 1);
               } else {
            cm.sendOk("你沒有禮物盒-.-");
	    }
            cm.dispose();
	} else if (selection == 5) {
            cm.gainItem(1472063, 1);
            cm.dispose();
	} else if (selection == 6) {
	cm.gainItem(2060006, 800);
	cm.dispose();
	} else if (selection == 7) {
	    cm.sendGetNumber("Did you bring the powder keg with you? Then, please give me the #bPowder Keg#k you have.  I will make a nice firecracker.  How many are you willing to give me? \n\r #b< Number of Powder Keg in inventory : 0 >#k", 0, 0, 1000);
	}
    } else if (status == 2) {
	var num = selection;
	if (num == 7) {
	    cm.sendOk("T.T I will need the powder keg to start the fireworks....\r\n Please think again and talk to me.");
	} else if (cm.haveItem(4031875, num)) {
	    cm.gainItem(4031875, -num);
	    cm.giveKegs(num);
	    cm.sendOk("Don't forget to give me the powder keg when you obtain them."); cm.safeDispose();
	} else if (selection == 8) {
	    cm.sendNext("Status of Powder Keg Collection \n\r #B"+cm.getKegs()+"# \n\r If we collect them all, we can start the firework...");
        }
	cm.safeDispose();
    }
}