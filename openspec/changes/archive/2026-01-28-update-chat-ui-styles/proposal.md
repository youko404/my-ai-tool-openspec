# Change: Update Chat UI Dark Mode Contrast and Layout Height

## Why

当前聊天页面在暗黑模式下存在可读性问题：模型选择与用户消息文本对比度不足，且用户消息背景与回复不一致，影响识别与阅读。

## What Changes

- 调整暗黑模式下聊天页面的消息与控件配色，提升可读性。
- 统一暗黑模式下用户消息与 AI 回复的气泡背景色。
- 明确聊天页面整体高度占页面 90%。

## Impact

- Affected specs: chat-ui (new)
- Affected code: frontend/src/pages/chat-page/chat-page.css, frontend/src/pages/chat-page/chat-window.tsx,
  frontend/src/pages/chat-page/message-input.tsx
