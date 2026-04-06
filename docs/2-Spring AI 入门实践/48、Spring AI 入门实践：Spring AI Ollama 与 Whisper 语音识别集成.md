# Spring AI 入门实践：Spring AI Ollama 与 Whisper 语音识别集成

> 基于 Spring AI 框架实现 Ollama 与 Whisper 的集成，使用本地大语言模型和 OpenAI 开源的自动语音识别模型，构建完整的语音交互应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Ollama 与 Whisper 的示例，展示了如何在 Java/Spring Boot 应用中使用本地大语言模型 Ollama 和 OpenAI 开源的自动语音识别（ASR）模型 Whisper，构建完整的语音交互应用，实现语音识别、文字对话的端到端解决方案。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama | - | 本地大语言模型 |
| Whisper | - | OpenAI 语音识别 |
| Python | 3.7+ | Whisper 运行环境 |

### 1.3 核心功能

- ✅ 本地模型：使用 Ollama 运行本地大语言模型
- ✅ 语音识别：使用 Whisper 进行自动语音识别
- ✅ 多语言支持：支持多语言语音识别
- ✅ 语音对话：语音输入 → 文字 → Ollama 对话
- ✅ 离线运行：完全离线运行，无需网络
- ✅ 多种模型：支持多种模型大小（tiny、base、small、medium、large）
- ✅ 结果缓存：支持识别结果缓存优化性能

---

## 二、Whisper 简介

### 2.1 Whisper 介绍

Whisper 是 OpenAI 开源的自动语音识别（ASR）模型，支持多语言语音转文字，具有出色的语音识别质量。它支持多种语言和多种模型大小，从快速的 tiny 模型到高精度的 large 模型，可以根据需求选择合适的模型，是一个优秀的语音识别解决方案。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **高质量识别** | 出色的语音识别质量 |
| **多语言支持** | 支持多语言语音识别 |
| **开源免费** | 完全开源，免费使用 |
| **多种模型** | 支持多种模型大小 |
| **GPU 加速** | 支持 GPU 加速 |
| **易于部署** | 易于部署和集成 |
| **离线运行** | 完全离线运行，无需网络 |

---

## 三、项目结构

```
spring-ai-ollama-audio-whisper/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAudioWhisperApplication.java
│   │   │                   ├── controller/
│   │   │                   │   ├── WhisperController.java
│   │   │                   │   ├── VoiceChatController.java
│   │   │                   │   └── MultiLanguageSTTController.java
│   │   │                   └── service/
│   │   │                       └── WhisperService.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiOllamaAudioWhisperApplication.java` - Spring Boot 应用入口
- `WhisperController.java` - 语音识别 API 控制器
- `VoiceChatController.java` - 语音对话 API 控制器
- `MultiLanguageSTTController.java` - 多语言语音识别 API 控制器
- `WhisperService.java` - Whisper 服务
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-audio-whisper
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# Whisper 配置
whisper.model=base
whisper.language=auto
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

### 5.1 Whisper 服务实现

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class WhisperService {

    @Value("${whisper.model:base}")
    private String whisperModel;

    @Value("${whisper.language:auto}")
    private String defaultLanguage;

    public String transcribe(MultipartFile file) {
        return transcribe(file, defaultLanguage);
    }

    public String transcribe(MultipartFile file, String language) {
        try {
            // 保存临时文件
            Path tempFile = Files.createTempFile("whisper_", ".wav");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // 构建 Whisper 命令
            String[] command;
            if ("auto".equals(language)) {
                command = new String[]{
                    "whisper",
                    tempFile.toString(),
                    "--model", whisperModel,
                    "--output-format", "txt"
                };
            } else {
                command = new String[]{
                    "whisper",
                    tempFile.toString(),
                    "--model", whisperModel,
                    "--language", language,
                    "--output-format", "txt"
                };
            }

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            // 等待命令完成
            int exitCode = process.waitFor();

            // 删除临时文件
            Files.deleteIfExists(tempFile);

            if (exitCode == 0) {
                return result.toString().trim();
            } else {
                throw new RuntimeException("Whisper command failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to transcribe audio with Whisper", e);
        }
    }
}
```

### 5.2 语音识别控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WhisperController {

    private final WhisperService whisperService;

    @Autowired
    public WhisperController(WhisperService whisperService) {
        this.whisperService = whisperService;
    }

    @PostMapping("/stt/whisper")
    public String transcribeAudio(@RequestParam("file") MultipartFile file) {
        return whisperService.transcribe(file);
    }
}
```

### 5.3 语音对话控制器

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VoiceChatController {

    private final ChatModel chatModel;
    private final WhisperService whisperService;

    @Autowired
    public VoiceChatController(ChatModel chatModel, WhisperService whisperService) {
        this.chatModel = chatModel;
        this.whisperService = whisperService;
    }

    @PostMapping("/voice/chat")
    public String voiceChat(@RequestParam("file") MultipartFile file) {
        // 使用 Whisper 识别语音
        String text = whisperService.transcribe(file);
        
        // 使用 Ollama 生成回复
        return chatModel.call(text);
    }
}
```

### 5.4 多语言语音识别控制器

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MultiLanguageSTTController {

    private final WhisperService whisperService;

    @Autowired
    public MultiLanguageSTTController(WhisperService whisperService) {
        this.whisperService = whisperService;
    }

    @PostMapping("/stt/multilang")
    public String transcribeWithLanguage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "auto") String language) {
        return whisperService.transcribe(file, language);
    }
}
```

---

## 六、API 接口说明

### 6.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/stt/whisper` | 语音识别 |
| `POST` | `/voice/chat` | 语音对话 |
| `POST` | `/stt/multilang` | 多语言语音识别 |

### 6.2 请求/响应示例

#### 语音识别

**请求：**
```
POST /stt/whisper
Content-Type: multipart/form-data

file: [音频文件]
```

**响应：**
```
Content-Type: text/plain

你好，这是一个测试。
```

#### 语音对话

**请求：**
```
POST /voice/chat
Content-Type: multipart/form-data

file: [音频文件]
```

**响应：**
```
Content-Type: text/plain

你好！我很高兴为您服务。有什么我可以帮助您的吗？
```

#### 多语言语音识别

**请求：**
```
POST /stt/multilang?language=Chinese
Content-Type: multipart/form-data

file: [音频文件]
```

**响应：**
```
Content-Type: text/plain

你好，这是一个测试。
```

---

## 七、部署方式

### 方式一：本地部署

#### 1. 安装 Python

确保安装 Python 3.7+ 版本。

#### 2. 安装 CUDA（可选，用于 GPU 加速）

```bash
pip install cuda
```

#### 3. 安装 Whisper

```bash
pip install openai-whisper
```

#### 4. 测试 Whisper

```bash
whisper audio.mp3 --language Chinese --model large
```

#### 5. 安装 Ollama

访问 Ollama 官网下载并安装：https://ollama.com/

#### 6. 拉取模型

```bash
ollama pull qwen3.5
ollama pull llama3
```

---

## 八、使用示例

### 8.1 Whisper 模型选择

Whisper 提供多种模型大小，从快速的 tiny 模型到高精度的 large 模型：

| 模型 | 参数量 | 相对速度 | 适用场景 |
|------|--------|----------|----------|
| tiny | 39M | 最快 | 快速识别，精度较低 |
| base | 74M | 快 | 一般场景 |
| small | 244M | 中等 | 平衡性能和精度 |
| medium | 769M | 慢 | 高精度需求 |
| large | 1550M | 最慢 | 最高精度 |

### 8.2 模型使用示例

```bash
# 使用不同模型
whisper audio.mp3 --model tiny
whisper audio.mp3 --model base
whisper audio.mp3 --model small
whisper audio.mp3 --model medium
whisper audio.mp3 --model large
```

### 8.3 cURL 调用

#### 语音识别

```bash
curl -X POST "http://localhost:8080/stt/whisper" \
  -F "file=@audio.wav"
```

#### 语音对话

```bash
curl -X POST "http://localhost:8080/voice/chat" \
  -F "file=@audio.wav"
```

#### 多语言语音识别

```bash
curl -X POST "http://localhost:8080/stt/multilang?language=Chinese" \
  -F "file=@audio.wav"
```

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-audio-whisper
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: Whisper 有哪些模型大小？

Whisper 提供多种模型大小，从快速的 tiny 模型（39M 参数）到高精度的 large 模型（1550M 参数），可以根据精度和速度需求选择合适的模型。

### Q2: 如何选择合适的 Whisper 模型？

根据精度和速度需求选择合适的模型，一般场景可以选择 base 或 small 模型，高精度需求可以选择 medium 或 large 模型，快速识别可以选择 tiny 模型。

### Q3: 如何提高 Whisper 的识别速度？

可以使用 GPU 加速来提高识别速度，也可以选择更小的模型来提高速度，或者缓存识别结果来提高响应速度。

---

## 十一、许可证

- **Whisper**：MIT
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

Whisper 采用 MIT 开源许可证。

---

## 参考资源

- **Whisper 官方项目**：https://github.com/openai/whisper
- **Whisper Hugging Face**：https://huggingface.co/openai/whisper-large-v3
- **Ollama 官方文档**：https://ollama.com/docs
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-audio-whisper

---

## 致谢

- **感谢 OpenAI Whisper 团队** 提供优秀的自动语音识别模型
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
