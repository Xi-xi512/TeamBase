

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- 创建菜品表（menu_date：该菜品对应的供餐/可订日期，与订单 date 一致）
CREATE TABLE IF NOT EXISTS dish (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(255),
    menu_date VARCHAR(20) NOT NULL
);

-- 订单表（与 MyBatis OrderMapper 中表名 canteen_order 一致）
CREATE TABLE IF NOT EXISTS canteen_order (
    order_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    dish_id VARCHAR(50) NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    portion VARCHAR(20) NOT NULL,
    price DOUBLE NOT NULL,
    `date` VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES user(username),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
);

-- 已有库升级示例（按需执行）：
-- ALTER TABLE dish ADD COLUMN menu_date VARCHAR(20) NULL;
-- UPDATE dish SET menu_date = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d') WHERE menu_date IS NULL;
-- ALTER TABLE dish MODIFY menu_date VARCHAR(20) NOT NULL;
-- ALTER TABLE canteen_order ADD COLUMN create_time DATETIME NULL;
-- UPDATE canteen_order SET create_time = NOW() WHERE create_time IS NULL;
-- ALTER TABLE canteen_order MODIFY create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 插入测试数据
INSERT INTO user (username, password, role, name) VALUES
('admin', 'admin123', 'admin', '管理员'),
('student1', '123456', 'student', '学生1'),
('student2', '123456', 'student', '学生2');

-- 明日菜单：menu_date 使用相对「明天」的日期，便于本地直接跑通
INSERT INTO dish (id, name, price, description, menu_date) VALUES
('D001', '宫保鸡丁', 15.0, '经典川菜，香辣可口', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')),
('D002', '鱼香肉丝', 14.0, '酸甜可口，营养丰富', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')),
('D003', '红烧肉', 20.0, '肥而不腻，入口即化', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')),
('D004', '清炒时蔬', 10.0, '新鲜蔬菜，健康营养', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')),
('D005', '番茄鸡蛋', 12.0, '酸甜开胃，老少皆宜', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d'));
