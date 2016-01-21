SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `gashapon_items`
-- ----------------------------
DROP TABLE IF EXISTS `gashapon_items`;
CREATE TABLE `gashapon_items` (
  `gashaponsid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `chance` int(11) NOT NULL,
  `showmsg` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL,
  UNIQUE KEY `gashaponsid_itemid` (`gashaponsid`,`itemid`),
  KEY `itemid` (`itemid`),
  CONSTRAINT `gashapon_items_ibfk_1` FOREIGN KEY (`gashaponsid`) REFERENCES `gashapons` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of gashapon_items
-- ----------------------------

-- ----------------------------
-- Table structure for `gashapons`
-- ----------------------------
DROP TABLE IF EXISTS `gashapons`;
CREATE TABLE `gashapons` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npcId` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_npcId` (`id`,`npcId`),
  KEY `npcId` (`npcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of gashapons
-- ----------------------------
