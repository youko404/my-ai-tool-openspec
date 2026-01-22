# AI Content Management Platform

## 项目简介

AI 内容管理平台（AI Content Management System），用于对 AI 相关内容（如 Prompt、模型配置、知识库内容、生成结果、任务记录等）进行统一的创建、管理、审核和展示。

## 技术栈

| 层级       | 技术                                        |
|----------|-------------------------------------------|
| Frontend | React 18 + TypeScript + Vite + Ant Design |
| Backend  | Java 17 + Spring Boot 3.2                 |
| Database | MySQL 8.0                                 |
| Cache    | Redis 7                                   |
| DevOps   | Docker + Docker Compose                   |

## 目录结构

```
my-ai-tool-openspec/
├── backend/                 # Spring Boot 后端服务
│   ├── src/main/java/       # Java 源代码
│   ├── src/main/resources/  # 配置文件
│   ├── pom.xml              # Maven 配置
│   └── Dockerfile
├── frontend/                # React 前端应用
│   ├── src/                 # TypeScript 源代码
│   ├── package.json
│   └── Dockerfile
├── openspec/                # 项目规范定义
├── docker-compose.yml       # Docker 编排配置
└── README.md
```

## 快速开始

### 前置要求

- Node.js 20+
- Java 17+
- Docker & Docker Compose (可选)

### 开发模式

**前端开发**

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

**后端开发**

```bash
cd backend
./mvnw spring-boot:run
```

访问 http://localhost:8080/api/health

### Docker 模式

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

访问 http://localhost

## 开发规范

### Git Commit 规范

- `feat:` 新功能
- `fix:` 修复问题
- `refactor:` 重构
- `docs:` 文档
- `test:` 测试

### 分支策略

- `master` - 生产分支
- `develop` - 开发分支
- `feature/*` - 功能分支
- `fix/*` - 修复分支

## 许可证

MIT
