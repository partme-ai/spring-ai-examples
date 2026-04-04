# Spring AI 入门实践：Spring AI 使用 Ollama Chat

## 什么是 Ollama

Ollama 是一个本地运行大语言模型的工具，支持多种开源模型，如 Llama 3、Mistral、Gemini 等。

## 准备工作

### 1. 安装 Ollama

访问 [Ollama 官网](https://ollama.ai/) 下载并安装 Ollama。

### 2. 下载模型

使用以下命令下载模型：

```bash
ollama pull llama3
```

### 3. 添加 Spring AI 依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama</artifactId>
</dependency>
```

## 配置 Ollama Chat

### 1. 配置属性

在 `application.properties` 中添加：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3
```

### 2. 创建 Ollama Chat Client Bean

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
}
```

## 基本操作

### 1. 简单对话

```java
@Autowired
private ChatClient chatClient;

public void simpleChat() {
    ChatResponse response = chatClient.call(new Prompt("你好，我叫张三"));
    System.out.println("AI: " + response.getResult().getOutput().getContent());
}
```

### 2. 多轮对话

```java
public void multiTurnChat() {
    List<Message> messages = new ArrayList<>();
    messages.add(new UserMessage("你好，我叫李四"));
    
    // 第一轮对话
    ChatResponse response1 = chatClient.call(new Prompt(messages));
    String reply1 = response1.getResult().getOutput().getContent();
    System.out.println("AI: " + reply1);
    messages.add(new AssistantMessage(reply1));
    
    // 第二轮对话
    messages.add(new UserMessage("我喜欢编程，你呢？"));
    ChatResponse response2 = chatClient.call(new Prompt(messages));
    String reply2 = response2.getResult().getOutput().getContent();
    System.out.println("AI: " + reply2);
}
```

### 3. 系统提示

```java
public void chatWithSystemPrompt() {
    List<Message> messages = new ArrayList<>();
    messages.add(new SystemMessage("你是一个专业的技术助手，擅长解答编程问题。"));
    messages.add(new UserMessage("什么是 Spring Boot？"));
    
    ChatResponse response = chatClient.call(new Prompt(messages));
    System.out.println("AI: " + response.getResult().getOutput().getContent());
}
```

## 高级特性

### 1. 自定义模型参数

```java
@Bean
public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
    return ChatClient.builder(ollamaChatModel)
        .withDefaultOptions(ChatOptions.builder()
            .withTemperature(0.7f)
            .withTopP(0.9f)
            .withMaxTokens(1024)
            .build())
        .build();
}
```

### 2. 流式响应

```java
public void streamingChat() {
    Prompt prompt = new Prompt("请解释什么是微服务架构？");
    
    chatClient.stream(prompt).subscribe(
        chatResponse -> {
            String content = chatResponse.getResult().getOutput().getContent();
            if (content != null && !content.isEmpty()) {
                System.out.print(content);
            }
        },
        error -> System.err.println("Error: " + error),
        () -> System.out.println("\nStream completed")
    );
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OllamaChatService {

    @Autowired
    private ChatClient chatClient;

    public void demo() {
        // 简单对话
        System.out.println("=== 简单对话 ===");
        ChatResponse response = chatClient.call(new Prompt("你好，我叫张三"));
        System.out.println("AI: " + response.getResult().getOutput().getContent());
        
        // 多轮对话
        System.out.println("\n=== 多轮对话 ===");
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("你好，我叫李四"));
        
        ChatResponse response1 = chatClient.call(new Prompt(messages));
        String reply1 = response1.getResult().getOutput().getContent();
        System.out.println("AI: " + reply1);
        messages.add(new AssistantMessage(reply1));
        
        messages.add(new UserMessage("我喜欢编程，你呢？"));
        ChatResponse response2 = chatClient.call(new Prompt(messages));
        String reply2 = response2.getResult().getOutput().getContent();
        System.out.println("AI: " + reply2);
        
        // 系统提示
        System.out.println("\n=== 系统提示 ===");
        List<Message> systemMessages = new ArrayList<>();
        systemMessages.add(new SystemMessage("你是一个专业的技术助手，擅长解答编程问题。"));
        systemMessages.add(new UserMessage("什么是 Spring Boot？"));
        
        ChatResponse systemResponse = chatClient.call(new Prompt(systemMessages));
        System.out.println("AI: " + systemResponse.getResult().getOutput().getContent());
    }
}
```

## 总结

通过 Spring AI 与 Ollama 的集成，我们可以在本地运行大语言模型，实现离线的 AI 对话功能。这为需要隐私保护或离线部署的应用提供了理想的解决方案。

## 相关资源

- [Ollama 官方文档](https://ollama.ai/docs)
- [Spring AI Ollama 文档](https://docs.spring.io/spring-ai/docs/current/reference/html/index.html#ollama)