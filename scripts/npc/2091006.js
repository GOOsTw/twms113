/*
	Mu Lung Training Center entrance
*/
var status = -1;
var sel;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendNext("#b(Once I hurried took my hand off the notice, the mystifying ayra disappeared as well.)");
	cm.safeDispose();
	return;
    }

    if (status == 0) {
	cm.sendSimple("#e< ?砍?>#n \r\nAnyone with the temerity to challange the Mu Lung Training Tower shall visit the Tower immediately. - Mu Gong - \r\n#b#L0#Challange the Mu Lung Training Tower#l\r\n#b#L1#Read the notice in detail.#l")
    } else if (status == 1) {
	sel = selection;
	if (sel == 1) {
	    cm.sendNext("#e<?砍?: Challange issued! >#n\r\nThis is Mu Gong, the rightful owner of Mu Lung Training Tower. A long time ago, I came to Mu Lung hoping to become a master, and now I have reached the point where I know I have reached the point where I know all Martial Arts. The owner of Mu Lung Training Tower was a weak individual who detested combat. That is why from here onwards, I'll be taking over the Mu Lung Training Tower.\r\nMu Lung Training Tower should be rightfully owned by the strongest individual in Mu Lung.\r\nIf you wish to learn from the very best, then take on the challange! I do not care if you wish to directly face me as well. I'll make sure to imprint greatness in every part of your body.");
	} else {
	    cm.sendYesNo("#b(雿??閬??郎?祆??啣????");
	}
    } else if (status == 2) {
	if (sel == 1) {
	    cm.sendNextPrev("雿撌曹?銋????? 雿?隤雿?憭? 敹怠?暹???韏瑕??.");
	} else {
	    cm.saveLocation("MULUNG_TC");
	    cm.warp(925020000, 0);
	    cm.dispose();
	}
    } else if (status == 3) {
	cm.dispose();
    }
}