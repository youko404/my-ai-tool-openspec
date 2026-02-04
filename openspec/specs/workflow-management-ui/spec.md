# workflow-management-ui Specification

## Purpose

TBD - created by archiving change add-workflow-management-ui. Update Purpose after archive.

## Requirements

### Requirement: Workflow List Display

系统 SHALL 在工作流管理页面展示工作流列表，包含名称、描述、操作列。

#### Scenario: User views workflow list

- **WHEN** 用户进入工作流管理页面
- **THEN** 系统展示分页的工作流列表表格
- **AND** 表格包含以下列：名称、描述、操作

#### Scenario: Empty workflow list

- **WHEN** 系统中无任何工作流数据
- **THEN** 系统展示空状态提示 "暂无工作流"

---

### Requirement: Workflow Pagination

系统 SHALL 支持工作流列表分页，默认每页 10 条。

#### Scenario: Navigate to next page

- **WHEN** 用户点击下一页
- **THEN** 系统加载并展示下一页数据

#### Scenario: Change page size

- **WHEN** 用户修改每页显示数量
- **THEN** 系统按新的页大小重新加载数据

---

### Requirement: Workflow Search

系统 SHALL 支持按关键字搜索工作流（工作流名称或描述模糊匹配）。

#### Scenario: Search by keyword

- **WHEN** 用户在搜索框输入关键字并点击搜索或按回车
- **THEN** 系统展示匹配的工作流列表
- **AND** 重置分页到第一页

#### Scenario: Clear search

- **WHEN** 用户清空搜索框或点击清除按钮
- **THEN** 系统展示所有工作流列表

---

### Requirement: Create Workflow

系统 SHALL 支持通过表单弹窗创建新工作流，表单字段包含：工作流名称（必填）、描述（可选）。

#### Scenario: Open create workflow modal

- **WHEN** 用户点击 "新增工作流" 按钮
- **THEN** 系统打开新增工作流表单弹窗
- **AND** 表单包含名称和描述字段

#### Scenario: Create workflow successfully

- **WHEN** 用户填写工作流名称并点击确认
- **THEN** 系统调用创建 API
- **AND** 创建成功后关闭弹窗并刷新列表
- **AND** 显示成功提示消息

#### Scenario: Create workflow with invalid input

- **WHEN** 用户未填写必填字段（名称）并点击确认
- **THEN** 系统展示验证错误提示 "请输入工作流名称"

#### Scenario: Cancel create

- **WHEN** 用户点击取消按钮或关闭弹窗
- **THEN** 系统关闭弹窗且不创建工作流

---

### Requirement: Edit Workflow

系统 SHALL 支持编辑已存在的工作流信息。

#### Scenario: Open edit workflow modal

- **WHEN** 用户点击工作流操作列的 "编辑" 按钮
- **THEN** 系统打开编辑工作流表单弹窗
- **AND** 表单回显当前工作流的名称和描述

#### Scenario: Edit workflow successfully

- **WHEN** 用户修改工作流信息并点击确认
- **THEN** 系统调用更新 API
- **AND** 更新成功后关闭弹窗并刷新列表
- **AND** 显示成功提示消息

#### Scenario: Edit with no changes

- **WHEN** 用户未修改任何字段直接点击确认
- **THEN** 系统可选择性跳过 API 调用或提示 "未修改任何内容"

---

### Requirement: Delete Workflow

系统 SHALL 支持删除工作流，删除前需用户确认。

#### Scenario: Open delete confirmation

- **WHEN** 用户点击工作流操作列的 "删除" 按钮
- **THEN** 系统打开删除确认弹窗
- **AND** 弹窗显示 "确认删除工作流 [工作流名称] 吗？"

#### Scenario: Delete workflow with confirmation

- **WHEN** 用户在确认弹窗中点击确认
- **THEN** 系统调用删除 API
- **AND** 删除成功后刷新列表
- **AND** 显示成功提示消息

#### Scenario: Cancel delete

- **WHEN** 用户在确认弹窗中点击取消
- **THEN** 系统关闭弹窗且不执行删除操作

---

### Requirement: Copy Workflow

系统 SHALL 支持复制工作流，创建具有相同属性的工作流副本。

#### Scenario: Copy workflow successfully

- **WHEN** 用户点击工作流操作列的 "复制" 按钮
- **THEN** 系统调用复制 API
- **AND** 复制成功后刷新列表
- **AND** 新工作流名称为原名称加 " (副本)" 后缀
- **AND** 显示成功提示消息

#### Scenario: Copy workflow failure

- **WHEN** 复制工作流失败（如网络错误或工作流已被删除）
- **THEN** 系统显示错误提示消息

---

### Requirement: Workflow Action Buttons Layout

系统 SHALL 在工作流列表每行的最右侧提供操作按钮组。

#### Scenario: Action buttons display

- **WHEN** 用户查看工作流列表
- **THEN** 每行最右侧显示操作按钮组
- **AND** 按钮包含：编辑、删除、复制
- **AND** 按钮以图标或文字形式展示（可配置）

#### Scenario: Action buttons hover state

- **WHEN** 用户将鼠标悬停在操作按钮上
- **THEN** 按钮显示悬停效果（颜色变化或提示文字）

---

### Requirement: Workflow Form Modal

系统 SHALL 提供可复用的工作流表单弹窗组件，支持新增和编辑模式。

#### Scenario: Form modal component structure

- **WHEN** 系统渲染工作流表单弹窗
- **THEN** 弹窗组件位于 `frontend/src/components/workflow-form-modal/`
- **AND** 组件接收 `mode`（create/edit）、`workflow`（编辑时的初始数据）、`onSuccess`（成功回调）等 props

#### Scenario: Form validation

- **WHEN** 用户提交表单
- **THEN** 系统验证名称字段非空
- **AND** 验证名称长度不超过 255 字符
- **AND** 验证描述长度不超过 1000 字符

---

### Requirement: Workflow API Service Layer

系统 SHALL 提供前端 API 服务层封装后端工作流接口调用。

#### Scenario: API service implementation

- **WHEN** 前端需要调用工作流相关接口
- **THEN** 使用 `frontend/src/services/workflow-api.ts` 中的方法
- **AND** 提供以下方法：
    - `getWorkflows(page, size, keyword)` - 获取工作流列表
    - `getWorkflow(id)` - 获取单个工作流
    - `createWorkflow(data)` - 创建工作流
    - `updateWorkflow(id, data)` - 更新工作流
    - `deleteWorkflow(id)` - 删除工作流
    - `copyWorkflow(id)` - 复制工作流

---

### Requirement: Workflow TypeScript Types

系统 SHALL 定义工作流相关的 TypeScript 类型。

#### Scenario: Type definitions

- **WHEN** 前端代码中使用工作流数据
- **THEN** 使用 `frontend/src/types/workflow.ts` 中定义的类型
- **AND** 包含以下类型定义：
    - `Workflow` - 工作流对象（id, name, description, enabled, deleted, createTime, updateTime 等）
    - `WorkflowDTO` - 创建/更新工作流的数据传输对象
    - `WorkflowListResponse` - 分页列表响应对象

---

### Requirement: Loading and Error States

系统 SHALL 在工作流列表加载和操作过程中提供加载状态和错误提示。

#### Scenario: Loading state during data fetch

- **WHEN** 系统正在加载工作流列表数据
- **THEN** 显示加载指示器（Spinner 或 Skeleton）

#### Scenario: Error state on API failure

- **WHEN** 工作流 API 调用失败（网络错误或服务器错误）
- **THEN** 显示错误提示消息
- **AND** 提供重试选项（可选）

---

### Requirement: Theme Consistency (Light/Dark Mode)

系统 SHALL 确保工作流管理页面在明亮和暗黑模式下的 UI 一致性。

#### Scenario: Dark mode display

- **WHEN** 用户切换到暗黑模式
- **THEN** 工作流列表表格、按钮、弹窗的背景色为暗色
- **AND** 文字颜色为亮色，确保可读性

#### Scenario: Light mode display

- **WHEN** 用户切换到明亮模式
- **THEN** 工作流列表表格、按钮、弹窗的背景色为亮色
- **AND** 文字颜色为暗色，确保可读性

#### Scenario: Modal theme consistency

- **WHEN** 用户打开新增、编辑或删除确认弹窗
- **THEN** 弹窗的背景、文字、边框颜色与当前主题一致

