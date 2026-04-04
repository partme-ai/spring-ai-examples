# 9、Spring AI 入门实践：Spring AI 向量数据库（Vector Databases）

## 概述

向量数据库（Vector Databases）是专门用于存储和检索高维向量数据的数据库系统。在 AI 应用中，向量数据库通常用于存储文本、图片等数据的嵌入（embeddings），并支持基于相似度的快速检索。

## 向量数据库基础

### 什么是向量数据库？

向量数据库是一种专门用于存储、索引和查询高维向量数据的数据库系统。与传统数据库不同，向量数据库使用向量相似度（如余弦相似度、欧几里得距离等）来查找最相似的数据。

### 为什么需要向量数据库？

1. **语义搜索**：基于语义而非关键词进行搜索
2. **推荐系统**：根据用户偏好推荐相似内容
3. **问答系统**：从知识库中检索相关信息
4. **异常检测**：识别与正常模式不同的数据

### 常见的向量数据库

- **Chroma**：开源、易用的向量数据库
- **Milvus**：高性能、可扩展的向量数据库
- **Pinecone**：托管的向量数据库服务
- **Qdrant**：高性能、开源的向量数据库
- **Redis**：支持向量搜索的内存数据库
- **Weaviate**：语义搜索引擎
- **PGvector**：PostgreSQL 的向量扩展
- **Neo4j**：支持向量搜索的图数据库

## Spring AI 向量数据库支持

Spring AI 提供了对多种向量数据库的支持，通过统一的 API 接口，开发者可以轻松切换不同的向量数据库。

### 核心接口

```java
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.document.Document;
import java.util.List;

public interface VectorStore {
    void add(List<Document> documents);
    List<Document> similaritySearch(SearchRequest request);
    void delete(List<String> idList);
    // ... 其他方法
}
```

## 准备工作

### 1. 添加依赖

根据选择的向量数据库，添加相应的依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- 选择一个向量数据库 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-redis-store-spring-boot-starter</artifactId>
    </dependency>
    <!-- 或者 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-chroma-store-spring-boot-starter</artifactId>
    </dependency>
    <!-- 或者其他向量数据库 -->
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 配置 API 密钥

在 `application.properties` 中配置：

```properties
spring.ai.openai.api-key=your-api-key
spring.ai.openai.embedding.options.model=text-embedding-3-small

# Redis 向量数据库配置
spring.ai.vectorstore.redis.uri=redis://localhost:6379
spring.ai.vectorstore.redis.index-name=documents
spring.ai.vectorstore.redis.prefix=doc:
```

## 基本使用

### 1. 添加文档

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void addDocument(String id, String text, Map<String, Object> metadata) {
        Document document = new Document(id, text, metadata);
        vectorStore.add(List.of(document));
    }
    
    public void addDocuments(List<Map<String, Object>> documentData) {
        List<Document> documents = documentData.stream()
            .map(data -> new Document(
                (String) data.get("id"),
                (String) data.get("text"),
                (Map<String, Object>) data.getOrDefault("metadata", Map.of())
            ))
            .collect(Collectors.toList());
        
        vectorStore.add(documents);
    }
}
```

### 2. 相似度搜索

```java
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SearchService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query)
            .withTopK(topK)
            .withSimilarityThreshold(0.7);
        
        return vectorStore.similaritySearch(request);
    }
    
    public List<Document> searchWithFilter(String query, int topK, Map<String, Object> filter) {
        SearchRequest request = SearchRequest.query(query)
            .withTopK(topK)
            .withSimilarityThreshold(0.7)
            .withFilterExpression(createFilterExpression(filter));
        
        return vectorStore.similaritySearch(request);
    }
    
    private String createFilterExpression(Map<String, Object> filter) {
        return filter.entrySet().stream()
            .map(entry -> entry.getKey() + " == '" + entry.getValue() + "'")
            .collect(Collectors.joining(" && "));
    }
}
```

### 3. 删除文档

```java
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentManagementService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void deleteDocument(String id) {
        vectorStore.delete(List.of(id));
    }
    
    public void deleteDocuments(List<String> ids) {
        vectorStore.delete(ids);
    }
}
```

## 高级功能

### 1. 自定义嵌入模型

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;

@Configuration
class VectorStoreConfig {
    
    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
            .withModel("text-embedding-3-small")
            .withDimensions(1536)
            .build();
        
        return new OpenAiEmbeddingModel(options);
    }
}
```

### 2. 批量操作

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
class BatchVectorStoreService {
    
    @Autowired
    private VectorStore vectorStore;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public CompletableFuture<Void> batchAddDocuments(List<Document> documents) {
        return CompletableFuture.runAsync(() -> {
            int batchSize = 100;
            for (int i = 0; i < documents.size(); i += batchSize) {
                int end = Math.min(i + batchSize, documents.size());
                List<Document> batch = documents.subList(i, end);
                vectorStore.add(batch);
            }
        }, executor);
    }
}
```

### 3. 向量存储优化

```java
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.VectorStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
class VectorStoreOptimizationService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void optimizeIndex() {
        if (vectorStore instanceof RedisVectorStore) {
            RedisVectorStore redisStore = (RedisVectorStore) vectorStore;
            redisStore.optimizeIndex();
        }
    }
    
    public void rebuildIndex() {
        if (vectorStore instanceof RedisVectorStore) {
            RedisVectorStore redisStore = (RedisVectorStore) vectorStore;
            redisStore.rebuildIndex();
        }
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class VectorDatabaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(VectorDatabaseApplication.class, args);
    }
}

@Service
class VectorDatabaseService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void addDocument(String id, String text, Map<String, Object> metadata) {
        Document document = new Document(id, text, metadata);
        vectorStore.add(List.of(document));
    }
    
    public List<Map<String, Object>> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query)
            .withTopK(topK)
            .withSimilarityThreshold(0.7);
        
        return vectorStore.similaritySearch(request).stream()
            .map(doc -> Map.of(
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
}

@RestController
@RequestMapping("/api/vector-database")
class VectorDatabaseController {
    
    @Autowired
    private VectorDatabaseService service;
    
    @PostMapping("/documents")
    public Map<String, String> addDocument(@RequestBody Map<String, Object> request) {
        String id = (String) request.get("id");
        String text = (String) request.get("text");
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());
        
        service.addDocument(id, text, metadata);
        
        return Map.of("status", "success", "message", "Document added successfully");
    }
    
    @PostMapping("/search")
    public List<Map<String, Object>> searchSimilar(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        int topK = (int) request.getOrDefault("topK", 5);
        
        return service.searchSimilar(query, topK);
    }
    
    @DeleteMapping("/documents/{id}")
    public Map<String, String> deleteDocument(@PathVariable String id) {
        service.deleteDocument(id);
        return Map.of("status", "success", "message", "Document deleted successfully");
    }
}
```

## 测试方法

1. **启动应用**：运行 `VectorDatabaseApplication` 类
2. **添加文档**：
   ```bash
   curl -X POST http://localhost:8080/api/vector-database/documents \
     -H "Content-Type: application/json" \
     -d '{"id":"1","text":"Spring AI 是一个强大的 AI 框架","metadata":{"category":"tech"}}'
   ```
3. **搜索相似文档**：
   ```bash
   curl -X POST http://localhost:8080/api/vector-database/search \
     -H "Content-Type: application/json" \
     -d '{"query":"AI 框架","topK":3}'
   ```
4. **删除文档**：
   ```bash
   curl -X DELETE http://localhost:8080/api/vector-database/documents/1
   ```

## 总结

Spring AI 提供了对多种向量数据库的统一支持，使得开发者可以轻松构建基于向量搜索的应用。通过向量数据库，可以实现语义搜索、推荐系统、问答系统等功能。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [向量数据库比较](https://www.pinecone.io/learn/vector-database/)
- [Chroma 文档](https://docs.trychroma.com/)
- [Milvus 文档](https://milvus.io/docs/)
- [Pinecone 文档](https://docs.pinecone.io/)
- [Qdrant 文档](https://qdrant.tech/documentation/)
- [Redis Vector Search](https://redis.com/solutions/use-cases/vector-search/)
- [Weaviate 文档](https://weaviate.io/)
- [PGvector 文档](https://github.com/pgvector/pgvector)
- [Neo4j Vector Search](https://neo4j.com/docs/cypher-manual/current/indexes-for-vector-search/)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关向量数据库集成的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) - 向量数据库统一抽象
- [Spring AI Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings.html) - 嵌入模型接口

### 核心概念
- **`EmbeddingModel`**：文本 → 向量的统一抽象，由不同模型提供商实现
- **`VectorStore`**：向量存储的统一接口，支持文档写入和相似度检索
- **`SearchRequest`**：检索请求封装，支持 `topK`、相似度阈值等参数

### 向量库速查表
Spring AI 支持多种向量数据库，本仓库提供相应的示例模块：

| 向量库 | 官方文档 | 本仓库示例模块 |
|--------|----------|------------------|
| **Chroma** | [Getting Started](https://docs.trychroma.com/getting-started) | `spring-ai-ollama-rag-chroma` |
| **Milvus** | [Example Code](https://milvus.io/docs/example_code.md) | `spring-ai-ollama-rag-milvus` |
| **Neo4j** | [Vector indexes](https://neo4j.com/docs/cypher-manual/current/indexes-for-vector-search/) | `spring-ai-ollama-rag-neo4j` |
| **PGvector** | [pgvector GitHub](https://github.com/pgvector/pgvector) | `spring-ai-ollama-rag-pgvector` |
| **Pinecone** | [Quickstart](https://docs.pinecone.io/docs/quickstart) | `spring-ai-ollama-rag-pinecone` |
| **Qdrant** | [Quick Start](https://qdrant.tech/documentation/quick-start/) | `spring-ai-ollama-rag-qdrant` |
| **Redis** | [Redis Stack 向量检索](https://redis.io/docs/latest/develop/interact/search-and-query/) | `spring-ai-ollama-rag-redis` |
| **Weaviate** | [Weaviate Docs](https://weaviate.io/developers/weaviate) | `spring-ai-ollama-rag-weaviate` |
| **Elasticsearch** | [Elastic 向量搜索](https://www.elastic.co/guide/en/elasticsearch/reference/current/dense-vector.html) | `spring-ai-ollama-rag-elasticsearch` |
| **OpenSearch** | [OpenSearch k-NN](https://opensearch.org/docs/latest/vector-search/) | `spring-ai-ollama-rag-opensearch` |
| **MongoDB** | [Atlas Vector Search](https://www.mongodb.com/docs/atlas/atlas-vector-search/vector-search-overview/) | `spring-ai-ollama-rag-mongodb` |
| **Cassandra** | [Vector Search](https://cassandra.apache.org/doc/latest/cassandra/vector-search/overview.html) | `spring-ai-ollama-rag-cassandra` |

### 配置差异
不同向量数据库的 Starter 配置有所差异：

| 模块示例 | Starter 依赖 | 备注 |
|----------|--------------|------|
| `spring-ai-ollama-rag-chroma` | `spring-ai-starter-vector-store-chroma` | 轻量，常本地或 Docker 部署 |
| `spring-ai-ollama-rag-redis` | `spring-ai-starter-vector-store-redis` | 需 Redis Stack（支持向量检索） |
| `spring-ai-ollama-rag-pgvector` | `spring-ai-starter-vector-store-pgvector` | PostgreSQL + pgvector 扩展 |

### 运行与验证
1. **准备环境**：启动 Ollama 和目标向量数据库（本地或云实例）
2. **启动应用**：进入对应模块目录执行 `mvn spring-boot:run`
3. **验证接口**：使用各模块提供的 Embedding、Chat 或 RAG 接口进行验证

### 注意事项
1. **职责划分**：检索 `topK` 参数应写在 `SearchRequest` 中，**不要**放在 `OllamaEmbeddingOptions` 上
2. **配置参考**：各子模块的 `application.properties` / `application.yml` 包含该向量库的连接和索引配置
3. **共用说明**：详细职责划分请参考以下 RAG 类模块共用说明

### RAG 类模块共用说明
以下说明适用于本仓库中所有以 **Ollama** 作为 **Chat / Embedding** 后端、以 **不同向量存储** 作为 `VectorStore` 的 RAG 示例模块：

#### 官方文档对应
- [Introduction](https://docs.spring.io/spring-ai/reference/index.html)  
- [Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings.html)  
- [Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)  
- [ChatClient — Retrieval Augmented Generation](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation)  
- [ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)  

#### 共性先决条件
1. **JDK 17+**，父工程已统一 **Spring AI 1.1.x**（见 `spring-ai-examples/pom.xml` 中 `spring-ai.version`）。  
2. 本地安装并启动 [Ollama](https://ollama.com/)，并拉取对话与嵌入所用模型（各模块 `application.yml` 中一般有默认模型名，可按需修改）。  
3. 启动 **对应向量数据库** 或 **云托管向量服务**（各模块 README 中列出端口与镜像说明）。  

#### 共性依赖思路
- `spring-ai-starter-model-ollama`：对话与向量嵌入。  
- `spring-ai-starter-vector-store-{provider}`：与具体向量库对应的 Spring AI Starter（artifact 名因库而异，见各模块 `pom.xml`）。  
- 版本由父 POM BOM 管理，子模块 **勿** 手写 Spring AI 版本号。

#### 共性运行方式
```bash
cd spring-ai-ollama-rag-{provider}
mvn spring-boot:run
```
若模块集成了 Knife4j/SpringDoc，启动后可通过 OpenAPI 页面调试 RAG 相关接口。

#### 检索参数说明（Spring AI 1.1.x）
- 向量检索的 **Top-K**、相似度阈值等应使用 `SearchRequest`（见 `VectorStore#similaritySearch(SearchRequest)`），**不要** 放在 `OllamaEmbeddingOptions` 上（嵌入选项与检索参数职责不同）。

#### 各向量库差异
请打开具体模块目录下的 `README.md`，查看该模块使用的 **Starter 名称**、**连接配置键名**及 **官方 Vector Store 文档小节**（若有）。