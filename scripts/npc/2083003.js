var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (cm.getMapId() == 240050100) {

        var eim = cm.getPlayer().getEventInstance();

        if (cm.haveItem(4001088, 1)) {
            cm.gainItem(4001088, -1);
            eim.setProperty("enter1", "1");
            eim.broadcastPlayerMsg(6, "第二個迷宮房的門被打開了。");
            var react = eim.getMapFactory().getMap("240050101").getReactorByName("passKey1");
            react.setEventId(1);
            react.forceHitReactor(0);
            cm.sendOk("第二個迷宮房的門被打開了。");
        } else if (cm.haveItem(4001089, 1)) {
            cm.gainItem(4001089, -1);
            eim.setProperty("enter2", "1");
            var react = eim.getMapFactory().getMap("240050102").getReactorByName("passKey2");
            react.setEventId(2);
            react.forceHitReactor(0);
            eim.broadcastPlayerMsg(6, "第三個迷宮房的門被打開了。");
            cm.sendOk("第三個迷宮房的門被打開了。");
        } else if (cm.haveItem(4001090, 1)) {
            cm.gainItem(4001090, -1);
            eim.setProperty("enter3", "1");
            var react = eim.getMapFactory().getMap("240050103").getReactorByName("passKey3");
            react.setEventId(3);
            react.forceHitReactor(0);
            eim.broadcastPlayerMsg(6, "第四個迷宮房的門被打開了。");
            cm.sendOk("第四個迷宮房的門被打開了。");
        } else if (cm.haveItem(4001091, 1)) {
            cm.gainItem(4001091, -1);
            eim.setProperty("enter4", "1");
            var react = eim.getMapFactory().getMap("240050104").getReactorByName("passKey4");
            react.setEventId(3);
            react.forceHitReactor(0);
            eim.broadcastPlayerMsg(6, "第五個迷宮房的門被打開了。");
            cm.sendOk("第五個迷宮房的門被打開了。");
        } else {
            var react = eim.getMapFactory().getMap("240050102").getReactorByName("passKey2");
            react.setEventId(1);
            react.forceHitReactor(0);
            cm.sendOk("...");
        }


        cm.dispose();

        // if (cm.isLeader() && cm.haveItem(4001087, 1) && cm.haveItem(4001088, 1) && cm.haveItem(4001089, 1) && cm.haveItem(4001090, 1) && cm.haveItem(4001091, 1)) {
        //     cm.showEffect(true, "quest/party/clear");
        //     cm.playSound(true, "Party1/Clear");
        //     cm.gainItem(4001087, -1);
        //     cm.gainItem(4001088, -1);
        //     cm.gainItem(4001089, -1);
        //     cm.gainItem(4001090, -1);
        //     cm.gainItem(4001091, -1);
        //     cm.warpParty(240050300);
        //     cm.dispose();
        // } else {
        //     cm.sendOk("請叫你的隊長帶著5種迷宮室鑰匙來找我");
        //     cm.dispose();
        // }
    }
}
