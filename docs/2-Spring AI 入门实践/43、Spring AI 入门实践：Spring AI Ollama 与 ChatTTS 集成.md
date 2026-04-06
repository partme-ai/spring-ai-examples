# Spring AI 入门实践：Spring AI Ollama 与 ChatTTS 集成

> 基于 Spring AI 框架实现 Ollama 与 ChatTTS 的集成，使用本地大语言模型和开源文本转语音模型，构建完整的语音交互应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Ollama 与 ChatTTS 的示例，展示了如何在 Java/Spring Boot 应用中使用本地大语言模型 Ollama 和开源文本转语音模型 ChatTTS，构建完整的语音交互应用，实现文本生成和语音转换的端到端解决方案。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama | - | 本地大语言模型 |
| ChatTTS | - | 开源文本转语音模型 |
| ChatTTS-ui | - | ChatTTS Web 界面和 API |

### 1.3 核心功能

- ✅ 本地模型：使用 Ollama 运行本地大语言模型
- ✅ 文本转语音：使用 ChatTTS 进行文本转语音
- ✅ 中英文混合：支持中英文混合语音合成
- ✅ 对话转语音：将 Ollama 对话直接转换为语音
- ✅ 离线运行：完全离线运行，无需网络
- ✅ 异步处理：支持异步处理提高响应速度
- ✅ 音频缓存：支持音频缓存优化性能

---

## 二、ChatTTS 简介

### 2.1 ChatTTS 介绍

ChatTTS 是一款优秀的开源文本转语音（TTS）模型，支持中英文混合，具有出色的语音合成质量。ChatTTS 不直接提供 API 功能，需要配合 ChatTTS-ui 使用，后者提供了 Web 界面和 REST API 接口，方便集成到应用中。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **中英文混合** | 支持中英文混合语音合成 |
| **开源免费** | 完全开源，免费使用 |
| **高质量语音** | 出色的语音合成质量 |
| **灵活配置** | 支持多种参数配置 |
| **REST API** | 通过 ChatTTS-ui 提供 REST API |
| **Web 界面** | 提供 Web 界面方便测试 |
| **离线运行** | 完全离线运行，无需网络 |

---

## 三、项目结构

```
spring-ai-ollama-audio-chattts/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAudioChatttsApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── TTSController.java
│   │   │                   │   └── ChatTTSController.java
│   │   │                   └── service/
│   │   │                       └── ChatTTSService.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiOllamaAudioChatttsApplication.java` - Spring Boot 应用入口
- `TTSController.java` - 文本转语音 API 控制器
- `ChatTTSController.java` - 对话转语音 API 控制器
- `ChatTTSService.java` - ChatTTS 服务
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-audio-chattts
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# ChatTTS 配置
chattts.api.url=http://localhost:8080/api/tts
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

### 5.1 ChatTTS 服务实现

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatTTSService {

    @Value("${chattts.api.url}")
    private String chatTtsApiUrl;

    private final RestTemplate restTemplate;

    public ChatTTSService() {
        this.restTemplate = new RestTemplate();
    }

    public byte[] textToSpeech(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("speaker", 0);
        requestBody.put("speed", 1.0);
        requestBody.put("temperature", 0.3);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                chatTtsApiUrl,
                HttpMethod.POST,
                request,
                byte[].class
        );

        return response.getBody();
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
public class TTSController {

    private final ChatTTSService chatTTSService;

    @Autowired
    public TTSController(ChatTTSService chatTTSService) {
        this.chatTTSService = chatTTSService;
    }

    @PostMapping("/tts/generate")
    public ResponseEntity<byte[]> generateSpeech(@RequestBody String text) {
        byte[] audio = chatTTSService.textToSpeech(text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        headers.setContentDispositionFormData("attachment", "speech.wav");

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
public class ChatTTSController {

    private final ChatModel chatModel;
    private final ChatTTSService chatTTSService;

    @Autowired
    public ChatTTSController(ChatModel chatModel, ChatTTSService chatTTSService) {
        this.chatModel = chatModel;
        this.chatTTSService = chatTTSService;
    }

    @GetMapping("/chat/tts")
    public ResponseEntity<byte[]> chatAndSpeak(@RequestParam String message) {
        // 使用 Ollama 生成回复
        String response = chatModel.call(message);
        
        // 使用 ChatTTS 转换为语音
        byte[] audio = chatTTSService.textToSpeech(response);

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
| `POST` | `/tts/generate` | 文本转语音 |
| `GET` | `/chat/tts` | 对话转语音 |

### 6.2 请求/响应示例

#### 文本转语音

**请求：**
```
POST /tts/generate
Content-Type: text/plain

你好，欢迎使用 ChatTTS！
```

**响应：**
```
Content-Type: audio/wav
Content-Disposition: attachment; filename="speech.wav"

[音频二进制数据]
```

#### 对话转语音

**请求：**
```
GET /chat/tts?message=你好
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

#### 1. 安装 Ollama

访问 Ollama 官网下载并安装：https://ollama.com/

#### 2. 拉取模型

```bash
ollama pull qwen3.5
ollama pull llama3
```

#### 3. 安装 ChatTTS-ui

访问 ChatTTS-ui 项目：https://github.com/jianchang512/ChatTTS-ui

按照项目说明安装和配置，然后启动服务。

---

## 八、使用示例

### 8.1 cURL 调用

#### 文本转语音

```bash
curl -X POST "http://localhost:8080/tts/generate" \
  -H "Content-Type: text/plain" \
  -d "你好，欢迎使用 ChatTTS！" \
  --output speech.wav
```

#### 对话转语音

```bash
curl "http://localhost:8080/chat/tts?message=你好" \
  --output response.wav
```

### 8.2 推荐模型

#### Qwen3.5

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

#### Llama 3

Meta 的最新开源模型：

```bash
ollama run llama3
ollama run llama3:70b
```

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-audio-chattts
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: ChatTTS 不提供 API 怎么办？

ChatTTS 不直接提供 API 功能，需要配合 ChatTTS-ui 使用，后者提供了 REST API 接口。

### Q2: 如何优化音频质量？

可以调整 ChatTTS 的参数来优化音质，如 speaker、speed、temperature 等参数。

### Q3: 如何提高响应速度？

可以使用异步处理和音频缓存来提高响应速度，缓存常见文本的语音结果。

---

## 十一、许可证

- **ChatTTS**：MIT 许可证
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

ChatTTS 采用 MIT 开源许可证。

---

## 参考资源

- **ChatTTS 官方项目**：https://github.com/2noise/ChatTTS
- **ChatTTS-ui 项目**：https://github.com/jianchang512/ChatTTS-ui
- **Ollama 官方文档**：https://ollama.com/docs
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-chattts

---

## 致谢

- **感谢 ChatTTS 社区** 提供优秀的文本转语音模型
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
