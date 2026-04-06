# Spring AI 入门实践：Spring AI 与 Amazon Bedrock 集成

> 基于 Spring AI 框架实现与 Amazon Bedrock 的集成，提供多模型支持（Claude、Llama、Titan等）、文本生成、嵌入计算、工具调用等功能，支持企业级 AI 应用开发。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Amazon Bedrock 的示例，展示了如何在 Java/Spring Boot 应用中使用 AWS Bedrock 提供的各种高性能基础模型。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Amazon Bedrock | - | AWS 托管 AI 服务 |
| AWS SDK | 2.x | AWS 集成 SDK |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-bedrock
**本地路径**：`spring-ai-bedrock/`

### 1.4 核心功能

- **多模型支持**：Anthropic Claude、Cohere Command、Amazon Titan、Meta Llama 等多种模型
- **文本生成**：强大的对话和内容生成能力
- **文本嵌入**：高质量的文本向量化支持
- **工具调用**：支持函数调用和工具集成
- **安全托管**：AWS 完全托管，企业级安全保障
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 企业级 AI 应用开发
- 内容生成和创意写作
- 知识库问答系统
- 代码助手和编程辅助
- 多语言翻译和处理
- 数据分析和洞察

## 二、Amazon Bedrock 简介

Amazon Bedrock 提供了一个统一的 API 来访问来自多个提供商的最新基础模型，让开发者可以灵活选择最适合其用例的模型。

### 2.1 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Amazon Bedrock 官方文档](https://docs.aws.amazon.com/bedrock/) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

### 2.2 可用模型对比

| 模型提供商 | 模型名称 | 特点 | 适用场景 |
|-----------|---------|------|---------|
| Anthropic | Claude 3 Sonnet | 平衡性能与速度 | 通用对话、复杂推理 |
| Anthropic | Claude 3 Haiku | 快速响应 | 实时应用、高效处理 |
| Anthropic | Claude 3 Opus | 最高质量 | 复杂任务、精确输出 |
| Amazon | Titan Text G1 | AWS 原生模型 | 企业应用、成本优化 |
| Amazon | Titan Embeddings | 高质量嵌入 | RAG、语义搜索 |
| Meta | Llama 3 | 开源模型 | 定制化、研究 |
| Cohere | Command | 指令遵循能力强 | 任务执行、工具使用 |

### 核心概念

| 概念 | 说明 |
|------|------|
| Foundation Model (FM) | 基础模型，Bedrock 提供的核心 AI 能力 |
| Inference Profile | 推理配置文件，管理模型访问 |
| Model Access | 模型访问权限管理 |
| Guardrails | 安全防护，控制内容输出 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- AWS 账号（已开通 Bedrock 服务）

### 3.2 AWS 账号配置

1. **登录 AWS 控制台**：访问 https://console.aws.amazon.com/
2. **开通 Bedrock 服务**：在 AWS 控制台中找到 Bedrock 服务并开通
3. **配置模型访问**：在 Bedrock 控制台中申请需要使用的模型访问权限
4. **创建 IAM 用户**：
   - 创建具有 `AmazonBedrockFullAccess` 权限的 IAM 用户
   - 生成 Access Key 和 Secret Key

### 3.3 AWS 凭证配置

可以通过以下方式之一配置 AWS 凭证：

**方式一：环境变量**
```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1
```

**方式二：AWS 配置文件**
```bash
# ~/.aws/credentials
[default]
aws_access_key_id = your-access-key
aws_secret_access_key = your-secret-key

# ~/.aws/config
[default]
region = us-east-1
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-amazon-bedrock/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── bedrock/
│   │   │                   ├── SpringAiAmazonBedrockApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── ChatController.java
│   │   │                   │   └── EmbeddingController.java
│   │   │                   ├── service/
│   │   │                   │   ├── ChatService.java
│   │   │                   │   └── RagService.java
│   │   │                   └── tool/
│   │   │                       └── WeatherTool.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| ChatService | 对话业务逻辑 |
| ChatController | 对话 REST API |
| EmbeddingController | 嵌入 REST API |
| RagService | RAG 业务逻辑 |
| WeatherTool | 天气查询工具 |

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
        <artifactId>spring-ai-starter-model-bedrock-converse</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-bedrock</artifactId>
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
    name: spring-ai-amazon-bedrock
  
  ai:
    bedrock:
      aws:
        region: us-east-1
        credentials:
          access-key: ${AWS_ACCESS_KEY_ID:}
          secret-key: ${AWS_SECRET_ACCESS_KEY:}
      chat:
        enabled: true
        model: anthropic.claude-3-sonnet-20240229-v1:0
        options:
          temperature: 0.7
          max-tokens: 2048
      embedding:
        enabled: true
        model: amazon.titan-embed-text-v1

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 天气查询工具

```java
package com.github.partmeai.bedrock.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherTool {
    
    private static final Map<String, String> WEATHER_DATA = new HashMap<>();
    
    static {
        WEATHER_DATA.put("北京", "{\"city\":\"北京\",\"temperature\":25,\"condition\":\"晴天\",\"humidity\":45}");
        WEATHER_DATA.put("上海", "{\"city\":\"上海\",\"temperature\":22,\"condition\":\"多云\",\"humidity\":60}");
        WEATHER_DATA.put("广州", "{\"city\":\"广州\",\"temperature\":28,\"condition\":\"小雨\",\"humidity\":75}");
        WEATHER_DATA.put("深圳", "{\"city\":\"深圳\",\"temperature\":30,\"condition\":\"晴天\",\"humidity\":70}");
    }
    
    @Tool(name = "get_weather", description = "获取指定城市的天气信息")
    public String getWeather(
            @ToolParam(description = "城市名称，如 '北京'、'上海'") 
            String city) {
        return WEATHER_DATA.getOrDefault(city, 
                String.format("{\"city\":\"%s\",\"error\":\"暂无天气数据\"}", city));
    }
}
```

### 6.2 对话服务

```java
package com.github.partmeai.bedrock.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient.Builder chatClientBuilder,
                       WeatherTool weatherTool) {
        ToolCallbackProvider tools = ToolCallbacks.from(weatherTool);
        this.chatClient = chatClientBuilder
                .defaultTools(tools)
                .build();
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
}
```

### 6.3 RAG 服务

```java
package com.github.partmeai.bedrock.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {
    
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    
    public RagService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }
    
    public void addDocuments(List<Map<String, String>> docs) {
        List<Document> documents = docs.stream()
                .map(doc -> new Document(
                        doc.get("content"),
                        Map.of("source", doc.getOrDefault("source", "unknown"))
                ))
                .toList();
        
        vectorStore.add(documents);
    }
    
    public Map<String, Object> queryWithRag(String userQuery) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(userQuery).withTopK(3)
        );
        
        String context = relevantDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));
        
        String systemPrompt = String.format("""
                你是一个智能助手。请根据以下上下文信息回答用户的问题。
                如果上下文中没有相关信息，请说明你无法从提供的上下文中找到答案。
                
                上下文信息：
                %s
                """, context);
        
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(userQuery)
                .call()
                .content();
        
        return Map.of(
                "query", userQuery,
                "context", context,
                "response", response,
                "sourceDocuments", relevantDocs.stream()
                        .map(doc -> Map.of(
                                "content", doc.getContent(),
                                "metadata", doc.getMetadata()
                        ))
                        .toList()
        );
    }
}
```

### 6.4 REST 控制器

```java
package com.github.partmeai.bedrock.controller;

import com.github.partmeai.bedrock.service.ChatService;
import com.github.partmeai.bedrock.service.RagService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    
    private final ChatService chatService;
    private final RagService ragService;
    private final EmbeddingModel embeddingModel;
    
    public ChatController(ChatService chatService,
                          RagService ragService,
                          EmbeddingModel embeddingModel) {
        this.chatService = chatService;
        this.ragService = ragService;
        this.embeddingModel = embeddingModel;
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
        float[] embedding = embeddingModel.embed(text);
        
        return Map.of(
                "text", text,
                "dimension", embedding.length,
                "embedding", embedding
        );
    }
    
    @PostMapping("/rag/documents")
    public Map<String, String> addDocuments(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> docs = (List<Map<String, String>>) request.get("documents");
        ragService.addDocuments(docs);
        
        return Map.of("status", "success", "message", "Documents added");
    }
    
    @PostMapping("/rag/query")
    public Map<String, Object> queryRag(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        return ragService.queryWithRag(query);
    }
}
```

### 6.5 主应用类和配置

```java
package com.github.partmeai.bedrock;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.InMemoryVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAiAmazonBedrockApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiAmazonBedrockApplication.class, args);
    }
    
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new InMemoryVectorStore(embeddingModel);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 普通对话 | POST | `/api/chat` | 简单的文本对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 文本嵌入 | POST | `/api/embed` | 生成文本嵌入向量 |
| 添加文档 | POST | `/api/rag/documents` | 添加文档到 RAG 知识库 |
| RAG 查询 | POST | `/api/rag/query` | 使用 RAG 进行查询 |

### 7.2 接口使用示例

#### 普通对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Amazon Bedrock"}'
```

响应：
```json
{
  "message": "请介绍一下 Amazon Bedrock",
  "response": "Amazon Bedrock 是 AWS 提供的一项完全托管的服务..."
}
```

#### 系统提示对话

```bash
curl -X POST http://localhost:8080/api/chat/system \
  -H "Content-Type: application/json" \
  -d '{
    "systemPrompt": "你是一个专业的技术专家，擅长用简洁的语言解释技术概念。",
    "message": "什么是 RAG？"
  }'
```

#### 文本嵌入

```bash
curl -X POST http://localhost:8080/api/embed \
  -H "Content-Type: application/json" \
  -d '{"text": "Spring AI 是一个强大的 AI 集成框架"}'
```

#### 添加 RAG 文档

```bash
curl -X POST http://localhost:8080/api/rag/documents \
  -H "Content-Type: application/json" \
  -d '{
    "documents": [
      {
        "content": "Spring AI 是一个用于构建 AI 应用的 Spring 框架集成项目，支持多种大语言模型。",
        "source": "spring-ai-docs"
      },
      {
        "content": "Amazon Bedrock 提供了来自多个 AI 提供商的基础模型，包括 Anthropic、Cohere、Amazon 等。",
        "source": "aws-docs"
      }
    ]
  }'
```

#### RAG 查询

```bash
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"query": "Spring AI 支持哪些模型提供商？"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
# 配置 AWS 凭证
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1

# 启动应用
cd spring-ai-amazon-bedrock
mvn spring-boot:run
```

### 8.2 AWS 部署

可以将应用部署到 AWS EC2、ECS 或 Lambda：

```bash
# 打包
mvn clean package -DskipTests

# 使用 AWS CLI 部署到 EC2（示例）
scp target/spring-ai-amazon-bedrock-1.0.0-SNAPSHOT.jar ec2-user@your-instance:/home/ec2-user/
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import json

class BedrockClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat",
            json={"message": message}
        )
        return response.json()
    
    def chat_with_system_prompt(self, system_prompt, message):
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
    
    def add_documents(self, documents):
        response = requests.post(
            f"{self.base_url}/api/rag/documents",
            json={"documents": documents}
        )
        return response.json()
    
    def query_rag(self, query):
        response = requests.post(
            f"{self.base_url}/api/rag/query",
            json={"query": query}
        )
        return response.json()

client = BedrockClient()

# 普通对话
result = client.chat("请介绍一下 Amazon Bedrock")
print(f"Response: {result['response']}")

# 系统提示对话
result = client.chat_with_system_prompt(
    "你是一个诗人，请用诗的语言回答。",
    "春天是什么样的？"
)
print(f"\nPoetic response: {result['response']}")

# 文本嵌入
result = client.embed("Hello World")
print(f"\nEmbedding dimension: {result['dimension']}")

# RAG 示例
documents = [
    {
        "content": "Amazon Bedrock 提供 Anthropic Claude、Cohere Command、Amazon Titan 等模型。",
        "source": "doc1"
    },
    {
        "content": "Claude 3 Sonnet 在性能和速度之间取得了很好的平衡。",
        "source": "doc2"
    }
]
client.add_documents(documents)

result = client.query_rag("Bedrock 提供哪些模型？")
print(f"\nRAG response: {result['response']}")
```

### 9.2 最佳实践

1. **模型选择**：
   - 通用对话：Claude 3 Sonnet
   - 快速响应：Claude 3 Haiku
   - 复杂任务：Claude 3 Opus
   - 成本优化：Amazon Titan

2. **参数调优**：
   - 创造性任务：temperature 0.7-1.0
   - 确定性任务：temperature 0.0-0.3
   - 长文本生成：增加 max-tokens

3. **安全考虑**：
   - 使用 IAM 角色而非长期凭证
   - 启用 Bedrock Guardrails
   - 加密传输和存储
   - 监控使用情况和成本

4. **成本控制**：
   - 选择合适的模型大小
   - 合理设置 max-tokens
   - 监控使用量和成本
   - 考虑使用缓存

### 9.3 模型推荐表

| 场景 | 推荐模型 | 理由 |
|------|----------|------|
| 企业应用 | Claude 3 Sonnet | 平衡性能与成本 |
| 实时应用 | Claude 3 Haiku | 响应速度快 |
| 高质量输出 | Claude 3 Opus | 最强大的模型 |
| 成本敏感 | Amazon Titan | AWS 原生，成本低 |
| RAG 嵌入 | Titan Embeddings | 高质量嵌入 |

## 十、运行项目

### 10.1 前置检查

```bash
# 确认 AWS 凭证已配置
aws sts get-caller-identity

# 确认 Bedrock 模型访问权限
aws bedrock list-foundation-models --region us-east-1
```

### 10.2 启动应用

```bash
cd spring-ai-amazon-bedrock
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 凭证和权限问题

**Q: 提示 Access Denied 错误怎么办？**

- 确认 IAM 用户具有 `AmazonBedrockFullAccess` 权限
- 确认凭证配置正确（环境变量或配置文件）
- 确认在 Bedrock 控制台中已申请模型访问权限
- 检查使用的区域是否支持所选模型

**Q: 模型访问被拒绝怎么办？**

- 在 Bedrock 控制台申请模型访问权限
- 确认模型 ID 拼写正确
- 确认使用的区域支持该模型

### 11.2 性能和成本问题

**Q: 响应速度太慢怎么办？**

- 使用更小更快的模型（如 Claude 3 Haiku）
- 减少 max-tokens 参数
- 考虑使用就近的 AWS 区域
- 实现请求缓存

**Q: 如何控制成本？**

- 选择合适的模型大小
- 合理设置 max-tokens
- 使用请求缓存
- 监控使用量和设置预算告警
- 考虑使用 Amazon Titan 降低成本

### 11.3 功能问题

**Q: 工具调用不工作怎么办？**

- 确认使用支持工具调用的模型（Claude 3 系列）
- 检查工具定义是否正确
- 优化系统提示词，明确说明使用工具
- 查看详细的错误日志

**Q: 嵌入质量不高怎么办？**

- 使用专门的嵌入模型（Titan Embeddings）
- 对输入文本进行预处理
- 考虑调整 chunking 策略
- 测试不同的相似度阈值

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Amazon Bedrock 官方文档：https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html
- Spring AI Amazon Bedrock：https://docs.spring.io/spring-ai/reference/api/chat/bedrock-chat.html
- AWS SDK for Java：https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html
- 示例模块：spring-ai-amazon-bedrock

---

## 十四、Java 客户端示例

### 14.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class BedrockClient {

    private static final String BASE_URL = "http://localhost:8080/api/bedrock";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message, String model) {
        String url = BASE_URL + "/chat?message={message}&model={model}";
        return restTemplate.getForObject(url, String.class, message, model);
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

- **感谢 AWS 团队** 提供强大的企业级 AI 服务平台
- **感谢 Spring AI 团队** 提供 AWS Bedrock 集成框架
- **感谢开源社区** 提供丰富的技术资源
