# Change: Add knowledge base file progress modal

## Why

当前知识库仅展示任务级别进度，无法查看每个文件的解析进度，影响排查和使用体验。

## What Changes

- 在知识库列表新增“文件进度”入口，打开弹窗展示所有历史上传文件的进度列表。
- 新增后端 API 返回文件级进度（状态、百分比等）并支持分页。
- 在向量库表中持久化文件级进度字段，异步解析过程中持续更新。

## Impact

- Affected specs: knowledge-base-management-ui, knowledge-base-management-api
- Affected code: backend ingestion worker/repository/controller, frontend knowledge base page and services
- Data: PgVector `knowledge_files` schema update
