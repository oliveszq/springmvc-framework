-- `graduation-design`.t_about definition

CREATE TABLE `t_about` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `content` text COMMENT '内容',
                           `create_time` datetime NOT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='关于信息表';


-- `graduation-design`.t_exception_log definition

CREATE TABLE `t_exception_log` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `opt_uri` varchar(255) NOT NULL COMMENT '请求接口',
                                   `opt_method` varchar(255) NOT NULL COMMENT '请求方式',
                                   `request_method` varchar(255) DEFAULT NULL COMMENT '请求方式',
                                   `request_param` varchar(2000) DEFAULT NULL COMMENT '请求参数',
                                   `opt_desc` varchar(255) DEFAULT NULL COMMENT '操作描述',
                                   `exception_info` text COMMENT '异常信息',
                                   `ip_address` varchar(255) DEFAULT NULL COMMENT 'ip',
                                   `ip_source` varchar(255) DEFAULT NULL COMMENT 'ip来源',
                                   `create_time` datetime NOT NULL COMMENT '操作时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='异常日志表';


-- `graduation-design`.t_job definition

CREATE TABLE `t_job` (
                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
                         `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
                         `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
                         `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
                         `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
                         `misfire_policy` tinyint(1) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
                         `concurrent` tinyint(1) DEFAULT '1' COMMENT '是否并发执行（0禁止 1允许）',
                         `status` tinyint(1) DEFAULT '0' COMMENT '状态（0暂停 1正常）',
                         `create_time` datetime NOT NULL COMMENT '创建时间',
                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                         `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
                         PRIMARY KEY (`id`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度表';


-- `graduation-design`.t_job_log definition

CREATE TABLE `t_job_log` (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
                             `job_id` int(11) NOT NULL COMMENT '任务ID',
                             `job_name` varchar(64) NOT NULL COMMENT '任务名称',
                             `job_group` varchar(64) NOT NULL COMMENT '任务组名',
                             `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
                             `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
                             `status` tinyint(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
                             `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                             `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志表';


-- `graduation-design`.t_menu definition

CREATE TABLE `t_menu` (
                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                          `name` varchar(20) NOT NULL COMMENT '菜单名',
                          `path` varchar(50) NOT NULL COMMENT '菜单路径',
                          `component` varchar(50) NOT NULL COMMENT '组件',
                          `icon` varchar(50) NOT NULL COMMENT '菜单图标',
                          `create_time` datetime NOT NULL COMMENT '创建时间',
                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                          `order_num` tinyint(1) NOT NULL COMMENT '排序',
                          `parent_id` int(11) DEFAULT NULL COMMENT '父id',
                          `is_hidden` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否隐藏  0否1是',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='菜单表';


-- `graduation-design`.t_operation_log definition

CREATE TABLE `t_operation_log` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                   `opt_module` varchar(20) NOT NULL COMMENT '操作模块',
                                   `opt_type` varchar(20) NOT NULL COMMENT '操作类型',
                                   `opt_uri` varchar(255) NOT NULL COMMENT '操作url',
                                   `opt_method` varchar(255) NOT NULL COMMENT '操作方法',
                                   `opt_desc` varchar(255) NOT NULL COMMENT '操作描述',
                                   `request_param` longtext COMMENT '请求参数',
                                   `request_method` varchar(20) NOT NULL COMMENT '请求方式',
                                   `response_data` longtext NOT NULL COMMENT '返回数据',
                                   `user_id` int(11) NOT NULL COMMENT '用户id',
                                   `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
                                   `ip_address` varchar(255) NOT NULL COMMENT '操作ip',
                                   `ip_source` varchar(255) NOT NULL COMMENT '操作地址',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1864 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志表';


-- `graduation-design`.t_resource definition

CREATE TABLE `t_resource` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `resource_name` varchar(50) NOT NULL COMMENT '资源名',
                              `url` varchar(255) DEFAULT NULL COMMENT '权限路径',
                              `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式',
                              `parent_id` int(11) DEFAULT NULL COMMENT '父模块id',
                              `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名访问 0否 1是',
                              `create_time` datetime NOT NULL COMMENT '创建时间',
                              `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1189 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='资源表';


-- `graduation-design`.t_role definition

CREATE TABLE `t_role` (
                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                          `role_name` varchar(20) NOT NULL COMMENT '角色名',
                          `is_disable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否禁用  0否 1是',
                          `create_time` datetime NOT NULL COMMENT '创建时间',
                          `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                          `is_super` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为超级管理员',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';


-- `graduation-design`.t_role_menu definition

CREATE TABLE `t_role_menu` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `role_id` int(11) DEFAULT NULL COMMENT '角色id',
                               `menu_id` int(11) DEFAULT NULL COMMENT '菜单id',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3429 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色菜单表';


-- `graduation-design`.t_role_resource definition

CREATE TABLE `t_role_resource` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `role_id` int(11) DEFAULT NULL COMMENT '角色id',
                                   `resource_id` int(11) DEFAULT NULL COMMENT '权限id',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6264 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色资源表';


-- `graduation-design`.t_user_auth definition

CREATE TABLE `t_user_auth` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `user_info_id` int(11) NOT NULL COMMENT '用户信息id',
                               `username` varchar(50) NOT NULL COMMENT '用户名',
                               `password` varchar(100) NOT NULL COMMENT '密码',
                               `login_type` tinyint(1) NOT NULL COMMENT '登录类型',
                               `ip_address` varchar(50) DEFAULT NULL COMMENT '用户登录ip',
                               `ip_source` varchar(50) DEFAULT NULL COMMENT 'ip来源',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户授权表';


-- `graduation-design`.t_user_info definition

CREATE TABLE `t_user_info` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                               `email` varchar(50) DEFAULT NULL COMMENT '邮箱号',
                               `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
                               `avatar` varchar(1024) NOT NULL DEFAULT '' COMMENT '用户头像',
                               `intro` varchar(255) DEFAULT NULL COMMENT '用户简介',
                               `website` varchar(255) DEFAULT NULL COMMENT '个人网站',
                               `is_subscribe` tinyint(1) DEFAULT NULL COMMENT '是否订阅',
                               `is_disable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否禁用',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';


-- `graduation-design`.t_user_role definition

CREATE TABLE `t_user_role` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                               `role_id` int(11) DEFAULT NULL COMMENT '角色id',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户角色表';


-- `graduation-design`.t_website_config definition

CREATE TABLE `t_website_config` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `config` varchar(2000) DEFAULT NULL COMMENT '配置信息',
                                    `create_time` datetime NOT NULL COMMENT '创建时间',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='网站设置表';