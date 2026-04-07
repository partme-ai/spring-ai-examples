# 3、Spring AI 入门实践：Spring AI 提示工程（Prompt Engineering）

## 概述

提示工程（Prompt Engineering）是指通过设计和优化提示词来引导大语言模型生成更准确、更有用的响应。Spring AI 提供了丰富的提示工程功能，包括提示模板、变量替换、系统提示等。

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
```

## 基本使用

### 1. 简单提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimplePromptService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateResponse(String userInput) {
        return chatClient.prompt()
            .user(userInput)
            .call()
            .content();
    }
}
```

### 2. 使用提示模板

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PromptTemplateService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateResponse(String template, Map<String, Object> variables) {
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(variables);
        
        return chatClient.call(prompt)
            .getResult()
            .getOutput()
            .getContent();
    }
}
```

## 高级功能

### 1. 系统提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemPromptService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateResponse(String userMessage) {
        SystemMessage systemMessage = new SystemMessage(
            "你是一个专业的技术顾问，专门回答关于软件开发的问题。"
        );
        UserMessage userMsg = new UserMessage(userMessage);
        
        Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
        
        return chatClient.call(prompt)
            .getResult()
            .getOutput()
            .getContent();
    }
}
```

### 2. 结构化提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StructuredPromptService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateCodeReview(String code) {
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
}
```

### 3. 链式提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ChainPromptService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String generateArticle(String topic) {
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        
        messages.add(new SystemMessage("你是一个专业的文章写作助手。"));
        
        String outlinePrompt = String.format("请为 '%s' 这个主题生成一个文章大纲。", topic);
        messages.add(new UserMessage(outlinePrompt));
        
        String outline = chatClient.call(new Prompt(messages))
            .getResult()
            .getOutput()
            .getContent();
        
        messages.add(new AssistantMessage(outline));
        
        String articlePrompt = String.format(
            "基于以下大纲，写一篇完整的文章：\n%s", 
            outline
        );
        messages.add(new UserMessage(articlePrompt));
        
        return chatClient.call(new Prompt(messages))
            .getResult()
            .getOutput()
            .getContent();
    }
}
```

### 4. 少样本提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FewShotPromptService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String classifyText(String text) {
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
}
```

### 5. 思维链提示

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChainOfThoughtService {
    
    @Autowired
    private ChatClient chatClient;
    
    public String solveProblem(String problem) {
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
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class PromptEngineeringApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromptEngineeringApplication.class, args);
    }
}

@Service
class PromptEngineeringService {
    
    @Autowired
    private ChatClient chatClient;
    
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
    
    public String systemPrompt(String userMessage) {
        SystemMessage systemMessage = new SystemMessage(
            "你是一个专业的技术顾问，专门回答关于软件开发的问题。"
        );
        UserMessage userMsg = new UserMessage(userMessage);
        
        Prompt prompt = new Prompt(List.of(systemMessage, userMsg));
        
        return chatClient.call(prompt)
            .getResult()
            .getOutput()
            .getContent();
    }
    
    public String fewShotPrompt(String text) {
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
}

@RestController
@RequestMapping("/api/prompt-engineering")
class PromptEngineeringController {
    
    @Autowired
    private PromptEngineeringService service;
    
    @PostMapping("/simple")
    public String simplePrompt(@RequestBody String userInput) {
        return service.simplePrompt(userInput);
    }
    
    @PostMapping("/template")
    public String templatePrompt(@RequestBody Map<String, Object> request) {
        String template = (String) request.get("template");
        Map<String, Object> variables = (Map<String, Object>) request.get("variables");
        return service.templatePrompt(template, variables);
    }
    
    @PostMapping("/system")
    public String systemPrompt(@RequestBody String userMessage) {
        return service.systemPrompt(userMessage);
    }
    
    @PostMapping("/few-shot")
    public String fewShotPrompt(@RequestBody String text) {
        return service.fewShotPrompt(text);
    }
    
    @PostMapping("/chain-of-thought")
    public String chainOfThought(@RequestBody String problem) {
        return service.chainOfThought(problem);
    }
}
```

## 测试方法

1. **启动应用**：运行 `PromptEngineeringApplication` 类
2. **简单提示**：
   ```bash
   curl -X POST http://localhost:8080/api/prompt-engineering/simple \
     -H "Content-Type: application/json" \
     -d "什么是 Spring AI？"
   ```
3. **模板提示**：
   ```bash
   curl -X POST http://localhost:8080/api/prompt-engineering/template \
     -H "Content-Type: application/json" \
     -d '{"template":"请用 {language} 写一个 {type} 程序","variables":{"language":"Java","type":"Hello World"}}'
   ```
4. **少样本提示**：
   ```bash
   curl -X POST http://localhost:8080/api/prompt-engineering/few-shot \
     -H "Content-Type: application/json" \
     -d "这个新功能太棒了，我很喜欢！"
   ```
5. **思维链**：
   ```bash
   curl -X POST http://localhost:8080/api/prompt-engineering/chain-of-thought \
     -H "Content-Type: application/json" \
     -d "如何设计一个高并发的电商系统？"
   ```

## 总结

Spring AI 提供了丰富的提示工程功能，包括简单提示、提示模板、系统提示、结构化提示、链式提示、少样本提示和思维链提示等。通过合理使用这些技术，可以显著提高大语言模型的输出质量和准确性。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI 提示工程指南](https://platform.openai.com/docs/guides/prompt-engineering)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关提示工程的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Prompt](https://docs.spring.io/spring-ai/reference/api/prompt.html) - 提示模板与变量替换
- [Spring AI Advisors API](https://docs.spring.io/spring-ai/reference/api/advisors.html) - 对提示的前后处理
- [Spring AI Testing](https://docs.spring.io/spring-ai/reference/api/testing.html) - 自动化断言与评测

### 示例模块
- **`spring-ai-ollama-prompt`** - 本仓库中的提示工程示例模块
  - 主类：`com.github.teachingai.ollama.SpringAiOllamaApplication`
  - 控制器：`com.github.teachingai.ollama.controller.PromptController` 和 `EvaluationAssistantController`
  - 路径：`/v1/prompt`（模板变量）和 `/evaluation/getWordsOfWishByTeacher`（业务模板）

### 核心概念
- **`PromptTemplate`**：占位符 `{name}`，通过 `create(Map)` 渲染为 `Prompt`
- **`ClassPathResource("prompts/...")`**：长系统提示放入资源文件，避免硬编码
- **`SystemMessage` + `UserMessage`**：与多轮对话相同的消息模型

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-ollama-prompt

# 启动应用
mvn spring-boot:run

# 测试模板提示
curl "http://localhost:8080/v1/prompt"

# 测试业务模板
curl "http://localhost:8080/evaluation/getWordsOfWishByTeacher?message=学生表现良好"
```

### 最佳实践
1. **模板与代码分离**：长提示、易变文案放 `resources/prompts/` 目录
2. **版本管理**：提示词变更与发版同步，避免线上与仓库不一致
3. **自动化评测**：结合 `FactChecking` 等测试类做自动化断言
4. **业务场景适配**：教育场景下的评价/寄语模板示例见 `EvaluationAssistantController`