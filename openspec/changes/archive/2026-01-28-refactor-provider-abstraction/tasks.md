## 1. Implementation

- [x] 1.1 新增服务商抽象基类，封装配置读取、API Key 解密与基础 URL 处理
- [x] 1.2 实现 OpenRouter 服务商类，继承抽象基类并调用现有 `OpenRouterClient`
- [x] 1.3 实现 OpenAI 服务商类（保持未实现语义）
- [x] 1.4 重构聊天路由层（ChatService）使用服务商实现类完成调用
- [x] 1.5 更新/新增单元测试，覆盖抽象基类与路由行为
- [ ] 1.6 运行后端相关测试（用户表示不需要）
