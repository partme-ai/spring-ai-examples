# 10、Spring AI 入门实践：Spring AI 使用 Ollama Chat

## 一、项目概述

Ollama 是一个本地运行大语言模型的工具，支持多种开源模型，如 Llama 3、Mistral、Gemma 等。Spring AI 提供了与 Ollama 的完整集成，使得开发者可以轻松地在 Spring 应用中使用本地部署的大语言模型。本文将介绍如何使用 Spring AI 集成 Ollama 进行对话交互。

### 核心功能

- **本地模型部署**：无需云服务即可运行大语言模型
- **多种模型支持**：Llama 3、Mistral、Gemma、Phi 等多种模型
- **对话管理**：支持简单对话、多轮对话和系统提示
- **流式响应**：支持实时流式输出
- **参数调优**：可配置温度、top_p、max_tokens 等参数
- **OpenAI 兼容接口**：提供与 OpenAI 兼容的 API

### 适用场景

- 隐私敏感的企业应用
- 离线环境的 AI 应用
- 快速原型开发和测试
- 成本敏感的生产环境
- 定制化的 AI 助手

## 二、Ollama Chat 简介

Ollama 提供了简单易用的命令行工具和 API，用于在本地部署和运行开源大语言模型。Spring AI 在此基础上提供了更高级的抽象和便利功能。

### 常用模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Llama 3 | 综合性能好、推理能力强 | 通用对话、复杂推理 |
| Mistral | 速度快、响应迅速 | 快速响应、效率优先 |
| Gemma | Google 出品、代码能力强 | 代码生成、技术问答 |
| Phi | 轻量级、资源占用少 | 资源受限环境 |

### 核心概念

| 概念 | 说明 |
|------|------|
| ChatModel | 对话模型的统一抽象 |
| ChatClient | 高级对话客户端，简化交互 |
| ChatOptions | 对话参数配置 |
| Message | 对话消息（用户消息、助手消息、系统消息） |

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

# 拉取模型
ollama pull llama3.1:8b
ollama pull gemma3:4b
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-chat/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   └── service/
│   │   │                       └── ChatService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| ChatService | 对话业务逻辑 |
| ChatController | REST API 控制器 |

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
    name: spring-ai-ollama-chat
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: llama3.1:8b
          temperature: 0.7
          top-p: 0.9
          max-tokens: 2048

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    private final List<Message> conversationHistory = new ArrayList<>();
    
    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String simpleChat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
    
    public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userMessage)
        ));
        
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
    
    public String multiTurnChat(String userMessage) {
        conversationHistory.add(new UserMessage(userMessage));
        
        Prompt prompt = new Prompt(new ArrayList<>(conversationHistory));
        
        String response = chatClient.prompt(prompt)
                .call()
                .content();
        
        conversationHistory.add(new AssistantMessage(response));
        
        return response;
    }
    
    public void clearHistory() {
        conversationHistory.clear();
    }
    
    public Flux<String> streamingChat(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/simple")
    public Map<String, String> simpleChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.simpleChat(message);
        
        return Map.of(
            "message", message,
            "response", response
        );
    }
    
    @PostMapping("/system")
    public Map<String, String> chatWithSystemPrompt(@RequestBody Map<String, String> request) {
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有帮助的助手。");
        String userMessage = request.get("message");
        String response = chatService.chatWithSystemPrompt(systemPrompt, userMessage);
        
        return Map.of(
            "systemPrompt", systemPrompt,
            "message", userMessage,
            "response", response
        );
    }
    
    @PostMapping("/multiturn")
    public Map<String, String> multiTurnChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.multiTurnChat(message);
        
        return Map.of(
            "message", message,
            "response", response
        );
    }
    
    @DeleteMapping("/history")
    public Map<String, String> clearHistory() {
        chatService.clearHistory();
        return Map.of("status", "success", "message", "History cleared");
    }
    
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat/simple` | 简单的单次对话 |
| 系统提示 | POST | `/api/chat/system` | 带系统提示的对话 |
| 多轮对话 | POST | `/api/chat/multiturn` | 保持上下文的多轮对话 |
| 清除历史 | DELETE | `/api/chat/history` | 清除对话历史 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下 Spring AI"}'
```

响应：
```json
{
  "message": "你好，请介绍一下 Spring AI",
  "response": "Spring AI 是一个强大的 AI 集成框架..."
}
```

#### 系统提示对话

```bash
curl -X POST http://localhost:8080/api/chat/system \
  -H "Content-Type: application/json" \
  -d '{
    "systemPrompt": "你是一个专业的技术专家，擅长解释编程概念。",
    "message": "什么是微服务架构？"
  }'
```

#### 多轮对话

```bash
# 第一轮对话
curl -X POST http://localhost:8080/api/chat/multiturn \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，我叫张三"}'

# 第二轮对话
curl -X POST http://localhost:8080/api/chat/multiturn \
  -H "Content-Type: application/json" \
  -d '{"message": "我叫什么名字？"}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=请详细解释什么是人工智能"
```

#### 清除历史

```bash
curl -X DELETE http://localhost:8080/api/chat/history
```

## 八、部署方式

### 8.1 本地运行

```bash
# 确保 Ollama 正在运行
ollama serve

# 启动应用
cd spring-ai-ollama-chat
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-chat-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import json

class OllamaChatClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def simple_chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat/simple",
            json={"message": message}
        )
        return response.json()
    
    def chat_with_system_prompt(self, system_prompt, message):
        response = requests.post(
            f"{self.base_url}/api/chat/system",
            json={
                "systemPrompt": system_prompt,
                "message": message
            }
        )
        return response.json()
    
    def multi_turn_chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat/multiturn",
            json={"message": message}
        )
        return response.json()
    
    def clear_history(self):
        response = requests.delete(
            f"{self.base_url}/api/chat/history"
        )
        return response.json()

client = OllamaChatClient()

# 简单对话
result = client.simple_chat("你好，请介绍一下自己")
print(f"AI: {result['response']}")

# 带系统提示
result = client.chat_with_system_prompt(
    "你是一个专业的代码审查专家",
    "请看一下这段代码有什么问题"
)
print(f"AI: {result['response']}")

# 多轮对话
client.clear_history()
client.multi_turn_chat("我叫李四")
result = client.multi_turn_chat("我叫什么名字？")
print(f"AI: {result['response']}")
```

### 9.2 最佳实践

1. **模型选择**：根据任务复杂度选择合适的模型
2. **参数调优**：根据应用场景调整温度等参数
3. **对话管理**：合理管理对话历史，避免过长
4. **流式输出**：对于长响应使用流式输出提升用户体验
5. **错误处理**：实现适当的错误处理和重试机制

### 9.3 常用模型推荐

| 场景 | 推荐模型 | 理由 |
|------|----------|------|
| 通用对话 | Llama 3.1 | 综合性能好、推理能力强 |
| 快速响应 | Mistral | 速度快、延迟低 |
| 代码生成 | Gemma | 代码能力强、技术问答好 |
| 资源受限 | Phi | 轻量级、资源占用少 |

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama 是否正在运行
curl http://localhost:11434/api/tags

# 确认模型已下载
ollama list | grep llama
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-chat
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -X POST http://localhost:8080/api/chat/simple \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
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

**Q: 响应速度很慢怎么办？**

- 使用更小更快的模型（如 Mistral）
- 减少 max-tokens 参数
- 使用更好的硬件（GPU 加速）
- 考虑批量处理请求

**Q: 如何提升对话质量？**

- 使用更大更强大的模型（如 Llama 3.1:70b）
- 优化系统提示词
- 调整温度参数（创造性任务用更高温度，确定性任务用更低温度）
- 在提示中提供更多上下文信息

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Ollama Chat：https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
- Ollama 官方文档：https://ollama.ai/docs
- 示例模块：spring-ai-ollama-chat

## 十四、致谢

感谢 Ollama 团队和 Spring AI 团队提供的优秀工具，让本地大语言模型部署变得如此简单易用。
