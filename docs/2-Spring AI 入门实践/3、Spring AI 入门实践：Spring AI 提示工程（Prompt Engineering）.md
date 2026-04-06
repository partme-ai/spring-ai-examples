# 3、Spring AI 入门实践：Spring AI 提示工程（Prompt Engineering）

## 一、项目概述

提示工程是通过设计和优化提示词来引导大语言模型生成更准确、更有用响应的技术。Spring AI 提供了丰富的提示工程功能，包括提示模板、变量替换、系统提示等。本文将介绍如何在 Spring AI 中使用各种提示工程技术。

### 核心功能

- **提示模板**：支持变量替换的可复用提示
- **系统提示**：设置 AI 的角色和行为
- **结构化提示**：指导模型按特定格式输出
- **少样本提示**：通过示例引导模型
- **思维链提示**：让模型展示推理过程
- **链式提示**：分步完成复杂任务

### 适用场景

- 内容生成与摘要
- 代码审查与生成
- 文本分类与情感分析
- 问题解答与推理
- 数据提取与格式化

## 二、提示工程简介

提示工程的核心是用清晰、具体的语言指导模型。好的提示通常包含任务说明、上下文信息、输出格式要求等要素。

### 常见提示技术

| 技术 | 说明 | 适用场景 |
|------|------|---------|
| 系统提示 | 设置角色和规则 | 需要特定风格或专业知识 |
| 提示模板 | 变量替换 | 批量处理类似任务 |
| 少样本提示 | 提供示例 | 分类、格式转换 |
| 思维链 | 展示推理 | 数学题、逻辑推理 |
| 结构化输出 | 指定格式 | JSON、XML 等结构化数据 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型）

### 3.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取模型
ollama pull gemma3:4b
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-prompt/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── PromptController.java
│   │   │                   └── service/
│   │   │                       └── PromptService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── prompts/
│   │           └── code-review.st
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `PromptService` | 封装各种提示工程技术 |
| `PromptController` | 提供 REST API |

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
    name: spring-ai-ollama-prompt
  
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

### 6.1 提示服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class PromptService {
    
    private final ChatClient chatClient;
    
    public PromptService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String simplePrompt(String userInput) {
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }
    
    public String templatePrompt(String template, Map<String, Object> variables) {
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(variables);
        
        return chatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
    
    public String systemPrompt(String systemMessage, String userMessage) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemMessage),
                new UserMessage(userMessage)
        ));
        
        return chatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
    
    public String codeReview(String code) {
        String systemPrompt = """
            你是一个专业的代码审查专家。请按照以下格式审查代码：
            
            1. 代码质量评估
            2. 潜在问题分析
            3. 改进建议
            4. 最佳实践推荐
            """;
        
        String userPrompt = String.format("""
            请审查以下代码：
            
            ```java
            %s
            ```
            """, code);
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
        ));
        
        return chatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
    
    public String fewShotClassify(String text) {
        String systemPrompt = """
            你是一个文本分类专家。请根据以下示例，对输入的文本进行分类。
            
            示例：
            输入：今天天气真好，阳光明媚。
            分类：积极
            
            输入：这个产品质量太差了，我很失望。
            分类：消极
            
            输入：会议将在明天上午10点开始。
            分类：中性
            """;
        
        String userPrompt = String.format("请分类以下文本：\n%s", text);
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
        ));
        
        return chatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
    
    public String chainOfThought(String problem) {
        String systemPrompt = """
            你是一个问题解决专家。在回答问题时，请按照以下步骤进行思考：
            
            1. 理解问题：明确问题的要求和约束条件
            2. 分析问题：识别问题的关键要素和关系
            3. 制定方案：提出可能的解决方案
            4. 评估方案：分析每个方案的优缺点
            5. 选择方案：选择最佳方案并说明理由
            6. 执行方案：详细说明如何实施所选方案
            
            请逐步展示你的思考过程。
            """;
        
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(problem)
        ));
        
        return chatClient.call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
    
    public String generateArticle(String topic) throws IOException {
        ClassPathResource resource = new ClassPathResource("prompts/article-outline.st");
        String outlineTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        String outline = templatePrompt(outlineTemplate, Map.of("topic", topic));
        
        String articlePrompt = """
            基于以下大纲，写一篇完整的文章：
            
            %s
            """.formatted(outline);
        
        return simplePrompt(articlePrompt);
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.PromptService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/prompt")
public class PromptController {
    
    private final PromptService promptService;
    
    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }
    
    @PostMapping("/simple")
    public String simplePrompt(@RequestBody String userInput) {
        return promptService.simplePrompt(userInput);
    }
    
    @PostMapping("/template")
    public String templatePrompt(@RequestBody Map<String, Object> request) {
        String template = (String) request.get("template");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        return promptService.templatePrompt(template, variables);
    }
    
    @PostMapping("/system")
    public String systemPrompt(@RequestBody Map<String, String> request) {
        String systemMessage = request.get("systemMessage");
        String userMessage = request.get("userMessage");
        return promptService.systemPrompt(systemMessage, userMessage);
    }
    
    @PostMapping("/code-review")
    public String codeReview(@RequestBody String code) {
        return promptService.codeReview(code);
    }
    
    @PostMapping("/classify")
    public String classify(@RequestBody String text) {
        return promptService.fewShotClassify(text);
    }
    
    @PostMapping("/chain-of-thought")
    public String chainOfThought(@RequestBody String problem) {
        return promptService.chainOfThought(problem);
    }
    
    @PostMapping("/article")
    public String generateArticle(@RequestParam String topic) throws IOException {
        return promptService.generateArticle(topic);
    }
}
```

### 6.3 提示模板文件

在 `src/main/resources/prompts/article-outline.st` 中：

```
请为 "{topic}" 这个主题生成一个详细的文章大纲。

要求：
1. 包含引言、主体部分（3-5个小节）、结论
2. 每个部分有明确的标题
3. 逻辑清晰，结构合理
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单提示 | POST | `/api/prompt/simple` | 简单文本提示 |
| 模板提示 | POST | `/api/prompt/template` | 带变量的提示模板 |
| 系统提示 | POST | `/api/prompt/system` | 带系统消息的提示 |
| 代码审查 | POST | `/api/prompt/code-review` | 代码审查 |
| 文本分类 | POST | `/api/prompt/classify` | 少样本分类 |
| 思维链 | POST | `/api/prompt/chain-of-thought` | 推理问题 |
| 文章生成 | POST | `/api/prompt/article` | 生成文章 |

### 7.2 接口使用示例

#### 简单提示

```bash
curl -X POST http://localhost:8080/api/prompt/simple \
  -H "Content-Type: application/json" \
  -d "什么是 Spring AI？"
```

#### 模板提示

```bash
curl -X POST http://localhost:8080/api/prompt/template \
  -H "Content-Type: application/json" \
  -d '{
    "template": "请用 {language} 写一个 {type} 程序",
    "variables": {
      "language": "Java",
      "type": "Hello World"
    }
  }'
```

#### 代码审查

```bash
curl -X POST http://localhost:8080/api/prompt/code-review \
  -H "Content-Type: application/json" \
  -d 'public class Hello { public static void main(String[] args) { System.out.println("Hello"); } }'
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-ollama-prompt
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-prompt-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import json

class PromptClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def simple_prompt(self, message):
        response = requests.post(
            f"{self.base_url}/api/prompt/simple",
            data=message,
            headers={"Content-Type": "application/json"}
        )
        return response.text
    
    def classify(self, text):
        response = requests.post(
            f"{self.base_url}/api/prompt/classify",
            data=text,
            headers={"Content-Type": "application/json"}
        )
        return response.text

client = PromptClient()
print(client.classify("这个产品太棒了！"))
```

### 9.2 提示工程最佳实践

1. **具体明确**：避免模糊的描述
2. **提供上下文**：给出足够的背景信息
3. **指定格式**：明确要求输出格式
4. **使用示例**：通过示例说明期望
5. **分而治之**：复杂任务拆分为多个步骤

## 十、运行项目

### 10.1 前置检查

```bash
curl http://localhost:11434/api/tags
```

### 10.2 启动应用

```bash
mvn spring-boot:run
```

## 十一、常见问题

### 11.1 提示设计问题

**Q: 模型输出总是不符合预期怎么办？**

尝试以下方法：
1. 更明确地描述任务要求
2. 添加输出格式示例
3. 使用少样本提示给出正确示例
4. 调整温度参数

**Q: 如何让模型输出 JSON 格式？**

在提示中明确要求：
```
请以 JSON 格式输出，包含以下字段：title, content, summary
```

### 11.2 性能问题

**Q: 提示词太长导致费用高或响应慢？**

- 精简提示词，保留关键信息
- 使用提示模板复用公共部分
- 考虑使用摘要代替完整历史

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Prompt 文档：https://docs.spring.io/spring-ai/reference/api/prompt.html
- OpenAI 提示工程指南：https://platform.openai.com/docs/guides/prompt-engineering
- 示例模块：spring-ai-ollama-prompt

## 十四、致谢

感谢所有提示工程领域的研究者和实践者分享的经验和技巧。
