# Spring AI 增强扩展：Spring AI 集成 Prometheus

## 概述

Prometheus 是一个开源的监控系统，用于收集和存储时间序列数据。Spring AI 可以集成 Prometheus 来监控 AI 模型的性能和使用情况，帮助开发者了解模型的调用频率、响应时间、错误率等指标。

## 准备工作

### 1. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-observation-prometheus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 配置 Prometheus

在 `application.properties` 文件中配置 Prometheus 相关设置：

```properties
# Actuator 配置
management.endpoints.web.exposure.include=prometheus,metrics,health

# Prometheus 配置
management.metrics.export.prometheus.enabled=true

# Spring AI 观测配置
spring.ai.observation.enabled=true
spring.ai.observation.metrics.enabled=true
```

## 核心功能

### 1. 监控指标

Spring AI 集成 Prometheus 后，会自动收集以下指标：

- **spring_ai_chat_client_calls_total**：聊天客户端调用总数
- **spring_ai_chat_client_call_duration_seconds**：聊天客户端调用持续时间
- **spring_ai_chat_client_errors_total**：聊天客户端错误总数
- **spring_ai_embedding_client_calls_total**：嵌入客户端调用总数
- **spring_ai_embedding_client_call_duration_seconds**：嵌入客户端调用持续时间
- **spring_ai_embedding_client_errors_total**：嵌入客户端错误总数

### 2. 自定义指标

您可以添加自定义指标来监控特定的业务逻辑：

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private MeterRegistry meterRegistry;

    private final Counter chatCounter = meterRegistry.counter("spring_ai_custom_chat_calls");

    public String chat(String message) {
        chatCounter.increment();

        Prompt prompt = new Prompt(List.of(
            new SystemMessage("You are a helpful assistant."),
            new UserMessage(message)
        ));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 3. 查看指标

启动应用后，您可以通过以下 URL 查看 Prometheus 指标：

- **Actuator 端点**：`http://localhost:8080/actuator/prometheus`
- **Prometheus 控制台**：如果您有部署 Prometheus 服务器，可以在控制台中查看指标

## 完整示例

### 项目结构

```
spring-ai-ollama-observation-prometheus/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── observation/
│   │   │                   │   └── prometheus/
│   │   │                   │       ├── controller/
│   │   │                   │       │   └── ChatController.java
│   │   │                   │       └── SpringAiOllamaObservationPrometheusApplication.java
│   │   │                   └── SpringAiOllamaObservationPrometheusApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── ollama/
│                           └── observation/
│                               └── prometheus/
│                                   └── SpringAiOllamaObservationPrometheusApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
└── mvnw.cmd
```

### 主应用类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaObservationPrometheusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaObservationPrometheusApplication.class, args);
    }

}
```

### 控制器类

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("You are a helpful assistant."),
            new UserMessage(message)
        ));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 测试方法

1. **启动应用**：运行 `SpringAiOllamaObservationPrometheusApplication` 类
2. **测试聊天功能**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```
3. **查看 Prometheus 指标**：
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

## 最佳实践

1. **指标命名**：使用统一的命名规范，例如 `spring_ai_*` 前缀
2. **指标粒度**：根据需要设置合适的指标粒度，避免过多的指标影响性能
3. **告警配置**：为关键指标设置告警，例如错误率超过阈值时
4. **监控面板**：使用 Grafana 等工具创建监控面板，直观展示指标
5. **性能优化**：定期清理过期指标，避免存储过多的历史数据

## 相关资源

- [Prometheus 官方文档](https://prometheus.io/docs/)
- [Spring Boot Actuator 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer 官方文档](https://micrometer.io/docs/)
- [Grafana 官方文档](https://grafana.com/docs/)

## 深度用法

如需更深入的监控配置和用法，请参考 [Spring AI 监控与可观察性](../2-Spring%20AI%20入门实践/spring-ai-observability.md) 长文。