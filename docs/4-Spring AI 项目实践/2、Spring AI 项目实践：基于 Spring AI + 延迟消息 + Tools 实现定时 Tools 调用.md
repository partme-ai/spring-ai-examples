# Spring AI 项目实践：基于 Spring AI + 延迟消息 + Tools 实现定时 Tools 调用

## 概述

本项目实践展示了如何结合 Spring AI、延迟消息和 Tools 功能，实现定时执行 Tools 调用的场景。通过这种方式，我们可以实现诸如定时查询天气、定时获取股票信息等功能。

## 技术栈

- Spring Boot 3.2+
- Spring AI 1.1.4+
- Spring Cloud Stream（或 RabbitMQ、Kafka）
- Spring AI Tools

## 核心架构

1. **延迟消息系统**：用于调度定时任务
2. **Spring AI Tools**：提供外部工具调用能力
3. **AI 模型**：处理复杂逻辑和决策
4. **业务服务**：执行具体的业务逻辑

## 实现步骤

### 1. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-tools</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
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
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 配置文件

在 `application.properties` 文件中配置相关设置：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.model=qwen3.5:7b

# 消息队列配置
spring.cloud.stream.bindings.delayedOutput.destination=delayed-messages
spring.cloud.stream.bindings.delayedInput.destination=delayed-messages

# 延迟消息配置
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 3. 定义 Tools

创建一个天气查询工具：

```java
import org.springframework.ai.chat.tool.ToolDefinition;
import org.springframework.ai.chat.tool.ToolResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WeatherTool {

    @ToolDefinition(name = "getWeather", description = "获取指定城市的天气信息")
    public ToolResponse getWeather(Map<String, Object> parameters) {
        String city = (String) parameters.get("city");
        // 模拟天气查询
        String weather = "城市: " + city + "，天气: 晴，温度: 25°C";
        return ToolResponse.builder()
                .withResult(weather)
                .build();
    }

}
```

### 4. 延迟消息处理器

创建延迟消息处理器：

```java
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DelayedMessageHandler {

    @Autowired
    private ChatClient chatClient;

    @StreamListener(Sink.INPUT)
    public void handleDelayedMessage(String message) {
        // 解析消息，获取需要执行的工具调用
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一个工具调度助手，根据用户请求调用相应的工具。"),
            new UserMessage(message)
        ));
        
        // 调用 AI 模型处理消息
        chatClient.call(prompt);
    }

}
```

### 5. 定时任务服务

创建定时任务服务：

```java
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private Source source;

    public void scheduleToolCall(String task, long delayMs) {
        // 发送延迟消息
        source.output().send(MessageBuilder.withPayload(task)
                .setHeader("x-delay", delayMs)
                .build());
    }

}
```

### 6. 控制器

创建控制器用于接收定时任务请求：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ScheduleController {

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @PostMapping("/schedule/tool")
    public String scheduleToolCall(@RequestBody ScheduleRequest request) {
        scheduledTaskService.scheduleToolCall(
            request.getTask(),
            request.getDelayMs()
        );
        return "Task scheduled successfully";
    }

}

class ScheduleRequest {
    private String task;
    private long delayMs;
    // getters and setters
}
```

## 完整示例

### 项目结构

```
spring-ai-scheduled-tools/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── scheduled/
│   │   │                   ├── tool/
│   │   │                   │   └── WeatherTool.java
│   │   │                   ├── handler/
│   │   │                   │   └── DelayedMessageHandler.java
│   │   │                   ├── service/
│   │   │                   │   └── ScheduledTaskService.java
│   │   │                   ├── controller/
│   │   │                   │   └── ScheduleController.java
│   │   │                   └── SpringAiScheduledToolsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── scheduled/
│                           └── SpringAiScheduledToolsApplicationTests.java
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
public class SpringAiScheduledToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiScheduledToolsApplication.class, args);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiScheduledToolsApplication` 类
2. **启动 RabbitMQ**：确保 RabbitMQ 服务正在运行
3. **创建定时任务**：
   ```bash
   curl -X POST http://localhost:8080/schedule/tool \
     -H "Content-Type: application/json" \
     -d '{"task": "查询北京明天的天气", "delayMs": 5000}'
   ```
4. **观察结果**：5秒后，应用会调用天气工具并输出结果

## 最佳实践

1. **消息持久化**：确保延迟消息在系统重启后仍然有效
2. **错误处理**：实现消息处理失败的重试机制
3. **监控**：添加监控指标，跟踪定时任务的执行情况
4. **安全**：对工具调用进行权限控制，防止恶意调用
5. **限流**：对工具调用频率进行限制，防止 API 滥用

## 相关资源

- [Spring AI Tools 文档](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Spring Cloud Stream 文档](https://spring.io/projects/spring-cloud-stream)
- [RabbitMQ 延迟消息插件](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)

## 扩展场景

1. **定时数据分析**：定时分析系统数据并生成报告
2. **定时通知**：定时发送提醒或通知
3. **定时数据同步**：定时同步外部系统数据
4. **定时健康检查**：定时检查系统健康状态
5. **定时任务调度**：集成 Quartz 等调度系统，实现更复杂的定时任务