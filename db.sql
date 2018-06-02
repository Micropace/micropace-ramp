-- 托管公众号信息表
drop table if exists `wx_app`;
create table `wx_app` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `wx_id` varchar (32) NOT NULL comment '微信公众号原始ID',
  `app_id` varchar (64) NOT NULL comment '微信公众号 app_id',
  `secret` varchar (64) NOT NULL comment '微信公众号 secret',
  `token` varchar (32) NOT NULL comment '微信公众号 token',
  `aes_key` varchar (64) NULL comment '微信公众号 aes_key',
  `wx_type` tinyint(4) NOT NULL comment '公众号类型：0:订阅号，1: 企业号，2: 服务号, 3: 小程序',
  `category` tinyint(4) not null comment '公众号所属分类：0:B类型公众号，1:C类型公众号',
  `industry` varchar (32) NULL comment '行业名称',
  `description` varchar (256) NULL comment '描述',
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- 二维码信息表
drop table if exists `qrcode`;
create table `qrcode` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_wx_app` BIGINT UNSIGNED NOT NULL comment '公众号记录ID, 这里的公众号只能是C类型',
  `scene_str` varchar(64) NOT NULL comment '场景值(数字类型的字符串), 绑定B类型公众号用户的标识',
  `type` tinyint(4) NOT NULL DEFAULT 0 comment '二维码类型, 0: 永久, 1: 临时',
  `ticket` varchar(256) NOT NULL comment '二维码的ticket',
  `wxurl` varchar(256) NOT NULL comment '二维码的url',
  `local_path` varchar(256) NOT NULL comment '下载到本地的路径',
  `filename` varchar(72) NOT NULL comment '文件名称',
  `is_bind` tinyint(4) NOT NULL comment '是否已被绑定, 0: 否, 1: 是',
  `expire` int(11) NOT NULL DEFAULT 0 comment '临时二维码的有效期',
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- C公众号用户表
drop table if exists `c_user`;
create table `c_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `openid` varchar(256) NOT NULL comment '微信OpenId',
  `unionid` varchar(256) NOT NULL comment '微信UnionId',
  `id_wx_app` BIGINT UNSIGNED NOT NULL comment '托管公众号信息表关联主键',
  `mobile` varchar(11) NULL comment '手机号',
  `nickname` varchar(32) NULL comment '用户昵称',
  `sex` tinyint(4) NULL comment '性别, 0: 未设置，1: 男，2: 女',
  `country` varchar(32) NULL comment '用户所在国家',
  `province` varchar(32) NULL comment '用户所在省份',
  `city` varchar(32) NULL comment '用户所在城市',
  `avatar` varchar(320) NULL comment '用户头像链接地址',
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 comment '用户是否取消关注公众号 0:否, 1:是',
  `created_at` datetime NULL,
  `updated_at` datetime NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- C公众号用户网页授权信息表
drop table if exists `c_access`;
create table `c_access` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_cuser` BIGINT UNSIGNED NOT NULL comment 'C类型公众号用户表主键',
  `access_token` varchar(256) NULL comment '微信网页授权 的access_token',
  `refresh_token` varchar(256) NULL comment '微信网页授权 的refresh_token',
  `token_refresh_at` datetime NULL comment '微信网页授权 access_token刷新时间',
  `expire` int(11) NOT NULL DEFAULT 0 comment '微信网页授权 的access_token有效时间',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- B公众号用户表
drop table if exists `b_user`;
create table `b_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `openid` varchar(256) NOT NULL comment '微信OpenId',
  `unionid` varchar(256) NOT NULL comment '微信UnionId',
  `id_wx_app` BIGINT UNSIGNED NOT NULL comment 'wx_app表关联主键',
  -- 二维码绑定相关字段
  `id_qrcode` BIGINT UNSIGNED NULL comment 'qrcode表关联主键',
  `bind_at` datetime NULL comment '绑定二维码的时间',
  `status` tinyint(4) NOT NULL DEFAULT 0 comment '注册状态,0:未注册, 1:审核中, 2:审核通过',
  -- 微信授权的用户信息相关字段
  `nickname` varchar(32) NULL comment '用户昵称',
  `sex` tinyint(4) NULL comment '性别, 0: 未设置，1: 男，2: 女',
  `country` varchar(32) NULL comment '用户所在国家',
  `province` varchar(32) NULL comment '用户所在省份',
  `city` varchar(32) NULL comment '用户所在城市',
  `avatar` varchar(320) NULL comment '用户头像链接地址',
  -- 用户注册相关字段
  `mobile` varchar(11) NOT NULL comment '手机号',
  `name` varchar(32) NOT NULL comment '真实姓名',
  `brand` varchar(64) NULL comment '品牌名称',
  `region` varchar(128) NULL comment '所在地区',
  `address` varchar(256) NULL comment '详细地址',
  `description` varchar(256) NULL comment '品牌描述',
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 comment '用户是否取消关注公众号 0:否, 1:是',
  `created_at` datetime NULL,
  `updated_at` datetime NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- B公众号用户网页授权信息表
drop table if exists `b_access`;
create table `b_access` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_buser` BIGINT UNSIGNED NOT NULL comment 'B类型公众号用户表主键',
  `access_token` varchar(256) NULL comment '微信网页授权 的access_token',
  `refresh_token` varchar(256) NULL comment '微信网页授权 的refresh_token',
  `token_refresh_at` datetime NULL comment '微信网页授权 access_token刷新时间',
  `expire` int(11) NOT NULL DEFAULT 0 comment '微信网页授权 的access_token有效时间',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;

-- B、C用户关系表
drop table if exists `relation`;
create table `relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_buser` BIGINT UNSIGNED NOT NULL comment 'B类型公众号用户表主键',
  `id_cuser` BIGINT UNSIGNED NOT NULL comment 'C类型公众号用户表主键',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NULL,
  PRIMARY KEY (`id`)
) engine=InnoDB auto_increment=1 default charset=utf8;
