# 7、Spring AI 入门实践：Spring AI 模型上下文协议 (MCP)

## 概述

模型上下文协议（Model Context Protocol，MCP）是一种标准化的协议，用于在 AI 模型和外部系统之间传递上下文信息。Spring AI 提供了对 MCP 的支持，使得开发者可以更方便地管理和传递上下文。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI API**（或其他兼容的 LLM API）

## 准备工作

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-mcp-spring-boot-starter</artifactId>
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

### 2. 配置 API 密钥

在 `application.properties` 中配置：

```properties
spring.ai.openai.api-key=your-api-key
spring.ai.openai.chat.options.model=gpt-4
```

## 基本使用

### 1. 创建 MCP 上下文

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class McpContextService {
    
    @Autowired
    private McpContextManager contextManager;
    
    public McpContext createContext(String contextId) {
        return contextManager.createContext(contextId);
    }
    
    public McpContext getContext(String contextId) {
        return contextManager.getContext(contextId);
    }
}
```

### 2. 添加上下文数据

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class McpDataService {
    
    @Autowired
    private McpContextManager contextManager;
    
    public void addDataToContext(String contextId, String key, Object value) {
        McpContext context = contextManager.getContext(contextId);
        context.addData(key, value);
    }
    
    public void addMultipleData(String contextId, Map<String, Object> data) {
        McpContext context = contextManager.getContext(contextId);
        context.addData(data);
    }
}
```

## 高级功能

### 1. 上下文持久化

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.ai.mcp.persistence.McpContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class McpPersistenceService {
    
    @Autowired
    private McpContextManager contextManager;
    
    @Autowired
    private McpContextRepository contextRepository;
    
    public void saveContext(String contextId) {
        McpContext context = contextManager.getContext(contextId);
        contextRepository.save(context);
    }
    
    public McpContext loadContext(String contextId) {
        return contextRepository.findById(contextId)
            .orElseGet(() -> contextManager.createContext(contextId));
    }
}
```

### 2. 上下文版本控制

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.ai.mcp.version.McpVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class McpVersionService {
    
    @Autowired
    private McpContextManager contextManager;
    
    @Autowired
    private McpVersionManager versionManager;
    
    public String createVersion(String contextId) {
        McpContext context = contextManager.getContext(contextId);
        return versionManager.createVersion(context);
    }
    
    public McpContext restoreVersion(String contextId, String versionId) {
        return versionManager.restoreVersion(contextId, versionId);
    }
}
```

### 3. 上下文共享

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.ai.mcp.share.McpContextSharer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class McpSharingService {
    
    @Autowired
    private McpContextManager contextManager;
    
    @Autowired
    private McpContextSharer contextSharer;
    
    public String shareContext(String contextId, List<String> userIds) {
        McpContext context = contextManager.getContext(contextId);
        return contextSharer.share(context, userIds);
    }
    
    public McpContext accessSharedContext(String shareToken, String userId) {
        return contextSharer.access(shareToken, userId);
    }
}
```

### 4. 上下文生命周期管理

```java
import org.springframework.ai.mcp.McpContext;
import org.springframework.ai.mcp.McpContextManager;
import org.springframework.ai.mcp.lifecycle.McpLifecycleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Service
public class McpLifecycleService {
    
    @Autowired
    private McpContextManager contextManager;
    
    @Autowired
    private McpLifecycleManager lifecycleManager;
    
    public void setContextTTL(String contextId, Duration ttl) {
        lifecycleManager.setTTL(contextId, ttl);
    }
    
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredContexts() {
        List<String> expiredContexts = lifecycleManager.getExpiredContexts();
        expiredContexts.forEach(contextId -> {
            contextManager.deleteContext(contextId);
        });
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.*;
import org.springframework.ai.mcp.persistence.McpContextRepository;
import org.springframework.ai.mcp.share.McpContextSharer;
import org.springframework.ai.mcp.version.McpVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class McpApplication {
    public static void main(String[] args) {
        SpringApplication.run(McpApplication.class, args);
    }
}

@Service
class McpService {
    
    @Autowired
    private McpContextManager contextManager;
    
    @Autowired
    private McpContextRepository contextRepository;
    
    @Autowired
    private McpVersionManager versionManager;
    
    @Autowired
    private McpContextSharer contextSharer;
    
    @Autowired
    private ChatClient chatClient;
    
    public String createAndUseContext(String contextId, String userMessage) {
        McpContext context = contextManager.createContext(contextId);
        
        context.addData("timestamp", new Date());
        context.addData("user_message", userMessage);
        
        String contextData = context.getData().entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n"));
        
        String systemPrompt = "你是一个智能助手。以下是当前上下文信息：\n" + contextData;
        
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(systemPrompt),
            new UserMessage(userMessage)
        ));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
    
    public String saveAndLoadContext(String contextId) {
        McpContext context = contextManager.getContext(contextId);
        contextRepository.save(context);
        
        McpContext loadedContext = contextRepository.findById(contextId).orElse(null);
        return loadedContext != null ? "Context loaded successfully" : "Context not found";
    }
    
    public String createVersion(String contextId) {
        McpContext context = contextManager.getContext(contextId);
        return versionManager.createVersion(context);
    }
    
    public String shareContext(String contextId, List<String> userIds) {
        McpContext context = contextManager.getContext(contextId);
        return contextSharer.share(context, userIds);
    }
}

@RestController
@RequestMapping("/api/mcp")
class McpController {
    
    @Autowired
    private McpService service;
    
    @PostMapping("/contexts/{contextId}")
    public Map<String, Object> createContextAndUse(@PathVariable String contextId, @RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response = service.createAndUseContext(contextId, userMessage);
        
        return Map.of(
            "contextId", contextId,
            "response", response
        );
    }
    
    @PostMapping("/contexts/{contextId}/save")
    public Map<String, String> saveContext(@PathVariable String contextId) {
        String result = service.saveAndLoadContext(contextId);
        return Map.of("result", result);
    }
    
    @PostMapping("/contexts/{contextId}/versions")
    public Map<String, String> createVersion(@PathVariable String contextId) {
        String versionId = service.createVersion(contextId);
        return Map.of("versionId", versionId);
    }
    
    @PostMapping("/contexts/{contextId}/share")
    public Map<String, String> shareContext(@PathVariable String contextId, @RequestBody Map<String, List<String>> request) {
        List<String> userIds = request.get("userIds");
        String shareToken = service.shareContext(contextId, userIds);
        return Map.of("shareToken", shareToken);
    }
}
```

## 测试方法

1. **启动应用**：运行 `McpApplication` 类
2. **创建并使用上下文**：
   ```bash
   curl -X POST http://localhost:8080/api/mcp/contexts/test-context \
     -H "Content-Type: application/json" \
     -d '{"message":"你好，请介绍一下 Spring AI"}'
   ```
3. **保存上下文**：
   ```bash
   curl -X POST http://localhost:8080/api/mcp/contexts/test-context/save
   ```
4. **创建版本**：
   ```bash
   curl -X POST http://localhost:8080/api/mcp/contexts/test-context/versions
   ```
5. **共享上下文**：
   ```bash
   curl -X POST http://localhost:8080/api/mcp/contexts/test-context/share \
     -H "Content-Type: application/json" \
     -d '{"userIds":["user1","user2"]}'
   ```

## 总结

Spring AI 的 MCP 功能提供了完整的上下文管理解决方案，包括上下文创建、数据管理、持久化、版本控制、共享和生命周期管理等。通过这些功能，可以构建更智能、更灵活的 AI 应用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [MCP 协议规范](https://modelcontextprotocol.io/)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关模型上下文协议（MCP）的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI MCP Overview](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html) - MCP 协议概述
- [Getting Started with MCP](https://docs.spring.io/spring-ai/reference/guides/getting-started-mcp.html) - MCP 入门指南

### 示例模块
本仓库提供四个 MCP 相关模块，覆盖不同的技术栈组合：

| 模块 | 角色 | 技术栈 |
|------|------|--------|
| **`spring-ai-ollama-mcp-webmvc-server`** | MCP Server | Spring MVC |
| **`spring-ai-ollama-mcp-webmvc-client`** | MCP Client | Spring MVC |
| **`spring-ai-ollama-mcp-webflux-server`** | MCP Server | WebFlux |
| **`spring-ai-ollama-mcp-webflux-client`** | MCP Client | WebFlux |

各模块 `pom.xml` 引入对应 **`spring-ai-starter-mcp-*`** Starter。

### 配置要点
1. **API 密钥安全**：云厂商 Key 勿提交到版本库，建议使用环境变量
2. **协议配置**：SSE / Streamable HTTP 等与 `spring.ai.mcp.*` 对齐官方当前版本说明
3. **模型配置**：`application.properties` 中配置智谱 / Ollama 等模型

### 运行与验证
```bash
# 分别进入子目录执行
cd spring-ai-ollama-mcp-webmvc-server
mvn spring-boot:run

# 默认端口通常为 8080（未显式配置时）
# 先启 Server 再启 Client 进行联调
```

### 注意事项
1. **模块选择**：根据项目技术栈选择对应的 Server/Client 模块
2. **启动顺序**：先启动 Server 模块，再启动 Client 模块进行联调
3. **端点确认**：具体端点以各模块 Controller 与 MCP 自动配置为准
4. **版本兼容**：确保 MCP Starter 版本与 Spring AI 核心版本兼容