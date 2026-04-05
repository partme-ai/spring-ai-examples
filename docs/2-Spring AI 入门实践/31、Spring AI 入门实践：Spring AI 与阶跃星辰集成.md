# Spring AI 入门实践：Spring AI 与阶跃星辰集成

## 概述

阶跃星辰（StepFun）是中国新兴的人工智能公司，推出了 Step 系列大语言模型。Spring AI 提供了对阶跃星辰 API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Step 系列模型进行文本生成和对话。

## 准备工作

### 1. 阶跃星辰账号配置

首先，您需要配置阶跃星辰账号并获取 API Key：

1. 访问 [阶跃星辰开放平台](https://platform.stepfun.com/)
2. 注册账号并登录
3. 创建应用并获取 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-stepfun</artifactId>
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

### 3. 配置阶跃星辰连接

在 `application.properties` 文件中配置阶跃星辰相关设置：

```properties
# 阶跃星辰配置
spring.ai.stepfun.api-key=你的API密钥

# Chat 模型配置
spring.ai.stepfun.chat.enabled=true
spring.ai.stepfun.chat.options.model=step-1-8k
```

## 核心功能

### 1. 文本生成

使用阶跃星辰进行文本生成：

```java
package com.github.hiwepy.stepfun.controller;

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
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好") String message) {
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
    public Flux<ChatResponse> chatCompletions(@RequestParam(value = "message", defaultValue = "讲个故事") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-stepfun/
├── src/main/java/com/github/hiwepy/stepfun/
│   ├── controller/
│   │   └── ChatController.java
│   └── SpringStepFunAiApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-stepfun
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成
   - `POST /v1/chat/completions?message=讲个故事` - 流式对话

## 最佳实践

1. **API Key 管理**：使用环境变量或配置中心管理敏感信息
2. **模型选择**：根据需求选择合适的模型
   - step-1-8k：适合一般对话场景
   - step-1-32k：适合长文本处理
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 无效或过期 | 检查 API Key 是否正确 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |

## 相关资源

- [阶跃星辰官方文档](https://platform.stepfun.com/docs)
- [Step系列模型介绍](https://platform.stepfun.com/)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-stepfun)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与商汤日日新集成](30、Spring AI 入门实践：Spring AI 与商汤日日新集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)