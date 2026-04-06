# Spring AI 环境准备与快速开始

> 本文是《Spring AI 入门实践》系列的第一篇文章，将带您了解 Spring AI 的核心概念，完成环境搭建，并创建第一个 Spring AI 应用。通过本教程，您将掌握 Spring AI 的基本使用，为后续深入学习奠定基础。

## 一、项目概述

### 1.1 项目定位

Spring AI 环境准备与快速开始教程旨在帮助开发者快速上手 Spring AI 框架，了解其核心概念，完成开发环境搭建，并创建第一个可运行的 Spring AI 应用。本教程适合具有 Spring Boot 基础的开发者，无需 AI 专业知识即可快速入门。

### 1.2 技术栈

| 组件                | 版本      | 说明           |
| ----------------- | ------- | ------------ |
| **Spring Boot**   | 3.5.6   | Java 企业级应用框架 |
| **Spring AI**     | 1.1.4   | AI 模型集成框架    |
| **Java**          | 17+     | 编程语言和运行环境    |
| **Maven**         | 3.6+    | 项目构建和依赖管理    |
| **Ollama**        | 最新版     | 本地 AI 模型运行环境 |
| **IntelliJ IDEA** | 2023.3+ | 推荐的 Java IDE |

### 1.3 核心功能

- ✅ **环境搭建指南**：详细的 Java、Maven、IDE 安装配置步骤
- ✅ **Spring AI 集成**：快速集成 Ollama 等 AI 模型提供商
- ✅ **项目创建**：从零创建 Spring Boot + Spring AI 项目
- ✅ **代码示例**：完整的控制器和配置代码实现
- ✅ **运行验证**：应用启动和功能测试步骤
- ✅ **最佳实践**：配置管理、错误处理、性能优化建议

***

## 二、Spring AI 简介

> 本节内容基于 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

### 2.1 Spring AI 介绍

Spring AI 是 Spring 生态系统中的新一代 AI 应用框架，旨在简化 AI 应用的开发。它提供了统一的 API 来集成各种 AI 模型和服务，让开发者能够轻松构建智能应用。Spring AI 基于 Spring Boot 的自动配置特性，提供了开箱即用的 AI 功能集成。

### 2.2 核心特性

| 特性                   | 说明                                                           |
| -------------------- | ------------------------------------------------------------ |
| **统一的 AI 模型接口**      | 支持 OpenAI、Anthropic、Azure OpenAI、Ollama 等主流 AI 提供商，提供一致的编程接口 |
| **多模态支持**            | 文本生成、对话、图像处理、语音识别等多种 AI 能力                                   |
| **向量数据库集成**          | 支持 Chroma、Elasticsearch、MongoDB、Neo4j 等 15+ 种向量数据库           |
| **RAG 检索增强**         | 内置检索增强生成（RAG）能力，支持文档检索和智能问答                                  |
| **工具调用**             | 支持 Function Calling 和外部工具集成，实现 AI 与业务系统的交互                   |
| **Spring Boot 自动配置** | 基于 Spring Boot 的自动配置机制，简化 AI 服务集成                            |

### 2.3 技术架构

Spring AI 采用分层架构设计，从上到下分为应用层、核心层和模型适配层：

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

**架构说明**：

- **应用层**：提供面向开发者的客户端接口，如 ChatClient、EmbeddingClient 等
- **核心层**：包含提示模板、结构化输出、对话记忆等核心功能组件
- **模型适配层**：对接各种 AI 模型提供商，实现统一的 API 抽象

***

## 三、环境要求与系统配置

### 3.1 系统要求

为确保 Spring AI 应用的顺利开发和运行，需要满足以下系统要求：

| 组件              | 最低要求       | 推荐版本       | 说明                              |
| --------------- | ---------- | ---------- | ------------------------------- |
| **Java**        | JDK 17     | JDK 21     | Spring Boot 3.5.x 要求 Java 17+   |
| **构建工具**        | Maven 3.6+ | Maven 3.9+ | 或 Gradle 7.0+                   |
| **Spring Boot** | 3.5.x      | 3.5.6      | 本仓库父 POM 为 3.5.6                |
| **Spring AI**   | 1.1.x      | 1.1.4      | 本仓库 `spring-ai.version` 为 1.1.4 |
| **内存**          | 4GB RAM    | 8GB RAM    | 运行 Ollama 模型需要额外内存              |
| **磁盘空间**        | 2GB        | 5GB+       | 存储模型文件和依赖库                      |

### 3.2 开发环境搭建

#### 步骤 1：安装 Java 17+

```bash
# 检查当前 Java 版本
java -version

# 如果未安装或版本过低，请下载安装 JDK 17+
# 下载地址：https://adoptium.net/
# 或使用包管理器安装：
# Ubuntu/Debian: sudo apt install openjdk-17-jdk
# macOS: brew install openjdk@17
```

#### 步骤 2：安装 Maven

```bash
# 检查 Maven 版本
mvn -version

# 如果未安装，请下载安装 Maven 3.6+
# 下载地址：https://maven.apache.org/download.cgi
# 或使用包管理器安装：
# Ubuntu/Debian: sudo apt install maven
# macOS: brew install maven
```

#### 步骤 3：安装 IDE（推荐）

- **IntelliJ IDEA**：下载地址 <https://www.jetbrains.com/idea/>
  - 社区版（免费）已足够开发 Spring AI 应用
  - 专业版提供更多企业级功能
- **VS Code**：下载地址 <https://code.visualstudio.com/>
  - 需要安装 Java 扩展包
  - 轻量级，适合配置较低的开发环境

#### 步骤 4：安装 Ollama（可选但推荐）

Ollama 允许在本地运行 AI 模型，避免依赖外部 API 服务：

```bash
# macOS 安装
curl -fsSL https://ollama.com/install.sh | sh

# Linux 安装
# Ubuntu/Debian:
curl -fsSL https://ollama.com/install.sh | sh
# 或使用 Snap:
sudo snap install ollama

# Windows 安装
# 下载地址：https://ollama.com/download

# 验证安装
ollama --version

# 下载测试模型（约 4GB）
ollama pull qwen3.5:7b

# 启动 Ollama 服务
ollama serve
```

### 3.3 网络环境要求

- **Ollama 本地运行**：无需外部网络，模型文件本地存储
- **使用云 AI 服务**：需要访问 OpenAI、Anthropic 等服务的网络环境
- **Maven 仓库访问**：需要能访问 Maven Central 或阿里云镜像仓库

***

## 四、项目结构

### 4.1 标准 Spring AI 项目结构

一个典型的 Spring AI 项目遵循标准的 Spring Boot 项目结构：

```
spring-ai-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── SpringAiDemoApplication.java          # 主应用类
│   │   │       ├── controller/
│   │   │       │   └── ChatController.java               # REST 控制器
│   │   │       ├── service/
│   │   │       │   └── ChatService.java                  # 业务服务层
│   │   │       └── config/
│   │   │           └── AiConfig.java                     # AI 配置类
│   │   └── resources/
│   │       ├── application.properties                    # 主配置文件
│   │       ├── application-dev.properties                # 开发环境配置
│   │       └── application-prod.properties               # 生产环境配置
│   └── test/
│       └── java/
│           └── com/example/
│               └── SpringAiDemoApplicationTests.java     # 应用测试类
├── pom.xml                                              # Maven 配置文件
├── README.md                                            # 项目说明文档
└── .gitignore                                           # Git 忽略文件
```

### 4.2 核心组件说明

#### 4.2.1 主应用类 (`SpringAiDemoApplication.java`)

- **位置**：`src/main/java/com/example/SpringAiDemoApplication.java`
- **作用**：应用入口点，使用 `@SpringBootApplication` 注解启用 Spring Boot 自动配置
- **关键代码**：
  ```java
  @SpringBootApplication
  public class SpringAiDemoApplication {
      public static void main(String[] args) {
          SpringApplication.run(SpringAiDemoApplication.class, args);
      }
  }
  ```

#### 4.2.2 控制器层 (`ChatController.java`)

- **位置**：`src/main/java/com/example/controller/ChatController.java`
- **作用**：处理 HTTP 请求，提供 REST API 接口
- **关键注解**：`@RestController`、`@RequestMapping`、`@PostMapping`、`@GetMapping`

#### 4.2.3 服务层 (`ChatService.java`)

- **位置**：`src/main/java/com/example/service/ChatService.java`
- **作用**：封装业务逻辑，处理 AI 模型调用和数据处理
- **关键注解**：`@Service`

#### 4.2.4 配置层 (`AiConfig.java`)

- **位置**：`src/main/java/com/example/config/AiConfig.java`
- **作用**：配置 AI 相关参数，如模型选择、超时设置、重试策略等
- **关键注解**：`@Configuration`、`@ConfigurationProperties`

#### 4.2.5 配置文件

- **主配置**：`application.properties` - 通用配置项
- **开发环境**：`application-dev.properties` - 开发专用配置
- **生产环境**：`application-prod.properties` - 生产环境配置

### 4.3 依赖关系图

Spring AI 应用的典型依赖关系如下：

```
┌─────────────────┐     ┌─────────────────┐    ┌─────────────────┐
│   Controller    │───▶│     Service      │───▶│   ChatClient    │
└─────────────────┘     └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   HTTP Client   │    │  Business Logic │    │   AI Provider   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Spring MVC    │    │   Spring Core   │    │   Ollama/API    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

***

## 五、核心配置

### 5.1 依赖配置 (`pom.xml`)

Spring AI 项目使用 Maven 进行依赖管理，核心配置如下：

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

### 5.2 应用配置 (`application.properties`)

Spring AI 应用的核心配置文件：

```properties
# ====================
# Ollama 配置
# ====================

# Ollama 服务地址（默认端口 11434）
spring.ai.ollama.base-url=http://localhost:11434

# 聊天模型配置
spring.ai.ollama.chat.options.model=qwen3.5:7b
spring.ai.ollama.chat.options.temperature=0.7
spring.ai.ollama.chat.options.max-tokens=2048
spring.ai.ollama.chat.options.top-p=0.9

# ====================
# 应用基础配置
# ====================

# 服务端口
server.port=8080

# 应用名称
spring.application.name=spring-ai-demo

# 日志配置
logging.level.org.springframework.ai=INFO
logging.level.com.example=DEBUG

# ====================
# 连接和超时配置
# ====================

# HTTP 客户端配置
spring.ai.ollama.client.connection-timeout=30s
spring.ai.ollama.client.read-timeout=60s
spring.ai.ollama.client.max-connections=50

# 重试配置
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=1000
spring.ai.retry.backoff.multiplier=2.0
spring.ai.retry.backoff.max-interval=10000
```

### 5.3 多环境配置示例

#### 开发环境配置 (`application-dev.properties`)

```properties
# 开发环境专用配置
spring.ai.ollama.chat.options.model=qwen3.5:7b
spring.ai.ollama.chat.options.temperature=0.8
spring.ai.ollama.chat.options.max-tokens=1024

# 开发环境日志级别
logging.level.org.springframework.ai=DEBUG
logging.level.com.example=DEBUG

# 开发环境服务端口
server.port=8081
```

#### 生产环境配置 (`application-prod.properties`)

```properties
# 生产环境专用配置
spring.ai.ollama.chat.options.model=qwen3.5:14b
spring.ai.ollama.chat.options.temperature=0.5
spring.ai.ollama.chat.options.max-tokens=4096

# 生产环境日志级别
logging.level.org.springframework.ai=WARN
logging.level.com.example=INFO

# 生产环境服务端口
server.port=8080

# 生产环境连接池配置
spring.ai.ollama.client.max-connections=100
spring.ai.ollama.client.connection-timeout=60s
spring.ai.ollama.client.read-timeout=120s
```

### 5.4 其他 AI 模型配置示例

#### OpenAI 配置

```properties
# OpenAI 配置
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4-turbo
spring.ai.openai.chat.options.temperature=0.7
```

#### Anthropic 配置

```properties
# Anthropic 配置
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY}
spring.ai.anthropic.chat.options.model=claude-3-opus-20240229
spring.ai.anthropic.chat.options.temperature=0.7
```

***

## 六、代码实现详解

### 6.1 主应用类实现

主应用类是 Spring Boot 应用的入口点，负责启动整个应用：

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI 演示应用主类
 * 
 * 使用 @SpringBootApplication 注解启用 Spring Boot 自动配置
 * 该注解包含了 @Configuration、@EnableAutoConfiguration 和 @ComponentScan
 */
@SpringBootApplication
public class SpringAiDemoApplication {

    /**
     * 应用主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(SpringAiDemoApplication.class, args);
        
        // 应用启动后的日志输出
        System.out.println("=========================================");
        System.out.println("Spring AI Demo 应用启动成功！");
        System.out.println("服务地址: http://localhost:8080");
        System.out.println("健康检查: http://localhost:8080/api/chat/health");
        System.out.println("=========================================");
    }
}
```

### 6.2 控制器实现详解

控制器负责处理 HTTP 请求，提供 RESTful API 接口：

```java
package com.example.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 聊天控制器
 * 
 * 提供 AI 聊天相关的 REST API 接口
 * 使用 @RestController 注解表示这是一个 REST 控制器
 * 使用 @RequestMapping 注解定义基础路径
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatModel chatModel;

    /**
     * 构造函数注入 ChatModel
     * 
     * @param chatModel Spring AI 提供的聊天模型实例
     */
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 文本生成接口
     * 
     * @param request 包含 message 字段的 JSON 请求体
     * @return 包含生成结果和元数据的 JSON 响应
     */
    @PostMapping("/generate")
    public Map<String, Object> generateText(@RequestBody Map<String, String> request) {
        try {
            // 1. 获取用户输入，默认为"你好！"
            String message = request.getOrDefault("message", "你好！");
            
            // 2. 创建提示，包含用户消息
            Prompt prompt = new Prompt(new UserMessage(message));
            
            // 3. 调用 AI 模型生成响应
            ChatResponse response = chatModel.call(prompt);
            
            // 4. 构建成功响应
            return Map.of(
                "success", true,
                "message", response.getResult().getOutput().getText(),
                "usage", response.getMetadata().getUsage(),
                "model", "qwen3.5:7b",  // 实际应从配置中读取
                "timestamp", LocalDateTime.now().toString()
            );
        } catch (Exception e) {
            // 5. 错误处理
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            );
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 应用健康状态
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "message", "Spring AI Demo is running!",
            "timestamp", LocalDateTime.now().toString(),
            "version", "1.0.0"
        );
    }

    /**
     * 批量生成接口
     * 
     * @param requests 包含多个消息的请求体
     * @return 批量生成结果
     */
    @PostMapping("/batch-generate")
    public Map<String, Object> batchGenerate(@RequestBody Map<String, Object> requests) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("timestamp", LocalDateTime.now().toString());
        
        @SuppressWarnings("unchecked")
        Map<String, String> messages = (Map<String, String>) requests.get("messages");
        Map<String, String> responses = new HashMap<>();
        
        if (messages != null) {
            for (Map.Entry<String, String> entry : messages.entrySet()) {
                try {
                    Prompt prompt = new Prompt(new UserMessage(entry.getValue()));
                    ChatResponse response = chatModel.call(prompt);
                    responses.put(entry.getKey(), response.getResult().getOutput().getText());
                } catch (Exception e) {
                    responses.put(entry.getKey(), "Error: " + e.getMessage());
                }
            }
        }
        
        result.put("responses", responses);
        return result;
    }
}
```

### 6.3 服务层实现（可选但推荐）

服务层封装业务逻辑，使控制器更简洁：

```java
package com.example.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 聊天服务
 * 
 * 封装 AI 模型调用的业务逻辑
 * 使用 @Service 注解标识为 Spring 服务组件
 */
@Service
public class ChatService {

    private final ChatModel chatModel;

    /**
     * 构造函数注入 ChatModel
     */
    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 生成单个响应
     */
    public String generateResponse(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    /**
     * 生成带元数据的响应
     */
    public Map<String, Object> generateResponseWithMetadata(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        ChatResponse response = chatModel.call(prompt);
        
        return Map.of(
            "content", response.getResult().getOutput().getText(),
            "usage", response.getMetadata().getUsage(),
            "finishReason", response.getResult().getOutput().getFinishReason()
        );
    }

    /**
     * 带系统提示的生成
     */
    public String generateWithSystemPrompt(String systemPrompt, String userMessage) {
        Prompt prompt = new Prompt(
            new org.springframework.ai.chat.messages.SystemMessage(systemPrompt),
            new UserMessage(userMessage)
        );
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
```

### 6.4 配置类实现

配置类用于自定义 AI 相关配置：

```java
package com.example.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * AI 配置类
 * 
 * 配置 AI 模型相关参数和 Bean
 */
@Configuration
@ConfigurationProperties(prefix = "spring.ai.ollama")
@Validated
public class AiConfig {

    private String baseUrl = "http://localhost:11434";
    private ChatOptions chat = new ChatOptions();

    /**
     * 创建 OllamaApi Bean
     */
    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(baseUrl);
    }

    /**
     * 创建 OllamaChatModel Bean
     */
    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        OllamaOptions options = OllamaOptions.builder()
            .model(chat.getModel())
            .temperature(chat.getTemperature())
            .maxTokens(chat.getMaxTokens())
            .topP(chat.getTopP())
            .build();
        
        return new OllamaChatModel(ollamaApi, options);
    }

    // Getter 和 Setter 方法
    public static class ChatOptions {
        private String model = "qwen3.5:7b";
        private Double temperature = 0.7;
        private Integer maxTokens = 2048;
        private Double topP = 0.9;
        
        // getters and setters
    }
}
```

***

## 七、API 接口说明

### 7.1 接口列表

Spring AI 演示应用提供以下 REST API 接口：

| 方法     | 路径                         | 说明               | 请求体                                            | 响应格式 |
| ------ | -------------------------- | ---------------- | ---------------------------------------------- | ---- |
| `POST` | `/api/chat/generate`       | 单次文本生成           | `{"message": "文本"}`                            | JSON |
| `POST` | `/api/chat/batch-generate` | 批量文本生成           | `{"messages": {"key1": "文本1", "key2": "文本2"}}` | JSON |
| `GET`  | `/api/chat/health`         | 健康检查             | 无                                              | JSON |
| `GET`  | `/actuator/health`         | Spring Boot 健康检查 | 无                                              | JSON |
| `GET`  | `/actuator/info`           | 应用信息             | 无                                              | JSON |

### 7.2 请求/响应示例

#### 单次文本生成请求

```bash
curl -X POST "http://localhost:8080/api/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Spring AI"}'
```

#### 单次文本生成响应

```json
{
  "success": true,
  "message": "Spring AI 是 Spring 生态系统中的新一代 AI 应用框架，旨在简化 AI 应用的开发。它提供了统一的 API 来集成各种 AI 模型和服务，让开发者能够轻松构建智能应用。",
  "usage": {
    "promptTokens": 10,
    "generationTokens": 150,
    "totalTokens": 160
  },
  "model": "qwen3.5:7b",
  "timestamp": "2026-04-06T15:30:00.123456"
}
```

#### 批量文本生成请求

```bash
curl -X POST "http://localhost:8080/api/chat/batch-generate" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": {
      "question1": "什么是人工智能？",
      "question2": "机器学习有哪些类型？",
      "question3": "深度学习与机器学习有什么区别？"
    }
  }'
```

#### 批量文本生成响应

```json
{
  "success": true,
  "timestamp": "2026-04-06T15:31:00.123456",
  "responses": {
    "question1": "人工智能（AI）是计算机科学的一个分支...",
    "question2": "机器学习主要分为监督学习、无监督学习...",
    "question3": "深度学习是机器学习的一个子领域..."
  }
}
```

#### 健康检查响应

```json
{
  "status": "UP",
  "message": "Spring AI Demo is running!",
  "timestamp": "2026-04-06T15:32:00.123456",
  "version": "1.0.0"
}
```

#### 错误响应示例

```json
{
  "success": false,
  "error": "Connection refused: localhost/127.0.0.1:11434",
  "timestamp": "2026-04-06T15:33:00.123456"
}
```

### 7.3 API 文档访问

启动应用后，可以通过以下方式访问 API 文档：

- **Swagger UI**：如果集成了 SpringDoc，访问 `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**：访问 `http://localhost:8080/v3/api-docs`
- **Actuator 端点**：访问 `http://localhost:8080/actuator` 查看所有可用端点

***

## 八、部署方式

### 方式一：本地开发部署

#### 1. 环境准备

确保已安装以下组件：

- Java 17+ (`java -version`)
- Maven 3.6+ (`mvn -version`)
- Ollama（可选，用于本地模型运行）

#### 2. 下载和准备项目

```bash
# 克隆项目（如果尚未克隆）
git clone https://github.com/teaching-ai/spring-ai-examples.git
cd spring-ai-examples

# 进入项目目录
cd spring-ai-ollama-generation
```

#### 3. 启动 Ollama 服务（如使用本地模型）

```bash
# 启动 Ollama 服务（后台运行）
ollama serve &

# 下载模型（首次运行需要）
ollama pull qwen3.5:7b

# 验证 Ollama 服务
curl http://localhost:11434/api/tags
```

#### 4. 编译和运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用（开发模式）
mvn spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-generation-0.0.1-SNAPSHOT.jar
```

#### 5. 验证部署

```bash
# 健康检查
curl http://localhost:8080/api/chat/health

# 测试文本生成
curl -X POST "http://localhost:8080/api/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, World!"}'
```

### 方式二：Docker 容器部署

#### 1. 创建 Dockerfile

```dockerfile
# 使用 Eclipse Temurin 17 作为基础镜像
FROM eclipse-temurin:17-jre-alpine

# 安装必要的工具
RUN apk add --no-cache bash curl

# 设置工作目录
WORKDIR /app

# 复制应用 JAR 文件
COPY target/*.jar app.jar

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

#### 2. 构建 Docker 镜像

```bash
# 构建项目
mvn clean package -DskipTests

# 构建 Docker 镜像
docker build -t spring-ai-demo:latest .

# 查看镜像
docker images | grep spring-ai-demo
```

#### 3. 运行 Docker 容器

```bash
# 运行容器（连接外部 Ollama 服务）
docker run -d \
  -p 8080:8080 \
  -e SPRING_AI_OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  --name spring-ai-demo \
  spring-ai-demo:latest

# 或使用 Docker Compose（推荐）
```

#### 4. Docker Compose 部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  ollama:
    image: ollama/ollama:latest
    container_name: ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    restart: unless-stopped
    command: serve

  spring-ai-demo:
    image: spring-ai-demo:latest
    container_name: spring-ai-demo
    depends_on:
      - ollama
    ports:
      - "8080:8080"
    environment:
      - SPRING_AI_OLLAMA_BASE_URL=http://ollama:11434
      - SPRING_PROFILES_ACTIVE=docker
    restart: unless-stopped

volumes:
  ollama_data:
```

启动服务：

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f spring-ai-demo
```

### 方式三：Kubernetes 部署

#### 1. 创建 Kubernetes 部署文件

创建 `spring-ai-deployment.yaml`：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-ai-demo
  labels:
    app: spring-ai-demo
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-ai-demo
  template:
    metadata:
      labels:
        app: spring-ai-demo
    spec:
      containers:
      - name: spring-ai-demo
        image: spring-ai-demo:latest
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_AI_OLLAMA_BASE_URL
          value: "http://ollama-service:11434"
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 20
---
apiVersion: v1
kind: Service
metadata:
  name: spring-ai-demo-service
spec:
  selector:
    app: spring-ai-demo
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### 2. 部署到 Kubernetes

```bash
# 应用部署
kubectl apply -f spring-ai-deployment.yaml

# 查看部署状态
kubectl get deployments
kubectl get pods
kubectl get services

# 查看日志
kubectl logs -f deployment/spring-ai-demo

# 访问服务
kubectl port-forward service/spring-ai-demo-service 8080:80
```

***

## 九、使用示例

### 9.1 cURL 调用示例

#### 基础调用

```bash
# 健康检查
curl http://localhost:8080/api/chat/health

# 简单文本生成
curl -X POST "http://localhost:8080/api/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{"message": "什么是 Spring Boot？"}'
```

#### 带自定义参数的调用

```bash
# 使用自定义温度参数
curl -X POST "http://localhost:8080/api/chat/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "写一首关于春天的诗",
    "temperature": 0.9,
    "max_tokens": 500
  }'
```

#### 批量处理

```bash
# 批量生成多个问题的答案
curl -X POST "http://localhost:8080/api/chat/batch-generate" \
  -H "Content-Type: application/json" \
  -d '{
    "messages": {
      "q1": "解释一下微服务架构",
      "q2": "什么是容器化技术",
      "q3": "Kubernetes 的主要组件有哪些"
    }
  }'
```

### 9.2 Java 客户端示例

#### 使用 RestTemplate

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

public class SpringAiJavaClient {
    
    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String generateText(String message) {
        String url = BASE_URL + "/api/chat/generate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", message);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        
        if (response.getBody() != null && (Boolean) response.getBody().get("success")) {
            return (String) response.getBody().get("message");
        } else {
            throw new RuntimeException("AI 调用失败: " + response.getBody());
        }
    }
    
    public static void main(String[] args) {
        SpringAiJavaClient client = new SpringAiJavaClient();
        
        // 测试调用
        String response = client.generateText("请用 Java 写一个 Hello World 程序");
        System.out.println("AI 响应: " + response);
    }
}
```

#### 使用 WebClient（响应式）

```java
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

public class SpringAiWebClient {
    
    private final WebClient webClient;
    
    public SpringAiWebClient() {
        this.webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    public Mono<String> generateTextAsync(String message) {
        return webClient.post()
            .uri("/api/chat/generate")
            .bodyValue(Map.of("message", message))
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> (String) response.get("message"));
    }
}
```

### 9.3 Python 客户端示例

```python
import requests
import json
from typing import Optional, Dict, List

class SpringAiPythonClient:
    def __init__(self, base_url: str = "http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
    
    def health_check(self) -> Dict:
        """健康检查"""
        response = self.session.get(f"{self.base_url}/api/chat/health")
        response.raise_for_status()
        return response.json()
    
    def generate_text(self, message: str, **kwargs) -> Dict:
        """文本生成"""
        url = f"{self.base_url}/api/chat/generate"
        
        payload = {"message": message}
        if kwargs:
            payload.update(kwargs)
        
        response = self.session.post(url, json=payload)
        response.raise_for_status()
        return response.json()
    
    def batch_generate(self, messages: Dict[str, str]) -> Dict:
        """批量生成"""
        url = f"{self.base_url}/api/chat/batch-generate"
        payload = {"messages": messages}
        
        response = self.session.post(url, json=payload)
        response.raise_for_status()
        return response.json()
    
    def interactive_chat(self, system_prompt: Optional[str] = None) -> None:
        """交互式聊天"""
        print("=== Spring AI 交互式聊天 ===")
        print("输入 'quit' 或 'exit' 退出")
        print("输入 'clear' 清空对话历史")
        print("=" * 30)
        
        history = []
        if system_prompt:
            history.append({"role": "system", "content": system_prompt})
            print(f"系统提示: {system_prompt}")
        
        while True:
            user_input = input("\n你: ").strip()
            
            if user_input.lower() in ['quit', 'exit', 'q']:
                print("再见！")
                break
            elif user_input.lower() == 'clear':
                history = []
                if system_prompt:
                    history.append({"role": "system", "content": system_prompt})
                print("对话历史已清空")
                continue
            
            try:
                response = self.generate_text(user_input)
                if response.get("success"):
                    ai_response = response["message"]
                    print(f"AI: {ai_response}")
                    history.append({"role": "user", "content": user_input})
                    history.append({"role": "assistant", "content": ai_response})
                else:
                    print(f"错误: {response.get('error', '未知错误')}")
            except Exception as e:
                print(f"请求失败: {e}")

# 使用示例
if __name__ == "__main__":
    client = SpringAiPythonClient()
    
    # 健康检查
    health = client.health_check()
    print(f"应用状态: {health['status']}")
    
    # 单次生成
    response = client.generate_text("什么是人工智能？")
    if response["success"]:
        print(f"AI回答: {response['message'][:100]}...")
    
    # 批量生成
    questions = {
        "q1": "解释一下 RESTful API",
        "q2": "什么是依赖注入",
        "q3": "微服务的优缺点有哪些"
    }
    batch_response = client.batch_generate(questions)
    print(f"批量生成完成，共 {len(batch_response['responses'])} 个回答")
    
    # 交互式聊天
    # client.interactive_chat("你是一个编程助手，用中文回答技术问题。")
```

### 9.4 JavaScript/Node.js 客户端示例

```javascript
const axios = require('axios');

class SpringAiJsClient {
    constructor(baseUrl = 'http://localhost:8080') {
        this.client = axios.create({
            baseURL: baseUrl,
            timeout: 30000,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    async healthCheck() {
        try {
            const response = await this.client.get('/api/chat/health');
            return response.data;
        } catch (error) {
            console.error('健康检查失败:', error.message);
            throw error;
        }
    }

    async generateText(message, options = {}) {
        try {
            const payload = { message, ...options };
            const response = await this.client.post('/api/chat/generate', payload);
            return response.data;
        } catch (error) {
            console.error('文本生成失败:', error.message);
            throw error;
        }
    }

    async batchGenerate(messages) {
        try {
            const payload = { messages };
            const response = await this.client.post('/api/chat/batch-generate', payload);
            return response.data;
        } catch (error) {
            console.error('批量生成失败:', error.message);
            throw error;
        }
    }

    async streamGenerate(message, onChunk) {
        // 流式生成示例（需要服务端支持 Server-Sent Events）
        try {
            const eventSource = new EventSource(`${this.baseUrl}/api/chat/stream?message=${encodeURIComponent(message)}`);
            
            eventSource.onmessage = (event) => {
                const data = JSON.parse(event.data);
                onChunk(data.chunk);
            };
            
            eventSource.onerror = (error) => {
                console.error('流式生成错误:', error);
                eventSource.close();
            };
            
            return () => eventSource.close();
        } catch (error) {
            console.error('流式生成失败:', error.message);
            throw error;
        }
    }
}

// 使用示例
async function main() {
    const client = new SpringAiJsClient();
    
    try {
        // 健康检查
        const health = await client.healthCheck();
        console.log('应用状态:', health.status);
        
        // 单次生成
        const response = await client.generateText('用 JavaScript 实现一个斐波那契数列函数');
        if (response.success) {
            console.log('AI回答:', response.message.substring(0, 100) + '...');
        }
        
        // 批量生成
        const questions = {
            q1: '什么是闭包？',
            q2: '解释一下 Promise',
            q3: 'async/await 有什么优点',
            q4: '微服务的优缺点有哪些'
        };
        const batchResponse = await client.batchGenerate(questions);
        console.log(`批量生成完成，共 ${Object.keys(batchResponse.responses).length} 个回答`);
        
    } catch (error) {
        console.error('操作失败:', error.message);
    }
}

main();
```

## 十一、常见问题

### 11.1 环境配置问题

**Q1: 如何选择合适的 JDK 版本？**

推荐使用 JDK 17 或 21，这是 Spring Boot 3.x 的官方支持版本。JDK 17 是 LTS（长期支持）版本，稳定性更好；JDK 21 包含更多新特性。

**Q2: Maven 下载依赖很慢怎么办？**

配置阿里云 Maven 镜像可以显著提升下载速度。在 `~/.m2/settings.xml` 中添加：

```xml
<mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

**Q3: IDEA 无法识别 Maven 项目？**

- 确保已安装 Maven 插件
- 右键点击 `pom.xml` → "Add as Maven Project"
- 执行 "File" → "Invalidate Caches" → "Invalidate and Restart"

### 11.2 Ollama 相关问题

**Q1: Ollama 启动失败怎么办？**

- 检查端口 11434 是否被占用：`lsof -i :11434`
- 查看日志：Linux/macOS 查看 `~/.ollama/logs/server.log`，Windows 查看事件查看器
- 尝试重启 Ollama 服务

**Q2: 模型下载速度很慢？**

- 配置国内镜像源
- 使用代理：设置 `HTTP_PROXY` 和 `HTTPS_PROXY` 环境变量
- 手动下载模型文件后导入

**Q3: 如何查看已下载的模型？**

```bash
ollama list
```

### 11.3 Spring AI 运行问题

**Q1: 应用启动失败，提示连接 Ollama 失败？**

- 确认 Ollama 服务正在运行：`curl http://localhost:11434/api/tags`
- 检查 `application.yml` 中的 Ollama 地址配置
- 检查防火墙设置

**Q2: 提示 "No qualifying bean of type 'ChatModel'"？**

- 确认已添加 `spring-ai-ollama-spring-boot-starter` 依赖
- 检查 `spring.ai.ollama.chat.enabled` 是否为 true（默认为 true）
- 查看启动日志中的自动配置信息

**Q3: 生成的回答总是被截断？**

这通常是因为模型的上下文窗口限制。可以尝试：
- 使用更大上下文窗口的模型（如 llama3.1:8b）
- 缩短输入提示词
- 在 `ChatOptions` 中调整 `maxTokens` 参数

### 11.4 性能优化建议

**Q1: 如何提升响应速度？**

- 使用量化模型（如 q4_0、q8_0 版本）
- 确保有足够的 GPU 显存（使用 NVIDIA GPU 配合 CUDA）
- 调整模型参数：降低 `maxTokens`，使用更快的采样策略

**Q2: 如何支持并发请求？**

Spring Boot 默认使用线程池处理请求，可以通过以下配置调整：

```yaml
server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
```

同时建议在 Ollama 中启用并行处理：
```bash
OLLAMA_NUM_PARALLEL=4 ollama serve
```

## 十二、许可证

本项目采用 **Apache License 2.0** 许可证。

### Apache License 2.0 主要条款

- ✅ **商业使用**：允许将软件用于商业目的
- ✅ **修改**：允许修改源代码
- ✅ **分发**：允许再分发原始或修改后的软件
- ✅ **专利使用**：提供专利授权
- ✅ **私人使用**：允许私人使用
- ⚠️ **需要声明**：分发时需要包含许可证和版权声明
- ⚠️ **需要说明修改**：如果修改了代码，需要说明修改内容
- ❌ **无商标授权**：不授权使用商标
- ❌ **无责任担保**：软件按"原样"提供，不提供任何担保

### 完整许可证文本

完整的 Apache License 2.0 许可证文本可在以下地址获取：
- [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0)

### 本项目的许可证使用

本快速开始项目中的所有代码示例均可自由使用、修改和分发，无需额外授权。如在您的项目中使用，请在适当位置保留版权声明。

## 十三、参考资源

### 13.1 官方文档

| 资源 | 链接 | 说明 |
|------|------|------|
| Spring AI 官方文档 | [https://docs.spring.io/spring-ai/](https://docs.spring.io/spring-ai/) | Spring AI 完整参考手册 |
| Spring Boot 官方文档 | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) | Spring Boot 官方文档 |
| Ollama 官方文档 | [https://github.com/ollama/ollama](https://github.com/ollama/ollama) | Ollama 使用指南 |
| Maven 官方文档 | [https://maven.apache.org/guides/](https://maven.apache.org/guides/) | Maven 学习资源 |

### 13.2 教程与指南

| 标题 | 链接 | 说明 |
|------|------|------|
| Spring AI 入门指南 | [Spring AI 官方博客](https://spring.io/blog) | Spring 官方博客文章 |
| Ollama 模型库 | [https://ollama.com/library](https://ollama.com/library) | 所有可用模型列表 |
| Spring AI GitHub | [https://github.com/spring-projects/spring-ai](https://github.com/spring-projects/spring-ai) | 源码和 Issue 跟踪 |

### 13.3 本系列文档

本文档是 Spring AI 入门实践系列的第 0 篇，后续还包括：

1. **Spring AI 集成 Ollama 聊天对话** - 更深入的聊天对话功能
2. **Spring AI 集成 Ollama 流式对话** - 流式响应实现
3. **Spring AI 集成 Ollama 多轮对话** - 上下文记忆功能
4. **Spring AI 集成 Ollama Prompt 模板** - 提示词工程
5. **Spring AI 集成 Ollama 结构化输出** - JSON 格式输出
6. **Spring AI 集成 Ollama Function Calling** - 函数调用
7. **Spring AI 集成 Ollama RAG 文档检索** - 检索增强生成
8. **Spring AI 集成 Ollama 图像识别** - 多模态功能
9. **Spring AI 集成 Ollama 图片生成** - AI 绘图
10. **Spring AI 集成 Ollama 语音识别** - 语音转文字
11. **Spring AI 集成 Ollama 语音合成** - 文字转语音
12. **Spring AI 集成 Ollama 向量数据库** - 向量存储和检索
13. **Spring AI 集成 Ollama Agent 智能体** - AI Agent 开发
14. **Spring AI 集成 Ollama 最佳实践** - 生产环境部署指南

### 13.4 社区资源

| 平台 | 链接 | 说明 |
|------|------|------|
| Stack Overflow | [spring-ai 标签](https://stackoverflow.com/questions/tagged/spring-ai) | 技术问答 |
| Reddit | [r/SpringAI](https://www.reddit.com/r/SpringAI/) | 社区讨论 |
| Discord | [Spring Discord](https://discord.gg/spring) | 实时聊天 |
| Gitter | [Spring AI Gitter](https://gitter.im/spring-projects/spring-ai) | 社区交流 |

## 十四、致谢

感谢以下开源项目和社区的贡献，使得 Spring AI 快速开始项目得以实现：

### 核心项目

- **Spring AI** - 感谢 Spring 团队开发和维护 Spring AI 框架，为 Java 开发者提供了强大的 AI 集成能力
- **Ollama** - 感谢 Ollama 团队提供简洁高效的本地大模型部署方案
- **Spring Boot** - 感谢 Spring Boot 团队提供优秀的应用开发框架

### 模型贡献者

感谢所有开源大模型的研究团队和贡献者，包括但不限于：
- Meta（Llama 系列）
- Mistral AI（Mistral 系列）
- Google（Gemma 系列）
- 所有其他开源模型的贡献者

### 社区贡献

- 感谢 Spring 社区的活跃开发者们提供的宝贵反馈和贡献
- 感谢所有在 GitHub 上提交 Issue 和 Pull Request 的贡献者
- 感谢技术博客作者们分享的 Spring AI 实践经验

### 读者

感谢您阅读本文档！如果您在学习过程中发现任何问题或有改进建议，欢迎通过以下方式反馈：

- 在项目仓库提交 Issue
- 参与社区讨论
- 分享您的使用经验

祝您在 Spring AI 的学习之旅中取得成功！

```

