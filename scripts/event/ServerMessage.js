var Message = new Array(
    "如果遇到不能點技能/能力值/不能進傳點/不能點NPC,請在對話框打@ea就可以了",
    "/找人 玩家名字 可以用來找人喔",
    "一頻弓箭手訓練場I缺人哈拉",
    "寵物跟精靈商人可以去自由市場入口找蛋糕禮物盒領");


var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    setupTask = em.schedule("start", 60000);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    em.broadcastYellowMsg("[尻尻谷幫助]" + Message[Math.floor(Math.random() * Message.length)]);
}