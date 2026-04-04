# Spring AI 入门实践：Spring AI 与百度千帆集成

## 概述

百度千帆大模型平台是百度智能云推出的一站式大模型开发和服务平台，提供了ERNIE系列大语言模型。Spring AI 提供了对百度千帆 API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 ERNIE-Bot、ERNIE-Bot-turbo 等模型进行文本生成、对话和嵌入计算。

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

## 核心功能

### 1. 文本生成

使用百度千帆进行文本生成：

```java
package com.github.teachingai.baidu.qianfan.controller;

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
package com.github.teachingai.baidu.qianfan.controller;

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

## 相关资源

- [百度千帆官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/index.html)
- [ERNIE系列模型介绍](https://cloud.baidu.com/product/wenxinworkshop)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-qianfan)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 OpenAI 集成](25、Spring AI 入门实践：Spring AI 与 OpenAI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 文本嵌入](5、Spring AI 入门实践：Spring AI 文本嵌入（Embeddings）.md)