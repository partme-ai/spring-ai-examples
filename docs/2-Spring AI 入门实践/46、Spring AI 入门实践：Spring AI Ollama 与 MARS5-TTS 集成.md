# Spring AI 入门实践：Spring AI Ollama 与 MARS5-TTS 集成

## 概述

MARS5-TTS 是 CAMB.AI 开源的高质量文本转语音模型，支持多种语言和声音风格。通过整合 MARS5-TTS 与本地 Ollama 服务器，可以实现高质量的文本转语音音频响应。

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

### 2. MARS5-TTS 安装与配置

安装和运行 MARS5-TTS：

1. 访问 [MARS5-TTS 项目](https://github.com/camb-ai/mars5-tts)
2. 按照项目说明安装和配置
3. 启动 MARS5-TTS 服务

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

### 1. 高质量语音合成

使用 MARS5-TTS 进行高质量语音合成：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MARS5TTSController {

    private final MARS5TTSService mars5TTSService;

    @PostMapping("/tts/mars5")
    public byte[] generateSpeech(@RequestBody String text) {
        return mars5TTSService.textToSpeech(text);
    }
}
```

### 2. 对话语音合成

将 Ollama 对话转换为高质量语音：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatMARS5Controller {

    private final ChatModel chatModel;
    private final MARS5TTSService mars5TTSService;

    @GetMapping("/chat/mars5-tts")
    public byte[] chatAndSpeak(@RequestParam String message) {
        // 1. 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 2. 使用 MARS5-TTS 转换为语音
        return mars5TTSService.textToSpeech(response);
    }
}
```

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 MARS5-TTS 服务正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-audio-mars5tts
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /tts/mars5` - 高质量语音合成
   - `GET /chat/mars5-tts?message=你好` - 对话语音合成

## 最佳实践

1. **模型选择**：选择适合应用场景的 MARS5 模型
2. **参数调优**：调整合成参数优化音质
3. **性能优化**：使用 GPU 加速语音合成
4. **错误处理**：处理服务不可用的情况

## 相关资源

- [MARS5-TTS 官方项目](https://github.com/camb-ai/mars5-tts)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-audio-mars5tts)

## 扩展阅读

- [Spring AI Ollama 与 EmotiVoice 集成](45、Spring AI 入门实践：Spring AI Ollama 与 EmotiVoice 集成.md)
- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)