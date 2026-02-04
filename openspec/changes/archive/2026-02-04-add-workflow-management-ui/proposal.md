# Change: 实现工作流管理页面功能

## Why

前端工作流页面目前仅为空占位符，用户无法管理工作流。需要实现完整的工作流管理界面，允许用户查看、创建、编辑、删除和复制工作流。同时需要实现后端
API 来支持这些操作。

## What Changes

- 在后端实现工作流 CRUD API (`/api/workflows`)
- 创建 Workflow 实体（包含 id、name、description、enabled、deleted 等字段）
- 实现 Controller/Service/Repository 分层
- 在前端 `workflow-page` 中实现工作流列表展示（支持分页、搜索）
- 实现新增工作流的表单弹窗（包含名称、描述字段）
- 实现编辑工作流的表单弹窗
- 实现删除工作流的确认弹窗
- 实现复制工作流功能
- 新增前端 API 服务层封装后端接口调用
- 新增 TypeScript 类型定义

## Impact

- Affected specs:
    - `workflow-management-api` (新增)
    - `workflow-management-ui` (新增)
- Affected code:
    - Backend:
        - `backend/src/main/java/com/youko404/tool/entity/Workflow.java` (新增)
        - `backend/src/main/java/com/youko404/tool/controller/WorkflowController.java` (新增)
        - `backend/src/main/java/com/youko404/tool/service/WorkflowService.java` (新增)
        - `backend/src/main/java/com/youko404/tool/repository/WorkflowRepository.java` (新增)
        - `backend/src/main/java/com/youko404/tool/dto/WorkflowDTO.java` (新增)
    - Frontend:
        - `frontend/src/pages/workflow-page/index.tsx`
        - `frontend/src/pages/workflow-page/workflow-page.css`
        - `frontend/src/services/workflow-api.ts` (新增)
        - `frontend/src/types/workflow.ts` (新增)
        - `frontend/src/components/workflow-form-modal/` (新增)
