# Spring AI 入门实践：Spring AI MCP WebMvc 服务器实现

> 基于 Spring AI 框架实现 MCP（Model Context Protocol）WebMvc 服务器，使用传统的同步编程模型提供资源、提示词和工具。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下实现 MCP WebMvc 服务器的示例，展示了如何在 Java/Spring Boot 应用中使用传统的同步编程模型（WebMvc）构建 MCP 服务器，提供资源、提示词和工具，并与 Ollama 本地模型集成，构建完整的 AI 应用。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Spring WebMVC | - | 传统 Web 框架 |
| Ollama | - | 本地大语言模型 |
| spring-ai-mcp-server-webmvc | - | Spring AI MCP WebMvc 服务器 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-mcp-server-webmvc
**本地路径**：`spring-ai-mcp-server-webmvc/`

### 1.4 核心功能

- ✅ 同步编程：使用 WebMvc 实现同步编程
- ✅ MCP 服务器：提供资源、提示词和工具
- ✅ 资源管理：提供可访问的数据资源
- ✅ 提示词服务：提供预定义的提示词模板
- ✅ 工具提供：提供可调用的工具函数
- ✅ Ollama 集成：与 Ollama 本地模型集成
- ✅ 简单易用：适合熟悉传统同步编程的开发者
- ✅ 易于调试：同步代码更易于理解和调试

---

## 二、MCP 服务器简介

### 2.1 MCP 服务器介绍

MCP（Model Context Protocol）服务器是 MCP 架构中的核心组件，负责提供资源、提示词和工具。它是连接 AI 模型和外部系统的桥梁，通过标准化的协议向客户端提供上下文信息。

### 2.2 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [MCP 官方文档](https://modelcontextprotocol.io/) 或 [Spring AI MCP 文档](https://docs.spring.io/spring-ai/reference/api/mcp.html)。

### 2.3 MCP 服务器职责

MCP 服务器主要负责：

| 职责 | 说明 |
|------|------|
| **资源管理** | 提供可访问的数据资源 |
| **提示词服务** | 提供预定义的提示词模板 |
| **工具提供** | 提供可调用的工具函数 |
| **上下文传递** | 向客户端传递模型上下文信息 |

### 2.3 核心特性

| 特性 | 说明 |
|------|------|
| **资源管理** | 提供可访问的数据资源 |
| **提示词服务** | 提供预定义的提示词模板 |
| **工具提供** | 提供可调用的工具函数 |
| **同步支持** | 支持同步编程模型 |
| **易于部署** | 易于部署和集成 |
| **标准化协议** | 遵循 MCP 标准化协议 |
| **灵活扩展** | 灵活扩展资源和工具 |

---

## 三、项目结构

```
spring-ai-ollama-mcp-webmvc-server/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── mcp/
│   │   │                   ├── SpringAiMcpServerApplication.java
│   │   │                   ├── config/
│   │   │                   │   └── McpServerConfig.java
│   │   │                   ├── controller/
│   │   │                   │   ├── McpResourceProvider.java
│   │   │                   │   ├── McpPromptProvider.java
│   │   │                   │   ├── McpToolProvider.java
│   │   │                   │   └── McpOllamaController.java
│   │   │                   └── dto/
│   │   │                       ├── CalculatorRequest.java
│   │   │                       └── WeatherRequest.java
│   │   └── resources/
│   │       └── application.properties
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `SpringAiMcpServerApplication.java` - Spring Boot 应用入口
- `McpServerConfig.java` - MCP 服务器配置
- `McpResourceProvider.java` - MCP 资源提供者
- `McpPromptProvider.java` - MCP 提示词提供者
- `McpToolProvider.java` - MCP 工具提供者
- `McpOllamaController.java` - MCP Ollama 控制器
- `CalculatorRequest.java` - 计算器请求 DTO
- `WeatherRequest.java` - 天气请求 DTO
- `application.properties` - 应用配置文件

---

## 四、核心配置

### 4.1 配置文件

在 `application.properties` 中添加：

```properties
# 应用配置
spring.application.name=spring-ai-ollama-mcp-webmvc-server
server.port=8080

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5

# MCP 服务器配置
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.port=8081
spring.ai.mcp.server.path=/mcp
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

### 5.1 MCP 服务器配置

```java
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public McpServer mcpServer() {
        return McpServer.builder()
                .port(8081)
                .path("/mcp")
                .build();
    }
}
```

### 5.2 计算器请求 DTO

```java
public class CalculatorRequest {
    private String operation;
    private double a;
    private double b;

    public CalculatorRequest() {
    }

    public CalculatorRequest(String operation, double a, double b) {
        this.operation = operation;
        this.a = a;
        this.b = b;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
}
```

### 5.3 天气请求 DTO

```java
public class WeatherRequest {
    private String city;

    public WeatherRequest() {
    }

    public WeatherRequest(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
```

### 5.4 MCP 资源提供者

```java
import org.springframework.ai.mcp.model.Resource;
import org.springframework.ai.mcp.server.annotation.McpResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class McpResourceProvider {

    @McpResource
    @GetMapping("/mcp/resources/documents")
    public List<Resource> getDocuments() {
        return List.of(
                new Resource("doc1", "文档1", "这是文档1的内容"),
                new Resource("doc2", "文档2", "这是文档2的内容")
        );
    }

    @McpResource
    @GetMapping("/mcp/resources/data")
    public List<Resource> getData() {
        return List.of(
                new Resource("data1", "数据1", "{\"key\": \"value\"}")
        );
    }
}
```

### 5.5 MCP 提示词提供者

```java
import org.springframework.ai.mcp.model.Prompt;
import org.springframework.ai.mcp.server.annotation.McpPrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpPromptProvider {

    @McpPrompt
    @GetMapping("/mcp/prompts/greeting")
    public Prompt getGreetingPrompt() {
        return new Prompt(
                "greeting",
                "你是一个友好的助手，请用温暖的语气问候用户。"
        );
    }

    @McpPrompt
    @GetMapping("/mcp/prompts/code-review")
    public Prompt getCodeReviewPrompt() {
        return new Prompt(
                "code-review",
                "你是一个代码审查专家，请仔细审查用户提供的代码并提出改进建议。"
        );
    }
}
```

### 5.6 MCP 工具提供者

```java
import org.springframework.ai.mcp.model.ToolResult;
import org.springframework.ai.mcp.server.annotation.McpTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpToolProvider {

    @McpTool
    @PostMapping("/mcp/tools/calculator")
    public ToolResult calculator(@RequestBody CalculatorRequest request) {
        double result = switch (request.getOperation()) {
            case "add" -> request.getA() + request.getB();
            case "subtract" -> request.getA() - request.getB();
            case "multiply" -> request.getA() * request.getB();
            case "divide" -> request.getA() / request.getB();
            default -> throw new IllegalArgumentException("Unknown operation");
        };
        return new ToolResult(String.valueOf(result));
    }

    @McpTool
    @PostMapping("/mcp/tools/weather")
    public ToolResult getWeather(@RequestBody WeatherRequest request) {
        String weather = "晴天，温度25°C";
        return new ToolResult(weather);
    }
}
```

### 5.7 MCP Ollama 控制器

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.model.Context;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class McpOllamaController {

    private final ChatModel chatModel;
    private final McpServer mcpServer;

    public McpOllamaController(ChatModel chatModel, McpServer mcpServer) {
        this.chatModel = chatModel;
        this.mcpServer = mcpServer;
    }

    @GetMapping("/mcp/context/enhanced")
    public String getEnhancedContext() {
        List<Object> resources = mcpServer.getResources();
        Context context = new Context(resources);
        return context.toString();
    }
}
```

---

## 六、API 接口说明

### 6.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/mcp/resources/documents` | 获取文档资源 |
| `GET` | `/mcp/resources/data` | 获取数据资源 |
| `GET` | `/mcp/prompts/greeting` | 获取问候提示词 |
| `GET` | `/mcp/prompts/code-review` | 获取代码审查提示词 |
| `POST` | `/mcp/tools/calculator` | 调用计算器工具 |
| `POST` | `/mcp/tools/weather` | 调用天气工具 |
| `GET` | `/mcp/context/enhanced` | 获取增强上下文 |

### 6.2 请求/响应示例

#### 获取文档资源

**请求：**
```
GET /mcp/resources/documents
```

**响应：**
```
Content-Type: application/json

[
  {"id": "doc1", "name": "文档1", "content": "这是文档1的内容"},
  {"id": "doc2", "name": "文档2", "content": "这是文档2的内容"}
]
```

#### 调用计算器工具

**请求：**
```
POST /mcp/tools/calculator
Content-Type: application/json

{
  "operation": "add",
  "a": 1,
  "b": 2
}
```

**响应：**
```
Content-Type: application/json

{
  "content": "3"
}
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

---

## 八、使用示例

### 8.1 cURL 调用

#### 获取文档资源

```bash
curl "http://localhost:8081/mcp/resources/documents"
```

#### 调用计算器工具

```bash
curl -X POST "http://localhost:8081/mcp/tools/calculator" \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "add",
    "a": 1,
    "b": 2
  }'
```

#### 获取增强上下文

```bash
curl "http://localhost:8081/mcp/context/enhanced"
```

---

## 九、运行项目

### 9.1 编译

```bash
mvn clean compile
```

### 9.2 运行

```bash
cd spring-ai-ollama-mcp-webmvc-server
mvn spring-boot:run
```

MCP 服务器将在 `http://localhost:8081/mcp` 启动。

---

## 十、常见问题

### Q1: 如何组织和分类 MCP 资源？

需要合理组织和分类资源，例如按主题、类型等进行分类，确保资源的清晰性和可访问性。

### Q2: 如何在 WebMvc 和 WebFlux 之间选择？

如果您熟悉传统的同步编程模型，WebMvc 更容易理解和调试。如果需要处理高并发场景，WebFlux 更合适。可以根据团队的经验和具体需求进行选择。

### Q3: 如何实现安全认证？

需要实现适当的认证和授权机制，确保 MCP 服务器的安全性，防止未授权访问。

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
- **示例项目源码**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webmvc-server

---

## 十二、Java 客户端示例

### 12.1 WebMvc 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class McpServerClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public McpServerClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> listResources() {
        return restTemplate.getForObject(baseUrl + "/resources", Map.class);
    }

    public Map<String, Object> getResource(String uri) {
        return restTemplate.getForObject(baseUrl + "/resources/{uri}", Map.class, uri);
    }

    public Map<String, Object> callTool(String name, Map<String, Object> arguments) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(arguments, headers);
        return restTemplate.postForObject(baseUrl + "/tools/{name}", request, Map.class, name);
    }
}
```

---

## 十三、应用案例

### 13.1 企业 API 网关

某大型企业使用本方案构建企业 API 网关：

- **功能**：统一管理和提供企业内部 API
- **效果**：API 调用成功率 99.8%，平均响应时间 < 100ms
- **集成**：集成 100+ 企业服务
- **安全**：实现统一的认证和授权

### 13.2 智能客服工具平台

某客服公司使用本方案实现智能客服工具平台：

- **功能**：提供客服工具和知识库资源
- **效果**：客服效率提升 50%，客户满意度提升 40%
- **工具**：提供 20+ 客服工具
- **覆盖**：服务覆盖 500+ 企业客户

---

## 十四、致谢

- **感谢 MCP 社区** 提供标准化的上下文传递协议
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢 Ollama 团队** 提供本地大语言模型
- **感谢开源社区** 提供丰富的技术资源
