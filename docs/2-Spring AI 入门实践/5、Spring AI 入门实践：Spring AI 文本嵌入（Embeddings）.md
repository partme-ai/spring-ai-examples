# 5、Spring AI 入门实践：Spring AI 文本嵌入（Embeddings）

## 一、项目概述

文本嵌入（Embeddings）是将文本转换为数值向量的技术，这些向量可以表示文本的语义信息。Spring AI 提供了丰富的文本嵌入功能，支持多种嵌入模型和向量存储。通过文本嵌入，可以实现语义搜索、相似度计算、推荐系统等功能。

### 核心功能

- **文本嵌入生成**：将文本转换为向量表示
- **批量嵌入**：支持批量处理多个文本
- **相似度计算**：计算文本间的语义相似度
- **向量存储**：集成多种向量数据库
- **语义搜索**：基于相似度的文档检索
- **多模型支持**：支持 Ollama、OpenAI 等多种嵌入模型

### 适用场景

- 语义搜索系统
- 文档相似度匹配
- 推荐系统
- 问答系统
- 文本聚类
- 信息检索

## 二、文本嵌入简介

文本嵌入的核心思想是将自然语言文本映射到高维向量空间，语义相似的文本在向量空间中的距离也会更近。

### 嵌入向量特性

| 特性 | 说明 |
|------|------|
| 语义表示 | 向量包含文本的语义信息 |
| 维度固定 | 同一模型输出维度一致 |
| 距离可度量 | 可通过余弦相似度等度量距离 |
| 可计算 | 支持向量运算和比较 |

### 常用嵌入模型

| 模型 | 维度 | 提供商 |
|------|------|--------|
| nomic-embed-text | 768 | Ollama |
| bge-m3 | 1024 | Ollama |
| text-embedding-3-small | 1536 | OpenAI |
| text-embedding-3-large | 3072 | OpenAI |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型）
- Redis 或其他向量数据库（可选）

### 3.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取嵌入模型
ollama pull nomic-embed-text
# 或者
ollama pull bge-m3
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
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── EmbeddingController.java
│   │   │                   └── service/
│   │   │                       ├── IEmbeddingService.java
│   │   │                       └── OllamaEmbeddingService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `IEmbeddingService` | 嵌入服务接口 |
| `OllamaEmbeddingService` | Ollama 嵌入服务实现 |
| `EmbeddingController` | REST API 控制器 |

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
    <!-- Spring AI Document Reader -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-jsoup-document-reader</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-markdown-document-reader</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pdf-document-reader</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-tika-document-reader</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox-tools -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox-tools</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox-io -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox-io</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.pdfbox/fontbox -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>fontbox</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.tika/tika-core -->
    <dependency>
        <groupId>org.apache.tika</groupId>
        <artifactId>tika-core</artifactId>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.apache.tika/tika-parsers-standard-package -->
    <dependency>
        <groupId>org.apache.tika</groupId>
        <artifactId>tika-parsers-standard-package</artifactId>
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
          model: nomic-embed-text

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 嵌入服务接口

```java
package com.github.partmeai.ollama.service;

import java.util.List;

public interface IEmbeddingService {
    
    float[] embed(String text);
    
    List<float[]> embedBatch(List<String> texts);
    
    double cosineSimilarity(float[] vectorA, float[] vectorB);
    
    double calculateSimilarity(String text1, String text2);
}
```

### 6.2 Ollama 嵌入服务实现

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OllamaEmbeddingService implements IEmbeddingService {
    
    private final EmbeddingModel embeddingModel;
    
    public OllamaEmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    
    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        return response.getResults().stream()
                .map(result -> result.getOutput())
                .collect(Collectors.toList());
    }
    
    @Override
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("向量维度必须相同");
        }
        
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
    
    @Override
    public double calculateSimilarity(String text1, String text2) {
        float[] embedding1 = embed(text1);
        float[] embedding2 = embed(text2);
        return cosineSimilarity(embedding1, embedding2);
    }
}
```

### 6.3 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.IEmbeddingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class EmbeddingController {
    
    private final IEmbeddingService embeddingService;
    
    public EmbeddingController(IEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }
    
    @GetMapping("/embedding")
    public Map<String, Object> getEmbedding(@RequestParam String text) {
        float[] embedding = embeddingService.embed(text);
        return Map.of(
            "text", text,
            "embedding", embedding,
            "dimension", embedding.length
        );
    }
    
    @PostMapping("/embedding")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        float[] embedding = embeddingService.embed(content);
        return Map.of(
            "filename", file.getOriginalFilename(),
            "embedding", embedding,
            "dimension", embedding.length
        );
    }
    
    @PostMapping("/embedding/batch")
    public Map<String, Object> batchEmbedding(@RequestBody List<String> texts) {
        List<float[]> embeddings = embeddingService.embedBatch(texts);
        return Map.of(
            "count", texts.size(),
            "embeddings", embeddings
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
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询嵌入 | GET | `/v1/embedding` | 生成文本嵌入向量 |
| 文件嵌入 | POST | `/v1/embedding` | 上传文件并生成嵌入 |
| 批量嵌入 | POST | `/v1/embedding/batch` | 批量生成嵌入 |
| 相似度计算 | POST | `/v1/similarity` | 计算文本相似度 |

### 7.2 接口使用示例

#### 查询文本嵌入

```bash
curl "http://localhost:8080/v1/embedding?text=hello"
```

响应：
```json
{
  "text": "hello",
  "embedding": [0.1, 0.2, ...],
  "dimension": 768
}
```

#### 上传文件

```bash
curl -X POST http://localhost:8080/v1/embedding \
  -F "file=@document.txt"
```

#### 批量嵌入

```bash
curl -X POST http://localhost:8080/v1/embedding/batch \
  -H "Content-Type: application/json" \
  -d '["文本1", "文本2", "文本3"]'
```

#### 计算相似度

```bash
curl -X POST http://localhost:8080/v1/similarity \
  -H "Content-Type: application/json" \
  -d '{
    "text1": "Spring AI 是一个强大的 AI 框架",
    "text2": "Spring AI 提供了丰富的 AI 功能"
  }'
```

## 八、部署方式

### 8.1 本地运行

```bash
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
import json

class EmbeddingClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def get_embedding(self, text):
        response = requests.get(
            f"{self.base_url}/v1/embedding",
            params={"text": text}
        )
        return response.json()
    
    def calculate_similarity(self, text1, text2):
        response = requests.post(
            f"{self.base_url}/v1/similarity",
            json={"text1": text1, "text2": text2}
        )
        return response.json()["similarity"]

client = EmbeddingClient()

# 获取嵌入
result = client.get_embedding("Spring AI")
print(f"维度: {result['dimension']}")

# 计算相似度
similarity = client.calculate_similarity(
    "Spring AI 是一个 AI 框架",
    "Spring Boot 是一个 Web 框架"
)
print(f"相似度: {similarity}")
```

### 9.2 配置要点

1. **嵌入模型配置**：`spring.ai.ollama.embedding.options.model` 指定 Ollama 嵌入模型
2. **模型下载**：使用 `ollama pull` 下载对应嵌入模型（如 `nomic-embed-text`、`bge-m3`）
3. **连接设置**：Ollama `base-url`、超时与重试配置
4. **与 RAG 衔接**：向量生成后由 `VectorStore.add(documents)` 写入向量库

### 9.3 注意事项

1. 嵌入模型维度需与向量库索引维度匹配
2. 生产环境建议配置适当的超时和重试机制
3. 文件上传接口支持批量处理，注意内存和性能优化

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama
curl http://localhost:11434/api/tags

# 确认嵌入模型已下载
ollama list | grep embed
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-embedding
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl "http://localhost:8080/v1/embedding?text=hello"
```

## 十一、常见问题

### 11.1 嵌入问题

**Q: 嵌入维度与向量库不匹配怎么办？**

确保使用的嵌入模型输出维度与向量库索引创建时的维度一致。可以通过以下方式检查：
- 查看模型文档确认维度
- 调用一次嵌入接口查看返回的维度
- 重新创建向量库索引

**Q: 批量处理时内存占用过高？**

- 减少单次批量处理的数量
- 使用分批处理策略
- 调整 JVM 内存参数

### 11.2 性能问题

**Q: 如何提升嵌入速度？**

- 使用更小的嵌入模型
- 启用批量处理
- 使用 GPU 加速（如果可用）
- 缓存常用文本的嵌入结果

**Q: 相似度计算不准确？**

- 确保使用合适的相似度度量方法
- 检查文本预处理是否合适
- 考虑使用更高质量的嵌入模型

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Embeddings：https://docs.spring.io/spring-ai/reference/api/embeddings.html
- Spring AI Vector Databases：https://docs.spring.io/spring-ai/reference/api/vectordbs.html
- Spring AI ETL Pipeline：https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
- 示例模块：spring-ai-ollama-embedding

## 十四、致谢

感谢 Spring AI 团队提供的优秀框架，让文本嵌入功能变得如此简单易用。
