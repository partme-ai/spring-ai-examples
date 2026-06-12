# Spring AI 入门实践：Spring AI 与 LLMs Free API 集成

> 基于 Spring AI 框架实现与 LLMs Free API 的集成，提供多平台聚合服务、免费访问接口，支持 Kimi、通义千问、智谱清言、阶跃星辰等模型。

### ⚠️ 重要声明

**仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！**

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 LLMs Free API 的示例，展示了如何在 Java/Spring Boot 应用中免费使用多个大模型平台。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| LLMs Free API | - | 免费API聚合服务 |
| llms-free-api | - | Node.js API 服务 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-llms-free-api
**本地路径**：`spring-ai-llms-free-api/`

### 1.4 核心功能

## 二、LLMs Free API 简介

LLM Red Team 意为 LLM 大模型红队，该组织成立的愿景是通过各厂商大模型应用中已公开的信息挖掘潜在的安全问题并公开一些技术细节。

### 可用平台对比

| 平台 | 仓库 | 特点 |
|------|------|------|
| Moonshot AI（Kimi.ai） | kimi-free-api | 月之暗面 Kimi 模型 |
| 阶跃星辰（跃问StepChat） | step-free-api | 阶跃星辰 StepChat 模型 |
| 阿里通义（Qwen） | qwen-free-api | 阿里通义千问模型 |
| ZhipuAI（智谱清言） | glm-free-api | 智谱 AI 模型 |
| 秘塔AI（metaso） | metaso-free-api | 秘塔 AI 模型 |
| 聆心智能（Emohaa） | emohaa-free-api | 聆心智能模型 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 免费访问 | 提供免费的 API 访问 |
| 多平台 | 支持多个大模型平台 |
| 开源 | 组织开源维护 |
| 学习研究 | 仅限学习研究用途 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Node.js（用于部署 Free API 服务）

### 3.2 部署 Free API 服务

1. **选择 Free API 项目**（如 kimi-free-api）
2. **访问 GitHub 仓库**：https://github.com/LLM-Red-Team/
3. **按照项目 README 部署服务**
4. **获取服务的访问地址**（如 http://localhost:8000）

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-llmsfreeapi/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── llmsfreeapi/
│   │   │                   ├── SpringAiLlmFreeApiApplication.java
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
        <artifactId>spring-ai-starter-model-openai</artifactId>
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
    name: spring-ai-llmsfreeapi
  
  ai:
    llms-free-api:
      base-url: ${LLMS_FREE_API_BASE_URL:http://localhost:8000/v1}
      api-key: ${LLMS_FREE_API_KEY:your-api-key}
      chat:
        enabled: true
        options:
          model: ${LLMS_FREE_API_MODEL:kimi}
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.llmsfreeapi.service;

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
package com.github.partmeai.llmsfreeapi.controller;

import com.github.partmeai.llmsfreeapi.service.ChatService;
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
package com.github.partmeai.llmsfreeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiLlmFreeApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiLlmFreeApiApplication.class, args);
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
  -d '{"message": "请介绍一下 LLMs Free API"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
# 1. 先启动对应的 Free API 服务（在另一个终端）
# 2. 配置环境变量并启动 Spring 应用
export LLMS_FREE_API_BASE_URL=http://localhost:8000/v1
export LLMS_FREE_API_KEY=your-api-key
export LLMS_FREE_API_MODEL=kimi
cd spring-ai-llmsfreeapi
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export LLMS_FREE_API_BASE_URL=http://localhost:8000/v1
export LLMS_FREE_API_KEY=your-api-key
export LLMS_FREE_API_MODEL=kimi
mvn clean package -DskipTests
java -jar target/spring-ai-llmsfreeapi-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **仅供学习研究**：仅用于学习研究，不要用于生产环境
2. **遵守条款**：遵守各平台的使用条款
3. **合理使用**：避免对官方服务造成压力
4. **风险自担**：使用风险由使用者自行承担

### 9.2 平台推荐表

| 平台 | 推荐用途 |
|------|----------|
| Kimi（Moonshot AI） | 长文档、专业内容 |
| 通义千问 | 中文内容、通用场景 |
| 智谱清言 | 中文对话、通用场景 |
| 阶跃星辰 | 对话交互、通用场景 |

## 十、运行项目

### 10.1 启动应用

```bash
# 1. 先启动对应的 Free API 服务
# 2. 启动 Spring 应用
export LLMS_FREE_API_BASE_URL=http://localhost:8000/v1
export LLMS_FREE_API_KEY=your-api-key
export LLMS_FREE_API_MODEL=kimi
cd spring-ai-llmsfreeapi
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

## 十一、常见问题

### 11.1 服务连接问题

**Q: Free API 服务连接失败怎么办？**

- 确认 Free API 服务已启动
- 检查服务地址和端口配置
- 查看 Free API 服务的日志
- 确认本地网络连接正常

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- LLM Red Team GitHub：https://github.com/LLM-Red-Team
- spring-ai-starter-model-openai：https://github.com/hiwepy/spring-ai-starter-model-openai
- 示例模块：spring-ai-llmsfreeapi

---

## 十四、Java 客户端示例

### 14.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class LLMsFreeClient {

    private static final String BASE_URL = "http://localhost:8080/api/llmsfree";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String platform, String message) {
        String url = BASE_URL + "/chat?platform={platform}&message={message}";
        return restTemplate.getForObject(url, String.class, platform, message);
    }

    public Map<String, Object> chatCompletion(String platform, Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(BASE_URL + "/{platform}/completions", entity, Map.class, platform);
    }
}
```

---

## 十五、致谢

- **感谢 LLM Red Team 组织** 提供免费的 API 聚合服务
- **感谢 Spring AI 团队** 提供集成框架支持
- **感谢开源社区** 提供丰富的技术资源

---

## ⚠️ 免责声明

本项目仅用于学习和研究目的。禁止对外提供服务或商业用途，避免对官方服务造成压力。使用本服务所产生的任何后果，由使用者自行承担。
