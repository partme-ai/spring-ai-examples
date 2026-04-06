# 2、Spring AI 入门实践：Spring AI 多轮对话（Chat Completion API）

## 一、项目概述

多轮对话是构建智能对话应用的核心功能。与单次文本生成不同，多轮对话能够记住之前的交互内容，让对话更加自然连贯。本文将介绍如何使用 Spring AI 实现多轮对话功能，包括对话历史管理、系统提示设置以及流式响应等特性。

### 核心功能

- **对话历史管理**：维护完整的对话上下文
- **系统提示**：设置 AI 的角色和行为规范
- **消息类型支持**：支持用户、助手、系统等多种消息类型
- **会话隔离**：支持多用户独立会话
- **流式响应**：可选的流式输出模式

### 适用场景

- 智能客服机器人
- 个人助理应用
- 在线教育辅导
- 技术支持对话系统
- 游戏 NPC 交互

## 二、多轮对话原理简介

多轮对话的核心是维护对话历史。每次交互时，将之前的所有消息连同新消息一起发送给模型，模型就能基于完整的上下文生成回复。

### 消息类型

| 类型 | 说明 | 用途 |
|------|------|------|
| 系统消息 | 设置 AI 的角色、能力范围和回答风格 | 在对话开始时设置，指导 AI 行为 |
| 用户消息 | 用户的输入内容 | 用户的问题或指令 |
| 助手消息 | AI 的历史回复 | 保持对话连贯性 |

### 工作流程

1. 初始化对话时，添加系统提示
2. 用户发送消息，追加到对话历史
3. 将完整历史发送给模型
4. 收到模型回复，追加到对话历史
5. 重复步骤 2-4，直到对话结束

## 三、环境准备

### 3.1 开发环境

确保已安装以下软件：

- JDK 17 或更高版本
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（用于本地模型部署）

### 3.2 Ollama 配置

如果还没有安装 Ollama，可以按以下步骤操作：

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取模型，推荐 gemma3:4b 或 llama3.1:8b
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
│   │   │                   ├── service/
│   │   │                   │   └── ChatService.java
│   │   │                   └── session/
│   │   │                       └── ChatSession.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `SpringAiOllamaApplication` | 应用启动入口 |
| `ChatController` | 提供 REST API |
| `ChatService` | 对话业务逻辑 |
| `ChatSession` | 单个会话的状态管理 |

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
          model: gemma3:4b
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 会话管理类

```java
package com.github.partmeai.ollama.session;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatSession {
    
    private final String sessionId;
    private final List<Message> messages;
    
    public ChatSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.messages = new ArrayList<>();
    }
    
    public ChatSession(String systemPrompt) {
        this();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            this.messages.add(new SystemMessage(systemPrompt));
        }
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void addUserMessage(String content) {
        messages.add(new UserMessage(content));
    }
    
    public void addAssistantMessage(String content) {
        messages.add(new AssistantMessage(content));
    }
    
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public void clear() {
        messages.clear();
    }
    
    public int getMessageCount() {
        return messages.size();
    }
}
```

### 6.2 会话管理器

```java
package com.github.partmeai.ollama.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {
    
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    
    public ChatSession createSession() {
        ChatSession session = new ChatSession();
        sessions.put(session.getSessionId(), session);
        return session;
    }
    
    public ChatSession createSession(String systemPrompt) {
        ChatSession session = new ChatSession(systemPrompt);
        sessions.put(session.getSessionId(), session);
        return session;
    }
    
    public ChatSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    public boolean exists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
```

### 6.3 对话服务

```java
package com.github.partmeai.ollama.service;

import com.github.partmeai.ollama.session.ChatSession;
import com.github.partmeai.ollama.session.ChatSessionManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    private final ChatSessionManager sessionManager;
    
    public ChatService(ChatClient.Builder chatClientBuilder,
                       ChatSessionManager sessionManager) {
        this.chatClient = chatClientBuilder.build();
        this.sessionManager = sessionManager;
    }
    
    public String createSession() {
        ChatSession session = sessionManager.createSession();
        return session.getSessionId();
    }
    
    public String createSession(String systemPrompt) {
        ChatSession session = sessionManager.createSession(systemPrompt);
        return session.getSessionId();
    }
    
    public String chat(String sessionId, String userMessage) {
        ChatSession session = sessionManager.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        
        session.addUserMessage(userMessage);
        
        String assistantMessage = chatClient.prompt()
                .messages(session.getMessages())
                .call()
                .content();
        
        session.addAssistantMessage(assistantMessage);
        
        return assistantMessage;
    }
    
    public Flux<String> chatStream(String sessionId, String userMessage) {
        ChatSession session = sessionManager.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("会话不存在: " + sessionId);
        }
        
        session.addUserMessage(userMessage);
        
        Flux<String> responseStream = chatClient.prompt()
                .messages(session.getMessages())
                .stream()
                .content();
        
        StringBuilder responseBuilder = new StringBuilder();
        return responseStream.doOnNext(chunk -> responseBuilder.append(chunk))
                .doOnComplete(() -> session.addAssistantMessage(responseBuilder.toString()));
    }
    
    public void clearSession(String sessionId) {
        ChatSession session = sessionManager.getSession(sessionId);
        if (session != null) {
            session.clear();
        }
    }
    
    public void endSession(String sessionId) {
        sessionManager.removeSession(sessionId);
    }
}
```

### 6.4 REST 控制器

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
    
    @PostMapping("/session")
    public Map<String, String> createSession(@RequestBody(required = false) Map<String, String> request) {
        String systemPrompt = request != null ? request.get("systemPrompt") : null;
        String sessionId = systemPrompt != null 
                ? chatService.createSession(systemPrompt)
                : chatService.createSession();
        return Map.of("sessionId", sessionId);
    }
    
    @PostMapping("/{sessionId}/message")
    public Map<String, String> sendMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = chatService.chat(sessionId, message);
        return Map.of("response", response);
    }
    
    @GetMapping(value = "/{sessionId}/message/stream", produces = "text/event-stream")
    public Flux<String> sendMessageStream(
            @PathVariable String sessionId,
            @RequestParam String message) {
        return chatService.chatStream(sessionId, message);
    }
    
    @DeleteMapping("/{sessionId}")
    public void clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
    }
    
    @DeleteMapping("/{sessionId}/end")
    public void endSession(@PathVariable String sessionId) {
        chatService.endSession(sessionId);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建会话 | POST | `/api/chat/session` | 创建新的对话会话 |
| 发送消息 | POST | `/api/chat/{sessionId}/message` | 同步发送消息 |
| 流式消息 | GET | `/api/chat/{sessionId}/message/stream` | 流式发送消息 |
| 清空会话 | DELETE | `/api/chat/{sessionId}` | 清空会话历史 |
| 结束会话 | DELETE | `/api/chat/{sessionId}/end` | 结束并删除会话 |

### 7.2 接口详情

#### 创建会话

```bash
curl -X POST http://localhost:8080/api/chat/session \
  -H "Content-Type: application/json" \
  -d '{
    "systemPrompt": "你是一个专业的技术助手"
  }'
```

响应：
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 发送消息

```bash
curl -X POST http://localhost:8080/api/chat/550e8400-e29b-41d4-a716-446655440000/message \
  -H "Content-Type: application/json" \
  -d '{
    "message": "什么是 Spring Boot？"
  }'
```

#### 流式消息

```bash
curl -N "http://localhost:8080/api/chat/550e8400-e29b-41d4-a716-446655440000/message/stream?message=介绍一下Spring AI"
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-ollama-chat
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-chat-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 命令行测试

```bash
# 创建会话
SESSION_ID=$(curl -s -X POST http://localhost:8080/api/chat/session | jq -r .sessionId)
echo "会话ID: $SESSION_ID"

# 第一轮对话
curl -X POST "http://localhost:8080/api/chat/$SESSION_ID/message" \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，我叫小明"}'

# 第二轮对话
curl -X POST "http://localhost:8080/api/chat/$SESSION_ID/message" \
  -H "Content-Type: application/json" \
  -d '{"message": "我叫什么名字？"}'

# 结束会话
curl -X DELETE "http://localhost:8080/api/chat/$SESSION_ID/end"
```

### 9.2 Python 客户端

```python
import requests
import json

class ChatClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session_id = None
    
    def create_session(self, system_prompt=None):
        data = {"systemPrompt": system_prompt} if system_prompt else None
        response = requests.post(f"{self.base_url}/api/chat/session", json=data)
        self.session_id = response.json()["sessionId"]
        return self.session_id
    
    def send_message(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat/{self.session_id}/message",
            json={"message": message}
        )
        return response.json()["response"]
    
    def end_session(self):
        requests.delete(f"{self.base_url}/api/chat/{self.session_id}/end")

# 使用示例
client = ChatClient()
client.create_session("你是一个乐于助人的助手")

print(client.send_message("你好"))
print(client.send_message("我刚才说了什么？"))

client.end_session()
```

## 十、运行项目

### 10.1 前置检查

确保 Ollama 正在运行：
```bash
curl http://localhost:11434/api/tags
```

### 10.2 启动应用

```bash
mvn spring-boot:run
```

### 10.3 简单测试

打开浏览器访问健康检查：
```
http://localhost:8080/actuator/health
```

## 十一、常见问题

### 11.1 会话管理

**Q: 会话数据会永久保存吗？**

当前实现使用内存存储，应用重启后会话会丢失。生产环境建议使用 Redis 或数据库持久化会话。

**Q: 如何限制会话时长？**

可以在 `ChatSessionManager` 中添加定时任务，清理长时间不活跃的会话。

### 11.2 性能问题

**Q: 对话历史太长导致响应变慢怎么办？**

可以实现对话历史截断策略，只保留最近的 N 条消息，或者对旧消息进行摘要。

**Q: 如何支持并发对话？**

当前的 `ConcurrentHashMap` 已经支持并发访问，Spring Boot 默认的线程池也能处理并发请求。

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI ChatClient 文档：https://docs.spring.io/spring-ai/reference/api/chatclient.html
- Ollama 官方网站：https://ollama.com
- 示例代码仓库：spring-ai-ollama-chat 模块

## 十四、致谢

感谢 Spring AI 团队提供的优秀框架，感谢 Ollama 团队让本地部署大模型变得如此简单。
