/* Door of Dimension
	Enter 3rd job event
*/

function start() {
//    if (!cm.haveItem(4031057)) {
//	var em = cm.getEventManager("3rdjob");
	var em = cm.getEventManager("4jberserk");
	if (em == null) {
	    cm.sendOk("Sorry, but everything is broken.");
	} else {
	    em.newInstance(cm.getName()).registerPlayer(cm.getChar());
	}
//    } else {
//	cm.sendOk("lul.");
//    }
    cm.dispose();
}

function action(mode, type, selection) {

}