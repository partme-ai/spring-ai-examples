# 21、Spring AI 入门实践：Spring AI 与 Hugging Face 集成

## 一、项目概述

Hugging Face 是一个开源的 AI 模型和数据集平台，提供了大量预训练的模型。Spring AI 提供了对 Hugging Face 的集成支持，使得开发者可以在 Spring 应用中轻松使用 Hugging Face 上的各种模型。

### 核心功能

- **丰富的模型库**：Hugging Face 提供数十万预训练模型
- **多模态支持**：文本、图像、音频等多种类型
- **开源生态**：社区驱动的开源模型生态
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 研究和实验
- 快速原型开发
- 定制化模型应用
- 教育和学习

## 二、Hugging Face 简介

Hugging Face 是一个开源的 AI 模型和数据集平台，提供了大量高质量的预训练模型。

### 核心特性

| 特性 | 说明 |
|------|------|
| 模型丰富 | 提供数十万预训练模型 |
| 开源 | 社区驱动的开源生态 |
| 多模态 | 支持文本、图像、音频等 |
| 数据集 | 提供大量公开数据集 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Hugging Face API 令牌

### 3.2 获取 Hugging Face API 令牌

1. **访问 Hugging Face**：https://huggingface.co/
2. **注册账号并登录**
3. **创建 API 令牌（Access Token）**

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-huggingface/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── huggingface/
│   │   │                   ├── SpringAiHuggingfaceApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   └── service/
│   │   │                       └── ChatService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 五、核心配置

### 5.1 Maven 依赖

```xml
<dependencies>
    <!-- For Spring AI Common -->
    <dependency>
        <groupId>com.github.partmeai</groupId>
        <artifactId>spring-ai-common</artifactId>
        <version>${revision}</version>
    </dependency>
    <!-- For Chat Completion & Embedding -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-huggingface</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-autoconfigure-retry</artifactId>
    </dependency>
    <!-- For Chat Memory -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
    </dependency>
    <!-- For Vector Store  -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure-cosmos-db</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-cassandra</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-couchbase</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-gemfire</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mongodb-atlas</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-neo4j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-opensearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-oracle</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-typesense</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-weaviate</artifactId>
    </dependency>
    <!-- For Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <!-- For Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
    <!-- For Embed Undertow -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- For Testcontainers : https://testcontainers.com/ -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>ollama</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>typesense</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 5.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-huggingface
  
  ai:
    huggingface:
      api-key: ${HUGGINGFACE_API_KEY:your-api-token}
      chat:
        enabled: true
        options:
          model: mistralai/Mistral-7B-v0.1
          temperature: 0.7
      embedding:
        enabled: true
        options:
          model: sentence-transformers/all-MiniLM-L6-v2

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.huggingface.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    
    public ChatService(ChatClient chatClient, EmbeddingModel embeddingModel) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }
    
    public Map<String, Object> chat(String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        return Map.of(
                "message", message,
                "response", response
        );
    }
    
    public Map<String, Object> embed(String text) {
        float[] embedding = embeddingModel.embed(text);
        
        return Map.of(
                "text", text,
                "embedding", embedding,
                "dimension", embedding.length
        );
    }
    
    public Flux<String> streamingChat(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.huggingface.controller;

import com.github.partmeai.huggingface.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatService.chat(message);
    }
    
    @PostMapping("/embed")
    public Map<String, Object> embed(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return chatService.embed(text);
    }
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.huggingface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiHuggingfaceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuggingfaceApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 文本嵌入 | POST | `/api/embed` | 文本向量化 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

#### 文本嵌入

```bash
curl -X POST http://localhost:8080/api/embed \
  -H "Content-Type: application/json" \
  -d '{"text": "This is a test sentence"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
export HUGGINGFACE_API_KEY=your-api-token
cd spring-ai-huggingface
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export HUGGINGFACE_API_KEY=your-api-token
mvn clean package -DskipTests
java -jar target/spring-ai-huggingface-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **模型选择**：根据 Hugging Face 模型库选择合适的模型
2. **开源研究**：适合研究和实验场景
3. **本地部署**：考虑结合 Ollama 进行本地部署

## 十、运行项目

### 10.1 启动应用

```bash
export HUGGINGFACE_API_KEY=your-api-token
cd spring-ai-huggingface
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 API 令牌问题

**Q: 提示 API 令牌无效怎么办？**

- 确认 API 令牌正确且有效
- 检查环境变量或配置文件中的令牌设置
- 确认 Hugging Face 账户状态正常

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Hugging Face：https://huggingface.co/
- Spring AI Hugging Face：参考官方文档
- 示例模块：spring-ai-huggingface

## 十四、致谢

感谢 Hugging Face 团队和 Spring AI 团队提供的优秀工具。
