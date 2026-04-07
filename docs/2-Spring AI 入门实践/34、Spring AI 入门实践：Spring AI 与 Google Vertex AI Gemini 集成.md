# Spring AI 入门实践：Spring AI 与 Google Vertex AI Gemini 集成

## 概述

Google Vertex AI Gemini 是 Google Cloud 提供的大模型服务平台，提供了 Gemini 系列大语言模型。Spring AI 提供了对 Vertex AI Gemini API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Gemini Pro、Gemini Ultra 等模型进行文本生成、对话和多模态处理。

## 准备工作

### 1. Google Cloud 账号配置

首先，您需要配置 Google Cloud 账号并获取访问权限：

1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建项目或选择现有项目
3. 启用 Vertex AI API
4. 配置服务账号并下载 JSON 密钥文件
5. 确保账号有足够的配额

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-vertex-ai-gemini</artifactId>
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

### 3. 配置 Vertex AI Gemini 连接

在 `application.properties` 文件中配置 Vertex AI 相关设置：

```properties
# Google Cloud 配置
spring.ai.vertex.ai.gemini.project-id=你的项目ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.credentials-uri=file:///path/to/credentials.json

# Gemini 模型配置
spring.ai.vertex.ai.gemini.chat.enabled=true
spring.ai.vertex.ai.gemini.chat.options.model=gemini-pro
```

## 核心功能

### 1. 文本生成

使用 Vertex AI Gemini 进行文本生成：

```java
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
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

## 完整示例

### 运行应用

1. 配置好 `application.properties` 中的项目ID和凭证
2. 启动应用：

```bash
cd spring-ai-vertexai-gemini
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=Hello` - 文本生成

## 相关资源

- [Google Vertex AI 官方文档](https://cloud.google.com/vertex-ai/docs)
- [Gemini 模型介绍](https://cloud.google.com/vertex-ai/docs/generative-ai/model-reference/gemini)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-vertexai-gemini)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 Mistral AI 集成](33、Spring AI 入门实践：Spring AI 与 Mistral AI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)