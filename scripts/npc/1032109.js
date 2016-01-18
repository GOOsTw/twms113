function start() {
    if (cm.isQuestActive(20718) && cm.getPlayerVariable("summ") == null) {
        cm.setPlayerVariable("summ", "1");
        for (var i = 0; i< 20; i++) {
            cm.spawnMonster(2220100, 5, 184);
        }
    }
    cm.dispose();
}
