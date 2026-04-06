# Spring AI 入门实践：Spring AI Ollama 与 UnifiedTTS 集成

> 基于 Spring AI 框架实现 Ollama 与 UnifiedTTS 的集成，使用本地大语言模型和统一的文本转语音框架，构建完整的语音交互应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Ollama 与 UnifiedTTS 的示例，展示了如何在 Java/Spring Boot 应用中使用本地大语言模型 Ollama 和统一的文本转语音框架 UnifiedTTS，灵活选择不同的 TTS 引擎，构建完整的语音交互应用，实现文本生成和语音转换的端到端解决方案。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama | - | 本地大语言模型 |
| UnifiedTTS | - | 统一 TTS 框架 |
| Python | 3.7+ | UnifiedTTS 运行环境 |

### 1.3 核心功能

- ✅ 本地模型：使用 Ollama 运行本地大语言模型
- ✅ 统一语音合成：使用 UnifiedTTS 进行统一语音合成
- ✅ 多引擎支持：支持多种 TTS 引擎（ChatTTS、Edge-TTS、EmotiVoice、MARS5-TTS 等）
- ✅ 灵活切换：灵活切换不同的 TTS 引擎
- ✅ 对话转语音：将 Ollama 对话直接转换为语音
- ✅ 离线运行：完全离线运行，无需网络
- ✅ 音频缓存：支持音频缓存优化性能

---

## 二、UnifiedTTS 简介

### 2.1 UnifiedTTS 介绍

UnifiedTTS 是一个统一的文本转语音框架，整合了多种 TTS 引擎，提供统一的接口，允许用户灵活选择不同的 TTS 引擎。它支持 ChatTTS、Edge-TTS、EmotiVoice、MARS5-TTS 等多种流行的 TTS 引擎，是一个灵活的文本转语音解决方案。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **统一接口** | 统一的接口，多种 TTS 引擎 |
| **多引擎支持** | 支持多种 TTS 引擎 |
| **灵活切换** | 灵活切换不同的 TTS 引擎 |
| **易于集成** | 易于部署和集成 |
| **开源免费** | 完全开源，免费使用 |
| **统一配置** | 统一的配置管理 |
| **离线运行** | 完全离线运行，无需网络 |

---

## 三、项目结构

```
spring-ai-ollama-audio-unifiedtts/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAudioUnifiedttsApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── UnifiedTTSController.java
│   │   │                   │   ├── MultiEngineTTSController.java
│   │   │                   │   └── ChatUnifiedTTSController.java
│   │   │                   ├── service/
│   │   │                   │   └── UnifiedTTSService.java
│   │   │                   └── dto/
│   │   │                       └── UnifiedTTSRequest.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiOllamaAudioUnifiedttsApplication.java` - Spring Boot 应用入口
- `UnifiedTTSController.java` - 统一语音合成 API 控制器
- `MultiEngineTTSController.java` - 多引擎语音合成 API 控制器
- `ChatUnifiedTTSController.java` - 对话语音合成 API 控制器
- `UnifiedTTSService.java` - UnifiedTTS 服务
- `UnifiedTTSRequest.java` - 统一语音合成请求 DTO
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-audio-unifiedtts
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# UnifiedTTS 配置
unifiedtts.api.url=http://localhost:8765
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

### 5.1 UnifiedTTS 服务实现

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UnifiedTTSService {

    @Value("${unifiedtts.api.url}")
    private String unifiedttsApiUrl;

    private final RestTemplate restTemplate;

    public UnifiedTTSService() {
        this.restTemplate = new RestTemplate();
    }

    public byte[] textToSpeech(String text, String engine) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("engine", engine);
        requestBody.put("speed", 1.0);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                unifiedttsApiUrl,
                HttpMethod.POST,
                request,
                byte[].class
        );

        return response.getBody();
    }
}
```

### 5.2 统一语音合成请求 DTO

```java
public class UnifiedTTSRequest {
    private String text;
    private String engine;

    public UnifiedTTSRequest() {
    }

    public UnifiedTTSRequest(String text, String engine) {
        this.text = text;
        this.engine = engine;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }
}
```

### 5.3 统一语音合成控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnifiedTTSController {

    private final UnifiedTTSService unifiedTTSService;

    @Autowired
    public UnifiedTTSController(UnifiedTTSService unifiedTTSService) {
        this.unifiedTTSService = unifiedTTSService;
    }

    @PostMapping("/tts/unified")
    public ResponseEntity<byte[]> generateSpeech(@RequestBody UnifiedTTSRequest request) {
        byte[] audio = unifiedTTSService.textToSpeech(request.getText(), request.getEngine());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "speech.wav");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

### 5.4 多引擎语音合成控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiEngineTTSController {

    private final UnifiedTTSService unifiedTTSService;

    @Autowired
    public MultiEngineTTSController(UnifiedTTSService unifiedTTSService) {
        this.unifiedTTSService = unifiedTTSService;
    }

    @PostMapping("/tts/multi-engine")
    public ResponseEntity<byte[]> generateSpeechWithEngine(
            @RequestParam String text,
            @RequestParam(defaultValue = "chattts") String engine) {
        byte[] audio = unifiedTTSService.textToSpeech(text, engine);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "speech.wav");

        return ResponseEntity.ok()
                .headers(headers)
                .body(audio);
    }
}
```

### 5.5 对话语音合成控制器

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
public class ChatUnifiedTTSController {

    private final ChatModel chatModel;
    private final UnifiedTTSService unifiedTTSService;

    @Autowired
    public ChatUnifiedTTSController(ChatModel chatModel, UnifiedTTSService unifiedTTSService) {
        this.chatModel = chatModel;
        this.unifiedTTSService = unifiedTTSService;
    }

    @GetMapping("/chat/unified-tts")
    public ResponseEntity<byte[]> chatAndSpeak(@RequestParam String message) {
        // 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 使用 UnifiedTTS 转换为语音
        byte[] audio = unifiedTTSService.textToSpeech(response, "chattts");

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
| `POST` | `/tts/unified` | 统一语音合成 |
| `POST` | `/tts/multi-engine` | 多引擎语音合成 |
| `GET` | `/chat/unified-tts` | 对话语音合成 |

### 6.2 请求/响应示例

#### 统一语音合成

**请求：**
```
POST /tts/unified
Content-Type: application/json

{
  "text": "你好，欢迎使用 UnifiedTTS！",
  "engine": "chattts"
}
```

**响应：**
```
Content-Type: audio/wav
Content-Disposition: attachment; filename="speech.wav"

[音频二进制数据]
```

#### 多引擎语音合成

**请求：**
```
POST /tts/multi-engine?text=Hello&engine=edge-tts
```

**响应：**
```
Content-Type: audio/wav
Content-Disposition: attachment; filename="speech.wav"

[音频二进制数据]
```

#### 对话语音合成

**请求：**
```
GET /chat/unified-tts?message=你好
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

#### 1. 安装 UnifiedTTS

访问 UnifiedTTS 项目，按照项目说明安装和配置，然后启动服务。

#### 2. 测试 UnifiedTTS

确保 UnifiedTTS 服务正在运行并可以正常访问。

#### 3. 安装 Ollama

访问 Ollama 官网下载并安装：https://ollama.com/

#### 4. 拉取模型

```bash
ollama pull qwen3.5
ollama pull llama3
```

---

## 八、使用示例

### 8.1 支持的 TTS 引擎

| 引擎 | 说明 |
|------|------|
| `chattts` | ChatTTS |
| `edge-tts` | Edge-TTS |
| `emotivoice` | EmotiVoice |
| `mars5-tts` | MARS5-TTS |
| 其他自定义引擎 | 自定义引擎 |

### 8.2 cURL 调用

#### 统一语音合成

```bash
curl -X POST "http://localhost:8080/tts/unified" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "你好，欢迎使用 UnifiedTTS！",
    "engine": "chattts"
  }' \
  --output speech.wav
```

#### 多引擎语音合成

```bash
curl -X POST "http://localhost:8080/tts/multi-engine?text=Hello&engine=edge-tts" \
  --output speech.wav
```

#### 对话语音合成

```bash
curl "http://localhost:8080/chat/unified-tts?message=你好" \
  --output response.wav
```

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-audio-unifiedtts
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: UnifiedTTS 支持哪些 TTS 引擎？

UnifiedTTS 支持多种 TTS 引擎，包括 ChatTTS、Edge-TTS、EmotiVoice、MARS5-TTS 等，可以根据应用需求灵活选择合适的 TTS 引擎。

### Q2: 如何选择合适的 TTS 引擎？

根据应用需求选择合适的 TTS 引擎，例如需要情感表达可以选择 EmotiVoice，需要高质量语音可以选择 MARS5-TTS，需要多语言支持可以选择 Edge-TTS。

### Q3: 如何提高响应速度？

可以使用音频缓存来提高响应速度，缓存常见文本的语音结果，监控不同引擎的语音质量和性能。

---

## 十一、许可证

- **UnifiedTTS**：Apache 2.0
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

UnifiedTTS 采用 Apache 2.0 开源许可证。

---

## 参考资源

- **UnifiedTTS 官方网站**：https://unifiedtts.com/
- **Ollama 官方文档**：https://ollama.com/docs
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-unifiedtts

---

## 致谢

- **感谢 UnifiedTTS 社区** 提供优秀的统一文本转语音框架
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
