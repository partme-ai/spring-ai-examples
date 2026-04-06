# 7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP)

## 一、项目概述

模型上下文协议（Model Context Protocol，MCP）是一种标准化的协议，用于在 AI 模型和外部系统之间传递上下文信息。Spring AI 提供了完整的 MCP 支持，包括服务器端和客户端实现，支持 Spring MVC 和 WebFlux 两种技术栈。

### 核心功能

- **MCP 服务器**：提供工具和资源的标准化接口
- **MCP 客户端**：连接和调用 MCP 服务器
- **Spring MVC 支持**：同步阻塞式 Web 框架实现
- **WebFlux 支持**：响应式非阻塞式 Web 框架实现
- **工具注册**：声明式工具定义和注册
- **资源管理**：上下文资源的标准化访问

### 适用场景

- AI 助手工具集成
- 多 Agent 协作系统
- 企业内部知识库访问
- 开发工具链自动化
- 跨系统上下文共享

## 二、MCP 简介

模型上下文协议（MCP）由 Anthropic 提出，是一种开放标准，用于 AI 模型与外部工具和资源之间的标准化通信。

### MCP 架构

| 组件 | 说明 |
|------|------|
| MCP Server | 提供工具和资源的服务端 |
| MCP Client | 调用 MCP 服务的客户端 |
| Tools | 可执行的功能单元 |
| Resources | 可访问的数据资源 |
| Prompts | 预定义的提示模板 |

### 技术栈选择

| 技术栈 | 特点 | 适用场景 |
|--------|------|---------|
| Spring MVC | 同步阻塞、简单易用 | 传统 Web 应用 |
| WebFlux | 响应式非阻塞、高并发 | 高吞吐量系统 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型，可选）

### 3.2 仓库示例模块

本仓库提供四个 MCP 相关模块：

| 模块 | 角色 | 技术栈 |
|------|------|--------|
| spring-ai-ollama-mcp-webmvc-server | MCP Server | Spring MVC |
| spring-ai-ollama-mcp-webmvc-client | MCP Client | Spring MVC |
| spring-ai-ollama-mcp-webflux-server | MCP Server | WebFlux |
| spring-ai-ollama-mcp-webflux-client | MCP Client | WebFlux |

## 四、项目结构

### 4.1 标准项目结构（WebFlux 服务器示例）

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

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| FileTools | 文件操作工具 |
| DateTimeTools | 日期时间工具 |
| CalculatorTools | 计算器工具 |
| McpServerConfig | MCP 服务器配置 |

## 五、核心配置

### 5.1 Maven 依赖（WebFlux 服务器）

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

### 5.2 应用配置

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

## 六、代码实现详解

### 6.1 日期时间工具

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

### 6.2 计算器工具

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

### 6.3 主应用类

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

## 七、API 接口说明

### 7.1 MCP 协议接口

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

### 7.2 使用方式

MCP 服务器通过标准输入输出（STDIO）或 SSE 协议与客户端通信，具体使用方式参考各模块的 Controller 配置。

## 八、部署方式

### 8.1 本地运行（WebFlux 服务器）

```bash
cd spring-ai-ollama-mcp-webflux-server
mvn spring-boot:run
```

### 8.2 启动顺序

1. 先启动 MCP Server 模块
2. 再启动 MCP Client 模块进行联调

### 8.3 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-mcp-webflux-server-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 模块选择指南

根据项目技术栈选择对应的模块：

| 场景 | 推荐模块 |
|------|---------|
| 传统 Spring MVC 项目 | spring-ai-ollama-mcp-webmvc-* |
| 响应式 WebFlux 项目 | spring-ai-ollama-mcp-webflux-* |
| 仅需提供工具 | spring-ai-ollama-mcp-*-server |
| 仅需调用工具 | spring-ai-ollama-mcp-*-client |

### 9.2 配置要点

1. **API 密钥安全**：云厂商 Key 勿提交到版本库，建议使用环境变量
2. **协议配置**：SSE / Streamable HTTP 等与 `spring.ai.mcp.*` 对齐官方当前版本说明
3. **工具扫描**：`spring.ai.mcp.tools.scan-packages` 配置工具包扫描路径

## 十、运行项目

### 10.1 运行服务器

```bash
cd spring-ai-ollama-mcp-webflux-server
mvn spring-boot:run
```

### 10.2 运行客户端

```bash
cd spring-ai-ollama-mcp-webflux-client
mvn spring-boot:run
```

## 十一、常见问题

### 11.1 模块选择问题

**Q: 应该选择 MVC 还是 WebFlux 版本？**

- 如果现有项目使用 Spring MVC，选择 MVC 版本
- 如果需要高并发和响应式特性，选择 WebFlux 版本
- 两者功能相同，主要差异在底层 Web 框架

**Q: Server 和 Client 模块有什么区别？**

- Server 模块：提供工具和资源供其他系统调用
- Client 模块：连接和调用其他 MCP 服务器
- 根据需求选择或同时使用

### 11.2 配置问题

**Q: 工具没有被自动注册怎么办？**

检查 `spring.ai.mcp.tools.scan-packages` 配置是否正确包含了工具类所在的包路径。

**Q: 版本兼容性问题？**

确保 MCP Starter 版本与 Spring AI 核心版本兼容，参考官方文档的版本兼容性矩阵。

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI MCP Overview：https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html
- Getting Started with MCP：https://docs.spring.io/spring-ai/reference/guides/getting-started-mcp.html
- MCP 协议规范：https://modelcontextprotocol.io/
- 示例模块：spring-ai-ollama-mcp-webmvc-server、spring-ai-ollama-mcp-webmvc-client、spring-ai-ollama-mcp-webflux-server、spring-ai-ollama-mcp-webflux-client

## 十四、致谢

感谢 Spring AI 团队和 Anthropic 团队提供的优秀框架和协议，让 AI 工具集成变得如此简单易用。
