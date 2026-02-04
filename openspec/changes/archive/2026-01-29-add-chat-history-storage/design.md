# Design: Chat history persistence

## Overview

在后端引入会话与消息持久化，前端改为从服务端加载历史会话列表。会话在首条用户消息发送时创建，并记录用户输入、AI
输出及失败时的供应商原始响应。当前阶段不按用户隔离，所有会话为全局可见；删除采用软删除。

## Data Model

- **chat_conversation**
    - id
    - title（默认“新对话 {序号}”或首条消息截断）
    - model_id / model_name（保存模型快照）
    - last_message_preview（可选，用于列表展示）
    - is_deleted
    - created_at / updated_at
- **chat_message**
    - id
    - conversation_id
    - role（user/assistant）
    - content
    - status（success/error）
    - error_message（错误摘要）
    - provider_response（原始供应商响应 JSON/文本，仅在错误时必填）
    - model（响应模型名）
    - created_at

说明：保留 provider_response 用于排障审计；当前不存储用户信息。

## API Shape (proposed)

- `POST /chat/conversations`（首次发送时创建并写入消息）
    - request: modelId, messages（目前仅首条 user 消息）
    - response: conversationId, assistantMessage, error (可选)
- `POST /chat/conversations/{id}/messages`（追加消息）
- `GET /chat/conversations`（返回列表，按 updated_at 倒序）
- `GET /chat/conversations/{id}`（返回会话详情与消息）
- `DELETE /chat/conversations/{id}`（软删除）

与现有 `/models/{id}/chat` 的集成方式：可在实现阶段决定保留旧接口并内部复用新逻辑，或将前端切到新接口。

## Flow

1. 用户发送首条消息 → 后端创建会话与用户消息记录。
2. 调用供应商 → 成功则写入 assistant 消息；失败则写入 error 状态的 assistant 消息，并保存原始响应。
3. 前端打开聊天页 → 调用列表接口并按 updated_at 倒序渲染左侧对话列表。
4. 选中会话 → 拉取详情并展示消息。

## Soft Delete

会话删除标记 `is_deleted=true`，消息可保持不变；列表与详情查询默认过滤删除项。
