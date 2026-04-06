# 9、Spring AI 入门实践：Spring AI 向量数据库（Vector Databases）

## 一、项目概述

向量数据库（Vector Databases）是专门用于存储和检索高维向量数据的数据库系统。在 AI 应用中，向量数据库通常用于存储文本、图片等数据的嵌入（embeddings），并支持基于相似度的快速检索。Spring AI 提供了对多种向量数据库的统一支持，使得开发者可以轻松切换不同的向量数据库。

### 核心功能

- **统一接口**：VectorStore 统一抽象，支持多种向量数据库
- **文档存储**：支持文档的添加、删除和更新
- **相似度搜索**：基于余弦相似度等度量的语义搜索
- **元数据过滤**：支持基于元数据的结果过滤
- **批量操作**：支持批量文档导入和处理
- **多种实现**：Chroma、Milvus、Redis、PGvector 等多种选择

### 适用场景

- 语义搜索系统
- 推荐系统
- 问答系统
- 异常检测
- RAG（检索增强生成）应用
- 文档相似度匹配

## 二、向量数据库简介

向量数据库是一种专门用于存储、索引和查询高维向量数据的数据库系统。与传统数据库不同，向量数据库使用向量相似度（如余弦相似度、欧几里得距离等）来查找最相似的数据。

### 为什么需要向量数据库？

| 功能 | 说明 |
|------|------|
| 语义搜索 | 基于语义而非关键词进行搜索 |
| 推荐系统 | 根据用户偏好推荐相似内容 |
| 问答系统 | 从知识库中检索相关信息 |
| 异常检测 | 识别与正常模式不同的数据 |

### 常见的向量数据库

| 向量库 | 特点 | 部署方式 |
|--------|------|---------|
| Chroma | 开源、易用 | 本地或 Docker |
| Milvus | 高性能、可扩展 | 独立部署 |
| Pinecone | 托管服务 | 云服务 |
| Qdrant | 高性能、开源 | 本地或云 |
| Redis | 内存数据库、速度快 | 本地或云 |
| Weaviate | 语义搜索引擎 | 本地或云 |
| PGvector | PostgreSQL 扩展 | 数据库扩展 |
| Neo4j | 图数据库 + 向量搜索 | 独立部署 |

### 核心概念

| 概念 | 说明 |
|------|------|
| EmbeddingModel | 文本 → 向量的统一抽象 |
| VectorStore | 向量存储的统一接口 |
| SearchRequest | 检索请求封装，支持 topK、相似度阈值 |
| Document | 包含文本、元数据和 ID 的文档对象 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（用于嵌入模型）
- 向量数据库（根据选择准备）

### 3.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取嵌入模型
ollama pull nomic-embed-text
```

### 3.3 向量数据库选择

本仓库提供多种向量数据库的示例模块：

| 向量库 | 示例模块 |
|--------|----------|
| Chroma | spring-ai-ollama-rag-chroma |
| Milvus | spring-ai-ollama-rag-milvus |
| Neo4j | spring-ai-ollama-rag-neo4j |
| PGvector | spring-ai-ollama-rag-pgvector |
| Pinecone | spring-ai-ollama-rag-pinecone |
| Qdrant | spring-ai-ollama-rag-qdrant |
| Redis | spring-ai-ollama-rag-redis |
| Weaviate | spring-ai-ollama-rag-weaviate |
| Elasticsearch | spring-ai-ollama-rag-elasticsearch |
| OpenSearch | spring-ai-ollama-rag-opensearch |
| MongoDB | spring-ai-ollama-rag-mongodb |
| Cassandra | spring-ai-ollama-rag-cassandra |

## 四、项目结构

### 4.1 标准项目结构（以 Redis 为例）

```
spring-ai-ollama-rag-redis/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── VectorController.java
│   │   │                   └── service/
│   │   │                       └── VectorService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| VectorService | 向量数据库操作服务 |
| VectorController | REST API 控制器 |

## 五、核心配置

### 5.1 Maven 依赖（以 Redis 为例）

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

### 5.2 应用配置（以 Redis 为例）

```yaml
spring:
  application:
    name: spring-ai-ollama-rag-redis
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: llama3.1:8b
      embedding:
        enabled: true
        options:
          model: nomic-embed-text
  
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 向量服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorService {
    
    private final VectorStore vectorStore;
    
    public VectorService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
    
    public void addDocument(String id, String text, Map<String, Object> metadata) {
        Document document = new Document(id, text, metadata);
        vectorStore.add(List.of(document));
    }
    
    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
    }
    
    public List<Map<String, Object>> searchSimilar(String query, int topK, double similarityThreshold) {
        SearchRequest request = SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(similarityThreshold);
        
        return vectorStore.similaritySearch(request).stream()
                .map(doc -> Map.<String, Object>of(
                    "id", doc.getId(),
                    "text", doc.getText(),
                    "metadata", doc.getMetadata(),
                    "score", doc.getScore()
                ))
                .collect(Collectors.toList());
    }
    
    public void deleteDocument(String id) {
        vectorStore.delete(List.of(id));
    }
    
    public void deleteDocuments(List<String> ids) {
        vectorStore.delete(ids);
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.VectorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vector")
public class VectorController {
    
    private final VectorService vectorService;
    
    public VectorController(VectorService vectorService) {
        this.vectorService = vectorService;
    }
    
    @PostMapping("/documents")
    public Map<String, String> addDocument(@RequestBody Map<String, Object> request) {
        String id = (String) request.get("id");
        String text = (String) request.get("text");
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());
        
        vectorService.addDocument(id, text, metadata);
        
        return Map.of(
            "status", "success",
            "message", "Document added successfully"
        );
    }
    
    @PostMapping("/search")
    public List<Map<String, Object>> searchSimilar(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        int topK = (int) request.getOrDefault("topK", 5);
        double similarityThreshold = (double) request.getOrDefault("similarityThreshold", 0.7);
        
        return vectorService.searchSimilar(query, topK, similarityThreshold);
    }
    
    @DeleteMapping("/documents/{id}")
    public Map<String, String> deleteDocument(@PathVariable String id) {
        vectorService.deleteDocument(id);
        return Map.of(
            "status", "success",
            "message", "Document deleted successfully"
        );
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 添加文档 | POST | `/api/vector/documents` | 添加单个文档 |
| 相似度搜索 | POST | `/api/vector/search` | 搜索相似文档 |
| 删除文档 | DELETE | `/api/vector/documents/{id}` | 删除指定文档 |

### 7.2 接口使用示例

#### 添加文档

```bash
curl -X POST http://localhost:8080/api/vector/documents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "text": "Spring AI 是一个强大的 AI 框架",
    "metadata": {"category": "tech"}
  }'
```

响应：
```json
{
  "status": "success",
  "message": "Document added successfully"
}
```

#### 相似度搜索

```bash
curl -X POST http://localhost:8080/api/vector/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "AI 框架",
    "topK": 3,
    "similarityThreshold": 0.7
  }'
```

响应：
```json
[
  {
    "id": "1",
    "text": "Spring AI 是一个强大的 AI 框架",
    "metadata": {"category": "tech"},
    "score": 0.95
  }
]
```

#### 删除文档

```bash
curl -X DELETE http://localhost:8080/api/vector/documents/1
```

## 八、部署方式

### 8.1 本地运行（以 Redis 为例）

```bash
# 启动 Redis（使用 Docker）
docker run -d -p 6379:6379 redis/redis-stack:latest

# 启动应用
cd spring-ai-ollama-rag-redis
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-rag-redis-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class VectorDBClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def add_document(self, doc_id, text, metadata=None):
        data = {
            "id": doc_id,
            "text": text,
            "metadata": metadata or {}
        }
        response = requests.post(
            f"{self.base_url}/api/vector/documents",
            json=data
        )
        return response.json()
    
    def search(self, query, top_k=5, threshold=0.7):
        data = {
            "query": query,
            "topK": top_k,
            "similarityThreshold": threshold
        }
        response = requests.post(
            f"{self.base_url}/api/vector/search",
            json=data
        )
        return response.json()
    
    def delete_document(self, doc_id):
        response = requests.delete(
            f"{self.base_url}/api/vector/documents/{doc_id}"
        )
        return response.json()

client = VectorDBClient()

# 添加文档
client.add_document("1", "Spring AI 提供向量数据库支持", {"category": "tech"})

# 搜索
results = client.search("向量数据库", top_k=3)
for result in results:
    print(f"ID: {result['id']}, Score: {result['score']}")
    print(f"Text: {result['text']}\n")
```

### 9.2 配置要点

1. **职责划分**：检索 topK 参数应写在 SearchRequest 中，不要放在 OllamaEmbeddingOptions 上
2. **配置参考**：各子模块的 application.properties / application.yml 包含该向量库的连接和索引配置
3. **向量库选择**：根据项目需求选择合适的向量数据库

### 9.3 向量库选择指南

| 场景 | 推荐向量库 |
|------|-----------|
| 快速原型开发 | Chroma |
| 高性能生产环境 | Milvus / Qdrant |
| 已有 Redis 基础设施 | Redis Vector Search |
| 已有 PostgreSQL | PGvector |
| 云托管服务 | Pinecone |

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama
curl http://localhost:11434/api/tags

# 检查向量数据库（以 Redis 为例）
redis-cli ping
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-rag-redis
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -X POST http://localhost:8080/api/vector/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"test","text":"Hello world"}'
```

## 十一、常见问题

### 11.1 配置问题

**Q: 检索参数应该放在哪里？**

向量检索的 Top-K、相似度阈值等应使用 SearchRequest（见 VectorStore#similaritySearch(SearchRequest)），不要放在 OllamaEmbeddingOptions 上（嵌入选项与检索参数职责不同）。

**Q: 不同向量数据库的配置有什么差异？**

不同向量数据库的 Starter 配置有所差异，请查看具体模块目录下的 README.md，了解该模块使用的 Starter 名称、连接配置键名及官方文档。

### 11.2 性能问题

**Q: 如何提升搜索性能？**

- 选择合适的向量数据库
- 调整索引参数
- 使用批量操作
- 考虑使用缓存
- 优化 topK 参数

**Q: 文档批量导入很慢怎么办？**

- 使用批量添加方法
- 适当增加批量大小
- 考虑使用异步处理
- 检查向量数据库配置

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Vector Databases：https://docs.spring.io/spring-ai/reference/api/vectordbs.html
- Spring AI Embeddings：https://docs.spring.io/spring-ai/reference/api/embeddings.html
- Spring AI ChatClient — RAG：https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation
- 示例模块：spring-ai-ollama-rag-chroma、spring-ai-ollama-rag-milvus、spring-ai-ollama-rag-redis 等

## 十四、致谢

感谢 Spring AI 团队和各向量数据库提供商提供的优秀工具，让向量数据库集成变得如此简单易用。
