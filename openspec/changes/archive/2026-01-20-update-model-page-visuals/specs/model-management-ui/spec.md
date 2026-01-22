## ADDED Requirements

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
