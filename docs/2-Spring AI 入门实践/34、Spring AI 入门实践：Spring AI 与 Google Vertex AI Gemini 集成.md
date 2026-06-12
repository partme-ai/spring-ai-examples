# Spring AI 入门实践：Spring AI 与 Google Vertex AI Gemini 集成

> 基于 Spring AI 框架实现与 Google Vertex AI Gemini 的集成，提供多模态大语言模型、文本生成、图像理解、音频处理等功能，支持企业级多模态应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Google Vertex AI Gemini 的示例，展示了如何在 Java/Spring Boot 应用中使用 Gemini 系列多模态模型。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Google Vertex AI | - | Google Cloud AI 平台 |
| spring-ai-starter-model-vertex-ai-gemini | - | Spring AI Vertex AI 集成 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-vertex-ai-gemini
**本地路径**：`spring-ai-vertex-ai-gemini/`

### 1.4 核心功能

- **Gemini 系列模型**：Gemini Pro、Gemini Ultra 等多种模型
- **强大多模态能力**：支持文本、图像、音频等多模态交互
- **企业级服务**：Google Cloud 稳定可靠的服务保障
- **工具调用**：支持函数调用和工具集成
- **流式响应**：支持实时流式输出
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 多模态智能客服和助手
- 多模态内容生成和创作
- 图像理解和分析
- 知识问答系统
- 企业级多模态应用

## 二、Google Vertex AI Gemini 简介

Google Vertex AI 是 Google Cloud 提供的企业级 AI 平台，包含 Gemini 系列多模态大语言模型。

### 2.1 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Google Vertex AI 官方文档](https://cloud.google.com/vertex-ai) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

### 2.2 可用模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Gemini Pro | 综合能力强，支持多模态 | 通用对话、多模态任务 |
| Gemini Ultra | 能力最强，支持复杂任务 | 复杂任务、专业应用 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 多模态 | 支持文本、图像、音频等多模态 |
| 企业级 | Google Cloud 稳定可靠的服务 |
| 工具调用 | 原生支持函数调用 |
| Google 生态 | 与 Google Cloud 生态集成 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Google Cloud 账号和凭证

### 3.2 获取 Google Cloud 凭证

1. **访问 Google Cloud Console**：https://console.cloud.google.com/
2. **注册账号并登录**
3. **启用 Vertex AI API**
4. **创建服务账号并获取 JSON 凭证**
5. **确保账号有足够的配额**

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-vertexai-gemini/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── vertexai/
│   │   │                   ├── SpringAiVertexAiApplication.java
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
        <artifactId>spring-ai-starter-model-vertex-ai-gemini</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-vertex-ai-embedding</artifactId>
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
    name: spring-ai-vertexai-gemini
  
  ai:
    vertexai:
      gemini:
        project-id: ${GCP_PROJECT_ID:your-project-id}
        location: ${GCP_LOCATION:us-central1}
        credentials-location: ${GCP_CREDENTIALS_LOCATION:file:/path/to/credentials.json}
        chat:
          enabled: true
          options:
            model: gemini-pro
            temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.vertexai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
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
package com.github.partmeai.vertexai.controller;

import com.github.partmeai.vertexai.service.ChatService;
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
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.vertexai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiVertexAiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiVertexAiApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Google Gemini"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
export GCP_PROJECT_ID=your-project-id
export GCP_LOCATION=us-central1
export GCP_CREDENTIALS_LOCATION=file:/path/to/credentials.json
cd spring-ai-vertexai-gemini
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export GCP_PROJECT_ID=your-project-id
export GCP_LOCATION=us-central1
export GCP_CREDENTIALS_LOCATION=file:/path/to/credentials.json
mvn clean package -DskipTests
java -jar target/spring-ai-vertexai-gemini-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **模型选择**：
   - 通用对话：gemini-pro
   - 复杂任务：gemini-ultra

2. **多模态应用**：充分利用 Gemini 的多模态能力

3. **Google Cloud 集成**：与 Google Cloud 生态系统集成

### 9.2 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 通用对话 | gemini-pro |
| 复杂任务 | gemini-ultra |

## 十、运行项目

### 10.1 启动应用

```bash
export GCP_PROJECT_ID=your-project-id
export GCP_LOCATION=us-central1
export GCP_CREDENTIALS_LOCATION=file:/path/to/credentials.json
cd spring-ai-vertexai-gemini
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 凭证配置问题

**Q: Google Cloud 凭证配置无效怎么办？**

- 确认凭证 JSON 文件路径正确
- 检查环境变量配置
- 确认已启用 Vertex AI API
- 确认服务账号有足够权限

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Google Vertex AI 官方文档：https://cloud.google.com/vertex-ai
- Spring AI Vertex AI Gemini：参考官方文档
- 示例模块：spring-ai-vertexai-gemini

---

## 十四、Java 客户端示例

### 14.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class VertexAIClient {

    private static final String BASE_URL = "http://localhost:8080/api/vertexai";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {
        String url = BASE_URL + "/chat?message={message}";
        return restTemplate.getForObject(url, String.class, message);
    }

    public Map<String, Object> chatCompletion(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(BASE_URL + "/completions", entity, Map.class);
    }
}
```

---

## 十五、致谢

- **感谢 Google Vertex AI 团队** 提供强大的多模态 AI 平台
- **感谢 Spring AI 团队** 提供 Vertex AI 集成框架
- **感谢开源社区** 提供丰富的技术资源
