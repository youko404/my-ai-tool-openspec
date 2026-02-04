# chat-history-api Specification

## Purpose

TBD - created by archiving change add-chat-history-storage. Update Purpose after archive.

## Requirements

### Requirement: Chat Conversation Persistence

系统 SHALL 在首次发送聊天消息时创建会话，并将会话与消息持久化到数据库。

#### Scenario: Create conversation on first message

- **WHEN** 用户发送首条消息
- **THEN** 系统创建新的会话记录并保存用户消息
- **AND** 返回会话标识以便后续追加消息

### Requirement: Chat Message Recording

系统 SHALL 记录每次对话的用户输入与 AI 输出，并为 AI 消息记录 prompt/completion/total token；若供应商未返回 usage，则将
usage 记录为 0；消息详情 API 必须返回 usage 字段。

#### Scenario: Record successful chat messages with usage

- **WHEN** 供应商返回成功响应且包含 usage
- **THEN** 系统保存用户消息与 AI 回复消息
- **AND** AI 消息包含模型名称与生成内容
- **AND** AI 消息记录 prompt/completion/total token 并在消息详情 API 中返回

#### Scenario: Record successful chat messages without usage

- **WHEN** 供应商返回成功响应但未包含 usage
- **THEN** 系统将 AI 消息的 prompt/completion/total token 记录为 0

#### Scenario: Record failed chat messages

- **WHEN** 供应商调用失败
- **THEN** 系统保存用户消息与一条 error 状态的 AI 消息
- **AND** 该记录包含错误信息与完整供应商响应
- **AND** AI 消息的 prompt/completion/total token 记录为 0

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

### Requirement: Chat Message Length Limit

系统 SHALL 限制用户单条消息内容不超过 5000 字符，超过则拒绝请求并不发送给模型。

#### Scenario: Reject oversized user message

- **WHEN** 用户发送单条消息内容超过 5000 字符
- **THEN** 系统拒绝该请求并返回错误
- **AND** 系统不调用模型也不写入该条消息

### Requirement: Chat Context Window Assembly

系统 SHALL 在用户发送新消息时一次查询当前会话的全部历史消息，并构建请求消息窗口。

#### Scenario: Build window with limits

- **WHEN** 用户发送新消息
- **THEN** 系统以单次查询获取该会话的历史消息
- **AND** 系统以最新消息优先选取窗口，消息条数不超过 10 条且总消息文本长度不超过 5000 字符，且包含当前用户消息
- **AND** 当超过限制时系统丢弃更早的历史消息，保留最新内容
- **AND** 系统按时间正序构建请求消息列表并发送给模型

