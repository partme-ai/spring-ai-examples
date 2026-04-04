# Spring AI 入门实践：Spring AI MCP WebFlux 客户端实现

## 概述

MCP（Model Context Protocol）是一个标准化的协议，用于在AI模型和外部系统之间传递上下文信息。本示例展示如何使用 Spring WebFlux 实现 MCP 客户端，与 MCP 服务器进行异步通信。

## MCP 协议简介

### 什么是 MCP？

MCP（Model Context Protocol）是一个开放协议，标准化了应用程序向 LLM 提供上下文的方式。可以将 MCP 想象为 AI 应用的 USB-C 端口——提供一种标准化的方式将 AI 模型连接到不同的数据源和工具。

### MCP 架构

MCP 遵循客户端-服务器架构：

- **MCP Client（客户端）**：应用程序（如 Claude Desktop、IDE 或 AI 工具），连接到服务器
- **MCP Server（服务器）**：提供资源、提示词和工具的服务程序
- **MCP Host（宿主）**：运行 MCP 客户端的应用程序环境

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
        <artifactId>spring-ai-mcp-client-webflux</artifactId>
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

### 3. 配置 MCP 客户端

在 `application.properties` 文件中配置 MCP 客户端：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3.5

# MCP 客户端配置
spring.ai.mcp.client.server-url=http://localhost:8081/mcp
spring.ai.mcp.client.enabled=true
```

## 核心功能

### 1. MCP 客户端配置

配置 MCP 客户端连接：

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class McpClientConfig {

    @Bean
    public McpClient mcpClient(WebClient.Builder webClientBuilder) {
        return McpClient.builder()
                .webClientBuilder(webClientBuilder)
                .serverUrl("http://localhost:8081/mcp")
                .build();
    }
}
```

### 2. 使用 MCP 资源

从 MCP 服务器获取资源：

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpResourceController {

    private final McpClient mcpClient;

    @GetMapping("/mcp/resources")
    public Mono<String> getResources() {
        return mcpClient.listResources()
                .map(resources -> resources.toString());
    }
}
```

### 3. 调用 MCP 工具

调用 MCP 服务器提供的工具：

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.model.ToolCall;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpToolController {

    private final McpClient mcpClient;

    @PostMapping("/mcp/tools/call")
    public Mono<String> callTool(@RequestBody ToolCallRequest request) {
        ToolCall toolCall = new ToolCall(request.getToolName(), request.getArguments());
        return mcpClient.callTool(toolCall)
                .map(result -> result.getContent());
    }
}
```

### 4. 与 Ollama 集成

将 MCP 与 Ollama 结合使用：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class McpChatController {

    private final ChatModel chatModel;
    private final McpClient mcpClient;

    @GetMapping("/mcp/chat")
    public Mono<String> chatWithMcp(@RequestParam String message) {
        // 1. 从 MCP 服务器获取上下文
        return mcpClient.listResources()
                .flatMap(resources -> {
                    // 2. 将上下文与用户消息结合
                    String context = resources.toString();
                    String prompt = String.format("上下文信息：%s\n\n用户问题：%s", context, message);
                    
                    // 3. 使用 Ollama 生成回复
                    return Mono.just(chatModel.call(prompt));
                });
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-ollama-mcp-webflux-client/
├── src/main/java/com/github/teachingai/mcp/
│   ├── config/
│   │   └── McpClientConfig.java
│   ├── controller/
│   │   ├── McpResourceController.java
│   │   ├── McpToolController.java
│   │   └── McpChatController.java
│   └── SpringAiMcpClientApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 MCP 服务器正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-mcp-webflux-client
mvn spring-boot:run
```

4. 访问 API 端点：
   - `GET /mcp/resources` - 获取 MCP 资源
   - `POST /mcp/tools/call` - 调用 MCP 工具
   - `GET /mcp/chat?message=你好` - 使用 MCP 上下文对话

## 最佳实践

1. **异步处理**：充分利用 WebFlux 的异步特性
2. **错误处理**：处理 MCP 服务器不可用的情况
3. **超时设置**：合理设置连接和请求超时
4. **重试机制**：实现自动重试机制
5. **资源管理**：合理管理 MCP 资源的生命周期

## 相关资源

- [MCP 官方文档](https://modelcontextprotocol.io/)
- [Spring AI MCP 文档](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webflux-client)

## 扩展阅读

- [Spring AI MCP WebFlux 服务器实现](50、Spring AI 入门实践：Spring AI MCP WebFlux 服务器实现.md)
- [Spring AI 模型上下文协议 (MCP)](7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP).md)
- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)