# Spring AI 入门实践：Spring AI 与华为 Pangu 大模型集成

## 概述

华为 Pangu 大模型是华为自主研发的通用人工智能模型，具有强大的中文理解和生成能力。Spring AI 提供了对华为 Pangu 大模型的集成支持，使得开发者可以在 Spring 应用中轻松使用 Pangu 模型的各种功能。

## 准备工作

### 1. 获取华为云 API 密钥

首先，您需要在华为云注册账号并获取 API 密钥：

1. 登录 [华为云控制台](https://console.huaweicloud.com/)
2. 创建 IAM 用户并赋予适当的权限
3. 获取 API 密钥（Access Key 和 Secret Key）
4. 开通 Pangu 大模型服务

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-huaweiai-pangu</artifactId>
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

在 `application.properties` 文件中配置华为云相关设置：

```properties
# 华为云配置
spring.ai.huaweiai-pangu.access-key=your-access-key
spring.ai.huaweiai-pangu.secret-key=your-secret-key
spring.ai.huaweiai-pangu.region=cn-north-4
spring.ai.huaweiai-pangu.chat.enabled=true
spring.ai.huaweiai-pangu.chat.model=pangu-3.5
spring.ai.huaweiai-pangu.chat.options.temperature=0.7
```

## 核心功能

### 1. 文本生成

使用华为 Pangu 大模型进行文本生成：

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

### 2. 多轮对话

使用华为 Pangu 大模型进行多轮对话：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.ai.chat.prompt.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    private final List<Object> messageHistory = new ArrayList<>();

    @PostMapping("/chat/multi")
    public String multiChat(@RequestBody String message) {
        // 添加用户消息到历史
        messageHistory.add(new UserMessage(message));

        // 创建包含历史消息的提示
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("你是一个智能助手，用中文回答用户问题。"),
                messageHistory.toArray(new Object[0])
            )
        );

        // 调用模型
        String response = chatClient.call(prompt).getResult().getOutput().getContent();

        // 添加助手回复到历史
        messageHistory.add(new AssistantMessage(response));

        return response;
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-huaweiai-pangu/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── huaweiai/
│   │   │                   └── pangu/
│   │   │                       ├── controller/
│   │   │                       │   └── ChatController.java
│   │   │                       └── SpringAiHuaweiAiPanguApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── huaweiai/
│                           └── pangu/
│                               └── SpringAiHuaweiAiPanguApplicationTests.java
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
public class SpringAiHuaweiAiPanguApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiHuaweiAiPanguApplication.class, args);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiHuaweiAiPanguApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=你好，介绍一下华为 Pangu 大模型"
   ```
3. **测试多轮对话**：
   ```bash
   curl -X POST http://localhost:8080/chat/multi \
     -H "Content-Type: text/plain" \
     -d "什么是人工智能？"
   curl -X POST http://localhost:8080/chat/multi \
     -H "Content-Type: text/plain" \
     -d "它有什么应用？"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的 Pangu 模型版本
2. **参数调优**：根据应用场景调整温度等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [华为 Pangu 大模型官方文档](https://www.huaweicloud.com/product/pangu.html)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [华为云 AI 服务](https://www.huaweicloud.com/product/ai.html)