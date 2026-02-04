## ADDED Requirements

### Requirement: Provider Chat Client Abstraction

系统 SHALL 使用服务商抽象基类封装聊天调用的通用逻辑，并由各服务商实现具体调用。

#### Scenario: 基类提供通用能力

- **WHEN** 通过服务商实现类发起聊天调用
- **THEN** 通用逻辑由抽象基类统一处理
- **AND** 包含服务商配置读取、API Key 解密与基础 URL 解析

#### Scenario: OpenRouter 使用抽象实现

- **WHEN** 选择 provider 为 `OPENROUTER` 的模型进行聊天调用
- **THEN** 系统使用 OpenRouter 的实现类完成调用
- **AND** 外部行为与错误语义保持不变
