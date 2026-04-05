# Spring AI 入门实践：Spring AI Ollama 与 UnifiedTTS 集成

## 概述

UnifiedTTS 是一个统一的文本转语音框架，整合了多种 TTS 引擎。通过整合 UnifiedTTS 与本地 Ollama 服务器，可以灵活选择不同的 TTS 引擎，实现高质量的文本转语音音频响应。

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

### 2. UnifiedTTS 安装与配置

安装和运行 UnifiedTTS：

1. 访问 [UnifiedTTS 项目](https://unifiedtts.com/)
2. 按照项目说明安装和配置
3. 启动 UnifiedTTS 服务

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

### 1. 统一语音合成接口

使用 UnifiedTTS 进行语音合成：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnifiedTTSController {

    private final UnifiedTTSService unifiedTTSService;

    @PostMapping("/tts/unified")
    public byte[] generateSpeech(@RequestBody UnifiedTTSRequest request) {
        return unifiedTTSService.textToSpeech(request.getText(), request.getEngine());
    }
}
```

### 2. 多引擎切换

灵活切换不同的 TTS 引擎：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiEngineTTSController {

    private final UnifiedTTSService unifiedTTSService;

    @PostMapping("/tts/multi-engine")
    public byte[] generateSpeechWithEngine(
            @RequestParam String text,
            @RequestParam(defaultValue = "chattts") String engine) {
        return unifiedTTSService.textToSpeech(text, engine);
    }
}
```

### 3. 对话语音合成

将 Ollama 对话转换为语音：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatUnifiedTTSController {

    private final ChatModel chatModel;
    private final UnifiedTTSService unifiedTTSService;

    @GetMapping("/chat/unified-tts")
    public byte[] chatAndSpeak(@RequestParam String message) {
        // 1. 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 2. 使用 UnifiedTTS 转换为语音
        return unifiedTTSService.textToSpeech(response, "chattts");
    }
}
```

## 支持的引擎

UnifiedTTS 支持多种 TTS 引擎：
- ChatTTS
- Edge-TTS
- EmotiVoice
- MARS5-TTS
- 其他自定义引擎

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 UnifiedTTS 服务正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-audio-unifiedtts
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /tts/unified` - 统一语音合成
   - `POST /tts/multi-engine` - 多引擎语音合成
   - `GET /chat/unified-tts?message=你好` - 对话语音合成

## 最佳实践

1. **引擎选择**：根据应用需求选择合适的 TTS 引擎
2. **性能优化**：缓存常用文本的语音结果
3. **错误处理**：处理引擎不可用的情况
4. **质量监控**：监控不同引擎的语音质量

## 相关资源

- [UnifiedTTS 官方网站](https://unifiedtts.com/)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-unifiedtts)

## 扩展阅读

- [Spring AI Ollama 与 MARS5-TTS 集成](46、Spring AI 入门实践：Spring AI Ollama 与 MARS5-TTS 集成.md)
- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)