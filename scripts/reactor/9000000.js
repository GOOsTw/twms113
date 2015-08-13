/* 採寶箱活動by:Kodan
 * 
 * 
*/

function act(){
	var rand = (Math.random() * 10) + 1;
	var randMap = (Math.random() * 9) + 1;
	if (rand < 5) {
	rm.gainItem(4031017, 1);
	rm.playerMessage("得到了寶箱趕緊去找NPC。");
	} else {
	rm.warp(109010110 + randMap);
}
}