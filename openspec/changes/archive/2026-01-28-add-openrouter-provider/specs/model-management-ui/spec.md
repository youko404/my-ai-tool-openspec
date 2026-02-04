## ADDED Requirements

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
