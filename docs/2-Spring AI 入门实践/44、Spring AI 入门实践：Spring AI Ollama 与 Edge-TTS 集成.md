# Spring AI 入门实践：Spring AI Ollama 与 Edge-TTS 集成

> 基于 Spring AI 框架实现 Ollama 与 Edge-TTS 的集成，使用本地大语言模型和微软 Edge 浏览器文本转语音引擎的开源实现，构建完整的语音交互应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Ollama 与 Edge-TTS 的示例，展示了如何在 Java/Spring Boot 应用中使用本地大语言模型 Ollama 和微软 Edge 浏览器的文本转语音引擎的开源实现 Edge-TTS，构建完整的语音交互应用，实现文本生成和高质量语音转换的端到端解决方案。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama | - | 本地大语言模型 |
| Edge-TTS | - | 微软 Edge TTS 开源实现 |
| Python | 3.7+ | Edge-TTS 运行环境 |

### 1.3 核心功能

- ✅ 本地模型：使用 Ollama 运行本地大语言模型
- ✅ 文本转语音：使用 Edge-TTS 进行高质量文本转语音
- ✅ 多语言支持：支持多种语言和声音
- ✅ 对话转语音：将 Ollama 对话直接转换为语音
- ✅ 离线运行：完全离线运行，无需网络
- ✅ 多声音选择：支持多种声音和音调
- ✅ 音频缓存：支持音频缓存优化性能

---

## 二、Edge-TTS 简介

### 2.1 Edge-TTS 介绍

Edge-TTS 是微软 Edge 浏览器的文本转语音引擎的开源实现，提供高质量的语音合成能力。它基于 Microsoft Azure Cognitive Services 的语音技术，支持多种语言和声音，是一个优秀的文本转语音解决方案。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **高质量语音** | 基于微软 Azure 技术，高质量语音合成 |
| **多语言支持** | 支持多种语言和声音 |
| **开源免费** | 完全开源，免费使用 |
| **Python 实现** | 基于 Python，易于集成 |
| **多声音选择** | 支持多种声音和音调 |
| **MP3 格式** | 支持 MP3 音频格式 |
| **离线运行** | 完全离线运行，无需网络 |

---

## 三、项目结构

```
spring-ai-ollama-audio-edgetts/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAudioEdgettsApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── EdgeTTSController.java
│   │   │                   │   ├── ChatEdgeTTSController.java
│   │   │                   │   └── MultiLanguageTTSController.java
│   │   │                   └── service/
│   │   │                       └── EdgeTTSService.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiOllamaAudioEdgettsApplication.java` - Spring Boot 应用入口
- `EdgeTTSController.java` - 文本转语音 API 控制器
- `ChatEdgeTTSController.java` - 对话转语音 API 控制器
- `MultiLanguageTTSController.java` - 多语言语音合成 API 控制器
- `EdgeTTSService.java` - Edge-TTS 服务
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-audio-edgetts
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5
```

### 4.2 依赖配置

在 `pom.xml` 中添加：

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
        <artifactId>spring-ai-starter-model-ollama</artifactId>
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

---

## 五、代码实现详解

### 5.1 Edge-TTS 服务实现

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EdgeTTSService {

    public byte[] textToSpeech(String text) {
        return textToSpeech(text, "zh-CN-XiaoxiaoNeural");
    }

    public byte[] textToSpeech(String text, String voice) {
        try {
            // 创建临时文件
            String tempFile = "temp_output.mp3";
            
            // 构建 Edge-TTS 命令
            String[] command = {
                "edge-tts",
                "--text", text,
                "--voice", voice,
                "--write-media", tempFile
            };
            
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            
            // 等待命令完成
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // 读取音频文件
                byte[] audio = Files.readAllBytes(Paths.get(tempFile));
                
                // 删除临时文件
                Files.deleteIfExists(Paths.get(tempFile));
                
                return audio;
            } else {
                throw new RuntimeException("Edge-TTS command failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate speech with Edge-TTS", e);
        }
    }
}
```

### 5.2 文本转语音控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EdgeTTSController {

    private final EdgeTTSService edgeTTSService;

    @Autowired
    public EdgeTTSController(EdgeTTSService edgeTTSService) {
        this.edgeTTSService = edgeTTSService;
    }

    @PostMapping("/tts/edge")
    public ResponseEntity<byte[]> generateSpeech(@RequestBody String text) {
        byte[] audio = edgeTTSService.textToSpeech(text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentDispositionFormData("attachment", "speech.mp3");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

### 5.3 对话转语音控制器

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatEdgeTTSController {

    private final ChatModel chatModel;
    private final EdgeTTSService edgeTTSService;

    @Autowired
    public ChatEdgeTTSController(ChatModel chatModel, EdgeTTSService edgeTTSService) {
        this.chatModel = chatModel;
        this.edgeTTSService = edgeTTSService;
    }

    @GetMapping("/chat/edge-tts")
    public ResponseEntity<byte[]> chatAndSpeak(@RequestParam String message) {
        // 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 使用 Edge-TTS 转换为语音
        byte[] audio = edgeTTSService.textToSpeech(response);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentDispositionFormData("attachment", "response.mp3");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

### 5.4 多语言语音合成控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiLanguageTTSController {

    private final EdgeTTSService edgeTTSService;

    @Autowired
    public MultiLanguageTTSController(EdgeTTSService edgeTTSService) {
        this.edgeTTSService = edgeTTSService;
    }

    @PostMapping("/tts/multilang")
    public ResponseEntity<byte[]> generateSpeechWithVoice(
            @RequestParam String text,
            @RequestParam(defaultValue = "zh-CN-XiaoxiaoNeural") String voice) {
        byte[] audio = edgeTTSService.textToSpeech(text, voice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentDispositionFormData("attachment", "speech.mp3");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

---

## 六、API 接口说明

### 6.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/tts/edge` | 文本转语音 |
| `GET` | `/chat/edge-tts` | 对话转语音 |
| `POST` | `/tts/multilang` | 多语言语音合成 |

### 6.2 请求/响应示例

#### 文本转语音

**请求：**
```
POST /tts/edge
Content-Type: text/plain

你好，欢迎使用 Edge-TTS！
```

**响应：**
```
Content-Type: audio/mpeg
Content-Disposition: attachment; filename="speech.mp3"

[音频二进制数据]
```

#### 对话转语音

**请求：**
```
GET /chat/edge-tts?message=你好
```

**响应：**
```
Content-Type: audio/mpeg
Content-Disposition: attachment; filename="response.mp3"

[音频二进制数据]
```

#### 多语言语音合成

**请求：**
```
POST /tts/multilang?text=Hello&voice=en-US-JennyNeural
```

**响应：**
```
Content-Type: audio/mpeg
Content-Disposition: attachment; filename="speech.mp3"

[音频二进制数据]
```

---

## 七、部署方式

### 方式一：本地部署

#### 1. 安装 Python

确保安装 Python 3.7+ 版本。

#### 2. 安装 Edge-TTS

```bash
pip install edge-tts
```

#### 3. 测试 Edge-TTS

```bash
edge-tts --text "Hello, this is a test" --write-media test.mp3
```

#### 4. 安装 Ollama

访问 Ollama 官网下载并安装：https://ollama.com/

#### 5. 拉取模型

```bash
ollama pull qwen3.5
ollama pull llama3
```

---

## 八、使用示例

### 8.1 cURL 调用

#### 文本转语音

```bash
curl -X POST "http://localhost:8080/tts/edge" \
  -H "Content-Type: text/plain" \
  -d "你好，欢迎使用 Edge-TTS！" \
  --output speech.mp3
```

#### 对话转语音

```bash
curl "http://localhost:8080/chat/edge-tts?message=你好" \
  --output response.mp3
```

#### 多语言语音合成

```bash
curl -X POST "http://localhost:8080/tts/multilang?text=Hello&voice=en-US-JennyNeural" \
  --output speech.mp3
```

### 8.2 推荐声音

#### 中文声音

| 声音 | 说明 |
|------|------|
| `zh-CN-XiaoxiaoNeural` | 晓晓（女声） |
| `zh-CN-YunxiNeural` | 云希（男声） |
| `zh-CN-YunyangNeural` | 云扬（男声） |

#### 英文声音

| 声音 | 说明 |
|------|------|
| `en-US-JennyNeural` | Jenny（女声） |
| `en-US-GuyNeural` | Guy（男声） |

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-audio-edgetts
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: Edge-TTS 需要什么环境？

Edge-TTS 需要 Python 3.7+ 环境，需要先安装 Python，然后通过 pip 安装 Edge-TTS。

### Q2: 如何选择合适的声音？

Edge-TTS 支持多种语言和声音，可以根据应用场景选择合适的声音。中文推荐使用 `zh-CN-XiaoxiaoNeural`，英文推荐使用 `en-US-JennyNeural`。

### Q3: 如何提高响应速度？

可以使用音频缓存来提高响应速度，缓存常见文本的语音结果。

---

## 十一、许可证

- **Edge-TTS**：GPL 3.0
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

Edge-TTS 采用 GPL 3.0 开源许可证。

---

## 参考资源

- **Edge-TTS 官方项目**：https://github.com/rany2/edge-tts
- **Edge-TTS 声音列表**：https://speech.microsoft.com/portal/voicegallery
- **Ollama 官方文档**：https://ollama.com/docs
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-edgetts

---

## 致谢

- **感谢 Edge-TTS 社区** 提供优秀的文本转语音实现
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
