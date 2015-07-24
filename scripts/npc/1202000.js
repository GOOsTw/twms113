/*
 * Tutorial Lirin
 */

var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
if (cm.getPlayer().getMapId() != 140090000) {
if (status == 0) {
	cm.sendSimple("等等! The information listed below can all be obtained simply by playing through Level 10, so it's not something you'll need to learn way in advance. Only the ones that would like to learn these in advance should continue from here on out. \n\r Okay, which of these would you like to learn more of?  \n\r #b#L1#Minimap#l \n\r #b#L2#Quest window#l \n\r #b#L3#Inventory#l \n\r #b#L4#Regular attacks#l \n\r #b#L5#Picking up items#l \n\r #b#L6#Equipping an item#l \n\r #b#L7#Skill window#l \n\r #b#L8#How to use Quick Slot#l \n\r #b#L9#Breaking the box#l \n\r #b#L10#Sitting on a chair#l \n\r #b#L11#Raising stats#l");
} else {
    cm.summonMsg(selection);
    cm.dispose();
}
} else {
    if (cm.getInfoQuest(21019).equals("")) {
	if (status == 0) {
	    cm.sendNext("你....終於醒了!");
	} else if (status == 1) {
	    cm.sendNextPrevS("...你是誰?", 2);
	} else if (status == 2) {
	    cm.sendNextPrev("我已經在這等你好久了. 等待那個與黑磨法師對抗的英雄甦醒...!");
	} else if (status == 3) {
	    cm.sendNextPrevS("等等, 你說什麼..? 你又是誰...?", 2);
	} else if (status == 4) {
	    cm.sendNextPrevS("等等... 我是誰...? 我既不起以前的事情了... 啊...我頭好痛啊..", 2);
	} else if (status == 5) {
	    cm.updateInfoQuest(21019, "helper=clear");
	    cm.showWZEffect("Effect/Direction1.img/aranTutorial/face");
	    cm.showWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin");
	    cm.playerSummonHint(true);
	    cm.dispose();
	}
    } else {
	if (status == 0) {
	    cm.sendNext("你還好嘛?");
	} else if (status == 1) {
	    cm.sendNextPrevS("我... 真的記不起來任何事了... 我是誰...你又是誰..?", 2);
	} else if (status == 2) {
	    cm.sendNextPrev("放輕鬆. 因為黑磨法師的詛咒，讓你想不起以前的了. 以前的事情已經不重要了. 我會幫助你想起所有事情的.");
	} else if (status == 3) {
	    cm.sendNextPrev("你曾經是這裡的英雄. 幾百年以前, 你與你的朋友們對抗黑魔法師，拯救了楓之谷的世界. 但那個時候，黑磨法師對你下了詛咒，將你冰凍起來，直到抹去你所有的記憶為止.");
	} else if (status == 4) {
	    cm.sendNextPrev("You are currently at an island called Rien, and it's the island the Black Wizard chose to trap you for hundreds of years. Because of his curse, this island is always covered in snow and ice, even though the weather is nothing close to that level. You were found somewhere deep in the cave.");
	} else if (status == 5) {
	    cm.sendNextPrev("And my name is Lirin, a member of the Rien race. The Rien race has been holding hope for your return for centuries, and now... the hope has finally paid off. You are here, standing right in front of me, the living breathing legend.");
	} else if (status == 6) {
	    cm.sendNextPrev("I may have given you too much information all at once. If you have yet to catch on to everything, then that's okay. You'll find out, sooner or later. In the mean time, #byou should head over to town#k. If you have any questions before getting to town, please feel free to ask me.");
	} else if (status == 7) {
	    cm.playerSummonHint(true);
	    cm.warp(140090100, 1);
	    cm.dispose();
	}
    }
}
}