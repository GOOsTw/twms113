function enter(pi) {
//    if (pi.getNumMobs() == 0 && pi.getPlayerVariable("summ") != null) {
        pi.deletePlayerVariable("summ");
        pi.warp(101000000, "jobin00");
//        pi.hideNpc(910110000, 1032109);
//        pi.hideNpc(910110000, 1032110);
        pi.openNpc(1103003);
        return true;
//    }
//    return false;
}
