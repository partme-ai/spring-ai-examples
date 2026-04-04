# Spring AI 入门实践：Spring AI 与 Amazon Bedrock 集成

## 概述

Amazon Bedrock 是 AWS 提供的一项完全托管的服务，用于构建和扩展生成式 AI 应用。Spring AI 提供了对 Amazon Bedrock 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Bedrock 提供的各种模型。

## 准备工作

### 1. AWS 账号配置

首先，您需要配置 AWS 账号并设置访问权限：

1. 登录 AWS 控制台
2. 创建 IAM 用户并赋予适当的权限（AmazonBedrockFullAccess）
3. 配置 AWS 凭证（access key 和 secret key）

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-amazon-bedrock-spring-boot-starter</artifactId>
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

### 3. 配置 AWS 凭证

在 `application.properties` 文件中配置 AWS 凭证和 Bedrock 相关设置：

```properties
# AWS 配置
spring.cloud.aws.credentials.access-key=your-access-key
spring.cloud.aws.credentials.secret-key=your-secret-key
spring.cloud.aws.region.static=us-east-1

# Amazon Bedrock 配置
spring.ai.bedrock.chat.enabled=true
spring.ai.bedrock.chat.model=anthropic.claude-3-sonnet-20240229-v1:0
spring.ai.bedrock.chat.options.temperature=0.7

# Embedding 配置
spring.ai.bedrock.embedding.enabled=true
spring.ai.bedrock.embedding.model=amazon.titan-embed-text-v1
```

## 核心功能

### 1. 文本生成

使用 Amazon Bedrock 进行文本生成：

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

### 2. 文本嵌入

使用 Amazon Bedrock 进行文本嵌入：

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddingController {

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostMapping("/embed")
    public List<Double> embed(@RequestBody String text) {
        EmbeddingRequest request = EmbeddingRequest.from(text);
        EmbeddingResponse response = embeddingModel.embed(request);
        return response.getResult().getOutput();
    }
}
```

### 3. 工具调用

Amazon Bedrock 支持工具调用功能，您可以定义自定义工具：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.ai.chat.tools.ToolSpecification;
import org.springframework.ai.chat.tools.ToolResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FunctionConfig {

    @Bean
    public ToolSpecification getWeatherFunction() {
        return ToolSpecification.builder()
            .withName("get_weather")
            .withDescription("获取指定城市的天气信息")
            .withParameters(Map.of(
                "type", "object",
                "properties", Map.of(
                    "city", Map.of(
                        "type", "string",
                        "description", "城市名称"
                    )
                ),
                "required", List.of("city")
            ))
            .build();
    }

    @Bean
    public ToolResponse getWeatherToolResponse() {
        return params -> {
            String city = (String) params.get("city");
            // 模拟天气数据
            return "{\"city\": \"" + city + "\", \"temperature\": 25, \"condition\": \"晴天\"}";
        };
    }
}
```

### 4. RAG 实现

使用 Amazon Bedrock 实现检索增强生成（RAG）：

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    public String queryWithRag(String userQuery) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(userQuery).withTopK(3)
        );

        String context = relevantDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));

        String systemPrompt = """
            你是一个智能助手。请根据以下上下文信息回答用户的问题。
            如果上下文中没有相关信息，请说明你无法从提供的上下文中找到答案。

            上下文信息：
            %s
            """.formatted(context);

        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(userQuery)
        ));

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-amazon-bedrock/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── amazon/
│   │   │                   └── bedrock/
│   │   │                       ├── controller/
│   │   │                       │   ├── ChatController.java
│   │   │                       │   └── EmbeddingController.java
│   │   │                       ├── functions/
│   │   │                       │   ├── FunctionConfig.java
│   │   │                       │   ├── GetWeatherFunction.java
│   │   │                       │   └── PconlineRegionFunction.java
│   │   │                       ├── rag/
│   │   │                       │   └── Todo.java
│   │   │                       ├── router/
│   │   │                       │   └── RouterFunctionConfig.java
│   │   │                       └── SpringAiAmazonBedrockApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── azure/
│                           └── openai/
│                               └── SpringAiAzureOpenaiApplicationTests.java
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
public class SpringAiAmazonBedrockApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAmazonBedrockApplication.class, args);
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
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            ChatController chatController,
            EmbeddingController embeddingController) {
        return route(GET("/chat"), chatController::chat)
                .andRoute(POST("/embed"), embeddingController::embed);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiAmazonBedrockApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```
3. **测试文本嵌入**：
   ```bash
   curl -X POST http://localhost:8080/embed \
     -H "Content-Type: text/plain" \
     -d "Spring AI is awesome"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的模型，例如 Claude 3 适合复杂推理，Titan 适合嵌入任务
2. **参数调优**：根据应用场景调整温度、top_p 等参数
3. **错误处理**：实现适当的错误处理和重试机制
4. **成本控制**：监控 API 使用情况，设置合理的使用限制
5. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [Amazon Bedrock 官方文档](https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)