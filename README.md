# 图书管理系统

基于 Spring Boot + MyBatis + Redis 的图书管理系统。

## 技术栈

- Java 21
- Spring Boot 3.4.3
- MyBatis 3.0.4
- MySQL 8.0
- Redis 6.0
- JWT (java-jwt 4.4.0)

## 项目功能

- 用户注册、登录(JWT 认证)
- 图书的增删改查、分页查询、条件搜索
- 图书分类管理
- 借书、还书、查看个人借阅记录
- Redis 缓存图书详情(30分钟过期)
- 全局异常处理、统一返回格式

## 快速开始

### 1. 环境要求

- JDK 21+
- MySQL 8.0+
- Redis 6.0+(默认端口 6379)
- Maven 3.8+

### 2. 创建数据库

用 MySQL 客户端(Workbench 或命令行)执行:

```sql
CREATE DATABASE library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后切换到这个库:

```sql
USE library;
```

再执行下面这些建表语句:

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    role TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    isbn VARCHAR(20),
    category_id BIGINT,
    publisher VARCHAR(100),
    stock INT DEFAULT 0,
    total INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE borrow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_time DATETIME NOT NULL,
    return_time DATETIME,
    status TINYINT DEFAULT 0
);

INSERT INTO category (name, description) VALUES
('计算机', '计算机相关书籍'),
('文学', '文学作品'),
('历史', '历史书籍');

INSERT INTO book (title, author, isbn, category_id, publisher, stock, total) VALUES
('Java核心技术', 'Cay S. Horstmann', '9787111547426', 1, '机械工业出版社', 5, 5),
('Spring Boot实战', 'Dave Syer', '9787115428028', 1, '人民邮电出版社', 3, 3),
('百年孤独', '加西亚·马尔克斯', '9787544253994', 2, '南海出版公司', 2, 2);
```

### 3. 修改 application.yaml

打开 `src/main/resources/application.yaml`,把 MySQL 密码改成你自己的:

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码
```

### 4. 启动 Redis

确认 Redis 在 6379 端口运行(用 Tiny RDM 连一下能看到就行)。

### 5. 启动项目

在 IDEA 里运行 `LibrarySystemApplication.java` 启动类,启动成功后访问:

````
http://localhost:8080/api/test
````

返回 `{"code":200,"message":"success","data":"Hello"}` 即成功。

## API 接口

| 方法 | 路径 | 说明 | 需要登录 |
|------|------|------|----------|
| POST | /api/users/register | 用户注册 | 否 |
| POST | /api/users/login | 用户登录 | 否 |
| GET | /api/users/info | 获取当前用户信息 | 是 |
| GET | /api/books | 图书分页查询 | 否 |
| GET | /api/books/{id} | 图书详情 | 否 |
| POST | /api/books | 新增图书 | 是 |
| DELETE | /api/books/{id} | 删除图书 | 是 |
| GET | /api/categories | 分类列表 | 否 |
| POST | /api/categories | 新增分类 | 是 |
| POST | /api/borrow/{bookId} | 借书 | 是 |
| POST | /api/borrow/return/{recordId} | 还书 | 是 |
| GET | /api/borrow/my | 我的借阅 | 是 |

## 登录示例

**请求**:`POST /api/users/login`

```json
{
  "username": "admin",
  "password": "123456"
}
```

**返回**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJ0eXAiOiJKV1Qi...",
    "userId": 1,
    "username": "admin",
    "role": 1
  }
}
```

**带 Token 请求其他接口**:

````
GET /api/users/info
Header: Authorization: Bearer eyJ0eXAiOiJKV1Qi...
````

## 核心设计

### JWT 认证

用户登录后,后端用 JwtUtil 生成 Token 返回给前端。前端后续请求在 Header 里带 `Authorization: Bearer <token>`,JwtInterceptor 拦截器验证 Token 有效性后,把 userId 放进 request 属性,Controller 就能拿到当前登录用户。

### Redis 缓存

- **查询**:`getById` 方法先查 Redis,有就直接返回(不查数据库);没有再查 MySQL,并把结果写入 Redis(30分钟过期)
- **更新**:借书、还书、删书时,删除 Redis 中对应的缓存,下次查询自动从数据库重新加载

### 事务与防超卖

借书用 `@Transactional` 保证「扣库存 + 写借阅记录」两步要么都成功、要么都失败。SQL 层用条件更新 `UPDATE book SET stock = stock - 1 WHERE id = ? AND stock > 0`,避免库存扣成负数。
