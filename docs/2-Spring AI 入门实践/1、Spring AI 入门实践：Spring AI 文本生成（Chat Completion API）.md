# 1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）

## 一、项目概述

Spring AI 的文本生成功能基于 Chat Completion API，允许开发者通过简单的 API 调用来生成文本内容。本文档将详细介绍如何使用 Spring AI 进行文本生成，包括基本文本生成、流式响应、自定义参数配置以及多轮对话等功能。

### 核心功能

- **基本文本生成**：通过简单的 API 调用生成文本内容
- **流式响应**：支持 Server-Sent Events (SSE) 实现文本流式输出
- **自定义参数**：灵活配置模型、温度、最大 tokens 等参数
- **多轮对话**：维护对话上下文，实现连续的交互式对话

### 适用场景

- 智能客服系统
- 内容生成工具
- 代码辅助生成
- 对话式应用
- 文档摘要生成

## 二、Chat Completion API 简介

Chat Completion API 是大语言模型的核心接口，用于完成对话式文本生成任务。Spring AI 对该 API 进行了封装，提供了统一的抽象层，使得开发者可以无缝切换不同的 LLM 提供商。

### 工作原理

Chat Completion API 的工作流程如下：

1. 开发者构建包含消息列表的请求
2. 请求发送到 LLM 服务
3. 模型根据对话历史生成响应
4. 返回包含生成文本的响应

### 消息类型

| 消息类型 | 说明 | 用途 |
|---------|------|------|
| System | 系统消息 | 设置模型的行为和角色 |
| User | 用户消息 | 用户的输入内容 |
| Assistant | 助手消息 | 模型的历史回复 |
| Function | 函数消息 | 工具调用的返回结果 |

## 三、环境准备

### 3.1 软件要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | Spring Boot 3.x 要求 |
| Maven | 3.8+ | 项目构建工具 |
| IDE | IntelliJ IDEA / Eclipse | 推荐 IntelliJ IDEA |
| Ollama | 最新版 | 本地大模型运行环境（可选） |

### 3.2 Ollama 环境配置（可选）

如果使用 Ollama 本地模型，需要先安装并配置 Ollama：

```bash
# 1. 安装 Ollama（根据操作系统选择）
# macOS/Linux:
curl -fsSL https://ollama.com/install.sh | sh

# Windows:
# 下载安装包：https://ollama.com/download

# 2. 启动 Ollama 服务
ollama serve

# 3. 拉取模型（推荐使用 gemma3:4b 或 llama3.1:8b）
ollama pull gemma3:4b

# 4. 验证模型
ollama list
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-generation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java    # 主启动类
│   │   │                   ├── controller/
│   │   │                   │   └── GenerationController.java      # REST 控制器
│   │   │                   └── service/
│   │   │                       ├── TextGenerationService.java      # 文本生成服务
│   │   │                       └── ConversationService.java        # 对话服务
│   │   └── resources/
│   │       ├── application.yml                                    # 应用配置
│   │       └── static/
│   └── test/
│       └── java/
└── pom.xml                                                          # Maven 配置
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `SpringAiOllamaApplication` | Spring Boot 应用主入口 |
| `GenerationController` | 提供 REST API 接口 |
| `TextGenerationService` | 封装文本生成逻辑 |
| `ConversationService` | 管理多轮对话上下文 |

## 五、核心配置

### 5.1 Maven 依赖配置

在 `pom.xml` 中添加以下依赖：

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

在 `src/main/resources/application.yml` 中配置：

```yaml
spring:
  application:
    name: spring-ai-ollama-generation
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: gemma3:4b
          temperature: 0.7
          top-p: 0.9

server:
  port: 8080
  servlet:
    context-path: /

# Actuator 配置（可选）
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 5.3 配置参数说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `spring.ai.ollama.base-url` | Ollama 服务地址 | http://localhost:11434 |
| `spring.ai.ollama.chat.options.model` | 使用的模型名称 | gemma3:4b |
| `spring.ai.ollama.chat.options.temperature` | 温度参数（0-2） | 0.7 |
| `spring.ai.ollama.chat.options.top-p` | 核采样参数 | 0.9 |

## 六、代码实现详解

### 6.1 主启动类

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

### 6.2 文本生成服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TextGenerationService {
    
    private final ChatClient chatClient;
    
    public TextGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    /**
     * 同步文本生成
     * @param prompt 用户输入的提示词
     * @return 生成的文本
     */
    public String generateText(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    
    /**
     * 流式文本生成
     * @param prompt 用户输入的提示词
     * @return 流式响应
     */
    public Flux<String> generateTextStream(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
```

### 6.3 高级文本生成服务（自定义参数）

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Service
public class AdvancedTextGenerationService {
    
    private final ChatModel chatModel;
    
    public AdvancedTextGenerationService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    /**
     * 使用自定义参数生成文本
     * @param prompt 用户输入的提示词
     * @param temperature 温度参数
     * @param maxTokens 最大 token 数
     * @return 生成的文本
     */
    public String generateTextWithCustomOptions(String prompt, 
                                                  double temperature,
                                                  int maxTokens) {
        OllamaOptions options = OllamaOptions.builder()
                .withTemperature(temperature)
                .withNumPredict(maxTokens)
                .build();
        
        return chatModel.call(new Prompt(prompt, options))
                .getResult()
                .getOutput()
                .getContent();
    }
}
```

### 6.4 对话服务（多轮对话）

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationService {
    
    private final ChatClient chatClient;
    private final List<Message> conversationHistory = new ArrayList<>();
    
    public ConversationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    /**
     * 发送消息并获取回复
     * @param userMessage 用户消息
     * @return AI 回复
     */
    public String chat(String userMessage) {
        conversationHistory.add(new UserMessage(userMessage));
        
        String assistantMessage = chatClient.call(new Prompt(conversationHistory))
                .getResult()
                .getOutput()
                .getContent();
        
        conversationHistory.add(new AssistantMessage(assistantMessage));
        
        return assistantMessage;
    }
    
    /**
     * 清空对话历史
     */
    public void clearConversation() {
        conversationHistory.clear();
    }
    
    /**
     * 获取对话历史
     * @return 对话历史列表
     */
    public List<Message> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
}
```

### 6.5 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.AdvancedTextGenerationService;
import com.github.partmeai.ollama.service.ConversationService;
import com.github.partmeai.ollama.service.TextGenerationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class GenerationController {
    
    private final TextGenerationService textGenerationService;
    private final AdvancedTextGenerationService advancedTextGenerationService;
    private final ConversationService conversationService;
    
    public GenerationController(TextGenerationService textGenerationService,
                                  AdvancedTextGenerationService advancedTextGenerationService,
                                  ConversationService conversationService) {
        this.textGenerationService = textGenerationService;
        this.advancedTextGenerationService = advancedTextGenerationService;
        this.conversationService = conversationService;
    }
    
    /**
     * 同步文本生成
     */
    @GetMapping("/generate")
    public String generate(@RequestParam String message) {
        return textGenerationService.generateText(message);
    }
    
    /**
     * 流式文本生成
     */
    @GetMapping(value = "/generateStream", produces = "text/event-stream")
    public Flux<String> generateStream(@RequestParam String message) {
        return textGenerationService.generateTextStream(message);
    }
    
    /**
     * 自定义参数文本生成
     */
    @PostMapping("/generateAdvanced")
    public String generateAdvanced(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        double temperature = request.containsKey("temperature") 
                ? ((Number) request.get("temperature")).doubleValue() 
                : 0.7;
        int maxTokens = request.containsKey("maxTokens")
                ? ((Number) request.get("maxTokens")).intValue()
                : 512;
        
        return advancedTextGenerationService.generateTextWithCustomOptions(
                message, temperature, maxTokens);
    }
    
    /**
     * 多轮对话
     */
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return conversationService.chat(message);
    }
    
    /**
     * 清空对话历史
     */
    @PostMapping("/chat/clear")
    public void clearChat() {
        conversationService.clearConversation();
    }
    
    /**
     * 获取对话历史
     */
    @GetMapping("/chat/history")
    public List<?> getChatHistory() {
        return conversationService.getConversationHistory();
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 同步生成 | GET | `/ai/generate` | 同步文本生成 |
| 流式生成 | GET | `/ai/generateStream` | 流式文本生成 |
| 高级生成 | POST | `/ai/generateAdvanced` | 自定义参数生成 |
| 对话 | POST | `/ai/chat` | 多轮对话 |
| 清空对话 | POST | `/ai/chat/clear` | 清空对话历史 |
| 对话历史 | GET | `/ai/chat/history` | 获取对话历史 |

### 7.2 接口详情

#### 7.2.1 同步文本生成

**请求示例：**
```bash
curl "http://localhost:8080/ai/generate?message=用两句话介绍 Spring AI"
```

**响应示例：**
```
Spring AI 是一个用于将 AI 功能集成到 Spring 应用程序中的框架。它提供了统一的抽象层，支持多种 LLM 提供商，包括 OpenAI、Ollama、Anthropic 等。
```

#### 7.2.2 流式文本生成

**请求示例：**
```bash
curl -N "http://localhost:8080/ai/generateStream?message=写一首关于春天的诗"
```

**响应：** Server-Sent Events 流式输出

#### 7.2.3 高级文本生成

**请求示例：**
```bash
curl -X POST "http://localhost:8080/ai/generateAdvanced" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "解释什么是机器学习",
    "temperature": 0.5,
    "maxTokens": 1024
  }'
```

#### 7.2.4 多轮对话

**第一轮对话：**
```bash
curl -X POST "http://localhost:8080/ai/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，我叫张三"}'
```

**第二轮对话：**
```bash
curl -X POST "http://localhost:8080/ai/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "我叫什么名字？"}'
```

## 八、部署方式

### 8.1 本地开发部署

```bash
# 1. 进入项目目录
cd spring-ai-ollama-generation

# 2. 编译项目
mvn clean compile

# 3. 启动应用
mvn spring-boot:run
```

### 8.2 打包部署

```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 运行 JAR 包
java -jar target/spring-ai-ollama-generation-1.0.0-SNAPSHOT.jar
```

### 8.3 Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

构建并运行：

```bash
# 构建镜像
docker build -t spring-ai-ollama-generation .

# 运行容器
docker run -p 8080:8080 \
  -e SPRING_AI_OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  spring-ai-ollama-generation
```

## 九、使用示例

### 9.1 命令行示例

```bash
# 测试同步生成
curl "http://localhost:8080/ai/generate?message=用Java写一个Hello World程序"

# 测试流式生成
curl -N "http://localhost:8080/ai/generateStream?message=写一篇关于AI的短文"
```

### 9.2 Python 客户端示例

```python
import requests
import json

class SpringAIClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def generate_text(self, message):
        response = requests.get(
            f"{self.base_url}/ai/generate",
            params={"message": message}
        )
        return response.text
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/ai/chat",
            json={"message": message}
        )
        return response.text

# 使用示例
client = SpringAIClient()

# 文本生成
print(client.generate_text("介绍一下Spring AI"))

# 多轮对话
print(client.chat("你好"))
print(client.chat("我刚才说了什么？"))
```

### 9.3 JavaScript/Node.js 客户端示例

```javascript
const axios = require('axios');

class SpringAIClient {
    constructor(baseUrl = 'http://localhost:8080') {
        this.baseUrl = baseUrl;
    }
    
    async generateText(message) {
        const response = await axios.get(`${this.baseUrl}/ai/generate`, {
            params: { message }
        });
        return response.data;
    }
    
    async chat(message) {
        const response = await axios.post(`${this.baseUrl}/ai/chat`, {
            message
        });
        return response.data;
    }
}

// 使用示例
async function main() {
    const client = new SpringAIClient();
    
    // 文本生成
    const text = await client.generateText("介绍一下Spring AI");
    console.log('生成结果:', text);
    
    // 多轮对话
    const reply1 = await client.chat("你好");
    console.log('AI:', reply1);
    
    const reply2 = await client.chat("我刚才说了什么？");
    console.log('AI:', reply2);
}

main();
```

## 十、运行项目

### 10.1 前置检查

```bash
# 检查 Ollama 是否运行
curl http://localhost:11434/api/tags

# 检查模型是否已下载
ollama list
```

### 10.2 启动应用

```bash
# 方式1：使用 Maven
mvn spring-boot:run

# 方式2：使用 IDE
# 直接运行 SpringAiOllamaApplication 类
```

### 10.3 验证应用

访问健康检查端点：
```bash
curl http://localhost:8080/actuator/health
```

预期响应：
```json
{
  "status": "UP"
}
```

## 十一、常见问题

### 11.1 连接问题

**Q1: 提示 "Connection refused" 连接 Ollama 失败？**

- 确认 Ollama 服务正在运行：`curl http://localhost:11434/api/tags`
- 检查 `application.yml` 中的 `base-url` 配置是否正确
- 检查防火墙设置

**Q2: 使用 Docker 时无法连接宿主机 Ollama？**

在 Docker 运行命令中添加：
```bash
--add-host=host.docker.internal:host-gateway
```

并将配置改为：
```yaml
spring:
  ai:
    ollama:
      base-url: http://host.docker.internal:11434
```

### 11.2 模型问题

**Q1: 提示 "model not found"？**

- 确认模型已下载：`ollama list`
- 下载缺失的模型：`ollama pull gemma3:4b`
- 检查配置中的模型名称是否正确

**Q2: 生成速度很慢？**

- 使用量化模型：`ollama pull gemma3:4b-q4_0`
- 确保有足够的 GPU 显存
- 降低 `maxTokens` 参数值

### 11.3 参数调优

**Q1: 如何让生成内容更有创意？**

提高 `temperature` 参数值（如 1.0-1.5），同时降低 `top_p` 参数。

**Q2: 如何让生成内容更准确？**

降低 `temperature` 参数值（如 0.1-0.3），提高 `top_p` 参数。

## 十二、许可证

本项目采用 **Apache License 2.0** 许可证。

### Apache License 2.0 主要条款

- ✅ **商业使用**：允许将软件用于商业目的
- ✅ **修改**：允许修改源代码
- ✅ **分发**：允许再分发原始或修改后的软件
- ✅ **专利使用**：提供专利授权
- ✅ **私人使用**：允许私人使用
- ⚠️ **需要声明**：分发时需要包含许可证和版权声明
- ⚠️ **需要说明修改**：如果修改了代码，需要说明修改内容
- ❌ **无商标授权**：不授权使用商标
- ❌ **无责任担保**：软件按"原样"提供，不提供任何担保

## 十三、参考资源

### 13.1 官方文档

| 资源 | 链接 | 说明 |
|------|------|------|
| Spring AI 官方文档 | [https://docs.spring.io/spring-ai/](https://docs.spring.io/spring-ai/) | Spring AI 完整参考手册 |
| Spring AI ChatModel | [https://docs.spring.io/spring-ai/reference/api/chatmodel.html](https://docs.spring.io/spring-ai/reference/api/chatmodel.html) | ChatModel 接口详解 |
| Spring AI ChatClient | [https://docs.spring.io/spring-ai/reference/api/chatclient.html](https://docs.spring.io/spring-ai/reference/api/chatclient.html) | ChatClient 使用指南 |
| Ollama 官方文档 | [https://github.com/ollama/ollama](https://github.com/ollama/ollama) | Ollama 使用指南 |

### 13.2 本系列文档

本文档是 Spring AI 入门实践系列的第 1 篇，其他相关文档：

1. **环境准备与快速开始** - 开发环境搭建
2. **多轮对话** - 上下文记忆功能
3. **提示工程** - 提示词设计技巧
4. **工具调用** - Function Calling 功能
5. **文本嵌入** - Embeddings 向量生成
6. **图片生成** - AI 绘图功能
7. **模型上下文协议** - MCP 集成
8. **检索增强生成** - RAG 文档检索

### 13.3 社区资源

| 平台 | 链接 |
|------|------|
| Spring AI GitHub | [https://github.com/spring-projects/spring-ai](https://github.com/spring-projects/spring-ai) |
| Stack Overflow | [spring-ai 标签](https://stackoverflow.com/questions/tagged/spring-ai) |
| Ollama 模型库 | [https://ollama.com/library](https://ollama.com/library) |

## 十四、致谢

感谢以下开源项目和社区的贡献：

- **Spring AI** - 感谢 Spring 团队提供强大的 AI 集成框架
- **Ollama** - 感谢 Ollama 团队提供简洁高效的本地大模型部署方案
- **Spring Boot** - 感谢 Spring Boot 团队提供优秀的应用开发框架
- **所有开源大模型的研究团队和贡献者** - 包括 Meta、Google、Mistral AI 等

感谢您阅读本文档！祝您在 Spring AI 的学习之旅中取得成功！
