# Tasks: Fix Backend Model API Path

## 1. Implementation

- [ ] 1.1 修改 `AiModelController` 的 `@RequestMapping` 从 `/api/models` 改为 `/models`

## 2. Verification

- [ ] 2.1 启动后端服务并验证 API 路径 `/api/models` 返回 200
- [ ] 2.2 验证前端模型管理页面能够正常加载数据
- [ ] 2.3 验证 CRUD 操作（创建、读取、更新、删除）正常工作
- [ ] 2.4 验证分页和搜索功能正常工作
