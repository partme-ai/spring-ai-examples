# Spring AI 入门实践：Spring AI 使用 Ollama Embeddings

## 什么是 Ollama Embeddings

Ollama Embeddings 是 Ollama 提供的文本嵌入功能，用于将文本转换为向量表示，支持多种开源嵌入模型。

## 准备工作

### 1. 安装 Ollama

访问 [Ollama 官网](https://ollama.ai/) 下载并安装 Ollama。

### 2. 下载嵌入模型

使用以下命令下载嵌入模型：

```bash
ollama pull mxbai-embed-large
```

### 3. 添加 Spring AI 依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama</artifactId>
</dependency>
```

## 配置 Ollama Embeddings

### 1. 配置属性

在 `application.properties` 中添加：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.embedding.model=mxbai-embed-large
```

### 2. 创建 Ollama Embedding Client Bean

```java
@Bean
public EmbeddingClient embeddingClient(OllamaEmbeddingModel ollamaEmbeddingModel) {
    return new EmbeddingClient(ollamaEmbeddingModel);
}
```

## 基本操作

### 1. 生成文本嵌入

```java
@Autowired
private EmbeddingClient embeddingClient;

public void generateEmbeddings() {
    List<String> texts = new ArrayList<>();
    texts.add("Spring Boot 是一个快速开发框架");
    texts.add("Spring AI 提供了 AI 能力集成");
    
    List<Embedding> embeddings = embeddingClient.embed(texts);
    for (int i = 0; i < embeddings.size(); i++) {
        System.out.println("文本: " + texts.get(i));
        System.out.println("嵌入维度: " + embeddings.get(i).getValues().length);
        System.out.println();
    }
}
```

### 2. 计算文本相似度

```java
public void calculateSimilarity() {
    // 生成嵌入
    Embedding embedding1 = embeddingClient.embed("Spring Boot 是一个快速开发框架").get(0);
    Embedding embedding2 = embeddingClient.embed("Spring AI 提供了 AI 能力集成").get(0);
    Embedding embedding3 = embeddingClient.embed("Python 是一种编程语言").get(0);
    
    // 计算余弦相似度
    double similarity12 = cosineSimilarity(embedding1.getValues(), embedding2.getValues());
    double similarity13 = cosineSimilarity(embedding1.getValues(), embedding3.getValues());
    double similarity23 = cosineSimilarity(embedding2.getValues(), embedding3.getValues());
    
    System.out.println("Spring Boot vs Spring AI: " + similarity12);
    System.out.println("Spring Boot vs Python: " + similarity13);
    System.out.println("Spring AI vs Python: " + similarity23);
}

// 余弦相似度计算
private double cosineSimilarity(float[] v1, float[] v2) {
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
```

### 3. 与向量存储集成

```java
@Bean
public VectorStore vectorStore(EmbeddingClient embeddingClient) {
    return new InMemoryVectorStore(embeddingClient);
}

public void vectorStoreDemo() {
    // 添加文档
    List<Document> documents = new ArrayList<>();
    documents.add(new Document("Spring Boot 是一个快速开发框架", Map.of("category", "framework")));
    documents.add(new Document("Spring AI 提供了 AI 能力集成", Map.of("category", "ai")));
    documents.add(new Document("Python 是一种编程语言", Map.of("category", "language")));
    
    vectorStore.add(documents);
    
    // 相似度搜索
    List<Document> results = vectorStore.similaritySearch("Spring 相关技术", 2);
    for (Document document : results) {
        System.out.println("内容: " + document.getContent());
        System.out.println("相似度: " + document.getMetadata().get("score"));
    }
}
```

## 高级特性

### 1. 自定义模型参数

```java
@Bean
public OllamaEmbeddingModel ollamaEmbeddingModel() {
    return new OllamaEmbeddingModel(
        new OllamaApi(System.getenv("OLLAMA_BASE_URL")),
        OllamaEmbeddingOptions.builder()
            .withModel("mxbai-embed-large")
            .withTemperature(0.0f)
            .build()
    );
}
```

### 2. 批量嵌入

```java
public void batchEmbeddings() {
    List<String> texts = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
        texts.add("文档 " + i + ": 这是一个测试文档");
    }
    
    List<Embedding> embeddings = embeddingClient.embed(texts);
    System.out.println("生成了 " + embeddings.size() + " 个嵌入");
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.InMemoryVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService {

    @Autowired
    private EmbeddingClient embeddingClient;

    @Autowired
    private VectorStore vectorStore;

    public void demo() {
        // 生成文本嵌入
        System.out.println("=== 生成文本嵌入 ===");
        List<String> texts = new ArrayList<>();
        texts.add("Spring Boot 是一个快速开发框架");
        texts.add("Spring AI 提供了 AI 能力集成");
        texts.add("Python 是一种编程语言");
        
        List<Embedding> embeddings = embeddingClient.embed(texts);
        for (int i = 0; i < embeddings.size(); i++) {
            System.out.println("文本: " + texts.get(i));
            System.out.println("嵌入维度: " + embeddings.get(i).getValues().length);
            System.out.println();
        }
        
        // 计算文本相似度
        System.out.println("=== 计算文本相似度 ===");
        Embedding embedding1 = embeddingClient.embed("Spring Boot 是一个快速开发框架").get(0);
        Embedding embedding2 = embeddingClient.embed("Spring AI 提供了 AI 能力集成").get(0);
        Embedding embedding3 = embeddingClient.embed("Python 是一种编程语言").get(0);
        
        double similarity12 = cosineSimilarity(embedding1.getValues(), embedding2.getValues());
        double similarity13 = cosineSimilarity(embedding1.getValues(), embedding3.getValues());
        double similarity23 = cosineSimilarity(embedding2.getValues(), embedding3.getValues());
        
        System.out.println("Spring Boot vs Spring AI: " + similarity12);
        System.out.println("Spring Boot vs Python: " + similarity13);
        System.out.println("Spring AI vs Python: " + similarity23);
        
        // 与向量存储集成
        System.out.println("\n=== 与向量存储集成 ===");
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Spring Boot 是一个快速开发框架", Map.of("category", "framework")));
        documents.add(new Document("Spring AI 提供了 AI 能力集成", Map.of("category", "ai")));
        documents.add(new Document("Python 是一种编程语言", Map.of("category", "language")));
        
        vectorStore.add(documents);
        
        List<Document> results = vectorStore.similaritySearch("Spring 相关技术", 2);
        for (Document document : results) {
            System.out.println("内容: " + document.getContent());
            System.out.println("类别: " + document.getMetadata().get("category"));
            System.out.println("相似度: " + document.getMetadata().get("score"));
            System.out.println();
        }
    }

    private double cosineSimilarity(float[] v1, float[] v2) {
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
}
```

## 总结

通过 Spring AI 与 Ollama Embeddings 的集成，我们可以在本地生成文本嵌入，用于文本相似度计算、向量搜索等任务。这为需要离线处理文本嵌入的应用提供了理想的解决方案。

## 相关资源

- [Ollama 官方文档](https://ollama.ai/docs)
- [Spring AI Embeddings 文档](https://docs.spring.io/spring-ai/docs/current/reference/html/index.html#embeddings)