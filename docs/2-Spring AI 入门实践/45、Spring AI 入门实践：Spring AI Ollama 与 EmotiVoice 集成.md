# Spring AI 入门实践：Spring AI Ollama 与 EmotiVoice 集成

## 概述

EmotiVoice 是网易有道开源的多语言文本转语音引擎，支持情感语音合成。通过整合 EmotiVoice 与本地 Ollama 服务器，可以实现带有情感表达的文本转语音音频响应。

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

### 2. EmotiVoice 安装与配置

安装和运行 EmotiVoice：

1. 访问 [EmotiVoice 项目](https://github.com/netease-youdao/EmotiVoice)
2. 按照项目说明安装和配置
3. 启动 EmotiVoice 服务

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

## 核心功能

### 1. 情感语音合成

使用 EmotiVoice 进行情感语音合成：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmotiVoiceController {

    private final EmotiVoiceService emotiVoiceService;

    @PostMapping("/tts/emoti")
    public byte[] generateEmotionalSpeech(@RequestBody EmotiRequest request) {
        return emotiVoiceService.textToSpeech(request.getText(), request.getEmotion());
    }
}
```

### 2. 对话情感语音

将 Ollama 对话转换为情感语音：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatEmotiController {

    private final ChatModel chatModel;
    private final EmotiVoiceService emotiVoiceService;

    @GetMapping("/chat/emoti-tts")
    public byte[] chatAndSpeakWithEmotion(@RequestParam String message) {
        // 1. 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 2. 使用 EmotiVoice 转换为情感语音
        return emotiVoiceService.textToSpeech(response, "neutral");
    }
}
```

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 EmotiVoice 服务正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-audio-emoti
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /tts/emoti` - 情感语音合成
   - `GET /chat/emoti-tts?message=你好` - 对话情感语音

## 最佳实践

1. **情感选择**：根据文本内容选择合适的情感
2. **语音质量**：调整参数优化语音质量
3. **多语言支持**：利用 EmotiVoice 的多语言能力
4. **错误处理**：处理服务不可用的情况

## 相关资源

- [EmotiVoice 官方项目](https://github.com/netease-youdao/EmotiVoice)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-audio-emoti)

## 扩展阅读

- [Spring AI Ollama 与 Edge-TTS 集成](44、Spring AI 入门实践：Spring AI Ollama 与 Edge-TTS 集成.md)
- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)