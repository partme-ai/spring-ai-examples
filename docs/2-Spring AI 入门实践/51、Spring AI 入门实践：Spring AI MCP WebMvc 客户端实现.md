# Spring AI 入门实践：Spring AI MCP WebMvc 客户端实现

## 概述

模型上下文协议（Model Context Protocol，MCP）是一种标准化的协议，用于在 AI 模型和外部系统之间传递上下文信息。Spring AI 提供了对 MCP 的支持，WebMvc 客户端实现允许基于传统 Spring MVC 的应用连接到 MCP 服务器，获取和使用上下文信息。

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
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp-client-webmvc-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
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

在 `application.properties` 文件中配置 MCP 客户端相关设置：

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=qwen3.5

# MCP 客户端配置
spring.ai.mcp.client.server-url=http://localhost:8081/mcp
spring.ai.mcp.client.enabled=true
```

## 核心功能

### 1. MCP 客户端基础

使用 MCP 客户端获取上下文：

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpClientController {

    private final McpClient mcpClient;

    @Autowired
    public McpClientController(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    @GetMapping("/mcp/context")
    public String getContext(@RequestParam String query) {
        return mcpClient.getContext(query);
    }
}
```

### 2. 与 Ollama 集成

将 MCP 上下文与 Ollama 对话结合：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpChatController {

    private final ChatModel chatModel;
    private final McpClient mcpClient;

    @GetMapping("/mcp/chat")
    public String chatWithContext(@RequestParam String message) {
        // 1. 从 MCP 服务器获取上下文
        String context = mcpClient.getContext(message);
        
        // 2. 构建带上下文的提示
        String prompt = String.format("上下文信息：%s\n\n用户问题：%s", context, message);
        
        // 3. 使用 Ollama 生成回复
        return chatModel.call(prompt);
    }
}
```

### 3. 工具调用

通过 MCP 调用远程工具：

```java
import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class McpToolController {

    private final McpClient mcpClient;

    @PostMapping("/mcp/tool")
    public Object callTool(@RequestBody Map<String, Object> params) {
        return mcpClient.callTool(params);
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-ollama-mcp-webmvc-client/
├── src/main/java/com/github/teachingai/mcp/
│   ├── controller/
│   │   ├── McpClientController.java
│   │   ├── McpChatController.java
│   │   └── McpToolController.java
│   └── SpringAiMcpClientApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 确保 MCP 服务器正在运行
3. 启动应用：

```bash
cd spring-ai-ollama-mcp-webmvc-client
mvn spring-boot:run
```

4. 访问 API 端点：
   - `GET /mcp/context?query=查询内容` - 获取上下文
   - `GET /mcp/chat?message=问题` - 带上下文的对话
   - `POST /mcp/tool` - 调用远程工具

## 最佳实践

1. **上下文缓存**：缓存常用的上下文信息
2. **错误处理**：处理 MCP 服务器不可用的情况
3. **超时设置**：合理设置连接和读取超时
4. **安全认证**：配置 MCP 服务器的认证信息
5. **性能监控**：监控 MCP 调用的性能指标

## 相关资源

- [MCP 协议规范](https://modelcontextprotocol.io/)
- [Spring AI MCP 文档](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webmvc-client)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI MCP WebFlux 客户端实现](49、Spring AI 入门实践：Spring AI MCP WebFlux 客户端实现.md)
- [Spring AI 模型上下文协议 (MCP)](7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP).md)
- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)