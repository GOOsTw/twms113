function action(mode, type, selection) {
    cm.sendOk("嗨~ 我是沒工作的NPC.\r\n我的代碼是 #r" + npcid.toString() + "#k." + "您看到這個就表示這個 NPC 沒有工作\r\n如果這是個很重要的 NPC 請聯繫 GM\r\n謝謝您 !");
    cm.safeDispose();

}