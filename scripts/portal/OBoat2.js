importPackage(Packages.tools);
function enter(pi) {
    pi.playPortalSE();
    pi.warp(200090010, 5);
    if (pi.getClient().getChannelServer().getEventSM().getEventManager("Boats").getProperty("haveBalrog").equals("true")) {
	pi.getPlayer().getMap().broadcastMessage(MaplePacketCreator.boatEffect(1034));
    }
}
