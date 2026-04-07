# Spring AI 环境准备与快速开始

> **对外标题**：Spring AI 入门实践：环境准备与快速开始

> 本文是《Spring AI 入门实践》系列的第一篇文章，将带您了解 Spring AI 的核心概念，完成环境搭建，并创建第一个 Spring AI 应用。

**编者说明（读前看一眼即可）**：本系列成文较早，后续随 **Spring Boot 3.5.x / Spring AI 1.1.x** 做过多轮修订。文中「从零新建 `spring-ai-demo`」的段落，目的是让你**独立走通**依赖与启动流程；若你直接打开本仓库的 **`spring-ai-examples`** 聚合工程，包名、类名会与下文不完全相同，这很正常——把**原理**对上，比逐字抄包名更重要。第二篇 [文本生成](./spring-ai-text-generation.md) 已与仓库内 **`spring-ai-ollama-generation`** 逐行对齐，建议两篇对照阅读。

## 目录

- [Spring AI 简介](#spring-ai-简介)
- [环境准备](#环境准备)
- [第一个 Spring AI 应用](#第一个-spring-ai-应用)
- [项目结构分析](#项目结构分析)
- [最佳实践](#最佳实践)
- [扩展阅读](#扩展阅读)

## Spring AI 简介

### 什么是 Spring AI？

Spring AI 是 Spring 生态系统中的新一代 AI 应用框架，旨在简化 AI 应用的开发。它提供了统一的 API 来集成各种 AI 模型和服务，让开发者能够轻松构建智能应用。

### 核心特性

- **统一的 AI 模型接口** - 支持 OpenAI、Anthropic、Azure OpenAI、Ollama 等主流 AI 提供商
- **多模态支持** - 文本生成、对话、图像处理、语音识别等
- **向量数据库集成** - 支持 Chroma、Elasticsearch、MongoDB、Neo4j 等 15+ 种向量数据库
- **RAG 检索增强** - 内置检索增强生成能力
- **工具调用** - 支持 Function Calling 和外部工具集成
- **Spring Boot 自动配置** - 开箱即用的配置和启动器

### 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring AI 应用层                          │
├─────────────────────────────────────────────────────────────┤
│  ChatClient │ EmbeddingClient │ ImageClient │ AudioClient   │
├─────────────────────────────────────────────────────────────┤
│                    Spring AI 核心层                          │
├─────────────────────────────────────────────────────────────┤
│  Prompt Templates │ Structured Output │ Chat Memory        │
├─────────────────────────────────────────────────────────────┤
│                    AI 模型适配层                             │
├─────────────────────────────────────────────────────────────┤
│  OpenAI │ Anthropic │ Azure OpenAI │ Ollama │ 其他模型      │
└─────────────────────────────────────────────────────────────┘
```

## 环境准备

### 系统要求

- **Java 版本**: JDK 17 或更高版本
- **构建工具**: Maven 3.6+ 或 Gradle 7.0+
- **Spring Boot**: 3.5.x（本仓库父 POM 为 3.5.6）
- **Spring AI**: 1.1.x（本仓库 `spring-ai.version` 为 1.1.4）

### 开发环境搭建

#### 1. 安装 Java 17+

```bash
# 检查 Java 版本
java -version

# 如果没有安装，请下载并安装 JDK 17+
# 下载地址：https://adoptium.net/
```

#### 2. 安装 Maven

```bash
# 检查 Maven 版本
mvn -version

# 如果没有安装，请下载并安装 Maven 3.6+
# 下载地址：https://maven.apache.org/download.cgi
```

#### 3. 安装 IDE

推荐使用 IntelliJ IDEA 或 VS Code：

- **IntelliJ IDEA**: 下载地址 https://www.jetbrains.com/idea/
- **VS Code**: 下载地址 https://code.visualstudio.com/

#### 4. 安装 Ollama（可选）

如果您想使用本地 AI 模型，需要安装 Ollama：

```bash
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh

# Windows
# 下载地址：https://ollama.com/download

# 验证安装
ollama --version

# 下载测试模型
ollama pull qwen3.5:7b
```

### 项目依赖管理

#### Maven 依赖

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.5.6</spring-boot.version>
    <spring-ai.version>1.1.4</spring-ai.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### Spring AI 启动器

```xml
<!-- Ollama 模型支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>

<!-- OpenAI 模型支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>

<!-- Anthropic 模型支持 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-anthropic</artifactId>
</dependency>
```

## 第一个 Spring AI 应用

### 项目创建

#### 1. 使用 Spring Initializr

访问 https://start.spring.io/ 创建项目：

- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.6
- **Java**: 17
- **Packaging**: Jar
- **Dependencies**: 
  - Spring Web
  - Spring AI Ollama

#### 2. 手动创建项目

创建 `pom.xml` 文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>spring-ai-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-ai-demo</name>
    <description>Spring AI Demo Project</description>
    
    <properties>
        <java.version>17</java.version>
        <spring-ai.version>1.1.4</spring-ai.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring AI Ollama -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-ollama</artifactId>
        </dependency>
        
        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 应用配置

创建 `src/main/resources/application.properties`：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3.5:7b
spring.ai.ollama.chat.options.temperature=0.7

# 应用配置
server.port=8080
spring.application.name=spring-ai-demo
```

### 主应用类

创建 `src/main/java/com/example/SpringAiDemoApplication.java`：

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
    }
}
```

### 控制器实现

创建 `src/main/java/com/example/controller/ChatController.java`：

```java
package com.example.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/generate")
    public Map<String, Object> generateText(@RequestBody Map<String, String> request) {
        try {
            String message = request.getOrDefault("message", "你好！");
            Prompt prompt = new Prompt(new UserMessage(message));
            ChatResponse response = chatModel.call(prompt);

            return Map.of(
                "success", true,
                "message", response.getResult().getOutput().getText(),
                "usage", response.getMetadata().getUsage()
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "message", "Spring AI Demo is running!");
    }
}
```

### 运行应用

#### 1. 启动 Ollama 服务

```bash
# 启动 Ollama 服务
ollama serve

# 在另一个终端下载模型
ollama pull qwen3.5:7b
```

#### 2. 启动 Spring Boot 应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

#### 3. 测试应用

```bash
# 健康检查
curl http://localhost:8080/api/chat/health

# 文本生成
curl -X POST http://localhost:8080/api/chat/generate \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Spring AI"}'
```

### 预期输出

```json
{
  "success": true,
  "message": "Spring AI 是 Spring 生态系统中的新一代 AI 应用框架...",
  "usage": {
    "promptTokens": 10,
    "generationTokens": 150,
    "totalTokens": 160
  }
}
```

## 项目结构分析

### 标准项目结构

```
spring-ai-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── SpringAiDemoApplication.java
│   │   │       ├── controller/
│   │   │       │   └── ChatController.java
│   │   │       ├── service/
│   │   │       │   └── ChatService.java
│   │   │       └── config/
│   │   │           └── AiConfig.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/
│           └── com/example/
│               └── SpringAiDemoApplicationTests.java
├── pom.xml
├── README.md
└── .gitignore
```

### 核心组件说明

#### 1. 主应用类
- **SpringAiDemoApplication**: 应用入口，使用 `@SpringBootApplication` 注解

#### 2. 控制器层
- **ChatController**: 处理 HTTP 请求，提供 REST API

#### 3. 服务层
- **ChatService**: 业务逻辑处理，封装 AI 模型调用

#### 4. 配置层
- **AiConfig**: AI 相关配置，如模型参数、重试策略等

#### 5. 配置文件
- **application.properties**: 主配置文件
- **application-dev.properties**: 开发环境配置
- **application-prod.properties**: 生产环境配置

### 依赖关系

```
┌─────────────────┐     ┌─────────────────┐    ┌─────────────────┐
│   Controller       │───▶│     Service         │───▶│   ChatClient    │
└─────────────────┘     └─────────────────┘    └─────────────────┘
         │                        │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   HTTP Client   │    │  Business Logic │    │   AI Provider   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 最佳实践

### 1. 配置管理

#### 环境分离
```properties
# application-dev.properties
spring.ai.ollama.chat.options.model=qwen3.5:7b
spring.ai.ollama.chat.options.temperature=0.8

# application-prod.properties
spring.ai.ollama.chat.options.model=qwen3.5:14b
spring.ai.ollama.chat.options.temperature=0.5
```

#### 配置验证
```java
@Configuration
@ConfigurationProperties(prefix = "spring.ai.ollama")
@Validated
public class OllamaProperties {
    
    @NotBlank
    private String baseUrl = "http://localhost:11434";
    
    @Valid
    private ChatOptions chat = new ChatOptions();
    
    // getters and setters
}
```

### 2. 错误处理

#### 全局异常处理
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
    }
}
```

#### 重试机制
```properties
# 重试配置
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=1000
spring.ai.retry.backoff.multiplier=2.0
spring.ai.retry.backoff.max-interval=10000
```

### 3. 性能优化

#### 连接池配置
```properties
# HTTP 连接池
spring.ai.ollama.client.connection-timeout=30s
spring.ai.ollama.client.read-timeout=60s
spring.ai.ollama.client.max-connections=50
```

#### 缓存策略
```java
@Service
public class ChatService {
    
    @Cacheable(value = "chat-responses", key = "#message")
    public String generateResponse(String message) {
        // AI 模型调用
    }
}
```

### 4. 监控和日志

#### 日志配置
```properties
# 日志级别
logging.level.org.springframework.ai=DEBUG
logging.level.com.example=INFO
```

#### 健康检查
```java
@Component
public class OllamaHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // 检查 Ollama 服务状态
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
    }
}
```

## 扩展阅读

### 官方资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)

### 相关技术

- [Ollama 官方文档](https://ollama.com/docs)
- [Spring Boot 3.x 新特性](https://spring.io/projects/spring-boot)
- [Java 17 新特性](https://openjdk.org/projects/jdk/17/)

### 学习路径

1. **基础阶段**: Spring Boot 基础 → Spring AI 入门
2. **进阶阶段**: AI 模型集成 → 向量数据库 → RAG 应用
3. **高级阶段**: 微服务架构 → 性能优化 → 生产部署

### 实践项目

- [Spring AI Examples](https://github.com/spring-projects/spring-ai/tree/main/spring-ai-examples)
- [Ollama 示例项目](https://github.com/ollama/ollama/tree/main/examples)

---

**下一篇预告**: 《Spring AI 文本生成（Text Generation API）》- 深入探讨 Spring AI 的文本生成能力，包括同步生成、流式生成、提示词工程等核心功能。

---

*本文是《Spring AI 入门实践》系列的第一篇文章，后续将陆续发布更多技术文章，敬请关注！*

