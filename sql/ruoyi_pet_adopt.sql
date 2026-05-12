-- RuoYi Pet Adopt local development database
-- Import with: mysql -uroot -p < sql/ruoyi_pet_adopt.sql

CREATE DATABASE IF NOT EXISTS `ruoyi_pet_adopt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ruoyi_pet_adopt`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度日志表';

DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表字段';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息表';

DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度表';

DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成业务表';

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';

DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` text COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- Clean RuoYi seed data for local pet development.
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', NOW(), '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', NOW(), '', NULL, '初始化密码 123456'),
(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', NOW(), '', NULL, '深色主题theme-dark，浅色主题theme-light'),
(4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 'admin', NOW(), '', NULL, '是否开启验证码功能'),
(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', NOW(), '', NULL, '是否开启注册用户功能'),
(6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', NOW(), '', NULL, '设置登录IP黑名单限制');

INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
(100, 0, '0', '宠物领养平台', 0, 'admin', '', '', '0', '0', 'admin', NOW(), '', NULL),
(101, 100, '0,100', '运营中心', 1, 'admin', '', '', '0', '0', 'admin', NOW(), '', NULL),
(102, 100, '0,100', '审核中心', 2, 'admin', '', '', '0', '0', 'admin', NOW(), '', NULL);

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '用户性别', 'sys_user_sex', '0', 'admin', NOW(), '', NULL, '用户性别列表'),
(2, '菜单状态', 'sys_show_hide', '0', 'admin', NOW(), '', NULL, '菜单状态列表'),
(3, '系统开关', 'sys_normal_disable', '0', 'admin', NOW(), '', NULL, '系统开关列表'),
(4, '任务状态', 'sys_job_status', '0', 'admin', NOW(), '', NULL, '任务状态列表'),
(5, '任务分组', 'sys_job_group', '0', 'admin', NOW(), '', NULL, '任务分组列表'),
(6, '系统是否', 'sys_yes_no', '0', 'admin', NOW(), '', NULL, '系统是否列表'),
(7, '通知类型', 'sys_notice_type', '0', 'admin', NOW(), '', NULL, '通知类型列表'),
(8, '通知状态', 'sys_notice_status', '0', 'admin', NOW(), '', NULL, '通知状态列表'),
(9, '操作类型', 'sys_oper_type', '0', 'admin', NOW(), '', NULL, '操作类型列表'),
(10, '系统状态', 'sys_common_status', '0', 'admin', NOW(), '', NULL, '登录状态列表'),
(101, '宠物类型', 'pet_species', '0', 'admin', NOW(), '', NULL, '宠物类型'),
(102, '宠物服务类型', 'pet_service_type', '0', 'admin', NOW(), '', NULL, '宠物本地服务类型'),
(103, '审核状态', 'pet_audit_status', '0', 'admin', NOW(), '', NULL, '宠物业务审核状态');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '性别男'),
(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '性别女'),
(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '性别未知'),
(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '显示菜单'),
(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '隐藏菜单'),
(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '正常状态'),
(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '停用状态'),
(8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '系统默认是'),
(9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '系统默认否'),
(10, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', NOW(), '', NULL, '通知'),
(11, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '公告'),
(12, 1, '正常', '0', 'sys_common_status', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '正常'),
(13, 2, '停用', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '停用'),
(101, 1, '猫', 'cat', 'pet_species', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '猫'),
(102, 2, '狗', 'dog', 'pet_species', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '狗'),
(103, 3, '其他', 'other', 'pet_species', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '其他宠物'),
(110, 1, '洗护美容', 'grooming', 'pet_service_type', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '洗护美容'),
(111, 2, '寄养托管', 'boarding', 'pet_service_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '寄养托管'),
(112, 3, '医疗体检', 'medical', 'pet_service_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '医疗体检'),
(113, 4, '训练陪伴', 'training', 'pet_service_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '训练陪伴'),
(120, 1, '待审核', '0', 'pet_audit_status', '', 'warning', 'Y', '0', 'admin', NOW(), '', NULL, '待审核'),
(121, 2, '通过', '1', 'pet_audit_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '通过'),
(122, 3, '拒绝', '2', 'pet_audit_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '拒绝');

INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 'ceo', '平台负责人', 1, '0', 'admin', NOW(), '', NULL, ''),
(2, 'audit', '审核员', 2, '0', 'admin', NOW(), '', NULL, '');

INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '超级管理员'),
(2, '平台管理员', 'pet_admin', 2, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '宠物领养平台运营管理'),
(3, '普通用户', 'pet_user', 3, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '宠物主人'),
(4, '宠物博主', 'pet_blogger', 4, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '宠物博主');

INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 101, 'admin', '管理员', '00', '', '', '2', '', '', '0', '0', '', NULL, 'admin', NOW(), '', NULL, '本地管理员');

INSERT INTO `sys_user_post` (`user_id`, `post_id`) VALUES (1, 1);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '系统默认（无业务动作）', 'DEFAULT', 'ryNoParams', '0/30 * * * * ?', '3', '1', '1', 'admin', NOW(), '', NULL, '本地开发默认停用任务');

DROP TABLE IF EXISTS `sys_read_range`;
CREATE TABLE `sys_read_range` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type_id` bigint(20) NOT NULL COMMENT '兼容旧用户编辑页的类型ID',
  `allow_preview` char(1) DEFAULT '1' COMMENT '允许预览',
  `allow_download` char(1) DEFAULT '1' COMMENT '允许下载',
  PRIMARY KEY (`user_id`, `type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='兼容旧用户编辑页的阅读范围';

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '系统管理', 0, 4, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-12-18 14:30:54', 'admin', '2026-03-30 14:13:30', '系统管理目录'),
(2, '系统监控', 0, 5, 'monitor', NULL, '', 1, 0, 'M', '1', '0', '', 'monitor', 'admin', '2025-12-18 14:30:54', 'admin', '2026-03-30 14:13:55', '系统监控目录'),
(3, '系统工具', 0, 5, 'tool', NULL, '', 1, 0, 'M', '1', '0', '', 'tool', 'admin', '2025-12-18 14:30:55', 'admin', '2026-03-30 14:13:59', '系统工具目录'),
(4, '项目说明', 0, 6, 'https://doc.ruoyi.vip', NULL, '', 0, 0, 'M', '1', '0', '', 'guide', 'admin', '2025-12-18 14:30:55', 'admin', '2026-03-30 14:14:09', '若依官网地址'),
(100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-12-18 14:30:55', '', NULL, '用户管理菜单'),
(101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-12-18 14:30:55', '', NULL, '角色管理菜单'),
(102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-12-18 14:30:55', '', NULL, '菜单管理菜单'),
(103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-12-18 14:30:55', '', NULL, '部门管理菜单'),
(104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-12-18 14:30:55', '', NULL, '岗位管理菜单'),
(105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '1', '0', 'system:dict:list', 'dict', 'admin', '2025-12-18 14:30:55', 'admin', '2026-04-05 10:14:07', '字典管理菜单'),
(106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-12-18 14:30:55', 'admin', '2026-04-04 18:08:21', '参数设置菜单'),
(107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '1', '0', 'system:notice:list', 'message', 'admin', '2025-12-18 14:30:55', 'admin', '2026-03-30 14:11:25', '通知公告菜单'),
(108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '1', '0', '', 'log', 'admin', '2025-12-18 14:30:55', 'admin', '2026-03-30 14:11:09', '日志管理菜单'),
(109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-12-18 14:30:55', '', NULL, '在线用户菜单'),
(110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-12-18 14:30:55', '', NULL, '定时任务菜单'),
(111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-12-18 14:30:55', '', NULL, '数据监控菜单'),
(112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-12-18 14:30:55', '', NULL, '服务监控菜单'),
(113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-12-18 14:30:55', '', NULL, '缓存监控菜单'),
(114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-12-18 14:30:55', '', NULL, '缓存列表菜单'),
(115, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-12-18 14:30:56', '', NULL, '表单构建菜单'),
(116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-12-18 14:30:56', '', NULL, '代码生成菜单'),
(117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-12-18 14:30:56', '', NULL, '系统接口菜单'),
(500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-12-18 14:30:56', '', NULL, '操作日志菜单'),
(501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-12-18 14:30:56', '', NULL, '登录日志菜单'),
(1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-12-18 14:30:56', '', NULL, ''),
(1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1016, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1017, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1018, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1019, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1020, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1021, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1022, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1023, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1024, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1025, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1026, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1027, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1028, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1029, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1030, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-12-18 14:30:57', '', NULL, ''),
(1031, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1032, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1033, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1034, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1035, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1036, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1037, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1038, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1039, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1040, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1041, '日志导出', 500, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1042, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1043, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1044, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1045, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-12-18 14:30:58', '', NULL, ''),
(1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1049, '任务查询', 110, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1050, '任务新增', 110, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1051, '任务修改', 110, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1052, '任务删除', 110, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1053, '状态修改', 110, 5, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1054, '任务导出', 110, 6, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1055, '生成查询', 116, 1, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1056, '生成修改', 116, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1057, '生成删除', 116, 3, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1058, '导入代码', 116, 4, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1059, '预览代码', 116, 5, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-12-18 14:30:59', '', NULL, ''),
(1060, '生成代码', 116, 6, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-12-18 14:30:59', '', NULL, '');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3000, '宠物领养平台', 0, 1, 'pet', NULL, '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', NOW(), '', NULL, '宠物业务目录'),
(3001, '用户主页', 3000, 1, 'profile', 'pet/profile/index', '', 1, 0, 'C', '0', '0', 'manager:pet:profile:list', 'user', 'admin', NOW(), '', NULL, ''),
(3013, '博主认证', 3000, 2, 'blogger', 'pet/blogger/index', '', 1, 0, 'C', '0', '0', 'manager:pet:profile:review', 'peoples', 'admin', NOW(), '', NULL, ''),
(3002, '宠物档案', 3000, 3, 'pet', 'pet/pet/index', '', 1, 0, 'C', '0', '0', 'manager:pet:pet:list', 'guide', 'admin', NOW(), '', NULL, ''),
(3003, '帖子审核', 3000, 4, 'post', 'pet/post/index', '', 1, 0, 'C', '0', '0', 'manager:pet:post:list', 'documentation', 'admin', NOW(), '', NULL, ''),
(3004, '话题管理', 3000, 5, 'topic', 'pet/topic/index', '', 1, 0, 'C', '0', '0', 'manager:pet:topic:list', 'tree', 'admin', NOW(), '', NULL, ''),
(3005, '评论审核', 3000, 6, 'comment', 'pet/comment/index', '', 1, 0, 'C', '0', '0', 'manager:pet:comment:list', 'message', 'admin', NOW(), '', NULL, ''),
(3006, '商家审核', 3000, 7, 'merchant', 'pet/merchant/index', '', 1, 0, 'C', '0', '0', 'manager:pet:merchant:list', 'peoples', 'admin', NOW(), '', NULL, ''),
(3007, '服务管理', 3000, 8, 'service', 'pet/service/index', '', 1, 0, 'C', '0', '0', 'manager:pet:service:list', 'shopping', 'admin', NOW(), '', NULL, ''),
(3008, '服务咨询', 3000, 9, 'serviceRequest', 'pet/serviceRequest/index', '', 1, 0, 'C', '0', '0', 'manager:pet:serviceRequest:list', 'form', 'admin', NOW(), '', NULL, ''),
(3009, '服务评价', 3000, 10, 'review', 'pet/review/index', '', 1, 0, 'C', '0', '0', 'manager:pet:review:list', 'star', 'admin', NOW(), '', NULL, ''),
(3010, '健康档案', 3000, 11, 'health', 'pet/health/index', '', 1, 0, 'C', '0', '0', 'manager:pet:health:list', 'clipboard', 'admin', NOW(), '', NULL, ''),
(3011, '到期提醒', 3000, 12, 'reminder', 'pet/reminder/index', '', 1, 0, 'C', '0', '0', 'manager:pet:reminder:list', 'date', 'admin', NOW(), '', NULL, ''),
(3012, '数据统计', 3000, 13, 'statistics', 'pet/statistics/index', '', 1, 0, 'C', '0', '0', 'manager:pet:statistics:view', 'chart', 'admin', NOW(), '', NULL, ''),
(3101, '博主认证审核', 3013, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:profile:review', '#', 'admin', NOW(), '', NULL, ''),
(3102, '话题新增', 3004, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:topic:add', '#', 'admin', NOW(), '', NULL, ''),
(3103, '话题修改', 3004, 2, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:topic:edit', '#', 'admin', NOW(), '', NULL, ''),
(3104, '话题删除', 3004, 3, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:topic:remove', '#', 'admin', NOW(), '', NULL, ''),
(3105, '帖子审核操作', 3003, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:post:review', '#', 'admin', NOW(), '', NULL, ''),
(3111, '帖子删除', 3003, 2, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:post:remove', '#', 'admin', NOW(), '', NULL, ''),
(3106, '评论审核操作', 3005, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:comment:review', '#', 'admin', NOW(), '', NULL, ''),
(3107, '商家审核操作', 3006, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:merchant:review', '#', 'admin', NOW(), '', NULL, ''),
(3108, '服务新增', 3007, 1, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:service:add', '#', 'admin', NOW(), '', NULL, ''),
(3109, '服务修改', 3007, 2, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:service:edit', '#', 'admin', NOW(), '', NULL, ''),
(3110, '服务删除', 3007, 3, '#', '', '', 1, 0, 'F', '0', '0', 'manager:pet:service:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 3000),(2, 3001),(2, 3013),(2, 3002),(2, 3003),(2, 3004),(2, 3005),(2, 3006),(2, 3007),(2, 3008),(2, 3009),(2, 3010),(2, 3011),(2, 3012),(2, 3101),(2, 3102),(2, 3103),(2, 3104),(2, 3105),(2, 3111),(2, 3106),(2, 3107),(2, 3108),(2, 3109),(2, 3110);

DROP TABLE IF EXISTS `pet_user_profile`;
CREATE TABLE `pet_user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT 'sys_user.user_id',
  `nickname` varchar(64) DEFAULT '',
  `avatar` varchar(255) DEFAULT '',
  `bio` varchar(500) DEFAULT '',
  `homepage_cover` varchar(255) DEFAULT '',
  `blogger_status` tinyint DEFAULT 0 COMMENT '0未申请 1通过 2待审核 3拒绝',
  `follower_count` int DEFAULT 0,
  `following_count` int DEFAULT 0,
  `post_count` int DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_pet_user_profile_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物用户主页';

DROP TABLE IF EXISTS `pet_profile`;
CREATE TABLE `pet_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(64) NOT NULL,
  `species` varchar(32) DEFAULT '',
  `breed` varchar(64) DEFAULT '',
  `gender` varchar(16) DEFAULT '',
  `birthday` date DEFAULT NULL,
  `weight_kg` decimal(8,2) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT '',
  `health_status` varchar(128) DEFAULT '',
  `neutered` tinyint DEFAULT 0,
  `status` tinyint DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_pet_profile_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案';

DROP TABLE IF EXISTS `pet_topic`;
CREATE TABLE `pet_topic` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `description` varchar(500) DEFAULT '',
  `cover_url` varchar(255) DEFAULT '',
  `post_count` int DEFAULT 0,
  `status` tinyint DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_pet_topic_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话题';

DROP TABLE IF EXISTS `pet_post`;
CREATE TABLE `pet_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_id` bigint NOT NULL,
  `pet_id` bigint DEFAULT NULL,
  `title` varchar(160) DEFAULT '',
  `content` text,
  `post_type` varchar(20) DEFAULT 'image',
  `cover_url` varchar(255) DEFAULT '',
  `audit_status` tinyint DEFAULT 0,
  `visibility` tinyint DEFAULT 0,
  `status` tinyint DEFAULT 0,
  `like_count` int DEFAULT 0,
  `comment_count` int DEFAULT 0,
  `favorite_count` int DEFAULT 0,
  `view_count` int DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_pet_post_author` (`author_id`), KEY `idx_pet_post_audit` (`audit_status`, `status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子';

DROP TABLE IF EXISTS `pet_post_media`;
CREATE TABLE `pet_post_media` (`id` bigint NOT NULL AUTO_INCREMENT, `post_id` bigint NOT NULL, `media_type` varchar(20) DEFAULT 'image', `url` varchar(255) NOT NULL, `sort_order` int DEFAULT 0, PRIMARY KEY (`id`), KEY `idx_pet_post_media_post` (`post_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子媒体';
DROP TABLE IF EXISTS `pet_post_topic`;
CREATE TABLE `pet_post_topic` (`id` bigint NOT NULL AUTO_INCREMENT, `post_id` bigint NOT NULL, `topic_id` bigint NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `uk_pet_post_topic` (`post_id`, `topic_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子话题关系';

DROP TABLE IF EXISTS `pet_comment`;
CREATE TABLE `pet_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT, `post_id` bigint NOT NULL, `parent_id` bigint DEFAULT 0, `user_id` bigint NOT NULL, `content` varchar(1000) NOT NULL,
  `audit_status` tinyint DEFAULT 0, `status` tinyint DEFAULT 0, `like_count` int DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_pet_comment_post` (`post_id`, `audit_status`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论';

DROP TABLE IF EXISTS `pet_interaction`;
CREATE TABLE `pet_interaction` (`id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `target_type` varchar(32) NOT NULL, `target_id` bigint NOT NULL, `interaction_type` varchar(32) NOT NULL, `create_time` datetime DEFAULT NULL, PRIMARY KEY (`id`), UNIQUE KEY `uk_pet_interaction` (`user_id`, `target_type`, `target_id`, `interaction_type`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='互动行为';

DROP TABLE IF EXISTS `pet_merchant`;
CREATE TABLE `pet_merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint DEFAULT NULL, `name` varchar(128) NOT NULL, `contact_name` varchar(64) DEFAULT '', `phone` varchar(32) DEFAULT '',
  `city` varchar(64) DEFAULT '', `district` varchar(64) DEFAULT '', `address` varchar(255) DEFAULT '', `longitude` decimal(10,6) DEFAULT NULL, `latitude` decimal(10,6) DEFAULT NULL,
  `qualification_status` tinyint DEFAULT 0, `status` tinyint DEFAULT 1, `description` varchar(1000) DEFAULT '', `logo_url` varchar(255) DEFAULT '', `score` decimal(4,2) DEFAULT 0, `review_count` int DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), UNIQUE KEY `uk_pet_merchant_user` (`user_id`), KEY `idx_pet_merchant_geo` (`latitude`, `longitude`), KEY `idx_pet_merchant_status` (`qualification_status`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物商家';

DROP TABLE IF EXISTS `pet_merchant_qualification`;
CREATE TABLE `pet_merchant_qualification` (`id` bigint NOT NULL AUTO_INCREMENT, `merchant_id` bigint NOT NULL, `material_type` varchar(64) DEFAULT '', `material_url` varchar(255) DEFAULT '', `audit_status` tinyint DEFAULT 0, `audit_remark` varchar(500) DEFAULT '', `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_qualification_merchant` (`merchant_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家资质材料';

DROP TABLE IF EXISTS `pet_service_item`;
CREATE TABLE `pet_service_item` (
  `id` bigint NOT NULL AUTO_INCREMENT, `merchant_id` bigint NOT NULL, `service_name` varchar(128) NOT NULL, `service_type` varchar(64) DEFAULT '', `description` varchar(1000) DEFAULT '',
  `price_min` decimal(10,2) DEFAULT NULL, `price_max` decimal(10,2) DEFAULT NULL, `cover_url` varchar(255) DEFAULT '', `status` tinyint DEFAULT 0, `review_score` decimal(4,2) DEFAULT 0, `review_count` int DEFAULT 0,
  `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`), KEY `idx_pet_service_merchant` (`merchant_id`), KEY `idx_pet_service_type` (`service_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目';

DROP TABLE IF EXISTS `pet_service_request`;
CREATE TABLE `pet_service_request` (`id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `service_id` bigint NOT NULL, `merchant_id` bigint NOT NULL, `pet_id` bigint DEFAULT NULL, `contact_phone` varchar(32) DEFAULT '', `service_time` datetime DEFAULT NULL, `status` tinyint DEFAULT 0, `message` varchar(500) DEFAULT '', `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_service_request_user` (`user_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务咨询预约';

DROP TABLE IF EXISTS `pet_service_message`;
CREATE TABLE `pet_service_message` (`id` bigint NOT NULL AUTO_INCREMENT, `request_id` bigint NOT NULL, `sender_user_id` bigint NOT NULL, `sender_role` varchar(32) NOT NULL, `content` varchar(1000) NOT NULL, `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_service_message_request` (`request_id`, `create_time`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单对话';

DROP TABLE IF EXISTS `pet_service_review`;
CREATE TABLE `pet_service_review` (`id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `service_id` bigint NOT NULL, `merchant_id` bigint NOT NULL, `request_id` bigint DEFAULT NULL, `rating` tinyint NOT NULL, `content` varchar(1000) DEFAULT '', `image_urls` text, `status` tinyint DEFAULT 1, `hide_status` tinyint DEFAULT 0, `hide_reason` varchar(500) DEFAULT '', `hide_audit_reason` varchar(500) DEFAULT '', `top_flag` tinyint DEFAULT 0, `top_time` datetime DEFAULT NULL, `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_review_service` (`service_id`), KEY `idx_pet_review_merchant` (`merchant_id`), KEY `idx_pet_review_request` (`request_id`), KEY `idx_pet_review_hide` (`hide_status`), KEY `idx_pet_review_top` (`service_id`, `status`, `top_flag`, `top_time`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务评价';

DROP TABLE IF EXISTS `pet_health_record`;
CREATE TABLE `pet_health_record` (`id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `pet_id` bigint DEFAULT NULL, `record_type` varchar(64) DEFAULT '', `record_date` date DEFAULT NULL, `next_due_date` date DEFAULT NULL, `title` varchar(160) NOT NULL, `description` varchar(1000) DEFAULT '', `attachment_urls` text, `boarding_relation_id` bigint DEFAULT NULL, `source_record_id` bigint DEFAULT NULL, `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_health_user_pet` (`user_id`, `pet_id`), KEY `idx_pet_health_boarding` (`boarding_relation_id`, `source_record_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案';

DROP TABLE IF EXISTS `pet_reminder`;
CREATE TABLE `pet_reminder` (`id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `pet_id` bigint DEFAULT NULL, `reminder_type` varchar(64) DEFAULT '', `title` varchar(160) NOT NULL, `due_time` datetime NOT NULL, `status` tinyint DEFAULT 0, `repeat_rule` varchar(128) DEFAULT '', `notice_sent` tinyint DEFAULT 0, `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_reminder_due` (`user_id`, `status`, `due_time`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='到期提醒';

DROP TABLE IF EXISTS `pet_notification`;
CREATE TABLE `pet_notification` (`id` bigint NOT NULL AUTO_INCREMENT, `receiver_user_id` bigint NOT NULL, `actor_user_id` bigint DEFAULT NULL, `notice_type` varchar(64) NOT NULL, `target_type` varchar(64) DEFAULT '', `target_id` bigint DEFAULT NULL, `title` varchar(160) NOT NULL, `content` varchar(1000) DEFAULT '', `action_url` varchar(255) DEFAULT '', `status` tinyint DEFAULT 0, `read_time` datetime DEFAULT NULL, `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_notification_user_status` (`receiver_user_id`, `status`, `create_time`), KEY `idx_pet_notification_target` (`target_type`, `target_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物领养平台通知';

DROP TABLE IF EXISTS `pet_boarding_relation`;
CREATE TABLE `pet_boarding_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `merchant_id` bigint NOT NULL,
  `merchant_user_id` bigint NOT NULL,
  `owner_user_id` bigint NOT NULL,
  `owner_pet_id` bigint NOT NULL,
  `merchant_pet_id` bigint DEFAULT NULL,
  `pet_name` varchar(64) NOT NULL,
  `request_note` varchar(500) DEFAULT '',
  `status` tinyint DEFAULT 0 COMMENT '0待确认 1寄养中 2已拒绝 3客户取消 4商家取消',
  `request_time` datetime DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `cancel_time` datetime DEFAULT NULL,
  `cancel_by` bigint DEFAULT NULL,
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pet_boarding_owner` (`owner_user_id`, `status`, `create_time`),
  KEY `idx_pet_boarding_merchant` (`merchant_id`, `status`, `create_time`),
  KEY `idx_pet_boarding_pet` (`owner_pet_id`, `merchant_pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='临时寄养档案授权关系';

DROP TABLE IF EXISTS `pet_audit_record`;
CREATE TABLE `pet_audit_record` (`id` bigint NOT NULL AUTO_INCREMENT, `auditor_id` bigint NOT NULL, `target_type` varchar(32) NOT NULL, `target_id` bigint NOT NULL, `audit_status` tinyint NOT NULL, `audit_reason` varchar(500) DEFAULT '', `create_by` varchar(64) DEFAULT '', `create_time` datetime DEFAULT NULL, `update_by` varchar(64) DEFAULT '', `update_time` datetime DEFAULT NULL, `remark` varchar(500) DEFAULT NULL, PRIMARY KEY (`id`), KEY `idx_pet_audit_target` (`target_type`, `target_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录';

INSERT INTO `pet_topic` (`id`, `name`, `description`, `post_count`, `status`, `create_by`, `create_time`) VALUES
(1, '新手养宠', '新手常见问题、用品选择与日常照护经验', 0, 0, 'admin', NOW()),
(2, '健康护理', '疫苗、驱虫、体检、用药与恢复记录', 0, 0, 'admin', NOW()),
(3, '附近服务', '本地宠物商家体验与服务推荐', 0, 0, 'admin', NOW());

ALTER TABLE `sys_user_role`
  ADD PRIMARY KEY (`user_id`,`role_id`);

ALTER TABLE `sys_notice`
  ADD PRIMARY KEY (`notice_id`);

ALTER TABLE `sys_notice`
  MODIFY `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID';

ALTER TABLE `sys_job_log`
  ADD PRIMARY KEY (`job_log_id`);

ALTER TABLE `sys_job_log`
  MODIFY `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID';

ALTER TABLE `gen_table_column`
  ADD PRIMARY KEY (`column_id`);

ALTER TABLE `gen_table_column`
  MODIFY `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号', AUTO_INCREMENT=140;

ALTER TABLE `sys_role`
  ADD PRIMARY KEY (`role_id`);

ALTER TABLE `sys_role`
  MODIFY `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID', AUTO_INCREMENT=4;

ALTER TABLE `sys_oper_log`
  ADD PRIMARY KEY (`oper_id`),
  ADD KEY `idx_sys_oper_log_bt` (`business_type`),
  ADD KEY `idx_sys_oper_log_s` (`status`),
  ADD KEY `idx_sys_oper_log_ot` (`oper_time`);

ALTER TABLE `sys_oper_log`
  MODIFY `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键', AUTO_INCREMENT=789;

ALTER TABLE `sys_user`
  ADD PRIMARY KEY (`user_id`);

ALTER TABLE `sys_user`
  MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID', AUTO_INCREMENT=6;

ALTER TABLE `sys_menu`
  ADD PRIMARY KEY (`menu_id`);

ALTER TABLE `sys_menu`
  MODIFY `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID', AUTO_INCREMENT=9021;

ALTER TABLE `sys_post`
  ADD PRIMARY KEY (`post_id`);

ALTER TABLE `sys_post`
  MODIFY `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID', AUTO_INCREMENT=5;

ALTER TABLE `sys_job`
  ADD PRIMARY KEY (`job_id`,`job_name`,`job_group`);

ALTER TABLE `sys_job`
  MODIFY `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID', AUTO_INCREMENT=4;

ALTER TABLE `gen_table`
  ADD PRIMARY KEY (`table_id`);

ALTER TABLE `gen_table`
  MODIFY `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号', AUTO_INCREMENT=18;

ALTER TABLE `sys_role_menu`
  ADD PRIMARY KEY (`role_id`,`menu_id`);

ALTER TABLE `sys_role_dept`
  ADD PRIMARY KEY (`role_id`,`dept_id`);

ALTER TABLE `sys_dict_data`
  ADD PRIMARY KEY (`dict_code`);

ALTER TABLE `sys_dict_data`
  MODIFY `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码', AUTO_INCREMENT=108;

ALTER TABLE `sys_dict_type`
  ADD PRIMARY KEY (`dict_id`),
  ADD UNIQUE KEY `dict_type` (`dict_type`);

ALTER TABLE `sys_dict_type`
  MODIFY `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键', AUTO_INCREMENT=104;

ALTER TABLE `sys_user_post`
  ADD PRIMARY KEY (`user_id`,`post_id`);

ALTER TABLE `sys_dept`
  ADD PRIMARY KEY (`dept_id`);

ALTER TABLE `sys_dept`
  MODIFY `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id', AUTO_INCREMENT=110;

ALTER TABLE `sys_logininfor`
  ADD PRIMARY KEY (`info_id`),
  ADD KEY `idx_sys_logininfor_s` (`status`),
  ADD KEY `idx_sys_logininfor_lt` (`login_time`);

ALTER TABLE `sys_logininfor`
  MODIFY `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID', AUTO_INCREMENT=283;

ALTER TABLE `sys_config`
  ADD PRIMARY KEY (`config_id`);

ALTER TABLE `sys_config`
  MODIFY `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键', AUTO_INCREMENT=102;

SET FOREIGN_KEY_CHECKS = 1;
