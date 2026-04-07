# Spring AI 入门实践：Spring AI MCP WebFlux 服务器实现

## 概述

MCP（Model Context Protocol）服务器是 MCP 架构中的核心组件，负责提供资源、提示词和工具。本示例展示如何使用 Spring WebFlux 实现异步、响应式的 MCP 服务器。

## MCP 服务器职责

MCP 服务器主要负责：

1. **资源管理**：提供可访问的数据资源
2. **提示词服务**：提供预定义的提示词模板
3. **工具提供**：提供可调用的工具函数
4. **上下文传递**：向客户端传递模型上下文信息

## 准备工作

### 1. Ollama 安装与配置

首先，您需要在本地计算机上运行 Ollama：

1. 访问 [Ollama 官网](https://ollama.com/)
2. 下载并安装适合您操作系统的版本
3. 启动 Ollama 服务
4. 拉取所需的模型：

```bash
ollama pull qwen3.5
ollama pull llama3
```

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-ollama</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp-server-webflux</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3. 配置 MCP 服务器

在 `application.properties` 文件中配置 MCP 服务器：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3.5

# MCP 服务器配置
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.port=8081
spring.ai.mcp.server.path=/mcp
```

## 核心功能

### 1. MCP 服务器配置

配置 MCP 服务器：

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

### 2. 提供资源

定义 MCP 资源：

```java
import org.springframework.ai.mcp.model.Resource;
import org.springframework.ai.mcp.server.annotation.McpResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class McpResourceProvider {

    @McpResource
    @GetMapping("/mcp/resources/documents")
    public Flux<Resource> getDocuments() {
        return Flux.just(
                new Resource("doc1", "文档1", "这是文档1的内容"),
                new Resource("doc2", "文档2", "这是文档2的内容")
        );
    }

    @McpResource
    @GetMapping("/mcp/resources/data")
    public Flux<Resource> getData() {
        return Flux.just(
                new Resource("data1", "数据1", "{\"key\": \"value\"}")
        );
    }
}
```

### 3. 提供提示词

定义 MCP 提示词：

```java
import org.springframework.ai.mcp.model.Prompt;
import org.springframework.ai.mcp.server.annotation.McpPrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpPromptProvider {

    @McpPrompt
    @GetMapping("/mcp/prompts/greeting")
    public Mono<Prompt> getGreetingPrompt() {
        return Mono.just(new Prompt(
                "greeting",
                "你是一个友好的助手，请用温暖的语气问候用户。"
        ));
    }

    @McpPrompt
    @GetMapping("/mcp/prompts/code-review")
    public Mono<Prompt> getCodeReviewPrompt() {
        return Mono.just(new Prompt(
                "code-review",
                "你是一个代码审查专家，请仔细审查用户提供的代码并提出改进建议。"
        ));
    }
}
```

### 4. 提供工具

定义 MCP 工具：

```java
import org.springframework.ai.mcp.model.Tool;
import org.springframework.ai.mcp.model.ToolResult;
import org.springframework.ai.mcp.server.annotation.McpTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpToolProvider {

    @McpTool
    @PostMapping("/mcp/tools/calculator")
    public Mono<ToolResult> calculator(@RequestBody CalculatorRequest request) {
        double result = switch (request.getOperation()) {
            case "add" -> request.getA() + request.getB();
            case "subtract" -> request.getA() - request.getB();
            case "multiply" -> request.getA() * request.getB();
            case "divide" -> request.getA() / request.getB();
            default -> throw new IllegalArgumentException("Unknown operation");
        };
        return Mono.just(new ToolResult(String.valueOf(result)));
    }

    @McpTool
    @PostMapping("/mcp/tools/weather")
    public Mono<ToolResult> getWeather(@RequestBody WeatherRequest request) {
        String weather = "晴天，温度25°C";
        return Mono.just(new ToolResult(weather));
    }
}
```

### 5. 与 Ollama 集成

将 MCP 服务器与 Ollama 结合：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.model.Context;
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpOllamaController {

    private final ChatModel chatModel;
    private final McpServer mcpServer;

    @GetMapping("/mcp/context/enhanced")
    public Mono<String> getEnhancedContext() {
        return mcpServer.getResources()
                .collectList()
                .map(resources -> {
                    Context context = new Context(resources);
                    return context.toString();
                });
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-ollama-mcp-webflux-server/
├── src/main/java/com/github/teachingai/mcp/
│   ├── config/
│   │   └── McpServerConfig.java
│   ├── controller/
│   │   ├── McpResourceProvider.java
│   │   ├── McpPromptProvider.java
│   │   ├── McpToolProvider.java
│   │   └── McpOllamaController.java
│   └── SpringAiMcpServerApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-mcp-webflux-server
mvn spring-boot:run
```

3. MCP 服务器将在 `http://localhost:8081/mcp` 启动
4. 访问端点：
   - `GET /mcp/resources/documents` - 获取文档资源
   - `GET /mcp/prompts/greeting` - 获取问候提示词
   - `POST /mcp/tools/calculator` - 调用计算器工具

## 最佳实践

1. **资源组织**：合理组织和分类资源
2. **异步处理**：充分利用 WebFlux 的异步特性
3. **错误处理**：提供清晰的错误信息
4. **安全性**：实现适当的认证和授权
5. **性能优化**：使用缓存和异步处理提高性能

## 相关资源

- [MCP 官方文档](https://modelcontextprotocol.io/)
- [Spring AI MCP 文档](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webflux-server)

## 扩展阅读

- [Spring AI MCP WebFlux 客户端实现](49、Spring AI 入门实践：Spring AI MCP WebFlux 客户端实现.md)
- [Spring AI MCP WebMVC 客户端实现](51、Spring AI 入门实践：Spring AI MCP WebMVC 客户端实现.md)
- [Spring AI 模型上下文协议 (MCP)](7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP).md)