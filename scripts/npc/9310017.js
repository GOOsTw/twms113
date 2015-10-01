/* Brittany
	Henesys Random Hair/Hair Color Change.
*/
var status = -1;
var beauty = 0;
var hair_Colo_new;

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }

    if (status == 0) {
	cm.sendSimple("嗨，我是#p9310017# 如果你有 #b#t5150014##k 或者 #b#t5151010##k, 我就可以免費幫你弄好看的頭髮。 \r\n#L0#使用: #i5150014##t5150014##l\r\n#L1#使用: #i5151010##t5151010##l");
    } else if (status == 1) {
	if (selection == 0) {
	    var hair = cm.getPlayerStat("HAIR");
	    hair_Colo_new = [];
	    beauty = 1;

	    if (cm.getPlayerStat("GENDER") == 0) {
		hair_Colo_new = [30031, 30041, 30001, 30062, 30111, 30121, 30161, 30261, 30271, 30421, 30551, 30341, 30301];
	    } else {
		hair_Colo_new = [31001, 31421, 31291, 31491, 30261, 30421, 31481, 31811, 31081, 31881, 31031, 31851, 31701, 34001];
	    }
	    for (var i = 0; i < hair_Colo_new.length; i++) {
		hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	    }
	    cm.sendYesNo("確定要使用 #b#t5150014##k 隨機剪髮了？？");

	} else if (selection == 1) {
	    var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	    hair_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 7; i++) {
		hair_Colo_new[i] = currenthaircolo + i;
	    }
	    cm.sendYesNo("確定要使用 #b#t5151010##k 隨機染髮了？？");
	}
    } else if (status == 2){
	if (beauty == 1){
	    if (cm.setRandomAvatar(5150014, hair_Colo_new) == 1) {
		cm.sendOk("享受！");
	    } else {
		cm.sendOk("痾.... 貌似沒有#b#t5150014##k。");
	    }
	} else {
	    if (cm.setRandomAvatar(5151010, hair_Colo_new) == 1) {
		cm.sendOk("享受！");
	    } else {
		cm.sendOk("痾.... 貌似沒有#b#t5151010##k。");
	    }
	}
	cm.safeDispose();
    }
}