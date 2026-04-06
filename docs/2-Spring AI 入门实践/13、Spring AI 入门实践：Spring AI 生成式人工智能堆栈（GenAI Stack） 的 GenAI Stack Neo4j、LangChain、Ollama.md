# 13、Spring AI 入门实践：Spring AI 生成式人工智能堆栈（GenAI Stack）集成

## 一、项目概述

GenAI Stack 是一个集成了多种 AI 技术的完整堆栈，包括向量数据库、语言模型、开发框架等组件，用于构建端到端的生成式 AI 应用。本文将介绍如何使用 Spring AI 集成 GenAI Stack 的核心组件：Neo4j 图数据库、Ollama 本地模型运行时，构建强大的 RAG（检索增强生成）应用。

### 核心功能

- **向量存储**：使用 Neo4j 作为向量数据库，支持知识图谱和向量搜索
- **本地模型**：使用 Ollama 本地运行大语言模型和嵌入模型
- **RAG 应用**：完整的检索增强生成实现
- **知识图谱**：结合图数据库的关系查询能力
- **统一集成**：Spring AI 提供的统一抽象接口

### 适用场景

- 企业知识库问答系统
- 知识图谱驱动的智能搜索
- 复杂关系数据的智能查询
- 隐私敏感的本地 AI 应用
- 端到端的 AI 应用开发

## 二、GenAI Stack 技术组件简介

GenAI Stack 集成了多个优秀的开源项目，为生成式 AI 应用提供完整的技术栈。

### 核心组件对比

| 组件 | 职责 | 特点 |
|------|------|------|
| Neo4j | 图数据库与向量存储 | 支持知识图谱、向量搜索、关系查询 |
| Ollama | 本地模型运行时 | 支持多种开源 LLM 和嵌入模型 |
| Spring AI | AI 应用开发框架 | 统一抽象、简化集成、Spring 生态 |

### 技术架构

GenAI Stack 的典型架构：
1. **数据层**：Neo4j 存储文档向量和知识图谱
2. **模型层**：Ollama 提供 LLM 和嵌入能力
3. **应用层**：Spring AI 集成各组件，提供业务逻辑
4. **接口层**：REST API 或 Web 界面提供服务

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Docker（用于运行 Neo4j）
- Ollama（本地模型运行环境）

### 3.2 Neo4j 安装和配置

使用 Docker 启动 Neo4j：

```bash
# 拉取并启动 Neo4j
docker run -d \
  --name neo4j \
  -p 7474:7474 \
  -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/password \
  -e NEO4J_PLUGINS='["apoc", "graph-data-science"]' \
  neo4j:5.15-community
```

访问 Neo4j 浏览器：http://localhost:7474
- 用户名：neo4j
- 密码：password

### 3.3 Ollama 安装和配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取模型
ollama pull llama3.1:8b
ollama pull mxbai-embed-large
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-genai-stack/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── genai/
│   │   │                   ├── SpringAiGenAIStackApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── RagController.java
│   │   │                   ├── service/
│   │   │                   │   └── RagService.java
│   │   │                   └── config/
│   │   │                       └── Neo4jConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| RagService | RAG 业务逻辑 |
| RagController | REST API 控制器 |
| Neo4jConfig | Neo4j 配置 |

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
        <artifactId>spring-ai-starter-model-ollama</artifactId>
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
    name: spring-ai-genai-stack
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: llama3.1:8b
          temperature: 0.7
      embedding:
        enabled: true
        options:
          model: mxbai-embed-large
  
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 Neo4j 配置

```java
package com.github.partmeai.genai.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    
    @Value("${spring.neo4j.uri}")
    private String neo4jUri;
    
    @Value("${spring.neo4j.authentication.username}")
    private String neo4jUsername;
    
    @Value("${spring.neo4j.authentication.password}")
    private String neo4jPassword;
    
    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(
                neo4jUri,
                AuthTokens.basic(neo4jUsername, neo4jPassword)
        );
    }
    
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel, Driver driver) {
        return Neo4jVectorStore.builder(embeddingModel, driver)
                .withLabel("Document")
                .withEmbeddingProperty("embedding")
                .withIndexName("vector-index")
                .initializeSchema(true)
                .build();
    }
}
```

### 6.2 RAG 服务

```java
package com.github.partmeai.genai.service;

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
                        Map.of(
                                "source", doc.getOrDefault("source", "unknown"),
                                "category", doc.getOrDefault("category", "general")
                        )
                ))
                .toList();
        
        vectorStore.add(documents);
    }
    
    public Map<String, Object> query(String question) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(3)
        );
        
        String context = relevantDocs.stream()
                .map(doc -> String.format("- %s (来源: %s)", 
                        doc.getContent(), 
                        doc.getMetadata().get("source")))
                .collect(Collectors.joining("\n"));
        
        String systemPrompt = String.format("""
                你是一个智能助手。请根据以下相关信息回答用户的问题。
                如果上下文中没有相关信息，请说明你无法从提供的上下文中找到答案。
                
                相关信息：
                %s
                """, context);
        
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();
        
        return Map.of(
                "question", question,
                "context", context,
                "answer", response,
                "sourceDocuments", relevantDocs.stream()
                        .map(doc -> Map.of(
                                "content", doc.getContent(),
                                "metadata", doc.getMetadata()
                        ))
                        .toList()
        );
    }
    
    public List<Document> searchSimilar(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(topK)
        );
    }
}
```

### 6.3 REST 控制器

```java
package com.github.partmeai.genai.controller;

import com.github.partmeai.genai.service.RagService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    
    private final RagService ragService;
    
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }
    
    @PostMapping("/documents")
    public Map<String, String> addDocuments(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> docs = (List<Map<String, String>>) request.get("documents");
        ragService.addDocuments(docs);
        
        return Map.of("status", "success", "message", "Documents added");
    }
    
    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        return ragService.query(question);
    }
    
    @GetMapping("/search")
    public List<Document> search(@RequestParam String query,
                                  @RequestParam(defaultValue = "5") int topK) {
        return ragService.searchSimilar(query, topK);
    }
}
```

### 6.4 主应用类

```java
package com.github.partmeai.genai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiGenAIStackApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiGenAIStackApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 添加文档 | POST | `/api/rag/documents` | 添加文档到向量存储 |
| RAG 查询 | POST | `/api/rag/query` | 使用 RAG 进行查询 |
| 相似度搜索 | GET | `/api/rag/search` | 执行相似度搜索 |

### 7.2 接口使用示例

#### 添加文档

```bash
curl -X POST http://localhost:8080/api/rag/documents \
  -H "Content-Type: application/json" \
  -d '{
    "documents": [
      {
        "content": "Spring Boot 是一个快速开发框架，用于构建生产级别的应用。",
        "source": "spring-boot-docs",
        "category": "framework"
      },
      {
        "content": "Spring AI 提供了与各种 AI 模型的集成，包括 OpenAI、Ollama 等。",
        "source": "spring-ai-docs",
        "category": "ai"
      },
      {
        "content": "Neo4j 是一个图形数据库，支持向量搜索和复杂关系查询。",
        "source": "neo4j-docs",
        "category": "database"
      },
      {
        "content": "Ollama 是一个本地运行大语言模型的工具，支持多种开源模型。",
        "source": "ollama-docs",
        "category": "tool"
      }
    ]
  }'
```

#### RAG 查询

```bash
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question": "什么是 Spring AI？"}'
```

响应：
```json
{
  "question": "什么是 Spring AI？",
  "context": "...",
  "answer": "Spring AI 是一个用于 AI 应用开发的框架...",
  "sourceDocuments": [...]
}
```

#### 相似度搜索

```bash
curl "http://localhost:8080/api/rag/search?query=Spring%20框架&topK=3"
```

## 八、部署方式

### 8.1 本地运行

```bash
# 启动 Neo4j
docker start neo4j

# 确保 Ollama 正在运行
ollama serve

# 启动应用
cd spring-ai-genai-stack
mvn spring-boot:run
```

### 8.2 Docker Compose 部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'
services:
  neo4j:
    image: neo4j:5.15-community
    ports:
      - "7474:7474"
      - "7687:7687"
    environment:
      - NEO4J_AUTH=neo4j/password
      - NEO4J_PLUGINS=["apoc", "graph-data-science"]
    volumes:
      - neo4j_data:/data

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_NEO4J_URI=bolt://neo4j:7687
      - SPRING_AI_OLLAMA_BASE_URL=http://host.docker.internal:11434
    depends_on:
      - neo4j

volumes:
  neo4j_data:
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import json

class GenAIStackClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def add_documents(self, documents):
        response = requests.post(
            f"{self.base_url}/api/rag/documents",
            json={"documents": documents}
        )
        return response.json()
    
    def query(self, question):
        response = requests.post(
            f"{self.base_url}/api/rag/query",
            json={"question": question}
        )
        return response.json()
    
    def search(self, query, top_k=5):
        response = requests.get(
            f"{self.base_url}/api/rag/search",
            params={"query": query, "topK": top_k}
        )
        return response.json()

client = GenAIStackClient()

# 添加示例文档
documents = [
    {
        "content": "Spring Boot 是一个快速开发框架，用于构建生产级别的应用。",
        "source": "doc1",
        "category": "framework"
    },
    {
        "content": "Spring AI 提供了与各种 AI 模型的集成，包括 OpenAI、Ollama 等。",
        "source": "doc2",
        "category": "ai"
    },
    {
        "content": "Neo4j 是一个图形数据库，支持向量搜索和复杂关系查询。",
        "source": "doc3",
        "category": "database"
    }
]

print("Adding documents...")
result = client.add_documents(documents)
print(result)

# RAG 查询
print("\nQuerying...")
result = client.query("什么是 Spring AI？")
print(f"Question: {result['question']}")
print(f"Answer: {result['answer']}")

# 相似度搜索
print("\nSearching...")
results = client.search("Spring 框架", top_k=2)
for i, doc in enumerate(results):
    print(f"\nResult {i+1}:")
    print(f"Content: {doc['content']}")
    print(f"Metadata: {doc['metadata']}")
```

### 9.2 最佳实践

1. **文档分块**：对长文档进行合理分块，提高检索精度
2. **元数据管理**：为文档添加丰富的元数据，便于筛选和过滤
3. **索引优化**：根据查询模式优化 Neo4j 索引配置
4. **缓存策略**：对频繁查询结果进行缓存
5. **监控告警**：监控 Neo4j 和 Ollama 的运行状态
6. **备份恢复**：定期备份 Neo4j 数据

### 9.3 组件推荐

| 场景 | 推荐配置 |
|------|----------|
| 小规模应用 | Neo4j Community + Ollama Llama 3.1:8b |
| 中等规模 | Neo4j Enterprise + Ollama Llama 3.1:70b |
| 生产环境 | Neo4j Aura + 托管 LLM 服务 |

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Neo4j 是否运行
docker ps | grep neo4j

# 检查 Ollama 是否运行
curl http://localhost:11434/api/tags
```

### 10.2 启动应用

```bash
cd spring-ai-genai-stack
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Hello"}'
```

## 十一、常见问题

### 11.1 Neo4j 连接问题

**Q: 无法连接到 Neo4j 怎么办？**

- 确认 Docker 容器正在运行：`docker ps`
- 检查端口 7687 是否可访问
- 确认用户名密码配置正确
- 查看 Neo4j 日志：`docker logs neo4j`

**Q: Neo4j 向量索引创建失败怎么办？**

- 确认使用 Neo4j 5.0+ 版本
- 检查 APOC 插件是否正确安装
- 查看初始化日志确认错误原因

### 11.2 RAG 效果问题

**Q: 检索结果不准确怎么办？**

- 调整 topK 参数值
- 优化文档分块策略
- 考虑使用更高质量的嵌入模型
- 调整查询文本，使其更具体

**Q: 生成的答案不相关怎么办？**

- 优化系统提示词
- 增加检索到的文档数量
- 确认检索到的文档确实包含相关信息
- 调整 LLM 的温度参数

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Neo4j Vector Store：https://docs.spring.io/spring-ai/reference/api/vectorstores/neo4j-vector-store.html
- Neo4j 官方文档：https://neo4j.com/docs/
- Ollama 官方文档：https://ollama.ai/docs
- 示例模块：spring-ai-genai-stack

## 十四、致谢

感谢 Neo4j 团队、Ollama 团队和 Spring AI 团队提供的优秀工具，让构建强大的 GenAI Stack 应用变得如此简单易用。
