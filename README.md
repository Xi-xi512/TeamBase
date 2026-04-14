# 校园食堂「光盘」行动智能推荐系统

## Sprint 1 - MVP 版本

### 项目简介
纯控制台版的校园食堂订餐系统，支持学生订餐和管理员统计功能。

---

### 技术栈
- **语言**: Java (JDK 8+)
- **存储**: 本地 TXT 文件
- **交互**: 控制台输入/输出

---

### 项目结构
```
canteen-system/
├── src/com/canteen/
│   ├── entity/          # 实体类
│   │   ├── User.java    # 用户实体
│   │   ├── Dish.java    # 菜品实体
│   │   └── Order.java   # 订单实体
│   ├── dao/             # 数据访问层
│   │   └── OrderDAO.java
│   ├── service/         # 业务逻辑层
│   │   ├── LoginService.java
│   │   ├── DishService.java
│   │   └── OrderService.java
│   ├── view/            # 视图层
│   │   ├── StudentView.java
│   │   └── AdminView.java
│   └── Main.java        # 主程序入口
├── data/                # 数据文件目录
│   └── orders.txt       # 订单数据文件
├── out/                 # 编译输出目录
├── build.bat            # 编译运行脚本
└── README.md            # 项目说明
```

---

### 测试账号

| 角色 | 账号 | 密码 | 姓名 |
|:--- |:--- |:--- |:--- |
| 学生 | stu1 | 123456 | 张三 |
| 学生 | stu2 | 123456 | 李四 |
| 学生 | stu3 | 123456 | 王五 |
| 管理员 | admin | 123456 | 管理员 |

---

### 功能说明

#### 学生端
1. 登录系统
2. 查看明日菜品列表
3. 选择菜品和份量（整份/半份）
4. 提交订单

#### 管理员端
1. 登录系统
2. 查看订餐统计报表
   - 订餐总人数
   - 各菜品总需求量

---

### 快速开始

#### 方式一：IntelliJ IDEA（推荐）
```
1. 打开 IDEA → File → Open → 选择 D:\canteen-system
2. 选择 JDK 21
3. 右键 src/com/canteen/Main.java → Run 'Main.main()'
```

#### 方式二：使用批处理脚本
```bash
# 双击运行或在命令行执行
build.bat
```

#### 方式三：手动编译运行
```bash
# 1. 进入项目目录
cd D:\canteen-system

# 2. 编译
javac -d out -encoding UTF-8 src\com\canteen\*.java src\com\canteen\**\*.java

# 3. 运行
cd out
java com.canteen.Main
```

---

### 数据文件说明

订单数据保存在 `data/orders.txt` 文件中，格式如下：
```
订单 ID，账号，姓名，菜品 ID，菜品名称，份量，价格，日期
ORD0001,stu1，张三，D001，红烧肉，whole,12.00,2026-03-20
ORD0002,stu2，李四，D002，宫保鸡丁，half,5.00,2026-03-20
```

---

### 注意事项
1. 确保已安装 JDK 8 或更高版本
2. 首次运行前请执行 `build.bat` 进行编译
3. 订单数据会自动保存到 `data/orders.txt`
4. 重启系统后历史数据不会丢失

---

### 开发团队
**Xi-xi512** | Sprint 1 | 2026 年 3 月
