﻿var status = -1;

function action(mode, type, selection) {
    if (mode != 1) {
<<<<<<< HEAD
        cm.sendOk("祝你好運在這一次的公會任務！");
=======
        cm.sendOk("祝福你們公會戰順利");
>>>>>>> origin/master
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        if (cm.isPlayerInstance()) {
<<<<<<< HEAD
            cm.sendSimple("你想做什麼?? \r\n #L0#離開公會任務#l");
        } else {
            cm.sendOk("很抱歉我不能為你做任何事情。");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.sendYesNo("你確定要這麼做?\r\n那麼可能將回不來這裡了!!");
=======
            cm.sendSimple("你要想要做什麼? \r\n #L0#離開公會戰#l");
        } else {
            cm.sendOk("很抱歉，我不能為你做任何事。");
            cm.dispose();
        }
    } else if (status == 1) {
        cm.sendYesNo("你真的確定要離開嗎?");
>>>>>>> origin/master
    } else if (status == 2) {
        if (cm.isPlayerInstance()) {
            cm.getPlayer().getEventInstance().removePlayer(cm.getPlayer());
        }
        cm.dispose();
        return;
    }
}
