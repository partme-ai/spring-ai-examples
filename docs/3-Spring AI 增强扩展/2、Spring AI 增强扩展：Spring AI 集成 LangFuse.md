# Spring AI 增强扩展：Spring AI 集成 LangFuse

## 概述

LangFuse 是一个开源的 LLM 应用可观测性平台，用于跟踪、评估和改进 AI 应用。Spring AI 可以集成 LangFuse 来监控 AI 模型的性能、提示效果和用户反馈，帮助开发者优化 AI 应用。

## 准备工作

### 1. 获取 LangFuse API 密钥

首先，您需要在 LangFuse 注册账号并获取 API 密钥：

1. 访问 [LangFuse 官网](https://langfuse.com/)
2. 注册账号并登录
3. 创建项目并获取 API 密钥

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-observation-langfuse</artifactId>
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

### 3. 配置 LangFuse

在 `application.properties` 文件中配置 LangFuse 相关设置：

```properties
# LangFuse 配置
spring.ai.observation.langfuse.enabled=true
spring.ai.observation.langfuse.secret-key=your-secret-key
spring.ai.observation.langfuse.public-key=your-public-key
spring.ai.observation.langfuse.base-url=https://cloud.langfuse.com

# Spring AI 观测配置
spring.ai.observation.enabled=true
spring.ai.observation.tracing.enabled=true
```

## 核心功能

### 1. 追踪 AI 调用

LangFuse 会自动追踪以下信息：

- **提示和响应**：完整的提示内容和模型响应
- **调用时间**：开始时间、结束时间、持续时间
- **模型信息**：使用的模型名称和参数
- **错误信息**：如果发生错误，会记录错误详情

### 2. 提示管理

使用 LangFuse 管理和版本控制提示：

```java
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

    public String chat(String message) {
        // 使用 LangFuse 提示管理
        String systemPrompt = "You are a helpful assistant.";
        
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(message)
        ));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 3. 评估指标

LangFuse 提供了多种评估指标：

- **延迟**：API 调用的响应时间
- **成本**：API 调用的费用
- **质量**：响应质量评估
- **用户反馈**：收集用户对响应的反馈

### 4. 查看追踪数据

启动应用后，您可以在 LangFuse 控制台中查看追踪数据：

- **追踪列表**：所有 AI 调用的历史记录
- **详细信息**：每个调用的详细信息，包括提示、响应、时间等
- **分析仪表板**：性能和质量的分析图表

## 完整示例

### 项目结构

```
spring-ai-ollama-observation-langfuse/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── observation/
│   │   │                   │   └── langfuse/
│   │   │                   │       ├── controller/
│   │   │                   │       │   └── ChatController.java
│   │   │                   │       └── SpringAiOllamaObservationLangfuseApplication.java
│   │   │                   └── SpringAiOllamaObservationLangfuseApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── ollama/
│                           └── observation/
│                               └── langfuse/
│                                   └── SpringAiOllamaObservationLangfuseApplicationTests.java
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
public class SpringAiOllamaObservationLangfuseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaObservationLangfuseApplication.class, args);
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

1. **启动应用**：运行 `SpringAiOllamaObservationLangfuseApplication` 类
2. **测试聊天功能**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```
3. **查看 LangFuse 控制台**：登录 LangFuse 控制台查看追踪数据

## 最佳实践

1. **提示版本控制**：使用 LangFuse 的提示管理功能，版本控制提示
2. **评估指标设置**：根据业务需求设置合适的评估指标
3. **用户反馈收集**：集成用户反馈系统，收集用户对响应的评价
4. **性能优化**：根据 LangFuse 的分析结果，优化提示和模型参数
5. **错误监控**：设置错误告警，及时发现和处理问题

## 相关资源

- [LangFuse 官方文档](https://docs.langfuse.com/)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [LangFuse GitHub 仓库](https://github.com/langfuse/langfuse)

## 深度用法

如需更深入的监控配置和用法，请参考 [Spring AI 监控与可观察性](../2-Spring%20AI%20入门实践/spring-ai-observability.md) 长文。