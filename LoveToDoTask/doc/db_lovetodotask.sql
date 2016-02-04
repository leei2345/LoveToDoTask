/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50546
Source Host           : 10.0.2.2:3306
Source Database       : db_lovetodotask

Target Server Type    : MYSQL
Target Server Version : 50546
File Encoding         : 65001

Date: 2016-02-04 13:31:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `tb_conf`
-- ----------------------------
DROP TABLE IF EXISTS `tb_conf`;
CREATE TABLE `tb_conf` (
  `name` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '名称主键',
  `value` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '配置值',
  `desc` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_conf
-- ----------------------------
INSERT INTO `tb_conf` VALUES ('EACHTASKNEEDSCORE', '5', '每一份任务需要积分');

-- ----------------------------
-- Table structure for `tb_receive`
-- ----------------------------
DROP TABLE IF EXISTS `tb_receive`;
CREATE TABLE `tb_receive` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `task_id` int(12) NOT NULL COMMENT '任务id',
  `receive_uid` int(12) NOT NULL COMMENT '领取人id',
  `img_id_list` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '图片idlist用逗号分隔',
  `audit_result` enum('1','0') COLLATE utf8_unicode_ci DEFAULT '0' COMMENT '审核结果',
  `ctime` datetime NOT NULL,
  `utime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_receive
-- ----------------------------
INSERT INTO `tb_receive` VALUES ('1', '8', '1', '1.jpg,2.jpg', '0', '2016-01-18 10:17:11', '2016-02-03 14:30:17');
INSERT INTO `tb_receive` VALUES ('2', '1', '2', null, '0', '2016-02-03 14:53:30', null);

-- ----------------------------
-- Table structure for `tb_task`
-- ----------------------------
DROP TABLE IF EXISTS `tb_task`;
CREATE TABLE `tb_task` (
  `id` int(12) NOT NULL AUTO_INCREMENT COMMENT '自增Id',
  `user_id` int(12) NOT NULL COMMENT '用户id',
  `app_name` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT 'app名称',
  `search_key` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '搜索关键词',
  `rank` int(4) DEFAULT '0' COMMENT 'app目前排名',
  `comment_key` varchar(32) COLLATE utf8_unicode_ci NOT NULL COMMENT '评论关键词',
  `need_score` int(12) NOT NULL COMMENT '所需积分',
  `task_count` int(4) NOT NULL DEFAULT '1' COMMENT '任务份额',
  `receive_count` int(4) DEFAULT '0' COMMENT '以领取数量',
  `complate_count` int(4) DEFAULT '0' COMMENT '已完成数量',
  `ctime` datetime NOT NULL COMMENT '发布时间',
  `dtime` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_task
-- ----------------------------
INSERT INTO `tb_task` VALUES ('7', '1', '公牛炒股', '股票', '16', '好用的股票软件', '50', '10', '0', '0', '2016-01-14 16:32:12', null);
INSERT INTO `tb_task` VALUES ('8', '2', 'test1', '管它呢', '1', '好用个屎', '50', '10', '1', '0', '2016-01-15 14:02:11', null);
INSERT INTO `tb_task` VALUES ('9', '1', 'test2', '哒哒哒', '2', '大大大叔', '50', '10', '0', '0', '2016-01-15 14:03:02', null);

-- ----------------------------
-- Table structure for `tb_user`
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `openid` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'weixin针对当前应用唯一用户标示',
  `score` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '用户目前积分',
  `nickname` varchar(128) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户昵称',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 1 男 2女',
  `province` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '省份',
  `city` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '省份',
  `country` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `headimgurl` varchar(128) COLLATE utf8_unicode_ci NOT NULL COMMENT '头像',
  `unionid` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户weixin唯一标示',
  `ltime` datetime NOT NULL COMMENT '首次登陆时间',
  `utime` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1', 'asdasd', '950', 'leei', '1', '河北', '承德', '中国', 'http://www.divcss5.com/uploads/allimg/1310/1_131023133621_1.png', 'sdsd', '2016-01-13 19:09:30', '2016-01-13 19:09:32');
INSERT INTO `tb_user` VALUES ('2', 'sadasd', '1000', '三点电脑卡', '1', '北京', '北京', '中国', 'http://www.dpfile.com/gp/cms/1452490575136.jpg', 'sadasd', '2016-01-18 10:08:12', '2016-01-18 10:08:15');
