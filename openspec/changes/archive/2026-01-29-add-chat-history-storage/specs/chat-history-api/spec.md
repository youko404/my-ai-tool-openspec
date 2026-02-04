# chat-history-api Specification Delta

## ADDED Requirements

### Requirement: Chat Conversation Persistence

系统 SHALL 在首次发送聊天消息时创建会话，并将会话与消息持久化到数据库。

#### Scenario: Create conversation on first message

- **WHEN** 用户发送首条消息
- **THEN** 系统创建新的会话记录并保存用户消息
- **AND** 返回会话标识以便后续追加消息

### Requirement: Chat Message Recording

系统 SHALL 记录每次对话的用户输入与 AI 输出；若调用失败，记录错误状态与供应商原始响应。

#### Scenario: Record successful chat messages

- **WHEN** 供应商返回成功响应
- **THEN** 系统保存用户消息与 AI 回复消息
- **AND** AI 消息包含模型名称与生成内容

#### Scenario: Record failed chat messages

- **WHEN** 供应商调用失败
- **THEN** 系统保存用户消息与一条 error 状态的 AI 消息
- **AND** 该记录包含错误信息与完整供应商响应

### Requirement: Chat History List

系统 SHALL 提供历史会话列表查询能力，并按更新时间倒序返回。

#### Scenario: Query conversation list on open

- **WHEN** 客户端打开聊天窗口并请求历史列表
- **THEN** 系统返回未删除的会话列表
- **AND** 列表按 updated_at 倒序排序

### Requirement: Chat History Detail

系统 SHALL 支持按会话查询消息明细。

#### Scenario: Query conversation detail

- **WHEN** 客户端请求指定会话的详情
- **THEN** 系统返回该会话的消息列表（按时间顺序）

### Requirement: Soft Delete Conversation

系统 SHALL 支持会话软删除，删除后不再出现在列表与详情中。

#### Scenario: Soft delete conversation

- **WHEN** 客户端删除某会话
- **THEN** 系统将该会话标记为已删除
- **AND** 历史列表与详情查询不返回该会话
