/* 	Kelvin
	SingaPore VIP Face changer
*/
var status = -1;
var beauty = 0;
var mface = Array(20109, 20110, 20106, 20108, 20112, 20013);
var fface = Array(21021, 21009, 21010, 21006, 21008, 21012);
var facenew = Array();

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }

    if (status == 0) {
	cm.sendSimple("使用#b#t5152038##k的話，你就能指定更換臉型喔...你想要使用 #b#t5152038##k來整形嘛？ \r\n\#L2#來吧！！#l");
    } else if (selection == 2) {
	facenew = Array();
	if (cm.getChar().getGender() == 0) {
	    for(var i = 0; i < mface.length; i++) {
		facenew.push(mface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
	    }
	}
	if (cm.getChar().getGender() == 1) {
	    for(var i = 0; i < fface.length; i++) {
		facenew.push(fface[i] + cm.getChar().getFace() % 1000 - (cm.getChar().getFace() % 100));
	    }
	}
	cm.sendStyle("選擇一個喜歡的", facenew);

    } else if (status == 2){
	    if (cm.setAvatar(5152038, facenew[selection]) == 1){
	    cm.sendOk("享受你新的造型吧！");
	} else {
	    cm.sendOk("由於沒有#b#t5152038##k 所以我不能幫忙。");
	}
	cm.safeDispose();
    }
}