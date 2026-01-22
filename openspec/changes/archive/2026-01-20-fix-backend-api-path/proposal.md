# Change: Fix Backend Model API Path Configuration

## Why

后端 API 路径配置存在重复前缀问题。`application.yml` 配置了 `context-path: /api`，同时 `AiModelController` 使用
`@RequestMapping("/api/models")`，导致实际 API 路径为 `/api/api/models`，而前端期望的路径是 `/api/models`。

## What Changes

- 修改 `AiModelController` 的 `@RequestMapping` 从 `/api/models` 改为 `/models`
- 这样结合 `context-path: /api`，实际路径将变为 `/api/models`，与前端期望一致

## Impact

- Affected specs: model-management-api (新建)
- Affected code:
    - `backend/src/main/java/com/ai/content/controller/AiModelController.java`
- **BREAKING**: 修改后 API 路径从 `/api/api/models` 变为 `/api/models`
