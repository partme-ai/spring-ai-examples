# Spring AI 入门实践：Spring AI Ollama 与 Whisper 语音识别集成

## 概述

Whisper 是 OpenAI 开源的自动语音识别（ASR）模型，支持多语言语音转文字。通过整合 Whisper 与本地 Ollama 服务器，可以实现语音识别与对话的完整语音交互应用。

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

### 2. Whisper 安装与配置

安装和运行 Whisper：

1. 安装 Python 3.7+
2. 安装 CUDA（GPU 加速）：

```bash
pip install cuda
```

3. 安装 Whisper：

```bash
pip install openai-whisper
```

4. 测试 Whisper：

```bash
whisper audio.mp3 --language Chinese --model large
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

### 1. 语音识别基础

使用 Whisper 进行语音识别：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WhisperController {

    private final WhisperService whisperService;

    @PostMapping("/stt/whisper")
    public String transcribeAudio(@RequestParam("file") MultipartFile file) {
        return whisperService.transcribe(file);
    }
}
```

### 2. 语音对话

语音输入 -> 文字 -> Ollama 对话 -> 文字输出：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VoiceChatController {

    private final ChatModel chatModel;
    private final WhisperService whisperService;

    @PostMapping("/voice/chat")
    public String voiceChat(@RequestParam("file") MultipartFile file) {
        // 1. 使用 Whisper 识别语音
        String text = whisperService.transcribe(file);
        
        // 2. 使用 Ollama 生成回复
        return chatModel.call(text);
    }
}
```

### 3. 多语言识别

支持多语言语音识别：

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MultiLanguageSTTController {

    private final WhisperService whisperService;

    @PostMapping("/stt/multilang")
    public String transcribeWithLanguage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "auto") String language) {
        return whisperService.transcribe(file, language);
    }
}
```

## Whisper 模型

### 模型选择

Whisper 提供多种模型大小：

| 模型 | 参数量 | 相对速度 | 适用场景 |
|------|--------|----------|----------|
| tiny | 39M | 最快 | 快速识别，精度较低 |
| base | 74M | 快 | 一般场景 |
| small | 244M | 中等 | 平衡性能和精度 |
| medium | 769M | 慢 | 高精度需求 |
| large | 1550M | 最慢 | 最高精度 |

### 使用示例

```bash
# 使用不同模型
whisper audio.mp3 --model tiny
whisper audio.mp3 --model base
whisper audio.mp3 --model small
whisper audio.mp3 --model medium
whisper audio.mp3 --model large
```

## 完整示例

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 Whisper 已安装
3. 启动应用：

```bash
cd spring-ai-ollama-audio-whisper
mvn spring-boot:run
```

4. 访问 API 端点：
   - `POST /stt/whisper` - 语音识别
   - `POST /voice/chat` - 语音对话
   - `POST /stt/multilang` - 多语言识别

## 最佳实践

1. **模型选择**：根据精度和速度需求选择合适的模型
2. **GPU 加速**：使用 GPU 加速语音识别
3. **音频格式**：使用 WAV 或 MP3 格式
4. **错误处理**：处理音频质量差的情况
5. **性能优化**：缓存识别结果

## 相关资源

- [Whisper 官方项目](https://github.com/openai/whisper)
- [Whisper Hugging Face](https://huggingface.co/openai/whisper-large-v3)
- [Ollama 官方文档](https://ollama.com/docs)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-whisper)

## 扩展阅读

- [Spring AI Ollama 与 UnifiedTTS 集成](47、Spring AI 入门实践：Spring AI Ollama 与 UnifiedTTS 集成.md)
- [Spring AI 语音与音频处理集成](22、Spring AI 入门实践：Spring AI 语音与音频处理集成.md)
- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)