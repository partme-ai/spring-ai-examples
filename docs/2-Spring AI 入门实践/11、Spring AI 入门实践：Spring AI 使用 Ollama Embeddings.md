# 11、Spring AI 入门实践：Spring AI 使用 Ollama Embeddings

## 一、项目概述

Ollama Embeddings 是 Ollama 提供的文本嵌入功能，用于将文本转换为向量表示，支持多种开源嵌入模型。Spring AI 提供了与 Ollama Embeddings 的完整集成，使得开发者可以轻松地在 Spring 应用中使用本地部署的文本嵌入模型。文本嵌入是 RAG（检索增强生成）、语义搜索、文本聚类等 AI 应用的基础。

### 核心功能

- **本地模型部署**：无需云服务即可生成文本嵌入
- **多种模型支持**：mxbai-embed-large、nomic-embed-text、snowflake-arctic-embed 等多种模型
- **批量嵌入**：支持一次处理多个文本
- **向量相似度计算**：内置余弦相似度等计算方法
- **向量存储集成**：可直接与多种向量数据库集成
- **OpenAI 兼容接口**：提供与 OpenAI 兼容的 API

### 适用场景

- 检索增强生成（RAG）
- 语义搜索和推荐
- 文本相似度计算
- 文档聚类和分类
- 隐私敏感的企业应用
- 离线环境的 AI 应用

## 二、Ollama Embeddings 简介

文本嵌入将文本转换为高维向量空间中的点，语义相似的文本在向量空间中距离更近。Ollama 提供了多种高质量的开源嵌入模型，Spring AI 在此基础上提供了统一的抽象和便利功能。

### 常用嵌入模型对比

| 模型 | 维度 | 特点 | 适用场景 |
|------|------|------|---------|
| mxbai-embed-large | 1024 | 综合性能好、语义理解强 | 通用嵌入、RAG |
| nomic-embed-text | 768 | 速度快、轻量级 | 快速响应、效率优先 |
| snowflake-arctic-embed | 1024 | 代码和技术文档强 | 技术文档、代码搜索 |
| all-minilm | 384 | 极小、速度极快 | 资源受限环境 |

### 核心概念

| 概念 | 说明 |
|------|------|
| EmbeddingModel | 嵌入模型的统一抽象 |
| EmbeddingClient | 高级嵌入客户端，简化交互 |
| Embedding | 文本的向量表示 |
| VectorStore | 向量存储抽象 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型运行环境）

### 3.2 Ollama 安装和配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取嵌入模型
ollama pull mxbai-embed-large
ollama pull nomic-embed-text
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-embedding/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaEmbeddingApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── EmbeddingController.java
│   │   │                   └── service/
│   │   │                       └── EmbeddingService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| EmbeddingService | 嵌入业务逻辑 |
| EmbeddingController | REST API 控制器 |

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
    name: spring-ai-ollama-embedding
  
  ai:
    ollama:
      base-url: http://localhost:11434
      embedding:
        enabled: true
        options:
          model: mxbai-embed-large

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 嵌入服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    private final VectorStore vectorStore;
    
    public EmbeddingService(EmbeddingClient embeddingClient, VectorStore vectorStore) {
        this.embeddingClient = embeddingClient;
        this.vectorStore = vectorStore;
    }
    
    public List<float[]> generateEmbeddings(List<String> texts) {
        List<Embedding> embeddings = embeddingClient.embedForResponse(texts);
        return embeddings.stream()
                .map(Embedding::getOutput)
                .toList();
    }
    
    public float[] generateSingleEmbedding(String text) {
        return embeddingClient.embed(text);
    }
    
    public double calculateCosineSimilarity(float[] v1, float[] v2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    public Map<String, Double> calculateSimilarities(String targetText, List<String> comparisonTexts) {
        float[] targetEmbedding = generateSingleEmbedding(targetText);
        
        Map<String, Double> similarities = new java.util.HashMap<>();
        for (String text : comparisonTexts) {
            float[] textEmbedding = generateSingleEmbedding(text);
            double similarity = calculateCosineSimilarity(targetEmbedding, textEmbedding);
            similarities.put(text, similarity);
        }
        
        return similarities;
    }
    
    public void addDocuments(List<Map<String, Object>> docs) {
        List<Document> documents = docs.stream()
                .map(doc -> new Document(
                        (String) doc.get("content"),
                        (Map<String, Object>) doc.getOrDefault("metadata", Map.of())
                ))
                .toList();
        
        vectorStore.add(documents);
    }
    
    public List<Map<String, Object>> similaritySearch(String query, int topK) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query(query)
                        .withTopK(topK)
        );
        
        return results.stream()
                .map(doc -> Map.<String, Object>of(
                        "content", doc.getContent(),
                        "metadata", doc.getMetadata(),
                        "score", doc.getMetadata().get("score")
                ))
                .toList();
    }
    
    public int getEmbeddingDimension() {
        return embeddingClient.dimensions();
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {
    
    private final EmbeddingService embeddingService;
    
    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }
    
    @PostMapping("/batch")
    public Map<String, Object> generateEmbeddings(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> texts = (List<String>) request.get("texts");
        
        List<float[]> embeddings = embeddingService.generateEmbeddings(texts);
        
        return Map.of(
            "count", embeddings.size(),
            "dimension", embeddingService.getEmbeddingDimension(),
            "texts", texts,
            "embeddings", embeddings
        );
    }
    
    @PostMapping("/single")
    public Map<String, Object> generateSingleEmbedding(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        float[] embedding = embeddingService.generateSingleEmbedding(text);
        
        return Map.of(
            "text", text,
            "dimension", embedding.length,
            "embedding", embedding
        );
    }
    
    @PostMapping("/similarity")
    public Map<String, Object> calculateSimilarity(@RequestBody Map<String, Object> request) {
        String targetText = (String) request.get("target");
        @SuppressWarnings("unchecked")
        List<String> comparisonTexts = (List<String>) request.get("comparisons");
        
        Map<String, Double> similarities = embeddingService.calculateSimilarities(targetText, comparisonTexts);
        
        return Map.of(
            "target", targetText,
            "similarities", similarities
        );
    }
    
    @PostMapping("/documents")
    public Map<String, String> addDocuments(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> docs = (List<Map<String, Object>>) request.get("documents");
        
        embeddingService.addDocuments(docs);
        
        return Map.of("status", "success", "message", "Documents added");
    }
    
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String query,
                                       @RequestParam(defaultValue = "5") int topK) {
        List<Map<String, Object>> results = embeddingService.similaritySearch(query, topK);
        
        return Map.of(
            "query", query,
            "topK", topK,
            "results", results
        );
    }
    
    @GetMapping("/dimension")
    public Map<String, Integer> getDimension() {
        return Map.of("dimension", embeddingService.getEmbeddingDimension());
    }
}
```

### 6.3 主应用类和配置

```java
package com.github.partmeai.ollama;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.InMemoryVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAiOllamaEmbeddingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaEmbeddingApplication.class, args);
    }
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient) {
        return new InMemoryVectorStore(embeddingClient);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 批量嵌入 | POST | `/api/embedding/batch` | 批量生成文本嵌入 |
| 单个嵌入 | POST | `/api/embedding/single` | 生成单个文本的嵌入 |
| 相似度计算 | POST | `/api/embedding/similarity` | 计算文本相似度 |
| 添加文档 | POST | `/api/embedding/documents` | 添加文档到向量存储 |
| 相似度搜索 | GET | `/api/embedding/search` | 执行相似度搜索 |
| 获取维度 | GET | `/api/embedding/dimension` | 获取嵌入维度 |

### 7.2 接口使用示例

#### 批量嵌入

```bash
curl -X POST http://localhost:8080/api/embedding/batch \
  -H "Content-Type: application/json" \
  -d '{
    "texts": [
      "Spring Boot 是一个快速开发框架",
      "Spring AI 提供了 AI 能力集成",
      "Python 是一种编程语言"
    ]
  }'
```

#### 单个嵌入

```bash
curl -X POST http://localhost:8080/api/embedding/single \
  -H "Content-Type: application/json" \
  -d '{"text": "这是一个测试文本"}'
```

#### 相似度计算

```bash
curl -X POST http://localhost:8080/api/embedding/similarity \
  -H "Content-Type: application/json" \
  -d '{
    "target": "Spring Boot 是一个快速开发框架",
    "comparisons": [
      "Spring AI 提供了 AI 能力集成",
      "Python 是一种编程语言",
      "Java 是一种编程语言"
    ]
  }'
```

#### 添加文档

```bash
curl -X POST http://localhost:8080/api/embedding/documents \
  -H "Content-Type: application/json" \
  -d '{
    "documents": [
      {
        "content": "Spring Boot 是一个快速开发框架",
        "metadata": {"category": "framework", "year": 2023}
      },
      {
        "content": "Spring AI 提供了 AI 能力集成",
        "metadata": {"category": "ai", "year": 2024}
      },
      {
        "content": "Python 是一种编程语言",
        "metadata": {"category": "language", "year": 1991}
      }
    ]
  }'
```

#### 相似度搜索

```bash
curl "http://localhost:8080/api/embedding/search?query=Spring%20相关技术&topK=3"
```

#### 获取嵌入维度

```bash
curl http://localhost:8080/api/embedding/dimension
```

## 八、部署方式

### 8.1 本地运行

```bash
# 确保 Ollama 正在运行
ollama serve

# 启动应用
cd spring-ai-ollama-embedding
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-embedding-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class OllamaEmbeddingClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def batch_embed(self, texts):
        response = requests.post(
            f"{self.base_url}/api/embedding/batch",
            json={"texts": texts}
        )
        return response.json()
    
    def single_embed(self, text):
        response = requests.post(
            f"{self.base_url}/api/embedding/single",
            json={"text": text}
        )
        return response.json()
    
    def calculate_similarity(self, target, comparisons):
        response = requests.post(
            f"{self.base_url}/api/embedding/similarity",
            json={"target": target, "comparisons": comparisons}
        )
        return response.json()
    
    def add_documents(self, documents):
        response = requests.post(
            f"{self.base_url}/api/embedding/documents",
            json={"documents": documents}
        )
        return response.json()
    
    def search(self, query, top_k=5):
        response = requests.get(
            f"{self.base_url}/api/embedding/search",
            params={"query": query, "topK": top_k}
        )
        return response.json()
    
    def get_dimension(self):
        response = requests.get(
            f"{self.base_url}/api/embedding/dimension"
        )
        return response.json()

client = OllamaEmbeddingClient()

# 获取嵌入维度
dim = client.get_dimension()
print(f"Embedding dimension: {dim['dimension']}")

# 批量嵌入
texts = [
    "Spring Boot 是一个快速开发框架",
    "Spring AI 提供了 AI 能力集成",
    "Python 是一种编程语言"
]
result = client.batch_embed(texts)
print(f"Generated {result['count']} embeddings")

# 相似度计算
similarity_result = client.calculate_similarity(
    "Spring Boot 是一个快速开发框架",
    [
        "Spring AI 提供了 AI 能力集成",
        "Python 是一种编程语言"
    ]
)
print("Similarities:", similarity_result["similarities"])

# 添加文档和搜索
documents = [
    {
        "content": "Spring Boot 是一个快速开发框架",
        "metadata": {"category": "framework"}
    },
    {
        "content": "Spring AI 提供了 AI 能力集成",
        "metadata": {"category": "ai"}
    }
]
client.add_documents(documents)

search_result = client.search("Spring 相关技术", top_k=2)
print("Search results:", search_result["results"])
```

### 9.2 最佳实践

1. **模型选择**：根据任务需求选择合适的模型维度和质量
2. **批量处理**：对于大量文本使用批量嵌入提高效率
3. **向量存储**：根据数据规模选择合适的向量数据库
4. **相似度阈值**：根据应用场景设置合理的相似度阈值
5. **缓存策略**：对频繁访问的嵌入结果进行缓存
6. **错误处理**：实现适当的错误处理和重试机制

### 9.3 嵌入模型推荐

| 场景 | 推荐模型 | 理由 |
|------|----------|------|
| 通用RAG | mxbai-embed-large | 综合性能好、语义理解强 |
| 快速原型 | nomic-embed-text | 速度快、轻量级 |
| 技术文档 | snowflake-arctic-embed | 代码和技术文档处理能力强 |
| 资源受限 | all-minilm | 极小、速度极快 |

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama 是否正在运行
curl http://localhost:11434/api/tags

# 确认模型已下载
ollama list | grep embed
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-embedding
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -X POST http://localhost:8080/api/embedding/single \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello World"}'
```

## 十一、常见问题

### 11.1 连接问题

**Q: 无法连接到 Ollama 怎么办？**

- 确认 Ollama 服务正在运行：`ollama serve`
- 检查配置的 base-url 是否正确
- 确认端口 11434 没有被防火墙阻止
- 尝试在浏览器中访问 http://localhost:11434

**Q: 模型加载失败怎么办？**

- 使用 `ollama pull <model-name>` 重新下载模型
- 检查磁盘空间是否充足
- 查看 Ollama 日志确认错误原因

### 11.2 性能问题

**Q: 嵌入生成速度很慢怎么办？**

- 使用更小更快的模型（如 nomic-embed-text）
- 使用批量嵌入减少网络请求
- 使用更好的硬件（GPU 加速）
- 考虑使用缓存避免重复计算

**Q: 如何提升嵌入质量？**

- 使用更高质量的模型（如 mxbai-embed-large）
- 对输入文本进行预处理（去除噪声、标准化）
- 考虑使用微调后的模型适配特定领域
- 使用查询重写技术改进搜索效果

### 11.3 向量搜索问题

**Q: 相似度搜索结果不准确怎么办？**

- 检查模型选择是否合适
- 调整搜索的 topK 参数
- 考虑对文档进行分块处理
- 优化查询文本
- 调整相似度阈值

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Ollama Embeddings：https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
- Ollama 官方文档：https://ollama.ai/docs
- 示例模块：spring-ai-ollama-embedding

## 十四、致谢

感谢 Ollama 团队和 Spring AI 团队提供的优秀工具，让本地文本嵌入和向量搜索变得如此简单易用。
