# Spring AI 入门实践：Spring AI 与 DeepSeek 集成

## 概述

DeepSeek 是一家专注于人工智能研究的公司，其开发的 DeepSeek-R1 模型以在数学、代码和推理任务中的出色表现著称。Spring AI 提供了对 DeepSeek 模型的集成支持，使得开发者可以在 Spring 应用中轻松使用 DeepSeek 的强大能力。

## 准备工作

### 1. 获取 DeepSeek API 密钥

首先，您需要在 DeepSeek 官网注册账号并获取 API 密钥：

1. 访问 [DeepSeek 官网](https://www.deepseek.com/)
2. 注册账号并登录
3. 创建 API 密钥

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-deepseek</artifactId>
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

在 `application.properties` 文件中配置 DeepSeek 相关设置：

```properties
# DeepSeek 配置
spring.ai.deepseek.api-key=your-api-key
spring.ai.deepseek.chat.enabled=true
spring.ai.deepseek.chat.model=deepseek-r1
spring.ai.deepseek.chat.options.temperature=0.7
```

## 核心功能

### 1. 文本生成

使用 DeepSeek 进行文本生成：

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

### 2. 代码生成

使用 DeepSeek 进行代码生成：

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
public class CodeGenerateController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/code/generate")
    public String generateCode(@RequestParam String prompt) {
        Prompt codePrompt = new Prompt(List.of(
            new SystemMessage("You are a professional code assistant. Generate clean, efficient code based on the user's request."),
            new UserMessage(prompt)
        ));
        return chatClient.call(codePrompt).getResult().getOutput().getContent();
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-deepseek/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── deepseek/
│   │   │                   ├── controller/
│   │   │                   │   ├── ChatController.java
│   │   │                   │   └── CodeGenerateController.java
│   │   │                   ├── router/
│   │   │                   │   └── RouterFunctionConfig.java
│   │   │                   └── SpringAiDeepSeekApplication.java
│   │   └── resources/
│   │       ├── conf/
│   │       │   └── log4j2-dev.xml
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── zhipuai/
│                           └── SpringAiDeepSeekApplicationTests.java
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
public class SpringAiDeepSeekApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDeepSeekApplication.class, args);
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
    public RouterFunction<ServerResponse> routerFunction(
            ChatController chatController,
            CodeGenerateController codeGenerateController) {
        return route(GET("/chat"), chatController::chat)
                .andRoute(GET("/code/generate"), codeGenerateController::generateCode);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiDeepSeekApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```
3. **测试代码生成**：
   ```bash
   curl "http://localhost:8080/code/generate?prompt=Write a Java function to calculate factorial"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的 DeepSeek 模型
2. **参数调优**：根据应用场景调整温度等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [DeepSeek 官方文档](https://www.deepseek.com/docs)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [DeepSeek-R1 模型介绍](https://www.deepseek.com/deepseek-r1)