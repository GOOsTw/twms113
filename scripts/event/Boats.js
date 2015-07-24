importPackage(Packages.tools);

function init() {
    Orbis_btf = em.getMapFactory().getMap(200000112);
    Ellinia_btf = em.getMapFactory().getMap(101000301);
    Boat_to_Orbis = em.getMapFactory().getMap(200090010);
    Boat_to_Ellinia = em.getMapFactory().getMap(200090000);
    Orbis_Boat_Cabin = em.getMapFactory().getMap(200090011);
    Ellinia_Boat_Cabin = em.getMapFactory().getMap(200090001);
    Orbis_docked = em.getMapFactory().getMap(200000100);
    Ellinia_docked = em.getMapFactory().getMap(101000300);
    Orbis_Station = em.getMapFactory().getMap(200000111);
    Orbis_Boat_Cabin.getPortal("out00").setScriptName("OBoat1");
    Orbis_Boat_Cabin.getPortal("out01").setScriptName("OBoat2");
    Ellinia_Boat_Cabin.getPortal("out00").setScriptName("EBoat1");
    Ellinia_Boat_Cabin.getPortal("out01").setScriptName("EBoat2");
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.setProperty("haveBalrog", "false");
    em.schedule("stopentry", 240000); //The time to close the gate [4 min]
    em.schedule("takeoff", 300000); // The time to begin the ride [5 min]

    em.getMapFactory().getMap(200090000).resetFully();
    em.getMapFactory().getMap(200090010).resetFully();
}

function stopentry() {
    em.setProperty("entry","false");
    em.getMapFactory().getMap(200090011).resetFully();
    em.getMapFactory().getMap(200090001).resetFully();
}

function takeoff() {
    em.warpAllPlayer(200000112, 200090000);
    em.warpAllPlayer(101000301, 200090010);
    em.broadcastShip(200000111, 3);
    em.broadcastShip(101000300, 3);
    em.setProperty("docked","false");
    em.schedule("invasion", 60000); // Time to spawn Balrog [1 min]
    em.schedule("arrived", 420000); // The time that require move to destination [7 min]
}

function arrived() {
    em.warpAllPlayer(200090010, 200000100);
    em.warpAllPlayer(200090011, 200000100);
    em.warpAllPlayer(200090000, 101000300);
    em.warpAllPlayer(200090001, 101000300);
    em.broadcastShip(200000111, 1);
    em.broadcastShip(101000300, 1);
    em.getMapFactory().getMap(200090010).resetFully();
    em.getMapFactory().getMap(200090000).resetFully();
    em.setProperty("haveBalrog", "false");
    scheduleNew();
}

function invasion() {
    if (Math.floor(Math.random() * 10) < 10) {
	var map1 = em.getMapFactory().getMap(200090000);
	var pos1 = new java.awt.Point(-538, 143);
	map1.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos1);
	map1.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos1);

	var map2 = em.getMapFactory().getMap(200090010);
	var pos2 = new java.awt.Point(339, 148);
	map2.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos2);
	map2.spawnMonsterOnGroundBelow(em.getMonster(8150000), pos2);

        em.setProperty("haveBalrog","true");
        Boat_to_Orbis.broadcastMessage(MaplePacketCreator.boatEffect(1034));
        Boat_to_Ellinia.broadcastMessage(MaplePacketCreator.boatEffect(1034));
    }
}

function cancelSchedule() {
}