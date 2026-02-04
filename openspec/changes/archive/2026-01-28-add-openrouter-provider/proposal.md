# Change: Add OpenRouter AI provider support

## Why

平台需要接入 OpenRouter AI 服务，并在模型配置页面提供统一的服务商配置入口，以便集中管理 API 地址与密钥。服务商配置需要持久化到数据库，避免依赖
application.yml，便于运维与多环境一致性管理。当前仅支持 OpenRouter 与 OpenAI（OpenAI 仅保存配置，暂不接入调用）。

## What Changes

- 新增服务商配置管理（数据库持久化），支持保存 API 地址与 API Key（加密存储）
- 模型配置页面右上角新增“服务商配置”入口，弹窗选择服务商并填写/保存配置
- 调用时根据服务商配置动态设置 baseUrl 与 apiKey；未配置允许启动，但调用时报错
- OpenRouter 调用能力保留；OpenAI 仅保存配置，暂不实现调用
- 遵循现有架构模式：通过 Adapter/Client 层与第三方服务解耦
- **新增**：模型供应商枚举扩展为 `OPENROUTER` + `OPENAI`

## Impact

- Affected specs: `model-management-api`（服务商配置管理、调用路由与错误处理）、`model-management-ui`（服务商配置入口与弹窗）
- Affected code:
    - 新增服务商配置相关 Entity/DTO/VO/Repository/Service/Controller（待定命名）
    - 调整 `OpenRouterClient` 支持按服务商配置动态 baseUrl 与 apiKey
    - 扩展 `ModelProvider` 枚举支持 OPENAI
    - 调整模型管理 API 与聊天服务在未配置服务商时的错误处理
    - 前端新增服务商配置弹窗与 API 调用（`frontend/src/pages/model-page` 等）
