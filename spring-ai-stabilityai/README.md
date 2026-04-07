## spring-ai-stabilityai

> 演示 **Spring AI** 与 **Stability AI** 的 Chat 等能力集成。依赖版本由父 POM 管理（Spring Boot 3.5.x / Spring AI 1.1.x）。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- [Chat Model](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)


### 依赖要点

- 主要 Starter：`spring-ai-starter-model-stability-ai（以模块 pom 为准）`

### 配置与运行

1. 在 `src/main/resources/application.properties` 或环境变量中配置 API Key / Base URL（见文件内注释）。
2. 启动应用：

```bash
cd spring-ai-stabilityai
mvn spring-boot:run
```


### 故障排除

- 401/403：检查 Key 与计费。
- 超时：检查网络与 endpoint。
