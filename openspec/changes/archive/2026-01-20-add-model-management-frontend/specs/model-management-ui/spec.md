# Capability: Model Management UI

模型管理前端界面，支持用户通过 Web 页面对 AI 模型进行增删改查操作。

## ADDED Requirements

### Requirement: Model List Display

系统 SHALL 在模型管理页面展示模型列表，包含 ID、模型名称、供应商、状态、创建时间、操作列。

#### Scenario: User views model list

- **WHEN** 用户进入模型管理页面
- **THEN** 系统展示分页的模型列表表格

#### Scenario: Empty model list

- **WHEN** 系统中无任何模型数据
- **THEN** 系统展示空状态提示

---

### Requirement: Model Pagination

系统 SHALL 支持模型列表分页，默认每页 10 条。

#### Scenario: Navigate to next page

- **WHEN** 用户点击下一页
- **THEN** 系统加载并展示下一页数据

---

### Requirement: Model Search

系统 SHALL 支持按关键字搜索模型（模型名称模糊匹配）。

#### Scenario: Search by keyword

- **WHEN** 用户输入搜索关键字并提交
- **THEN** 系统展示匹配的模型列表

---

### Requirement: Create Model

系统 SHALL 支持通过表单弹窗创建新模型，表单字段包含：模型名称（必填）、供应商（必填）、是否启用。

#### Scenario: Create model successfully

- **WHEN** 用户填写有效表单并点击确认
- **THEN** 系统创建模型并刷新列表

#### Scenario: Create model with invalid input

- **WHEN** 用户未填写必填字段并点击确认
- **THEN** 系统展示验证错误提示

---

### Requirement: Edit Model

系统 SHALL 支持编辑已存在的模型信息。

#### Scenario: Edit model successfully

- **WHEN** 用户修改模型信息并点击确认
- **THEN** 系统更新模型并刷新列表

---

### Requirement: Delete Model

系统 SHALL 支持删除模型，删除前需用户确认。

#### Scenario: Delete model with confirmation

- **WHEN** 用户点击删除按钮并确认
- **THEN** 系统删除模型并刷新列表

#### Scenario: Cancel delete

- **WHEN** 用户点击删除按钮后取消确认
- **THEN** 系统不执行删除操作

---

### Requirement: Toggle Model Status

系统 SHALL 支持通过开关切换模型的启用/禁用状态。

#### Scenario: Enable model

- **WHEN** 用户将开关切换为启用
- **THEN** 系统将模型状态更新为启用

#### Scenario: Disable model

- **WHEN** 用户将开关切换为禁用
- **THEN** 系统将模型状态更新为禁用
