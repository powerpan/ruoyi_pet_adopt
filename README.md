# 宠物领养平台

这是一个基于 RuoYi-Vue 复用改造的宠物领养平台，当前项目已从同级旧项目 `ruoyi_pet_program/ruoyi_pet` 迁移出可启动底座，并改为独立的 `ruoyi_pet_adopt` 数据库与运行配置。

## 当前复用内容

- 后端基座：RuoYi 3.8.7、Spring Boot 2.5、Spring Security、MyBatis-Plus、Redis、MySQL。
- 管理后台：Vue2 + Element UI 的后台管理端。
- 用户端：Vue2 用户端页面、登录注册、上传、通知、宠物档案等基础能力。
- 宠物业务：宠物档案、健康档案、提醒、通知、内容审核、服务预约等模块可继续改造成领养业务。
- 本地启动：保留 `start-dev.command` / `stop-dev.command` 和 Docker 部署目录。

## 后续领养改造方向

- 待领养宠物：发布宠物来源、年龄、疫苗、绝育、性格、领养要求、状态。
- 领养申请：用户提交申请，平台或发布方审核，记录审核意见。
- 沟通与通知：申请状态变化、审核结果、回访提醒进入通知中心。
- 后台管理：待领养宠物管理、申请审核、发布方管理、回访记录。
- 用户端页面：领养大厅、领养详情、提交申请、我的申请。

## 本地配置

- 默认数据库：`ruoyi_pet_adopt`
- 默认后端端口：`8080`
- 默认后台端口：`8081`
- 默认用户端端口：`8088`
- 默认上传目录：`/Users/ericpan/ruoyi_pet_adopt/uploadPath`

初始化 SQL 位于 `sql/ruoyi_pet_adopt.sql`。
