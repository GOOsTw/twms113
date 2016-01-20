SET NAMES utf8;
SET time_zone = '+00:00';

DROP TABLE IF EXISTS `custom_npcs`;
CREATE TABLE `custom_npcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `npcId` int(11) NOT NULL,
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL,
  `channel` int(11) NOT NULL,
  `foothold` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `scriptid` (`npcId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;