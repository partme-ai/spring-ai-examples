# Spring AI 入门实践：Spring AI 与 MiniMax 集成

## 概述

MiniMax 是中国领先的人工智能公司，提供了强大的大语言模型。Spring AI 提供了对 MiniMax API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 MiniMax 模型进行文本生成和对话。

## 准备工作

### 1. MiniMax 账号配置

首先，您需要配置 MiniMax 账号并获取 API Key：

1. 访问 [MiniMax 开放平台](https://www.minimaxi.com/)
2. 注册账号并登录
3. 创建应用并获取 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-minimax</artifactId>
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

### 3. 配置 MiniMax 连接

在 `application.properties` 文件中配置 MiniMax 相关设置：

```properties
# MiniMax 配置
spring.ai.minimax.api-key=你的API密钥
spring.ai.minimax.chat.enabled=true
spring.ai.minimax.chat.options.model=abab5.5-chat
```

## 核心功能

### 1. 文本生成

使用 MiniMax 进行文本生成：

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
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

## 完整示例

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-minimax
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成

## 相关资源

- [MiniMax 官方文档](https://www.minimaxi.com/document/)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-minimax)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与阶跃星辰集成](31、Spring AI 入门实践：Spring AI 与阶跃星辰集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)