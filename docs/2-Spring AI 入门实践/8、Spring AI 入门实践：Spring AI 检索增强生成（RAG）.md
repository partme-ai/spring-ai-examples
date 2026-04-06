# 8、Spring AI 入门实践：Spring AI 检索增强生成（RAG）

## 一、项目概述

检索增强生成（Retrieval-Augmented Generation，RAG）是一种结合了检索和生成的技术，通过从知识库中检索相关信息来增强大语言模型的生成能力。Spring AI 提供了完整的 RAG 解决方案，包括文档加载、分块、向量存储、相似度搜索和答案生成等完整流程。

### 核心功能

- **文档加载**：支持 PDF、文本等多种格式
- **文档分块**：灵活的文本分割策略
- **向量存储**：集成多种向量数据库
- **相似度搜索**：基于语义的文档检索
- **上下文增强**：将检索结果拼接到提示中
- **答案生成**：基于增强上下文生成答案

### 适用场景

- 智能问答系统
- 知识库问答
- 文档检索与摘要
- 企业内部知识库
- 技术文档助手

## 二、RAG 简介

RAG 的核心思想是先从知识库中检索相关文档，然后将这些文档作为上下文，让模型基于这些信息生成更准确的答案。

### RAG 流程

| 步骤 | 说明 |
|------|------|
| 文档切块与写入 | 将文档分割为适当大小的块并存入向量库 |
| 用户查询嵌入 | 将用户问题转换为向量表示 |
| 相似度检索 | 在向量库中搜索与查询最相关的文档块 |
| 上下文拼接 | 将检索结果拼接到系统提示中 |
| LLM 调用 | 使用增强的上下文生成最终答案 |

### 优势

- 减少幻觉：基于真实文档生成答案
- 知识更新：无需重新训练模型即可更新知识
- 可追溯性：答案来源可追溯到具体文档
- 成本效益：相比微调更经济

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型）
- 向量数据库（Cassandra、Chroma 等

### 3.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取模型
ollama pull llama3.1:8b
ollama pull nomic-embed-text
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-rag-cassandra/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── RAGController.java
│   │   │                   └── service/
│   │   │                       └── RagService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `RagService` | RAG 业务逻辑 |
| `RAGController` | REST API 控制器 |

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
    name: spring-ai-ollama-rag-cassandra
  
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
          model: nomic-embed-text

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 RAG 服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;
    
    public RagService(ChatClient chatClient,
                      VectorStore vectorStore,
                      TokenTextSplitter textSplitter) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.textSplitter = textSplitter;
    }
    
    public void ingestDocument(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPagesPerDocument(1)
                .build();
        
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        List<Document> documents = pdfReader.get();
        
        List<Document> splitDocuments = textSplitter.apply(documents);
        vectorStore.add(splitDocuments);
    }
    
    public String queryWithRag(String userQuery) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(userQuery).withTopK(3)
        );
        
        String context = relevantDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        
        String systemPrompt = """
            你是一个智能助手。请根据以下上下文信息回答用户的问题。
            如果上下文中没有相关信息，请说明你无法从提供的上下文中找到答案。
            
            上下文信息：
            %s
            """.formatted(context);
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userQuery)
        ));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.RagService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ai/rag")
public class RAGController {
    
    private final RagService ragService;
    
    public RAGController(RagService ragService) {
        this.ragService = ragService;
    }
    
    @PostMapping("/ingest")
    public Map<String, String> ingestDocument(@RequestParam("file") MultipartFile file) {
        try {
            ragService.ingestDocument(file.getResource());
            return Map.of(
                "status", "success",
                "message", "文档处理成功"
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "message", e.getMessage()
            );
        }
    }
    
    @GetMapping("/query")
    public Map<String, Object> query(@RequestParam String query) {
        String answer = ragService.queryWithRag(query);
        return Map.of(
            "query", query,
            "answer", answer
        );
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 文档上传 | POST | `/ai/rag/ingest` | 上传并处理文档 |
| RAG 查询 | GET | `/ai/rag/query` | 基于知识库查询 |

### 7.2 接口使用示例

#### 上传文档

```bash
curl -X POST http://localhost:8080/ai/rag/ingest \
  -F "file=@document.pdf"
```

响应：
```json
{
  "status": "success",
  "message": "文档处理成功"
}
```

#### RAG 查询

```bash
curl "http://localhost:8080/ai/rag/query?query=Spring+AI+的主要功能是什么？"
```

响应：
```json
{
  "query": "Spring AI 的主要功能是什么？",
  "answer": "Spring AI 提供了完整的 AI 集成功能，包括..."
}
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-ollama-rag-cassandra
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-rag-cassandra-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class RAGClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def ingest_document(self, file_path):
        with open(file_path, 'rb') as f:
            files = {'file': f}
            response = requests.post(
                f"{self.base_url}/ai/rag/ingest",
                files=files
            )
            return response.json()
    
    def query(self, query):
        response = requests.get(
            f"{self.base_url}/ai/rag/query",
            params={'query': query}
        )
        return response.json()

client = RAGClient()

# 上传文档
result = client.ingest_document("document.pdf")
print(result)

# 查询
result = client.query("Spring AI 的主要功能是什么？")
print(f"答案: {result['answer']}")
```

### 9.2 最佳实践

1. **文档切块优化**：控制 Document 粒度，添加适当的元数据便于过滤与溯源
2. **检索参数调优**：使用 SearchRequest 调整 topK 和相似度阈值，平衡召回率与噪声
3. **幻觉防范**：将检索片段作为唯一事实来源的提示策略，生产环境建议人工抽检
4. **模块选择**：根据需求选择合适的向量库（Cassandra、Chroma 等），逻辑均为 VectorStore + ChatClient 组合

### 9.3 注意事项

- 向量检索的 Top-K、相似度阈值等应使用 SearchRequest（见 VectorStore#similaritySearch(SearchRequest)），不要放在 OllamaEmbeddingOptions 上（嵌入选项与检索参数职责不同。

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama
curl http://localhost:11434/api/tags

# 确认向量数据库运行
# 根据使用的向量数据库进行相应检查
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-rag-cassandra
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl "http://localhost:8080/ai/rag/query?query=hello"
```

## 十一、常见问题

### 11.1 文档处理问题

**Q: 文档上传后检索不到相关内容？**

- 检查文档是否成功分块并写入向量库
- 调整分块大小，确保语义完整性
- 检查嵌入模型是否正常工作

**Q: 如何提高检索准确率？**

- 调整 topK 参数（一般 3-5 个文档块较合适
- 优化文档分块策略，确保块大小适中
- 使用更好的嵌入模型

### 11.2 性能问题

**Q: 检索速度慢怎么办？**

- 优化向量数据库索引
- 使用缓存机制缓存常见查询
- 减少 topK 数量

**Q: 如何减少幻觉？**

- 明确要求模型只基于上下文回答
- 添加"不知道时明确说明
- 生产环境建议人工验证

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI ChatClient — RAG：https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation
- Spring AI Vector Databases：https://docs.spring.io/spring-ai/reference/api/vectordbs.html
- 示例模块：spring-ai-ollama-rag-cassandra、spring-ai-ollama-rag-chroma

## 十四、致谢

感谢 Spring AI 团队提供的优秀框架，让 RAG 功能变得如此简单易用。
