# 22、Spring AI 入门实践：Spring AI 语音与音频处理集成

## 一、项目概述

语音与音频处理是 AI 应用的重要组成部分，包括语音识别（Speech-to-Text）和语音合成（Text-to-Speech）。Spring AI 提供了对多种音频处理方案的集成支持，可以与 Ollama 文本链路组合使用，构建完整的语音交互应用。

### 核心功能

- **语音识别**：将语音转换为文本（Whisper 等）
- **语音合成**：将文本转换为语音（Edge-TTS、ChatTTS 等）
- **Ollama 集成**：与 Ollama 文本链路组合
- **多种方案**：支持多种 TTS 和 STT 方案

### 适用场景

- 语音助手和聊天机器人
- 语音转录和字幕生成
- 有声读物和内容创作
- 无障碍访问功能
- 多模态交互应用

## 二、语音与音频处理简介

Spring AI 支持多种语音处理方案，包括语音识别和语音合成。

### 能力划分

| 能力类型 | 说明 | 推荐方案 |
|---------|------|---------|
| Speech-to-Text | 语音转文本 | Whisper |
| Text-to-Speech | 文本转语音 | Edge-TTS、ChatTTS 等 |

### 子模块一览

| 模块 | 说明 |
|------|------|
| spring-ai-ollama-audio-whisper | 语音识别（Whisper） |
| spring-ai-ollama-audio-edgetts | Edge-TTS 语音合成 |
| spring-ai-ollama-audio-chattts | ChatTTS 语音合成 |
| spring-ai-ollama-audio-emoti | Emoti 语音合成 |
| spring-ai-ollama-audio-mars5tts | MARS5 语音合成 |
| spring-ai-ollama-audio-unifiedtts | 统一 TTS 抽象 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Python 3.8+（部分 TTS 方案需要）
- Ollama（本地模型部署）

### 3.2 依赖环境

根据选择的音频方案，可能需要：
- **Whisper**：Ollama 或本地 Whisper 模型
- **Edge-TTS**：Python + Edge-TTS CLI
- **ChatTTS**：Python + ChatTTS 库
- **其他 TTS**：参考各模块 README.md

## 四、项目结构

### 4.1 音频处理项目结构

以 Edge-TTS 为例：

```
spring-ai-ollama-audio-edgetts/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── audio/
│   │   │                   ├── SpringAiAudioApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── AudioController.java
│   │   │                   └── service/
│   │   │                       └── AudioService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 五、核心配置

### 5.1 Maven 依赖

以 Edge-TTS 为例：

```xml
<dependencies>
    <!-- For Spring AI Common -->
    <dependency>
        <groupId>com.github.partmeai</groupId>
        <artifactId>spring-ai-common</artifactId>
        <version>${revision}</version>
    </dependency>
    <!-- For Chat Completion & Embedding -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-autoconfigure-retry</artifactId>
    </dependency>
    <!-- For Chat Memory -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
    </dependency>
    <!-- For Vector Store  -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure-cosmos-db</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-cassandra</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-couchbase</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-gemfire</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mongodb-atlas</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-neo4j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-opensearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-oracle</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-typesense</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-weaviate</artifactId>
    </dependency>
    <!-- For Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <!-- For Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
    <!-- For Embed Undertow -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- For Testcontainers : https://testcontainers.com/ -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>ollama</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>typesense</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 5.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-audio-edgetts
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: llama3.2
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 音频服务

以 Edge-TTS 为例：

```java
package com.github.partmeai.audio.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AudioService {
    
    private final ChatClient chatClient;
    
    public AudioService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Map<String, Object> textToSpeech(String text) {
        String response = chatClient.prompt()
                .user("请将以下文本转换为语音：" + text)
                .call()
                .content();
        
        return Map.of(
                "text", text,
                "status", "completed",
                "note", "实际 TTS 实现请参考具体模块"
        );
    }
    
    public Map<String, Object> speechToText(String audioFile) {
        String response = chatClient.prompt()
                .user("请识别以下语音内容（示例）")
                .call()
                .content();
        
        return Map.of(
                "audioFile", audioFile,
                "status", "completed",
                "note", "实际 STT 实现请参考具体模块"
        );
    }
    
    public Map<String, Object> audioChat(String text) {
        String response = chatClient.prompt()
                .user(text)
                .call()
                .content();
        
        return Map.of(
                "input", text,
                "response", response
        );
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.audio.controller;

import com.github.partmeai.audio.service.AudioService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AudioController {
    
    private final AudioService audioService;
    
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }
    
    @PostMapping("/tts")
    public Map<String, Object> textToSpeech(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return audioService.textToSpeech(text);
    }
    
    @PostMapping("/stt")
    public Map<String, Object> speechToText(@RequestBody Map<String, String> request) {
        String audioFile = request.get("audioFile");
        return audioService.speechToText(audioFile);
    }
    
    @PostMapping("/audio-chat")
    public Map<String, Object> audioChat(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        return audioService.audioChat(text);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiAudioApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiAudioApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 文本转语音 | POST | `/api/tts` | 将文本转换为语音 |
| 语音转文本 | POST | `/api/stt` | 将语音转换为文本 |
| 音频对话 | POST | `/api/audio-chat` | 音频交互对话 |

### 7.2 接口使用示例

#### 文本转语音

```bash
curl -X POST http://localhost:8080/api/tts \
  -H "Content-Type: application/json" \
  -d '{"text": "你好，这是一个语音合成测试"}'
```

## 八、部署方式

### 8.1 本地运行

以 Edge-TTS 为例：

```bash
cd spring-ai-ollama-audio-edgetts
mvn spring-boot:run
```

**注意**：各模块可能依赖本机 Python、Edge-TTS CLI、Ollama 等，详见子目录 `README.md` 中的环境与端口说明。

### 8.2 打包部署

```bash
cd spring-ai-ollama-audio-edgetts
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-audio-edgetts-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **方案选择**：
   - 语音识别：推荐使用 Whisper（Ollama 或本地）
   - 语音合成：根据需求选择 Edge-TTS、ChatTTS 等
   - 完整链路：结合 Ollama 文本模型

2. **环境配置**：
   - 仔细阅读各模块的 README.md
   - 确保 Python 和相关依赖正确安装
   - 配置好 Ollama 和所需模型

3. **应用场景**：
   - 语音助手和聊天机器人
   - 语音转录服务
   - 有声内容生成

### 9.2 官方文档

- Audio Transcriptions：https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html
- Audio Speech：https://docs.spring.io/spring-ai/reference/api/audio/speech.html

## 十、运行项目

### 10.1 启动应用

以 Edge-TTS 为例：

```bash
cd spring-ai-ollama-audio-edgetts
mvn spring-boot:run
```

### 10.2 验证

浏览器打开静态页或调用接口，确认模型名与本地 `ollama list` 一致。

## 十一、常见问题

### 11.1 依赖环境问题

**Q: Python 或 TTS 工具无法正常工作怎么办？**

- 确认 Python 版本和依赖正确安装
- 检查各模块 README.md 中的环境要求
- 确保相关 CLI 工具在 PATH 中

### 11.2 Ollama 集成问题

**Q: Ollama 模型无法加载怎么办？**

- 确认 Ollama 服务正常运行（http://localhost:11434）
- 使用 `ollama list` 检查模型是否已下载
- 检查网络连接和模型下载

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Audio：https://docs.spring.io/spring-ai/reference/api/audio/
- Ollama：https://ollama.com/
- 示例模块：spring-ai-ollama-audio-*（各子模块）
- 各模块 README.md（详细环境说明）

## 十四、致谢

感谢 Spring AI 团队和各音频处理方案提供者，让构建语音交互应用变得如此简单。
