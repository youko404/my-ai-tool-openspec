# workflow-management-api Specification

## Purpose

TBD - created by archiving change add-workflow-management-ui. Update Purpose after archive.

## Requirements

### Requirement: Workflow API Path Configuration

后端工作流管理 API 的路径 SHALL 遵循 RESTful 规范，结合 Spring Boot context-path 配置，最终暴露为 `/api/workflows`。

#### Scenario: API path follows convention

- **WHEN** 后端服务启动
- **THEN** 工作流管理 API 在 `/api/workflows` 路径可访问

#### Scenario: Frontend compatibility

- **WHEN** 前端调用 `/api/workflows` 端点
- **THEN** 后端正确响应并返回工作流数据

---

### Requirement: Workflow CRUD Operations

系统 SHALL 提供完整的工作流 CRUD 操作接口。

#### Scenario: Create workflow successfully

- **WHEN** 用户通过 POST `/api/workflows` 提交包含 `name` 和 `description` 的有效数据
- **THEN** 系统创建新工作流并返回工作流对象（包含自动生成的 id、创建时间等）
- **AND** HTTP 状态码为 201

#### Scenario: Get workflow list with pagination

- **WHEN** 用户通过 GET `/api/workflows?page=0&size=10` 请求工作流列表
- **THEN** 系统返回分页的工作流列表
- **AND** 响应包含 `content`、`totalElements`、`totalPages`、`number`、`size` 字段

#### Scenario: Get single workflow by ID

- **WHEN** 用户通过 GET `/api/workflows/{id}` 请求指定工作流
- **THEN** 系统返回该工作流的完整信息
- **AND** HTTP 状态码为 200

#### Scenario: Update workflow successfully

- **WHEN** 用户通过 PUT `/api/workflows/{id}` 提交更新的 `name` 或 `description`
- **THEN** 系统更新工作流信息并返回更新后的对象
- **AND** 更新 `updateTime` 字段

#### Scenario: Delete workflow (soft delete)

- **WHEN** 用户通过 DELETE `/api/workflows/{id}` 删除工作流
- **THEN** 系统执行软删除，将 `deleted` 字段设置为 true
- **AND** HTTP 状态码为 204
- **AND** 该工作流不再出现在列表查询结果中

#### Scenario: Workflow not found

- **WHEN** 用户请求不存在的工作流 ID
- **THEN** 系统返回 404 错误并提示 "Workflow not found"

---

### Requirement: Workflow Copy Operation

系统 SHALL 支持复制工作流，创建具有相同属性的新工作流副本。

#### Scenario: Copy workflow successfully

- **WHEN** 用户通过 POST `/api/workflows/{id}/copy` 复制指定工作流
- **THEN** 系统创建新工作流，名称为原工作流名称加 " (副本)" 后缀
- **AND** 复制 `description` 和其他可复制属性
- **AND** 生成新的 id、创建时间等字段
- **AND** HTTP 状态码为 201

#### Scenario: Copy non-existent workflow

- **WHEN** 用户尝试复制不存在的工作流
- **THEN** 系统返回 404 错误并提示 "Workflow not found"

---

### Requirement: Workflow Search by Keyword

系统 SHALL 支持按关键字搜索工作流（按名称或描述模糊匹配）。

#### Scenario: Search workflows by keyword

- **WHEN** 用户通过 GET `/api/workflows?keyword=测试&page=0&size=10` 搜索工作流
- **THEN** 系统返回名称或描述包含 "测试" 的工作流列表
- **AND** 支持分页

#### Scenario: Search with no results

- **WHEN** 用户搜索关键字无匹配结果
- **THEN** 系统返回空列表且 `totalElements` 为 0

---

### Requirement: Workflow Data Model

系统 SHALL 使用规范的实体模型和 DTO 对象管理工作流数据。

#### Scenario: Workflow entity structure

- **WHEN** 系统存储工作流数据到数据库
- **THEN** Workflow 实体包含以下字段：
    - `id` (Long, 主键)
    - `name` (String, 必填)
    - `description` (String, 可选)
    - `enabled` (Boolean, 默认 true)
    - `deleted` (Boolean, 默认 false)
    - `creator` (String, 可选)
    - `editor` (String, 可选)
    - `createTime` (LocalDateTime, 自动生成)
    - `updateTime` (LocalDateTime, 自动更新)

#### Scenario: DTO and VO separation

- **WHEN** 前端提交创建或更新请求
- **THEN** 系统使用 `WorkflowDTO` 接收数据
- **AND** 响应时使用 `WorkflowVO` 返回数据，确保 DTO、VO、Entity 严格分离

---

### Requirement: Workflow Validation

系统 SHALL 对工作流输入数据进行验证。

#### Scenario: Missing required fields

- **WHEN** 用户创建工作流时未提供 `name` 字段
- **THEN** 系统返回 400 错误并提示 "Name is required"

#### Scenario: Name length validation

- **WHEN** 用户提供的工作流名称超过 255 个字符
- **THEN** 系统返回 400 错误并提示 "Name is too long"

#### Scenario: Description length validation

- **WHEN** 用户提供的描述超过 1000 个字符
- **THEN** 系统返回 400 错误并提示 "Description is too long"

---

### Requirement: Workflow Business Logic Layer

系统 SHALL 使用分层架构实现工作流管理功能。

#### Scenario: Service layer implementation

- **WHEN** Controller 调用业务逻辑
- **THEN** `WorkflowService` 接口提供以下方法：
    - `create(WorkflowDTO dto)` - 创建工作流
    - `update(Long id, WorkflowDTO dto)` - 更新工作流
    - `delete(Long id)` - 软删除工作流
    - `findById(Long id)` - 查询单个工作流
    - `findAll(Pageable pageable, String keyword)` - 分页查询和搜索
    - `copy(Long id)` - 复制工作流

#### Scenario: Repository layer implementation

- **WHEN** Service 需要访问数据库
- **THEN** `WorkflowRepository` 继承 `JpaRepository` 并提供以下查询方法：
    - `findByDeletedFalse(Pageable pageable)` - 查询未删除的工作流
    - `findByDeletedFalseAndNameContainingOrDescriptionContaining(...)` - 关键字搜索

