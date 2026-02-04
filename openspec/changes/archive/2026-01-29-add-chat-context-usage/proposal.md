# Change: Add chat context window and usage tracking

## Why

需要在聊天请求中携带最近上下文，同时记录并返回每次对话的 token 消耗，便于控制成本与复盘。

## What Changes

- 记录 prompt/completion/total token 到消息表，未返回 usage 时写入 0，并在消息详情 API 中返回。
- 用户发送新消息时一次查询历史消息，构建带窗口限制的请求消息列表（最多 10 条、文本总长度最多 5000 字符，包含当前用户消息）。
- 超出限制时丢弃更早历史消息，保留最新内容。
- 限制用户单条消息内容不超过 5000 字符，超过则拒绝请求。

## Impact

- Affected specs: chat-history-api
- Affected code: `backend/src/main/java/com/ai/content/domain/entity/mysql/ChatMessage.java`,
  `backend/src/main/java/com/ai/content/vo/ChatMessageVO.java`,
  `backend/src/main/java/com/ai/content/service/impl/ChatHistoryServiceImpl.java`, provider clients/mapping, DB
  migration
