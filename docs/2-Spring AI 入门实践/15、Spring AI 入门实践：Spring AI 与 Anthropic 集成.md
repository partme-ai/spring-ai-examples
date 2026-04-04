# Spring AI 入门实践：Spring AI 与 Anthropic 集成

## 概述

Anthropic 是一家专注于人工智能安全和研究的公司，其开发的 Claude 模型以安全性和准确性著称。Spring AI 提供了对 Anthropic Claude 模型的集成支持，使得开发者可以在 Spring 应用中轻松使用 Claude 的强大能力。

## 准备工作

### 1. 获取 Anthropic API 密钥

首先，您需要在 Anthropic 官网注册账号并获取 API 密钥：

1. 访问 [Anthropic 官网](https://www.anthropic.com/)
2. 注册账号并登录
3. 创建 API 密钥

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
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

### 3. 配置 API 密钥

在 `application.properties` 文件中配置 Anthropic API 密钥：

```properties
# Anthropic 配置
spring.ai.anthropic.api-key=your-api-key
spring.ai.anthropic.chat.enabled=true
spring.ai.anthropic.chat.model=claude-3-sonnet-20240229
spring.ai.anthropic.chat.options.temperature=0.7
spring.ai.anthropic.chat.options.max-tokens=1024
```

## 核心功能

### 1. 文本生成

使用 Anthropic Claude 进行文本生成：

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

### 2. 流式响应

使用 Anthropic Claude 进行流式响应：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat/stream")
    public SseEmitter streamChat(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter();

        Prompt prompt = new Prompt(List.of(
            new SystemMessage("You are a helpful assistant."),
            new UserMessage(message)
        ));

        chatClient.stream(prompt).subscribe(
            response -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(response.getResult().getOutput().getContent()));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            },
            error -> emitter.completeWithError(error),
            () -> emitter.complete()
        );

        return emitter;
    }
}
```

### 3. 多轮对话

使用 Anthropic Claude 进行多轮对话：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.ai.chat.prompt.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    private final List<Object> messageHistory = new ArrayList<>();

    @PostMapping("/chat/multi")
    public String multiChat(@RequestBody String message) {
        // 添加用户消息到历史
        messageHistory.add(new UserMessage(message));

        // 创建包含历史消息的提示
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("You are a helpful assistant."),
                messageHistory.toArray(new Object[0])
            )
        );

        // 调用模型
        String response = chatClient.call(prompt).getResult().getOutput().getContent();

        // 添加助手回复到历史
        messageHistory.add(new AssistantMessage(response));

        return response;
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-anthropic/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── anthropic/
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   ├── router/
│   │   │                   │   └── RouterFunctionConfig.java
│   │   │                   └── SpringAiAnthropicApplication.java
│   │   └── resources/
│   │       ├── conf/
│   │       │   └── log4j2-dev.xml
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── anthropic/
│                           └── SpringAiAnthropicApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

### 主应用类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiAnthropicApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAnthropicApplication.class, args);
    }

}
```

### 路由配置

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ChatController chatController) {
        return route(GET("/chat"), chatController::chat);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiAnthropicApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```
3. **测试流式响应**：
   ```bash
   curl "http://localhost:8080/chat/stream?message=Tell me a story"
   ```
4. **测试多轮对话**：
   ```bash
   curl -X POST http://localhost:8080/chat/multi \
     -H "Content-Type: text/plain" \
     -d "What's the capital of France?"
   curl -X POST http://localhost:8080/chat/multi \
     -H "Content-Type: text/plain" \
     -d "What's the population?"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的 Claude 模型版本
2. **参数调优**：根据应用场景调整温度、最大 token 数等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [Anthropic 官方文档](https://docs.anthropic.com/claude/docs)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Claude 模型介绍](https://www.anthropic.com/claude)