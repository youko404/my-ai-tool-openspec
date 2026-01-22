## Context

现有解析状态以任务维度展示，无法满足“查看历史文件进度”的需求。后端目前仅在 `knowledge_files` 中保存文件状态（无进度），且没有文件列表
API。

## Goals / Non-Goals

- Goals:
    - 提供按知识库查询的文件进度列表（含百分比、状态）。
    - 前端弹窗展示文件进度，支持刷新或轮询更新。
- Non-Goals:
    - 不改动知识库上传流程的入口与校验逻辑。
    - 不引入新的文件存储与下载能力。

## Decisions

- Decision: 在 `knowledge_files` 增加 `total_chunks`、`processed_chunks`、`progress_percent` 字段，并在解析过程中更新。
- Decision: 新增 `GET /knowledge-bases/{id}/files` 返回分页列表，优先返回进行中与最新上传的文件。
- Decision: 前端弹窗使用表格展示文件名、大小、状态、进度、上传时间；当存在进行中任务时每 5 秒轮询刷新，全部完成后停止轮询。

## Risks / Trade-offs

- 频繁更新进度会增加数据库写入频率 → 采用按批次/阈值更新策略（例如每 N 个 chunk 更新一次）。

## Migration Plan

1. 扩展 `knowledge_files` 表结构，补充进度字段。
2. 更新异步解析逻辑写入文件级进度。
3. 增加查询 API 与前端 UI。

## Open Questions

- 是否需要在 API 层限制返回总量或默认分页大小？
