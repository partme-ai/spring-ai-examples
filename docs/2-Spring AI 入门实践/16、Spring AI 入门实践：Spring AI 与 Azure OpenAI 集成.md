# 16、Spring AI 入门实践：Spring AI 与 Azure OpenAI 集成

## 一、项目概述

Azure OpenAI 是 Microsoft Azure 提供的一项服务，让开发者可以在 Azure 企业级环境中使用 OpenAI 的强大模型。Spring AI 提供了对 Azure OpenAI 的完整集成支持，使得开发者可以在 Spring 应用中轻松使用 Azure OpenAI 的各种功能，包括文本生成、嵌入、图像生成等。

### 核心功能

- **企业级安全**：Azure 的安全和合规保障
- **多种模型支持**：GPT-4、GPT-3.5 Turbo、DALL-E、Embeddings 等
- **全球部署**：Azure 全球数据中心支持
- **企业级 SLA**：99.9% 可用性保障
- **VNet 集成**：支持私有网络访问
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 企业级 AI 应用开发
- 高可用性要求的生产环境
- 安全合规要求高的应用
- 全球部署的 AI 服务
- 与 Azure 生态系统集成的应用

## 二、Azure OpenAI 简介

Azure OpenAI Service 提供了与 OpenAI API 兼容的 REST API，同时带来了 Azure 的企业级功能。

### 可用模型对比

| 模型类别 | 模型名称 | 特点 | 适用场景 |
|---------|---------|------|---------|
| 对话模型 | GPT-4 Turbo | 最新、最强大 | 复杂推理、长文档 |
| 对话模型 | GPT-4 | 高质量输出 | 企业应用 |
| 对话模型 | GPT-3.5 Turbo | 平衡性能与成本 | 通用对话 |
| 嵌入模型 | text-embedding-3 | 高质量嵌入 | RAG、语义搜索 |
| 图像模型 | DALL-E 3 | 图像生成 | 创意内容 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 企业安全 | Azure Active Directory、VNet、私有端点 |
| 内容过滤 | 内置内容安全检测 |
| 监控告警 | Azure Monitor 集成 |
| 容量规划 | 可配置的配额和限制 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Azure 订阅（已开通 Azure OpenAI 服务）

### 3.2 Azure 资源准备

1. **登录 Azure 门户**：https://portal.azure.com/
2. **创建 OpenAI 资源**：
   - 选择区域（推荐 East US、West Europe 等）
   - 创建资源名称
   - 选择定价层
3. **部署模型**：
   - 在 Azure OpenAI Studio 中部署需要的模型
   - 记录部署名称（deployment name）
4. **获取凭证**：
   - 获取 API 密钥和端点 URL

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-azure-openai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── azureopenai/
│   │   │                   ├── SpringAiAzureOpenAiApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── ChatController.java
│   │   │                   │   └── EmbeddingController.java
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
        <artifactId>spring-ai-starter-model-azure-openai</artifactId>
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
    name: spring-ai-azure-openai
  
  ai:
    azure:
      openai:
        api-key: ${AZURE_OPENAI_API_KEY:your-api-key}
        endpoint: ${AZURE_OPENAI_ENDPOINT:https://your-resource.openai.azure.com}
        chat:
          enabled: true
          deployment-name: gpt-35-turbo
          options:
            temperature: 0.7
        embedding:
          enabled: true
          deployment-name: text-embedding-ada-002

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.azureopenai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
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
    
    public Map<String, Object> embed(String text) {
        float[] embedding = embeddingModel.embed(text);
        
        return Map.of(
                "text", text,
                "dimension", embedding.length,
                "embedding", embedding
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
package com.github.partmeai.azureopenai.controller;

import com.github.partmeai.azureopenai.service.ChatService;
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
    public Map<String, Object> chatWithSystemPrompt(@RequestBody Map<String, String> request) {
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有帮助的助手。");
        String message = request.get("message");
        return chatService.chatWithSystemPrompt(systemPrompt, message);
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
package com.github.partmeai.azureopenai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiAzureOpenAiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiAzureOpenAiApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 文本嵌入 | POST | `/api/embed` | 生成文本嵌入向量 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Azure OpenAI"}'
```

#### 系统提示对话

```bash
curl -X POST http://localhost:8080/api/chat/system \
  -H "Content-Type: application/json" \
  -d '{
    "systemPrompt": "你是一个专业的技术专家。",
    "message": "什么是 Azure OpenAI？"
  }'
```

#### 文本嵌入

```bash
curl -X POST http://localhost:8080/api/embed \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI 是一个强大的框架"}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=详细解释什么是人工智能"
```

## 八、部署方式

### 8.1 本地运行

```bash
export AZURE_OPENAI_API_KEY=your-api-key
export AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com
cd spring-ai-azure-openai
mvn spring-boot:run
```

### 8.2 部署到 Azure App Service

```bash
mvn clean package -DskipTests
az webapp deploy --name your-app-name --resource-group your-rg --target-path target/spring-ai-azure-openai-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class AzureOpenAIClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat",
            json={"message": message}
        )
        return response.json()
    
    def chat_with_system(self, system_prompt, message):
        response = requests.post(
            f"{self.base_url}/api/chat/system",
            json={"systemPrompt": system_prompt, "message": message}
        )
        return response.json()
    
    def embed(self, text):
        response = requests.post(
            f"{self.base_url}/api/embed",
            json={"text": text}
        )
        return response.json()

client = AzureOpenAIClient()

result = client.chat("请介绍一下 Azure OpenAI")
print(f"Response: {result['response']}")
```

### 9.2 最佳实践

1. **模型部署选择**：
   - 通用任务：gpt-35-turbo
   - 复杂任务：gpt-4
   - 成本优化：gpt-35-turbo-instruct

2. **安全配置**：
   - 使用 Azure Active Directory 认证
   - 配置 VNet 和私有端点
   - 启用内容安全过滤

3. **监控和运维**：
   - 配置 Azure Monitor 告警
   - 跟踪使用量和成本
   - 设置合理的配额限制

### 9.3 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 通用对话 | GPT-3.5 Turbo |
| 复杂推理 | GPT-4 Turbo |
| 企业应用 | GPT-4 |
| 成本敏感 | GPT-3.5 Turbo |

## 十、运行项目

### 10.1 启动应用

```bash
export AZURE_OPENAI_API_KEY=your-api-key
export AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com
cd spring-ai-azure-openai
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 认证和权限问题

**Q: 提示认证失败怎么办？**

- 确认 API 密钥正确且有效
- 检查端点 URL 配置正确
- 确认资源和部署存在
- 检查 Azure 订阅状态和配额

**Q: 权限不足怎么办？**

- 确认 IAM 角色有适当的权限
- 检查资源的访问策略
- 确认使用正确的 Azure AD 租户

### 11.2 部署和配额问题

**Q: 提示部署不存在怎么办？**

- 确认在 Azure OpenAI Studio 中已部署模型
- 检查部署名称拼写正确
- 确认部署在正确的区域

**Q: 配额不足怎么办？**

- 在 Azure 门户中申请增加配额
- 考虑使用更高效的模型
- 实现请求批处理和缓存

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Azure OpenAI 官方文档：https://learn.microsoft.com/en-us/azure/cognitive-services/openai/
- Spring AI Azure OpenAI：https://docs.spring.io/spring-ai/reference/api/chat/azure-openai-chat.html
- Azure OpenAI Studio：https://oai.azure.com/
- 示例模块：spring-ai-azure-openai

## 十四、致谢

感谢 Microsoft Azure 团队和 Spring AI 团队提供的优秀工具，让构建企业级 AI 应用变得如此简单易用。
