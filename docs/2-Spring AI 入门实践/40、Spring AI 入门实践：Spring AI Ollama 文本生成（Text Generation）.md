# Spring AI 入门实践：Spring AI Ollama 文本生成（Text Generation）

## 概述

文本生成是大语言模型的核心功能之一。通过 Ollama，我们可以在本地运行各种大型语言模型（LLM）并从中生成文本。Spring AI 通过 `OllamaChatModel` 提供了强大的文本生成支持。

## 准备工作

### 1. Ollama 安装与配置

首先，您需要访问 Ollama 实例：

- 在本地机器上[下载并安装 Ollama](https://ollama.com/download)
- 通过 [Testcontainers](https://docs.spring.io/spring-ai/reference/api/testcontainers.html) 配置和运行 Ollama
- 通过 [Kubernetes Service Bindings](https://docs.spring.io/spring-ai/reference/api/cloud-bindings.html) 绑定到 Ollama 实例

拉取想要使用的模型：

```bash
ollama pull llama2
ollama pull mistral
ollama pull qwen3.5
ollama pull deepseek-r1
```

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-ollama</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
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

### 3. 配置 Ollama 连接

在 `application.properties` 文件中配置 Ollama 相关设置：

```properties
# Ollama 基础配置
spring.ai.ollama.base-url=http://localhost:11434

# Chat 模型配置
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=llama2
spring.ai.ollama.chat.options.temperature=0.8
```

## 核心功能

### 1. 基础文本生成

使用 Ollama 进行文本生成：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GenerationController {

    private final ChatModel chatModel;

    @Autowired
    public GenerationController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好") String message) {
        String response = chatModel.call(message);
        return Map.of("generation", response);
    }
}
```

### 2. 流式文本生成

支持流式响应，适用于实时生成场景：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class StreamController {

    private final ChatModel chatModel;

    @GetMapping("/generate/stream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "讲个故事") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

### 3. 使用提示模板

使用提示模板进行结构化文本生成：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TemplateController {

    private final ChatModel chatModel;

    @GetMapping("/generate/template")
    public String generateFromTemplate(@RequestParam String topic) {
        PromptTemplate promptTemplate = new PromptTemplate("请写一篇关于{topic}的文章，字数在500字左右。");
        Prompt prompt = promptTemplate.create(Map.of("topic", topic));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 推荐模型

### DeepSeek-R1

DeepSeek-R1 是强大的混合专家（MoE）语言模型，在数学、代码和推理任务中表现优异：

```bash
ollama run deepseek-r1:7b
ollama run deepseek-r1:8b
ollama run deepseek-r1:14b
```

### Qwen3.5

Qwen3.5 是阿里巴巴推出的最新系列大型语言模型，在推理、代码生成和数学能力方面有显著提升：

```bash
ollama run qwen3.5:0.5b
ollama run qwen3.5:1.8b  
ollama run qwen3.5:4b
ollama run qwen3.5:7b
ollama run qwen3.5:14b
ollama run qwen3.5:32b
ollama run qwen3.5:72b
```

### Llama2

Meta 的开源大语言模型：

```bash
ollama run llama2
ollama run llama2:13b
ollama run llama2:70b
```

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-generation
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /generate?message=你好` - 文本生成
   - `GET /generate/stream?message=讲个故事` - 流式文本生成
   - `GET /generate/template?topic=人工智能` - 使用模板生成

## 最佳实践

1. **模型选择**：根据任务需求选择合适的模型
   - 简单对话：使用小型模型（0.5B-1.5B）
   - 复杂推理：使用大型模型（7B-70B）
2. **温度设置**：调整温度参数控制生成多样性
   - 创意写作：温度 0.8-1.0
   - 事实性回答：温度 0.2-0.5
3. **上下文管理**：合理管理对话上下文
4. **错误处理**：实现重试机制和超时处理

## 相关资源

- [Ollama 官方文档](https://ollama.com/docs)
- [Ollama 模型库](https://ollama.com/library)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-generation)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 提示工程](3、Spring AI 入门实践：Spring AI 提示工程（Prompt Engineering）.md)