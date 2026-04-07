# 1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）

## 概述

Spring AI 的文本生成功能基于 Chat Completion API，允许开发者通过简单的 API 调用来生成文本内容。本文将介绍如何使用 Spring AI 进行文本生成。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI API**（或其他兼容的 LLM API）

## 准备工作

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
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
spring.ai.openai.chat.options.temperature=0.7
```

## 基本使用

### 1. 简单文本生成

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextGenerationService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateText(String prompt) {
        ChatResponse response = chatClient.call(new Prompt(prompt));
        return response.getResult().getOutput().getContent();
    }
}
```

### 2. 使用 ChatClient Builder

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;

@Service
public class TextGenerationService {
    
    private final ChatClient chatClient;
    
    public TextGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String generateText(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
}
```

## 高级功能

### 1. 流式响应

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class StreamingTextGenerationService {
    
    @Autowired
    private ChatClient chatClient;
    
    public Flux<String> generateTextStream(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .stream()
            .content();
    }
}
```

### 2. 自定义参数

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvancedTextGenerationService {
    
    @Autowired
    private ChatModel chatModel;
    
    public String generateTextWithCustomOptions(String prompt) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
            .withModel("gpt-4")
            .withTemperature(0.8)
            .withMaxTokens(1000)
            .withTopP(0.9)
            .build();
        
        return chatModel.call(new Prompt(prompt, options))
            .getResult()
            .getOutput()
            .getContent();
    }
}
```

### 3. 多轮对话

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationService {
    
    @Autowired
    private ChatClient chatClient;
    
    private List<Message> conversationHistory = new ArrayList<>();
    
    public String chat(String userMessage) {
        conversationHistory.add(new UserMessage(userMessage));
        
        ChatResponse response = chatClient.call(new Prompt(conversationHistory));
        String assistantMessage = response.getResult().getOutput().getContent();
        
        conversationHistory.add(new AssistantMessage(assistantMessage));
        
        return assistantMessage;
    }
    
    public void clearConversation() {
        conversationHistory.clear();
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TextGenerationApplication {
    public static void main(String[] args) {
        SpringApplication.run(TextGenerationApplication.class, args);
    }
}

@Service
class TextGenerationService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateText(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
    
    public Flux<String> generateTextStream(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .stream()
            .content();
    }
}

@Service
class AdvancedTextGenerationService {
    
    @Autowired
    private ChatModel chatModel;
    
    public String generateTextWithCustomOptions(String prompt) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
            .withModel("gpt-4")
            .withTemperature(0.8)
            .withMaxTokens(1000)
            .withTopP(0.9)
            .build();
        
        return chatModel.call(new Prompt(prompt, options))
            .getResult()
            .getOutput()
            .getContent();
    }
}

@Service
class ConversationService {
    
    @Autowired
    private ChatClient chatClient;
    
    private List<Message> conversationHistory = new ArrayList<>();
    
    public String chat(String userMessage) {
        conversationHistory.add(new UserMessage(userMessage));
        
        ChatResponse response = chatClient.call(new Prompt(conversationHistory));
        String assistantMessage = response.getResult().getOutput().getContent();
        
        conversationHistory.add(new AssistantMessage(assistantMessage));
        
        return assistantMessage;
    }
    
    public void clearConversation() {
        conversationHistory.clear();
    }
}

@RestController
@RequestMapping("/api/text-generation")
class TextGenerationController {
    
    @Autowired
    private TextGenerationService textGenerationService;
    
    @Autowired
    private AdvancedTextGenerationService advancedTextGenerationService;
    
    @Autowired
    private ConversationService conversationService;
    
    @PostMapping("/generate")
    public String generateText(@RequestBody String prompt) {
        return textGenerationService.generateText(prompt);
    }
    
    @PostMapping("/generate-stream")
    public Flux<String> generateTextStream(@RequestBody String prompt) {
        return textGenerationService.generateTextStream(prompt);
    }
    
    @PostMapping("/generate-advanced")
    public String generateTextWithCustomOptions(@RequestBody String prompt) {
        return advancedTextGenerationService.generateTextWithCustomOptions(prompt);
    }
    
    @PostMapping("/chat")
    public String chat(@RequestBody String userMessage) {
        return conversationService.chat(userMessage);
    }
    
    @PostMapping("/clear")
    public void clearConversation() {
        conversationService.clearConversation();
    }
}
```

## 测试方法

1. **启动应用**：运行 `TextGenerationApplication` 类
2. **生成文本**：
   ```bash
   curl -X POST http://localhost:8080/api/text-generation/generate \
     -H "Content-Type: application/json" \
     -d "请写一首关于春天的诗"
   ```
3. **流式生成**：
   ```bash
   curl -X POST http://localhost:8080/api/text-generation/generate-stream \
     -H "Content-Type: application/json" \
     -d "请介绍一下人工智能的发展历史"
   ```
4. **多轮对话**：
   ```bash
   curl -X POST http://localhost:8080/api/text-generation/chat \
     -H "Content-Type: application/json" \
     -d "你好，我叫张三"
   ```

## 总结

Spring AI 的文本生成功能提供了简单易用的 API，支持基本的文本生成、流式响应、自定义参数和多轮对话等功能。通过合理配置和使用这些功能，可以快速构建各种基于文本生成的应用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference/chat)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Chat Model](https://docs.spring.io/spring-ai/reference/api/chatmodel.html) - ChatModel 接口详解
- [Spring AI ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html) - ChatClient 使用指南
- [Spring AI Prompt](https://docs.spring.io/spring-ai/reference/api/prompt.html) - 提示工程与消息模板

### 示例模块
- **`spring-ai-ollama-generation`** - 本仓库中的文本生成示例模块
  - 主类：`com.github.teachingai.ollama.SpringAiOllamaApplication`
  - 控制器：`com.github.teachingai.ollama.controller.GenerationController`
  - 路径：`/ai/generate`（同步）和 `/ai/generateStream`（流式）

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-ollama-generation

# 启动应用
mvn spring-boot:run

# 测试文本生成
curl "http://localhost:8080/ai/generate?message=用两句话介绍 Spring AI"

# 测试流式生成
curl -N "http://localhost:8080/ai/generateStream?message=写一首关于春天的诗"
```

### 注意事项
1. 本示例默认使用 Ollama 本地模型，如需使用 OpenAI 等云服务，请修改依赖和配置
2. 确保本地已安装并运行 Ollama，且已拉取相应模型（如 `gemma3:4b`）
3. 生产环境建议配置适当的超时、重试和监控机制