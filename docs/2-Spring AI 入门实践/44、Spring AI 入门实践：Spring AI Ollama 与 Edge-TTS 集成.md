# Spring AI 入门实践：Spring AI Ollama 与 Edge-TTS 集成

## 概述

Edge-TTS 是微软 Edge 浏览器的文本转语音引擎的开源实现，提供高质量的语音合成能力。通过整合 Edge-TTS 与本地 Ollama 服务器，可以实现离线模式下的高质量文本转语音音频响应。

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

### 2. Edge-TTS 安装与配置

Edge-TTS 需要 Python 环境：

1. 安装 Python 3.7+
2. 安装 Edge-TTS 模块：

```bash
pip install edge-tts
```

3. 测试 Edge-TTS：

```bash
edge-tts --text "Hello, this is a test" --write-media test.mp3
```

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

### 1. 文本转语音基础

使用 Edge-TTS 进行文本转语音：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EdgeTTSController {

    private final EdgeTTSService edgeTTSService;

    @PostMapping("/tts/edge")
    public byte[] generateSpeech(@RequestBody String text) {
        return edgeTTSService.textToSpeech(text);
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
public class ChatEdgeTTSController {

    private final ChatModel chatModel;
    private final EdgeTTSService edgeTTSService;

    @GetMapping("/chat/edge-tts")
    public byte[] chatAndSpeak(@RequestParam String message) {
        // 1. 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 2. 使用 Edge-TTS 转换为语音
        return edgeTTSService.textToSpeech(response);
    }
}
```

### 3. 多语言支持

Edge-TTS 支持多种语言和声音：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiLanguageTTSController {

    private final EdgeTTSService edgeTTSService;

    @PostMapping("/tts/multilang")
    public byte[] generateSpeechWithVoice(
            @RequestParam String text,
            @RequestParam(defaultValue = "zh-CN-XiaoxiaoNeural") String voice) {
        return edgeTTSService.textToSpeech(text, voice);
    }
}
```

## 推荐声音

### 中文声音
- `zh-CN-XiaoxiaoNeural` - 晓晓（女声）
- `zh-CN-YunxiNeural` - 云希（男声）
- `zh-CN-YunyangNeural` - 云扬（男声）

### 英文声音
- `en-US-JennyNeural` - Jenny（女声）
- `en-US-GuyNeural` - Guy（男声）

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 Edge-TTS 已安装
3. 启动应用：

```bash
cd spring-ai-ollama-audio-edgetts
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /tts/edge` - 文本转语音
   - `GET /chat/edge-tts?message=你好` - 对话转语音
   - `POST /tts/multilang` - 多语言语音合成

## 最佳实践

1. **声音选择**：根据应用场景选择合适的声音
2. **音频格式**：使用 MP3 格式平衡质量和文件大小
3. **错误处理**：处理 Edge-TTS 调用失败的情况
4. **性能优化**：缓存常用文本的语音结果

## 相关资源

- [Edge-TTS 官方项目](https://github.com/rany2/edge-tts)
- [Edge-TTS 声音列表](https://speech.microsoft.com/portal/voicegallery)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-audio-edgetts)

## 扩展阅读

- [Spring AI Ollama 与 ChatTTS 集成](43、Spring AI 入门实践：Spring AI Ollama 与 ChatTTS 集成.md)
- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)