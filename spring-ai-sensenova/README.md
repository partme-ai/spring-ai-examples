## spring-ai-sensenova

> 演示 **Spring AI** 与 **商汤 SenseNova（日日新）** 大模型的集成。版本由父 POM 管理（Spring AI 1.1.x）。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- [Chat Model](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [AI Model providers](https://docs.spring.io/spring-ai/reference/api/index.html)

## 先决条件

- JDK 17+、Maven 3.6+
- 商汤开放平台 API Key（见 `application.properties` 配置项说明）

## 依赖要点

- 以本模块 `pom.xml` 中 `spring-ai-starter-*` / 厂商适配依赖为准。

## 运行

```bash
cd spring-ai-sensenova
mvn spring-boot:run
```

## 故障排除

- 鉴权失败：核对 Key、环境变量与 endpoint。
- 模型不可用：在控制台确认模型名称与开通状态。
