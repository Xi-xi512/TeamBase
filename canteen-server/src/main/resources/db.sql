

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- 创建菜品表
CREATE TABLE IF NOT EXISTS dish (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    description VARCHAR(255)
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS `order` (
    order_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    dish_id VARCHAR(50) NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    portion VARCHAR(20) NOT NULL,
    price DOUBLE NOT NULL,
    date VARCHAR(20) NOT NULL,
    FOREIGN KEY (username) REFERENCES user(username),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
);

-- 插入测试数据
-- 用户数据
INSERT INTO user (username, password, role, name) VALUES
('admin', 'admin123', 'admin', '管理员'),
('student1', '123456', 'student', '学生1'),
('student2', '123456', 'student', '学生2');

-- 菜品数据
INSERT INTO dish (id, name, price, description) VALUES
('D001', '宫保鸡丁', 15.0, '经典川菜，香辣可口'),
('D002', '鱼香肉丝', 14.0, '酸甜可口，营养丰富'),
('D003', '红烧肉', 20.0, '肥而不腻，入口即化'),
('D004', '清炒时蔬', 10.0, '新鲜蔬菜，健康营养'),
('D005', '番茄鸡蛋', 12.0, '酸甜开胃，老少皆宜');
