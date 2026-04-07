# 8、Spring AI 入门实践：Spring AI 检索增强生成（RAG）

## 概述

检索增强生成（Retrieval-Augmented Generation，RAG）是一种结合了检索和生成的技术，通过从知识库中检索相关信息来增强大语言模型的生成能力。Spring AI 提供了完整的 RAG 解决方案。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI API**（或其他兼容的 LLM API）
- **向量数据库**（如 Redis、Chroma、Milvus 等）

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
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pdf-document-reader</artifactId>
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
spring.ai.openai.chat.options.model=gpt-4
spring.ai.openai.embedding.options.model=text-embedding-3-small

spring.ai.vectorstore.redis.uri=redis://localhost:6379
spring.ai.vectorstore.redis.index-name=documents
spring.ai.vectorstore.redis.prefix=doc:
```

## 基本使用

### 1. 文档加载和分块

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.TextSplitter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentLoaderService {
    
    @Autowired
    private TextSplitter textSplitter;
    
    public List<Document> loadAndSplitDocuments(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
            .withPageTopMargin(0)
            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                .withNumberOfTopTextLinesToDelete(0)
                .withNumberOfBottomTextLinesToDelete(0)
                .build())
            .withPagesPerDocument(1)
            .build();
        
        DocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        List<Document> documents = pdfReader.get();
        
        return textSplitter.apply(documents);
    }
}
```

### 2. 向量存储

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VectorStoreService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
    }
    
    public List<Document> searchSimilar(String query, int topK) {
        SearchRequest request = SearchRequest.query(query).withTopK(topK);
        return vectorStore.similaritySearch(request);
    }
}
```

### 3. RAG 查询

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private VectorStore vectorStore;
    
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

## 高级功能

### 1. 自定义分块策略

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Component
public class CustomTextSplitter implements TextSplitter {
    
    private final int chunkSize;
    private final int chunkOverlap;
    
    public CustomTextSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }
    
    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> splitDocuments = new ArrayList<>();
        
        for (Document document : documents) {
            String text = document.getText();
            List<String> chunks = splitText(text);
            
            for (int i = 0; i < chunks.size(); i++) {
                Document chunk = new Document(
                    document.getId() + "_" + i,
                    chunks.get(i),
                    document.getMetadata()
                );
                splitDocuments.add(chunk);
            }
        }
        
        return splitDocuments;
    }
    
    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end - chunkOverlap;
        }
        
        return chunks;
    }
}
```

### 2. 混合检索

```java
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HybridRetrievalService {
    
    @Autowired
    private VectorStore vectorStore;
    
    public List<Document> hybridSearch(String query, int topK) {
        List<Document> vectorResults = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK * 2)
        );
        
        List<Document> keywordResults = keywordSearch(query, topK * 2);
        
        return combineResults(vectorResults, keywordResults, topK);
    }
    
    private List<Document> keywordSearch(String query, int topK) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK)
        );
    }
    
    private List<Document> combineResults(List<Document> vectorResults, 
                                         List<Document> keywordResults, 
                                         int topK) {
        return vectorResults.stream()
            .limit(topK)
            .collect(Collectors.toList());
    }
}
```

### 3. RAG 链

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagChainService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private VectorStore vectorStore;
    
    public String ragChain(String userQuery) {
        String context = retrieveContext(userQuery);
        String answer = generateAnswer(userQuery, context);
        String refinedAnswer = refineAnswer(userQuery, context, answer);
        
        return refinedAnswer;
    }
    
    private String retrieveContext(String query) {
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(3)
        );
        
        return relevantDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));
    }
    
    private String generateAnswer(String query, String context) {
        String systemPrompt = """
            你是一个智能助手。请根据以下上下文信息回答用户的问题。
            
            上下文信息：
            %s
            """.formatted(context);
        
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(query)
        ));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
    
    private String refineAnswer(String query, String context, String initialAnswer) {
        String systemPrompt = """
            你是一个答案优化专家。请根据以下信息优化答案：
            
            原始问题：%s
            上下文信息：%s
            初始答案：%s
            
            请优化答案，使其更准确、更完整、更易理解。
            """.formatted(query, context, initialAnswer);
        
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage("请优化以上答案")
        ));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.*;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class RagApplication {
    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
}

@Service
class RagService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private VectorStore vectorStore;
    
    @Autowired
    private TokenTextSplitter textSplitter;
    
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

@RestController
@RequestMapping("/api/rag")
class RagController {
    
    @Autowired
    private RagService service;
    
    @PostMapping("/ingest")
    public Map<String, String> ingestDocument(@RequestParam("file") MultipartFile file) {
        Resource resource = file.getResource();
        service.ingestDocument(resource);
        return Map.of("status", "success", "message", "Document ingested successfully");
    }
    
    @PostMapping("/query")
    public Map<String, Object> queryWithRag(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String answer = service.queryWithRag(query);
        
        return Map.of(
            "query", query,
            "answer", answer
        );
    }
}
```

## 测试方法

1. **启动应用**：运行 `RagApplication` 类
2. **上传文档**：
   ```bash
   curl -X POST http://localhost:8080/api/rag/ingest \
     -F "file=@document.pdf"
   ```
3. **查询**：
   ```bash
   curl -X POST http://localhost:8080/api/rag/query \
     -H "Content-Type: application/json" \
     -d '{"query":"Spring AI 的主要功能是什么？"}'
   ```

## 总结

Spring AI 的 RAG 功能提供了完整的检索增强生成解决方案，包括文档加载、分块、向量存储、相似度搜索和答案生成等。通过这些功能，可以构建基于知识库的智能问答系统。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [RAG 论文](https://arxiv.org/abs/2005.11401)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关检索增强生成（RAG）的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI ChatClient — RAG](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation) - RAG 集成指南
- [Spring AI Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) - 向量数据库集成

### RAG 流程
典型的 RAG 流程包括以下步骤：
1. **文档切块与写入**：将文档分割为适当大小的块并存入向量库
2. **用户查询嵌入**：将用户问题转换为向量表示
3. **相似度检索**：在向量库中搜索与查询最相关的文档块
4. **上下文拼接**：将检索结果拼接到系统提示中
5. **LLM 调用**：使用增强的上下文生成最终答案

### 示例模块
- **`spring-ai-ollama-rag-cassandra`** - 本仓库中的 RAG 示例模块（Cassandra 向量库）
  - 控制器：`com.github.teachingai.ollama.controller.RAGController`
  - 核心逻辑：`vectorStore.similaritySearch()` + `SystemPromptTemplate` + `BeanOutputConverter<Person>`
- **`spring-ai-ollama-rag-chroma`** - 基于 Chroma 向量库的 RAG 示例
  - 集成测试：`OllamaEmbeddingTest` 演示 `SimpleVectorStore` 与相似度检索

### 运行与验证
```bash
# 进入 Cassandra RAG 示例模块
cd spring-ai-ollama-rag-cassandra

# 启动应用
mvn spring-boot:run

# 测试 RAG 接口（示例）
curl "http://localhost:8080/ai/rag/people?name=Alice"
```

### 最佳实践
1. **文档切块优化**：控制 `Document` 粒度，添加适当的元数据便于过滤与溯源
2. **检索参数调优**：使用 `SearchRequest` 调整 `topK` 和相似度阈值，平衡召回率与噪声
3. **幻觉防范**：将检索片段作为唯一事实来源的提示策略，生产环境建议人工抽检
4. **模块选择**：根据需求选择合适的向量库（Cassandra、Chroma 等），逻辑均为 `VectorStore + ChatClient` 组合

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