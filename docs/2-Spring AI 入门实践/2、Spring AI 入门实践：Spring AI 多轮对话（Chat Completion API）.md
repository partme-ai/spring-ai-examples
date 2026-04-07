# Spring AI 入门实践：Spring AI 多轮对话（Chat Completion API）

## 什么是多轮对话

多轮对话是指与 AI 模型进行连续的、上下文相关的交互。在 Spring AI 中，我们可以通过 `ChatClient` 和 `ChatResponse` 来实现多轮对话。

## 基本概念

- **ChatClient**：用于与 AI 模型进行交互的客户端
- **Prompt**：包含用户输入和系统指令的提示
- **ChatResponse**：包含 AI 模型响应的对象
- **Message**：对话中的单条消息

## 实现多轮对话

### 1. 添加依赖

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai</artifactId>
</dependency>
```

### 2. 配置 OpenAI API 密钥

在 `application.properties` 中添加：

```properties
spring.ai.openai.api-key=YOUR_API_KEY
```

### 3. 创建 ChatClient Bean

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
}
```

### 4. 实现多轮对话

```java
@Autowired
private ChatClient chatClient;

public void multiTurnConversation() {
    // 初始消息
    List<Message> messages = new ArrayList<>();
    messages.add(new UserMessage("你好，我叫张三"));
    
    // 第一轮对话
    ChatResponse response1 = chatClient.call(new Prompt(messages));
    String reply1 = response1.getResult().getOutput().getContent();
    System.out.println("AI: " + reply1);
    
    // 添加 AI 回复到消息列表
    messages.add(new AssistantMessage(reply1));
    
    // 第二轮对话
    messages.add(new UserMessage("我喜欢打篮球，你呢？"));
    ChatResponse response2 = chatClient.call(new Prompt(messages));
    String reply2 = response2.getResult().getOutput().getContent();
    System.out.println("AI: " + reply2);
}
```

## 高级特性

### 1. 消息历史管理

可以使用 `MessageHistory` 来管理对话历史：

```java
@Bean
public MessageHistory messageHistory() {
    return new SimpleMessageHistory();
}

public void conversationWithHistory() {
    messageHistory().add(new UserMessage("你好，我叫李四"));
    
    ChatResponse response = chatClient.call(new Prompt(messageHistory().getMessages()));
    String reply = response.getResult().getOutput().getContent();
    messageHistory().add(new AssistantMessage(reply));
    
    // 后续对话
    messageHistory().add(new UserMessage("我喜欢编程"));
    response = chatClient.call(new Prompt(messageHistory().getMessages()));
    reply = response.getResult().getOutput().getContent();
    messageHistory().add(new AssistantMessage(reply));
}
```

### 2. 系统提示

可以添加系统提示来指导 AI 行为：

```java
List<Message> messages = new ArrayList<>();
messages.add(new SystemMessage("你是一个友好的助手，总是用简洁的语言回答问题。"));
messages.add(new UserMessage("你好，我叫王五"));

ChatResponse response = chatClient.call(new Prompt(messages));
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
public class ConversationService {

    @Autowired
    private ChatClient chatClient;

    public void startConversation() {
        List<Message> messages = new ArrayList<>();
        
        // 添加系统提示
        messages.add(new SystemMessage("你是一个专业的技术助手，擅长解答编程问题。"));
        
        // 第一轮对话
        messages.add(new UserMessage("什么是 Spring Boot？"));
        ChatResponse response1 = chatClient.call(new Prompt(messages));
        String reply1 = response1.getResult().getOutput().getContent();
        System.out.println("AI: " + reply1);
        messages.add(new AssistantMessage(reply1));
        
        // 第二轮对话
        messages.add(new UserMessage("它和 Spring MVC 有什么区别？"));
        ChatResponse response2 = chatClient.call(new Prompt(messages));
        String reply2 = response2.getResult().getOutput().getContent();
        System.out.println("AI: " + reply2);
        messages.add(new AssistantMessage(reply2));
        
        // 第三轮对话
        messages.add(new UserMessage("如何快速上手 Spring Boot？"));
        ChatResponse response3 = chatClient.call(new Prompt(messages));
        String reply3 = response3.getResult().getOutput().getContent();
        System.out.println("AI: " + reply3);
    }
}
```

## 总结

通过 Spring AI 的 `ChatClient` 和消息管理，我们可以轻松实现多轮对话功能。这使得我们能够构建更加自然、连贯的 AI 交互应用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference/chat)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关多轮对话的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Chat Model](https://docs.spring.io/spring-ai/reference/api/chatmodel.html) - ChatModel 接口详解
- [Spring AI ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html) - ChatClient 使用指南
- [Spring AI Prompt](https://docs.spring.io/spring-ai/reference/api/prompt.html) - 提示工程与消息模板
- [Spring AI Chat Memory](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory) - 对话记忆管理

### 示例模块
- **`spring-ai-ollama-chat`** - 本仓库中的多轮对话示例模块
  - 主类：`com.github.teachingai.ollama.SpringAiOllamaApplication`
  - 控制器：`com.github.teachingai.ollama.controller.ChatController`
  - 请求 DTO：`com.github.teachingai.ollama.request.ApiRequest`
  - 路径：`/v1/chat/completions`（流式接口）

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-ollama-chat

# 启动应用
mvn spring-boot:run

# 测试多轮对话（流式响应）
curl -N -X POST "http://localhost:8080/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemma3:4b",
    "messages": [
      {"role": "system", "content": "你是一个简洁助手。"},
      {"role": "user", "content": "用两句话介绍 Spring AI。"}
    ],
    "temperature": 0.7
  }'
```

### 注意事项
1. 本示例默认使用 Ollama 本地模型，如需使用 OpenAI 等云服务，请修改依赖和配置
2. 确保本地已安装并运行 Ollama，且已拉取相应模型（如 `gemma3:4b`）
3. 流式接口注意背压与超时，生产环境建议配合网关限流
4. 模型名优先在配置中设默认值，请求中允许覆盖以便 A/B 测试