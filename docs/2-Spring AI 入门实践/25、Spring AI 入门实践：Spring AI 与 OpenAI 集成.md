# Spring AI 入门实践：Spring AI 与 OpenAI 集成

## 概述

OpenAI 是人工智能领域的领先公司，提供了强大的 GPT 系列大语言模型。Spring AI 提供了对 OpenAI API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 ChatGPT、DALL-E 等模型进行文本生成、图像生成和嵌入计算。

## 准备工作

### 1. OpenAI 账号配置

首先，您需要配置 OpenAI 账号并获取 API Key：

1. 访问 [OpenAI 平台](https://platform.openai.com/)
2. 注册账号并登录
3. 在 API Keys 页面创建新的 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
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

### 3. 配置 OpenAI 连接

在 `application.properties` 文件中配置 OpenAI 相关设置：

```properties
# OpenAI 基础配置
spring.ai.openai.base-url=http://192.168.3.100:31492/v1
spring.ai.openai.api-key=sk-你的API密钥

# Chat 模型配置
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.8

# 图像生成配置
spring.ai.openai.image.enabled=true
spring.ai.openai.image.options.model=dall-e-3
spring.ai.openai.image.options.n=1
spring.ai.openai.image.options.quality=standard
spring.ai.openai.image.options.response_format=URL
spring.ai.openai.image.options.size=1024x1024

# 重试配置
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=2000
spring.ai.retry.backoff.multiplier=2
spring.ai.retry.backoff.max-interval=5000
spring.ai.retry.on-client-errors=true
```

## 核心功能

### 1. 文本生成

使用 OpenAI 进行文本生成：

```java
package com.github.partmeai.openai.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final OpenAiChatModel chatModel;

    @Autowired
    public ChatController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 文本生成
     * @param message 提示词
     * @return 响应结果
     */
    @GetMapping("/ai/generate")
    @Operation(summary = "文本生成")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好！") String message) {
        try {
            String response = chatModel.call(message);
            return Map.of(
                "success", true,
                "generation", response,
                "message", "Generated successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "message", "Generation failed"
            );
        }
    }
}
```

### 2. 流式文本生成

支持流式响应，适用于实时对话场景：

```java
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final OpenAiChatModel chatModel;

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }
}
```

### 3. 图像生成

使用 DALL-E 模型进行图像生成：

```java
package com.github.partmeai.openai.controller;

import org.springframework.ai.image.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ImageGenController {

    private final ImageModel imageModel;

    public ImageGenController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @PostMapping("/imagegen")
    public String imageGen(@RequestBody ImageGenRequest request) {
        ImageOptions options = ImageOptionsBuilder.builder()
                .model("dall-e-3")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(request.prompt(), options);
        ImageResponse response = imageModel.call(imagePrompt);
        String imageUrl = response.getResult().getOutput().getUrl();

        return "redirect:" + imageUrl;
    }
}
```

图像生成请求类：

```java
package com.github.partmeai.openai.controller;

public class ImageGenRequest {
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
```

### 4. 文本嵌入

使用 OpenAI 进行文本嵌入计算：

```java
package com.github.partmeai.openai.controller;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmbeddingController {

    private final OpenAiEmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingController(OpenAiEmbeddingModel embeddingModel) {
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
spring-ai-openai/
├── src/main/java/com/github/teachingai/openai/
│   ├── controller/
│   │   ├── ChatController.java
│   │   ├── EmbeddingController.java
│   │   ├── ImageGenController.java
│   │   └── ImageGenRequest.java
│   ├── agent/
│   │   └── Todo.java
│   ├── config/
│   │   └── Config.java
│   ├── finetune/
│   │   └── Todo.java
│   ├── rag/
│   │   └── Todo.java
│   ├── service/
│   │   └── MockWeatherService.java
│   └── SpringAiOpenAiApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-openai
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /ai/generate?message=你好` - 文本生成
   - `GET /ai/generateStream?message=讲个故事` - 流式文本生成
   - `POST /imagegen` - 图像生成
   - `GET /v1/embedding?message=需要嵌入的文本` - 文本嵌入

## 测试方法

### 单元测试

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.openai.OpenAiChatModel;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenAiIntegrationTest {

    @Autowired
    private OpenAiChatModel chatModel;

    @Test
    void testChatGeneration() {
        String response = chatModel.call("你好");
        assertThat(response).isNotEmpty();
    }
}
```

### 集成测试

使用 `TestRestTemplate` 或 `WebTestClient` 进行端点测试。

## 最佳实践

1. **API Key 管理**：不要将 API Key 硬编码在代码中，使用环境变量或配置中心
2. **错误处理**：合理处理 OpenAI API 的限流、超时等异常
3. **成本控制**：监控 API 使用量，设置使用限额
4. **性能优化**：对于批量处理，考虑使用异步调用和缓存
5. **模型选择**：根据需求选择合适的模型（GPT-3.5-turbo 性价比高，GPT-4 能力更强）

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 无效或过期 | 检查 API Key 是否正确，是否有足够的余额 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或 OpenAI 服务不稳定 | 检查网络连接，增加超时时间配置 |
| 模型不可用 | 指定的模型不存在或不可访问 | 检查模型名称是否正确，确认账号是否有访问权限 |

## 相关资源

- [OpenAI 官方文档](https://platform.openai.com/docs/api-reference)
- [Spring AI OpenAI 集成文档](https://docs.spring.io/spring-ai/reference/api/clients/openai.html)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-openai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 Azure OpenAI 集成](16、Spring AI 入门实践：Spring AI 与 Azure OpenAI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 图片生成 API](6、Spring AI 入门实践：Spring AI 图片生成（Image Generation API）.md)