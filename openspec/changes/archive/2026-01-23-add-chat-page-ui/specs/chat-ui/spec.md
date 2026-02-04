## ADDED Requirements

### Requirement: Chat Tab Navigation

系统 SHALL 在主页面顶部 tab 导航的第一个位置显示"聊天"tab，点击后切换到聊天页面。

#### Scenario: Display chat tab

- **WHEN** 用户访问主页面
- **THEN** 系统在 tab 导航第一个位置显示"聊天"tab（图标为 MessageOutlined）
- **AND** tab 标签文本为"聊天"

#### Scenario: Switch to chat page

- **WHEN** 用户点击"聊天"tab
- **THEN** 系统切换到聊天页面
- **AND** tab 呈现激活状态样式

---

### Requirement: Chat Page Layout

系统 SHALL 采用左右分栏布局展示聊天页面，左侧为对话列表区域，右侧为聊天窗口区域。

#### Scenario: Display chat page layout

- **WHEN** 用户进入聊天页面
- **THEN** 系统显示左右分栏布局
- **AND** 左侧区域宽度为 300px
- **AND** 右侧区域占据剩余空间

#### Scenario: Responsive layout

- **WHEN** 浏览器窗口宽度小于 768px
- **THEN** 系统自动隐藏左侧对话列表
- **AND** 右侧聊天窗口占据全部宽度

---

### Requirement: Conversation List Display

系统 SHALL 在左侧对话列表区域显示所有会话，包含会话标题、最后消息预览和时间戳。

#### Scenario: Display conversation list

- **WHEN** 用户进入聊天页面
- **THEN** 系统在左侧显示对话列表
- **AND** 每个会话项显示标题、最后消息预览（最多 50 字符）、时间戳

#### Scenario: Empty conversation list

- **WHEN** 用户首次进入聊天页面且无任何会话
- **THEN** 系统显示空状态提示"暂无对话，点击新建对话开始聊天"

#### Scenario: Highlight active conversation

- **WHEN** 用户选择某个会话
- **THEN** 该会话项呈现高亮激活状态
- **AND** 右侧聊天窗口显示该会话的消息内容

---

### Requirement: Create New Conversation

系统 SHALL 支持用户创建新对话会话，每个会话需选择一个可用模型。

#### Scenario: Click new conversation button

- **WHEN** 用户点击左侧顶部"新建对话"按钮
- **THEN** 系统创建一个新会话
- **AND** 自动切换到该新会话
- **AND** 右侧聊天窗口显示模型选择器和空消息列表

#### Scenario: Auto-generate conversation title

- **WHEN** 用户创建新会话
- **THEN** 系统自动生成会话标题为"新对话 + 序号"（如"新对话 1"）

---

### Requirement: Model Selection

系统 SHALL 在聊天窗口顶部显示模型选择器，支持从可用模型列表中选择一个模型进行对话。

#### Scenario: Display model selector

- **WHEN** 用户进入新创建的会话
- **THEN** 系统在聊天窗口顶部显示模型选择下拉框
- **AND** 下拉框默认显示"请选择模型"占位文本

#### Scenario: Load available models

- **WHEN** 用户点击模型选择下拉框
- **THEN** 系统调用 `/api/models/enabled` 接口获取可用模型列表
- **AND** 在下拉框中显示模型名称和供应商信息

#### Scenario: Select model

- **WHEN** 用户从下拉框选择一个模型
- **THEN** 系统记录该会话关联的模型 ID
- **AND** 模型选择器显示已选模型的名称

#### Scenario: Lock model after first message

- **WHEN** 用户在会话中发送第一条消息后
- **THEN** 系统禁用模型选择器
- **AND** 该会话后续消息均使用该模型

---

### Requirement: Message Display

系统 SHALL 在聊天窗口中部区域显示消息列表，区分用户消息和 AI 回复。

#### Scenario: Display user message

- **WHEN** 用户发送消息
- **THEN** 系统在消息列表右侧显示用户消息气泡
- **AND** 消息气泡背景色为主题色
- **AND** 消息气泡包含消息内容和发送时间

#### Scenario: Display AI message

- **WHEN** AI 返回回复
- **THEN** 系统在消息列表左侧显示 AI 消息气泡
- **AND** 消息气泡背景色为浅灰色
- **AND** 消息气泡包含消息内容、发送时间和模型名称

#### Scenario: Auto-scroll to latest message

- **WHEN** 新消息添加到消息列表
- **THEN** 系统自动滚动到最新消息位置

---

### Requirement: Send Message

系统 SHALL 在聊天窗口底部显示消息输入框和发送按钮，支持用户输入并发送消息。

#### Scenario: Input message

- **WHEN** 用户在输入框中输入文本
- **THEN** 系统实时记录输入内容
- **AND** 发送按钮从禁用状态变为可用状态

#### Scenario: Send message successfully

- **WHEN** 用户点击发送按钮或按下 Enter 键
- **AND** 已选择模型且输入框非空
- **THEN** 系统将用户消息添加到消息列表
- **AND** 调用 `/api/models/{id}/chat` 接口发送请求
- **AND** 清空输入框
- **AND** 显示加载状态（AI 正在回复...）

#### Scenario: Display AI response

- **WHEN** 后端返回 AI 回复
- **THEN** 系统将 AI 回复添加到消息列表
- **AND** 隐藏加载状态
- **AND** 更新对话列表中的最后消息预览和时间戳

#### Scenario: Send without model selection

- **WHEN** 用户未选择模型时点击发送按钮
- **THEN** 系统显示错误提示"请先选择模型"
- **AND** 不发送消息

#### Scenario: Handle API error

- **WHEN** 调用 chat API 失败
- **THEN** 系统显示错误消息"发送失败，请重试"
- **AND** 隐藏加载状态

---

### Requirement: Local Conversation Storage

系统 SHALL 将会话数据存储在浏览器本地存储（localStorage），页面刷新后保持会话状态。

#### Scenario: Save conversation after message sent

- **WHEN** 用户发送消息或收到 AI 回复
- **THEN** 系统将会话数据保存到 localStorage
- **AND** 包含会话 ID、标题、模型 ID、消息列表、创建时间、更新时间

#### Scenario: Load conversations on page load

- **WHEN** 用户进入聊天页面
- **THEN** 系统从 localStorage 读取会话列表
- **AND** 在左侧对话列表中显示所有会话

#### Scenario: Persist active conversation

- **WHEN** 用户刷新页面
- **THEN** 系统恢复上次激活的会话
- **AND** 右侧聊天窗口显示该会话的消息内容

---

### Requirement: Delete Conversation

系统 SHALL 支持用户删除会话，删除前需用户确认。

#### Scenario: Click delete button

- **WHEN** 用户在对话列表项上悬停
- **THEN** 系统显示删除按钮（图标）

#### Scenario: Confirm delete conversation

- **WHEN** 用户点击删除按钮并确认
- **THEN** 系统从对话列表中移除该会话
- **AND** 从 localStorage 中删除该会话数据
- **AND** 如果删除的是当前激活会话，则切换到第一个会话或显示空状态

#### Scenario: Cancel delete

- **WHEN** 用户点击删除按钮后取消确认
- **THEN** 系统不执行删除操作

---

### Requirement: Theme Consistency

系统 SHALL 确保聊天页面与整体应用主题（light/dark）保持一致，包括背景色、文字颜色、边框和消息气泡样式。

#### Scenario: Apply light theme

- **WHEN** 应用处于 light 主题模式
- **THEN** 聊天页面使用浅色背景
- **AND** 消息气泡和对话列表项使用浅色主题样式

#### Scenario: Apply dark theme

- **WHEN** 应用处于 dark 主题模式
- **THEN** 聊天页面使用深色背景
- **AND** 消息气泡和对话列表项使用深色主题样式
- **AND** 文字颜色保持可读性
