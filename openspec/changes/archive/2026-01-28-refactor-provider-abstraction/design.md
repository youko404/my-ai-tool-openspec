## Context

当前聊天调用在 `ChatServiceImpl` 中根据 provider 分支，OpenRouter 的配置读取与 API Key 解密逻辑与路由逻辑耦合，后续接入更多服务商会导致重复与维护成本上升。

## Goals / Non-Goals

- Goals:
    - 抽象服务商聊天调用的通用逻辑（配置读取、API Key 解密、基础 URL 处理）
    - 保持现有外部 API 行为与错误语义不变
    - 为新增服务商提供清晰扩展点
- Non-Goals:
    - 改变 API 返回结构或接口路径
    - 引入新的服务商或完善 OpenAI 的实际调用能力

## Decisions

- Decision: 使用“抽象基类 + 各服务商实现类”的模式
    - 抽象基类负责：
        - 读取并校验 Provider 配置
        - 解密 API Key
        - 解析/拼接基础 URL（拼接 `/chat/completions` 等端点）
    - 具体实现类负责：
        - 构造服务商请求 DTO
        - 调用对应的 Client（例如 `OpenRouterClient`）
        - 处理响应映射
- Decision: 通过 Spring 注入 `Map<ModelProvider, ProviderChatClient>` 注册实现类
    - 路由层根据模型 provider 从 Map 中选择实现类
    - 实现类需声明其支持的 provider 并注册为 Spring Bean
- Decision: 保留既有异常语义
    - 未实现的服务商继续抛出 “Provider not implemented”
    - 缺失配置继续抛出 “Provider config not found ...”

## Alternatives considered

- 仅引入接口（不提供基类）
    - 缺点：通用逻辑仍需复制或分散在多处
- 在 `ChatServiceImpl` 中抽取私有通用方法
    - 缺点：抽象仍集中在路由层，不利于扩展与测试隔离

## Risks / Trade-offs

- 风险：抽象层过深导致调试复杂
    - 缓解：保持基类最小职责，仅覆盖公共逻辑，日志保留原有细粒度信息

## Migration Plan

- 引入抽象基类与实现类
- 重构聊天路由使用实现类注册/选择
- 保留既有测试并补充抽象层测试

## Open Questions

- 无
