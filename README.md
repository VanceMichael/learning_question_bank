# 题库练习网站 - Question Bank Practice Platform

大学生期末备考题库练习平台，支持多种题型（单选、多选、判断、填空、简答），包含用户端和管理后台双端架构。

## How to Run

### Docker 一键启动（推荐）

```bash
docker-compose up --build -d
```

等待所有服务启动完成（约 2-3 分钟），然后访问：

- 用户端：http://localhost:8081
- 管理后台：http://localhost:8082
- 后端 API：http://localhost:8080

### 本地开发启动

1. 启动 MySQL 8.0，创建数据库并执行 `backend/sql/schema.sql`
2. 启动后端：
```bash
cd backend
mvn spring-boot:run
```
3. 启动用户端前端：
```bash
cd frontend-user
npm install
npm run dev
```
4. 启动管理后台前端：
```bash
cd frontend-admin
npm install
npm run dev
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend-user | 8081 | 用户端（学生练习） |
| frontend-admin | 8082 | 管理后台 |
| backend | 8080 | Spring Boot API |
| mysql | 3306 | MySQL 8.0 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 可登录管理后台和用户端 |
| 学生 | student | 123456 | 仅可登录用户端 |

## 题目内容

用户需求：

> 我是一名大学生，由于马上要开始期末考试，我想有一个自己的题库练习网站可以帮助我学习。
>
> 功能要求：
> 1. 可以自己添加题目，单选多选判断简答填空
> 2. 选择答案后点击下一题可以跳转下一题，做完题目后会整体预览，用一个按钮可以显示和隐藏答案
> 3. 用图表显示正确率
> 4. 要有登录页面
> 5. 可以有不同的科目，例如 Java, Python...
>
> 要求界面美观好看，UI 配色美观，用户交互体验良好，简约大气，可以添加页面动效。

### 技术栈

- Frontend: Vue 3 + Vite + TypeScript + Element Plus + ECharts + Pinia + Axios + Scss + Animate.css
- Backend: Java 17 + Spring Boot 3 + MyBatis-Plus + MySQL 8.0 + JWT
- Deploy: Docker + Docker Compose + Nginx
# verification
# learning_question_bank
