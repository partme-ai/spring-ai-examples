# 5、Spring AI 入门实践：Spring AI 文本嵌入（Embeddings）

## 概述

文本嵌入（Embeddings）是将文本转换为数值向量的技术，这些向量可以表示文本的语义信息。Spring AI 提供了丰富的文本嵌入功能，支持多种嵌入模型和向量存储。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI Embeddings API**（或其他嵌入模型）

## 准备工作

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-redis-store-spring-boot-starter</artifactId>
    </dependency>
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
spring.ai.openai.embedding.options.dimensions=1536

spring.ai.vectorstore.redis.uri=redis://localhost:6379
spring.ai.vectorstore.redis.index-name=documents
spring.ai.vectorstore.redis.prefix=doc:
```

## 基本使用

### 1. 生成文本嵌入

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    public float[] generateEmbedding(String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResults().get(0).getOutput();
    }
    
    public List<float[]> generateEmbeddings(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        return response.getResults().stream()
            .map(result -> result.getOutput())
            .collect(Collectors.toList());
    }
}
```

### 2. 计算相似度

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SimilarityService {
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    public double calculateSimilarity(String text1, String text2) {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);
        
        return cosineSimilarity(embedding1, embedding2);
    }
    
    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

## 高级功能

### 1. 向量存储

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
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
    
    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }
    
    public void deleteDocument(String id) {
        vectorStore.delete(List.of(id));
    }
}
```

### 2. 批量处理

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BatchEmbeddingService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void batchAddDocuments(List<Map<String, Object>> documentData) {
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

### 3. 自定义嵌入模型

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomEmbeddingModel implements EmbeddingModel {
    
    @Override
    public float[] embed(String text) {
        return embedForResponse(List.of(text)).getResults().get(0).getOutput();
    }
    
    @Override
    public EmbeddingResponse embedForResponse(List<String> texts) {
        List<float[]> embeddings = texts.stream()
            .map(this::computeEmbedding)
            .collect(Collectors.toList());
        
        return new EmbeddingResponse(embeddings);
    }
    
    @Override
    public float[] embed(EmbeddingRequest request) {
        return embed(request.getInstructions().get(0));
    }
    
    private float[] computeEmbedding(String text) {
        return new float[1536];
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
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
public class EmbeddingApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmbeddingApplication.class, args);
    }
}

@Service
class EmbeddingService {
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }
    
    public List<float[]> generateEmbeddings(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        return response.getResults().stream()
            .map(result -> result.getOutput())
            .collect(Collectors.toList());
    }
    
    public double calculateSimilarity(String text1, String text2) {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);
        
        return cosineSimilarity(embedding1, embedding2);
    }
    
    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

@Service
class VectorStoreService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void addDocument(String id, String text, Map<String, Object> metadata) {
        Document document = new Document(id, text, metadata);
        vectorStore.add(List.of(document));
    }
    
    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }
    
    public void deleteDocument(String id) {
        vectorStore.delete(List.of(id));
    }
}

@RestController
@RequestMapping("/api/embeddings")
class EmbeddingController {
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Autowired
    private VectorStoreService vectorStoreService;
    
    @PostMapping("/generate")
    public Map<String, Object> generateEmbedding(@RequestBody String text) {
        float[] embedding = embeddingService.generateEmbedding(text);
        return Map.of(
            "text", text,
            "embedding", embedding,
            "dimension", embedding.length
        );
    }
    
    @PostMapping("/similarity")
    public Map<String, Object> calculateSimilarity(@RequestBody Map<String, String> request) {
        String text1 = request.get("text1");
        String text2 = request.get("text2");
        double similarity = embeddingService.calculateSimilarity(text1, text2);
        
        return Map.of(
            "text1", text1,
            "text2", text2,
            "similarity", similarity
        );
    }
    
    @PostMapping("/documents")
    public void addDocument(@RequestBody Map<String, Object> request) {
        String id = (String) request.get("id");
        String text = (String) request.get("text");
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());
        
        vectorStoreService.addDocument(id, text, metadata);
    }
    
    @PostMapping("/search")
    public List<Map<String, Object>> searchSimilar(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        int topK = (int) request.getOrDefault("topK", 5);
        
        return vectorStoreService.searchSimilar(query, topK).stream()
            .map(doc -> Map.of(
                "id", doc.getId(),
                "text", doc.getText(),
                "metadata", doc.getMetadata(),
                "score", doc.getScore()
            ))
            .collect(Collectors.toList());
    }
    
    @DeleteMapping("/documents/{id}")
    public void deleteDocument(@PathVariable String id) {
        vectorStoreService.deleteDocument(id);
    }
}
```

## 测试方法

1. **启动应用**：运行 `EmbeddingApplication` 类
2. **生成嵌入**：
   ```bash
   curl -X POST http://localhost:8080/api/embeddings/generate \
     -H "Content-Type: application/json" \
     -d "Spring AI 是一个强大的 AI 框架"
   ```
3. **计算相似度**：
   ```bash
   curl -X POST http://localhost:8080/api/embeddings/similarity \
     -H "Content-Type: application/json" \
     -d '{"text1":"Spring AI 是一个强大的 AI 框架","text2":"Spring AI 提供了丰富的 AI 功能"}'
   ```
4. **添加文档**：
   ```bash
   curl -X POST http://localhost:8080/api/embeddings/documents \
     -H "Content-Type: application/json" \
     -d '{"id":"1","text":"Spring AI 是一个强大的 AI 框架","metadata":{"category":"tech"}}'
   ```
5. **搜索相似文档**：
   ```bash
   curl -X POST http://localhost:8080/api/embeddings/search \
     -H "Content-Type: application/json" \
     -d '{"query":"AI 框架","topK":3}'
   ```

## 总结

Spring AI 的文本嵌入功能提供了完整的文本向量化解决方案，包括嵌入生成、相似度计算和向量存储。通过这些功能，可以构建语义搜索、推荐系统、问答系统等应用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI Embeddings API](https://platform.openai.com/docs/guides/embeddings)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关文本嵌入的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings.html) - 嵌入模型接口详解
- [Spring AI Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) - 向量数据库集成
- [Spring AI ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html) - 文档解析与切块

### 示例模块
- **`spring-ai-ollama-embedding`** - 本仓库中的文本嵌入示例模块
  - 主类：`com.github.partmeai.ollama.SpringAiOllamaApplication`
  - 控制器：`com.github.partmeai.ollama.controller.EmbeddingController`
  - 服务层：`com.github.partmeai.ollama.service.IEmbeddingService` 及实现类
  - 路径：`/v1/embedding`（GET 查询字符串，POST 文件上传）

### 核心概念
- **`EmbeddingModel`**：文本 → 向量的统一抽象（Ollama 实现为 `OllamaEmbeddingModel`）
- **向量维度统一**：确保嵌入模型输出维度与向量库索引维度一致
- **`SearchRequest.topK`**：检索时指定返回结果数量，**不要** 放在 `OllamaEmbeddingOptions` 上

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-ollama-embedding

# 启动应用
mvn spring-boot:run

# 测试文本嵌入
curl "http://localhost:8080/v1/embedding?text=hello"

# 测试文件嵌入（multipart 上传）
curl -X POST http://localhost:8080/v1/embedding \
  -F "file=@document.txt"
```

### 配置要点
1. **嵌入模型配置**：`spring.ai.ollama.embedding.options.model` 指定 Ollama 嵌入模型
2. **模型下载**：使用 `ollama pull` 下载对应嵌入模型（如 `nomic-embed-text`、`bge-m3`）
3. **连接设置**：Ollama `base-url`、超时与重试配置
4. **与 RAG 衔接**：向量生成后由 `VectorStore.add(documents)` 写入向量库

### 注意事项
1. 嵌入模型维度需与向量库索引维度匹配
2. 生产环境建议配置适当的超时和重试机制
3. 文件上传接口支持批量处理，注意内存和性能优化
4. 参考 [RAG-SHARED.md](./RAG-SHARED.md) 了解嵌入与检索的职责划分