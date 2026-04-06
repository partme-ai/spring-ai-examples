# Spring AI 入门实践：Spring AI 与华为 AI Gallery 集成

## 概述

华为 AI Gallery 是华为云提供的人工智能模型和算法的聚集地，提供了丰富的模型资源。Spring AI 提供了对华为 AI Gallery 的集成支持，使得开发者可以在 Spring 应用中轻松使用华为 AI Gallery 中的模型。

## 准备工作

### 1. 获取华为云 API 密钥

首先，您需要在华为云注册账号并获取 API 密钥：

1. 登录 [华为云控制台](https://console.huaweicloud.com/)
2. 创建 IAM 用户并赋予适当的权限
3. 获取 API 密钥（Access Key 和 Secret Key）

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-huaweiai-gallery</artifactId>
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
spring.ai.huaweiai-gallery.access-key=your-access-key
spring.ai.huaweiai-gallery.secret-key=your-secret-key
spring.ai.huaweiai-gallery.region=cn-north-4
spring.ai.huaweiai-gallery.chat.enabled=true
spring.ai.huaweiai-gallery.chat.options.temperature=0.7
```

## 核心功能

### 1. 文本生成

使用华为 AI Gallery 进行文本生成：

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
            new SystemMessage("You are a helpful assistant."),
            new UserMessage(message)
        ));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-huaweiai-gallery/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   └── SpringAiGemmaApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── ollama/
│                           └── SpringAiGemmaApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── 华为-AI应用开发SDK使用指南.docx
```

### 主应用类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiGemmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiGemmaApplication.class, args);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiGemmaApplication` 类
2. **测试文本生成**：
   ```bash
   curl "http://localhost:8080/chat?message=Hello, how are you?"
   ```

## 最佳实践

1. **模型选择**：根据具体需求选择合适的华为 AI Gallery 模型
2. **参数调优**：根据应用场景调整温度等参数
3. **提示设计**：精心设计系统提示，明确助手的角色和行为
4. **错误处理**：实现适当的错误处理和重试机制
5. **成本控制**：监控 API 使用情况，设置合理的使用限制
6. **安全考虑**：避免在提示中包含敏感信息

## 相关资源

- [华为 AI Gallery 官方文档](https://www.huaweicloud.com/product/ai_gallery.html)
- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [华为云 AI 服务](https://www.huaweicloud.com/product/ai.html)