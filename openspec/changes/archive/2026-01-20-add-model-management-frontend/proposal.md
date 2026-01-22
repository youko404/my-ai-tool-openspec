# Change: 实现模型管理页面前端功能

## Why

后端已实现完整的模型 CRUD API (`/api/models`)，但前端模型页面目前仅为空占位符。需要实现完整的模型管理界面，允许用户查看、创建、编辑、删除和切换模型状态。

## What Changes

- 在 `model-page` 中实现模型列表展示（支持分页、搜索）
- 实现新增模型的表单弹窗
- 实现编辑模型的表单弹窗
- 实现删除模型的确认弹窗
- 实现模型启用/禁用的状态切换
- 新增 API 服务层封装后端接口调用
- 新增 TypeScript 类型定义

## Impact

- Affected specs: `model-management-ui` (新增)
- Affected code:
    - `frontend/src/pages/model-page/index.tsx`
    - `frontend/src/pages/model-page/model-page.css`
    - `frontend/src/services/model-api.ts` (新增)
    - `frontend/src/types/model.ts` (新增)
    - `frontend/src/components/model-form-modal/` (新增)
