# Tasks: 工作流管理功能实现

## 1. 后端基础设施

- [ ] 1.1 创建 `Workflow` 实体类（id, name, description, enabled, deleted, creator, editor, createTime, updateTime）
- [ ] 1.2 创建 `WorkflowDTO` 数据传输对象
- [ ] 1.3 创建 `WorkflowRepository` 接口

## 2. 后端业务层

- [ ] 2.1 实现 `WorkflowService` 业务逻辑（CRUD 操作）
- [ ] 2.2 实现分页查询和关键字搜索功能
- [ ] 2.3 实现工作流复制功能（创建副本）
- [ ] 2.4 实现软删除逻辑

## 3. 后端 API 层

- [ ] 3.1 创建 `WorkflowController` 实现 RESTful API
- [ ] 3.2 实现 GET `/api/workflows` - 分页查询工作流列表
- [ ] 3.3 实现 GET `/api/workflows/{id}` - 获取单个工作流详情
- [ ] 3.4 实现 POST `/api/workflows` - 创建工作流
- [ ] 3.5 实现 PUT `/api/workflows/{id}` - 更新工作流
- [ ] 3.6 实现 DELETE `/api/workflows/{id}` - 删除工作流（软删除）
- [ ] 3.7 实现 POST `/api/workflows/{id}/copy` - 复制工作流

## 4. 前端基础设施

- [ ] 4.1 创建 `frontend/src/types/workflow.ts` 定义 Workflow 相关 TypeScript 类型
- [ ] 4.2 创建 `frontend/src/services/workflow-api.ts` 封装工作流 API 调用

## 5. 前端工作流列表功能

- [ ] 5.1 实现工作流列表表格展示（name, description, 操作列）
- [ ] 5.2 实现分页功能
- [ ] 5.3 实现关键字搜索功能
- [ ] 5.4 实现操作列按钮组（编辑、删除、复制）

## 6. 前端工作流表单弹窗

- [ ] 6.1 创建 `WorkflowFormModal` 组件
- [ ] 6.2 实现新增工作流功能（包含名称、描述字段，表单验证 + API 调用）
- [ ] 6.3 实现编辑工作流功能（回显数据 + 提交更新）

## 7. 前端删除与复制功能

- [ ] 7.1 实现删除确认弹窗
- [ ] 7.2 调用删除 API 并刷新列表
- [ ] 7.3 实现复制工作流功能（调用复制 API 并刷新列表）

## 8. 样式与交互

- [ ] 8.1 更新 `workflow-page.css` 添加表格与按钮样式
- [ ] 8.2 添加加载状态与错误提示
- [ ] 8.3 确保明亮和暗黑模式下的 UI 一致性

## 9. 验证

- [ ] 9.1 后端单元测试通过
- [ ] 9.2 TypeScript 构建验证通过
- [ ] 9.3 手动验证所有 CRUD 和复制操作正常（需启动服务）
