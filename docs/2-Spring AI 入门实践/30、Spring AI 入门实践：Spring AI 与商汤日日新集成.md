# Spring AI 入门实践：Spring AI 与商汤日日新集成

## 概述

商汤科技（SenseTime）推出的 SenseNova（日日新）大模型平台，提供了强大的大语言模型能力。Spring AI 提供了对商汤 SenseNova API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用日日新系列模型进行文本生成、对话等任务。

## 准备工作

### 1. 商汤开放平台账号配置

首先，您需要配置商汤开放平台账号并获取 API Key：

1. 访问 [商汤开放平台](https://platform.sensenova.cn/)
2. 注册账号并登录
3. 创建应用并获取 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-sensenova</artifactId>
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

### 3. 配置商汤 SenseNova 连接

在 `application.properties` 文件中配置商汤 SenseNova 相关设置：

```properties
# 商汤 SenseNova 配置
spring.ai.sensenova.api-key=你的API密钥

# Chat 模型配置
spring.ai.sensenova.chat.enabled=true
spring.ai.sensenova.chat.options.model=nova-ptc-xl-v1

# 嵌入模型配置（如支持）
spring.ai.sensenova.embedding.enabled=true
```

## 核心功能

### 1. 文本生成

使用商汤 SenseNova 进行文本生成：

```java
package com.github.hiwepy.sensenova.controller;

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
    public Map<String, Object> generate(
            @RequestParam(value = "message", defaultValue = "你好") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

### 2. 流式对话

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
            @RequestParam(value = "message", defaultValue = "讲个故事") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

### 3. 文本嵌入

使用商汤 SenseNova 进行文本嵌入计算：

```java
package com.github.hiwepy.sensenova.controller;

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
    public Map<String, Object> embedding(
            @RequestParam(value = "message", defaultValue = "需要嵌入的文本") String message) {
        return Map.of("embeddings", embeddingModel.embed(message));
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-sensenova/
├── src/main/java/com/github/hiwepy/sensenova/
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── EmbeddingController.java
│   ├── router/
│   │   └── RouterFunctionConfig.java
│   └── SpringAiSensenovaApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-sensenova
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成
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
class SensenovaIntegrationTest {

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
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额
5. **性能优化**：对于批量处理，考虑使用异步调用

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 鉴权失败 | API Key 无效或过期 | 检查 API Key 是否正确 |
| 模型不可用 | 指定的模型不存在或未开通 | 在控制台确认模型名称与开通状态 |
| 请求超时 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |

## 相关资源

- [商汤开放平台官方文档](https://platform.sensenova.cn/docs)
- [SenseNova 模型介绍](https://platform.sensenova.cn/)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-sensenova)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与智谱AI集成](29、Spring AI 入门实践：Spring AI 与智谱AI集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)