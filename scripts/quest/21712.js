var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
            qm.sendNext("等你想要來幫助的時候，我會再次告訴你方法。");
            qm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("到現在為止所發現的娃娃結果,那裡面會發出只對怪物了解的獨特的聲音,那個聲音的關係怪物變越來越兇暴得.簡單說怪物變得原因就是娃娃.第一個情報有大該收集完成.去告知#b特魯#k");
    } else if (status == 1) {
        qm.askAcceptDecline("將 #o1210102#  異常的原因告知#b特魯#k，並完成了第一次情報收集。這段時間把精力放在升級和修煉吧。");
    } else if (status == 2) {
        qm.forceStartQuest();
        qm.sendNextS("我不知道擺在第一次觸發。有沒有辦法這個傀儡自然地產生，這意味著有人策劃這一點。我應該留意的 #o1210102#s.", 9);
    } else if (status == 3) {
        qm.sendPrevS("#b(你能找出是什麼原因造成的變化的 #o1210102#s. 你應該報告 #p1002104# 並提供你收集的信息。)#k", 2);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}