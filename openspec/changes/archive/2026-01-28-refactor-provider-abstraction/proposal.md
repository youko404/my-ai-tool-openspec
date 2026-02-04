# Change: Refactor provider abstraction for chat services

## Why

当前聊天服务在业务层通过 switch 分支直连各服务商逻辑，配置读取与 API Key 解密等通用逻辑分散在实现中。抽象为基类可降低重复、统一错误语义，并便于后续扩展更多服务商。

## What Changes

- 引入服务商抽象基类，封装配置读取、API Key 解密、基础 URL 处理等通用逻辑
- 为每个服务商提供具体实现类并保持现有对外行为不变
- 重构聊天路由层以通过服务商实现类完成调用

## Impact

- Affected specs: model-management-api
- Affected code: backend service 层（聊天路由、服务商客户端、相关测试）
