## spring-ai-project-hotel-recommend

> 综合示例：结合 Spring AI 能力的 **酒店推荐** 等业务场景演示（具体能力以包内代码为准）。版本由父工程 `spring-ai-examples/pom.xml` 统一管理。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Tools / Function Calling](https://docs.spring.io/spring-ai/reference/api/tools.html)

## 先决条件

- JDK 17+、Maven 3.6+
- 按模块内 `application*.yml` / `properties` 准备模型服务或 API Key

## 运行

```bash
cd spring-ai-project-hotel-recommend
mvn spring-boot:run
```

## 说明

- 业务包结构与领域逻辑见 `src/main/java` 下各层代码；扩展时请保持与父工程统一的 Spring AI 版本，勿在子模块硬编码 `spring-ai.version`。
