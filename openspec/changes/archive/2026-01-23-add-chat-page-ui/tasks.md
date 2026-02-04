## 1. 准备工作

- [x] 1.1 阅读 `proposal.md` 和 `design.md`（如果存在）
- [x] 1.2 确认后端 `/api/models/enabled` 和 `/api/models/{id}/chat` 接口可用
- [x] 1.3 检查现有 tab 导航组件结构

## 2. 类型定义

- [x] 2.1 创建 `frontend/src/types/chat.ts`，定义聊天相关类型
    - `ChatMessage` - 消息类型（role, content, timestamp）
    - `Conversation` - 会话类型（id, title, modelId, messages, createdAt, updatedAt）
    - `ChatRequest` - 请求类型（messages, temperature, maxTokens）
    - `ChatResponse` - 响应类型（content, model, usage）

## 3. API 服务

- [x] 3.1 创建 `frontend/src/services/chat-api.ts`
    - 实现 `sendMessage(modelId: number, request: ChatRequest)` 方法
    - 调用 `POST /api/models/{id}/chat` 接口

## 4. Tab 导航更新

- [x] 4.1 修改 `frontend/src/components/tab-navigation/index.tsx`
    - 在 `TabKey` 类型中新增 `'chat'` 类型
    - 在 `tabs` 数组第一个位置插入聊天 tab 配置（key: 'chat', label: '聊天', icon: MessageOutlined）

- [x] 4.2 修改 `frontend/src/pages/home-page/index.tsx`
    - 默认激活 tab 改为 `'chat'`
    - 在 `renderContent()` 中新增 `'chat'` case，返回 `<ChatPage />`

## 5. 聊天页面组件

- [x] 5.1 创建 `frontend/src/pages/chat-page/index.tsx` 主组件
    - 实现左右分栏布局（Flex 布局）
    - 管理会话列表状态（conversations）
    - 管理当前激活会话状态（activeConversationId）
    - 实现 localStorage 持久化逻辑

- [x] 5.2 创建 `frontend/src/pages/chat-page/conversation-list.tsx` 对话列表组件
    - 显示会话列表
    - 支持创建新会话按钮
    - 支持选择和删除会话
    - 显示会话标题、最后消息预览和时间戳

- [x] 5.3 创建 `frontend/src/pages/chat-page/chat-window.tsx` 聊天窗口组件
    - 顶部：模型选择器（Select 组件）
    - 中部：消息列表区域（可滚动）
    - 底部：消息输入框和发送按钮

- [x] 5.4 创建 `frontend/src/pages/chat-page/message-list.tsx` 消息列表组件
    - 渲染用户消息和 AI 消息
    - 区分左右对齐样式
    - 显示消息时间戳和模型信息
    - 自动滚动到最新消息

- [x] 5.5 创建 `frontend/src/pages/chat-page/message-input.tsx` 消息输入组件
    - 多行文本输入框（TextArea）
    - 发送按钮
    - 支持 Enter 键发送（Shift+Enter 换行）
    - 输入框为空时禁用发送按钮

## 6. 样式实现

- [x] 6.1 创建 `frontend/src/pages/chat-page/chat-page.css`
    - 实现左右分栏布局样式
    - 响应式布局（小屏幕隐藏左侧栏）
    - 主题适配（light/dark）

- [x] 6.2 创建消息气泡样式
    - 用户消息：右对齐，主题色背景
    - AI 消息：左对齐，灰色背景
    - 圆角、阴影、间距等细节

- [x] 6.3 创建对话列表项样式
    - 悬停效果
    - 激活状态高亮
    - 删除按钮显示/隐藏动画

## 7. 功能实现

- [x] 7.1 实现创建新会话逻辑
    - 生成唯一会话 ID（UUID 或时间戳）
    - 自动生成会话标题（"新对话 + 序号"）
    - 添加到会话列表并激活

- [x] 7.2 实现模型选择逻辑
    - 调用 `/api/models/enabled` 接口获取可用模型
    - 保存选中的模型 ID 到会话数据
    - 发送第一条消息后禁用模型选择器

- [x] 7.3 实现发送消息逻辑
    - 验证模型已选择
    - 添加用户消息到消息列表
    - 调用 chat API
    - 显示加载状态
    - 处理 API 响应（成功/失败）
    - 更新会话的最后消息和时间戳

- [x] 7.4 实现 localStorage 持久化
    - 发送消息后保存会话数据
    - 页面加载时恢复会话列表
    - 恢复上次激活的会话

- [x] 7.5 实现删除会话逻辑
    - 显示确认弹窗
    - 从列表中移除会话
    - 从 localStorage 中删除数据
    - 处理删除当前激活会话的情况

## 8. 测试验证

- [x] 8.1 验证 tab 导航显示和切换功能
- [x] 8.2 验证创建新会话功能
- [x] 8.3 验证模型选择和锁定功能
- [x] 8.4 验证发送消息和接收回复功能
- [x] 8.5 验证会话切换功能
- [x] 8.6 验证删除会话功能
- [x] 8.7 验证 localStorage 持久化功能（刷新页面）
- [x] 8.8 验证响应式布局（不同屏幕尺寸）
- [x] 8.9 验证主题切换（light/dark）
- [x] 8.10 验证错误处理（API 失败、未选择模型等）

## 9. 优化和调整

- [x] 9.1 调整样式细节，确保与整体设计一致
- [x] 9.2 优化加载状态提示
- [x] 9.3 优化错误提示信息
- [x] 9.4 添加必要的 loading 动画
