# Tasks: Add OpenRouter AI Provider Support

## 1. 配置层实现

- [x] 1.1 创建 `OpenRouterProperties.java` 配置属性类
    - 属性：`apiKey`、`baseUrl`、`defaultModel`、`timeout`、`httpReferer`（可选）、`xTitle`（可选）
    - 使用 `@ConfigurationProperties(prefix = "openrouter")`
    - 使用 `@NotBlank` 验证 apiKey 必填

- [x] 1.2 创建 `OpenRouterConfig.java` 配置类
    - 使用 `@EnableConfigurationProperties(OpenRouterProperties.class)`
    - 创建 `RestClient` Bean，配置 baseUrl、Authorization Header、超时等

- [x] 1.3 更新 `application.yml` 和 `application-local.yml`
    - 添加 `openrouter.api-key`、`openrouter.base-url`（默认 `https://openrouter.ai/api/v1`）等配置
    - 在 local 配置中提供示例值或占位符

## 2. DTO 层实现

- [x] 2.1 创建 `OpenRouterChatRequest.java` 请求 DTO
    - 字段：`model`、`messages`（List<Message>）、`stream`（Boolean）、`temperature`（可选）、`maxTokens`（可选）
    - 内部类 `Message` 包含 `role` 和 `content`
    - 使用 Lombok `@Data`、`@Builder`

- [x] 2.2 创建 `OpenRouterChatResponse.java` 响应 DTO
    - 字段：`id`、`model`、`choices`（List<Choice>）、`usage`（可选）
    - 内部类 `Choice` 包含 `message`（Message）、`finishReason`
    - 内部类 `Usage` 包含 `promptTokens`、`completionTokens`、`totalTokens`
    - 使用 Lombok `@Data`

## 3. 服务层实现

- [x] 3.1 创建 `OpenRouterClient.java` 服务类
    - 使用 `@Service` 和 `@RequiredArgsConstructor`
    - 注入 `RestClient` 和 `OpenRouterProperties`
    - 实现 `chatCompletion(OpenRouterChatRequest request)` 方法（同步调用）
    - 实现错误处理：捕获网络异常、HTTP 错误状态码，抛出友好的业务异常
    - 添加日志记录：请求前记录 DEBUG 日志，响应后记录结果或错误

- [x] 3.2 （可选）实现流式响应支持
    - 添加 `chatCompletionStream(OpenRouterChatRequest request)` 方法
    - 返回 `Flux<String>` 或自定义流式处理接口
    - 处理 Server-Sent Events (SSE) 响应

## 4. 测试验证

- [x] 4.1 编写单元测试 `OpenRouterClientTest.java`
    - 使用 Mockito Mock `RestClient`
    - 测试正常响应场景
    - 测试错误响应场景（401、500 等）
    - 测试配置缺失场景

- [ ] 4.2 手动集成测试
    - 配置真实 OpenRouter API Key
    - 启动应用，验证配置加载成功
    - 调用 `chatCompletion` 方法，验证能够成功返回响应
    - 验证日志输出正确

## 5. 文档更新

- [x] 5.1 在 README 或项目文档中说明 OpenRouter 配置方式
    - 配置项说明
    - API Key 获取方式
    - 使用示例

## 6. 模型实体扩展

- [x] 6.1 修改 `AiModel.java` 实体类
    - 新增 `apiKey` 字段（String，可选，加密存储）
    - 将 `provider` 字段从 String 改为 `ModelProvider` 枚举类型
    - 添加相应的列注解和说明

- [x] 6.2 创建 `ModelProvider.java` 枚举类
    - 定义 `OPENROUTER` 枚举值
    - 添加 `getValue()` 和 `getDescription()` 方法
    - 支持从字符串转换为枚举

- [x] 6.3 更新 `AiModelDTO.java`
    - 新增 `apiKey` 字段（可选）
    - 将 `provider` 改为 `ModelProvider` 类型或字符串（JSON 序列化兼容）

- [x] 6.4 更新 `AiModelVO.java`
    - 将 `provider` 改为 `ModelProvider` 或字符串显示
    - **不包含** `apiKey` 字段（安全考虑）

- [x] 6.5 更新 `AiModelService` 和 `AiModelServiceImpl`
    - 保存/更新模型时加密存储 apiKey
    - 查询模型时不返回 apiKey（VO 层过滤）
    - 支持 apiKey 为空时使用全局配置

- [x] 6.6 数据库迁移脚本
    - 添加 `api_key` 列（VARCHAR(500)，可为空，用于加密存储）
    - 修改 `provider` 列类型为 VARCHAR(50)（存储枚举名称）

## 7. 聊天服务层实现

- [x] 7.1 创建 `ChatRequestDTO.java` 请求 DTO
    - 字段：`messages`（List<Message>）、`temperature`（可选）、`maxTokens`（可选）
    - 使用 Lombok `@Data`、`@Builder`

- [x] 7.2 创建 `ChatResponseDTO.java` 响应 DTO
    - 字段：`content`（String）、`model`（String）、`usage`（可选）
    - 使用 Lombok `@Data`

- [x] 7.3 创建 `ChatService.java` 接口
    - 方法：`ChatResponseDTO chat(Long modelId, ChatRequestDTO request)`

- [x] 7.4 创建 `ChatServiceImpl.java` 实现类
    - 根据 modelId 查询模型配置
    - 验证模型是否启用
    - 根据 provider 枚举路由到对应的 Client（当前仅 OpenRouterClient）
    - 优先使用模型的 apiKey，为空则使用全局配置
    - 转换 DTO 格式并调用对应 Client
    - 统一异常处理和日志记录

## 8. Controller 层扩展

- [x] 8.1 在 `AiModelController.java` 中新增聊天接口
    - 接口：`POST /models/{id}/chat`
    - 请求体：`ChatRequestDTO`
    - 响应：`Result<ChatResponseDTO>`
    - 调用 `ChatService.chat()` 方法

- [x] 8.2 新增获取供应商列表接口
    - 接口：`GET /models/providers`
    - 响应：`Result<List<String>>`（返回所有支持的 provider 枚举值）

## 9. 测试验证（补充）

- [x] 9.1 编写 `ModelProviderTest.java` 单元测试
    - 测试枚举值转换
    - 测试无效值处理

- [x] 9.2 编写 `ChatServiceTest.java` 单元测试
    - Mock AiModelService 和 OpenRouterClient
    - 测试模型路由逻辑
    - 测试 apiKey 优先级（模型 > 全局）
    - 测试模型未启用场景
    - 测试模型不存在场景

- [ ] 9.3 集成测试
    - 创建模型时设置 apiKey
    - 调用聊天接口验证成功返回
    - 验证 apiKey 不在响应中泄露
    - 验证 provider 枚举正确序列化

## 10. 服务商配置存储与接口

- [x] 10.1 数据库迁移：新增服务商配置表（provider 唯一、apiBaseUrl、apiKey 加密存储、时间戳）
- [x] 10.2 新增服务商配置 Entity/Repository
- [x] 10.3 新增服务商配置 DTO/VO（VO 不返回 apiKey）
- [x] 10.4 新增服务商配置 Service：保存/查询、apiKey 加密解密、字段校验
- [x] 10.5 新增服务商配置 API：按 provider 获取配置、保存配置
- [x] 10.6 更新 `ModelProvider` 枚举支持 `OPENROUTER` 与 `OPENAI`
- [x] 10.7 更新聊天路由：从服务商配置读取 baseUrl/apiKey，未配置时返回明确错误
- [x] 10.8 OpenAI 调用占位：返回“未实现”错误（配置可保存）
- [x] 10.9 移除对 `application.yml` 中 openrouter apiKey 的强校验与依赖

## 11. 前端服务商配置入口

- [x] 11.1 模型配置页面右上角新增“服务商配置”入口按钮
- [x] 11.2 新增弹窗：选择服务商 + 填写 API 地址与 API Key
- [x] 11.3 调用服务商配置 API：加载配置（不回显 apiKey）、保存配置
- [x] 11.4 表单校验与提示：必填校验、保存成功/失败提示

## 依赖关系

- 任务 1.1、1.2、1.3 可并行
- 任务 2.1、2.2 依赖配置层完成（实际可并行）
- 任务 3.1 依赖 1.2、2.1、2.2
- 任务 3.2 可选，依赖 3.1
- 任务 4.1、4.2 依赖 3.1
- 任务 5.1 可在任何时候完成
- 任务 6.1-6.6 可并行，但 6.6（数据库迁移）需在应用启动前完成
- 任务 7.1-7.2 可并行
- 任务 7.3-7.4 依赖 2.1、2.2、3.1、6.2、6.5
- 任务 8.1 依赖 7.1、7.2、7.4
- 任务 8.2 依赖 6.2
- 任务 9.1-9.3 依赖所有实现任务完成
