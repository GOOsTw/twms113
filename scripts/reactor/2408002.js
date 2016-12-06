function act() {
    var eim = rm.getPlayer().getEventInstance();
    if (eim != null) {
        var mapid = rm.getMap().getId();
        if (mapid == 240050101) {
            var react = rm.getMap(240050100).getReactorByName("keyDrop1");
            rm.forceStartReactor(240050100, react.getReactorId());
        } else if (mapid == 240050102) {
            var react = rm.getMap(240050100).getReactorByName("keyDrop2");
            rm.forceStartReactor(240050100, react.getReactorId());
	} else if (mapid == 240050103) {
            var react = rm.getMap(240050100).getReactorByName("keyDrop3");
	    rm.forceStartReactor(240050100, react.getReactorId());
        } else if (mapid == 240050104) {
            var react = rm.getMap(240050100).getReactorByName("keyDrop4");
            rm.forceStartReactor(240050100, react.getReactorId());
        }
    }
}
