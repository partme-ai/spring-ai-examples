# Spring AI 入门实践：Spring AI 与 Moonshot AI（月之暗面）集成

## 概述

Moonshot AI（月之暗面）是中国领先的人工智能公司，推出了 Kimi Chat 等知名产品。Spring AI 提供了对 Moonshot AI API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Moonshot 的大语言模型进行文本生成、对话和嵌入计算。

## 准备工作

### 1. Moonshot AI 账号配置

首先，您需要配置 Moonshot AI 账号并获取 API Key：

1. 访问 [Moonshot AI 平台](https://platform.moonshot.cn/)
2. 注册账号并登录
3. 在 API Keys 页面创建新的 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springaicommunity</groupId>
        <artifactId>spring-ai-autoconfigure-model-moonshot</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
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

### 3. 配置 Moonshot AI 连接

在 `application.properties` 文件中配置 Moonshot AI 相关设置：

```properties
# Moonshot AI 基础配置
spring.ai.moonshotai.api-key=你的API密钥

# Chat 模型配置
spring.ai.moonshotai.chat.enabled=true
spring.ai.moonshotai.chat.options.model=moonshot-v1-8k

# 嵌入模型配置
spring.ai.moonshotai.embedding.enabled=true
spring.ai.moonshotai.embedding.options.model=embedding-2
```

## 核心功能

### 1. 文本生成

使用 Moonshot AI 进行文本生成：

```java
package com.github.partmeai.moonshotai.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Moonshot（月之暗面）聊天示例
 */
@RestController
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map<String, Object> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

### 2. 提示模板使用

使用 Spring AI 的提示模板功能：

```java
import org.springframework.ai.chat.model.ChatModel;
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
    public List<Generation> prompt(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
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
    public Flux<ChatResponse> chatCompletions(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

### 4. 工具调用（Function Calling）

Moonshot AI 支持工具调用功能，可以与外部工具集成：

```java
package com.github.partmeai.moonshotai.functions;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    @Description("获取指定城市的天气信息")
    public FunctionCallbackWrapper<GetWeatherFunction.Request, GetWeatherFunction.Response> weatherFunction() {
        return FunctionCallbackWrapper.builder(new GetWeatherFunction())
                .withName("getWeather")
                .withDescription("获取指定城市的天气信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }

    @Bean
    @Description("获取指定地区的PC在线信息")
    public FunctionCallbackWrapper<PconlineRegionFunction.Request, PconlineRegionFunction.Response> pconlineRegionFunction() {
        return FunctionCallbackWrapper.builder(new PconlineRegionFunction())
                .withName("getPconlineRegion")
                .withDescription("获取指定地区的PC在线信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }
}
```

### 5. 文本嵌入

使用 Moonshot AI 进行文本嵌入计算：

```java
package com.github.partmeai.moonshotai.controller;

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
    public Map<String, Object> embedding(@RequestParam(value = "message", defaultValue = "需要嵌入的文本") String message) {
        return Map.of("embeddings", embeddingModel.embed(message));
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-moonshotai/
├── src/main/java/com/github/teachingai/moonshotai/
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── EmbeddingController.java
│   ├── functions/
│   │   ├── FunctionConfig.java
│   │   ├── GetWeatherFunction.java
│   │   └── PconlineRegionFunction.java
│   ├── router/
│   │   └── RouterFunctionConfig.java
│   └── SpringAiMoonshotAiApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-moonshotai
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
class MoonshotAiIntegrationTest {

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
2. **模型选择**：根据需求选择合适的模型，moonshot-v1-8k 适用于大多数场景
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额
5. **性能优化**：对于批量处理，考虑使用异步调用

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 无效或过期 | 检查 API Key 是否正确，是否有足够的余额 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |
| 模型不可用 | 指定的模型不存在或不可访问 | 检查模型名称是否正确 |

## 相关资源

- [Moonshot AI 官方文档](https://platform.moonshot.cn/docs)
- [Spring AI Moonshot 集成](https://docs.spring.io/spring-ai/reference/api/clients/moonshot.html)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-moonshotai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 OpenAI 集成](25、Spring AI 入门实践：Spring AI 与 OpenAI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 工具调用](4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）.md)