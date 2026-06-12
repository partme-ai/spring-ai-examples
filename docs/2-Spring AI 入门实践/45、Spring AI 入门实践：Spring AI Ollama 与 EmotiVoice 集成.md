# Spring AI 入门实践：Spring AI Ollama 与 EmotiVoice 集成

> 基于 Spring AI 框架实现 Ollama 与 EmotiVoice 的集成，使用本地大语言模型和网易有道开源的情感文本转语音引擎，构建完整的情感语音交互应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Ollama 与 EmotiVoice 的示例，展示了如何在 Java/Spring Boot 应用中使用本地大语言模型 Ollama 和网易有道开源的多语言情感文本转语音引擎 EmotiVoice，构建完整的情感语音交互应用，实现文本生成和情感语音转换的端到端解决方案。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama | - | 本地大语言模型 |
| EmotiVoice | - | 网易有道情感 TTS |
| Python | 3.7+ | EmotiVoice 运行环境 |

### 1.3 核心功能

- ✅ 本地模型：使用 Ollama 运行本地大语言模型
- ✅ 情感语音合成：使用 EmotiVoice 进行情感语音合成
- ✅ 多语言支持：支持多种语言
- ✅ 对话转情感语音：将 Ollama 对话直接转换为情感语音
- ✅ 离线运行：完全离线运行，无需网络
- ✅ 情感选择：支持多种情感表达
- ✅ 音频缓存：支持音频缓存优化性能

---

## 二、EmotiVoice 简介

### 2.1 EmotiVoice 介绍

EmotiVoice 是网易有道开源的多语言文本转语音引擎，支持情感语音合成，可以生成带有不同情感表达的语音。它提供了丰富的情感选项和声音选择，是一个优秀的情感语音合成解决方案。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **情感语音合成** | 支持多种情感表达 |
| **多语言支持** | 支持多种语言 |
| **开源免费** | 完全开源，免费使用 |
| **丰富情感** | 支持多种情感选择 |
| **高质量语音** | 优秀的语音合成质量 |
| **易于部署** | 易于部署和集成 |
| **离线运行** | 完全离线运行，无需网络 |

---

## 三、项目结构

```
spring-ai-ollama-audio-emoti/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAudioEmotiApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── EmotiVoiceController.java
│   │   │                   │   └── ChatEmotiController.java
│   │   │                   ├── service/
│   │   │                   │   └── EmotiVoiceService.java
│   │   │                   └── dto/
│   │   │                       └── EmotiRequest.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiOllamaAudioEmotiApplication.java` - Spring Boot 应用入口
- `EmotiVoiceController.java` - 情感语音合成 API 控制器
- `ChatEmotiController.java` - 对话情感语音 API 控制器
- `EmotiVoiceService.java` - EmotiVoice 服务
- `EmotiRequest.java` - 情感语音请求 DTO
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-audio-emoti
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# EmotiVoice 配置
emotivoice.api.url=http://localhost:9880
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

### 5.1 EmotiVoice 服务实现

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmotiVoiceService {

    @Value("${emotivoice.api.url}")
    private String emotiVoiceApiUrl;

    private final RestTemplate restTemplate;

    public EmotiVoiceService() {
        this.restTemplate = new RestTemplate();
    }

    public byte[] textToSpeech(String text, String emotion) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("emotion", emotion);
        requestBody.put("speed", 1.0);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                emotiVoiceApiUrl,
                HttpMethod.POST,
                request,
                byte[].class
        );

        return response.getBody();
    }
}
```

### 5.2 情感语音请求 DTO

```java
public class EmotiRequest {
    private String text;
    private String emotion;

    public EmotiRequest() {
    }

    public EmotiRequest(String text, String emotion) {
        this.text = text;
        this.emotion = emotion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
```

### 5.3 情感语音合成控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmotiVoiceController {

    private final EmotiVoiceService emotiVoiceService;

    @Autowired
    public EmotiVoiceController(EmotiVoiceService emotiVoiceService) {
        this.emotiVoiceService = emotiVoiceService;
    }

    @PostMapping("/tts/emoti")
    public ResponseEntity<byte[]> generateEmotionalSpeech(@RequestBody EmotiRequest request) {
        byte[] audio = emotiVoiceService.textToSpeech(request.getText(), request.getEmotion());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "speech.wav");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

### 5.4 对话情感语音控制器

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
public class ChatEmotiController {

    private final ChatModel chatModel;
    private final EmotiVoiceService emotiVoiceService;

    @Autowired
    public ChatEmotiController(ChatModel chatModel, EmotiVoiceService emotiVoiceService) {
        this.chatModel = chatModel;
        this.emotiVoiceService = emotiVoiceService;
    }

    @GetMapping("/chat/emoti-tts")
    public ResponseEntity<byte[]> chatAndSpeakWithEmotion(@RequestParam String message) {
        // 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 使用 EmotiVoice 转换为情感语音
        byte[] audio = emotiVoiceService.textToSpeech(response, "neutral");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "response.wav");

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
| `POST` | `/tts/emoti` | 情感语音合成 |
| `GET` | `/chat/emoti-tts` | 对话情感语音 |

### 6.2 请求/响应示例

#### 情感语音合成

**请求：**
```
POST /tts/emoti
Content-Type: application/json

{
  "text": "你好，很高兴认识你！",
  "emotion": "happy"
}
```

**响应：**
```
Content-Type: audio/wav
Content-Disposition: attachment; filename="speech.wav"

[音频二进制数据]
```

#### 对话情感语音

**请求：**
```
GET /chat/emoti-tts?message=你好
```

**响应：**
```
Content-Type: audio/wav
Content-Disposition: attachment; filename="response.wav"

[音频二进制数据]
```

---

## 七、部署方式

### 方式一：本地部署

#### 1. 安装 EmotiVoice

访问 EmotiVoice 项目：https://github.com/netease-youdao/EmotiVoice

按照项目说明安装和配置，然后启动服务。

#### 2. 测试 EmotiVoice

确保 EmotiVoice 服务正在运行并可以正常访问。

#### 3. 安装 Ollama

访问 Ollama 官网下载并安装：https://ollama.com/

#### 4. 拉取模型

```bash
ollama pull qwen3.5
ollama pull llama3
```

---

## 八、使用示例

### 8.1 cURL 调用

#### 情感语音合成

```bash
curl -X POST "http://localhost:8080/tts/emoti" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "你好，很高兴认识你！",
    "emotion": "happy"
  }' \
  --output speech.wav
```

#### 对话情感语音

```bash
curl "http://localhost:8080/chat/emoti-tts?message=你好" \
  --output response.wav
```

### 8.2 推荐情感

| 情感 | 说明 |
|------|------|
| `neutral` | 中性（默认） |
| `happy` | 开心 |
| `sad` | 悲伤 |
| `angry` | 生气 |
| `surprise` | 惊讶 |

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-audio-emoti
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: EmotiVoice 支持哪些情感？

EmotiVoice 支持多种情感，包括中性、开心、悲伤、生气、惊讶等，可以根据文本内容选择合适的情感。

### Q2: 如何选择合适的情感？

根据文本内容和应用场景选择合适的情感，例如问候语可以用开心，道歉可以用悲伤等。

### Q3: 如何提高响应速度？

可以使用音频缓存来提高响应速度，缓存常见文本的语音结果。

---

## 十一、许可证

- **EmotiVoice**：Apache 2.0
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

EmotiVoice 采用 Apache 2.0 开源许可证。

---

## 参考资源

- **EmotiVoice 官方项目**：https://github.com/netease-youdao/EmotiVoice
- **Ollama 官方文档**：https://ollama.com/docs
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-emoti

---

## 致谢

- **感谢网易有道 EmotiVoice 团队** 提供优秀的情感文本转语音引擎
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
