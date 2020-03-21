create database `myblog` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
use `myblog`;

SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- 用户基础信息表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `uid`         BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `username`    varchar(32)  NOT NULL COMMENT '用户名',
    `sex`         varchar(32)  NOT NULL default 'male' COMMENT '性别 male or female',
    `avatar_img`  varchar(255) NOT NULL COMMENT '头像地址',
    `intro`  varchar(255) NOT NULL COMMENT '介绍',
    `region`      varchar(255) NOT NULL COMMENT '省',
    `city`        varchar(255) NOT NULL COMMENT '市',
    `state`       tinyint(1)   NOT NULL default 1 COMMENT '状态 0删除',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`uid`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- 用户授权信息表
-- ----------------------------
DROP TABLE IF EXISTS `user_auth`;
CREATE TABLE `user_auth`
(
    `id`            BIGINT(11) NOT NULL AUTO_INCREMENT,
    `uid`           BIGINT(11) NOT NULL,
    `identity_type` varchar(64)         DEFAULT NULL COMMENT '登录类型 phone|email|username|github',
    `identifier`    varchar(64)         DEFAULT NULL COMMENT '标识（手机号|邮箱|用户名|第三方唯一标识）',
    `password`      varchar(64) COMMENT '密码|token',
    `state`         tinyint(1) NOT NULL default 1 COMMENT '状态 0删除',
    `create_time`   DATETIME   NOT NULL COMMENT '创建时间',
    `logged`        DATETIME            DEFAULT NULL COMMENT '上次登录时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`   BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL COMMENT '角色名',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role`
VALUES ('1', '超级管理员');
INSERT INTO `role`
VALUES ('2', '管理员');
INSERT INTO `role`
VALUES ('3', '用户');


-- ----------------------------
-- 用户角色关系表
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`      BIGINT(11) NOT NULL AUTO_INCREMENT,
    `uid`     BIGINT(11) NOT NULL COMMENT '用户id',
    `role_id` BIGINT(11) NOT NULL COMMENT '角色id',
    PRIMARY KEY (`id`),
    UNIQUE key `uid` (`uid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- 用户设置表
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`
(
  `id`      BIGINT(11) NOT NULL AUTO_INCREMENT,
  `uid`     BIGINT(11) NOT NULL COMMENT '用户id',
  `comment` smallINT(11) NOT NULL default 1 COMMENT '是否开放评论',
  `star` smallINT(11) NOT NULL default 1 COMMENT '是否开放收藏',
  PRIMARY KEY (`id`),
  UNIQUE key `uid` (`uid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;




-- ----------------------------
-- 文章
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`
(
    `id`              bigint(11)   NOT NULL AUTO_INCREMENT,
    `uid`             bigint(11)   NOT NULL COMMENT '作者id',
    `author`          varchar(255) NOT NULL COMMENT '作者名',
    `title`           varchar(255) NOT NULL COMMENT '标题',
    `content`         longtext     NOT NULL COMMENT '内容',
    `tags`            varchar(255) NOT NULL COMMENT '标签',
    `category_id`     bigint(11)   NOT NULL COMMENT '分类id',
    `img_url`         varchar(255) NOT NULL COMMENT '文章封面',
    `article_url`     varchar(255) NOT NULL COMMENT '文章地址',
    `article_summary` text         NOT NULL COMMENT '简介',
    `hits`            int(11)      NOT NULL default 0 COMMENT '点击量',
    `likes`            int(11)      NOT NULL default 0 COMMENT '点赞数',
    `privately`       tinyint(1)   NOT NULL default 0 COMMENT '是否设为私密 1私密',
    `reward`          tinyint(1)   NOT NULL default 0 COMMENT '是否开放打赏 1开启',
    `comment`         tinyint(1)   NOT NULL default 0 COMMENT '是否开放评论 1开放',
    `state`           tinyint(1)   NOT NULL default 1 COMMENT '是否删除 0删除',
    `create_time`     DATETIME     NOT NULL,
    `update_time`     DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- 评论
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`          bigint(11) NOT NULL AUTO_INCREMENT COMMENT '评论id',
    `uid`         bigint(11) NOT NULL COMMENT '用户id',
    `article_id`  bigint(11) NOT NULL COMMENT '文章id',
    `parent_id`   bigint(11) NOT NULL COMMENT '父评论id 0为评论文章',
    `gen_id`      bigint(11) NOT NULL COMMENT '主评论id 0为评论文章',
    `likes`       int(11)    NOT NULL COMMENT '点赞数量',
    `floor`       int(5) COMMENT '楼层',
    `content`     text       NOT NULL COMMENT '评论内容',
    `state`       tinyint(1) NOT NULL default 1 COMMENT '状态 0删除',
    `create_time` DATETIME   NOT NULL,
    `update_time` DATETIME   NOT NULL,
    PRIMARY KEY (`id`),
    INDEX article (article_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`        bigint(11)   NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `parent_id` bigint(11)   NOT NULL COMMENT '上级分类id 0为最上级',
    `name`      varchar(255) NOT NULL COMMENT '分类名',
    `url`       varchar(255) NOT NULL COMMENT '链接地址',
    `rank`      int(11)      NOT NULL COMMENT '同级排序',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `category`
VALUES ('-1', '0', '不分类', 'not-classified', '0');
INSERT INTO `category`
VALUES ('1', '0', 'java', 'java', '1');
INSERT INTO `category`
VALUES ('2', '0', 'web', 'web', '2');
INSERT INTO `category`
VALUES ('3', '1', 'spring', 'spring', '1');


DROP TABLE IF EXISTS `picture`;
CREATE TABLE `picture`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL COMMENT '文件名',
    `type`        varchar(255) NOT NULL COMMENT '文件类型',
    `url`         varchar(255) NOT NULL COMMENT '文件链接',
    `extra`       varchar(255) NOT NULL COMMENT '额外字段',
    `create_time` DATETIME     NOT NULL,
    `update_time` DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 29
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- 管理员操作日志
-- ----------------------------
DROP TABLE IF EXISTS `ops`;
CREATE TABLE `ops`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `username`    varchar(255) NOT NULL COMMENT '用户名',
    `msg`         varchar(255) NOT NULL COMMENT '详情',
    `create_time` DATETIME     NOT NULL,
    `update_time` DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- 消息通知
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `uid`         bigint(11)   NOT NULL COMMENT '用户id',
    `type`        smallint(11) NOT NULL COMMENT '通知分类id 1公告 2评论 3点赞 4系统通知',
    `content`     varchar(255) NOT NULL COMMENT '内容',
    `title`       varchar(255) NOT NULL COMMENT '标题',
    `state`       tinyint(1)   NOT NULL default 1 COMMENT '状态 0已读 1未读',
    `create_time` DATETIME     NOT NULL,
    `update_time` DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    INDEX state (state),
    INDEX uid (uid)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8;

-- ---------------------------------------------------------------------------

