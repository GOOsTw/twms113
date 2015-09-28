<<<<<<< HEAD
function act() {
	var map = rm.getPlayer().getMapId();
	if (map == 101040001 || rm.haveMonster(3230300)) {
		//rm.getPlayer().getMap().killAllMonsters(true);
		rm.getPlayer().getMap().killMonster(3230300);
		rm.mapMessage(5,"已經清除黑暗的力量!!");
	} else {
		rm.mapMessage(5,"沒有事情發生......");
	}
=======
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
>>>>>>> cf3d8e8145ff19d3dc48b221d0c75e9f8eb329ea
}