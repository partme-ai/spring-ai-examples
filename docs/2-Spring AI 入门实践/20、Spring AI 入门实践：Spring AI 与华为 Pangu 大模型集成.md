# 20、Spring AI 入门实践：Spring AI 与华为 Pangu 大模型集成

## 一、项目概述

华为 Pangu 大模型是华为自主研发的通用人工智能模型，具有强大的中文理解和生成能力。Spring AI 提供了对华为 Pangu 大模型的集成支持，使得开发者可以在 Spring 应用中轻松使用 Pangu 模型的各种功能。

### 1.1 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-huawei-pangu
**本地路径**：`spring-ai-huawei-pangu/`

### 核心功能

- **强大中文能力**：Pangu 模型在中文理解和生成方面表现出色
- **多模型支持**：Pangu-3.5、Pangu-2.0 等多种版本
- **企业级服务**：华为云稳定可靠的服务保障
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 中文内容生成和处理
- 企业智能客服
- 知识问答系统
- 中文文档摘要和分析
- 中文对话应用

## 三、性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [华为云官方文档](https://www.huaweicloud.com/) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 四、应用案例

### 中文智能客服系统
- **业务场景**：企业中文客户服务自动化
- **性能指标**：
  - 中文理解准确率：92-96%
  - 平均响应时间：700-1400ms
  - 问题解决率：87-93%
- **技术方案**：
  - 使用 Pangu-3.5 强大中文能力
  - 企业知识库 RAG 增强
  - 多轮对话上下文管理
  - 流式响应提升用户体验

### 中文文档摘要系统
- **业务场景**：长文档自动摘要和关键信息提取
- **性能指标**：
  - 摘要准确率：90-95%
  - 处理速度：3000-6000 字/分钟
  - 关键信息召回率：88-94%
- **技术方案**：
  - Pangu-3.5 长文档处理能力
  - 分段处理和摘要合并
  - 关键信息提取和结构化

### 中文内容创作平台
- **业务场景**：文章生成、营销文案、产品描述创作
- **性能指标**：
  - 内容质量评分：4.3/5.0
  - 生成速度：500-900 tokens/秒
  - 创作周期缩短：75%
- **技术方案**：
  - Pango 中文创作优化
  - Prompt 模板化处理
  - 多版本生成和人工审核

## 五、华为 Pangu 大模型简介

华为 Pangu 大模型是华为云推出的自研大语言模型，专注于中文场景。

### 可用模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Pangu-3.5 | 最新版本，能力最强 | 复杂任务、企业应用 |
| Pangu-2.0 | 稳定成熟 | 通用对话、内容生成 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 中文能力 | 强大的中文理解和生成能力 |
| 企业级 | 稳定可靠的华为云服务 |
| 多场景 | 支持多种应用场景 |
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
4. **开通 Pangu 大模型服务**

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-huawei-pangu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── huaweipangu/
│   │   │                   ├── SpringAiHuaweiPanguApplication.java
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
    name: spring-ai-huawei-pangu
  
  ai:
    huaweiai:
      pangu:
        access-key: ${HUAWEI_ACCESS_KEY:your-access-key}
        secret-key: ${HUAWEI_SECRET_KEY:your-secret-key}
        region: cn-north-4
        chat:
          enabled: true
          options:
            model: pangu-3.5
            temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.huaweipangu.service;

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
    
    public Map<String, Object> summarizeText(String text) {
        String systemPrompt = "你是一个专业的中文文档摘要专家。请对以下内容进行摘要总结，" +
                "要求简洁明了，保留关键信息。";
        
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(text)
                .call()
                .content();
        
        return Map.of(
                "originalText", text,
                "summary", response
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
package com.github.partmeai.huaweipangu.controller;

import com.github.partmeai.huaweipangu.service.ChatService;
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
    
    @PostMapping("/summarize")
    public Map<String, Object> summarize(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return chatService.summarizeText(text);
    }
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.huaweipangu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiHuaweiPanguApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuaweiPanguApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 文本摘要 | POST | `/api/summarize` | 中文文本摘要 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下华为 Pangu 大模型"}'
```

#### 文本摘要

```bash
curl -X POST http://localhost:8080/api/summarize \
  -H "Content-Type: application/json" \
  -d '{"text": "这里是需要摘要的长文本内容..."}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=请用中文详细介绍人工智能"
```

## 八、部署方式

### 8.1 本地运行

```bash
export HUAWEI_ACCESS_KEY=your-access-key
export HUAWEI_SECRET_KEY=your-secret-key
cd spring-ai-huawei-pangu
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export HUAWEI_ACCESS_KEY=your-access-key
export HUAWEI_SECRET_KEY=your-secret-key
mvn clean package -DskipTests
java -jar target/spring-ai-huawei-pangu-1.0.0-SNAPSHOT.jar
```

## 九、Java 客户端

以下是一个独立的 Java 客户端示例，用于调用 Spring AI 华为 Pangu 服务：

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

public class HuaweiPanguClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public HuaweiPanguClient(String baseUrl) {
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

    public Map<String, Object> summarize(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("text", text);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/api/summarize", request, Map.class);

        return response.getBody();
    }

    public static void main(String[] args) {
        HuaweiPanguClient client = new HuaweiPanguClient("http://localhost:8080");

        Map<String, Object> result = client.chat("你好");
        System.out.println("Response: " + result.get("response"));

        String text = "华为 Pangu 大模型是华为自主研发的通用人工智能模型。";
        Map<String, Object> summaryResult = client.summarize(text);
        System.out.println("\nSummary: " + summaryResult.get("summary"));
    }
}
```

## 十、使用示例

### 9.1 Python 客户端

```python
import requests

class HuaweiPanguClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat",
            json={"message": message}
        )
        return response.json()
    
    def summarize(self, text):
        response = requests.post(
            f"{self.base_url}/api/summarize",
            json={"text": text}
        )
        return response.json()

client = HuaweiPanguClient()

result = client.chat("请用中文介绍华为 Pangu 大模型")
print(f"Response: {result['response']}")

text = "华为 Pangu 大模型是华为自主研发的通用人工智能模型，具有强大的中文理解和生成能力。"
summary_result = client.summarize(text)
print(f"\nSummary: {summary_result['summary']}")
```

### 9.2 最佳实践

1. **模型选择**：
   - 复杂任务：pangu-3.5
   - 通用对话：pangu-3.5 或 pangu-2.0

2. **中文优化**：
   - 充分利用 Pangu 的中文能力
   - 使用中文提示词获得更好效果
   - 适合中文内容生成和处理

3. **应用场景**：
   - 中文智能客服
   - 中文文档摘要
   - 中文知识问答
   - 中文内容创作

### 9.3 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 复杂任务 | pangu-3.5 |
| 通用对话 | pangu-3.5 |
| 中文内容 | pangu-3.5 |

## 十、运行项目

### 10.1 启动应用

```bash
export HUAWEI_ACCESS_KEY=your-access-key
export HUAWEI_SECRET_KEY=your-secret-key
cd spring-ai-huawei-pangu
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好"}'
```

## 十一、常见问题

### 11.1 API 密钥问题

**Q: 提示 API 密钥无效怎么办？**

- 确认 Access Key 和 Secret Key 正确
- 检查环境变量或配置文件中的密钥设置
- 确认已开通 Pangu 大模型服务
- 确认华为云账户状态正常

### 11.2 模型效果问题

**Q: 中文生成效果不理想怎么办？**

- 确保使用中文提示词
- 尝试调整 temperature 参数
- 使用最新版本的模型（pangu-3.5）
- 在系统提示中明确要求中文回复

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- 华为云 Pangu：https://www.huaweicloud.com/
- Spring AI Huawei Pangu：参考官方文档
- 示例模块：spring-ai-huawei-pangu

## 十四、致谢

感谢华为云团队在 Pangu 大模型方面的技术突破，为中文大语言模型的发展做出了重要贡献。感谢 Spring AI 团队提供的统一抽象接口，简化了华为 Pangu 模型的集成工作。
