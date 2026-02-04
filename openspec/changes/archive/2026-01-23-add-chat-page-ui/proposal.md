# Change: 新增聊天页面 UI

## Why

当前系统已具备模型管理和聊天 API 能力（通过 OpenRouter 集成），但缺少用户友好的聊天交互界面。用户需要一个直观的聊天页面来与
AI 模型进行对话，而不是通过 API 工具测试。

## What Changes

- 在主页面顶部 tab 导航第一个位置新增"聊天"tab
- 实现聊天页面 UI，采用左右分栏布局（左侧对话列表 + 右侧聊天窗口）
- 支持从可用模型列表中选择模型进行对话
- 支持创建新对话会话
- 支持在对话列表中切换会话
- 调用后端 `/api/models/{id}/chat` 接口完成对话
- 先期实现单次对话功能（不支持多轮上下文）

## Impact

### Affected specs

- `chat-ui` - 新建规范，定义聊天页面的交互行为和 UI 要求

### Affected code

- `frontend/src/components/tab-navigation/index.tsx` - 新增"聊天"tab 定义
- `frontend/src/pages/home-page/index.tsx` - 新增聊天页面路由
- `frontend/src/pages/chat-page/` - 新建聊天页面组件（目录）
- `frontend/src/services/chat-api.ts` - 新建聊天 API 服务
- `frontend/src/types/chat.ts` - 新建聊天相关类型定义
