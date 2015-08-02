var status = 0;
var beauty = 0;
var mhair = Array();
var fhair = Array();
var hairnew = Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple("嗨，我是#p9105006# 想要看我這個快刀俠嗎？？如果你有 #b#t5150041##k 我就可以免費幫你弄好看的頭髮。 \r\n#L2#使用: #i5150041##t5150041##l");
        } else if (status == 1) {
            if (selection == 0) {
                beauty = 0;
                cm.sendSimple("");
            } else if (selection == 2) {
                beauty = 2;
				var hair = cm.getPlayerStat("HAIR");
				hair_Colo_new = [];
				if (cm.getPlayerStat("GENDER") == 0) {
				hair_Colo_new = [30310, 30330, 30060, 30150, 30410, 30210, 30140, 30120, 30200, 30560, 30510, 30610, 30470, 30920, 30860, 30800];
			} else {
				hair_Colo_new = [31150, 31310, 31300, 31160, 31100, 31410, 31030, 31080, 31070, 31610, 31350, 31510, 31740, 31560, 31710, 31880];
			}
			for (var i = 0; i < hair_Colo_new.length; i++) {
				hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
			}
                cm.sendYesNo("注意!這是隨機，請問是否要使用 #b#t5150041##k?");
            }
        }
        else if (status == 2){
            cm.dispose();
            if (beauty == 2){
				if (cm.setRandomAvatar(5150041, hair_Colo_new) == 1) {
					cm.sendOk("享受！");
                } else {
                    cm.sendOk("您貌似沒有#b#t5150041##k..");
                }
            }
    }
}
}
