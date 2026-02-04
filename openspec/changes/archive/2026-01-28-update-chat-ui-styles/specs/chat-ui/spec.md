## ADDED Requirements

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
