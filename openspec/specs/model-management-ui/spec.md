# model-management-ui Specification

## Purpose

TBD - created by archiving change add-model-management-frontend. Update Purpose after archive.
## Requirements
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

### Requirement: Model Table Hover Readability

系统 SHALL 在模型管理页面的列表行悬停状态下保持可读的文本与背景对比度。

#### Scenario: Hover model list row

- **WHEN** 用户将鼠标悬停在模型列表行
- **THEN** 行背景与文本颜色保持清晰可读且与页面主题一致

### Requirement: Model Modal Theme Consistency

系统 SHALL 使新增、编辑、删除确认弹窗在模型管理页面采用与列表一致的主题（背景、文字、边框）。

#### Scenario: Open create or edit modal

- **WHEN** 用户打开新增或编辑模型弹窗
- **THEN** 弹窗背景、文字与边框与模型页面主题一致且可读

#### Scenario: Open delete confirmation modal

- **WHEN** 用户打开删除确认弹窗
- **THEN** 弹窗背景、文字与边框与模型页面主题一致且可读

### Requirement: Provider Configuration Entry

系统 SHALL 在模型配置页面右上角提供“服务商配置”入口。

#### Scenario: 打开服务商配置弹窗

- **WHEN** 用户点击“服务商配置”入口
- **THEN** 系统打开服务商配置弹窗

### Requirement: Provider Configuration Modal

系统 SHALL 在弹窗中提供服务商选择与配置填写，支持 `OPENROUTER` 与 `OPENAI`。

#### Scenario: 选择服务商与填写字段

- **WHEN** 用户打开弹窗并选择服务商
- **THEN** 表单包含“服务商、API 地址、API Key”三个字段
- **AND** 字段均为必填

#### Scenario: 表单校验

- **WHEN** 用户提交表单但缺少必填字段
- **THEN** 系统提示对应的校验错误

### Requirement: Provider Configuration Persistence UX

系统 SHALL 在不暴露敏感信息的前提下展示配置状态并支持保存。

#### Scenario: 不回显 API Key

- **WHEN** 系统加载已有服务商配置
- **THEN** API 地址字段可回显
- **AND** API Key 字段保持为空或提示“已保存”，不显示明文

#### Scenario: 保存成功

- **WHEN** 用户提交有效配置并保存成功
- **THEN** 系统提示保存成功并关闭弹窗

#### Scenario: 保存失败

- **WHEN** 保存服务商配置失败
- **THEN** 系统提示失败原因且保留表单内容

