# Spring AI 入门实践：Spring AI 监控与可观察性

> 基于 Spring AI 框架实现监控与可观察性集成，支持 Prometheus 指标导出、Langfuse 追踪分析，提供请求量、延迟、JVM 指标监控，以及 Prompt 调试与版本对比功能。

---

## 一、项目概述

### 1.1 项目定位

本项目展示如何在 Spring AI 应用中实现监控与可观察性，通过集成 Micrometer/Prometheus 导出指标，以及 Langfuse 进行调用追踪，帮助开发者了解 AI 应用的性能表现和调用链路。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Micrometer | Latest | 指标收集 |
| Prometheus | Latest | 指标存储和可视化 |
| Langfuse | Latest | AI 调用追踪和分析 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-observation-prometheus
**本地路径**：`spring-ai-ollama-observation-prometheus/`、`spring-ai-ollama-observation-langfuse/`

### 1.4 核心功能

- ✅ Prometheus 指标：支持请求量、延迟、JVM 指标导出
- ✅ Langfuse 追踪：支持生成和嵌入调用追踪
- ✅ Prompt 调试：支持 Prompt 版本对比
- ✅ 调用链分析：支持完整的调用链路追踪
- ✅ 可视化监控：支持 Grafana 集成
- ✅ 告警支持：支持配置告警规则

---

## 二、监控与可观察性简介

### 2.1 Prometheus 指标

Prometheus 是一个开源的监控系统，通过采集和存储时序数据，提供强大的查询和告警能力。Spring AI 通过 Micrometer 集成 Prometheus，自动暴露 AI 调用相关的指标。

### 2.2 Langfuse 追踪

Langfuse 是一个专门用于 AI 应用的可观察性平台，提供调用追踪、Prompt 调试、版本对比等功能，帮助开发者优化 AI 应用性能。

### 2.3 核心特性

| 特性 | 说明 |
|------|------|
| **指标导出** | 自动暴露请求量、延迟等指标 |
| **调用追踪** | 完整的调用链路追踪 |
| **Prompt 调试** | 支持 Prompt 版本对比 |
| **可视化** | 支持 Grafana 集成 |
| **告警** | 支持配置告警规则 |
| **多后端支持** | 支持 Prometheus、Langfuse 等 |

---

## 三、性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Prometheus 官方文档](https://prometheus.io/docs/) 或 [Langfuse 官方文档](https://langfuse.com/docs)。

## 四、应用案例

### 生产环境监控
- **业务场景**：AI 应用生产监控
- **性能指标**：指标采集延迟 < 10ms，存储可扩展
- **技术方案**：Prometheus + Grafana 监控

### Prompt 优化
- **业务场景**：Prompt 调试和版本对比
- **性能指标**：调用追踪实时，版本对比准确
- **技术方案**：Langfuse 追踪分析

---

## 五、项目结构

```
spring-ai-ollama-observation/
├── spring-ai-ollama-observation-prometheus/
│   ├── pom.xml
│   └── src/main/
│       ├── java/.../PrometheusObservationApplication.java
│       └── resources/application.properties
└── spring-ai-ollama-observation-langfuse/
    ├── pom.xml
    └── src/main/
        ├── java/.../LangfuseObservationApplication.java
        └── resources/application.properties
```

### 文件说明

- `PrometheusObservationApplication.java` - Prometheus 监控示例
- `LangfuseObservationApplication.java` - Langfuse 追踪示例
- `application.properties` - 监控配置文件

---

## 六、核心配置

### 6.1 Prometheus 配置

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-observation-prometheus

# Prometheus 指标配置
management.endpoints.web.exposure.include=prometheus,health,info
management.metrics.export.prometheus.enabled=true
management.prometheus.metrics.export.enabled=true

# Spring AI Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5
```

### 6.2 Langfuse 配置

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-observation-langfuse

# Langfuse 配置
spring.ai.langfuse.public-key=${LANGFUSE_PUBLIC_KEY}
spring.ai.langfuse.secret-key=${LANGFUSE_SECRET_KEY}
spring.ai.langfuse.host=https://cloud.langfuse.com

# Spring AI Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5
```

---

## 七、运行项目

### 7.1 运行 Prometheus 示例

```bash
cd spring-ai-ollama-observation-prometheus
mvn spring-boot:run
```

访问 Prometheus 指标端点：
```
http://localhost:8080/actuator/prometheus
```

### 7.2 运行 Langfuse 示例

```bash
cd spring-ai-ollama-observation-langfuse
export LANGFUSE_PUBLIC_KEY=your-public-key
export LANGFUSE_SECRET_KEY=your-secret-key
mvn spring-boot:run
```

访问 Langfuse 控制台查看追踪数据：
```
https://cloud.langfuse.com
```

---

## 八、Java 客户端示例

### 8.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class ObservationClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {
        String url = BASE_URL + "/chat?message={message}";
        return restTemplate.getForObject(url, String.class, message);
    }

    public Map<String, Object> getMetrics() {
        return restTemplate.getForObject(BASE_URL + "/metrics", Map.class);
    }
}
```

---

## 九、许可证

- **Prometheus**：Apache 2.0
- **Langfuse**：MIT
- **Spring AI**：Apache 2.0
- **本项目**：Apache 2.0

---

## 十、致谢

- **感谢 Prometheus 团队** 提供强大的开源监控系统
- **感谢 Langfuse 团队** 提供专业的 AI 可观察性平台
- **感谢 Spring AI 团队** 提供完善的监控集成框架
- **感谢开源社区** 提供丰富的技术资源

---

## 参考资源

- [Spring AI Observability 文档](https://docs.spring.io/spring-ai/reference/observability/index.html)
- [Prometheus 官方文档](https://prometheus.io/docs/)
- [Langfuse 官方文档](https://langfuse.com/docs)
- [Micrometer 文档](https://micrometer.io/docs)
