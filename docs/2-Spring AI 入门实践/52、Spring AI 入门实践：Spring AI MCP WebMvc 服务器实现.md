# Spring AI 入门实践：Spring AI MCP WebMvc 服务器实现

## 概述

模型上下文协议（Model Context Protocol，MCP）服务器实现允许您构建自己的 MCP 服务器，为 MCP 客户端提供上下文信息、工具和资源。WebMvc 服务器实现基于传统的 Spring MVC，适合构建 RESTful API 风格的 MCP 服务。

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
        <artifactId>spring-ai-mcp-server-webmvc-spring-boot-starter</artifactId>
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

### 3. 配置 MCP 服务器

在 `application.properties` 文件中配置 MCP 服务器相关设置：

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

### 1. MCP 服务器基础

创建 MCP 服务器端点：

```java
import org.springframework.ai.mcp.server.McpServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpServerController {

    private final McpServer mcpServer;

    @GetMapping("/mcp/context")
    public String provideContext(@RequestParam String query) {
        return mcpServer.provideContext(query);
    }

    @PostMapping("/mcp/tool")
    public Object executeTool(@RequestBody Map<String, Object> params) {
        return mcpServer.executeTool(params);
    }
}
```

### 2. 提供上下文信息

实现上下文提供者：

```java
import org.springframework.ai.mcp.server.ContextProvider;
import org.springframework.stereotype.Component;

@Component
public class CustomContextProvider implements ContextProvider {

    @Override
    public String provide(String query) {
        // 根据查询提供相关上下文
        // 可以从数据库、文件系统或其他数据源获取
        return "这是与查询相关的上下文信息...";
    }
}
```

### 3. 注册工具

注册 MCP 工具：

```java
import org.springframework.ai.mcp.server.Tool;
import org.springframework.ai.mcp.server.ToolRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

    @Bean
    public Tool weatherTool() {
        return Tool.builder()
                .name("getWeather")
                .description("获取指定城市的天气信息")
                .handler((params) -> {
                    String city = (String) params.get("city");
                    // 实现天气查询逻辑
                    return "城市 " + city + " 的天气信息...";
                })
                .build();
    }

    @Bean
    public ToolRegistry toolRegistry() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(weatherTool());
        return registry;
    }
}
```

### 4. 资源提供

提供 MCP 资源：

```java
import org.springframework.ai.mcp.server.Resource;
import org.springframework.ai.mcp.server.ResourceProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomResourceProvider implements ResourceProvider {

    @Override
    public List<Resource> list() {
        // 列出所有可用资源
        return List.of(
            new Resource("doc1", "文档1", "文档1的内容"),
            new Resource("doc2", "文档2", "文档2的内容")
        );
    }

    @Override
    public Resource get(String id) {
        // 根据ID获取资源
        return new Resource(id, "资源名称", "资源内容");
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-ollama-mcp-webmvc-server/
├── src/main/java/com/github/teachingai/mcp/
│   ├── controller/
│   │   └── McpServerController.java
│   ├── provider/
│   │   ├── CustomContextProvider.java
│   │   └── CustomResourceProvider.java
│   ├── config/
│   │   └── McpToolConfig.java
│   └── SpringAiMcpServerApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-mcp-webmvc-server
mvn spring-boot:run
```

3. MCP 服务器将在 `http://localhost:8081/mcp` 启动
4. 客户端可以通过以下端点访问：
   - `GET /mcp/context?query=查询内容` - 获取上下文
   - `POST /mcp/tool` - 调用工具
   - `GET /mcp/resources` - 列出资源
   - `GET /mcp/resources/{id}` - 获取资源

## 最佳实践

1. **上下文质量**：提供高质量、相关的上下文信息
2. **工具设计**：设计清晰、单一职责的工具
3. **错误处理**：实现完善的错误处理机制
4. **安全认证**：配置认证和授权机制
5. **性能优化**：缓存常用上下文和资源
6. **日志记录**：记录 MCP 调用日志便于调试

## 相关资源

- [MCP 协议规范](https://modelcontextprotocol.io/)
- [Spring AI MCP 文档](https://docs.spring.io/spring-ai/reference/api/mcp.html)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webmvc-server)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI MCP WebFlux 服务器实现](50、Spring AI 入门实践：Spring AI MCP WebFlux 服务器实现.md)
- [Spring AI MCP WebMvc 客户端实现](51、Spring AI 入门实践：Spring AI MCP WebMvc 客户端实现.md)
- [Spring AI 模型上下文协议 (MCP)](7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP).md)