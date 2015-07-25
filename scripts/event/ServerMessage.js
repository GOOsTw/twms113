var Message = new Array(
    "如果遇到不能點技能/能力值/不能進傳點/不能點NPC,請在對話框打@ea就可以了",
    "/找人 玩家名字 可以用來找人喔",
	"禁止開外掛",
	"遊戲愉快!!",
    "如有bug請回報GM");


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
    em.broadcastYellowMsg("[SncyMS公告]" + Message[Math.floor(Math.random() * Message.length)]);
}