importPackage(Packages.client.inventory);
importPackage(Packages.tools);
importPackage(Packages.constants);
importPackage(Packages.server);
importPackage(java.util);

var status = -1;
var state = -1;
var first = true;
var next = false;
var item;
var size;
var rewardWeights = Array();
var rewardQuantity = Array();
var rewardItems = Array();
var totalWeights = Array();
var rng = new Random();
var time = 5;
var msg = "";
var next1 = true;

function start() {
    var rewards = Array(
            1302000, 1, 20,
            1302001, 1, 30,
			4000000, 1, 10,
            4000001, 7, 60
            );
    for (var i = 0; i < rewards.length; i++) {
        if (i % 3 == 0) {
            rewardItems.push(rewards[i]);
        } else if (i % 3 == 1) {
            rewardQuantity.push(rewards[i]);
        } else {
            rewardWeights.push(rewards[i]);
            var total = 0;
            for (var k = 0; k < rewardWeights.length; k++) {
                total += rewardWeights[k];
            }
            totalWeights.push(total);
        }
    }
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    if (first) {
        if (status == 0) {
            cm.sendSimple("" +
                    "想要做什麼？？\r\n" +
                    "#r注意！這裡獲得的東西非常稀有！！\r\n\r\n" +
                    "#L0##b使用#k#i5222000##d#t5222000##l\r\n" +
					"#L1##b購買#k#i5222000##d#t5222000##l\r\n" +
                    "#L2##b沒事。");
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                    reNew();
                    first = false;
                } else {
					msg = "確認背包是否滿了。";
					next1 = false;
                    
                }
			} else if (selection == 1) {
					cm.sendYesNo("確定是否要買#t5222000#??\r\n#r一次只能買一個若重複購買\r\n則會覆蓋上一個#t5222000#這點請注意！！");
            } else {
                cm.dispose();
            }
		} else if (status == 2) {
			if(cm.haveItem(5222000)) {
				status = 3;
				cm.sendYesNo("#r發現你身上已經有一個#t5222000#確定是否繼續購買??\r\n這是會蓋掉的哦！");
			} else {		
				status = 3;
				cm.sendYesNo("是否要購買??");
			}
        } else if (status == 4) {
			state = selection;
			buyItem();
			cm.dispose();
		}
    } else {
        next = selection == 0;
        if (next) {
            if (cm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && cm.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                reNew();
            } else {
				msg = "確認背包是否滿了。";
				next1 = false;
            }
        } else {
            cm.dispose();
            return;
        }	
    }
	if (!next1) {
        cm.sendNext(msg);
        cm.dispose();
        return;
    }
}

function reNew() {
    if (!cm.haveItem(5222000)) {
        cm.sendNext("騙我T.T，你沒有月光寶盒啊！？");
        cm.dispose();
        return;
    }
    var reward = getRewardIndex();
    cm.gainItem(5222000, -1);
    cm.gainItem(rewardItems[reward], rewardQuantity[reward]);
    cm.sendNext("" +
            "你獲得了以下獎賞：\r\n" +
            "" + rewardQuantity[reward] + "x#i" + rewardItems[reward] + "#:" + MapleItemInformationProvider.getInstance().getName(rewardItems[reward]) + "\r\n" +
            "你目前還有" + cm.itemQuantity(5222000) + "個月光寶盒\r\n" +
            "#L0##b繼續使用#k#i5222000#。#l\r\n" +
            "#L1##b先不使用。");
}

function buyItem() {
	if (!cm.haveItem(5222000)) {
		if (cm.getPlayer().getCSPoints(1) >= 15) {
			cm.getPlayer().modifyCSPoints(1, -15, false);
			cm.gainItem(5222000, 1);
        } else {
			msg = "#d你點數不夠哦";
            next1 = false;
        }
	} else {
		if (cm.getPlayer().getCSPoints(1) >= 15) {
			cm.getPlayer().modifyCSPoints(1, -15, false);
			cm.gainItem(5222000, -1);
			cm.gainItem(5222000, 1);
        } else {
			msg = "#d你點數不夠哦";
            next1 = false;
        }
	}
}

function getRewardIndex() {
    var weight = rng.nextInt(totalWeights[totalWeights.length - 1] + 1);
    var retIndex = 0;
    while (retIndex < totalWeights.length - 1) {
        if (weight < totalWeights[retIndex + 1] && weight >= totalWeights[retIndex]) {
            break;
        }
        retIndex++;
    }
    return retIndex;
} 