# chat-ui Specification Delta

## ADDED Requirements

### Requirement: Chat History Loading

系统 SHALL 在打开聊天页面时从服务端拉取历史会话，并按更新时间倒序显示在左侧对话列表。

#### Scenario: Load history on page open

- **WHEN** 用户打开聊天页面
- **THEN** 系统请求历史会话列表
- **AND** 左侧列表按更新时间倒序展示

### Requirement: Chat History Detail Display

系统 SHALL 在选择会话时加载对应消息并展示在聊天窗口。

#### Scenario: Select a conversation

- **WHEN** 用户点击某条会话
- **THEN** 系统加载该会话的消息明细
- **AND** 聊天窗口展示对应消息

### Requirement: Chat Conversation Deletion

系统 SHALL 在用户删除会话时调用后端软删除接口并更新列表。

#### Scenario: Delete conversation

- **WHEN** 用户确认删除对话
- **THEN** 系统调用删除接口并从列表移除该会话
