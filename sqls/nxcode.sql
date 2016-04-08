SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `nxcode`
-- ----------------------------
DROP TABLE IF EXISTS `nxcode`;
CREATE TABLE `nxcode` (
  `code` varchar(20) NOT NULL,
  `valid` int(11) NOT NULL DEFAULT '1',
  `user` varchar(15) DEFAULT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `item` int(11) NOT NULL DEFAULT '10000',
  `size` int(11) NOT NULL DEFAULT '1',
  `time` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of nxcode
-- ----------------------------
INSERT INTO `nxcode` VALUES ('MapleStoryYeeeeeee33', '0', '1000x2', '3', '1002140', '1', '1');
