# Spring AI 入门实践：Spring AI Ollama 与 ChatTTS 集成

## 概述

ChatTTS 是一款优秀的开源文本转语音（TTS）模型，支持中英文混合。通过整合 ChatTTS 与本地 Ollama 服务器，可以实现离线模式下的文本转语音音频响应，构建完整的语音交互应用。

## 准备工作

### 1. Ollama 安装与配置

首先，您需要在本地计算机上运行 Ollama：

1. 访问 [Ollama 官网](https://ollama.com/)
2. 下载并安装适合您操作系统的版本
3. 启动 Ollama 服务
4. 拉取所需的模型：

```bash
ollama pull qwen3.5
ollama pull llama3
```

### 2. ChatTTS 安装与配置

ChatTTS 不提供API功能，需要安装 ChatTTS-ui：

1. 访问 [ChatTTS-ui 项目](https://github.com/jianchang512/ChatTTS-ui)
2. 按照项目说明安装和配置
3. 启动 ChatTTS-ui 服务

### 3. 添加依赖

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

### 4. 配置连接

在 `application.properties` 文件中配置相关设置：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3.5

# ChatTTS 配置
chattts.api.url=http://localhost:8080/api/tts
```

## 核心功能

### 1. 文本转语音基础

使用 ChatTTS 进行文本转语音：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TTSController {

    private final ChatModel chatModel;
    private final ChatTTSService chatTTSService;

    @PostMapping("/tts/generate")
    public byte[] generateSpeech(@RequestBody String text) {
        // 使用 ChatTTS 生成语音
        return chatTTSService.textToSpeech(text);
    }
}
```

### 2. 对话转语音

将 Ollama 对话转换为语音：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatTTSController {

    private final ChatModel chatModel;
    private final ChatTTSService chatTTSService;

    @GetMapping("/chat/tts")
    public byte[] chatAndSpeak(@RequestParam String message) {
        // 1. 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 2. 使用 ChatTTS 转换为语音
        return chatTTSService.textToSpeech(response);
    }
}
```

## 推荐模型

### Qwen3.5

Qwen3.5 是阿里巴巴推出的最新系列大型语言模型，在推理、代码生成和数学能力方面有显著提升：

```bash
ollama run qwen3.5:0.5b
ollama run qwen3.5:1.8b  
ollama run qwen3.5:4b
ollama run qwen3.5:7b
ollama run qwen3.5:14b
ollama run qwen3.5:32b
ollama run qwen3.5:72b
```

### Llama 3

Meta 的最新开源模型：

```bash
ollama run llama3
ollama run llama3:70b
```

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 ChatTTS-ui 服务正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-audio-chattts
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /tts/generate` - 文本转语音
   - `GET /chat/tts?message=你好` - 对话转语音

## 最佳实践

1. **音频缓存**：缓存常见文本的语音结果
2. **异步处理**：使用异步处理提高响应速度
3. **错误处理**：处理 TTS 服务不可用的情况
4. **音质优化**：调整 ChatTTS 参数优化音质

## 相关资源

- [ChatTTS 官方项目](https://github.com/2noise/ChatTTS)
- [ChatTTS-ui 项目](https://github.com/jianchang512/ChatTTS-ui)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-audio-chattts)

## 扩展阅读

- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)
- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)