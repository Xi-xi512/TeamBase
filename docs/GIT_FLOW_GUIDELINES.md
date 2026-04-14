# Git Flow 分支治理规范

## 📋 分支策略

本项目采用 **Git Flow** 工作流进行分支管理。

### 主要分支

| 分支名 | 说明 | 保护规则 |
|--------|------|----------|
| `main` | 生产环境代码，仅接受来自 develop 的合并 | 禁止直接推送，仅允许 PR 合并 |
| `develop` | 开发环境代码，日常集成主干 | 禁止直接推送，需通过 PR 合并 |

### 临时分支

| 分支类型 | 命名规范 | 来源 | 目标 | 说明 |
|---------|---------|------|------|------|
| 功能分支 | `feature/<功能名>` | develop | develop | 开发新功能 |
| 修复分支 | `fix/<问题名>` | develop | develop | 修复 bug |
| 文档分支 | `docs/<文档名>` | develop | develop | 更新文档 |

**示例：**
- `feature/user-auth`
- `feature/order-management`
- `fix/login-validation`
- `docs/api-contract`

---

## 🔄 工作流程

### 1. 创建功能分支

```bash
# 确保本地 develop 是最新的
git checkout develop
git pull origin develop

# 创建并切换到功能分支
git checkout -b feature/your-feature-name
```

### 2. 开发并提交

```bash
# 进行开发工作
# ... 编写代码 ...

# 提交更改
git add .
git commit -m "feat: 添加用户登录功能"
```

### 3. 同步主干（反向同步）

在发起 PR 前，**必须**先将最新的 develop 合并到功能分支，解决冲突：

```bash
# 切换到功能分支
git checkout feature/your-feature-name

# 获取最新的 develop
git fetch origin
git merge origin/develop

# 如果有冲突，解决后提交
git add .
git commit -m "merge: 解决与 develop 的冲突"
```

### 4. 推送并创建 PR

```bash
# 推送功能分支到远程仓库
git push origin feature/your-feature-name

# 在 GitHub 上创建 Pull Request
# 目标分支: develop
# 标题格式: [Feature] 功能描述
```

### 5. PR 评审

- 至少 **1 名团队成员** 进行代码审查
- 通过 CI 自动化测试
- 解决所有评审意见
- 评审通过后合并到 develop

### 6. 删除功能分支

合并后可安全删除远程分支：

```bash
git push origin --delete feature/your-feature-name
```

---

## 📝 提交信息规范

采用 **Conventional Commits** 规范：

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Type 类型

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档更新 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 重构 |
| `test` | 测试相关 |
| `chore` | 构建/工具链相关 |

**示例：**
```
feat(auth): 添加用户登录验证
fix(order): 修复订单金额计算错误
docs(api): 更新 OpenAPI 契约文档
```

---

## ✅ PR 检查清单

发起 PR 前请确认：

- [ ] 代码已通过本地编译测试
- [ ] 已同步最新 develop 分支并解决冲突
- [ ] 提交信息符合 Conventional Commits 规范
- [ ] 已添加必要的单元测试
- [ ] 已更新相关文档
- [ ] CI 流水线显示 Green Build

---

## 📊 PR 评审记录模板

### PR #1 示例

```markdown
**PR 标题**: [Feature] 添加用户登录 API
**PR 编号**: #1
**作者**: @yourname
**评审人**: @reviewer1
**日期**: 2026-04-14

**变更描述**:
- 实现用户登录接口
- 添加 JWT 认证
- 编写单元测试

**评审意见**:
1. ✅ 代码结构清晰
2. 💡 建议添加密码加密强度验证
3. ✅ 测试覆盖完整

**状态**: ✅ 已合并
```

### PR #2 示例

```markdown
**PR 标题**: [Feature] 实现订单管理模块
**PR 编号**: #2
**作者**: @yourname
**评审人**: @reviewer2
**日期**: 2026-04-15

**变更描述**:
- 订单 CRUD 操作
- 订单状态机实现
- 数据持久化

**评审意见**:
1. ✅ 符合 API 契约
2. 💡 建议优化数据库查询性能
3. ✅ 异常处理完善

**状态**: ✅ 已合并
```
