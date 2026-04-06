# Spring AI 入门实践：Spring AI 与 Azure OpenAI 集成

## 概述

Azure OpenAI 是 Microsoft Azure 提供的一项服务，让开发者可以在 Azure 环境中使用 OpenAI 的强大模型。Spring AI 提供了对 Azure OpenAI 的集成支持，使得开发者可以在 Spring 应用中轻松使用 Azure OpenAI 的各种功能。

## 准备工作

### 1. 获取 Azure OpenAI 资源

首先，您需要在 Azure 门户中创建 OpenAI 资源：

1. 登录 [Azure 门户](https://portal.azure.com/)
2. 创建 OpenAI 资源
3. 部署模型（如 gpt-4、gpt-3.5-turbo 等）
4. 获取 API 密钥和端点

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-azure-openai</artifactId>
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

### 3. 配置 API 密钥和端点

在 `application.properties` 文件中配置 Azure OpenAI 相关设置：

```properties
# Azure OpenAI 配置
spring.ai.azure.openai.api-key=your-api-key
spring.ai.azure.openai.endpoint=your-endpoint
spring.ai.azure.openai.chat.enabled=true
spring.ai.azure.openai.chat.deployment-name=gpt-35-turbo
spring.ai.azure.openai.chat.options.temperature=0.7

# Embedding 配置
spring.ai.azure.openai.embedding.enabled=true
spring.ai.azure.openai.embedding.deployment-name=text-embedding-ada-002
```

## 核心功能

### 1. 文本生成

使用 Azure OpenAI 进行文本生成：

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

使用 Azure OpenAI 进行文本嵌入：

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

Azure OpenAI 支持工具调用功能，您可以定义自定义工具：

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
import java.util.Map;

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

    @Bean
    public ToolSpecification pconlineRegionFunction() {
        return ToolSpecification.builder()
            .withName("get_pconline_region")
            .withDescription("获取 PConline 区域信息")
            .withParameters(Map.of(
                "type", "object",
                "properties", Map.of(
                    "region", Map.of(
                        "type", "string",
                        "description", "区域名称"
                    )
                ),
                "required", List.of("region")
            ))
            .build();
    }

    @Bean
    public ToolResponse pconlineRegionToolResponse() {
        return params -> {
            String region = (String) params.get("region");
            // 模拟区域数据
            return "{\"region\": \"" + region + "\", \"code\": \"001\", \"name\": \"测试区域\"}";
        };
    }
}
```

### 4. RAG 实现

使用 Azure OpenAI 实现检索增强生成（RAG）：

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
spring-ai-azure-openai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── azure/
│   │   │                   └── openai/
│   │   │                       ├── agent/
│   │   │                       │   └── Todo.java
│   │   │                       ├── controller/
│   │   │                       │   ├── ChatController.java
│   │   │                       │   └── EmbeddingController.java
│   │   │                       ├── finetune/
│   │   │                       │   └── Todo.java
│   │   │                       ├── functions/
│   │   │                       │   ├── FunctionConfig.java
│   │   │                       │   ├── GetWeatherFunction.java
│   │   │                       │   └── PconlineRegionFunction.java
│   │   │                       ├── rag/
│   │   │                       │   └── Todo.java
│   │   │                       ├── router/
│   │   │                       │   └── RouterFunctionConfig.java
│   │   │                       └── SpringAiAzureOpenaiApplication.java
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
public class SpringAiAzureOpenaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAzureOpenaiApplication.class, args);
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

1. **启动应用**：运行 `SpringAiAzureOpenaiApplication` 类
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

1. **模型选择**：根据具体需求选择合适的模型和部署
2. **参数调优**：根据应用场景调整温度、top_p 等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [Azure OpenAI 官方文档](https://learn.microsoft.com/en-us/azure/cognitive-services/openai/)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Azure OpenAI 定价](https://azure.microsoft.com/en-us/pricing/details/cognitive-services/openai-service/)