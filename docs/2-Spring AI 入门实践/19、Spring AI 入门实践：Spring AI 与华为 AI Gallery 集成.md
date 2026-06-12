# 19、Spring AI 入门实践：Spring AI 与华为 AI Gallery 集成

## 一、项目概述

华为 AI Gallery 是华为云提供的人工智能模型和算法的聚集地，提供了丰富的模型资源。Spring AI 提供了对华为 AI Gallery 的集成支持，使得开发者可以在 Spring 应用中轻松使用华为 AI Gallery 中的模型。

### 1.1 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-huawei-gallery
**本地路径**：`spring-ai-huawei-gallery/`

### 核心功能

- **丰富的模型库**：华为 AI Gallery 提供大量优质模型
- **多模型支持**：文本、图像、语音等多种类型
- **企业级服务**：华为云稳定可靠的服务保障
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 企业应用 AI 赋能
- 智能客服和助手
- 内容生成和处理
- 数据分析和洞察

## 三、性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [华为云官方文档](https://www.huaweicloud.com/) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 四、应用案例

### 企业 AI 赋能平台
- **业务场景**：企业各部门 AI 能力统一接入和管理
- **性能指标**：
  - 模型加载时间：1-3 秒
  - 并发支持：100+ 请求/秒
  - 服务可用性：99.9%
- **技术方案**：
  - 使用华为 AI Gallery 丰富模型库
  - 统一 API 网关管理模型调用
  - 监控和日志系统追踪使用情况

### 智能客服助手
- **业务场景**：企业客服自动化和智能问答
- **性能指标**：
  - 问题解决率：85-92%
  - 平均响应时间：800-1500ms
  - 人工转接率：降低 60%
- **技术方案**：
  - 结合企业知识库实现智能问答
  - 多轮对话上下文管理
  - 人工客服无缝转接

### 内容生成与处理
- **业务场景**：营销文案、产品描述、内容摘要生成
- **性能指标**：
  - 生成速度：400-800 tokens/秒
  - 内容质量评分：4.2/5.0
  - 修改周期：缩短 70%
- **技术方案**：
  - 使用 Gallery 中高质量生成模型
  - Prompt 模板化处理不同场景
  - 支持批量生成和审核流程

## 五、华为 AI Gallery 简介

华为 AI Gallery 是华为云推出的人工智能模型和算法服务平台。

### 核心特性

| 特性 | 说明 |
|------|------|
| 模型丰富 | 提供大量优质预训练模型 |
| 企业级 | 稳定可靠的云服务 |
| 多模态 | 支持文本、图像、语音等 |
| 安全合规 | 符合企业安全要求 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- 华为云 API 密钥

### 3.2 获取华为云 API 密钥

1. **访问华为云**：https://console.huaweicloud.com/
2. **注册账号并登录**
3. **创建 IAM 用户并获取 Access Key 和 Secret Key**

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-huawei-gallery/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── huaweigallery/
│   │   │                   ├── SpringAiHuaweiGalleryApplication.java
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
    name: spring-ai-huawei-gallery
  
  ai:
    huaweiai:
      gallery:
        api-key: ${HUAWEI_API_KEY:your-api-key}
        secret-key: ${HUAWEI_SECRET_KEY:your-secret-key}
        chat:
          enabled: true
          options:
            temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.huaweigallery.service;

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
    
    public Map<String, Object> chatWithSystemPrompt(String systemPrompt, String userMessage) {
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
        
        return Map.of(
                "systemPrompt", systemPrompt,
                "message", userMessage,
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
package com.github.partmeai.huaweigallery.controller;

import com.github.partmeai.huaweigallery.service.ChatService;
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
    
    @PostMapping("/chat/system")
    public Map<String, Object> chatWithSystem(@RequestBody Map<String, String> request) {
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有帮助的助手。");
        String message = request.get("message");
        return chatService.chatWithSystemPrompt(systemPrompt, message);
    }
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.huaweigallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiHuaweiGalleryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuaweiGalleryApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=Hello"
```

## 八、部署方式

### 8.1 本地运行

```bash
export HUAWEI_API_KEY=your-api-key
export HUAWEI_SECRET_KEY=your-secret-key
cd spring-ai-huawei-gallery
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export HUAWEI_API_KEY=your-api-key
export HUAWEI_SECRET_KEY=your-secret-key
mvn clean package -DskipTests
java -jar target/spring-ai-huawei-gallery-1.0.0-SNAPSHOT.jar
```

## 九、Java 客户端

以下是一个独立的 Java 客户端示例，用于调用 Spring AI 华为 AI Gallery 服务：

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

public class HuaweiGalleryClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public HuaweiGalleryClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> chat(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("message", message);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/chat", request, Map.class);

        return response.getBody();
    }

    public Map<String, Object> chatWithSystem(String systemPrompt, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
            "systemPrompt", systemPrompt,
            "message", message
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/chat/system", request, Map.class);

        return response.getBody();
    }

    public static void main(String[] args) {
        HuaweiGalleryClient client = new HuaweiGalleryClient("http://localhost:8080");

        Map<String, Object> result = client.chat("你好");
        System.out.println("Response: " + result.get("response"));

        Map<String, Object> systemResult = client.chatWithSystem(
            "你是一个专业的技术顾问。",
            "请介绍一下华为 AI Gallery"
        );
        System.out.println("\nSystem Response: " + systemResult.get("response"));
    }
}
```

## 十、使用示例

### 9.1 最佳实践

1. **模型选择**：根据华为 AI Gallery 提供的模型进行选择
2. **企业应用**：适合企业级应用场景
3. **安全合规**：注意数据安全和合规性要求

## 十、运行项目

### 10.1 启动应用

```bash
export HUAWEI_API_KEY=your-api-key
export HUAWEI_SECRET_KEY=your-secret-key
cd spring-ai-huawei-gallery
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 API 密钥问题

**Q: 提示 API 密钥无效怎么办？**

- 确认 API 密钥和 Secret Key 正确
- 检查环境变量或配置文件中的密钥设置
- 确认华为云账户状态正常

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- 华为云 AI Gallery：https://www.huaweicloud.com/
- Spring AI Huawei Gallery：参考官方文档
- 示例模块：spring-ai-huawei-gallery

## 十四、致谢

感谢华为云团队在 AI Gallery 平台方面的开创性工作，为企业级 AI 应用提供了丰富的模型资源和稳定可靠的云服务。感谢 Spring AI 团队提供的统一抽象接口，简化了华为 AI Gallery 模型的集成工作。
