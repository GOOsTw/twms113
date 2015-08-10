function action(mode, type, selection) {
    if (cm.getPlayer().getMapId() == 920011200) { //exit
        for (var i = 4001044; i < 4001064; i++) {
            cm.removeAll(i); //holy
        }
        cm.warp(200080101);
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("OrbisPQ");
    if (em == null) {
        cm.sendOk("請稍後在試試.");
        cm.dispose();
        return;
    }
    if (!cm.isLeader()) {
        cm.sendOk("我只跟你的隊長說話哦 哼!");
        cm.dispose();
        return;
    }
    if (em.getProperty("pre").equals("0")) {
        cm.sendNext("親愛的趕快拯救我RRRR 我被那恐怖的精靈老爹困在這裡出不乃了QAQ 那個白痴精靈放錯了所有的雅典娜雕像的碎片。女神原諒我RRRRR T___T。我是雅典娜忠實的幫傭易克～請幫我把20件在雲中看到的寶珠！");
        cm.dispose();
        return;
    }
    switch (cm.getPlayer().getMapId()) {
        case 920010000:
            clear();
            cm.warpParty(920010000, 2);
            break;
        case 920010100:
            if (em.getProperty("stage").equals("6")) {
                if (em.getProperty("finished").equals("0")) {
                    cm.warpParty(920010800); //GARDEN.	
                } else {
                    cm.sendOk("感謝你拯救了雅典娜<3 請跟她說話吧！");
                }
            } else {
                cm.sendOk("拜偷，快救救雅典娜，收集六個雕像的小碎片然後跟我說話來換最終的碎片");
            }
            break;
        case 920010200: //walkway
            if (!cm.haveItem(4001050, 30)) {
                cm.sendOk("從怪物身上收集 30 個女神的小碎片，然後跟我說話讓我把這些碎片融合成一個QQ");
            } else {
                cm.removeAll(4001050);
                cm.gainItem(4001044, 1); //first piece
                cm.givePartyExp(7500);
                clear();
            }
            break;
        case 920010300: //storage
            if (!cm.haveItem(4001051, 15)) {
                cm.sendOk("從怪物身上收集 15 個女神的小碎片，然後跟我說話讓我把這些碎片融合成一個QQ");
            } else {
                cm.removeAll(4001051);
                cm.gainItem(4001045, 1); //second piece
                cm.givePartyExp(7500);
                clear();
            }
            break;
        case 920010400: //lobby
            if (em.getProperty("stage3").equals("0")) {
                cm.sendOk("Please, find the LP for the current day of week and place it on the music player.\r\n#v4001056#Sunday\r\n#v4001057#Monday\r\n#v4001058#Tuesday\r\n#v4001059#Wednesday\r\n#v4001060#Thursday\r\n#v4001061#Friday\r\n#v4001062#Saturday\r\n");
            } else if (em.getProperty("stage3").equals("1")) {
                if (cm.canHold(4001046, 1)) {
                    cm.gainItem(4001046, 1); //third piece
                    cm.givePartyExp(7500);
                    clear();
                    em.setProperty("stage3", "2");
                } else {
                    cm.sendOk("Please make room!");
                }
            } else {
                cm.sendOk("真是太愛你...阿不是..太感謝你了<3");
            }
            break;
        case 920010500: //sealed
            if (em.getProperty("stage4").equals("0")) {
                var players = Array();
                var total = 0;
                for (var i = 0; i < 3; i++) {
                    var z = cm.getMap().getNumPlayersInArea(i);
                    players.push(z);
                    total += z;
                }
                if (total < 5) {
                    cm.sendOk("要五個玩家站在平台上rrrrrr.");
                } else {
                    var num_correct = 0;
                    for (var i = 0; i < 3; i++) {
                        if (em.getProperty("stage4_" + i).equals("" + players[i])) {
                            num_correct++;
                        }
                    }
                    if (num_correct == 3) {
                        if (cm.canHold(4001047, 1)) {
                            clear();
                            cm.gainItem(4001047, 1); //fourth
                            cm.givePartyExp(7500);
                            em.setProperty("stage4", "1");
                        } else {
                            cm.sendOk("Please make room!");
                        }
                    } else {
                        cm.showEffect(true, "quest/party/wrong_kor");
                        cm.playSound(true, "Party1/Failed");
                        if (num_correct > 0) {
                            cm.sendOk("One of the platforms is correct.");
                        } else {
                            cm.sendOk("All of the platforms are wrong.");
                        }
                    }
                }
            } else {
                cm.sendOk("門已經開啟了，去吧！");
            }
            cm.dispose();
            break;
        case 920010600: //lounge
            if (!cm.haveItem(4001052, 40)) {
                cm.sendOk("Gather the 40 Statue Pieces from the monsters in this stage, and please bring them to me so I can put them together!");
            } else {
                cm.removeAll(4001052);
                cm.gainItem(4001048, 1); //fifth piece
                cm.givePartyExp(7500);
                clear();
            }
            break;
        case 920010700: //on the way up
            if (em.getProperty("stage6").equals("0")) {
                var react = Array();
                var total = 0;
                for (var i = 0; i < 5; i++) {
                    if (cm.getMap().getReactorByName("" + (i + 1)).getState() > 0) {
                        react.push("1");
                        total += 1;
                    } else {
                        react.push("0");
                    }
                }
                if (total != 2) {
                    cm.sendOk("There needs to be 2 levers at the top of the map pushed on.");
                } else {
                    var num_correct = 0;
                    for (var i = 0; i < 5; i++) {
                        if (em.getProperty("stage62_" + i).equals("" + react[i])) {
                            num_correct++;
                        }
                    }
                    if (num_correct == 5) {
                        if (cm.canHold(4001049, 1)) {
                            clear();
                            cm.gainItem(4001049, 1); //sixth
                            cm.givePartyExp(7500);
                            em.setProperty("stage6", "1");
                        } else {
                            cm.sendOk("Please make room!");
                        }
                    } else {
                        cm.showEffect(true, "quest/party/wrong_kor");
                        cm.playSound(true, "Party1/Failed");
                        if (num_correct >= 3) {
                            cm.sendOk("One of the levers is correct.");
                        } else {
                            cm.sendOk("Both of the levers are wrong.");
                        }
                    }
                }
            } else {
                cm.sendOk("感謝尼！");
            }
            break;
        case 920010800:
            cm.warpParty(920010100);
            break;
        case 920010900:
            cm.sendNext("This is the jail of the tower. You may find some goodies here, but other than that I don't think we have any pieces here.");
            break;
        case 920011000:
            cm.sendNext("This is the hidden room of the tower. You may find some goodies here, but other than that I don't think we have any pieces here.");
            break;
    }
    cm.dispose();
}

function clear() {
    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
}
