# Spring AI 监控与可观察性

> **对外标题**：Spring AI 入门实践：Spring AI 监控与可观察性（衔接「Spring AI 集成 Prometheus / Spring AI 集成 LangFuse」）

> 系列第十三篇。仓库提供两套示例：**`spring-ai-ollama-observation-prometheus`**（指标）与 **`spring-ai-ollama-observation-langfuse`**（追踪/分析），可与 Ollama 调用链结合。

## 目录

- [Prometheus 指标](#prometheus-指标)
- [Langfuse 追踪](#langfuse-追踪)
- [官方文档](#官方文档)
- [运行要点](#运行要点)
- [扩展阅读](#扩展阅读)

## Prometheus 指标

模块 **`spring-ai-ollama-observation-prometheus`**：集成 **Micrometer / Prometheus** 导出，便于观察请求量、延迟与 JVM 指标；具体端点与 `management.endpoints.web.exposure.include` 以 **`application.properties`** 为准（常见 **`/actuator/prometheus`**）。

## Langfuse 追踪

模块 **`spring-ai-ollama-observation-langfuse`**：将生成与嵌入等调用关联到 **Langfuse** 项目，便于调试 Prompt 与版本对比。需在配置中填写 **Langfuse Host、公钥/私钥**（使用环境变量注入）。

## 官方文档

- [Observability](https://docs.spring.io/spring-ai/reference/observability/index.html)

## 运行要点

```bash
cd spring-ai-ollama-observation-prometheus
mvn spring-boot:run
```

```bash
cd spring-ai-ollama-observation-langfuse
mvn spring-boot:run
```

生产环境请配合 **Grafana**、**告警规则** 与密钥管理。

## 扩展阅读

- 上一篇：[Audio](./spring-ai-audio-integration.md)  
- 下一篇：[Fine-tuning](./spring-ai-fine-tuning.md)
