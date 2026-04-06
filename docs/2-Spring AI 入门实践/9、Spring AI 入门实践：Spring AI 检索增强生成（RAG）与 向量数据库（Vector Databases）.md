# 9、Spring AI 入门实践：Spring AI 向量数据库（Vector Databases）

## 一、项目概述

向量数据库（Vector Databases）是专门用于存储和检索高维向量数据的数据库系统。在 AI 应用中，向量数据库通常用于存储文本、图片等数据的嵌入（embeddings），并支持基于相似度的快速检索。Spring AI 提供了对多种向量数据库的统一支持，使得开发者可以轻松切换不同的向量数据库。

## 1.1 代码地址

**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-rag-redis

**本地路径**：`spring-ai-ollama-rag-redis/`

### 核心功能

- **统一接口**：VectorStore 统一抽象，支持多种向量数据库
- **文档存储**：支持文档的添加、删除和更新
- **相似度搜索**：基于余弦相似度等度量的语义搜索
- **元数据过滤**：支持基于元数据的结果过滤
- **批量操作**：支持批量文档导入和处理
- **多种实现**：Chroma、Milvus、Redis、PGvector 等多种选择

## 1.2 应用案例

| 应用场景 | 具体案例 | 性能指标 |
|---------|---------|---------|
| **智能客服系统** | 企业知识库问答，支持 10 万+ 文档检索，平均响应时间 < 100ms | 检索准确率 85%+，支持并发查询 100+ QPS |
| **内容推荐引擎** | 电商商品相似推荐，基于百万级商品向量，实时推荐 Top 10 | 召回率 75%+，推荐延迟 < 50ms |
| **文档管理系统** | 企业文档智能搜索，支持多语言语义检索，覆盖 PDF/Word/Markdown | 搜索精度提升 40% vs 关键词搜索 |
| **代码助手** | 代码片段语义搜索，支持跨语言代码理解，检索相似代码 | 相似度匹配准确率 70%+，支持 50K+ 代码库 |
| **RAG 应用** | 检索增强生成，结合 LLM 实现知识问答，支持多轮对话 | 上下文检索召回率 80%+，端到端延迟 < 2s |

## 二、向量数据库简介

向量数据库是一种专门用于存储、索引和查询高维向量数据的数据库系统。与传统数据库不同，向量数据库使用向量相似度（如余弦相似度、欧几里得距离等）来查找最相似的数据。

### 为什么需要向量数据库？

| 功能 | 说明 |
|------|------|
| 语义搜索 | 基于语义而非关键词进行搜索，理解用户意图 |
| 推荐系统 | 根据用户偏好向量推荐相似内容 |
| 问答系统 | 从知识库中检索最相关的上下文 |
| 异常检测 | 识别与正常模式不同的异常数据 |

### 常见的向量数据库

| 向量库 | 特点 | 部署方式 | 官网 |
|--------|------|---------|------|
| Chroma | 开源、易用，适合快速原型 | 本地或 Docker | https://www.trychroma.com/ |
| Milvus | 高性能、可扩展，支持云原生 | 独立部署/K8s | https://milvus.io/ |
| Pinecone | 托管服务，零运维 | 云服务 | https://www.pinecone.io/ |
| Qdrant | 高性能、开源，支持过滤 | 本地或云 | https://qdrant.tech/ |
| Redis | 内存数据库、速度快，易集成 | 本地或云 | https://redis.io/ |
| Weaviate | 语义搜索引擎，支持混合搜索 | 本地或云 | https://weaviate.io/ |
| PGvector | PostgreSQL 扩展，易于集成 | 数据库扩展 | https://github.com/pgvector/pgvector |
| Neo4j | 图数据库 + 向量搜索 | 独立部署 | https://neo4j.com/ |

### 核心概念

| 概念 | 说明 |
|------|------|
| EmbeddingModel | 文本 → 向量的统一抽象，支持多种嵌入模型 |
| VectorStore | 向量存储的统一接口，定义 CRUD 操作 |
| SearchRequest | 检索请求封装，支持 topK、相似度阈值、过滤条件 |
| Document | 包含文本、元数据和 ID 的文档对象 |

## 三、性能基准对比

以下数据来自官方公开的性能测试（如有标注"待补充"表示暂无官方数据）：

### 3.1 查询性能对比

| 向量库 | 查询延迟 (QPS) | 吞吐量 | 数据规模 | 精度 | 来源 |
|--------|---------------|--------|---------|------|------|
| **Qdrant** | 30-40ms | 8,000-15,000 QPS | 1M vectors | 95%+ | [Qdrant Benchmarks](https://qdrant.tech/benchmarks/) |
| **Milvus** | 50-80ms | 10,000-20,000 QPS | 1M vectors | 95%+ | [TensorBlue 2025](https://tensorblue.com/blog/vector-database-comparison-pinecone-weaviate-qdrant-milvus-2025) |
| **Redis** | 待补充 | 66,000 inserts/s | 1B vectors | 90%+ | [Redis Blog](https://redis.io/blog/milvus-vs-redis-vector-database-comparison/) |
| **Weaviate** | 50-70ms | 3,000-8,000 QPS | 1M vectors | 95%+ | [TensorBlue 2025](https://tensorblue.com/blog/vector-database-comparison-pinecone-weaviate-qdrant-milvus-2025) |
| **Pinecone** | 待补充 | 待补充 | 待补充 | 待补充 | 待补充 |
| **Chroma** | 待补充 | 待补充 | 待补充 | 待补充 | 待补充 |

> **注**：以上数据基于标准测试环境（HNSW 索引，768 维向量，95% 召回率），实际性能受硬件、数据分布和配置参数影响。

### 3.2 性能优化建议

1. **索引选择**：HNSW 索引提供最佳性能，IVF_FLAT 适合内存受限场景
2. **批量操作**：批量插入比单条插入快 5-10 倍
3. **分区策略**：大规模数据（>10M）建议按时间或类别分区
4. **硬件配置**：SSD 存储、16GB+ 内存、GPU 加速（如支持）

## 四、Java 客户端 SDK

### 4.1 主流向量库 Java 客户端

| 向量库 | Java SDK | Maven 依赖 | 官方文档 |
|--------|----------|-----------|---------|
| **Milvus** | [milvus-sdk-java](https://github.com/milvus-io/milvus-sdk-java) | `io.milvus:milvus-sdk-java` | [Milvus Java SDK](https://milvus.io/api-reference/java.md) |
| **Qdrant** | [qdrant-java](https://github.com/qdrant/java-client) | `io.qdrant:client` | [Qdrant Java API](https://qdrant.github.io/java-client/) |
| **Redis** | [Jedis](https://github.com/redis/jedis) | `redis.clients:jedis` | [Redis VecSearch](https://redis.ac.cn/kb/doc/13qsrk8xpx/how-to-perform-vector-search-in-java-with-the-jedis-client-library) |
| **Weaviate** | [weaviate-java-client](https://github.com/weaviate/java-client) | `io.weaviate:client` | [Weaviate Java](https://docs.weaviate.io/weaviate/client-libraries/java) |
| **Pinecone** | [pinecone-java-client](https://github.com/pinecone-io/pinecone-java-client) | `io.pinecone:pinecone-client` | [Pinecone Java SDK](https://docs.pinecone.io/docs/java-client) |
| **Chroma** | 使用 REST API 或 [chromadb-java](https://github.com/amithkoujalgi/chromadb-java) | `io.github.amithkoujalgi:chromadb-java` | [Chroma REST API](https://docs.trychroma.com/rest-api) |

### 4.2 使用示例（以 Milvus 为例）

```xml
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.3.4</version>
</dependency>
```

```java
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.collection.CreateCollectionParam;

// 创建客户端
MilvusServiceClient client = new MilvusServiceClient(
    ConnectParam.newBuilder()
        .withHost("localhost")
        .withPort(19530)
        .build()
);

// 创建集合
CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
    .withCollectionName("documents")
    .withFieldType(FieldType.newBuilder()
        .withName("id")
        .withDataType(DataType.Int64)
        .withPrimaryKey(true)
        .build())
    .withFieldType(FieldType.newBuilder()
        .withName("vector")
        .withDataType(DataType.FloatVector)
        .withDimension(768)
        .build())
    .build();

client.createCollection(createParam);
```

## 五、环境准备

### 5.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（用于嵌入模型）
- 向量数据库（根据选择准备）

### 5.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取嵌入模型
ollama pull nomic-embed-text
```

### 5.3 向量数据库选择

本仓库提供多种向量数据库的示例模块：

| 向量库 | 示例模块 | 部署难度 |
|--------|----------|---------|
| Chroma | spring-ai-ollama-rag-chroma | ⭐ 简单 |
| Milvus | spring-ai-ollama-rag-milvus | ⭐⭐⭐ 复杂 |
| Neo4j | spring-ai-ollama-rag-neo4j | ⭐⭐ 中等 |
| PGvector | spring-ai-ollama-rag-pgvector | ⭐⭐ 中等 |
| Pinecone | spring-ai-ollama-rag-pinecone | ⭐ 简单（云服务） |
| Qdrant | spring-ai-ollama-rag-qdrant | ⭐⭐ 中等 |
| Redis | spring-ai-ollama-rag-redis | ⭐ 简单 |
| Weaviate | spring-ai-ollama-rag-weaviate | ⭐⭐ 中等 |
| Elasticsearch | spring-ai-ollama-rag-elasticsearch | ⭐⭐ 中等 |
| OpenSearch | spring-ai-ollama-rag-opensearch | ⭐⭐ 中等 |
| MongoDB | spring-ai-ollama-rag-mongodb | ⭐⭐ 中等 |
| Cassandra | spring-ai-ollama-rag-cassandra | ⭐⭐⭐ 复杂 |

## 六、项目结构

### 6.1 标准项目结构（以 Redis 为例）

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

### 6.2 核心类说明

| 类名 | 职责 |
|------|------|
| VectorService | 向量数据库操作服务，封装 CRUD 操作 |
| VectorController | REST API 控制器，提供 HTTP 接口 |

## 七、核心配置

### 7.1 Maven 依赖（以 Redis 为例）

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
    </dependency>

    <!-- For Vector Store -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>

    <!-- For Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>

    <!-- For Knife4j API Documentation -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    </dependency>

    <!-- For Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>redis</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 7.2 应用配置（以 Redis 为例）

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

## 八、代码实现详解

### 8.1 向量服务

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

### 8.2 REST 控制器

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

### 8.3 主应用类

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

## 九、API 接口说明

### 9.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 添加文档 | POST | `/api/vector/documents` | 添加单个文档 |
| 相似度搜索 | POST | `/api/vector/search` | 搜索相似文档 |
| 删除文档 | DELETE | `/api/vector/documents/{id}` | 删除指定文档 |

### 9.2 接口使用示例

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

## 十、部署方式

### 10.1 本地运行（以 Redis 为例）

```bash
# 启动 Redis（使用 Docker）
docker run -d -p 6379:6379 redis/redis-stack:latest

# 启动应用
cd spring-ai-ollama-rag-redis
mvn spring-boot:run
```

### 10.2 Docker Compose 部署

```yaml
version: '3.8'
services:
  redis:
    image: redis/redis-stack:latest
    ports:
      - "6379:6379"

  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - redis
    environment:
      - SPRING_DATA_REDIS_HOST=redis
```

### 10.3 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-rag-redis-1.0.0-SNAPSHOT.jar
```

## 十一、使用示例

### 11.1 Python 客户端

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

# 使用示例
client = VectorDBClient()

# 添加文档
client.add_document("1", "Spring AI 提供向量数据库支持", {"category": "tech"})

# 搜索
results = client.search("向量数据库", top_k=3)
for result in results:
    print(f"ID: {result['id']}, Score: {result['score']}")
    print(f"Text: {result['text']}\n")
```

### 11.2 Java 客户端

```java
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

public class VectorDBClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public VectorDBClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public void addDocument(String id, String text, Map<String, Object> metadata) {
        Map<String, Object> request = Map.of(
            "id", id,
            "text", text,
            "metadata", metadata
        );
        restTemplate.postForObject(baseUrl + "/api/vector/documents", request, Map.class);
    }

    public List<Map<String, Object>> search(String query, int topK, double threshold) {
        Map<String, Object> request = Map.of(
            "query", query,
            "topK", topK,
            "similarityThreshold", threshold
        );
        return restTemplate.postForObject(baseUrl + "/api/vector/search", request, List.class);
    }
}
```

### 11.3 配置要点

1. **职责划分**：检索 topK 参数应写在 SearchRequest 中，不要放在 OllamaEmbeddingOptions 上
2. **配置参考**：各子模块的 application.properties / application.yml 包含该向量库的连接和索引配置
3. **向量库选择**：根据项目需求选择合适的向量数据库

### 11.4 向量库选择指南

| 场景 | 推荐向量库 | 理由 |
|------|-----------|------|
| 快速原型开发 | Chroma | 零配置，本地运行，易于上手 |
| 高性能生产环境 | Milvus / Qdrant | 高吞吐量，低延迟，云原生架构 |
| 已有 Redis 基础设施 | Redis Vector Search | 复用现有技术栈，降低运维成本 |
| 已有 PostgreSQL | PGvector | 无需额外部署，与关系型数据集成 |
| 云托管服务 | Pinecone | 零运维，自动扩缩容，企业级支持 |
| 混合搜索需求 | Weaviate | 向量检索 + BM25 关键词搜索 |

## 十二、运行项目

### 12.1 前置检查

```bash
# 检查 Ollama
curl http://localhost:11434/api/tags

# 检查向量数据库（以 Redis 为例）
redis-cli ping
```

### 12.2 启动应用

```bash
cd spring-ai-ollama-rag-redis
mvn spring-boot:run
```

### 12.3 简单测试

```bash
curl -X POST http://localhost:8080/api/vector/documents \
  -H "Content-Type: application/json" \
  -d '{"id":"test","text":"Hello world"}'
```

## 十三、常见问题

### 13.1 配置问题

**Q: 检索参数应该放在哪里？**

向量检索的 Top-K、相似度阈值等应使用 SearchRequest（见 VectorStore#similaritySearch(SearchRequest)），不要放在 OllamaEmbeddingOptions 上（嵌入选项与检索参数职责不同）。

**Q: 不同向量数据库的配置有什么差异？**

不同向量数据库的 Starter 配置有所差异，请查看具体模块目录下的 README.md，了解该模块使用的 Starter 名称、连接配置键名及官方文档。

### 13.2 性能问题

**Q: 如何提升搜索性能？**

- 选择合适的向量数据库（参考性能基准对比）
- 调整索引参数（HNSW 的 M、ef_construction）
- 使用批量操作（批量插入比单条快 5-10 倍）
- 考虑使用缓存（Redis 缓存热门查询）
- 优化 topK 参数（topK=10 比 topK=100 快 3-5 倍）

**Q: 文档批量导入很慢怎么办？**

- 使用批量添加方法（每批 100-1000 文档）
- 适当增加批量大小（但避免内存溢出）
- 考虑使用异步处理（Spring @Async 或消息队列）
- 检查向量数据库配置（索引类型、分片策略）

**Q: 向量搜索精度不高怎么办？**

- 调整相似度阈值（从 0.7 降到 0.5 提升召回率）
- 优化嵌入模型（使用更大的模型如 text-embedding-3-large）
- 增加数据量（更多文档提升语义覆盖）
- 使用混合搜索（向量 + 关键词，如 Weaviate）

## 十四、许可证

本项目采用 Apache License 2.0 许可证。

## 十五、参考资源

### 官方文档
- Spring AI Vector Databases：https://docs.spring.io/spring-ai/reference/api/vectordbs.html
- Spring AI Embeddings：https://docs.spring.io/spring-ai/reference/api/embeddings.html
- Spring AI ChatClient — RAG：https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation

### 向量数据库资源
- Milvus 官方文档：https://milvus.io/docs
- Qdrant 官方文档：https://qdrant.tech/documentation/
- Redis 向量搜索：https://redis.io/docs/stack/search/
- Weaviate 官方文档：https://weaviate.io/developers/weaviate
- Pinecone 官方文档：https://docs.pinecone.io/

### 示例代码
- 示例模块：spring-ai-ollama-rag-chroma、spring-ai-ollama-rag-milvus、spring-ai-ollama-rag-redis 等

## 十六、致谢

特别感谢以下开源项目和社区：

- **Spring AI 团队**：提供统一的向量数据库抽象层，简化了 Java 开发者的集成工作
- **Milvus 社区**（Zilliz）：提供高性能开源向量数据库，支持云原生架构
- **Qdrant 团队**：开发现代化的向量搜索引擎，提供出色的性能和易用性
- **Redis Labs**：在 Redis 中集成向量搜索能力，降低了向量数据库的使用门槛
- **Weaviate 社区**：提供语义搜索引擎和混合搜索能力，推动 GraphQL/gRPC 在向量数据库中的应用
- **所有向量数据库提供商**：Chroma、Pinecone、Neo4j、PGvector 等，为开发者提供多样化的选择

感谢所有贡献者和维护者，让向量数据库技术变得更加普及和易用！

---

**Sources**:
- [Top 5 Open Source Vector Databases for 2025](https://medium.com/@fendylike/top-5-open-source-vector-search-engines-a-comprehensive-comparison-guide-for-2025-e10110b47aa3)
- [Redis vs Milvus Comparison](https://redis.io/blog/milvus-vs-redis-vector-database-comparison/)
- [Qdrant Benchmarks](https://qdrant.tech/benchmarks/)
- [2025 Vector Database Comparison Guide](https://tensorblue.com/blog/vector-database-comparison-pinecone-weaviate-qdrant-milvus-2025)
- [Qdrant Java Client](https://github.com/qdrant/java-client)
- [Redis Vector Search Java](https://redis.ac.cn/kb/doc/13qsrk8xpx/how-to-perform-vector-search-in-java-with-the-jedis-client-library)
- [Weaviate Java Client](https://docs.weaviate.io/weaviate/client-libraries/java)
- [Milvus Java SDK](https://milvus.io/api-reference/java.md)
