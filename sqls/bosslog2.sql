SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bosslog2`
-- ----------------------------
DROP TABLE IF EXISTS `bosslog2`;
CREATE TABLE `bosslog2` (
  `bosslogid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accountid` int(10) unsigned NOT NULL,
  `bossid` varchar(20) CHARACTER SET utf8 NOT NULL,
  `lastattempt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bosslogid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of bosslog2
-- ----------------------------
