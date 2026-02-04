## Context

系统当前已支持 Ollama 嵌入模型服务（用于知识库向量化），现需要扩展对话能力，接入 OpenRouter AI 作为统一的模型提供商。OpenRouter
提供了兼容 OpenAI 格式的 API，支持数百种模型，具有自动故障转移和成本优化能力。

新增需求：服务商配置（OpenRouter / OpenAI）的 API 地址与 API Key 需要在模型配置页面统一管理，并持久化到数据库，不再依赖
`application.yml`。系统允许在未配置服务商时启动，但在调用时提示配置缺失。OpenAI 仅保存配置，暂不接入调用。

## Goals / Non-Goals

- Goals:
    - 提供 OpenRouter AI 聊天补全 API 调用能力（同步模式）
    - 支持流式响应（streaming）
    - 服务商配置（API 地址、API Key）持久化到数据库并可通过 UI 管理
    - 允许系统在未配置服务商时启动，调用时报错提示配置缺失
    - 遵循现有项目架构模式（Controller/Service/Repository + Client 适配）
    - 提供清晰的错误处理和日志记录
    - AI 模型支持存储独立的 API Key（加密存储）
    - 模型供应商改为枚举类型，确保类型安全
    - 提供统一的聊天接口，根据模型自动路由到对应供应商服务
- Non-Goals:
    - 本阶段不实现 OpenAI 调用能力（仅保存配置）
    - 不实现历史对话管理和持久化
    - 不实现复杂的重试和限流机制（后续可扩展）
    - 不实现 OpenRouter 的其他 API（如 embeddings、models 列表等）

## Decisions

- Decision: 模型实体增加 `apiKey` 字段，支持每个模型配置独立的 API Key
    - Rationale: 不同模型可能需要不同的 API Key，提供更灵活的配置方式
- Decision: 将 `provider` 字段从 String 改为枚举 `ModelProvider`
    - Rationale: 确保类型安全，避免字符串拼写错误，便于扩展新供应商
- Decision: 创建 `ChatService` 服务层，根据模型的 provider 路由到对应的 Client
    - Rationale: 实现策略模式，解耦业务逻辑与具体供应商实现
- Decision: Controller 层提供 `/models/{id}/chat` 接口
    - Rationale: RESTful 风格，表明是对特定模型的操作
- Decision: 使用 Spring Boot RestClient 进行 HTTP 调用
    - Rationale: 与现有 OllamaEmbeddingModel 保持一致，Spring Boot 3.x 推荐使用 RestClient
- Decision: 服务商配置持久化到数据库，包含 `provider`、`apiBaseUrl`、`apiKey`（加密）
    - Rationale: 集中配置管理，避免环境配置分散，便于 UI 管理
- Decision: 移除对 `application.yml` 中 OpenRouter API Key 的强依赖
    - Rationale: 允许无配置启动，调用时再做校验与提示
- Decision: DTO 类定义请求和响应格式，使用 Lombok 简化代码
    - Rationale: 符合项目规范，保持代码整洁
- Decision: 服务类命名为 `OpenRouterClient`，放在 `service` 包下
    - Rationale: 与 OllamaEmbeddingModel 保持命名一致性
- Decision: 默认支持同步调用，流式响应作为可选功能
    - Rationale: 简化初期实现，满足基本对话需求
- Decision: OpenAI 仅保存配置，不提供调用实现
    - Rationale: 预留能力入口，降低当前实现复杂度

## Risks / Trade-offs

- API Key 敏感信息需要妥善管理，不得暴露至前端或日志
- 网络调用可能失败或超时，需要合理的超时配置和错误提示
- OpenRouter API 调用有费用和频率限制，需要在文档中说明
- 流式响应需要异步处理，增加实现复杂度

## Migration Plan

新增服务商配置表（或配置存储），需要数据库迁移。移除 `openrouter.*` 依赖后，原有配置不再强制要求。

## Open Questions

- 是否需要实现 token 使用量统计和费用监控？（建议后续迭代）
