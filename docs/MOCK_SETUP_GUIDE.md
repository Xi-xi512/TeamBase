# Mock 环境使用指南

## 📖 概述

本文档说明如何在后端 API 未完全就绪的情况下，使用 **Prism Mock 服务器** 或 **本地模拟数据** 进行前端开发和测试。

---

## 🚀 方式一：使用 Prism Mock 服务器（推荐）

### 1. 安装 Prism

```bash
# 使用 npm 安装
npm install -g @stoplight/prism-cli

# 或使用 yarn
yarn global add @stoplight/prism-cli
```

### 2. 启动 Mock 服务器

```bash
# 进入项目目录
cd TeamBase-main

# 使用 OpenAPI 契约文件启动 Mock 服务器
prism mock docs/api-contract.yaml --host 0.0.0.0 --port 4010
```

启动成功后会显示：
```
[Prism] Mock server running at http://localhost:4010
```

### 3. 访问演示页面

打开浏览器访问以下页面：

- **学生端**: 打开 `docs/mock-demo-student.html`
- **管理员端**: 打开 `docs/mock-demo-admin.html`

---

## 💻 方式二：使用本地模拟数据（无需安装）

如果不想安装 Prism，演示页面已内置本地模拟数据：

1. 直接用浏览器打开 `docs/mock-demo-student.html`
2. 页面会自动使用 `USE_LOCAL_MOCK = true` 模式
3. 所有 API 调用都会返回预设的模拟数据

### 模拟数据包括：

- ✅ 用户登录（账号: stu1, 密码: 123456）
- ✅ 菜品列表（6 个菜品）
- ✅ 订单创建与查询
- ✅ 管理员统计数据

---

## 🧪 边界测试用例

### 学生端测试

| 测试场景 | 操作 | 预期结果 |
|---------|------|---------|
| 正常登录 | 输入 stu1 / 123456 | 登录成功，显示用户信息 |
| 密码错误 | 输入 stu1 / wrong | 显示错误提示 |
| 查看菜品 | 登录成功 | 显示 6 个菜品卡片 |
| 创建订单 | 点击"立即订购" | 订单创建成功，订单列表更新 |
| 退出登录 | 点击"退出登录" | 返回登录页面 |

### 管理员端测试

| 测试场景 | 操作 | 预期结果 |
|---------|------|---------|
| 查看统计 | 打开页面 | 自动加载统计数据和图表 |
| 切换日期 | 选择不同日期 | 显示对应日期的统计数据 |
| 查看订单 | 滚动订单列表 | 显示所有订单记录 |

---

## 📋 API 契约验证

使用 Prism 启动 Mock 服务器后，可以验证 API 契约：

```bash
# 验证 OpenAPI 文件
prism validate docs/api-contract.yaml

# 查看契约详情
prism mock docs/api-contract.yaml --log-level info
```

---

## 🔧 自定义模拟数据

如需修改模拟数据，编辑演示页面中的 `MOCK_DATA` 对象：

```javascript
const MOCK_DATA = {
    user: {
        id: 'U001',
        username: 'stu1',
        name: '张三',
        role: 'STUDENT'
    },
    dishes: [
        // 添加或修改菜品
    ],
    orders: []
};
```

---

## 📊 测试报告模板

```markdown
### Mock 环境测试报告

**测试日期**: 2026-04-14
**测试人**: @yourname
**测试环境**: Mock (Prism / Local)

#### 学生端测试结果
- ✅ 登录功能: 通过
- ✅ 菜品展示: 通过
- ✅ 订单创建: 通过
- ✅ 边界测试: 通过

#### 管理员端测试结果
- ✅ 统计数据: 通过
- ✅ 图表展示: 通过
- ✅ 订单列表: 通过

#### 发现问题
1. [如有问题，在此记录]

#### 测试结论
Mock 环境下前端逻辑闭环已完成，核心功能正常运行。
```

---

## 🎯 作业提交检查清单

- [ ] Prism Mock 服务器成功启动（截图）
- [ ] 学生端页面逻辑闭环（截图）
- [ ] 管理员端页面逻辑闭环（截图）
- [ ] 边界测试用例执行记录
- [ ] OpenAPI 契约文件已提交到代码库
- [ ] 前端演示页面可正常运行
