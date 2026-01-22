# Design: 模型管理页面

## Context

后端已提供完整的模型 CRUD API，前端需实现对应的管理界面。项目使用 React + TypeScript + Ant Design 技术栈。

## Goals / Non-Goals

- **Goals**:
    - 提供直观的模型管理界面
    - 支持模型的增删改查
    - 支持分页、搜索
    - 状态切换（启用/禁用）
- **Non-Goals**:
    - 暂不实现批量操作
    - 暂不实现模型配置详情（如 API Key 等敏感信息）

## Decisions

### 组件结构

```
model-page/
├── index.tsx           # 主页面（列表 + 操作）
├── model-page.css      # 页面样式
components/
└── model-form-modal/
    ├── index.tsx       # 表单弹窗组件
    └── model-form-modal.css
services/
└── model-api.ts        # API 服务封装
types/
└── model.ts            # 类型定义
```

### API 调用方式

- 使用已有的 `services/http.ts` 作为基础 HTTP 客户端
- 新建 `model-api.ts` 封装具体业务接口

### 状态管理

- 使用 React useState + useEffect 管理本地状态
- 暂不引入全局状态管理（Redux/Zustand），保持简单

### UI 组件

- 使用 Ant Design 的 Table、Modal、Form、Input、Switch、Button、message 等组件

## Risks / Trade-offs

- **风险**: 后端 API 返回格式变化 → 需要在类型定义中保持同步
- **缓解**: 使用 TypeScript 严格类型检查

## Open Questions

- 无
