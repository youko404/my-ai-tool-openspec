# Change: Add chat history persistence

## Why

当前聊天对话仅保存在前端本地存储，刷新或换设备即丢失，且服务端无法追溯对话与错误信息。需要把对话与消息持久化到数据库，满足历史查询与审计需求。

## What Changes

- 新增聊天会话与消息的数据库存储，首次发送消息时创建会话。
- 记录每次对话的用户输入与 AI 输出；若调用失败，记录完整供应商响应与错误信息。
- 提供历史对话查询与软删除接口，前端打开聊天页面时加载并按时间倒序显示。

## Impact

- Affected specs: chat-ui (新增历史列表行为), new capability chat-history-api
- Affected code: backend chat service/controller/repository, DB migrations, frontend chat page data source
