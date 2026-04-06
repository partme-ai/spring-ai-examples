# 7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP)

> 模型上下文协议（Model Context Protocol，MCP）是一种标准化的协议，用于在 AI 模型和外部系统之间传递上下文信息。Spring AI 提供了完整的 MCP 支持，包括服务器端和客户端实现，支持 Spring MVC 和 WebFlux 两种技术栈。

---

## 一、项目概述

> 模型上下文协议（Model Context Protocol，MCP）是一种标准化的协议，用于在 AI 模型和外部系统之间传递上下文信息。Spring AI 提供了完整的 MCP 支持，包括服务器端和客户端实现，支持 Spring MVC 和 WebFlux 两种技术栈。

### 1.1 代码地址

**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-mcp-webmvc-server
**本地路径**：spring-ai-ollama-mcp-webmvc-server/

### 1.2 核心功能

- ✅ **MCP 服务器**：提供工具和资源的标准化接口
- ✅ **MCP 客户端**：连接和调用 MCP 服务器
- ✅ **Spring MVC 支持**：同步阻塞式 Web 框架实现
- ✅ **WebFlux 支持**：响应式非阻塞式 Web 框架实现
- ✅ **工具注册**：声明式工具定义和注册
- ✅ **资源管理**：上下文资源的标准化访问

### 1.2 技术栈

| 技术栈 | 特点 | 适用场景 |
|--------|------|---------|
| Spring MVC | 同步阻塞、简单易用 | 传统 Web 应用 |
| WebFlux | 响应式非阻塞、高并发 | 高吞吐量系统 |

### 1.3 仓库示例模块

本仓库提供四个 MCP 相关模块：

| 模块 | 角色 | 技术栈 |
|------|------|--------|
| spring-ai-ollama-mcp-webmvc-server | MCP Server | Spring MVC |
| spring-ai-ollama-mcp-webmvc-client | MCP Client | Spring MVC |
| spring-ai-ollama-mcp-webflux-server | MCP Server | WebFlux |
| spring-ai-ollama-mcp-webflux-client | MCP Client | WebFlux |

---

## 二、MCP 协议简介

### 2.1 MCP 介绍

MCP（Model Context Protocol）是一个开放协议，标准化了应用程序向 LLM 提供上下文的方式。可以将 MCP 想象为 AI 应用的 USB-C 端口——提供一种标准化的方式将 AI 模型连接到不同的数据源和工具。

### 2.2 MCP 架构

| 组件 | 说明 |
|------|------|
| **MCP Server** | 提供工具和资源的服务端 |
| **MCP Client** | 调用 MCP 服务的客户端 |
| **Tools** | 可执行的功能单元 |
| **Resources** | 可访问的数据资源 |
| **Prompts** | 预定义的提示模板 |

### 2.3 核心特性

| 特性 | 说明 |
|------|------|
| **标准化协议** | 标准化的上下文传递方式 |
| **资源管理** | 提供可访问的数据资源 |
| **提示词服务** | 提供预定义的提示词模板 |
| **工具提供** | 提供可调用的工具函数 |
| **上下文传递** | 向客户端传递模型上下文信息 |
| **多传输协议** | 支持 SSE、WebSocket、HTTP 等 |
| **易于集成** | 易于集成到现有应用 |

---

## 三、应用案例

### 3.1 AI 插件生态系统（可扩展的工具市场）

MCP 协议为 AI 应用提供了一个标准化的插件生态：

- **工具市场**：开发者可以发布标准化的 MCP 工具，用户可以按需安装
- **跨平台兼容**：同一工具可在 Claude Desktop、IDE、AI 应用等多平台使用
- **动态加载**：运行时动态加载和卸载工具，无需重启应用
- **版本管理**：支持工具版本管理和向后兼容

**示例场景**：
- 开发者发布一个"数据库查询工具"的 MCP 服务器
- AI 应用通过 MCP 客户端连接并使用该工具
- 用户可以直接向 AI 提出"查询最近 7 天的订单"，AI 通过 MCP 调用工具执行查询

### 3.2 企业知识库集成（安全的数据访问）

MCP 为企业内部系统集成提供安全可控的方式：

- **统一数据访问层**：通过 MCP 服务器封装内部 API 和数据库
- **权限控制**：在 MCP 服务器层实现统一的权限和审计
- **数据脱敏**：在返回给 AI 之前自动处理敏感信息
- **访问日志**：记录所有 AI 对企业数据的访问行为

**示例场景**：
- 企业部署 MCP 服务器连接内部 CRM、ERP、文档系统
- AI 助手通过 MCP 访问企业数据，回答员工问题
- 所有访问都经过权限校验和审计日志记录

### 3.3 多模型协同（不同模型协作完成复杂任务）

MCP 支持多个 AI 模型协同工作：

- **模型专业化**：不同模型负责不同任务（代码生成、图像分析、数据分析等）
- **工具共享**：多个模型可以共享同一套 MCP 工具
- **结果聚合**：一个模型调用另一个模型提供的工具获取结果
- **工作流编排**：通过 MCP 工具编排复杂的 AI 工作流

**示例场景**：
- 用户要求"分析这张图片并生成报告"
- 视觉模型通过 MCP 调用图像识别工具
- 识别结果传递给文本生成模型
- 文本生成模型调用格式化工具生成最终报告

---

## 四、性能基准

> ⚠️ 注：以下性能数据仅供参考，实际性能因硬件和环境而异。建议参考官方 Benchmark：[Spring AI MCP Documentation](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html)

### 4.1 MCP 协议开销

MCP 协议采用 JSON-RPC 2.0 格式，通信开销主要包括：

| 指标 | 数值 | 说明 |
|------|------|------|
| **协议开销** | ~100-500 字节 | JSON-RPC 封装开销 |
| **序列化开销** | <1ms | JSON 序列化/反序列化 |
| **网络延迟** | 视传输方式而定 | 见下方对比 |
| **内存占用** | ~10-50MB | 基础客户端/服务器 |

### 4.2 传输方式性能对比

| 传输方式 | 延迟 | 吞吐量 | 适用场景 |
|---------|------|--------|---------|
| **WebSocket** | 极低（<5ms） | 高 | 实时交互、高频调用 |
| **SSE** | 低（<10ms） | 中等 | 单向数据推送、流式响应 |
| **HTTP** | 中等（10-50ms） | 低 | 低频调用、简单场景 |

**性能建议**：
- 高并发场景优先使用 WebSocket
- 简单工具调用可使用 HTTP
- 流式响应使用 SSE

### 4.3 并发连接数限制

| 技术栈 | 默认限制 | 可调整性 |
|--------|---------|---------|
| **Spring MVC** | 受线程池限制（默认 200） | 通过 `server.tomcat.threads.max` 调整 |
| **WebFlux** | 理论无限制（受系统资源限制） | 通过 `spring.webflux.session.max-sessions` 调整 |
| **MCP Server** | 建议单实例 <1000 并发 | 使用负载均衡扩展 |

**性能优化建议**：
- WebFlux 适合高并发场景（>1000 QPS）
- MVC 适合传统应用（<500 QPS）
- 使用连接池管理 MCP 连接
- 启用缓存减少重复调用

---

## 五、环境准备

### 5.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型，可选）

### 5.2 项目结构

```
spring-ai-ollama-mcp-webflux-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── tools/
│   │   │                   │   ├── FileTools.java
│   │   │                   │   ├── DateTimeTools.java
│   │   │                   │   └── CalculatorTools.java
│   │   │                   └── config/
│   │   │                       └── McpServerConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 5.3 核心类说明

| 类名 | 职责 |
|------|------|
| FileTools | 文件操作工具 |
| DateTimeTools | 日期时间工具 |
| CalculatorTools | 计算器工具 |
| McpServerConfig | MCP 服务器配置 |

---

## 六、MCP 服务器实现

### 6.1 Maven 依赖

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
    </parent>

    <groupId>com.github.partmeai</groupId>
    <artifactId>spring-ai-ollama-mcp-webflux-server</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <java.version>17</java.version>
        <spring-ai.version>1.1.4</spring-ai.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-webflux-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

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
</project>
```

### 6.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-ollama-mcp-webflux-server

  ai:
    mcp:
      server:
        name: partme-mcp-server
        version: 1.0.0
        description: PartMe MCP Server
      tools:
        scan-packages: com.github.partmeai.ollama.tools

server:
  port: 8080
```

### 6.3 日期时间工具

```java
package com.github.partmeai.ollama.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeTools {

    @Tool(description = "获取当前日期时间")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));
    }

    @Tool(description = "获取当前日期")
    public String getCurrentDate() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
    }
}
```

### 6.4 计算器工具

```java
package com.github.partmeai.ollama.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    public record CalculationRequest(String operation, double a, double b) {}
    public record CalculationResponse(double result) {}

    @Tool(description = "执行基本的数学运算：加、减、乘、除")
    public CalculationResponse calculate(CalculationRequest request) {
        double result = switch (request.operation()) {
            case "add", "加" -> request.a() + request.b();
            case "subtract", "减" -> request.a() - request.b();
            case "multiply", "乘" -> request.a() * request.b();
            case "divide", "除" -> {
                if (request.b() == 0) {
                    throw new IllegalArgumentException("除数不能为零");
                }
                yield request.a() / request.b();
            }
            default -> throw new IllegalArgumentException("未知的操作: " + request.operation());
        };
        return new CalculationResponse(result);
    }
}
```

### 6.5 主应用类

```java
package com.github.partmeai.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }
}
```

---

## 七、Java 客户端实现

### 7.1 Maven 依赖

```xml
<dependencies>
    <!-- Spring AI MCP Client -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-mcp-webmvc-client</artifactId>
    </dependency>

    <!-- WebFlux 客户端使用此依赖 -->
    <!--
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-mcp-webflux-client</artifactId>
    </dependency>
    -->

    <!-- Ollama 集成 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-ollama</artifactId>
    </dependency>
</dependencies>
```

### 7.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-mcp-client

  ai:
    # Ollama 配置
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: qwen2.5

    # MCP 客户端配置
    mcp:
      client:
        server-url: http://localhost:8080/mcp
        enabled: true
        # 可选：指定要连接的 MCP 服务器列表
        servers:
          - name: local-mcp-server
            url: http://localhost:8080/mcp
            timeout: 30000

server:
  port: 8081
```

### 7.3 MCP 客户端配置

```java
package com.github.partmeai.mcp.config;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

    @Bean
    public McpClient mcpClient() {
        return McpClient.builder()
                .serverUrl("http://localhost:8080/mcp")
                .timeout(30000)
                .build();
    }
}
```

### 7.4 工具调用控制器

```java
package com.github.partmeai.mcp.controller;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
public class McpToolController {

    private final McpClient mcpClient;

    public McpToolController(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    /**
     * 列出所有可用工具
     */
    @GetMapping("/tools")
    public Map<String, ToolCallback> listTools() {
        return mcpClient.getToolCallbacks();
    }

    /**
     * 调用指定工具
     */
    @PostMapping("/tools/{toolName}")
    public String callTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> arguments) {
        return mcpClient.invokeTool(toolName, arguments);
    }

    /**
     * 调用计算器工具示例
     */
    @PostMapping("/calculate")
    public Object calculate(
            @RequestParam String operation,
            @RequestParam double a,
            @RequestParam double b) {
        var arguments = Map.of(
                "operation", operation,
                "a", a,
                "b", b
        );
        return mcpClient.invokeTool("CalculatorTools_calculate", arguments);
    }
}
```

### 7.5 资源访问控制器

```java
package com.github.partmeai.mcp.controller;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mcp/resources")
public class McpResourceController {

    private final McpClient mcpClient;

    public McpResourceController(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    /**
     * 列出所有可用资源
     */
    @GetMapping
    public List<String> listResources() {
        return mcpClient.listResources();
    }

    /**
     * 读取指定资源内容
     */
    @GetMapping("/{resourceId}")
    public String readResource(@PathVariable String resourceId) {
        return mcpClient.readResource(resourceId);
    }
}
```

### 7.6 集成 Ollama 对话

```java
package com.github.partmeai.mcp.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 聊天接口（自动调用 MCP 工具）
     */
    @PostMapping
    public String chat(@RequestBody String message) {
        var prompt = new Prompt(new UserMessage(message));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

---

## 八、API 接口说明

### 8.1 MCP 协议接口

MCP 协议使用标准化的 JSON-RPC 2.0 接口，主要包括：

| 方法 | 说明 |
|------|------|
| initialize | 初始化连接 |
| tools/list | 列出可用工具 |
| tools/call | 调用工具 |
| resources/list | 列出可用资源 |
| resources/read | 读取资源内容 |
| prompts/list | 列出可用提示 |
| prompts/get | 获取提示内容 |

### 8.2 REST API 接口

客户端提供的 REST 接口：

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/mcp/tools | GET | 列出所有工具 |
| /api/mcp/tools/{name} | POST | 调用指定工具 |
| /api/mcp/resources | GET | 列出所有资源 |
| /api/mcp/resources/{id} | GET | 读取指定资源 |
| /api/chat | POST | 对话（自动调用工具） |

---

## 九、部署方式

### 9.1 本地运行

**启动服务器**：
```bash
cd spring-ai-ollama-mcp-webflux-server
mvn spring-boot:run
```

**启动客户端**：
```bash
cd spring-ai-ollama-mcp-webflux-client
mvn spring-boot:run
```

### 9.2 打包部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/spring-ai-ollama-mcp-webflux-server-1.0.0-SNAPSHOT.jar
```

### 9.3 Docker 部署

**Dockerfile**：
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**构建和运行**：
```bash
docker build -t mcp-server .
docker run -p 8080:8080 mcp-server
```

---

## 十、模块选择指南

根据项目技术栈选择对应的模块：

| 场景 | 推荐模块 |
|------|---------|
| 传统 Spring MVC 项目 | spring-ai-ollama-mcp-webmvc-* |
| 响应式 WebFlux 项目 | spring-ai-ollama-mcp-webflux-* |
| 仅需提供工具 | spring-ai-ollama-mcp-*-server |
| 仅需调用工具 | spring-ai-ollama-mcp-*-client |
| 高并发场景 | WebFlux 版本 |
| 简单易用 | MVC 版本 |

---

## 十一、配置要点

### 11.1 安全配置

- **API 密钥安全**：云厂商 Key 勿提交到版本库，使用环境变量
- **CORS 配置**：跨域访问需要配置 CORS
- **认证授权**：生产环境建议添加认证机制

### 11.2 性能优化

- **连接池**：配置合理的连接池大小
- **超时设置**：根据业务调整超时时间
- **缓存策略**：启用工具结果缓存
- **异步调用**：使用异步方式调用长时间运行的工具

### 11.3 工具扫描

确保 `spring.ai.mcp.tools.scan-packages` 配置正确包含了工具类所在的包路径。

---

## 十二、常见问题

### 12.1 模块选择问题

**Q: 应该选择 MVC 还是 WebFlux 版本？**

- 如果现有项目使用 Spring MVC，选择 MVC 版本
- 如果需要高并发和响应式特性，选择 WebFlux 版本
- 两者功能相同，主要差异在底层 Web 框架

**Q: Server 和 Client 模块有什么区别？**

- Server 模块：提供工具和资源供其他系统调用
- Client 模块：连接和调用其他 MCP 服务器
- 根据需求选择或同时使用

### 12.2 配置问题

**Q: 工具没有被自动注册怎么办？**

检查 `spring.ai.mcp.tools.scan-packages` 配置是否正确包含了工具类所在的包路径。

**Q: 版本兼容性问题？**

确保 MCP Starter 版本与 Spring AI 核心版本兼容，参考官方文档的版本兼容性矩阵。

**Q: 连接超时怎么办？**

- 检查服务器是否正常启动
- 确认端口号配置正确
- 增加超时时间配置
- 检查防火墙设置

### 12.3 性能问题

**Q: 如何提高并发性能？**

- 使用 WebFlux 版本
- 调整线程池大小
- 启用连接池
- 使用缓存减少重复调用

**Q: 如何降低延迟？**

- 使用 WebSocket 传输
- 优化工具实现
- 使用异步调用
- 减少网络跳数

---

## 十三、参考资源

### 13.1 官方文档

- [Spring AI MCP Overview](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html)
- [Getting Started with MCP](https://docs.spring.io/spring-ai/reference/guides/getting-started-mcp.html)
- [MCP 协议规范](https://modelcontextprotocol.io/)

### 13.2 示例模块

| 模块 | 说明 |
|------|------|
| spring-ai-ollama-mcp-webmvc-server | MVC 服务器实现 |
| spring-ai-ollama-mcp-webmvc-client | MVC 客户端实现 |
| spring-ai-ollama-mcp-webflux-server | WebFlux 服务器实现 |
| spring-ai-ollama-mcp-webflux-client | WebFlux 客户端实现 |

### 13.3 相关文档

- [Spring AI MCP WebMvc 服务器实现](./52、Spring AI 入门实践：Spring AI MCP WebMvc 服务器实现.md)
- [Spring AI MCP WebMvc 客户端实现](./51、Spring AI 入门实践：Spring AI MCP WebMvc 客户端实现.md)
- [Spring AI MCP WebFlux 服务器实现](./50、Spring AI 入门实践：Spring AI MCP WebFlux 服务器实现.md)
- [Spring AI MCP WebFlux 客户端实现](./49、Spring AI 入门实践：Spring AI MCP WebFlux 客户端实现.md)

---

## 十四、致谢

感谢以下项目和团队对 MCP 生态系统做出的贡献：

- **Anthropic 团队**：提出并维护 MCP 协议规范，为 AI 应用标准化提供了重要基础
- **Spring AI 团队**：提供完整的 Java/Spring 生态 MCP 实现，让 Java 开发者能够轻松使用 MCP
- **开源社区贡献者**：在 GitHub 上积极贡献代码、文档和示例，帮助 MCP 生态快速发展
- **Claude Desktop 团队**：作为 MCP 的首批实现者，验证了协议的实用性

特别感谢所有参与 Spring AI MCP 实现的开发者，他们的努力让 MCP 在 Java 生态中的使用变得简单高效。

---

## 十五、许可证

本项目采用 Apache License 2.0 许可证。
