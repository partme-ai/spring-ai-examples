# Spring AI 入门实践：Spring AI Ollama 图像识别（Vision）

## 概述

图像识别（Vision）是多模态大语言模型的重要功能，允许模型理解和分析图像内容。通过 Ollama，我们可以在本地运行支持视觉的模型，并使用 Spring AI 构建图像识别应用。

## 准备工作

### 1. Ollama 安装与配置

首先，您需要在本地计算机上运行 Ollama：

1. 访问 [Ollama 官网](https://ollama.com/)
2. 下载并安装适合您操作系统的版本
3. 启动 Ollama 服务
4. 拉取支持视觉的模型：

```bash
ollama pull llava
ollama pull bakllava
ollama pull moondream
```

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-ollama</artifactId>
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

### 3. 配置 Ollama 连接

在 `application.properties` 文件中配置 Ollama 相关设置：

```properties
# Ollama 基础配置
spring.ai.ollama.base-url=http://localhost:11434

# Chat 模型配置
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=llava
```

## 核心功能

### 1. 图像识别基础

使用 Ollama 进行图像识别：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class VisionController {

    private final ChatModel chatModel;

    @Autowired
    public VisionController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/vision/analyze")
    public String analyzeImage(@RequestParam("file") MultipartFile file, 
                                @RequestParam(value = "question", defaultValue = "描述这张图片") String question) throws IOException {
        // 将图片转换为 Base64 或 URL
        String imageUrl = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(file.getBytes());
        
        UserMessage userMessage = new UserMessage(question, List.of(new Media(imageUrl)));
        Prompt prompt = new Prompt(List.of(userMessage));
        
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 2. 从 URL 识别图像

从网络 URL 识别图像：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VisionController {

    private final ChatModel chatModel;

    @GetMapping("/vision/analyze-url")
    public String analyzeImageFromUrl(@RequestParam String imageUrl,
                                       @RequestParam(value = "question", defaultValue = "描述这张图片") String question) {
        UserMessage userMessage = new UserMessage(question, List.of(new Media(imageUrl)));
        Prompt prompt = new Prompt(List.of(userMessage));
        
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 3. 多图像分析

同时分析多张图像：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MultiVisionController {

    private final ChatModel chatModel;

    @PostMapping("/vision/analyze-multiple")
    public String analyzeMultipleImages(@RequestParam("files") MultipartFile[] files,
                                         @RequestParam(value = "question", defaultValue = "比较这些图片") String question) throws IOException {
        List<Media> mediaList = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String imageUrl = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(file.getBytes());
            mediaList.add(new Media(imageUrl));
        }
        
        UserMessage userMessage = new UserMessage(question, mediaList);
        Prompt prompt = new Prompt(List.of(userMessage));
        
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

## 推荐模型

### LLaVA

LLaVA (Large Language and Vision Assistant) 是一个优秀的开源视觉语言模型：

```bash
ollama run llava
ollama run llava:13b
ollama run llava:34b
```

### BakLLaVA

BakLLaVA 是基于 Mistral 的视觉模型：

```bash
ollama run bakllava
```

### Moondream

Moondream 是一个小型高效的视觉模型：

```bash
ollama run moondream
```

## 完整示例

### 项目结构

```
spring-ai-ollama-vision/
├── src/main/java/com/github/teachingai/ollama/
│   ├── controller/
│   │   └── VisionController.java
│   └── SpringAiOllamaApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-vision
mvn spring-boot:run
```

3. 访问 API 端点：
   - `POST /vision/analyze` - 上传图片进行分析
   - `GET /vision/analyze-url?imageUrl=...` - 从 URL 分析图片
   - `POST /vision/analyze-multiple` - 分析多张图片

## 最佳实践

1. **图像预处理**：优化图像大小和质量
2. **提示设计**：设计清晰的图像分析提示
3. **错误处理**：处理图像加载和处理错误
4. **性能优化**：缓存常见图像的分析结果
5. **隐私保护**：注意图像数据的隐私和安全

## 相关资源

- [Ollama 官方文档](https://ollama.com/docs)
- [LLaVA 模型介绍](https://llava-vl.github.io/)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-vision)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)
- [Spring AI 图片生成](6、Spring AI 入门实践：Spring AI 图片生成（Image Generation API）.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)