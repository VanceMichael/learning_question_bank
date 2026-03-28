-- 题库练习网站数据库初始化脚本
-- 设置字符集为 utf8mb4 以支持完整的 Unicode 字符（包括 emoji 等特殊字符）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_unicode_ci';

CREATE DATABASE IF NOT EXISTS question_bank 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE question_bank;

-- 确保当前连接使用 utf8mb4
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `nickname` VARCHAR(50) DEFAULT '' COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色:0=学生,1=管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 科目表
CREATE TABLE IF NOT EXISTS `subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '科目名称',
    `description` VARCHAR(500) DEFAULT '' COMMENT '描述',
    `icon` VARCHAR(50) DEFAULT '' COMMENT '图标标识',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0=禁用,1=启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科目表';

-- 题目表
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `subject_id` BIGINT NOT NULL COMMENT '科目ID',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `type` TINYINT NOT NULL COMMENT '题型:1=单选,2=多选,3=判断,4=填空,5=简答',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `options` JSON DEFAULT NULL COMMENT '选项(JSON数组)',
    `answer` JSON NOT NULL COMMENT '答案(JSON)',
    `analysis` TEXT DEFAULT NULL COMMENT '解析',
    `difficulty` TINYINT NOT NULL DEFAULT 3 COMMENT '难度:1-5',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:0=草稿,1=发布',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_subject` (`subject_id`),
    KEY `idx_creator` (`creator_id`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

-- 练习记录表
CREATE TABLE IF NOT EXISTS `practice_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `subject_id` BIGINT NOT NULL COMMENT '科目ID',
    `total_count` INT NOT NULL DEFAULT 0 COMMENT '总题数',
    `correct_count` INT NOT NULL DEFAULT 0 COMMENT '正确数',
    `accuracy` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '正确率',
    `started_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `finished_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_subject` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习记录表';

-- 练习详情表
CREATE TABLE IF NOT EXISTS `practice_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `record_id` BIGINT NOT NULL COMMENT '记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `user_answer` TEXT DEFAULT NULL COMMENT '用户答案',
    `is_correct` TINYINT DEFAULT NULL COMMENT '是否正确:0=错,1=对',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '题目顺序',
    PRIMARY KEY (`id`),
    KEY `idx_record` (`record_id`),
    KEY `idx_question` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='练习详情表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT '' COMMENT '操作用户名',
    `module` VARCHAR(50) NOT NULL COMMENT '模块',
    `action` VARCHAR(50) NOT NULL COMMENT '操作',
    `detail` TEXT DEFAULT NULL COMMENT '详情',
    `ip` VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 初始数据
-- 初始用户由 DataInitializer 在应用启动时创建，密码均为 123456

INSERT INTO `subject` (`name`, `description`, `icon`, `sort_order`) VALUES
('Java', 'Java 程序设计语言', 'coffee', 1),
('Python', 'Python 程序设计语言', 'terminal', 2),
('数据结构', '数据结构与算法', 'share-2', 3),
('数据库', '数据库原理与应用', 'database', 4),
('计算机网络', '计算机网络基础', 'globe', 5);

-- Java 题目 (subject_id=1)
INSERT INTO `question` (`subject_id`, `creator_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `status`) VALUES
(1, 1, 1, 'Java中，以下哪个关键字用于定义常量？', '["var", "final", "static", "const"]', '"B"', 'final关键字用于定义常量，被final修饰的变量不能被重新赋值。', 2, 1),
(1, 1, 1, '以下哪个不是Java的基本数据类型？', '["int", "boolean", "String", "double"]', '"C"', 'String是引用类型，不是基本数据类型。Java的8种基本类型是：byte、short、int、long、float、double、char、boolean。', 2, 1),
(1, 1, 2, 'Java中，以下哪些是面向对象的特性？（多选）', '["封装", "继承", "多态", "递归"]', '["A", "B", "C"]', '面向对象的三大特性是封装、继承、多态。递归是一种编程技巧，不是面向对象特性。', 3, 1),
(1, 1, 3, 'Java是一种跨平台的编程语言。', NULL, '"true"', 'Java通过JVM（Java虚拟机）实现跨平台，一次编写到处运行。', 1, 1),
(1, 1, 4, 'Java中用于实现接口的关键字是____。', NULL, '"implements"', '类使用implements关键字来实现接口。', 2, 1);

-- Python 题目 (subject_id=2)
INSERT INTO `question` (`subject_id`, `creator_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `status`) VALUES
(2, 1, 1, 'Python中，以下哪个函数用于获取列表的长度？', '["size()", "length()", "len()", "count()"]', '"C"', 'len()是Python内置函数，用于获取序列（字符串、列表、元组等）的长度。', 1, 1),
(2, 1, 1, '以下哪个是Python中的不可变数据类型？', '["list", "dict", "set", "tuple"]', '"D"', 'tuple（元组）是不可变类型，创建后不能修改。list、dict、set都是可变类型。', 2, 1),
(2, 1, 2, 'Python中，以下哪些是合法的变量名？（多选）', '["_name", "2var", "my_var", "class"]', '["A", "C"]', '变量名不能以数字开头，不能使用保留字。_name和my_var是合法的变量名。', 2, 1),
(2, 1, 3, 'Python使用缩进来表示代码块。', NULL, '"true"', 'Python使用缩进（通常是4个空格）来表示代码块，而不是使用大括号。', 1, 1),
(2, 1, 4, 'Python中定义函数使用的关键字是____。', NULL, '"def"', '使用def关键字定义函数，如：def function_name():', 1, 1);

-- 数据结构 题目 (subject_id=3)
INSERT INTO `question` (`subject_id`, `creator_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `status`) VALUES
(3, 1, 1, '以下哪种数据结构是先进后出（LIFO）的？', '["队列", "栈", "链表", "数组"]', '"B"', '栈（Stack）是后进先出（LIFO）的数据结构，最后入栈的元素最先出栈。', 2, 1),
(3, 1, 1, '二叉树的前序遍历顺序是？', '["左-根-右", "根-左-右", "左-右-根", "根-右-左"]', '"B"', '前序遍历的顺序是：根节点 -> 左子树 -> 右子树。', 3, 1),
(3, 1, 2, '以下哪些排序算法的平均时间复杂度是O(nlogn)？（多选）', '["冒泡排序", "快速排序", "归并排序", "插入排序"]', '["B", "C"]', '快速排序和归并排序的平均时间复杂度都是O(nlogn)，冒泡排序和插入排序是O(n²)。', 4, 1),
(3, 1, 3, '链表的插入和删除操作时间复杂度是O(1)。', NULL, '"true"', '在已知位置的情况下，链表的插入和删除只需要修改指针，时间复杂度为O(1)。', 2, 1),
(3, 1, 5, '请简述栈和队列的区别。', NULL, '"栈是后进先出，队列是先进先出"', '栈（Stack）遵循LIFO原则，队列（Queue）遵循FIFO原则，这是它们最本质的区别。', 3, 1);

-- 数据库 题目 (subject_id=4)
INSERT INTO `question` (`subject_id`, `creator_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `status`) VALUES
(4, 1, 1, 'SQL中，用于查询数据的关键字是？', '["INSERT", "UPDATE", "SELECT", "DELETE"]', '"C"', 'SELECT语句用于从数据库表中查询数据。', 1, 1),
(4, 1, 1, '以下哪个不是数据库的约束类型？', '["主键约束", "外键约束", "循环约束", "唯一约束"]', '"C"', '常见的数据库约束有：主键约束、外键约束、唯一约束、非空约束、检查约束等，没有循环约束。', 2, 1),
(4, 1, 2, '以下哪些是SQL的聚合函数？（多选）', '["COUNT", "SUM", "JOIN", "AVG"]', '["A", "B", "D"]', 'COUNT、SUM、AVG、MAX、MIN都是聚合函数。JOIN是连接操作，不是聚合函数。', 3, 1),
(4, 1, 3, '事务的ACID特性中，A代表原子性（Atomicity）。', NULL, '"true"', 'ACID分别代表：原子性(Atomicity)、一致性(Consistency)、隔离性(Isolation)、持久性(Durability)。', 3, 1),
(4, 1, 4, 'SQL中删除表中所有数据但保留表结构的命令是____。', NULL, '"TRUNCATE"', 'TRUNCATE TABLE可以删除表中所有数据但保留表结构，比DELETE更快。', 3, 1);

-- 计算机网络 题目 (subject_id=5)
INSERT INTO `question` (`subject_id`, `creator_id`, `type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `status`) VALUES
(5, 1, 1, 'HTTP协议默认使用的端口号是？', '["21", "22", "80", "443"]', '"C"', 'HTTP默认端口是80，HTTPS默认端口是443，FTP是21，SSH是22。', 1, 1),
(5, 1, 1, 'OSI七层模型中，TCP协议工作在哪一层？', '["应用层", "传输层", "网络层", "数据链路层"]', '"B"', 'TCP是传输层协议，负责提供可靠的端到端数据传输。', 2, 1),
(5, 1, 2, '以下哪些是应用层协议？（多选）', '["HTTP", "TCP", "FTP", "IP"]', '["A", "C"]', 'HTTP和FTP是应用层协议，TCP是传输层协议，IP是网络层协议。', 3, 1),
(5, 1, 3, 'TCP是面向连接的可靠传输协议。', NULL, '"true"', 'TCP通过三次握手建立连接，提供可靠的、有序的数据传输服务。', 2, 1),
(5, 1, 4, 'IP地址192.168.1.1属于____类地址。', NULL, '"C"', 'C类地址范围是192.0.0.0到223.255.255.255，192.168.x.x是C类私有地址。', 2, 1);
