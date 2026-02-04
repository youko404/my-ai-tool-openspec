# chat-ui Specification

## Purpose

TBD - created by archiving change update-chat-ui-styles. Update Purpose after archive.

## Requirements

### Requirement: Chat Layout Height

系统 SHALL 使聊天页面区域高度占页面可用高度的 90%（明亮/暗黑模式一致）。

#### Scenario: Default chat layout height

- **WHEN** 用户打开聊天页面
- **THEN** 聊天页面区域高度占页面可用高度的 90%

### Requirement: Chat Dark Mode Message Readability

系统 SHALL 在暗黑模式下使用户消息气泡背景与 AI 回复气泡背景一致，并保持文本可读。

#### Scenario: Dark mode bubble contrast

- **WHEN** 暗黑模式启用且聊天消息展示
- **THEN** 用户与 AI 回复气泡背景颜色一致
- **AND** 气泡文本对比度可读

### Requirement: Chat Dark Mode Control Text Color

系统 SHALL 在暗黑模式下将发送按钮与模型选择框（含下拉选项）文字颜色设为白色。

#### Scenario: Dark mode controls text

- **WHEN** 暗黑模式启用且聊天页面显示发送按钮与模型选择框
- **THEN** 发送按钮文字颜色为白色
- **AND** 模型选择框的当前值、占位与下拉选项文字颜色为白色

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

