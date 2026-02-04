# Tasks: Add chat history persistence

- [x] 
    1. 定义数据库迁移，新增 `chat_conversation` 与 `chat_message` 表（含软删除字段与索引）。
- [x] 
    2. 新增会话/消息实体、Repository、DTO/VO，确保不暴露敏感字段（如 provider_response 仅用于内部）。
- [x] 
    3. 实现会话持久化服务：

    - [x] 3.1 首条消息创建会话并保存用户消息
    - [x] 3.2 调用供应商后保存 AI 消息；失败时保存 error 状态与原始响应
- [x] 
    4. 新增会话相关 API（列表、详情、追加消息、软删除），并在列表中按更新时间倒序返回。
- [x] 
    5. 前端聊天页改用服务端历史数据：

    - [x] 5.1 打开页面加载会话列表并默认选中最新
    - [x] 5.2 发送消息走新接口并刷新列表/详情
    - [x] 5.3 删除会话调用软删除接口
- [x] 
    6. 补充后端单测/集成测试与前端组件测试（至少覆盖创建会话、错误记录、列表排序）。
- [x] 
    7. 运行 `openspec validate add-chat-history-storage --strict --no-interactive` 并修复问题。
