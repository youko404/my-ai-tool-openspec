# Project Context

## Purpose

本项目旨在构建一个 **AI 内容管理平台（AI Content Management System）**，用于对 AI 相关内容（如
Prompt、模型配置、知识库内容、生成结果、任务记录等）进行统一的创建、管理、审核和展示。  
系统需要支持高可用、可扩展的架构，便于未来接入不同类型的 AI 能力（如大模型、向量检索、工作流编排等）。

## Tech Stack

- **Frontend**
    - React
    - TypeScript
    - Ant Design / MUI
    - Vite / Create React App
- **Backend**
    - Java 17+
    - Spring Boot
- **Data & Infra**
    - MySQL / PostgreSQL
    - Redis
- **DevOps**
    - Docker

## Project Conventions

### Code Style

- **Frontend**
    - 使用 TypeScript，禁止使用 `any`
    - 函数组件 + React Hooks
    - 文件命名采用 `kebab-case`，组件名使用 `PascalCase`
    - 组件职责单一，避免臃肿组件
- **Backend**
    - 遵循阿里 Java 开发规范
    - 分层清晰：Controller / Service / Repository
    - DTO、VO、Entity 严格区分
    - 命名语义清晰，避免无意义缩写

### Architecture Patterns

- 前后端分离（RESTful API）
- 后端采用分层架构：
    - Controller（接口层）
    - Service（业务层）
    - Domain / Entity（领域模型）
    - Repository / Mapper（数据访问层）
- 核心业务采用轻量级 DDD 思想
- AI 能力通过 Adapter / Client 层与第三方模型服务解耦

### Testing Strategy

- **Frontend**
    - Jest + React Testing Library
    - 核心组件与关键业务逻辑必须覆盖测试
- **Backend**
    - JUnit 5 + Mockito
    - Spring Boot Test 进行接口测试
    - 重要业务逻辑需具备单元测试

### Git Workflow

- 分支策略：
    - `master`：生产分支
    - `develop`：开发分支
    - `feature/*`：功能分支
    - `fix/*`：修复分支
- Commit Message 规范：
    - `feat:` 新功能
    - `fix:` 修复问题
    - `refactor:` 重构
    - `docs:` 文档
    - `test:` 测试

## Domain Context

- AI 内容包括但不限于：
    - Prompt 模板
    - 模型参数与推理配置
    - 知识库内容（文本 / 结构化数据）
    - AI 生成结果
- 内容特性：
    - 支持版本管理
    - 状态流转（草稿 / 已发布 / 已下线）
    - 权限与可见性控制
- 可能扩展能力：
    - 多模型接入（公有 / 私有大模型）
    - 异步任务处理（生成、索引、分析）

## Important Constraints

- 后端需支持高并发与横向扩展
- AI Key、模型配置等敏感信息不得暴露至前端
- 系统需支持操作审计与日志追踪
- 核心接口需具备幂等性与安全校验
- 前端需兼容主流现代浏览器
- 前端页面必须注意明亮和暗黑模式 UI 一致性，例如暗黑模式背景色为暗色、字体为亮色

## External Dependencies

- 第三方 AI 模型服务（如 OpenAI / 私有模型 API）
- 对象存储服务（用于内容附件，可选）
- 身份认证系统（SSO，可选）
- 日志与监控系统（如 Prometheus / ELK）
