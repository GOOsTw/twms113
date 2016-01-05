
function enter(pi) {
    if (pi.getPlayer().getClient().getChannel() != 1 && pi.getPlayer().getClient().getChannel() != 2) {
        pi.playerMessage(5, "拉圖斯只能在頻道1和2 能打而已。");
        return false;
    }
    if (pi.haveItem(4031870)) {
        pi.warp(922020300, 0);
        return true;
    }
    if (!pi.haveItem(4031172)) {
        pi.playerMessage(5, "不明的力量無法進入，需要有玩具獎牌。");
        return false;
    }
    if (pi.getPlayerCount(220080001) <= 0) { // Papu Map
        var papuMap = pi.getMap(220080001);

        papuMap.resetFully();

        pi.playPortalSE();
        pi.warp(220080001, "st00");
        return true;
    } else {
        if (pi.getMap(220080001).getSpeedRunStart() == 0 && (pi.getMonsterCount(220080001) <= 0 || pi.getMap(220080001).isDisconnected(pi.getPlayer().getId()))) {
            pi.playPortalSE();
            pi.warp(220080001, "st00");
            return true;
        } else {
            pi.playerMessage(5, "裡面的戰鬥已經開始，請稍後再嘗試。");
            return false;
        }
    }

    /*    var canEnter = false;

        if (pi.haveItem(4031172)) { // Ludibrium Medal
    	var currenttime = new Date().getTime();
    	var record = pi.getQuestRecord(7200);
    	var diff = currenttime - record.getCompletionTime();
    	
    	// After 1 day
    	if (diff >= 86400000) { // 24 hours
    	    record.setCompletionTime(currenttime);
    	    record.setCustomData("1");
    	    canEnter = true;
    	} else {
    	    var recordData = record.getCustomData();
    	    if (recordData == null || recordData.equals("")) {
    		record.setCompletionTime(currenttime);
    		record.setCustomData("1");
    		canEnter = true;
    	    } else if (recordData.equals("1")) {
    		record.setCustomData("2");
    		canEnter = true;
    	    }
    	}
        } else {
    	return false;
        }
        if (!canEnter) {
    	pi.playerMessage(5, "You may only enter the Origin of Clocktower twice per day.");
    	return false;
        }*/
}