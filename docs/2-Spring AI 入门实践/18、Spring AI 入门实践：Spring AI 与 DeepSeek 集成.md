# 18、Spring AI 入门实践：Spring AI 与 DeepSeek 集成

## 一、项目概述

DeepSeek 是一家专注于人工智能研究的公司，其开发的 DeepSeek-R1、DeepSeek-V3 等模型在数学、代码和推理任务中表现出色。Spring AI 提供了对 DeepSeek 模型的完整集成支持，使得开发者可以在 Spring 应用中轻松使用 DeepSeek 的强大能力。

### 核心功能

- **强大推理能力**：DeepSeek-R1 在数学、代码等推理任务表现出色
- **多模型支持**：DeepSeek-R1、DeepSeek-V3 等多种模型
- **长上下文**：支持超长上下文处理
- **工具调用**：支持函数调用和工具集成
- **流式响应**：支持实时流式输出
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 数学计算和推理
- 代码生成和审查
- 复杂问题求解
- 科学研究和分析
- 技术文档处理
- 编程辅助工具

## 二、DeepSeek 简介

DeepSeek 是一家专注于大语言模型研发的公司，其模型在推理能力方面表现突出。

### 可用模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| DeepSeek-R1 | 数学和代码推理能力强 | 数学计算、编程辅助 |
| DeepSeek-V3 | 综合能力强 | 通用对话、复杂推理 |
| DeepSeek-Coder | 代码生成能力强 | 代码辅助、编程工具 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 推理能力 | 在数学、代码任务上表现突出 |
| 长上下文 | 支持超长文本输入 |
| 工具调用 | 原生支持函数调用 |
| 代码能力 | 强大的代码理解和生成能力 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- DeepSeek API 密钥

### 3.2 获取 DeepSeek API 密钥

1. **访问 DeepSeek 官网**：https://platform.deepseek.com/
2. **注册账号并登录**
3. **创建 API 密钥**
4. **记录 API Key**

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-deepseek/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── deepseek/
│   │   │                   ├── SpringAiDeepSeekApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   └── service/
│   │   │                       └── ChatService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

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
        <artifactId>spring-ai-starter-model-deepseek</artifactId>
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
    name: spring-ai-deepseek
  
  ai:
    deepseek:
      api-key: ${DEEPSEEK_API_KEY:your-api-key}
      chat:
        enabled: true
        options:
          model: deepseek-chat
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.deepseek.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Map<String, Object> chat(String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        return Map.of(
                "message", message,
                "response", response
        );
    }
    
    public Map<String, Object> chatWithSystemPrompt(String systemPrompt, String userMessage) {
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
        
        return Map.of(
                "systemPrompt", systemPrompt,
                "message", userMessage,
                "response", response
        );
    }
    
    public Map<String, Object> solveMathProblem(String problem) {
        String systemPrompt = "你是一个专业的数学专家，请仔细分析并解决数学问题。" +
                "请展示完整的解题步骤。";
        
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(problem)
                .call()
                .content();
        
        return Map.of(
                "problem", problem,
                "solution", response
        );
    }
    
    public Map<String, Object> generateCode(String requirement) {
        String systemPrompt = "你是一个专业的代码助手。请生成高质量、可运行的代码。" +
                "请添加适当的注释和说明。";
        
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(requirement)
                .call()
                .content();
        
        return Map.of(
                "requirement", requirement,
                "code", response
        );
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
package com.github.partmeai.deepseek.controller;

import com.github.partmeai.deepseek.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatService.chat(message);
    }
    
    @PostMapping("/chat/system")
    public Map<String, Object> chatWithSystem(@RequestBody Map<String, String> request) {
        String systemPrompt = request.getOrDefault("systemPrompt", "你是一个有帮助的助手。");
        String message = request.get("message");
        return chatService.chatWithSystemPrompt(systemPrompt, message);
    }
    
    @PostMapping("/math")
    public Map<String, Object> solveMath(@RequestBody Map<String, String> request) {
        String problem = request.get("problem");
        return chatService.solveMathProblem(problem);
    }
    
    @PostMapping("/code")
    public Map<String, Object> generateCode(@RequestBody Map<String, String> request) {
        String requirement = request.get("requirement");
        return chatService.generateCode(requirement);
    }
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.deepseek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiDeepSeekApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiDeepSeekApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 数学解题 | POST | `/api/math` | 解决数学问题 |
| 代码生成 | POST | `/api/code` | 生成代码 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 DeepSeek"}'
```

#### 数学解题

```bash
curl -X POST http://localhost:8080/api/math \
  -H "Content-Type: application/json" \
  -d '{"problem": "求导数 f(x) = x^2 + 3x + 1"}'
```

#### 代码生成

```bash
curl -X POST http://localhost:8080/api/code \
  -H "Content-Type: application/json" \
  -d '{"requirement": "写一个快速排序的Java实现"}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=详细解释什么是深度学习"
```

## 八、部署方式

### 8.1 本地运行

```bash
export DEEPSEEK_API_KEY=your-api-key
cd spring-ai-deepseek
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export DEEPSEEK_API_KEY=your-api-key
mvn clean package -DskipTests
java -jar target/spring-ai-deepseek-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class DeepSeekClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat",
            json={"message": message}
        )
        return response.json()
    
    def solve_math(self, problem):
        response = requests.post(
            f"{self.base_url}/api/math",
            json={"problem": problem}
        )
        return response.json()
    
    def generate_code(self, requirement):
        response = requests.post(
            f"{self.base_url}/api/code",
            json={"requirement": requirement}
        )
        return response.json()

client = DeepSeekClient()

result = client.chat("请介绍一下 DeepSeek")
print(f"Response: {result['response']}")

math_result = client.solve_math("求导数 f(x) = x^2 + 3x + 1")
print(f"\nProblem: {math_result['problem']}")
print(f"Solution: {math_result['solution']}")

code_result = client.generate_code("写一个快速排序的Java实现")
print(f"\nRequirement: {code_result['requirement']}")
print(f"Code:\n{code_result['code']}")
```

### 9.2 最佳实践

1. **模型选择**：
   - 通用对话：deepseek-chat
   - 数学推理：deepseek-reasoner
   - 代码生成：deepseek-coder

2. **提示工程**：
   - 对于数学问题，明确要求展示解题步骤
   - 对于代码生成，要求添加注释和测试用例
   - 充分利用 DeepSeek 的推理能力

3. **应用场景**：
   - 数学教育和辅导
   - 编程辅助和教学
   - 科研计算和分析
   - 技术文档处理

### 9.3 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 通用对话 | deepseek-chat |
| 数学推理 | deepseek-reasoner |
| 代码生成 | deepseek-coder |
| 复杂问题 | deepseek-reasoner |

## 十、运行项目

### 10.1 启动应用

```bash
export DEEPSEEK_API_KEY=your-api-key
cd spring-ai-deepseek
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 API 密钥问题

**Q: 提示 API 密钥无效怎么办？**

- 确认 API 密钥正确且有效
- 检查环境变量或配置文件中的密钥设置
- 确认 API 密钥没有过期或被撤销
- 查看 DeepSeek 控制台的使用记录

### 11.2 推理效果问题

**Q: 数学推理结果不准确怎么办？**

- 考虑使用专门的推理模型
- 优化提示词，明确要求展示推理过程
- 对于复杂问题，尝试分步骤提问
- 检查问题表述是否清晰准确

**Q: 代码生成质量不高怎么办？**

- 使用专门的代码模型
- 在提示中明确代码规范和要求
- 要求生成完整的测试用例
- 考虑使用代码审查工具

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- DeepSeek 官方文档：https://platform.deepseek.com/docs
- Spring AI DeepSeek：https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html
- 示例模块：spring-ai-deepseek

## 十四、致谢

感谢 DeepSeek 团队和 Spring AI 团队提供的优秀工具，让构建强大的推理型 AI 应用变得如此简单易用。
