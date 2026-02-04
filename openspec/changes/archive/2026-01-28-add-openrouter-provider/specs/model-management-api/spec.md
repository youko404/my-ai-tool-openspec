## ADDED Requirements

### Requirement: AI Provider Configuration Management

系统 SHALL 支持持久化管理服务商配置（当前支持 OpenRouter 与 OpenAI），包含 `provider`、`apiBaseUrl` 与 `apiKey`（加密存储）。

#### Scenario: 保存服务商配置

- **WHEN** 用户提交服务商配置（provider、apiBaseUrl、apiKey）
- **THEN** 系统校验必填字段并加密存储 apiKey
- **AND** 配置可被更新（按 provider 覆盖保存）

#### Scenario: 获取服务商配置

- **WHEN** 请求获取指定 provider 的配置
- **THEN** 系统返回 `apiBaseUrl` 等非敏感字段
- **AND** 不返回 apiKey 明文

#### Scenario: 未配置时允许启动

- **WHEN** 系统启动且未配置任何服务商
- **THEN** 系统仍可启动并提供非调用类功能

### Requirement: OpenRouter Chat Completion API

系统 SHALL 提供 OpenRouter 聊天补全 API 调用能力，支持发送对话消息并获取模型生成的响应。

#### Scenario: 同步调用成功

- **WHEN** 调用 `OpenRouterClient.chatCompletion()` 方法并传入有效的请求参数
- **THEN** 系统向 OpenRouter API 发送 POST 请求到 `/chat/completions` 端点
- **AND** 返回包含模型生成内容的响应对象

#### Scenario: 使用服务商配置调用

- **WHEN** 调用聊天补全接口且 provider 为 `OPENROUTER`
- **THEN** 系统从服务商配置中读取 apiBaseUrl 与 apiKey
- **AND** 使用该配置发起请求

#### Scenario: 认证失败处理

- **WHEN** 使用无效的 API Key 调用聊天补全接口
- **THEN** 系统捕获 HTTP 401 错误并抛出 `IllegalStateException` 或自定义认证异常
- **AND** 错误信息中包含 "Authentication failed" 或类似描述

#### Scenario: 网络超时处理

- **WHEN** OpenRouter API 响应超过配置的超时时间
- **THEN** 系统捕获超时异常并抛出友好的业务异常
- **AND** 日志中记录超时详情

#### Scenario: 支持流式响应（可选）

- **WHEN** 调用 `OpenRouterClient.chatCompletionStream()` 方法并设置 `stream=true`
- **THEN** 系统建立 Server-Sent Events (SSE) 连接
- **AND** 逐块返回模型生成的内容

### Requirement: Request and Response Data Structure

系统 SHALL 使用结构化的 DTO 对象封装 OpenRouter API 的请求和响应数据。

#### Scenario: 请求对象包含必需字段

- **WHEN** 构建 `OpenRouterChatRequest` 对象
- **THEN** 对象必须包含 `model` 和 `messages` 字段
- **AND** `messages` 字段为包含至少一条消息的列表，每条消息包含 `role` 和 `content`

#### Scenario: 响应对象包含生成内容

- **WHEN** 解析 OpenRouter API 返回的 JSON 响应
- **THEN** 响应对象 `OpenRouterChatResponse` 包含 `id`、`model`、`choices` 字段
- **AND** `choices` 列表中第一个元素的 `message.content` 包含模型生成的文本

#### Scenario: 支持可选参数

- **WHEN** 在请求中设置可选参数如 `temperature`、`maxTokens`
- **THEN** 这些参数被序列化到 JSON 请求体中并发送给 OpenRouter API

### Requirement: Logging and Error Handling

系统 SHALL 对 OpenRouter API 调用过程进行日志记录，并提供清晰的错误处理机制。

#### Scenario: 请求前记录日志

- **WHEN** 发起 OpenRouter API 调用前
- **THEN** 系统以 DEBUG 级别记录请求的模型名称和消息数量

#### Scenario: 响应后记录结果

- **WHEN** 成功获取 OpenRouter API 响应后
- **THEN** 系统以 DEBUG 级别记录返回的响应 ID 和生成的 token 数量（如有）

#### Scenario: 错误时记录详细信息

- **WHEN** OpenRouter API 调用失败（网络错误、HTTP 错误等）
- **THEN** 系统以 ERROR 级别记录异常堆栈和错误详情
- **AND** 不记录 API Key 等敏感信息到日志中

### Requirement: Model API Key Management

系统 SHALL 支持为每个 AI 模型配置独立的 API Key，用于调用对应供应商的服务。

#### Scenario: 创建模型时设置 API Key

- **WHEN** 创建 AI 模型时提供 `apiKey` 字段
- **THEN** 系统将 API Key 加密存储到数据库
- **AND** 模型保存成功

#### Scenario: 更新模型的 API Key

- **WHEN** 更新 AI 模型时提供新的 `apiKey` 字段
- **THEN** 系统更新加密存储的 API Key
- **AND** 返回更新后的模型信息（不包含明文 API Key）

#### Scenario: 获取模型时不返回 API Key

- **WHEN** 查询 AI 模型详情或列表
- **THEN** 返回的 VO 对象不包含 API Key 字段
- **AND** 保护敏感信息不泄露

#### Scenario: API Key 为空时使用服务商配置

- **WHEN** 模型的 `apiKey` 字段为空或 null
- **THEN** 系统在调用 API 时使用服务商配置的 API Key

### Requirement: Model Provider Enum

系统 SHALL 使用枚举类型管理模型供应商，确保类型安全和可扩展性。

#### Scenario: 创建模型时指定供应商

- **WHEN** 创建 AI 模型时指定 `provider` 为 `OPENROUTER` 或 `OPENAI`
- **THEN** 系统验证枚举值有效并保存模型

#### Scenario: 无效供应商枚举值

- **WHEN** 创建或更新 AI 模型时提供无效的 `provider` 值（如 "INVALID_PROVIDER"）
- **THEN** 系统返回 400 错误并提示有效的供应商列表

#### Scenario: 查询支持的供应商列表

- **WHEN** 调用获取供应商列表接口
- **THEN** 返回所有支持的供应商枚举值（当前为 `["OPENROUTER", "OPENAI"]`）

### Requirement: Chat API with Model Routing

系统 SHALL 提供统一的聊天接口，根据指定模型的供应商自动路由到对应的服务实现。

#### Scenario: 使用 OpenRouter 模型聊天成功

- **WHEN** 调用 `/models/{id}/chat` 接口，指定的模型 provider 为 `OPENROUTER`
- **THEN** 系统从数据库加载模型配置（包括 apiKey）
- **AND** 如模型 apiKey 为空则读取服务商配置
- **AND** 调用 `OpenRouterClient.chatCompletion()` 方法
- **AND** 返回模型生成的回复内容

#### Scenario: 服务商未配置

- **WHEN** 调用 `/models/{id}/chat` 接口且对应 provider 未配置服务商参数
- **THEN** 系统返回 400 错误并提示 "Provider config not found" 或类似信息

#### Scenario: OpenAI 尚未实现

- **WHEN** 调用 `/models/{id}/chat` 接口且 provider 为 `OPENAI`
- **THEN** 系统返回 501 错误并提示 "Provider not implemented" 或类似信息

#### Scenario: 模型不存在

- **WHEN** 调用 `/models/{id}/chat` 接口，指定的模型 ID 不存在
- **THEN** 系统返回 404 错误并提示 "Model not found"

#### Scenario: 模型未启用

- **WHEN** 调用 `/models/{id}/chat` 接口，指定的模型 `isEnabled` 为 false
- **THEN** 系统返回 403 错误并提示 "Model is disabled"

#### Scenario: 供应商服务不可用

- **WHEN** 调用 `/models/{id}/chat` 接口，但对应供应商的服务调用失败
- **THEN** 系统捕获异常并返回 500 错误
- **AND** 错误信息包含供应商名称和失败原因
