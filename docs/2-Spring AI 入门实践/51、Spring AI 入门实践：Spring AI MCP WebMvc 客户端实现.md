# Spring AI 入门实践：Spring AI MCP WebMvc 客户端实现

> 基于 Spring AI 框架实现 MCP（Model Context Protocol）WebMvc 客户端，使用传统的同步编程模型连接 MCP 服务器，获取和使用上下文信息。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下实现 MCP WebMvc 客户端的示例，展示了如何在 Java/Spring Boot 应用中使用传统的同步编程模型（WebMvc）连接 MCP 服务器，获取资源、调用工具，并与 Ollama 本地模型集成，构建完整的 AI 应用。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Spring WebMVC | - | 传统 Web 框架 |
| Ollama | - | 本地大语言模型 |
| spring-ai-mcp-client-webmvc | - | Spring AI MCP WebMvc 客户端 |

### 1.3 核心功能

- ✅ 同步编程：使用 WebMvc 实现同步编程
- ✅ MCP 客户端：连接 MCP 服务器获取上下文
- ✅ 资源访问：从 MCP 服务器获取资源
- ✅ 工具调用：调用 MCP 服务器提供的工具
- ✅ Ollama 集成：与 Ollama 本地模型集成
- ✅ 简单易用：适合熟悉传统同步编程的开发者
- ✅ 易于调试：同步代码更易于理解和调试

---

## 二、MCP 协议简介

### 2.1 MCP 介绍

MCP（Model Context Protocol）是一个开放协议，标准化了应用程序向 LLM 提供上下文的方式。可以将 MCP 想象为 AI 应用的 USB-C 端口——提供一种标准化的方式将 AI 模型连接到不同的数据源和工具。

### 2.2 MCP 架构

MCP 遵循客户端-服务器架构：

| 组件 | 说明 |
|------|------|
| **MCP Client（客户端）** | 应用程序（如 Claude Desktop、IDE 或 AI 工具），连接到服务器 |
| **MCP Server（服务器）** | 提供资源、提示词和工具的服务程序 |
| **MCP Host（宿主）** | 运行 MCP 客户端的应用程序环境 |

### 2.3 核心特性

| 特性 | 说明 |
|------|------|
| **标准化协议** | 标准化的上下文传递方式 |
| **资源管理** | 提供可访问的数据资源 |
| **提示词服务** | 提供预定义的提示词模板 |
| **工具提供** | 提供可调用的工具函数 |
| **上下文传递** | 向客户端传递模型上下文信息 |
| **同步支持** | 支持同步编程模型 |
| **易于集成** | 易于集成到现有应用 |

---

## 三、项目结构

```
spring-ai-ollama-mcp-webmvc-client/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── mcp/
│   │   │                   ├── SpringAiMcpClientApplication.java
│   │   │                   ├── config/
│   │   │                   │   └── McpClientConfig.java
│   │   │                   ├── controller/
│   │   │                   │   ├── McpResourceController.java
│   │   │                   │   ├── McpToolController.java
│   │   │                   │   └── McpChatController.java
│   │   │                   └── dto/
│   │   │                       └── ToolCallRequest.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiMcpClientApplication.java` - Spring Boot 应用入口
- `McpClientConfig.java` - MCP 客户端配置
- `McpResourceController.java` - MCP 资源控制器
- `McpToolController.java` - MCP 工具控制器
- `McpChatController.java` - MCP 对话控制器
- `ToolCallRequest.java` - 工具调用请求 DTO
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-mcp-webmvc-client
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# MCP 客户端配置
spring.ai.mcp.client.server-url=http://localhost:8081/mcp
spring.ai.mcp.client.enabled=true
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

### 5.1 MCP 客户端配置

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class McpClientConfig {

    @Bean
    public McpClient mcpClient(RestTemplate restTemplate) {
        return McpClient.builder()
                .restTemplate(restTemplate)
                .serverUrl("http://localhost:8081/mcp")
                .build();
    }
}
```

### 5.2 工具调用请求 DTO

```java
public class ToolCallRequest {
    private String toolName;
    private Map<String, Object> arguments;

    public ToolCallRequest() {
    }

    public ToolCallRequest(String toolName, Map<String, Object> arguments) {
        this.toolName = toolName;
        this.arguments = arguments;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}
```

### 5.3 MCP 资源控制器

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class McpResourceController {

    private final McpClient mcpClient;

    public McpResourceController(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    @GetMapping("/mcp/resources")
    public String getResources() {
        List<Object> resources = mcpClient.listResources();
        return resources.toString();
    }
}
```

### 5.4 MCP 工具控制器

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.model.ToolCall;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpToolController {

    private final McpClient mcpClient;

    public McpToolController(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    @PostMapping("/mcp/tools/call")
    public String callTool(@RequestBody ToolCallRequest request) {
        ToolCall toolCall = new ToolCall(request.getToolName(), request.getArguments());
        String result = mcpClient.callTool(toolCall).getContent();
        return result;
    }
}
```

### 5.5 MCP 对话控制器

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class McpChatController {

    private final ChatModel chatModel;
    private final McpClient mcpClient;

    public McpChatController(ChatModel chatModel, McpClient mcpClient) {
        this.chatModel = chatModel;
        this.mcpClient = mcpClient;
    }

    @GetMapping("/mcp/chat")
    public String chatWithMcp(@RequestParam String message) {
        // 从 MCP 服务器获取上下文
        List<Object> resources = mcpClient.listResources();
        
        // 将上下文与用户消息结合
        String context = resources.toString();
        String prompt = String.format("上下文信息：%s\n\n用户问题：%s", context, message);
        
        // 使用 Ollama 生成回复
        return chatModel.call(prompt);
    }
}
```

---

## 六、API 接口说明

### 6.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/mcp/resources` | 获取 MCP 资源 |
| `POST` | `/mcp/tools/call` | 调用 MCP 工具 |
| `GET` | `/mcp/chat` | 使用 MCP 上下文对话 |

### 6.2 请求/响应示例

#### 获取 MCP 资源

**请求：**
```
GET /mcp/resources
```

**响应：**
```
Content-Type: text/plain

[资源列表]
```

#### 调用 MCP 工具

**请求：**
```
POST /mcp/tools/call
Content-Type: application/json

{
  "toolName": "calculator",
  "arguments": {
    "operation": "add",
    "a": 1,
    "b": 2
  }
}
```

**响应：**
```
Content-Type: text/plain

3
```

#### 使用 MCP 上下文对话

**请求：**
```
GET /mcp/chat?message=你好
```

**响应：**
```
Content-Type: text/plain

你好！我很高兴为您服务。有什么我可以帮助您的吗？
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

#### 3. 启动 MCP 服务器

确保 MCP 服务器正在运行，默认地址为：http://localhost:8081/mcp

---

## 八、使用示例

### 8.1 cURL 调用

#### 获取 MCP 资源

```bash
curl "http://localhost:8080/mcp/resources"
```

#### 调用 MCP 工具

```bash
curl -X POST "http://localhost:8080/mcp/tools/call" \
  -H "Content-Type: application/json" \
  -d '{
    "toolName": "calculator",
    "arguments": {
      "operation": "add",
      "a": 1,
      "b": 2
    }
  }'
```

#### 使用 MCP 上下文对话

```bash
curl "http://localhost:8080/mcp/chat?message=你好"
```

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-mcp-webmvc-client
mvn spring-boot:run
```

---

## 十、常见问题

### Q1: 如何处理 MCP 服务器不可用的情况？

需要实现完善的错误处理机制，包括连接失败、超时等情况的处理，提供友好的错误信息。

### Q2: 如何在 WebMvc 和 WebFlux 之间选择？

如果您熟悉传统的同步编程模型，WebMvc 更容易理解和调试。如果需要处理高并发场景，WebFlux 更合适。可以根据团队的经验和具体需求进行选择。

### Q3: 如何优化 MCP 客户端的性能？

可以使用缓存、连接池等技术提高性能，实现适当的错误处理和重试机制。

---

## 十一、许可证

- **MCP**：MIT 许可证
- **Spring AI**：Apache 2.0
- **Ollama**：MIT
- **本项目**：Apache 2.0

MCP 采用 MIT 开源许可证。

---

## 参考资源

- **MCP 官方文档**：https://modelcontextprotocol.io/
- **Spring AI MCP 文档**：https://docs.spring.io/spring-ai/reference/api/mcp.html
- **Spring WebMVC 文档**：https://docs.spring.io/spring-framework/reference/web/webmvc.html
- **Spring AI GitHub**：https://github.com/spring-projects/spring-ai
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webmvc-client

---

## 致谢

- **感谢 MCP 社区** 提供标准化的上下文传递协议
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
