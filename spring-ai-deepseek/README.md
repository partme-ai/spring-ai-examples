## spring-ai-deepseek

> 演示 **Spring AI** 与 **DeepSeek** 对话模型的集成（Chat、记忆等能力）。依赖版本由父工程 `spring-ai-examples/pom.xml` 统一管理：**Spring Boot 3.5.x**、**Spring AI 1.1.x**。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- [Chat Model](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Getting Started](https://docs.spring.io/spring-ai/reference/getting-started.html)

## 先决条件

- JDK 17+、Maven 3.6+
- [DeepSeek Open Platform](https://platform.deepseek.com/) API Key

## 依赖要点

- `spring-ai-starter-model-deepseek`（见本模块 `pom.xml`）

## 配置说明

- 在 `src/main/resources/application.properties` 中配置 `spring.ai.deepseek.*`（API Key、模型名、base-url 等），或通过环境变量注入。

## 运行

```bash
cd spring-ai-deepseek
mvn spring-boot:run
```

## 与官方示例关系

- 对应 Reference 中的 **Chat Completion** 与 **Spring Boot 自动配置** 用法，实现可运行的最小 DeepSeek 对话示例。

## 故障排除

- 401/403：检查 API Key 与账户额度。
- 连接失败：检查网络与 `base-url` 是否可达。
