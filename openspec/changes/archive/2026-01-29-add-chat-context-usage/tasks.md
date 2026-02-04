## 1. Implementation

- [ ] 1.1 增加用户消息长度校验（单条不超过 5000 字符），超限直接拒绝
- [ ] 1.2 添加消息表 token 字段（prompt_tokens/completion_tokens/total_tokens）并设置默认值
- [ ] 1.3 更新实体与 VO/DTO 映射以读写 token 字段并返回给消息详情 API
- [ ] 1.4 在聊天服务构建上下文窗口：单次查询历史消息，按限制裁剪并生成请求消息列表
- [ ] 1.5 更新 provider 响应映射，写入 usage；缺失时写入 0
- [ ] 1.6 补充单元/接口测试覆盖长度校验、窗口裁剪与 usage 记录

## 2. Validation

- [ ] 2.1 运行后端测试套件（或相关模块测试）
- [ ] 2.2 补充或更新测试数据/断言以覆盖 token 字段
