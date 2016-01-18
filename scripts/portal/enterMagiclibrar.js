/*
 * Ellinia
 * Enter Magician magic library
 * TODO : mapid 910110000 for cygnus quest
 */
function enter(pi) {

    if(pi.isQuestActive(20718) == true) {
        pi.warp(910110000, 0);
        //pi.getPlayer().startMapTimeLimitTask(10 * 60, pi.getMap(101000003));
    } else {
        pi.playPortalSE();
        pi.warp(101000003, 8);
    }
}
