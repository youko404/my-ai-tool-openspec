## Context

聊天服务目前仅使用用户当前输入调用模型，且消息表未记录 token usage。需要在请求中携带最近上下文，并在消息详情中返回 token
消耗。

## Goals / Non-Goals

- Goals:
    - 为每次对话记录 prompt/completion/total token，并在消息详情 API 返回。
    - 构建对话上下文窗口：最多 10 条消息，文本总长度最多 5000 字符，且必须包含当前用户消息。
    - 限制用户单条消息内容不超过 5000 字符，超过则拒绝请求。
    - 仅执行一次历史消息查询以生成上下文。
- Non-Goals:
    - 不新增前端展示或统计汇总。
    - 不改变模型选择与路由策略。

## Decisions

- Decision: 将 token usage 字段追加到 `chat_message` 表（prompt_tokens/completion_tokens/total_tokens），默认 0。
- Decision: 使用历史消息构建请求列表，窗口选取时从最新消息向更早消息回溯以满足限制；发送给模型时保持聊天顺序（时间正序）。
- Decision: 窗口长度统计以消息内容字符数累加；若当前用户消息超过 5000 字符，直接拒绝请求并不构建窗口。

## Risks / Trade-offs

- 需要一次性查询全部历史消息，可能带来较大的内存消耗；后续可引入分页或直接按倒序查询并裁剪。
- provider 未返回 usage 时统一写入 0，无法区分“真实为 0”与“未知”。

## Migration Plan

- 添加 DB 列并设置默认值为 0。
- 更新实体/VO/映射逻辑，发布后不影响旧数据读取。

## Open Questions

- 无。
