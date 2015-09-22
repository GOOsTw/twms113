-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- 主機: 127.0.0.1
-- 產生時間： 2015 �?09 ??15 ??07:37
-- 伺服器版本: 5.6.17
-- PHP 版本： 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 資料庫： `wwwww`
--

-- --------------------------------------------------------

--
-- 資料表結構 `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL DEFAULT '',
  `password` varchar(128) NOT NULL DEFAULT '',
  `salt` varchar(32) DEFAULT NULL,
  `2ndpassword` varchar(134) DEFAULT NULL,
  `salt2` varchar(32) DEFAULT NULL,
  `loggedin` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `lastlogin` timestamp NULL DEFAULT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `birthday` date NOT NULL DEFAULT '0000-00-00',
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  `banreason` text,
  `gm` tinyint(1) NOT NULL DEFAULT '0',
  `email` tinytext,
  `macs` tinytext,
  `tempban` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `greason` tinyint(4) unsigned DEFAULT NULL,
  `ACash` int(11) DEFAULT NULL,
  `mPoints` int(11) DEFAULT NULL,
  `gender` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `SessionIP` varchar(64) DEFAULT NULL,
  `points` int(11) NOT NULL DEFAULT '0',
  `vpoints` int(11) NOT NULL DEFAULT '0',
  `lastlogon` timestamp NULL DEFAULT NULL,
  `facebook_id` varchar(255) DEFAULT NULL,
  `access_token` varchar(255) DEFAULT '',
  `password_otp` varchar(255) DEFAULT '',
  `expiration` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ranking1` (`id`,`banned`,`gm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `achievements`
--

CREATE TABLE IF NOT EXISTS `achievements` (
  `achievementid` int(9) NOT NULL DEFAULT '0',
  `charid` int(9) NOT NULL DEFAULT '0',
  `accountid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`achievementid`,`charid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `alliances`
--

CREATE TABLE IF NOT EXISTS `alliances` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(13) NOT NULL,
  `leaderid` int(11) NOT NULL,
  `guild1` int(11) NOT NULL,
  `guild2` int(11) NOT NULL,
  `guild3` int(11) NOT NULL DEFAULT '0',
  `guild4` int(11) NOT NULL DEFAULT '0',
  `guild5` int(11) NOT NULL DEFAULT '0',
  `rank1` varchar(13) NOT NULL DEFAULT '公會長',
  `rank2` varchar(13) NOT NULL DEFAULT '公會副會長',
  `rank3` varchar(13) NOT NULL DEFAULT '公會成員',
  `rank4` varchar(13) NOT NULL DEFAULT '公會成員',
  `rank5` varchar(13) NOT NULL DEFAULT '公會成員',
  `capacity` int(11) NOT NULL DEFAULT '2',
  `notice` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `auth_server_channel`
--

CREATE TABLE IF NOT EXISTS `auth_server_channel` (
  `channelid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `world` int(11) NOT NULL DEFAULT '0',
  `number` int(11) DEFAULT NULL,
  `key` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`channelid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=21 ;

--
-- 資料表的匯出資料 `auth_server_channel`
--

INSERT INTO `auth_server_channel` (`channelid`, `world`, `number`, `key`) VALUES
(1, 0, 1, '64cc5ec141f3d07fb076ba87c5c46825c44a997c'),
(2, 0, 2, '71278272bd03d969454d32211592f6660a11b8ee'),
(3, 0, 3, 'ce1a2a86d8f4f5bab9e600dcac20388d54403915'),
(4, 0, 4, '0c508d5b595790b9af143d5ea98973c58d34a0b8'),
(5, 0, 5, 'afe0f764a60b96c0265966b45091680c520b791b'),
(6, 0, 6, 'a63c74dfb6dd62c2caef06184c1f41fa75eed514'),
(7, 0, 7, 'e05a1b16f8b24d43cdf50a242b5641e33f275a21'),
(8, 0, 8, '4183d0b0ef17d9130433523b6d7c845755782e87'),
(9, 0, 9, '05a7864614180db4d3374e680039d55279ebdb8c'),
(10, 0, 10, '7ce024fdb03cf3ef03171828bff527269d01f08f'),
(11, 0, 11, '7e1994fe975472f827ecc71188cb5d64bdc24c1d'),
(12, 0, 12, '462538d6ee0a15c0ff52f4bf09da6d60bc3a657d'),
(13, 0, 13, 'd82cfcb1edcb2e31b751b3ccfeb7464500bb30dc'),
(14, 0, 14, '2f32eeea3628e70980dab4e6ec2f9d27a3313b96'),
(15, 0, 15, 'f5bf68e8d67b5e5f3f2ebe684a85fe72166db8f2'),
(16, 0, 16, 'cf97404e96937aa89557a3af5c0d252b654e1b3a'),
(17, 0, 17, '00edfe19b7f9c1b5303ce7a679452b3b6d3fe381'),
(18, 0, 18, 'a9e267e5bdad70925db1729acd38f93b6edff9d4'),
(19, 0, 19, '518fa6b3a88ce3ab6d688d2ac1e42a0754720664'),
(20, 0, 20, '923dcc504ed557f563fe52c82517a2b1feceeba1');

-- --------------------------------------------------------

--
-- 資料表結構 `auth_server_channel_ip`
--

CREATE TABLE IF NOT EXISTS `auth_server_channel_ip` (
  `channelconfigid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channelid` int(10) unsigned NOT NULL DEFAULT '0',
  `name` tinytext NOT NULL,
  `value` tinytext NOT NULL,
  PRIMARY KEY (`channelconfigid`),
  KEY `channelid` (`channelid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=20 ;

--
-- 資料表的匯出資料 `auth_server_channel_ip`
--

INSERT INTO `auth_server_channel_ip` (`channelconfigid`, `channelid`, `name`, `value`) VALUES
(1, 2, 'net.sf.odinms.channel.net.port', '8587'),
(2, 3, 'net.sf.odinms.channel.net.port', '8588'),
(3, 4, 'net.sf.odinms.channel.net.port', '8589'),
(4, 5, 'net.sf.odinms.channel.net.port', '8590'),
(5, 6, 'net.sf.odinms.channel.net.port', '8591'),
(6, 7, 'net.sf.odinms.channel.net.port', '8592'),
(7, 8, 'net.sf.odinms.channel.net.port', '8593'),
(8, 9, 'net.sf.odinms.channel.net.port', '8594'),
(9, 10, 'net.sf.odinms.channel.net.port', '8595'),
(10, 11, 'net.sf.odinms.channel.net.port', '7585'),
(11, 12, 'net.sf.odinms.channel.net.port', '7586'),
(12, 13, 'net.sf.odinms.channel.net.port', '7587'),
(13, 14, 'net.sf.odinms.channel.net.port', '7588'),
(14, 15, 'net.sf.odinms.channel.net.port', '7589'),
(15, 16, 'net.sf.odinms.channel.net.port', '7590'),
(16, 17, 'net.sf.odinms.channel.net.port', '7591'),
(17, 18, 'net.sf.odinms.channel.net.port', '7592'),
(18, 19, 'net.sf.odinms.channel.net.port', '7593'),
(19, 20, 'net.sf.odinms.channel.net.port', '7594');

-- --------------------------------------------------------

--
-- 資料表結構 `auth_server_cs`
--

CREATE TABLE IF NOT EXISTS `auth_server_cs` (
  `CashShopServerId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL,
  `world` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`CashShopServerId`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=2 ;

--
-- 資料表的匯出資料 `auth_server_cs`
--

INSERT INTO `auth_server_cs` (`CashShopServerId`, `key`, `world`) VALUES
(1, '9eafbf39c3d7a53f7dab1f1dc4d380ce9314f7a4', 0);

-- --------------------------------------------------------

--
-- 資料表結構 `auth_server_login`
--

CREATE TABLE IF NOT EXISTS `auth_server_login` (
  `loginserverid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL DEFAULT '',
  `world` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`loginserverid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=3 ;

--
-- 資料表的匯出資料 `auth_server_login`
--

INSERT INTO `auth_server_login` (`loginserverid`, `key`, `world`) VALUES
(1, 'e709b6bfc9eda84c166514ec9784739c6cc201bd', 0);

-- --------------------------------------------------------

--
-- 資料表結構 `auth_server_mts`
--

CREATE TABLE IF NOT EXISTS `auth_server_mts` (
  `MTSServerId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(40) NOT NULL,
  `world` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`MTSServerId`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=2 ;

--
-- 資料表的匯出資料 `auth_server_mts`
--

INSERT INTO `auth_server_mts` (`MTSServerId`, `key`, `world`) VALUES
(1, '9eafbf39c3d7a53f7dab1f1dc4d380ce9314f7a4', 0);

-- --------------------------------------------------------

--
-- 資料表結構 `bbs_replies`
--

CREATE TABLE IF NOT EXISTS `bbs_replies` (
  `replyid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `threadid` int(10) unsigned NOT NULL,
  `postercid` int(10) unsigned NOT NULL,
  `timestamp` bigint(20) unsigned NOT NULL,
  `content` varchar(26) NOT NULL DEFAULT '',
  `guildid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`replyid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `bbs_threads`
--

CREATE TABLE IF NOT EXISTS `bbs_threads` (
  `threadid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `postercid` int(10) unsigned NOT NULL,
  `name` varchar(26) NOT NULL DEFAULT '',
  `timestamp` bigint(20) unsigned NOT NULL,
  `icon` smallint(5) unsigned NOT NULL,
  `startpost` text NOT NULL,
  `guildid` int(10) unsigned NOT NULL,
  `localthreadid` int(10) unsigned NOT NULL,
  PRIMARY KEY (`threadid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `blocklogin`
--

CREATE TABLE IF NOT EXISTS `blocklogin` (
  `account` varchar(255) DEFAULT NULL,
  `blocktime` varchar(255) DEFAULT NULL,
  `unblocktime` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `blockuse`
--

CREATE TABLE IF NOT EXISTS `blockuse` (
  `account` varchar(255) DEFAULT NULL,
  `blocktime` varchar(255) DEFAULT NULL,
  `unblocktime` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `usetype` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `bosslog`
--

CREATE TABLE IF NOT EXISTS `bosslog` (
  `bosslogid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned NOT NULL,
  `bossid` varchar(20) NOT NULL,
  `lastattempt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bosslogid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `buddies`
--

CREATE TABLE IF NOT EXISTS `buddies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL,
  `buddyid` int(11) NOT NULL,
  `pending` tinyint(4) NOT NULL DEFAULT '0',
  `groupname` varchar(16) NOT NULL DEFAULT '其他',
  PRIMARY KEY (`id`),
  KEY `buddies_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `cashshop_limit_sell`
--

CREATE TABLE IF NOT EXISTS `cashshop_limit_sell` (
  `serial` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`serial`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `cashshop_modified_items`
--

CREATE TABLE IF NOT EXISTS `cashshop_modified_items` (
  `serial` int(11) NOT NULL,
  `discount_price` int(11) NOT NULL DEFAULT '-1',
  `mark` tinyint(1) NOT NULL DEFAULT '-1',
  `showup` tinyint(1) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `priority` tinyint(3) NOT NULL DEFAULT '0',
  `package` tinyint(1) NOT NULL DEFAULT '0',
  `period` tinyint(3) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `count` tinyint(3) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  `unk_1` tinyint(1) NOT NULL DEFAULT '0',
  `unk_2` tinyint(1) NOT NULL DEFAULT '0',
  `unk_3` tinyint(1) NOT NULL DEFAULT '0',
  `extra_flags` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`serial`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `characters`
--

CREATE TABLE IF NOT EXISTS `characters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL DEFAULT '0',
  `world` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(20) NOT NULL DEFAULT '',
  `level` int(3) unsigned NOT NULL DEFAULT '0',
  `exp` int(11) NOT NULL DEFAULT '0',
  `str` int(5) NOT NULL DEFAULT '0',
  `dex` int(5) NOT NULL DEFAULT '0',
  `luk` int(5) NOT NULL DEFAULT '0',
  `int` int(5) NOT NULL DEFAULT '0',
  `hp` int(5) NOT NULL DEFAULT '0',
  `mp` int(5) NOT NULL DEFAULT '0',
  `maxhp` int(5) NOT NULL DEFAULT '0',
  `maxmp` int(5) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  `hpApUsed` int(5) NOT NULL DEFAULT '0',
  `job` int(5) NOT NULL DEFAULT '0',
  `skincolor` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `fame` int(5) NOT NULL DEFAULT '0',
  `hair` int(11) NOT NULL DEFAULT '0',
  `face` int(11) NOT NULL DEFAULT '0',
  `ap` int(5) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL DEFAULT '0',
  `spawnpoint` int(3) NOT NULL DEFAULT '0',
  `gm` int(3) NOT NULL DEFAULT '0',
  `party` int(11) NOT NULL DEFAULT '0',
  `buddyCapacity` int(3) NOT NULL DEFAULT '25',
  `createdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `guildid` int(10) unsigned NOT NULL DEFAULT '0',
  `guildrank` tinyint(1) unsigned NOT NULL DEFAULT '5',
  `allianceRank` tinyint(1) unsigned NOT NULL DEFAULT '5',
  `monsterbookcover` int(11) unsigned NOT NULL DEFAULT '0',
  `dojo_pts` int(11) unsigned NOT NULL DEFAULT '0',
  `dojoRecord` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `pets` varchar(13) NOT NULL DEFAULT '-1,-1,-1',
  `sp` varchar(255) NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0',
  `subcategory` int(11) NOT NULL DEFAULT '0',
  `Jaguar` int(3) NOT NULL DEFAULT '0',
  `rank` int(11) NOT NULL DEFAULT '1',
  `rankMove` int(11) NOT NULL DEFAULT '0',
  `jobRank` int(11) NOT NULL DEFAULT '1',
  `jobRankMove` int(11) NOT NULL DEFAULT '0',
  `marriageId` int(11) NOT NULL DEFAULT '0',
  `familyid` int(11) NOT NULL DEFAULT '0',
  `seniorid` int(11) NOT NULL DEFAULT '0',
  `junior1` int(11) NOT NULL DEFAULT '0',
  `junior2` int(11) NOT NULL DEFAULT '0',
  `currentrep` int(11) NOT NULL DEFAULT '0',
  `totalrep` int(11) NOT NULL DEFAULT '0',
  `charmessage` varchar(128) NOT NULL DEFAULT '安安',
  `expression` int(11) NOT NULL DEFAULT '0',
  `constellation` int(11) NOT NULL DEFAULT '0',
  `blood` int(11) NOT NULL DEFAULT '0',
  `month` int(11) NOT NULL DEFAULT '0',
  `day` int(11) NOT NULL DEFAULT '0',
  `beans` int(11) NOT NULL DEFAULT '0',
  `prefix` int(255) DEFAULT '0',
  `gachexp` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `accountid` (`accountid`),
  KEY `party` (`party`),
  KEY `ranking1` (`level`,`exp`),
  KEY `ranking2` (`gm`,`job`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `character_slots`
--

CREATE TABLE IF NOT EXISTS `character_slots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accid` int(11) NOT NULL DEFAULT '0',
  `worldid` int(11) NOT NULL DEFAULT '0',
  `charslots` int(11) NOT NULL DEFAULT '6',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `cheatlog`
--

CREATE TABLE IF NOT EXISTS `cheatlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `offense` tinytext NOT NULL,
  `count` int(11) NOT NULL DEFAULT '0',
  `lastoffensetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `param` tinytext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cid` (`characterid`) USING BTREE
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=829883 ;

-- --------------------------------------------------------

--
-- 資料表結構 `csequipment`
--

CREATE TABLE IF NOT EXISTS `csequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `csitems`
--

CREATE TABLE IF NOT EXISTS `csitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(13) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `drop_data`
--

CREATE TABLE IF NOT EXISTS `drop_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dropperid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobid` (`dropperid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `drop_data_global`
--

CREATE TABLE IF NOT EXISTS `drop_data_global` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `continent` int(11) NOT NULL,
  `dropType` tinyint(1) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  `comments` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobid` (`continent`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `drop_data_vana`
--

CREATE TABLE IF NOT EXISTS `drop_data_vana` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dropperid` int(11) NOT NULL,
  `flags` set('is_mesos') NOT NULL DEFAULT '',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `minimum_quantity` int(11) NOT NULL DEFAULT '1',
  `maximum_quantity` int(11) NOT NULL DEFAULT '1',
  `questid` int(11) NOT NULL DEFAULT '0',
  `chance` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobid` (`dropperid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `dueyequipment`
--

CREATE TABLE IF NOT EXISTS `dueyequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=6 ;

--
-- 資料表的匯出資料 `dueyequipment`
--

INSERT INTO `dueyequipment` (`inventoryequipmentid`, `inventoryitemid`, `upgradeslots`, `level`, `str`, `dex`, `int`, `luk`, `hp`, `mp`, `watk`, `matk`, `wdef`, `mdef`, `acc`, `avoid`, `hands`, `speed`, `jump`, `ViciousHammer`, `itemEXP`, `durability`, `enhance`, `potential1`, `potential2`, `potential3`, `hpR`, `mpR`) VALUES
(1, 2, 7, 0, 0, 0, 0, 0, 0, 5, 0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, -5, 0, 0, 0, 0),
(2, 3, 7, 0, 0, 2, 0, 0, 0, 11, 0, 0, 28, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0),
(3, 4, 5, 0, 0, 0, 0, 3, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0),
(4, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0),
(5, 6, 7, 0, 0, 0, 0, 2, 0, 0, 0, 0, 25, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- 資料表結構 `dueyitems`
--

CREATE TABLE IF NOT EXISTS `dueyitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=9 ;

--
-- 資料表的匯出資料 `dueyitems`
--

INSERT INTO `dueyitems` (`inventoryitemid`, `characterid`, `accountid`, `packageid`, `itemid`, `inventorytype`, `position`, `quantity`, `owner`, `GM_Log`, `uniqueid`, `flag`, `expiredate`, `type`, `sender`) VALUES
(1, NULL, NULL, 1, 2060000, 2, 13, 110, '', NULL, -1, 0, -1, 6, ''),
(2, NULL, NULL, 2, 1002150, 1, 19, 1, '', NULL, -1, 0, -1, 6, ''),
(3, NULL, NULL, 3, 1060071, 1, 20, 1, '', NULL, -1, 0, -1, 6, ''),
(4, NULL, NULL, 4, 1082039, 1, 21, 1, '', NULL, -1, 0, -1, 6, ''),
(5, NULL, NULL, 5, 1072087, 1, 22, 1, '', NULL, -1, 0, -1, 6, ''),
(6, NULL, NULL, 6, 1002185, 1, 17, 1, '', NULL, -1, 0, -1, 6, ''),
(8, NULL, NULL, 8, 2040105, 2, 15, 8, '', NULL, -1, 0, -1, 6, '');

-- --------------------------------------------------------

--
-- 資料表結構 `dueypackages`
--

CREATE TABLE IF NOT EXISTS `dueypackages` (
  `PackageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `RecieverId` int(10) NOT NULL,
  `SenderName` varchar(15) NOT NULL,
  `Mesos` int(10) unsigned DEFAULT '0',
  `TimeStamp` bigint(20) unsigned DEFAULT NULL,
  `Checked` tinyint(1) unsigned DEFAULT '1',
  `Type` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=8 ;

--
-- 資料表的匯出資料 `dueypackages`
--

INSERT INTO `dueypackages` (`PackageId`, `RecieverId`, `SenderName`, `Mesos`, `TimeStamp`, `Checked`, `Type`) VALUES
(1, 21, '2005', 0, 1408974911431, 1, 2),
(7, 239, '項脊軒志', 50866, 1409231128703, 1, 3);

-- --------------------------------------------------------

--
-- 資料表結構 `eventstats`
--

CREATE TABLE IF NOT EXISTS `eventstats` (
  `eventstatid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `event` varchar(30) NOT NULL,
  `instance` varchar(30) NOT NULL,
  `characterid` int(11) NOT NULL,
  `channel` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`eventstatid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `famelog`
--

CREATE TABLE IF NOT EXISTS `famelog` (
  `famelogid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `characterid_to` int(11) NOT NULL DEFAULT '0',
  `when` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`famelogid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `families`
--

CREATE TABLE IF NOT EXISTS `families` (
  `familyid` int(11) NOT NULL AUTO_INCREMENT,
  `leaderid` int(11) NOT NULL DEFAULT '0',
  `notice` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`familyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `game_poll_reply`
--

CREATE TABLE IF NOT EXISTS `game_poll_reply` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AccountId` int(10) unsigned NOT NULL,
  `SelectAns` tinyint(5) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `gifts`
--

CREATE TABLE IF NOT EXISTS `gifts` (
  `giftid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `recipient` int(11) NOT NULL DEFAULT '0',
  `from` varchar(13) NOT NULL DEFAULT '',
  `message` varchar(255) NOT NULL DEFAULT '',
  `sn` int(11) NOT NULL DEFAULT '0',
  `uniqueid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`giftid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `gmlog`
--

CREATE TABLE IF NOT EXISTS `gmlog` (
  `gmlogid` int(11) NOT NULL AUTO_INCREMENT,
  `cid` int(11) NOT NULL DEFAULT '0',
  `command` tinytext NOT NULL,
  `mapid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gmlogid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `guilds`
--

CREATE TABLE IF NOT EXISTS `guilds` (
  `guildid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `leader` int(10) unsigned NOT NULL DEFAULT '0',
  `GP` int(11) NOT NULL DEFAULT '0',
  `logo` int(10) unsigned DEFAULT NULL,
  `logoColor` smallint(5) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `rank1title` varchar(45) NOT NULL DEFAULT '公會長',
  `rank2title` varchar(45) NOT NULL DEFAULT '公會副會長',
  `rank3title` varchar(45) NOT NULL DEFAULT '公會成員',
  `rank4title` varchar(45) NOT NULL DEFAULT '公會成員',
  `rank5title` varchar(45) NOT NULL DEFAULT '公會成員',
  `capacity` int(10) unsigned NOT NULL DEFAULT '10',
  `logoBG` int(10) unsigned DEFAULT NULL,
  `logoBGColor` smallint(5) unsigned NOT NULL DEFAULT '0',
  `notice` varchar(101) DEFAULT NULL,
  `signature` int(11) NOT NULL DEFAULT '0',
  `alliance` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`guildid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `hiredmerch`
--

CREATE TABLE IF NOT EXISTS `hiredmerch` (
  `PackageId` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT '0',
  `accountid` int(10) unsigned DEFAULT NULL,
  `Mesos` int(10) unsigned DEFAULT '0',
  `time` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`PackageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `hiredmerchequipment`
--

CREATE TABLE IF NOT EXISTS `hiredmerchequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `hiredmerchitems`
--

CREATE TABLE IF NOT EXISTS `hiredmerchitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `htsquads`
--

CREATE TABLE IF NOT EXISTS `htsquads` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channel` int(10) unsigned NOT NULL,
  `leaderid` int(10) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `members` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `inventoryequipment`
--

CREATE TABLE IF NOT EXISTS `inventoryequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `str` smallint(6) NOT NULL DEFAULT '0',
  `dex` smallint(6) NOT NULL DEFAULT '0',
  `int` smallint(6) NOT NULL DEFAULT '0',
  `luk` smallint(6) NOT NULL DEFAULT '0',
  `hp` smallint(6) NOT NULL DEFAULT '0',
  `mp` smallint(6) NOT NULL DEFAULT '0',
  `watk` smallint(6) NOT NULL DEFAULT '0',
  `matk` smallint(6) NOT NULL DEFAULT '0',
  `wdef` smallint(6) NOT NULL DEFAULT '0',
  `mdef` smallint(6) NOT NULL DEFAULT '0',
  `acc` smallint(6) NOT NULL DEFAULT '0',
  `avoid` smallint(6) NOT NULL DEFAULT '0',
  `hands` smallint(6) NOT NULL DEFAULT '0',
  `speed` smallint(6) NOT NULL DEFAULT '0',
  `jump` smallint(6) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` mediumint(9) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `inventoryitems`
--

CREATE TABLE IF NOT EXISTS `inventoryitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `inventorylog`
--

CREATE TABLE IF NOT EXISTS `inventorylog` (
  `inventorylogid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `msg` tinytext NOT NULL,
  PRIMARY KEY (`inventorylogid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `inventoryslot`
--

CREATE TABLE IF NOT EXISTS `inventoryslot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT NULL,
  `equip` tinyint(3) unsigned DEFAULT NULL,
  `use` tinyint(3) unsigned DEFAULT NULL,
  `setup` tinyint(3) unsigned DEFAULT NULL,
  `etc` tinyint(3) unsigned DEFAULT NULL,
  `cash` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `invitecodedata`
--

CREATE TABLE IF NOT EXISTS `invitecodedata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `user` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `active` int(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `ipbans`
--

CREATE TABLE IF NOT EXISTS `ipbans` (
  `ipbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`ipbanid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `ipvotelog`
--

CREATE TABLE IF NOT EXISTS `ipvotelog` (
  `vid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accid` varchar(45) NOT NULL DEFAULT '0',
  `ipaddress` varchar(30) NOT NULL DEFAULT '127.0.0.1',
  `votetime` varchar(100) NOT NULL DEFAULT '0',
  `votetype` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`vid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `keymap`
--

CREATE TABLE IF NOT EXISTS `keymap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `key` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `action` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `keymap_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `loginlog`
--

CREATE TABLE IF NOT EXISTS `loginlog` (
  `account` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `logintype` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `macbans`
--

CREATE TABLE IF NOT EXISTS `macbans` (
  `macbanid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `mac` varchar(30) NOT NULL,
  PRIMARY KEY (`macbanid`),
  UNIQUE KEY `mac_2` (`mac`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `macfilters`
--

CREATE TABLE IF NOT EXISTS `macfilters` (
  `macfilterid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filter` varchar(30) NOT NULL,
  PRIMARY KEY (`macfilterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `monsterbook`
--

CREATE TABLE IF NOT EXISTS `monsterbook` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `charid` int(10) unsigned NOT NULL DEFAULT '0',
  `cardid` int(10) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(2) unsigned DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mountdata`
--

CREATE TABLE IF NOT EXISTS `mountdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(10) unsigned DEFAULT NULL,
  `Level` int(3) unsigned NOT NULL DEFAULT '0',
  `Exp` int(10) unsigned NOT NULL DEFAULT '0',
  `Fatigue` int(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mtsequipment`
--

CREATE TABLE IF NOT EXISTS `mtsequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mtsitems`
--

CREATE TABLE IF NOT EXISTS `mtsitems` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageId` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `characterid_2` (`characterid`,`inventorytype`),
  KEY `packageid` (`packageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mtstransfer`
--

CREATE TABLE IF NOT EXISTS `mtstransfer` (
  `inventoryitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `packageid` int(11) DEFAULT NULL,
  `itemid` int(11) NOT NULL DEFAULT '0',
  `inventorytype` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL DEFAULT '0',
  `quantity` int(11) NOT NULL DEFAULT '0',
  `owner` tinytext,
  `GM_Log` tinytext,
  `uniqueid` int(11) NOT NULL DEFAULT '-1',
  `flag` int(2) NOT NULL DEFAULT '0',
  `expiredate` bigint(20) NOT NULL DEFAULT '-1',
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `sender` varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (`inventoryitemid`),
  KEY `inventoryitems_ibfk_1` (`characterid`),
  KEY `characterid` (`characterid`),
  KEY `inventorytype` (`inventorytype`),
  KEY `accountid` (`accountid`),
  KEY `packageid` (`packageid`),
  KEY `characterid_2` (`characterid`,`inventorytype`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mtstransferequipment`
--

CREATE TABLE IF NOT EXISTS `mtstransferequipment` (
  `inventoryequipmentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventoryitemid` int(10) unsigned NOT NULL DEFAULT '0',
  `upgradeslots` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `str` int(11) NOT NULL DEFAULT '0',
  `dex` int(11) NOT NULL DEFAULT '0',
  `int` int(11) NOT NULL DEFAULT '0',
  `luk` int(11) NOT NULL DEFAULT '0',
  `hp` int(11) NOT NULL DEFAULT '0',
  `mp` int(11) NOT NULL DEFAULT '0',
  `watk` int(11) NOT NULL DEFAULT '0',
  `matk` int(11) NOT NULL DEFAULT '0',
  `wdef` int(11) NOT NULL DEFAULT '0',
  `mdef` int(11) NOT NULL DEFAULT '0',
  `acc` int(11) NOT NULL DEFAULT '0',
  `avoid` int(11) NOT NULL DEFAULT '0',
  `hands` int(11) NOT NULL DEFAULT '0',
  `speed` int(11) NOT NULL DEFAULT '0',
  `jump` int(11) NOT NULL DEFAULT '0',
  `ViciousHammer` tinyint(2) NOT NULL DEFAULT '0',
  `itemEXP` int(11) NOT NULL DEFAULT '0',
  `durability` int(11) NOT NULL DEFAULT '-1',
  `enhance` tinyint(3) NOT NULL DEFAULT '0',
  `potential1` smallint(5) NOT NULL DEFAULT '0',
  `potential2` smallint(5) NOT NULL DEFAULT '0',
  `potential3` smallint(5) NOT NULL DEFAULT '0',
  `hpR` smallint(5) NOT NULL DEFAULT '0',
  `mpR` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`inventoryequipmentid`),
  KEY `inventoryitemid` (`inventoryitemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mts_cart`
--

CREATE TABLE IF NOT EXISTS `mts_cart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `mts_items`
--

CREATE TABLE IF NOT EXISTS `mts_items` (
  `id` int(11) NOT NULL,
  `tab` tinyint(1) NOT NULL DEFAULT '1',
  `price` int(11) NOT NULL DEFAULT '0',
  `characterid` int(11) NOT NULL DEFAULT '0',
  `seller` varchar(15) NOT NULL DEFAULT '',
  `expiration` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- --------------------------------------------------------

--
-- 資料表結構 `mulungdojo`
--

CREATE TABLE IF NOT EXISTS `mulungdojo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `charid` int(11) NOT NULL DEFAULT '0',
  `stage` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `notes`
--

CREATE TABLE IF NOT EXISTS `notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to` varchar(15) NOT NULL DEFAULT '',
  `from` varchar(15) NOT NULL DEFAULT '',
  `message` text NOT NULL,
  `timestamp` bigint(20) unsigned NOT NULL,
  `gift` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `nxcode`
--

CREATE TABLE IF NOT EXISTS `nxcode` (
  `code` varchar(15) NOT NULL,
  `valid` int(11) NOT NULL DEFAULT '1',
  `user` varchar(15) DEFAULT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `item` int(11) NOT NULL DEFAULT '10000',
  PRIMARY KEY (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `pets`
--

CREATE TABLE IF NOT EXISTS `pets` (
  `petid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(13) DEFAULT NULL,
  `level` int(3) unsigned NOT NULL,
  `closeness` int(6) unsigned NOT NULL,
  `fullness` int(3) unsigned NOT NULL,
  `seconds` int(11) NOT NULL DEFAULT '0',
  `flags` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`petid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `playernpcs`
--

CREATE TABLE IF NOT EXISTS `playernpcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) NOT NULL,
  `hair` int(11) NOT NULL,
  `face` int(11) NOT NULL,
  `skin` int(11) NOT NULL,
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  `scriptid` int(11) NOT NULL,
  `foothold` int(11) NOT NULL,
  `dir` tinyint(1) NOT NULL DEFAULT '0',
  `gender` tinyint(1) NOT NULL DEFAULT '0',
  `pets` varchar(25) DEFAULT '0,0,0',
  PRIMARY KEY (`id`),
  KEY `scriptid` (`scriptid`),
  KEY `playernpcs_ibfk_1` (`charid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- 資料表結構 `playernpcs_equip`
--

CREATE TABLE IF NOT EXISTS `playernpcs_equip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npcid` int(11) NOT NULL,
  `equipid` int(11) NOT NULL,
  `equippos` int(11) NOT NULL,
  `charid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `playernpcs_equip_ibfk_1` (`charid`),
  KEY `playernpcs_equip_ibfk_2` (`npcid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=26 ;

-- --------------------------------------------------------

--
-- 資料表結構 `questactions`
--

CREATE TABLE IF NOT EXISTS `questactions` (
  `questactionid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `data` blob NOT NULL,
  PRIMARY KEY (`questactionid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `questinfo`
--

CREATE TABLE IF NOT EXISTS `questinfo` (
  `questinfoid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `quest` int(6) NOT NULL DEFAULT '0',
  `customData` varchar(555) DEFAULT NULL,
  PRIMARY KEY (`questinfoid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `questrequirements`
--

CREATE TABLE IF NOT EXISTS `questrequirements` (
  `questrequirementid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `questid` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `data` blob NOT NULL,
  PRIMARY KEY (`questrequirementid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `queststatus`
--

CREATE TABLE IF NOT EXISTS `queststatus` (
  `queststatusid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `quest` int(6) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `forfeited` int(11) NOT NULL DEFAULT '0',
  `customData` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`queststatusid`),
  KEY `characterid` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `queststatusmobs`
--

CREATE TABLE IF NOT EXISTS `queststatusmobs` (
  `queststatusmobid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `queststatusid` int(10) unsigned NOT NULL DEFAULT '0',
  `mob` int(11) NOT NULL DEFAULT '0',
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`queststatusmobid`),
  KEY `queststatusid` (`queststatusid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `reactordrops`
--

CREATE TABLE IF NOT EXISTS `reactordrops` (
  `reactordropid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `reactorid` int(11) NOT NULL,
  `itemid` int(11) NOT NULL,
  `chance` int(11) NOT NULL,
  `questid` int(5) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`reactordropid`),
  KEY `reactorid` (`reactorid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=1 AUTO_INCREMENT=883 ;

--
-- 資料表的匯出資料 `reactordrops`
--

INSERT INTO `reactordrops` (`reactordropid`, `reactorid`, `itemid`, `chance`, `questid`) VALUES
(126, 1022002, 4001340, 1, 28167),
(127, 1202000, 4001366, 1, 28194),
(128, 1202003, 4001346, 1, 28225),
(129, 1202004, 4001346, 1, 28225),
(130, 1032000, 4032362, 1, 28252),
(131, 1032000, 4032363, 1, 28252),
(132, 1202004, 4032494, 1, 28255),
(1, 2001, 4031161, 1, 1008),
(2, 2001, 4031162, 1, 1008),
(3, 2001, 2010009, 2, -1),
(4, 2001, 2010000, 4, -1),
(5, 2001, 2000000, 4, -1),
(6, 2001, 2000001, 7, -1),
(7, 2001, 2000002, 10, -1),
(8, 2001, 2000003, 15, -1),
(9, 1012000, 2000000, 6, -1),
(10, 1012000, 4000003, 6, -1),
(11, 1012000, 4031150, 3, -1),
(12, 1072000, 4031165, 4, -1),
(13, 1102000, 4000136, 1, -1),
(14, 1102001, 4000136, 1, -1),
(15, 1102002, 4000136, 1, -1),
(16, 2002000, 2000002, 4, -1),
(17, 2002000, 2000001, 2, -1),
(18, 2002000, 4031198, 2, -1),
(19, 2112000, 2000004, 1, -1),
(20, 2112001, 2020001, 1, -1),
(21, 2112004, 4001016, 1, -1),
(22, 2112005, 4001015, 1, -1),
(23, 2112003, 2000005, 1, -1),
(24, 2112007, 2022001, 1, -1),
(25, 2112008, 2000004, 1, -1),
(26, 2112009, 2020001, 1, -1),
(27, 2112010, 2000005, 1, -1),
(28, 2112011, 4001016, 1, -1),
(29, 2112012, 4001015, 1, -1),
(30, 2112014, 4001018, 1, -1),
(31, 2112016, 4001113, 1, -1),
(32, 2112017, 4001114, 1, -1),
(33, 2202000, 4031094, 1, -1),
(34, 2212000, 4031142, 2, -1),
(35, 2212000, 2000002, 3, -1),
(36, 2212001, 2000002, 3, -1),
(37, 2212002, 2000002, 3, -1),
(38, 2212001, 4031141, 2, -1),
(39, 2212002, 4031143, 2, -1),
(40, 2212003, 4031107, 2, -1),
(41, 2212004, 4031116, 2, -1),
(42, 2212004, 2000001, 2, -1),
(43, 2212005, 4031136, 8, -1),
(44, 2222000, 4031231, 2, -1),
(45, 2222000, 4031258, 2, -1),
(46, 2222000, 2000002, 3, -1),
(47, 2302000, 2000001, 3, -1),
(48, 2302000, 2022040, 6, -1),
(49, 2302000, 4031274, 50, -1),
(50, 2302000, 4031275, 50, -1),
(51, 2302000, 4031276, 50, -1),
(52, 2302000, 4031277, 50, -1),
(53, 2302000, 4031278, 50, -1),
(54, 2302001, 2000002, 3, -1),
(55, 2302001, 2022040, 4, -1),
(56, 2302002, 2000001, 3, -1),
(57, 2302002, 2022040, 8, -1),
(58, 2302003, 4161017, 1, -1),
(59, 2302005, 4031508, 1, -1),
(60, 2502000, 2022116, 1, -1),
(61, 2052001, 2022116, 1, -1),
(62, 9202000, 1032033, 1, -1),
(63, 9202009, 1032033, 1, -1),
(64, 2202001, 4031092, 1, -1),
(65, 9202001, 4001025, 1, -1),
(66, 9202002, 4001037, 1, -1),
(67, 9202003, 4001029, 1, -1),
(68, 9202004, 4001030, 1, -1),
(69, 9202005, 4001031, 1, -1),
(70, 9202006, 4001032, 1, -1),
(71, 9202007, 4001033, 1, -1),
(72, 9202008, 4001034, 1, -1),
(73, 9202012, 2020014, 3, -1),
(74, 9202012, 2020015, 3, -1),
(75, 9202012, 2001001, 3, -1),
(76, 9202012, 2000004, 3, -1),
(77, 9202012, 2000005, 3, -1),
(78, 9202012, 2000001, 3, -1),
(79, 9202012, 2000002, 3, -1),
(80, 9202012, 2000006, 3, -1),
(81, 9202012, 2001002, 3, -1),
(82, 9202012, 2040504, 40, -1),
(83, 9202012, 2040501, 40, -1),
(84, 9202012, 2040513, 40, -1),
(85, 9202012, 2040516, 40, -1),
(86, 9202012, 2041007, 40, -1),
(87, 9202012, 2041010, 40, -1),
(88, 9202012, 2041004, 40, -1),
(89, 9202012, 2041001, 40, -1),
(90, 9202012, 2041019, 40, -1),
(91, 9202012, 2041022, 40, -1),
(92, 9202012, 2041013, 40, -1),
(93, 9202012, 2041016, 40, -1),
(94, 9202012, 2040301, 40, -1),
(95, 9202012, 2040704, 40, -1),
(96, 9202012, 2040707, 40, -1),
(97, 9202012, 2040701, 40, -1),
(98, 9202012, 2040804, 40, -1),
(99, 9202012, 2040801, 40, -1),
(100, 9202012, 2040004, 40, -1),
(101, 9202012, 2040001, 40, -1),
(102, 9202012, 2290009, 60, -1),
(103, 9202012, 2290031, 60, -1),
(104, 9202012, 2290039, 60, -1),
(105, 9202012, 2290033, 60, -1),
(106, 9202012, 2290045, 60, -1),
(107, 9202012, 2290081, 60, -1),
(108, 9202012, 2290083, 60, -1),
(109, 9202012, 2290087, 60, -1),
(110, 9202012, 2290060, 60, -1),
(111, 9202012, 2290073, 60, -1),
(112, 9202012, 2100000, 250, -1),
(113, 2612004, 4031703, 1, -1),
(114, 1302000, 2010000, 30, -1),
(115, 1302000, 2010009, 30, -1),
(116, 1302000, 4032268, 1, 20013),
(117, 1302000, 4032267, 1, 20013),
(118, 1052000, 4031471, 1, 6153),
(119, 2112015, 2280000, 1, -1),
(120, 1022000, 4031452, 1, -1),
(121, 2202003, 4001022, 1, -1),
(122, 2201001, 4001022, 1, -1),
(123, 1402000, 4032309, 1, -1),
(124, 1402000, 4032310, 1, -1),
(125, 1022001, 4032319, 7, -1),
(133, 1052001, 3010126, 100, -1),
(134, 1052002, 3010126, 100, -1),
(135, 1052001, 1072375, 3, -1),
(136, 1052001, 1072376, 3, -1),
(137, 1052001, 2049100, 3, -1),
(138, 1052001, 2049001, 3, -1),
(139, 1052001, 1382068, 3, -1),
(140, 1052001, 1402062, 3, -1),
(141, 1052001, 1442078, 3, -1),
(142, 1052001, 1452071, 3, -1),
(143, 1052001, 1472086, 3, -1),
(144, 1052001, 1492037, 3, -1),
(145, 1052001, 2040728, 3, -1),
(146, 1052001, 2040729, 3, -1),
(147, 1052001, 2040730, 3, -1),
(148, 1052001, 2040731, 3, -1),
(149, 1052001, 2040732, 3, -1),
(150, 1052001, 2040733, 3, -1),
(151, 1052001, 2040734, 3, -1),
(152, 1052001, 2040735, 3, -1),
(153, 1052001, 2040736, 3, -1),
(154, 1052001, 2040737, 3, -1),
(155, 1052001, 2040738, 3, -1),
(156, 1052001, 2040739, 3, -1),
(157, 1052001, 1302112, 3, -1),
(158, 1052001, 1302113, 3, -1),
(159, 1052001, 1312042, 3, -1),
(160, 1052001, 1312043, 3, -1),
(161, 1052001, 1322068, 3, -1),
(162, 1052001, 1322069, 3, -1),
(163, 1052001, 1332084, 3, -1),
(164, 1052001, 1332085, 3, -1),
(165, 1052001, 1332086, 3, -1),
(166, 1052001, 1332087, 3, -1),
(167, 1052001, 1342019, 3, -1),
(168, 1052001, 1342020, 3, -1),
(169, 1052001, 1372050, 3, -1),
(170, 1052001, 1372051, 3, -1),
(171, 1052001, 1382066, 3, -1),
(172, 1052001, 1382067, 3, -1),
(173, 1052001, 1402056, 3, -1),
(174, 1052001, 1402057, 3, -1),
(175, 1052001, 1402058, 3, -1),
(176, 1052001, 1402059, 3, -1),
(177, 1052001, 1412038, 3, -1),
(178, 1052001, 1412039, 3, -1),
(179, 1052001, 1422042, 3, -1),
(180, 1052001, 1422043, 3, -1),
(181, 1052002, 1072375, 3, -1),
(182, 1052002, 1072376, 3, -1),
(183, 1052002, 2040728, 3, -1),
(184, 1052002, 2040729, 3, -1),
(185, 1052002, 2040730, 3, -1),
(186, 1052002, 2040731, 3, -1),
(187, 1052002, 2040732, 3, -1),
(188, 1052002, 2040733, 3, -1),
(189, 1052002, 2040734, 3, -1),
(190, 1052002, 2040735, 3, -1),
(191, 1052002, 2040736, 3, -1),
(192, 1052002, 2040737, 3, -1),
(193, 1052002, 2040738, 3, -1),
(194, 1052002, 2040739, 3, -1),
(195, 1052002, 1302112, 3, -1),
(196, 1052002, 1302113, 3, -1),
(197, 1052002, 1312042, 3, -1),
(198, 1052002, 1312043, 3, -1),
(199, 1052002, 1322068, 3, -1),
(200, 1052002, 1322069, 3, -1),
(201, 1052002, 1332084, 3, -1),
(202, 1052002, 1332085, 3, -1),
(203, 1052002, 1332086, 3, -1),
(204, 1052002, 1332087, 3, -1),
(205, 1052002, 1342019, 3, -1),
(206, 1052002, 1342020, 3, -1),
(207, 1052002, 1372050, 3, -1),
(208, 1052002, 1372051, 3, -1),
(209, 1052002, 1382066, 3, -1),
(210, 1052002, 1382067, 3, -1),
(211, 1052002, 1402056, 3, -1),
(212, 1052002, 1402057, 3, -1),
(213, 1052002, 1402058, 3, -1),
(214, 1052002, 1402059, 3, -1),
(215, 1052002, 1412038, 3, -1),
(216, 1052002, 1412039, 3, -1),
(217, 1052002, 1422042, 3, -1),
(218, 1052002, 1422043, 3, -1),
(219, 1002008, 4032452, 1, 22502),
(220, 6102001, 4001260, 1, -1),
(221, 6102002, 1472051, 25, -1),
(222, 6102002, 1442056, 25, -1),
(223, 6102002, 1072228, 25, -1),
(224, 6102002, 1322062, 25, -1),
(225, 6102002, 1092061, 25, -1),
(226, 6102002, 1452019, 25, -1),
(227, 6102002, 1492012, 25, -1),
(228, 6102002, 1092050, 25, -1),
(229, 6102002, 1402005, 25, -1),
(230, 6102002, 1052131, 25, -1),
(231, 6102002, 1462016, 25, -1),
(232, 6102002, 1332051, 25, -1),
(233, 6102002, 1102043, 50, -1),
(234, 6102002, 1102206, 50, -1),
(235, 6102002, 1102260, 50, -1),
(236, 6102002, 3010010, 25, -1),
(237, 6102002, 4032015, 25, -1),
(238, 6102002, 4032016, 25, -1),
(239, 6102002, 4032017, 25, -1),
(240, 6102002, 4161018, 25, -1),
(241, 6102002, 4161021, 25, -1),
(242, 6102002, 4001107, 25, -1),
(243, 6102002, 4161015, 25, -1),
(244, 6102002, 4161016, 25, -1),
(245, 6102002, 2022121, 25, -1),
(246, 6102002, 2044803, 25, -1),
(247, 6102002, 2044903, 25, -1),
(248, 6102002, 2290071, 50, -1),
(249, 6102002, 2290027, 50, -1),
(250, 6102002, 2290111, 50, -1),
(251, 6102002, 2290118, 50, -1),
(252, 6102002, 2290103, 50, -1),
(253, 6102002, 2290047, 50, -1),
(254, 6102002, 2290091, 50, -1),
(255, 6102002, 2290053, 50, -1),
(256, 6102002, 2290061, 50, -1),
(257, 6102002, 2290011, 50, -1),
(258, 6102002, 2290089, 50, -1),
(259, 6102003, 1472051, 25, -1),
(260, 6102003, 1442056, 25, -1),
(261, 6102003, 1072228, 25, -1),
(262, 6102003, 1322062, 25, -1),
(263, 6102003, 1092061, 25, -1),
(264, 6102003, 1452019, 25, -1),
(265, 6102003, 1492012, 25, -1),
(266, 6102003, 1092050, 25, -1),
(267, 6102003, 1402005, 25, -1),
(268, 6102003, 1052131, 25, -1),
(269, 6102003, 1462016, 25, -1),
(270, 6102003, 1332051, 25, -1),
(271, 6102003, 1102043, 50, -1),
(272, 6102003, 1102206, 50, -1),
(273, 6102003, 1102260, 50, -1),
(274, 6102003, 3010010, 25, -1),
(275, 6102003, 4032015, 25, -1),
(276, 6102003, 4032016, 25, -1),
(277, 6102003, 4032017, 25, -1),
(278, 6102003, 4161018, 25, -1),
(279, 6102003, 4161021, 25, -1),
(280, 6102003, 4001107, 25, -1),
(281, 6102003, 4161015, 25, -1),
(282, 6102003, 4161016, 25, -1),
(283, 6102003, 2022121, 25, -1),
(284, 6102003, 2044803, 25, -1),
(285, 6102003, 2044903, 25, -1),
(286, 6102003, 2290071, 50, -1),
(287, 6102003, 2290027, 50, -1),
(288, 6102003, 2290111, 50, -1),
(289, 6102003, 2290118, 50, -1),
(290, 6102003, 2290103, 50, -1),
(291, 6102003, 2290047, 50, -1),
(292, 6102003, 2290091, 50, -1),
(293, 6102003, 2290053, 50, -1),
(294, 6102003, 2290061, 50, -1),
(295, 6102003, 2290011, 50, -1),
(296, 6102003, 2290089, 50, -1),
(297, 6102004, 1472051, 25, -1),
(298, 6102004, 1442056, 25, -1),
(299, 6102004, 1072228, 25, -1),
(300, 6102004, 1322062, 25, -1),
(301, 6102004, 1092061, 25, -1),
(302, 6102004, 1452019, 25, -1),
(303, 6102004, 1492012, 25, -1),
(304, 6102004, 1092050, 25, -1),
(305, 6102004, 1402005, 25, -1),
(306, 6102004, 1052131, 25, -1),
(307, 6102004, 1462016, 25, -1),
(308, 6102004, 1332051, 25, -1),
(309, 6102004, 1102043, 50, -1),
(310, 6102004, 1102206, 50, -1),
(311, 6102004, 1102260, 50, -1),
(312, 6102004, 3010010, 25, -1),
(313, 6102004, 4032015, 25, -1),
(314, 6102004, 4032016, 25, -1),
(315, 6102004, 4032017, 25, -1),
(316, 6102004, 4161018, 25, -1),
(317, 6102004, 4161021, 25, -1),
(318, 6102004, 4001107, 25, -1),
(319, 6102004, 4161015, 25, -1),
(320, 6102004, 4161016, 25, -1),
(321, 6102004, 2022121, 25, -1),
(322, 6102004, 2044803, 25, -1),
(323, 6102004, 2044903, 25, -1),
(324, 6102004, 2290071, 50, -1),
(325, 6102004, 2290027, 50, -1),
(326, 6102004, 2290111, 50, -1),
(327, 6102004, 2290118, 50, -1),
(328, 6102004, 2290103, 50, -1),
(329, 6102004, 2290047, 50, -1),
(330, 6102004, 2290091, 50, -1),
(331, 6102004, 2290053, 50, -1),
(332, 6102004, 2290061, 50, -1),
(333, 6102004, 2290011, 50, -1),
(334, 6102004, 2290089, 50, -1),
(335, 6102005, 1472051, 25, -1),
(336, 6102005, 1442056, 25, -1),
(337, 6102005, 1072228, 25, -1),
(338, 6102005, 1322062, 25, -1),
(339, 6102005, 1092061, 25, -1),
(340, 6102005, 1452019, 25, -1),
(341, 6102005, 1492012, 25, -1),
(342, 6102005, 1092050, 25, -1),
(343, 6102005, 1402005, 25, -1),
(344, 6102005, 1052131, 25, -1),
(345, 6102005, 1462016, 25, -1),
(346, 6102005, 1332051, 25, -1),
(347, 6102005, 1102043, 50, -1),
(348, 6102005, 1102206, 50, -1),
(349, 6102005, 1102260, 50, -1),
(350, 6102005, 3010010, 25, -1),
(351, 6102005, 4032015, 25, -1),
(352, 6102005, 4032016, 25, -1),
(353, 6102005, 4032017, 25, -1),
(354, 6102005, 4161018, 25, -1),
(355, 6102005, 4161021, 25, -1),
(356, 6102005, 4001107, 25, -1),
(357, 6102005, 4161015, 25, -1),
(358, 6102005, 4161016, 25, -1),
(359, 6102005, 2022121, 25, -1),
(360, 6102005, 2044803, 25, -1),
(361, 6102005, 2044903, 25, -1),
(362, 6102005, 2290071, 50, -1),
(363, 6102005, 2290027, 50, -1),
(364, 6102005, 2290111, 50, -1),
(365, 6102005, 2290118, 50, -1),
(366, 6102005, 2290103, 50, -1),
(367, 6102005, 2290047, 50, -1),
(368, 6102005, 2290091, 50, -1),
(369, 6102005, 2290053, 50, -1),
(370, 6102005, 2290061, 50, -1),
(371, 6102005, 2290011, 50, -1),
(372, 6102005, 2290089, 50, -1),
(373, 3002000, 4001162, 1, -1),
(374, 3002001, 4001163, 1, -1),
(375, 6702000, 4031595, 1, -1),
(376, 6702003, 1032043, 5, -1),
(377, 6702003, 1032044, 5, -1),
(378, 6702003, 1032045, 5, -1),
(379, 6702003, 1102099, 20, -1),
(380, 6702003, 1102100, 20, -1),
(381, 6702003, 1102101, 50, -1),
(382, 6702003, 1102102, 50, -1),
(383, 6702003, 1102103, 50, -1),
(384, 6702003, 1102104, 50, -1),
(385, 6702003, 1102105, 50, -1),
(386, 6702003, 1102106, 50, -1),
(387, 6702003, 4021007, 5, -1),
(388, 6702003, 4021008, 5, -1),
(389, 6702003, 4020007, 5, -1),
(390, 6702003, 4020007, 5, -1),
(391, 6702003, 4011006, 5, -1),
(392, 6702003, 2040759, 50, -1),
(393, 6702003, 2041035, 20, -1),
(394, 6702003, 2041037, 20, -1),
(395, 6702003, 2041039, 20, -1),
(396, 6702003, 2041041, 20, -1),
(397, 6702003, 2000005, 5, -1),
(398, 6702003, 2022179, 4, -1),
(399, 6702003, 2022180, 5, -1),
(400, 6702003, 2022181, 5, -1),
(401, 6702003, 2022182, 5, -1),
(402, 6702003, 2000005, 5, -1),
(403, 6702003, 2020010, 5, -1),
(404, 6702003, 2020013, 5, -1),
(405, 6702003, 3010011, 50, -1),
(406, 6702003, 3012000, 100, -1),
(407, 6702003, 3012005, 100, -1),
(408, 6702004, 1032043, 5, -1),
(409, 6702004, 1032044, 5, -1),
(410, 6702004, 1032045, 5, -1),
(411, 6702004, 1102099, 20, -1),
(412, 6702004, 1102100, 20, -1),
(413, 6702004, 1102101, 50, -1),
(414, 6702004, 1102102, 50, -1),
(415, 6702004, 1102103, 50, -1),
(416, 6702004, 1102104, 50, -1),
(417, 6702004, 1102105, 50, -1),
(418, 6702004, 1102106, 50, -1),
(419, 6702004, 4021007, 5, -1),
(420, 6702004, 4021008, 5, -1),
(421, 6702004, 4020007, 5, -1),
(422, 6702004, 4020007, 5, -1),
(423, 6702004, 4011006, 5, -1),
(424, 6702004, 2040759, 50, -1),
(425, 6702004, 2041035, 20, -1),
(426, 6702004, 2041037, 20, -1),
(427, 6702004, 2041039, 20, -1),
(428, 6702004, 2041041, 20, -1),
(429, 6702004, 2000005, 5, -1),
(430, 6702004, 2022179, 4, -1),
(431, 6702004, 2022180, 5, -1),
(432, 6702004, 2022181, 5, -1),
(433, 6702004, 2022182, 5, -1),
(434, 6702004, 2000005, 5, -1),
(435, 6702004, 2020010, 5, -1),
(436, 6702004, 2020013, 5, -1),
(437, 6702004, 3010011, 50, -1),
(438, 6702004, 3012000, 100, -1),
(439, 6702004, 3012005, 100, -1),
(440, 6702005, 1032043, 5, -1),
(441, 6702005, 1032044, 5, -1),
(442, 6702005, 1032045, 5, -1),
(443, 6702005, 1102099, 20, -1),
(444, 6702005, 1102100, 20, -1),
(445, 6702005, 1102101, 50, -1),
(446, 6702005, 1102102, 50, -1),
(447, 6702005, 1102103, 50, -1),
(448, 6702005, 1102104, 50, -1),
(449, 6702005, 1102105, 50, -1),
(450, 6702005, 1102106, 50, -1),
(451, 6702005, 4021007, 5, -1),
(452, 6702005, 4021008, 5, -1),
(453, 6702005, 4020007, 5, -1),
(454, 6702005, 4020007, 5, -1),
(455, 6702005, 4011006, 5, -1),
(456, 6702005, 2040759, 50, -1),
(457, 6702005, 2041035, 20, -1),
(458, 6702005, 2041037, 20, -1),
(459, 6702005, 2041039, 20, -1),
(460, 6702005, 2041041, 20, -1),
(461, 6702005, 2000005, 5, -1),
(462, 6702005, 2022179, 4, -1),
(463, 6702005, 2022180, 5, -1),
(464, 6702005, 2022181, 5, -1),
(465, 6702005, 2022182, 5, -1),
(466, 6702005, 2000005, 5, -1),
(467, 6702005, 2020010, 5, -1),
(468, 6702005, 2020013, 5, -1),
(469, 6702005, 3010011, 50, -1),
(470, 6702005, 3012000, 100, -1),
(471, 6702005, 3012005, 100, -1),
(472, 6702006, 1032043, 5, -1),
(473, 6702006, 1032044, 5, -1),
(474, 6702006, 1032045, 5, -1),
(475, 6702006, 1102099, 20, -1),
(476, 6702006, 1102100, 20, -1),
(477, 6702006, 1102101, 50, -1),
(478, 6702006, 1102102, 50, -1),
(479, 6702006, 1102103, 50, -1),
(480, 6702006, 1102104, 50, -1),
(481, 6702006, 1102105, 50, -1),
(482, 6702006, 1102106, 50, -1),
(483, 6702006, 4021007, 5, -1),
(484, 6702006, 4021008, 5, -1),
(485, 6702006, 4020007, 5, -1),
(486, 6702006, 4020007, 5, -1),
(487, 6702006, 4011006, 5, -1),
(488, 6702006, 2040759, 50, -1),
(489, 6702006, 2041035, 20, -1),
(490, 6702006, 2041037, 20, -1),
(491, 6702006, 2041039, 20, -1),
(492, 6702006, 2041041, 20, -1),
(493, 6702006, 2000005, 5, -1),
(494, 6702006, 2022179, 4, -1),
(495, 6702006, 2022180, 5, -1),
(496, 6702006, 2022181, 5, -1),
(497, 6702006, 2022182, 5, -1),
(498, 6702006, 2000005, 5, -1),
(499, 6702006, 2020010, 5, -1),
(500, 6702006, 2020013, 5, -1),
(501, 6702006, 3010011, 50, -1),
(502, 6702006, 3012000, 100, -1),
(503, 6702006, 3012005, 100, -1),
(504, 6702007, 1032043, 5, -1),
(505, 6702007, 1032044, 5, -1),
(506, 6702007, 1032045, 5, -1),
(507, 6702007, 1102099, 20, -1),
(508, 6702007, 1102100, 20, -1),
(509, 6702007, 1102101, 50, -1),
(510, 6702007, 1102102, 50, -1),
(511, 6702007, 1102103, 50, -1),
(512, 6702007, 1102104, 50, -1),
(513, 6702007, 1102105, 50, -1),
(514, 6702007, 1102106, 50, -1),
(515, 6702007, 4021007, 5, -1),
(516, 6702007, 4021008, 5, -1),
(517, 6702007, 4020007, 5, -1),
(518, 6702007, 4020007, 5, -1),
(519, 6702007, 4011006, 5, -1),
(520, 6702007, 2040759, 50, -1),
(521, 6702007, 2041035, 20, -1),
(522, 6702007, 2041037, 20, -1),
(523, 6702007, 2041039, 20, -1),
(524, 6702007, 2041041, 20, -1),
(525, 6702007, 2000005, 5, -1),
(526, 6702007, 2022179, 4, -1),
(527, 6702007, 2022180, 5, -1),
(528, 6702007, 2022181, 5, -1),
(529, 6702007, 2022182, 5, -1),
(530, 6702007, 2000005, 5, -1),
(531, 6702007, 2020010, 5, -1),
(532, 6702007, 2020013, 5, -1),
(533, 6702007, 3010011, 50, -1),
(534, 6702007, 3012000, 100, -1),
(535, 6702007, 3012005, 100, -1),
(536, 6702008, 1032043, 5, -1),
(537, 6702008, 1032044, 5, -1),
(538, 6702008, 1032045, 5, -1),
(539, 6702008, 1102099, 20, -1),
(540, 6702008, 1102100, 20, -1),
(541, 6702008, 1102101, 50, -1),
(542, 6702008, 1102102, 50, -1),
(543, 6702008, 1102103, 50, -1),
(544, 6702008, 1102104, 50, -1),
(545, 6702008, 1102105, 50, -1),
(546, 6702008, 1102106, 50, -1),
(547, 6702008, 4021007, 5, -1),
(548, 6702008, 4021008, 5, -1),
(549, 6702008, 4020007, 5, -1),
(550, 6702008, 4020007, 5, -1),
(551, 6702008, 4011006, 5, -1),
(552, 6702008, 2040759, 50, -1),
(553, 6702008, 2041035, 20, -1),
(554, 6702008, 2041037, 20, -1),
(555, 6702008, 2041039, 20, -1),
(556, 6702008, 2041041, 20, -1),
(557, 6702008, 2000005, 5, -1),
(558, 6702008, 2022179, 4, -1),
(559, 6702008, 2022180, 5, -1),
(560, 6702008, 2022181, 5, -1),
(561, 6702008, 2022182, 5, -1),
(562, 6702008, 2000005, 5, -1),
(563, 6702008, 2020010, 5, -1),
(564, 6702008, 2020013, 5, -1),
(565, 6702008, 3010011, 50, -1),
(566, 6702008, 3012000, 100, -1),
(567, 6702008, 3012005, 100, -1),
(568, 6702009, 1032043, 5, -1),
(569, 6702009, 1032044, 5, -1),
(570, 6702009, 1032045, 5, -1),
(571, 6702009, 1102099, 20, -1),
(572, 6702009, 1102100, 20, -1),
(573, 6702009, 1102101, 50, -1),
(574, 6702009, 1102102, 50, -1),
(575, 6702009, 1102103, 50, -1),
(576, 6702009, 1102104, 50, -1),
(577, 6702009, 1102105, 50, -1),
(578, 6702009, 1102106, 50, -1),
(579, 6702009, 4021007, 5, -1),
(580, 6702009, 4021008, 5, -1),
(581, 6702009, 4020007, 5, -1),
(582, 6702009, 4020007, 5, -1),
(583, 6702009, 4011006, 5, -1),
(584, 6702009, 2040759, 50, -1),
(585, 6702009, 2041035, 20, -1),
(586, 6702009, 2041037, 20, -1),
(587, 6702009, 2041039, 20, -1),
(588, 6702009, 2041041, 20, -1),
(589, 6702009, 2000005, 5, -1),
(590, 6702009, 2022179, 4, -1),
(591, 6702009, 2022180, 5, -1),
(592, 6702009, 2022181, 5, -1),
(593, 6702009, 2022182, 5, -1),
(594, 6702009, 2000005, 5, -1),
(595, 6702009, 2020010, 5, -1),
(596, 6702009, 2020013, 5, -1),
(597, 6702009, 3010011, 50, -1),
(598, 6702009, 3012000, 100, -1),
(599, 6702009, 3012005, 100, -1),
(600, 6702010, 1032043, 5, -1),
(601, 6702010, 1032044, 5, -1),
(602, 6702010, 1032045, 5, -1),
(603, 6702010, 1102099, 20, -1),
(604, 6702010, 1102100, 20, -1),
(605, 6702010, 1102101, 50, -1),
(606, 6702010, 1102102, 50, -1),
(607, 6702010, 1102103, 50, -1),
(608, 6702010, 1102104, 50, -1),
(609, 6702010, 1102105, 50, -1),
(610, 6702010, 1102106, 50, -1),
(611, 6702010, 4021007, 5, -1),
(612, 6702010, 4021008, 5, -1),
(613, 6702010, 4020007, 5, -1),
(614, 6702010, 4020007, 5, -1),
(615, 6702010, 4011006, 5, -1),
(616, 6702010, 2040759, 50, -1),
(617, 6702010, 2041035, 20, -1),
(618, 6702010, 2041037, 20, -1),
(619, 6702010, 2041039, 20, -1),
(620, 6702010, 2041041, 20, -1),
(621, 6702010, 2000005, 5, -1),
(622, 6702010, 2022179, 4, -1),
(623, 6702010, 2022180, 5, -1),
(624, 6702010, 2022181, 5, -1),
(625, 6702010, 2022182, 5, -1),
(626, 6702010, 2000005, 5, -1),
(627, 6702010, 2020010, 5, -1),
(628, 6702010, 2020013, 5, -1),
(629, 6702010, 3010011, 50, -1),
(630, 6702010, 3012000, 100, -1),
(631, 6702010, 3012005, 100, -1),
(632, 6702011, 1032043, 5, -1),
(633, 6702011, 1032044, 5, -1),
(634, 6702011, 1032045, 5, -1),
(635, 6702011, 1102099, 20, -1),
(636, 6702011, 1102100, 20, -1),
(637, 6702011, 1102101, 50, -1),
(638, 6702011, 1102102, 50, -1),
(639, 6702011, 1102103, 50, -1),
(640, 6702011, 1102104, 50, -1),
(641, 6702011, 1102105, 50, -1),
(642, 6702011, 1102106, 50, -1),
(643, 6702011, 4021007, 5, -1),
(644, 6702011, 4021008, 5, -1),
(645, 6702011, 4020007, 5, -1),
(646, 6702011, 4020007, 5, -1),
(647, 6702011, 4011006, 5, -1),
(648, 6702011, 2040759, 50, -1),
(649, 6702011, 2041035, 20, -1),
(650, 6702011, 2041037, 20, -1),
(651, 6702011, 2041039, 20, -1),
(652, 6702011, 2041041, 20, -1),
(653, 6702011, 2000005, 5, -1),
(654, 6702011, 2022179, 4, -1),
(655, 6702011, 2022180, 5, -1),
(656, 6702011, 2022181, 5, -1),
(657, 6702011, 2022182, 5, -1),
(658, 6702011, 2000005, 5, -1),
(659, 6702011, 2020010, 5, -1),
(660, 6702011, 2020013, 5, -1),
(661, 6702011, 3010011, 50, -1),
(662, 6702011, 3012000, 100, -1),
(663, 6702011, 3012005, 100, -1),
(664, 6702012, 1032043, 5, -1),
(665, 6702012, 1032044, 5, -1),
(666, 6702012, 1032045, 5, -1),
(667, 6702012, 1102099, 20, -1),
(668, 6702012, 1102100, 20, -1),
(669, 6702012, 1102101, 50, -1),
(670, 6702012, 1102102, 50, -1),
(671, 6702012, 1102103, 50, -1),
(672, 6702012, 1102104, 50, -1),
(673, 6702012, 1102105, 50, -1),
(674, 6702012, 1102106, 50, -1),
(675, 6702012, 4021007, 5, -1),
(676, 6702012, 4021008, 5, -1),
(677, 6702012, 4020007, 5, -1),
(678, 6702012, 4020007, 5, -1),
(679, 6702012, 4011006, 5, -1),
(680, 6702012, 2040759, 50, -1),
(681, 6702012, 2041035, 20, -1),
(682, 6702012, 2041037, 20, -1),
(683, 6702012, 2041039, 20, -1),
(684, 6702012, 2041041, 20, -1),
(685, 6702012, 2000005, 5, -1),
(686, 6702012, 2022179, 5, -1),
(687, 6702012, 2022180, 5, -1),
(688, 6702012, 2022181, 5, -1),
(689, 6702012, 2022182, 5, -1),
(690, 6702012, 2000005, 5, -1),
(691, 6702012, 2020010, 5, -1),
(692, 6702012, 2020013, 5, -1),
(693, 6702012, 3010011, 50, -1),
(694, 6702012, 3012000, 100, -1),
(695, 6702012, 3012005, 100, -1),
(696, 1032000, 4001363, 1, -1),
(697, 1032000, 4001362, 1, -1),
(698, 2512000, 2022131, 1, -1),
(699, 2512000, 2022132, 1, -1),
(700, 2612002, 4001134, 1, -1),
(701, 2612001, 4001135, 1, -1),
(702, 9102002, 4001100, 3, -1),
(703, 9102002, 4001095, 3, -1),
(704, 9102003, 4001095, 3, -1),
(705, 9102003, 4001096, 3, -1),
(706, 9102004, 4001096, 3, -1),
(707, 9102004, 4001097, 3, -1),
(708, 9102005, 4001097, 3, -1),
(709, 9102005, 4001098, 3, -1),
(710, 9102006, 4001098, 3, -1),
(711, 9102006, 4001099, 3, -1),
(712, 9102007, 4001099, 3, -1),
(713, 9102007, 4001100, 3, -1),
(714, 2002001, 4001063, 1, -1),
(715, 2002002, 4001052, 1, -1),
(716, 2002003, 4001055, 1, -1),
(717, 2002004, 4001056, 1, -1),
(718, 2002005, 4001057, 1, -1),
(719, 2002006, 4001058, 1, -1),
(720, 2002007, 4001059, 1, -1),
(721, 2002008, 4001060, 1, -1),
(722, 2002009, 4001061, 1, -1),
(723, 2002010, 4001062, 1, -1),
(724, 2002011, 4001046, 1, -1),
(725, 2002012, 4001047, 1, -1),
(726, 2002013, 4001049, 1, -1),
(727, 2002017, 4001158, 1, -1),
(728, 2002018, 4001158, 1, -1),
(729, 2202004, 2000004, 5, -1),
(730, 2202004, 2000005, 10, -1),
(731, 2202004, 2020008, 1, -1),
(732, 2202004, 2020009, 1, -1),
(733, 2202004, 2020012, 1, -1),
(734, 2202004, 2020013, 1, -1),
(735, 2202004, 2020014, 1, -1),
(736, 2202004, 2020015, 1, -1),
(737, 2202004, 2001000, 1, -1),
(738, 2202004, 2001001, 1, -1),
(739, 2202004, 2001002, 1, -1),
(740, 2202004, 1102000, 100, -1),
(741, 2202004, 1102001, 100, -1),
(742, 2202004, 1102002, 100, -1),
(743, 2202004, 1102003, 100, -1),
(744, 2202004, 1102004, 100, -1),
(745, 2202004, 1102040, 300, -1),
(746, 2202004, 1102041, 300, -1),
(747, 2202004, 1102042, 300, -1),
(748, 2202004, 1102043, 100, -1),
(770, 9102000, 4031157, 1, 2074),
(750, 2202004, 1032012, 30, -1),
(751, 2202004, 1032013, 30, -1),
(752, 2202004, 2020009, 1, -1),
(753, 2202004, 2020010, 1, -1),
(754, 2202004, 2022003, 1, -1),
(755, 2202004, 1302020, 200, -1),
(756, 2202004, 1302030, 200, -1),
(757, 2202004, 1332025, 200, -1),
(758, 2202004, 1382009, 200, -1),
(759, 2202004, 1382012, 200, -1),
(760, 2202004, 1412011, 200, -1),
(761, 2202004, 1422014, 200, -1),
(762, 2202004, 1432012, 200, -1),
(763, 2202004, 1442024, 200, -1),
(764, 2202004, 1452016, 200, -1),
(765, 2202004, 1452022, 200, -1),
(766, 2202004, 1462014, 200, -1),
(767, 2202004, 1462019, 200, -1),
(768, 2202004, 1472030, 200, -1),
(769, 2202004, 1472032, 200, -1),
(771, 9102001, 4031158, 1, 2074),
(772, 2002014, 1102021, 200, -1),
(773, 2002014, 1102022, 200, -1),
(774, 2002014, 1102023, 200, -1),
(775, 2002014, 1102024, 200, -1),
(776, 2002014, 1002186, 200, -1),
(777, 2002014, 1102026, 200, -1),
(778, 2002014, 1102027, 200, -1),
(779, 2002014, 1102028, 200, -1),
(780, 2002014, 1102029, 200, -1),
(781, 2002014, 1102030, 200, -1),
(782, 2002014, 1102081, 200, -1),
(783, 2002014, 1102082, 200, -1),
(784, 2002014, 1102084, 200, -1),
(785, 2002014, 1102085, 200, -1),
(786, 2002014, 1102086, 200, -1),
(787, 2002014, 1102079, 200, -1),
(788, 2002014, 1432015, 200, -1),
(789, 2002014, 1432016, 200, -1),
(790, 2002014, 1432017, 200, -1),
(791, 2002014, 1432018, 200, -1),
(792, 2002014, 1302064, 300, -1),
(793, 2002014, 1312032, 300, -1),
(794, 2002014, 1322054, 300, -1),
(795, 2002014, 1332055, 300, -1),
(796, 2002014, 1332056, 300, -1),
(797, 2002014, 1372034, 300, -1),
(798, 2002014, 1382038, 300, -1),
(799, 2002014, 1382039, 300, -1),
(800, 2002014, 1402039, 300, -1),
(801, 2002014, 1412027, 300, -1),
(802, 2002014, 1422029, 300, -1),
(803, 2002014, 1432040, 300, -1),
(804, 2002014, 1442051, 300, -1),
(805, 2002014, 1452045, 300, -1),
(806, 2002014, 1462040, 300, -1),
(807, 2002014, 1472055, 300, -1),
(808, 2002014, 1442021, 300, -1),
(809, 2002014, 1442022, 300, -1),
(810, 2002014, 1442023, 300, -1),
(811, 2002014, 2012001, 200, -1),
(812, 2002014, 2041201, 200, -1),
(830, 2002014, 2041202, 200, -1),
(831, 2002014, 2041203, 200, -1),
(832, 2002014, 2041204, 200, -1),
(833, 2002014, 2041205, 200, -1),
(834, 2002014, 2041206, 200, -1),
(835, 2002014, 2041207, 200, -1),
(836, 2002014, 2041208, 200, -1),
(837, 2002014, 2041209, 200, -1),
(838, 2002014, 2041210, 200, -1),
(839, 2002014, 2041300, 200, -1),
(840, 2002014, 2041301, 200, -1),
(841, 2002014, 2041302, 200, -1),
(842, 2002014, 2041303, 200, -1),
(843, 2002014, 2041304, 200, -1),
(844, 2002014, 2041305, 200, -1),
(845, 2002014, 2041306, 200, -1),
(846, 2002014, 2041307, 200, -1),
(847, 2002014, 2041308, 200, -1),
(848, 2002014, 2041309, 200, -1),
(849, 2002014, 2041310, 200, -1),
(850, 2002014, 2041311, 200, -1),
(851, 2002014, 2041312, 200, -1),
(852, 2002014, 2041313, 200, -1),
(853, 2002014, 2041314, 200, -1),
(854, 2002014, 2041315, 200, -1),
(855, 2002014, 2041316, 200, -1),
(856, 2002014, 2041317, 200, -1),
(857, 2002014, 2041318, 200, -1),
(858, 2002014, 2041319, 200, -1),
(859, 2002014, 2041310, 200, -1),
(860, 2002014, 2000005, 200, -1),
(861, 2002014, 1112907, 200, -1),
(862, 2002014, 1332053, 300, -1),
(863, 2002014, 1092045, 300, -1),
(864, 2002014, 1092046, 300, -1),
(865, 2002014, 1092047, 300, -1),
(866, 2002014, 1402013, 300, -1),
(867, 2202004, 1482020, 300, -1),
(868, 2202004, 1482021, 300, -1),
(869, 2202004, 1492020, 300, -1),
(870, 2202004, 1492021, 300, -1),
(871, 6802000, 3010009, 1, -1),
(872, 6802000, 3994003, 1, -1),
(873, 6802000, 3994005, 1, -1),
(874, 6802000, 3994058, 1, -1),
(875, 6802000, 3994001, 1, -1),
(876, 6802000, 3994010, 1, -1),
(877, 6802000, 3994005, 1, -1),
(878, 6802000, 3994057, 1, -1),
(879, 6802001, 3994003, 1, -1),
(880, 6802001, 3994005, 1, -1),
(881, 6802001, 3994058, 1, -1),
(882, 7422000, 4031655, 1, 8689);

-- --------------------------------------------------------

--
-- 替換檢視表以便查看 `readable_cheatlog`
--
CREATE TABLE IF NOT EXISTS `readable_cheatlog` (
`accountname` varchar(30)
,`accountid` int(11)
,`name` varchar(20)
,`characterid` int(11)
,`offense` tinytext
,`count` int(11)
,`lastoffensetime` timestamp
,`param` tinytext
);
-- --------------------------------------------------------

--
-- 替換檢視表以便查看 `readable_last_hour_cheatlog`
--
CREATE TABLE IF NOT EXISTS `readable_last_hour_cheatlog` (
`accountname` varchar(30)
,`accountid` int(11)
,`name` varchar(20)
,`characterid` int(11)
,`numrepos` decimal(32,0)
);
-- --------------------------------------------------------

--
-- 資料表結構 `regrocklocations`
--

CREATE TABLE IF NOT EXISTS `regrocklocations` (
  `trockid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=61524 ;

--
-- 資料表的匯出資料 `regrocklocations`
--

INSERT INTO `regrocklocations` (`trockid`, `characterid`, `mapid`) VALUES
(61523, 4751, 0),
(61522, 4751, 0),
(61521, 4751, 0),
(61520, 4751, 0),
(61519, 4751, 0);

-- --------------------------------------------------------

--
-- 資料表結構 `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `reporttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reporterid` int(11) NOT NULL,
  `victimid` int(11) NOT NULL,
  `reason` tinyint(4) NOT NULL,
  `chatlog` text NOT NULL,
  `status` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `rings`
--

CREATE TABLE IF NOT EXISTS `rings` (
  `ringid` int(11) NOT NULL AUTO_INCREMENT,
  `partnerRingId` int(11) NOT NULL DEFAULT '0',
  `partnerChrId` int(11) NOT NULL DEFAULT '0',
  `itemid` int(11) NOT NULL DEFAULT '0',
  `partnername` varchar(255) NOT NULL,
  PRIMARY KEY (`ringid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `savedlocations`
--

CREATE TABLE IF NOT EXISTS `savedlocations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL,
  `locationtype` int(11) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `savedlocations_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `shopitems`
--

CREATE TABLE IF NOT EXISTS `shopitems` (
  `shopitemid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `shopid` int(10) unsigned NOT NULL,
  `itemid` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `pitch` int(11) NOT NULL DEFAULT '0',
  `position` int(11) NOT NULL COMMENT 'sort is an arbitrary field designed to give leeway when modifying shops. The lowest number is 104 and it increments by 4 for each item to allow decent space for swapping/inserting/removing items.',
  `reqitem` int(11) NOT NULL,
  `reqitemq` int(11) NOT NULL,
  PRIMARY KEY (`shopitemid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `shops`
--

CREATE TABLE IF NOT EXISTS `shops` (
  `shopid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npcid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`shopid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `skillmacros`
--

CREATE TABLE IF NOT EXISTS `skillmacros` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) NOT NULL DEFAULT '0',
  `position` tinyint(11) NOT NULL DEFAULT '0',
  `skill1` int(11) NOT NULL DEFAULT '0',
  `skill2` int(11) NOT NULL DEFAULT '0',
  `skill3` int(11) NOT NULL DEFAULT '0',
  `name` varchar(60) DEFAULT NULL,
  `shout` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `skills`
--

CREATE TABLE IF NOT EXISTS `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skillid` int(11) NOT NULL DEFAULT '0',
  `characterid` int(11) NOT NULL DEFAULT '0',
  `skilllevel` tinyint(4) NOT NULL DEFAULT '0',
  `masterlevel` tinyint(4) NOT NULL DEFAULT '0',
  `expiration` bigint(20) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  KEY `skills_ibfk_1` (`characterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `skills_cooldowns`
--

CREATE TABLE IF NOT EXISTS `skills_cooldowns` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `charid` int(11) NOT NULL,
  `SkillID` int(11) NOT NULL,
  `length` bigint(20) NOT NULL,
  `StartTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `speedruns`
--

CREATE TABLE IF NOT EXISTS `speedruns` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(13) NOT NULL,
  `leader` varchar(13) NOT NULL,
  `timestring` varchar(1024) NOT NULL,
  `time` bigint(20) NOT NULL DEFAULT '0',
  `members` varchar(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `storages`
--

CREATE TABLE IF NOT EXISTS `storages` (
  `storageid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL DEFAULT '0',
  `slots` int(11) NOT NULL DEFAULT '0',
  `meso` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`storageid`),
  KEY `accountid` (`accountid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `trocklocations`
--

CREATE TABLE IF NOT EXISTS `trocklocations` (
  `trockid` int(11) NOT NULL AUTO_INCREMENT,
  `characterid` int(11) DEFAULT NULL,
  `mapid` int(11) DEFAULT NULL,
  PRIMARY KEY (`trockid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `uselog`
--

CREATE TABLE IF NOT EXISTS `uselog` (
  `account` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `time` varchar(255) DEFAULT NULL,
  `usetype` varchar(255) DEFAULT NULL,
  `active` varchar(255) DEFAULT NULL,
  `newpassword` varchar(255) DEFAULT NULL,
  `oldpassword` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `wishlist`
--

CREATE TABLE IF NOT EXISTS `wishlist` (
  `characterid` int(11) NOT NULL,
  `sn` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;

-- --------------------------------------------------------

--
-- 資料表結構 `wz_mobskilldata`
--

CREATE TABLE IF NOT EXISTS `wz_mobskilldata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skillid` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `hp` int(11) NOT NULL DEFAULT '100',
  `mpcon` int(11) NOT NULL DEFAULT '0',
  `x` int(11) NOT NULL DEFAULT '1',
  `y` int(11) NOT NULL DEFAULT '1',
  `time` int(11) NOT NULL DEFAULT '0',
  `prop` int(11) NOT NULL DEFAULT '100',
  `limit` int(11) NOT NULL DEFAULT '0',
  `spawneffect` int(11) NOT NULL DEFAULT '0',
  `interval` int(11) NOT NULL DEFAULT '0',
  `summons` varchar(1024) NOT NULL DEFAULT '',
  `ltx` int(11) NOT NULL DEFAULT '0',
  `lty` int(11) NOT NULL DEFAULT '0',
  `rbx` int(11) NOT NULL DEFAULT '0',
  `rby` int(11) NOT NULL DEFAULT '0',
  `once` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 資料表結構 `wz_oxdata`
--

CREATE TABLE IF NOT EXISTS `wz_oxdata` (
  `questionset` smallint(6) NOT NULL DEFAULT '0',
  `questionid` smallint(6) NOT NULL DEFAULT '0',
  `question` varchar(200) NOT NULL DEFAULT '',
  `display` varchar(200) NOT NULL DEFAULT '',
  `answer` enum('o','x') NOT NULL,
  PRIMARY KEY (`questionset`,`questionid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `zaksquads`
--

CREATE TABLE IF NOT EXISTS `zaksquads` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `channel` int(10) unsigned NOT NULL,
  `leaderid` int(10) unsigned NOT NULL DEFAULT '0',
  `status` int(10) unsigned NOT NULL DEFAULT '0',
  `members` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- 檢視表結構 `readable_cheatlog`
--
DROP TABLE IF EXISTS `readable_cheatlog`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `readable_cheatlog` AS select `a`.`name` AS `accountname`,`a`.`id` AS `accountid`,`c`.`name` AS `name`,`c`.`id` AS `characterid`,`cl`.`offense` AS `offense`,`cl`.`count` AS `count`,`cl`.`lastoffensetime` AS `lastoffensetime`,`cl`.`param` AS `param` from ((`cheatlog` `cl` join `characters` `c`) join `accounts` `a`) where ((`cl`.`id` = `c`.`id`) and (`a`.`id` = `c`.`accountid`) and (`a`.`banned` = 0));

-- --------------------------------------------------------

--
-- 檢視表結構 `readable_last_hour_cheatlog`
--
DROP TABLE IF EXISTS `readable_last_hour_cheatlog`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `readable_last_hour_cheatlog` AS select `a`.`name` AS `accountname`,`a`.`id` AS `accountid`,`c`.`name` AS `name`,`c`.`id` AS `characterid`,sum(`cl`.`count`) AS `numrepos` from ((`cheatlog` `cl` join `characters` `c`) join `accounts` `a`) where ((`cl`.`id` = `c`.`id`) and (`a`.`id` = `c`.`accountid`) and (timestampdiff(HOUR,`cl`.`lastoffensetime`,now()) < 1) and (`a`.`banned` = 0)) group by `cl`.`id` order by sum(`cl`.`count`) desc;

--
-- 已匯出資料表的限制(Constraint)
--

--
-- 資料表的 Constraints `buddies`
--
ALTER TABLE `buddies`
  ADD CONSTRAINT `buddies_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `csequipment`
--
ALTER TABLE `csequipment`
  ADD CONSTRAINT `csequiptment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `csitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `dueyequipment`
--
ALTER TABLE `dueyequipment`
  ADD CONSTRAINT `dueyequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `dueyitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `famelog`
--
ALTER TABLE `famelog`
  ADD CONSTRAINT `famelog_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `hiredmerchequipment`
--
ALTER TABLE `hiredmerchequipment`
  ADD CONSTRAINT `hiredmerchantequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `hiredmerchitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `inventoryequipment`
--
ALTER TABLE `inventoryequipment`
  ADD CONSTRAINT `inventoryequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `inventoryitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `inventorylog`
--
ALTER TABLE `inventorylog`
  ADD CONSTRAINT `inventorylog_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `inventoryitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `keymap`
--
ALTER TABLE `keymap`
  ADD CONSTRAINT `keymap_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `mtsequipment`
--
ALTER TABLE `mtsequipment`
  ADD CONSTRAINT `mtsequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `mtsitems` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `mtstransferequipment`
--
ALTER TABLE `mtstransferequipment`
  ADD CONSTRAINT `mtstransferequipment_ibfk_1` FOREIGN KEY (`inventoryitemid`) REFERENCES `mtstransfer` (`inventoryitemid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `playernpcs`
--
ALTER TABLE `playernpcs`
  ADD CONSTRAINT `playernpcs_ibfk_1` FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `playernpcs_equip`
--
ALTER TABLE `playernpcs_equip`
  ADD CONSTRAINT `playernpcs_equip_ibfk_1` FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `playernpcs_equip_ibfk_2` FOREIGN KEY (`npcid`) REFERENCES `playernpcs` (`scriptid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `questinfo`
--
ALTER TABLE `questinfo`
  ADD CONSTRAINT `questsinfo_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `queststatus`
--
ALTER TABLE `queststatus`
  ADD CONSTRAINT `queststatus_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `queststatusmobs`
--
ALTER TABLE `queststatusmobs`
  ADD CONSTRAINT `queststatusmobs_ibfk_1` FOREIGN KEY (`queststatusid`) REFERENCES `queststatus` (`queststatusid`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `savedlocations`
--
ALTER TABLE `savedlocations`
  ADD CONSTRAINT `savedlocations_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `skills`
--
ALTER TABLE `skills`
  ADD CONSTRAINT `skills_ibfk_1` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE;

--
-- 資料表的 Constraints `storages`
--
ALTER TABLE `storages`
  ADD CONSTRAINT `storages_ibfk_1` FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
