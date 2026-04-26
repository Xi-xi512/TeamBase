# AGENTS.md

## 0) 快速上手（先看这一段）
- 这是一个前后端分离的校园订餐系统：`frontend`(Vue3) + `canteen-server`(Spring Boot 3 + MyBatis)。
- 核心链路：登录 -> 学生看明日菜单/下单 -> 管理员维护菜品与查看统计。
- 后端分层固定：`controller -> service -> dao -> mapper/sql`，禁止跨层直连。
- 新功能优先补 `Mock` 单测（控制层 `WebMvcTest`，业务层 `Mockito`）。
- 提交前至少保证：编译通过、已有测试通过、接口返回结构与现有风格一致。

## 1) 项目架构概述
- 架构：前后端分离 + 后端经典三层（Controller/Service/DAO）。
- 前端通过 HTTP 调用后端 `/api/**` 接口，不直接访问数据库。
- 后端通过 MyBatis DAO + XML mapper 访问 MySQL。
- 业务核心围绕“明日订餐”：日期、角色、权限、订单状态约束必须显式校验。

## 2) 目录结构说明
- `frontend/`: Vue3 前端工程
- `canteen-server/src/main/java/com/canteen/controller`: API 控制层
- `canteen-server/src/main/java/com/canteen/service`: 业务逻辑层
- `canteen-server/src/main/java/com/canteen/dao`: MyBatis DAO 接口
- `canteen-server/src/main/java/com/canteen/entity`: 实体模型
- `canteen-server/src/main/resources/mappers`: MyBatis XML
- `canteen-server/src/test/java/com/canteen`: 后端单元测试
- `docs/`: 课程报告、实验文档、截图证据

## 3) 核心模块职责
- `LoginController/LoginService`: 用户登录鉴权，返回用户角色(`student/admin`)。
- `DishController/DishService`: 学生侧“明日菜单”查询。
- `OrderController/OrderService`: 下单、个人订单、取消明日订单、份量更新。
- `AdminDishController`: 管理员菜品增删改查，删除前做订单关联检查。
- `ReportService`: 管理端统计报表（人数、订单量、菜品需求量）。

## 4) 渐进式工作流程（Anthropic 风格）
1. **先理解上下文**：先读目标模块的 Controller + Service + 相关测试。
2. **再做最小改动**：只改与需求直接相关文件，避免顺手重构无关代码。
3. **同步补测试**：每新增/修改业务分支，都要新增对应断言。
4. **最后统一验证**：本地运行测试，检查覆盖率与关键返回字段。

## 5) 编码规范约束
- Java 17 语法，沿用现有命名与分层，不引入新架构模式。
- Controller 只做参数校验、权限判断、响应组装；复杂逻辑放 Service。
- 返回值保持现有风格：`Map<String, Object>` + `success/message/...` 字段。
- 错误信息使用现有中文文案风格，避免中英文混杂。
- DAO 只声明数据访问；SQL 逻辑写在 mapper XML，不在 Java 拼接 SQL。
- 新增接口必须覆盖：正常路径 + 至少一个边界/失败路径。
- 测试优先：
  - Controller：`@WebMvcTest` + `@MockBean` + `MockMvc`
  - Service：`MockitoExtension` + mock DAO
- 不修改 `target/` 产物，不把构建产物当源码维护。

## 6) 禁止操作清单
- 禁止跨层调用（如 Controller 直接调 DAO）。
- 禁止绕过权限校验（学生/管理员角色判断不可省略）。
- 禁止删除或弱化已有业务约束（如“仅可取消明日订单”）。
- 禁止提交密钥、数据库密码、`.env` 等敏感信息。
- 禁止使用破坏性 Git 操作（`reset --hard`、强推）处理常规任务。
- 禁止在无测试验证情况下大规模改动核心 API。

## 7) 交付自检清单
- 改动是否仅影响目标需求范围？
- 接口字段是否与前端约定兼容（`success/message/data`）？
- 关键分支是否有测试覆盖（至少成功+失败）？
- `canteen-server` 测试是否通过，覆盖率是否满足当前任务要求？
