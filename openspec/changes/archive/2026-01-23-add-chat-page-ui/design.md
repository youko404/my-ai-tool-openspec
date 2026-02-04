# 设计文档：聊天页面 UI

## Context

当前系统已实现模型管理和后端聊天 API（通过 `add-openrouter-provider` 变更），但用户无法在前端界面直接与 AI
模型进行对话。本变更旨在添加一个类似 ChatGPT 的聊天界面，提供友好的用户交互体验。

聊天页面采用客户端存储方案（localStorage），不涉及后端会话管理，符合系统当前"无状态对话"的设计理念。

## Goals / Non-Goals

### Goals

- 提供直观的聊天界面，支持创建多个对话会话
- 支持从可用模型列表中选择模型进行对话
- 实现单次对话功能（每条消息独立调用后端 API）
- 使用 localStorage 持久化会话数据，页面刷新后保持状态
- 适配现有的主题系统（light/dark）
- 响应式布局，支持移动端访问

### Non-Goals

- 不实现多轮上下文对话（由后续变更支持）
- 不实现流式响应（SSE）
- 不实现会话云端同步（无后端存储）
- 不实现高级功能（如会话分享、导出、搜索等）

## Decisions

### Decision 1: 采用左右分栏布局

- **Rationale**: 参考主流 AI 聊天产品（ChatGPT、Claude），左侧对话列表便于会话管理，右侧聊天窗口专注对话交互
- **Implementation**: 使用 CSS Flexbox 实现，左侧固定宽度 300px，右侧自适应
- **Responsive**: 小屏幕（< 768px）自动隐藏左侧栏，通过按钮切换显示

### Decision 2: 使用 localStorage 存储会话数据

- **Rationale**:
    - 当前后端无会话管理功能，避免增加后端复杂度
    - localStorage 足够支持前端会话持久化需求
    - 数据量小（纯文本消息），不涉及大文件或媒体内容
- **Data Structure**:
  ```typescript
  interface Conversation {
    id: string              // 唯一标识（UUID 或时间戳）
    title: string           // 会话标题
    modelId: number         // 关联的模型 ID
    modelName: string       // 模型名称（冗余存储，便于显示）
    messages: ChatMessage[] // 消息列表
    createdAt: number       // 创建时间（时间戳）
    updatedAt: number       // 更新时间（时间戳）
  }

  interface ChatMessage {
    role: 'user' | 'assistant'
    content: string
    timestamp: number
    model?: string          // AI 消息附带的模型信息
  }
  ```
- **Storage Key**: `chat_conversations`
- **Limitations**: localStorage 容量限制（通常 5-10MB），超过限制时提示用户删除旧会话

### Decision 3: 模型选择后锁定

- **Rationale**:
    - 简化 UI 逻辑，避免用户在会话中频繁切换模型导致混乱
    - 符合"单次对话"的设计理念
    - 如需使用不同模型，可创建新会话
- **Implementation**: 发送第一条消息后，禁用模型选择器（disabled 状态）

### Decision 4: 单次对话（不传递历史上下文）

- **Rationale**:
    - 当前后端 chat API 支持传递 messages 数组，但先期不实现多轮上下文
    - 简化实现逻辑，降低初期开发成本
    - 未来可通过变更升级为多轮对话
- **Implementation**: 每次调用 `/api/models/{id}/chat` 仅传递当前用户消息，不传递历史消息
- **Future Enhancement**: 后续可添加"多轮模式"开关，支持传递完整对话历史

### Decision 5: 组件拆分策略

- **ChatPage**: 主容器组件，管理全局状态（conversations, activeConversationId）
- **ConversationList**: 左侧对话列表，负责显示和管理会话
- **ChatWindow**: 右侧聊天窗口，包含模型选择器、消息列表、输入框
- **MessageList**: 消息列表子组件，负责渲染消息气泡
- **MessageInput**: 消息输入子组件，负责输入和发送逻辑
- **Rationale**: 单一职责原则，便于维护和测试

### Decision 6: 使用 Ant Design 组件库

- **Components**:
    - `Select` - 模型选择器
    - `Input.TextArea` - 消息输入框
    - `Button` - 发送按钮、新建对话按钮
    - `Modal.confirm` - 删除确认弹窗
    - `Spin` - 加载状态
    - `Empty` - 空状态提示
- **Rationale**: 项目已使用 Ant Design，保持技术栈一致性

## Risks / Trade-offs

### Risk 1: localStorage 容量限制

- **Mitigation**:
    - 限制每个会话最多保存 100 条消息
    - 提供清理旧会话的功能
    - 在存储失败时提示用户

### Risk 2: 数据丢失风险

- **Mitigation**:
    - 用户清除浏览器数据会导致会话丢失（提示用户注意）
    - 未来可添加导出/导入功能

### Risk 3: 跨设备同步问题

- **Limitation**: localStorage 仅限单设备，不支持跨设备同步
- **Future Enhancement**: 后续可引入后端会话存储和云同步功能

## Alternatives Considered

### Alternative 1: 使用 IndexedDB 替代 localStorage

- **Pros**: 更大的存储容量，支持复杂查询
- **Cons**: API 复杂度高，对当前需求而言过度设计
- **Decision**: 先使用 localStorage，未来如需扩展再迁移

### Alternative 2: 直接复用"工作流"tab 而非新建"聊天"tab

- **Pros**: 减少一个 tab，界面更简洁
- **Cons**: "工作流"和"聊天"是不同的功能定位，合并会导致混乱
- **Decision**: 独立创建"聊天"tab，保持功能清晰分离

### Alternative 3: 实现流式响应（SSE）

- **Pros**: 更好的用户体验，实时展示 AI 生成内容
- **Cons**: 增加实现复杂度，后端需额外支持流式接口
- **Decision**: 先期不实现，标记为未来增强功能

## Migration Plan

本变更为新增功能，无数据迁移需求。

部署步骤：

1. 合并前端代码到主分支
2. 部署前端静态资源
3. 验证聊天页面功能正常

回滚策略：

- 如发现严重问题，可通过 Git 回滚前端代码
- localStorage 数据由用户自行管理，无需清理

## Open Questions

1. **会话标题生成策略**: 当前使用"新对话 + 序号"，未来是否支持根据首条消息自动生成标题？
    - **Answer**: 先期使用简单方案，后续可通过 LLM 生成有意义的标题

2. **消息长度限制**: 是否需要限制单条消息的最大长度？
    - **Answer**: 前端不做限制，依赖后端模型的 maxTokens 参数控制

3. **错误重试机制**: 消息发送失败后是否支持重试？
    - **Answer**: 先期仅提示错误，用户可手动重新发送，未来可添加自动重试功能
