/*var status = 0;

function start() {
    cm.sendYesNo("嗨，我是#p9010000# 想要和你的朋友在這個服留下美好的照片嗎？？");
}

function action(mode, type, selection) {
    if (mode != 1) {
        if (mode == 0)
            cm.sendOk("需要的時候，再來找我吧。");
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        cm.saveLocation("DONGDONGCHIANG");
        cm.warp(970000000, 0);
        cm.dispose();
    }
}*/
/* 
 * NPC   : Dev Doll
 * Map   : GMMAP
 */

importPackage(java.lang);
importPackage(Packages.constants);

var status = 0;
var invs = [1];
var invv;
var selected;
var items = Array();

var selItem = null;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 1) {
        var empty = true;
        var selStr = "我是裝備素質重置GM\r\n\r\n 注意！！！！！ 我只能重置一次 ， 請確定所有裝備都已經脫下放入物品欄中。";

        var inventory = cm.getInventory(invs[0]);

        for (var i = 0; i <= inventory.getSlotLimit(); i++) {
            var item = inventory.getItem(i);
            if (item == null) {
                continue;
            }
            var itemid = item.getItemId();
            if (!GameConstants.isEquip(itemid) || cm.isCash(itemid)) {
                continue;
            }
            items.push(i);
            empty = false;
            selStr += "#v" + itemid + "##t" + itemid + "##l\r\n";
        }

        if (empty) {
            cm.sendOk("你沒有任何的裝備.");
            cm.dispose();
            return;
        } else {
            cm.sendSimple(selStr + "#k");
        }

    }  else if (status == 2) {
        if( cm.getPlayerVariable('eqpfix') != null ) {
            cm.sendOk("無法再進行重置.");
            cm.dispose();
        }
        cm.sendYesNo("確定都放入物品欄位了？");
    }  else if (status == 3) {
        var inventory = cm.getInventory(1);

        for(var i = 0; i < items.length; i++) {
            var slot = items[i];
            var item = inventory.getItem(slot); 
            cm.gainItem(item.getItemId(), -1);
            cm.gainItem(item.getItemId(), 1);
        }
        cm.setPlayerVariable('eqpfix', '1');
        cm.sendSimple("好了")
    } else if (status == 5) {
        cm.dispose();
    }
}

