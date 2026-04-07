# Spring AI 入门实践：Spring AI 与 Hugging Face 集成

## 概述

Hugging Face 是一个开源的 AI 模型和数据集平台，提供了大量预训练的模型。Spring AI 提供了对 Hugging Face 的集成支持，使得开发者可以在 Spring 应用中轻松使用 Hugging Face 上的各种模型。

## 准备工作

### 1. 获取 Hugging Face API 令牌

首先，您需要在 Hugging Face 注册账号并获取 API 令牌：

1. 访问 [Hugging Face 官网](https://huggingface.co/)
2. 注册账号并登录
3. 创建 API 令牌

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-huggingface-spring-boot-starter</artifactId>
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

### 3. 配置 API 令牌

在 `application.properties` 文件中配置 Hugging Face 相关设置：

```properties
# Hugging Face 配置
spring.ai.huggingface.api-key=your-api-token
spring.ai.huggingface.chat.enabled=true
spring.ai.huggingface.chat.model=mistralai/Mistral-7B-v0.1
spring.ai.huggingface.chat.options.temperature=0.7

# Embedding 配置
spring.ai.huggingface.embedding.enabled=true
spring.ai.huggingface.embedding.model=sentence-transformers/all-MiniLM-L6-v2
```

## 核心功能

### 1. 文本生成

使用 Hugging Face 进行文本生成：

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

使用 Hugging Face 进行文本嵌入：

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

## 完整示例

### 项目结构

```
spring-ai-huggingface/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── huggingface/
│   │   │                   ├── controller/
│   │   │                   │   ├── ChatController.java
│   │   │                   │   └── EmbeddingController.java
│   │   │                   └── SpringAiHuggingFaceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── huggingface/
│                           └── SpringAiHuggingFaceApplicationTests.java
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
public class SpringAiHuggingFaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuggingFaceApplication.class, args);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiHuggingFaceApplication` 类
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

1. **模型选择**：根据具体需求选择合适的 Hugging Face 模型
2. **参数调优**：根据应用场景调整温度等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [Hugging Face 官方文档](https://huggingface.co/docs)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Hugging Face 模型库](https://huggingface.co/models)