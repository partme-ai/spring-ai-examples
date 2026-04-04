# Spring AI 入门实践：Spring AI 生成式人工智能堆栈（GenAI Stack） 的 GenAI Stack Neo4j、LangChain、Ollama

## 什么是 GenAI Stack

GenAI Stack 是一个集成了多种 AI 技术的完整堆栈，包括向量数据库、语言模型、框架等，用于构建端到端的生成式 AI 应用。

## 技术组件

### 1. Neo4j

Neo4j 是一个图形数据库，支持向量搜索，用于存储和查询复杂的知识图谱。

### 2. LangChain

LangChain 是一个用于构建 LLM 应用的框架，提供了链、代理、记忆等功能。

### 3. Ollama

Ollama 是一个本地运行大语言模型的工具，支持多种开源模型。

## 准备工作

### 1. 安装必要的软件

- **Neo4j**：使用 Docker 启动
  ```bash
  docker run -d --name neo4j -p 7474:7474 -p 7687:7687 -e NEO4J_AUTH=neo4j/password neo4j:5.15
  ```

- **Ollama**：访问 [Ollama 官网](https://ollama.ai/) 下载并安装

- **下载模型**：
  ```bash
  ollama pull llama3
  ollama pull mxbai-embed-large
  ```

### 2. 添加依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-neo4j-store</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-langchain4j</artifactId>
</dependency>
<dependency>
    <groupId>org.neo4j.driver</groupId>
    <artifactId>neo4j-java-driver</artifactId>
</dependency>
```

## 配置 GenAI Stack

### 1. 配置属性

在 `application.properties` 中添加：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3
spring.ai.ollama.embedding.model=mxbai-embed-large

# Neo4j 配置
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password
```

### 2. 创建 Bean

```java
@Bean
public EmbeddingClient embeddingClient() {
    return new OllamaEmbeddingModel(
        new OllamaApi("http://localhost:11434"),
        OllamaEmbeddingOptions.builder()
            .withModel("mxbai-embed-large")
            .build()
    );
}

@Bean
public VectorStore vectorStore(EmbeddingClient embeddingClient, Neo4jClient neo4jClient) {
    return new Neo4jVectorStore(
        embeddingClient,
        neo4jClient,
        "vector-index",
        "Document",
        "embedding",
        "content"
    );
}

@Bean
public ChatClient chatClient() {
    return ChatClient.builder(
        new OllamaChatModel(
            new OllamaApi("http://localhost:11434"),
            OllamaChatOptions.builder()
                .withModel("llama3")
                .build()
        )
    ).build();
}
```

## 构建 RAG 应用

### 1. 加载文档

```java
@Autowired
private VectorStore vectorStore;

public void loadDocuments() {
    List<Document> documents = new ArrayList<>();
    documents.add(new Document("Spring Boot 是一个快速开发框架，用于构建生产级别的应用。", Map.of("category", "framework")));
    documents.add(new Document("Spring AI 提供了与各种 AI 模型的集成，包括 OpenAI、Ollama 等。", Map.of("category", "ai")));
    documents.add(new Document("Neo4j 是一个图形数据库，支持向量搜索和复杂关系查询。", Map.of("category", "database")));
    documents.add(new Document("LangChain 是一个用于构建 LLM 应用的框架，提供了链、代理等功能。", Map.of("category", "framework")));
    documents.add(new Document("Ollama 是一个本地运行大语言模型的工具，支持多种开源模型。", Map.of("category", "tool")));
    
    vectorStore.add(documents);
    System.out.println("文档加载完成");
}
```

### 2. 实现 RAG

```java
@Autowired
private ChatClient chatClient;

@Autowired
private VectorStore vectorStore;

public String ragQuery(String question) {
    // 检索相关文档
    List<Document> relevantDocuments = vectorStore.similaritySearch(question, 3);
    
    // 构建上下文
    StringBuilder context = new StringBuilder();
    context.append("相关信息：\n");
    for (Document document : relevantDocuments) {
        context.append("- " + document.getContent() + "\n");
    }
    
    // 构建提示
    String prompt = "根据以下相关信息，回答问题：\n" + 
                   context.toString() + "\n" + 
                   "问题：" + question;
    
    // 调用模型
    ChatResponse response = chatClient.call(new Prompt(prompt));
    return response.getResult().getOutput().getContent();
}
```

### 3. 使用 LangChain

```java
@Bean
public Chain ragChain(ChatClient chatClient, VectorStore vectorStore) {
    return Chain.builder()
        .retriever(vectorStore.similaritySearchRetriever(3))
        .chatModel(new ChatClientChatModel(chatClient))
        .promptTemplate(new PromptTemplate("根据以下相关信息，回答问题：\n{context}\n问题：{question}"))
        .build();
}

public String langChainQuery(String question) {
    return ragChain.run(Map.of("question", question));
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.langchain4j.Chain;
import org.springframework.ai.langchain4j.PromptTemplate;
import org.springframework.ai.ollama.OllamaApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaChatOptions;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingOptions;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GenAIStackService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private Chain ragChain;

    public void demo() {
        // 加载文档
        loadDocuments();
        
        // 测试 RAG 查询
        System.out.println("=== 测试 RAG 查询 ===");
        String question1 = "什么是 Spring AI？";
        String answer1 = ragQuery(question1);
        System.out.println("问题: " + question1);
        System.out.println("回答: " + answer1);
        
        String question2 = "Neo4j 有什么特点？";
        String answer2 = ragQuery(question2);
        System.out.println("\n问题: " + question2);
        System.out.println("回答: " + answer2);
        
        // 测试 LangChain
        System.out.println("\n=== 测试 LangChain ===");
        String question3 = "Ollama 是做什么的？";
        String answer3 = langChainQuery(question3);
        System.out.println("问题: " + question3);
        System.out.println("回答: " + answer3);
    }

    public void loadDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("Spring Boot 是一个快速开发框架，用于构建生产级别的应用。", Map.of("category", "framework")));
        documents.add(new Document("Spring AI 提供了与各种 AI 模型的集成，包括 OpenAI、Ollama 等。", Map.of("category", "ai")));
        documents.add(new Document("Neo4j 是一个图形数据库，支持向量搜索和复杂关系查询。", Map.of("category", "database")));
        documents.add(new Document("LangChain 是一个用于构建 LLM 应用的框架，提供了链、代理等功能。", Map.of("category", "framework")));
        documents.add(new Document("Ollama 是一个本地运行大语言模型的工具，支持多种开源模型。", Map.of("category", "tool")));
        
        vectorStore.add(documents);
        System.out.println("文档加载完成");
    }

    public String ragQuery(String question) {
        List<Document> relevantDocuments = vectorStore.similaritySearch(question, 3);
        
        StringBuilder context = new StringBuilder();
        context.append("相关信息：\n");
        for (Document document : relevantDocuments) {
            context.append("- " + document.getContent() + "\n");
        }
        
        String prompt = "根据以下相关信息，回答问题：\n" + 
                       context.toString() + "\n" + 
                       "问题：" + question;
        
        ChatResponse response = chatClient.call(new Prompt(prompt));
        return response.getResult().getOutput().getContent();
    }

    public String langChainQuery(String question) {
        return ragChain.run(Map.of("question", question));
    }
}
```

## 总结

GenAI Stack 集成了 Neo4j、LangChain 和 Ollama 等技术，为构建端到端的生成式 AI 应用提供了完整的解决方案。通过 Spring AI 的集成，我们可以轻松构建强大的 RAG 应用，实现智能的信息检索和生成。

## 相关资源

- [Neo4j 官方文档](https://neo4j.com/docs/)
- [LangChain 官方文档](https://docs.langchain.com/)
- [Ollama 官方文档](https://ollama.ai/docs)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)