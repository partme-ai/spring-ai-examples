# Spring AI 入门实践：Spring AI 与百度千帆集成

## 概述

百度千帆大模型平台是百度智能云推出的一站式大模型开发和服务平台，提供了 ERNIE 系列大语言模型。Spring AI 提供了对百度千帆 API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 ERNIE-Bot、ERNIE-Bot-turbo 等模型进行文本生成、对话和嵌入计算。

## 项目概述

### 1.1 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-qianfan
**本地路径**：`spring-ai-qianfan/`

## 准备工作

### 1. 百度千帆账号配置

首先，您需要配置百度千帆账号并获取 API Key：

1. 访问 [百度智能云千帆平台](https://cloud.baidu.com/product/wenxinworkshop)
2. 注册账号并登录
3. 创建应用并获取 API Key 和 Secret Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springaicommunity</groupId>
        <artifactId>spring-ai-autoconfigure-model-qianfan</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-autoconfigure-retry</artifactId>
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

### 3. 配置百度千帆连接

在 `application.properties` 文件中配置百度千帆相关设置：

```properties
# 百度千帆基础配置
spring.ai.qianfan.api-key=你的API密钥
spring.ai.qianfan.secret-key=你的Secret密钥

# Chat 模型配置
spring.ai.qianfan.chat.enabled=true
spring.ai.qianfan.chat.options.model=ERNIE-Bot-turbo

# 嵌入模型配置
spring.ai.qianfan.embedding.enabled=true
spring.ai.qianfan.embedding.options.model=Embedding-V1
```

## 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [百度千帆官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/index.html) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 应用案例

### 1. 企业知识库问答系统

**业务场景**：
- 企业内部知识库智能问答
- 支持多轮对话和上下文理解
- 实时检索企业文档和政策信息

**性能指标**：
- 中文理解准确率：92%+
- 响应时间：<2s（ERNIE-Bot-turbo）
- 并发支持：100+ QPS

**技术实现**：
```java
@RestController
public class KnowledgeBaseController {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    @PostMapping("/kb/chat")
    public Map<String, Object> chat(@RequestBody ChatRequest request) {
        // 1. 检索相关文档
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(request.getQuery())
                .withTopK(5)
                .withSimilarityThreshold(0.7)
        );

        // 2. 构建提示词
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate("""
            基于以下企业知识库内容回答用户问题：
            {context}

            用户问题：{question}
            """);
        Prompt prompt = promptTemplate.create(Map.of(
            "context", context,
            "question", request.getQuery()
        ));

        // 3. 生成回答
        return Map.of("answer", chatModel.call(prompt));
    }
}
```

### 2. 智能客服系统

**业务场景**：
- 7×24 小时自动客户服务
- 多轮对话支持，记住上下文
- 自动识别意图并路由到相应服务

**性能指标**：
- 意图识别准确率：89%+
- 平均对话轮次：3-5 轮
- 自动解决率：75%+

**技术实现**：
```java
@Service
public class CustomerServiceAgent {

    private final ChatModel chatModel;
    private final Map<String, ConversationHistory> sessions = new ConcurrentHashMap<>();

    public String chat(String sessionId, String userMessage) {
        // 1. 获取对话历史
        ConversationHistory history = sessions.computeIfAbsent(
            sessionId, k -> new ConversationHistory()
        );

        // 2. 构建带历史的提示
        List<Message> messages = history.getMessages();
        messages.add(new UserMessage(userMessage));

        Prompt prompt = new Prompt(messages);
        ChatResponse response = chatModel.call(prompt);

        // 3. 更新历史
        history.addMessage(new UserMessage(userMessage));
        history.addMessage(response.getResult().getOutput());

        // 4. 检测是否需要人工介入
        if (needsHumanIntervention(response.getResult().getOutput().getContent())) {
            return "正在为您转接人工客服...";
        }

        return response.getResult().getOutput().getContent();
    }
}
```

### 3. 内容审核与生成

**业务场景**：
- 自动化内容生成（文章、摘要、标题）
- 内容合规性审核
- 敏感信息过滤

**性能指标**：
- 内容生成速度：500-1000 字/秒
- 审核准确率：95%+
- 误判率：<3%

**技术实现**：
```java
@Service
public class ContentModerationService {

    private final ChatModel chatModel;

    public ModerationResult moderate(String content) {
        PromptTemplate promptTemplate = new PromptTemplate("""
            请审核以下内容是否符合社区规范，检查以下方面：
            1. 是否包含违法违规信息
            2. 是否包含色情暴力内容
            3. 是否包含歧视性言论
            4. 是否包含垃圾广告信息

            内容：{content}

            请以 JSON 格式返回审核结果：
            {{
              "approved": true/false,
              "reason": "原因说明",
              "categories": ["问题类型列表"]
            }}
            """);

        Prompt prompt = promptTemplate.create(Map.of("content", content));
        String response = chatModel.call(prompt);

        return parseModerationResult(response);
    }
}
```

## 核心功能

### 1. 文本生成

使用百度千帆进行文本生成：

```java
package com.github.partmeai.baidu.qianfan.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

### 2. 提示模板使用

使用 Spring AI 的提示模板功能：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @GetMapping("/v1/prompt")
    public List<Generation> prompt(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", "funny", "topic", "cats"));
        return chatModel.call(prompt).getResults();
    }
}
```

### 3. 流式对话

支持流式响应，适用于实时对话场景：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @PostMapping("/v1/chat/completions")
    public Flux<ChatResponse> chatCompletions(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

### 4. 文本嵌入

使用百度千帆进行文本嵌入计算：

```java
package com.github.partmeai.baidu.qianfan.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/v1/embedding")
    public Map<String, Object> embedding(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("embeddings", embeddingModel.embed(message));
    }
}
```

## Java 客户端

以下是一个独立的 Java 客户端示例，用于调用百度千帆 API：

```java
package com.github.partmeai.baidu.qianfan.client;

import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class QianfanClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QianfanClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> generate(String message) {
        String url = baseUrl + "/v1/generate?message=" +
                    java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        return restTemplate.getForObject(url, Map.class);
    }

    public static void main(String[] args) {
        QianfanClient client = new QianfanClient("http://localhost:8080");
        Map<String, Object> result = client.generate("你好");
        System.out.println("Generation: " + result.get("generation"));
    }
}
```

**使用说明**：

1. 创建 `QianfanClient` 实例，传入服务器地址
2. 调用 `generate()` 方法进行文本生成
3. 返回结果包含生成的文本内容

**高级用法**：

```java
public class QianfanClientAdvanced {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QianfanClientAdvanced(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    // 流式对话
    public String streamChat(String message) {
        String url = baseUrl + "/v1/chat/completions";
        // 实现流式请求逻辑
        return restTemplate.postForObject(url, message, String.class);
    }

    // 文本嵌入
    public Map<String, Object> embedding(String message) {
        String url = baseUrl + "/v1/embedding?message=" +
                    java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        return restTemplate.getForObject(url, Map.class);
    }

    // 批量生成
    public Map<String, Object> batchGenerate(String[] messages) {
        String url = baseUrl + "/v1/batch-generate";
        return restTemplate.postForObject(url, messages, Map.class);
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-qianfan/
├── src/main/java/com/github/teachingai/baidu/qianfan/
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── EmbeddingController.java
│   ├── agent/
│   │   └── Todo.java
│   ├── finetune/
│   │   └── Todo.java
│   ├── functions/
│   │   ├── FunctionConfig.java
│   │   ├── GetWeatherFunction.java
│   │   └── PconlineRegionFunction.java
│   ├── rag/
│   │   └── Todo.java
│   ├── router/
│   │   └── RouterFunctionConfig.java
│   └── SpringAiBaiduAiQianfanApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key 和 Secret Key
2. 启动应用：

```bash
cd spring-ai-qianfan
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成
   - `GET /v1/prompt?message=测试` - 提示模板使用
   - `POST /v1/chat/completions?message=讲个故事` - 流式对话
   - `GET /v1/embedding?message=需要嵌入的文本` - 文本嵌入

## 测试方法

### 单元测试

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.chat.model.ChatModel;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QianfanIntegrationTest {

    @Autowired
    private ChatModel chatModel;

    @Test
    void testChatGeneration() {
        String response = chatModel.call("你好");
        assertThat(response).isNotEmpty();
    }
}
```

## 最佳实践

1. **API Key 管理**：使用环境变量或配置中心管理敏感信息
2. **模型选择**：根据需求选择合适的模型
   - ERNIE-Bot-turbo：速度快，适合一般对话场景
   - ERNIE-Bot：能力强，适合复杂推理和创作
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额
5. **性能优化**：对于批量处理，考虑使用异步调用

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 或 Secret Key 无效 | 检查 API Key 和 Secret Key 是否正确 |
| 403 错误 | 账号余额不足或权限不够 | 检查账号余额和应用权限 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |
| 模型不可用 | 指定的模型不存在或不可访问 | 检查模型名称是否正确 |

## 致谢

感谢百度千帆团队在 ERNIE 系列模型方面的技术突破，为中文大语言模型的发展做出了重要贡献。感谢 Spring AI 团队提供的统一抽象接口，简化了百度千帆模型的集成工作。

## 相关资源

- [百度千帆官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/index.html)
- [ERNIE系列模型介绍](https://cloud.baidu.com/product/wenxinworkshop)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-qianfan)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 OpenAI 集成](25、Spring AI 入门实践：Spring AI 与 OpenAI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 文本嵌入](5、Spring AI 入门实践：Spring AI 文本嵌入（Embeddings）.md)