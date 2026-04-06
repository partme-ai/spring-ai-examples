# 23、Spring AI 入门实践：Spring AI 模型微调（Fine-tuning）

## 一、项目概述

模型微调是在预训练模型的基础上，使用特定领域的数据进一步训练模型，使其更适合特定任务的过程。Spring AI 本身不直接提供微调训练功能，但可以与 Ollama 配合使用，导入微调后的自定义模型进行推理。

### 核心功能

- **自定义模型支持**：支持导入和使用微调后的自定义模型
- **Ollama 集成**：通过 Ollama 管理和使用自定义模型
- **Modelfile 配置**：使用 Ollama Modelfile 构建自定义标签
- **统一 API**：微调后的模型使用与普通模型相同的 API

### 适用场景

- 特定领域知识问答
- 企业内部知识库
- 定制化对话风格
- 专业术语理解

## 二、模型微调简介

模型微调通常涉及三个阶段：离线训练、Ollama 导入、Spring AI 调用。

### 流程概览

| 阶段 | 说明 | 工具 |
|------|------|------|
| 1. 离线训练 | 在训练框架中做 LoRA/QLoRA 等微调 | PyTorch、Transformers 等 |
| 2. Ollama 导入 | 通过 Modelfile 或导入方式构建自定义标签 | Ollama |
| 3. Spring AI 调用 | 配置指向自定义标签，照常 ChatModel 调用 | Spring AI |

### 微调方法对比

| 方法 | 特点 | 适用场景 |
|------|------|---------|
| LoRA | 参数高效微调，只训练少量参数 | 资源有限场景 |
| QLoRA | 量化 LoRA，更节省显存 | 低显存环境 |
| 全量微调 | 训练所有参数，效果最好 | 资源充足场景 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型部署）
- Python 3.8+（用于微调训练，可选）

### 3.2 Ollama 准备

1. **安装 Ollama**：https://ollama.com/
2. **启动 Ollama 服务**
3. **准备微调后的模型权重**（LoRA/QLoRA 导出）

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-fine-tuning/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── finetuning/
│   │   │                   ├── SpringAiFineTuningApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   └── service/
│   │   │                       └── ChatService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   │           └── index.html
│   └── test/
├── Modelfile
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
    name: spring-ai-ollama-fine-tuning
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: my-custom-model
          temperature: 0.7

server:
  port: 8080
```

### 5.3 Ollama Modelfile 示例

```
FROM llama3.2

# 设置系统提示
SYSTEM """你是一个专业的客服助手，用友好、专业的语气回答用户问题。"""

# 添加参数
PARAMETER temperature 0.7
PARAMETER top_p 0.9
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.finetuning.service;

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
                "response", response,
                "model", "my-custom-model"
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
package com.github.partmeai.finetuning.controller;

import com.github.partmeai.finetuning.service.ChatService;
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
        String systemPrompt = request.getOrDefault("systemPrompt", "");
        String message = request.get("message");
        return chatService.chatWithSystemPrompt(systemPrompt, message);
    }
    
    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.finetuning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiFineTuningApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiFineTuningApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 使用自定义模型对话 |
| 系统提示对话 | POST | `/api/chat/system` | 带系统提示的对话 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "你好，请介绍一下你自己"}'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=请详细介绍"
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-ollama-fine-tuning
mvn spring-boot:run
```

浏览器打开静态页或调用接口，确认模型名与本地 `ollama list` 一致。

### 8.2 打包部署

```bash
cd spring-ai-ollama-fine-tuning
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-fine-tuning-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Ollama 自定义模型构建

#### 方式一：使用 Modelfile

```bash
# 创建 Modelfile
cat > Modelfile << 'EOF'
FROM llama3.2
SYSTEM """你是一个专业的客服助手。"""
PARAMETER temperature 0.7
EOF

# 构建自定义模型
ollama create my-custom-model -f Modelfile

# 验证模型
ollama list
ollama run my-custom-model
```

#### 方式二：导入微调权重

```bash
# 导入微调后的模型（具体步骤参考 Ollama 文档）
ollama create my-fine-tuned-model --import /path/to/weights
```

### 9.2 最佳实践

1. **微调数据准备**：
   - 收集特定领域的高质量数据
   - 数据格式符合训练框架要求
   - 确保数据多样性和代表性

2. **训练策略**：
   - 优先使用 LoRA/QLoRA 等参数高效方法
   - 控制训练步数和学习率
   - 定期评估和验证效果

3. **Ollama 集成**：
   - 使用 Modelfile 管理自定义配置
   - 测试微调后的模型效果
   - 确认模型名与 Spring AI 配置一致

4. **Spring AI 调用**：
   - 配置文件中指向自定义模型名
   - 使用与普通模型相同的 API
   - 通过系统提示进一步优化效果

### 9.3 官方文档

- Chat Model — Ollama：https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
- Ollama 官方：https://github.com/ollama/ollama（Importing Models）

## 十、运行项目

### 10.1 启动应用

```bash
cd spring-ai-ollama-fine-tuning
mvn spring-boot:run
```

### 10.2 验证模型

```bash
# 确认 Ollama 中有自定义模型
ollama list

# 测试接口
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 模型导入问题

**Q: Ollama 无法导入微调后的模型怎么办？**

- 确认模型权重格式正确
- 检查 Modelfile 语法和配置
- 参考 Ollama 官方文档的导入指南
- 确认有足够的磁盘空间和内存

### 11.2 微调效果问题

**Q: 微调后的模型效果不理想怎么办？**

- 检查训练数据质量和数量
- 调整训练参数（学习率、步数等）
- 尝试不同的微调方法（LoRA vs QLoRA）
- 在系统提示中补充相关知识

### 11.3 Spring AI 配置问题

**Q: Spring AI 无法连接到自定义模型怎么办？**

- 确认 Ollama 服务正常运行（http://localhost:11434）
- 检查配置文件中的模型名是否正确
- 使用 `ollama list` 确认模型存在
- 查看应用日志中的错误信息

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Ollama：https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
- Ollama：https://ollama.com/
- Ollama Importing Models：https://github.com/ollama/ollama
- 示例模块：spring-ai-ollama-fine-tuning
- 模块 README.md（外部教程链接）

## 十四、致谢

感谢 Spring AI 团队和 Ollama 团队，让使用和部署自定义微调模型变得如此简单。
