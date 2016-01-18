function enter(pi) {
    if (pi.isQuestActive(20730)) {
        pi.openNpc(1063011);
        return true;
    } else {
        pi.playerMessage(5, "因不明的力量，而無法進入此洞穴。");
        return false;
    }

}
