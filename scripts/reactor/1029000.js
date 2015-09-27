function act() {
/*	var map = rm.getPlayer().getMapId();
	if (map == 101040001 || rm.haveMonster(3230300)) {
		rm.getPlayer().getMap().killAllMonsters(true);
		rm.mapMessage(5,"已經清除黑暗的力量!!");
	} else {
		rm.mapMessage(5,"沒有事情發生......");
	}*/
	rm.getPlayer().getMap().KillFk(true);
	return true;
}