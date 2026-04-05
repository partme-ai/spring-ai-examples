# Spring AI 入门实践：Spring AI 与 IBM Watsonx AI 集成

## 概述

IBM Watsonx AI 是 IBM 提供的企业级人工智能平台，提供了多种大语言模型。Spring AI 提供了对 IBM Watsonx AI API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Watsonx 模型进行文本生成和对话。

## 准备工作

### 1. IBM Cloud 账号配置

首先，您需要配置 IBM Cloud 账号并获取访问权限：

1. 访问 [IBM Cloud](https://cloud.ibm.com/)
2. 注册账号并登录
3. 创建 Watsonx AI 服务实例
4. 获取 API Key 和项目ID
5. 确保账号有足够的配额

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-watsonx-ai</artifactId>
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

### 3. 配置 IBM Watsonx AI 连接

在 `application.properties` 文件中配置 Watsonx AI 相关设置：

```properties
# IBM Watsonx AI 配置
spring.ai.watsonx.api-key=你的API密钥
spring.ai.watsonx.project-id=你的项目ID
spring.ai.watsonx.url=https://us-south.ml.cloud.ibm.com

# Chat 模型配置
spring.ai.watsonx.chat.enabled=true
spring.ai.watsonx.chat.options.model=ibm/granite-13b-chat-v2
```

## 核心功能

### 1. 文本生成

使用 IBM Watsonx AI 进行文本生成：

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

1. 配置好 `application.properties` 中的 API Key 和项目ID
2. 启动应用：

```bash
cd spring-ai-watsonxai
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=Hello` - 文本生成

## 相关资源

- [IBM Watsonx AI 官方文档](https://cloud.ibm.com/docs/watsonx)
- [Granite 模型介绍](https://www.ibm.com/products/watsonx-ai)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-watsonxai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 Stability AI 集成](35、Spring AI 入门实践：Spring AI 与 Stability AI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)