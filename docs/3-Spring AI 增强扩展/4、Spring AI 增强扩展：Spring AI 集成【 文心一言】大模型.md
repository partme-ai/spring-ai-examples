# Spring AI 增强扩展：Spring AI 集成【 文心一言】大模型

## 概述

文心一言（ERNIE Bot）是百度开发的通用人工智能模型，具有强大的中文理解和生成能力。Spring AI 提供了对文心一言的集成支持，使得开发者可以在 Spring 应用中轻松使用文心一言的各种功能。

## 准备工作

### 1. 获取百度千帆 API 密钥

首先，您需要在百度千帆平台注册账号并获取 API 密钥：

1. 访问 [百度千帆平台](https://cloud.baidu.com/product/wenxinworkshop)
2. 注册账号并登录
3. 创建应用并获取 API 密钥

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-qianfan-spring-boot-starter</artifactId>
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

### 3. 配置 API 密钥

在 `application.properties` 文件中配置百度千帆相关设置：

```properties
# 百度千帆配置
spring.ai.qianfan.api-key=your-api-key
spring.ai.qianfan.secret-key=your-secret-key
spring.ai.qianfan.chat.enabled=true
spring.ai.qianfan.chat.model=ERNIE-Bot-4
spring.ai.qianfan.chat.options.temperature=0.7

# Embedding 配置
spring.ai.qianfan.embedding.enabled=true
spring.ai.qianfan.embedding.model=text-embedding-v1
```

## 核心功能

### 1. 文本生成

使用文心一言进行文本生成：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一个智能助手，用中文回答用户问题。"),
            new UserMessage(message)
        ));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 2. 文本嵌入

使用文心一言进行文本嵌入：

```java
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddingController {

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostMapping("/embed")
    public List<Double> embed(@RequestBody String text) {
        EmbeddingRequest request = EmbeddingRequest.from(text);
        EmbeddingResponse response = embeddingModel.embed(request);
        return response.getResult().getOutput();
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-qianfan/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── qianfan/
│   │   │                   ├── controller/
│   │   │                   │   ├── ChatController.java
│   │   │                   │   └── EmbeddingController.java
│   │   │                   └── SpringAiQianfanApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── qianfan/
│                           └── SpringAiQianfanApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
└── mvnw.cmd
```

### 主应用类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiQianfanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiQianfanApplication.class, args);
    }

}
```

### 控制器类

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一个智能助手，用中文回答用户问题。"),
            new UserMessage(message)
        ));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 测试方法

1. **启动应用**：运行 `SpringAiQianfanApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=你好，介绍一下文心一言"
   ```
3. **测试文本嵌入**：
   ```bash
   curl -X POST http://localhost:8080/embed \
     -H "Content-Type: text/plain" \
     -d "Spring AI 集成文心一言"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的文心一言模型版本
2. **参数调优**：根据应用场景调整温度等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [百度千帆官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [文心一言官方网站](https://yiyan.baidu.com/)

## 深度用法

百度千帆 / 文心产品线以[官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/)为准，包含更多高级功能和配置选项。