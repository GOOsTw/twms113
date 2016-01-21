function enter(pi) {
    pi.playPortalSE();
    var map = pi.getSavedLocation("AMORIA");
    if(map < 0)
        map = 100000000;
    pi.warp(map, 0);
    pi.clearSavedLocation("AMORIA");
}