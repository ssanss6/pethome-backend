# pethome-backend

宠物寄养洗护预约管理平台 · 后端服务（Spring Boot 3 骨架）

## 技术栈

- Spring Boot 3.5 / Java 17
- MyBatis-Plus + MySQL 8
- Redis 7
- Spring Security + JWT
- 阿里云 OSS
- Knife4j / SpringDoc 接口文档

## 前置条件

1. JDK 17
2. Maven 3.8+
3. MySQL 8 已启动
4. Redis 已启动

## 初始化数据库

```bash
# 创建库
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS pethome DEFAULT CHARSET utf8mb4;"

# 导入建表 SQL（骨架阶段仅包含鉴权三表，其余表见开发设计文档 §6）
mysql -uroot -p pethome < src/main/resources/db/schema.sql
```

## 配置

复制 `src/main/resources/application-dev.yml`，按需修改数据库账号密码。
微信登录与 OSS 密钥在 `application.yml` 的 `pethome.*` 下配置：

- `pethome.wx.mock-enabled=true`：本地开发不接真实微信时，用 mock openid 直接登录（`code` 会被当作 openid 前缀）。
- `pethome.oss.*`：OSS endpoint/accessKey/secret/bucket；骨架用「服务端签名 URL」直传，不暴露 AccessKey。

## 运行

```bash
cd backend
mvn spring-boot:run
# 或
mvn clean package -DskipTests
java -jar target/pethome-backend-0.0.1-SNAPSHOT.jar
```

启动后：

- 健康检查：`GET http://localhost:8080/api/health`
- 接口文档：`http://localhost:8080/swagger-ui.html`

## 默认账号（首次启动自动种子，见 DataInitializer）

| 端 | 用户名 | 密码 | 说明 |
|---|---|---|---|
| 员工小程序 | `staff01` | `123456` | 演示照护员 |
| 管理后台 | `admin` | `admin123` | 超级管理员 |

## 登录接口

- 员工账号密码登录：`POST /api/auth/login`，body `{"username":"staff01","password":"123456"}`
- 宠物主微信登录：`POST /api/auth/wx-login`，body `{"code":"<wx.login 的 code>"}`

> 微信登录在 `mock-enabled=true` 时，任意 `code` 都会命中/创建一个宠物主账号（openid = `mock_<code>`），便于本地联调。
