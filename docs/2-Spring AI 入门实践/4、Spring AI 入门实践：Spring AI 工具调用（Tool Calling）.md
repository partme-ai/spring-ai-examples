# 4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）

## 概述

Spring AI 的工具调用（Tool Calling）功能允许大语言模型调用外部工具和函数，从而扩展模型的能力。通过工具调用，模型可以执行计算、查询数据库、调用 API 等操作。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI API**（或其他支持工具调用的 LLM API）

## 准备工作

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
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

### 1. 定义工具

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class CalculatorTool implements Function<CalculatorTool.Request, CalculatorTool.Response> {
    
    public record Request(String operation, double a, double b) {}
    public record Response(double result) {}
    
    @Override
    public Response apply(Request request) {
        return switch (request.operation) {
            case "add" -> new Response(request.a + request.b);
            case "subtract" -> new Response(request.a - request.b);
            case "multiply" -> new Response(request.a * request.b);
            case "divide" -> new Response(request.a / request.b);
            default -> throw new IllegalArgumentException("Unknown operation: " + request.operation);
        };
    }
    
    public String getName() {
        return "calculator";
    }
    
    public String getDescription() {
        return "执行基本的数学运算：加、减、乘、除";
    }
}
```

### 2. 使用工具

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolCallingService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private CalculatorTool calculatorTool;
    
    public String useTool(String userMessage) {
        FunctionCallbackWrapper<CalculatorTool.Request, CalculatorTool.Response> calculatorCallback = 
            FunctionCallbackWrapper.builder(calculatorTool)
                .withName(calculatorTool.getName())
                .withDescription(calculatorTool.getDescription())
                .build();
        
        return chatClient.prompt()
            .user(userMessage)
            .functions(calculatorCallback)
            .call()
            .content();
    }
}
```

## 高级功能

### 1. 多工具调用

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MultiToolService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private CalculatorTool calculatorTool;
    
    @Autowired
    private WeatherTool weatherTool;
    
    public String useMultipleTools(String userMessage) {
        List<FunctionCallbackWrapper<?, ?>> tools = List.of(
            FunctionCallbackWrapper.builder(calculatorTool)
                .withName(calculatorTool.getName())
                .withDescription(calculatorTool.getDescription())
                .build(),
            FunctionCallbackWrapper.builder(weatherTool)
                .withName(weatherTool.getName())
                .withDescription(weatherTool.getDescription())
                .build()
        );
        
        return chatClient.prompt()
            .user(userMessage)
            .functions(tools)
            .call()
            .content();
    }
}
```

### 2. 自定义工具

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class WeatherTool implements Function<WeatherTool.Request, WeatherTool.Response> {
    
    public record Request(String city) {}
    public record Response(String city, double temperature, String condition) {}
    
    @Override
    public Response apply(Request request) {
        return new Response(
            request.city,
            25.5,
            "晴天"
        );
    }
    
    public String getName() {
        return "weather";
    }
    
    public String getDescription() {
        return "获取指定城市的天气信息";
    }
}
```

### 3. 工具调用回调

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToolCallbackService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private CalculatorTool calculatorTool;
    
    public String useToolWithCallback(String userMessage) {
        FunctionCallbackWrapper<CalculatorTool.Request, CalculatorTool.Response> calculatorCallback = 
            FunctionCallbackWrapper.builder(calculatorTool)
                .withName(calculatorTool.getName())
                .withDescription(calculatorTool.getDescription())
                .withInputType(CalculatorTool.Request.class)
                .withOutputType(CalculatorTool.Response.class)
                .build();
        
        ChatResponse response = chatClient.prompt()
            .user(userMessage)
            .functions(calculatorCallback)
            .call()
            .chatResponse();
        
        return response.getResult().getOutput().getContent();
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.function.Function;

@SpringBootApplication
public class ToolCallingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToolCallingApplication.class, args);
    }
}

@Component
class CalculatorTool implements Function<CalculatorTool.Request, CalculatorTool.Response> {
    
    public record Request(String operation, double a, double b) {}
    public record Response(double result) {}
    
    @Override
    public Response apply(Request request) {
        return switch (request.operation) {
            case "add" -> new Response(request.a + request.b);
            case "subtract" -> new Response(request.a - request.b);
            case "multiply" -> new Response(request.a * request.b);
            case "divide" -> new Response(request.a / request.b);
            default -> throw new IllegalArgumentException("Unknown operation: " + request.operation);
        };
    }
    
    public String getName() {
        return "calculator";
    }
    
    public String getDescription() {
        return "执行基本的数学运算：加、减、乘、除";
    }
}

@Component
class WeatherTool implements Function<WeatherTool.Request, WeatherTool.Response> {
    
    public record Request(String city) {}
    public record Response(String city, double temperature, String condition) {}
    
    @Override
    public Response apply(Request request) {
        return new Response(
            request.city,
            25.5,
            "晴天"
        );
    }
    
    public String getName() {
        return "weather";
    }
    
    public String getDescription() {
        return "获取指定城市的天气信息";
    }
}

@Service
class ToolCallingService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private CalculatorTool calculatorTool;
    
    @Autowired
    private WeatherTool weatherTool;
    
    public String useCalculator(String userMessage) {
        FunctionCallbackWrapper<CalculatorTool.Request, CalculatorTool.Response> calculatorCallback = 
            FunctionCallbackWrapper.builder(calculatorTool)
                .withName(calculatorTool.getName())
                .withDescription(calculatorTool.getDescription())
                .build();
        
        return chatClient.prompt()
            .user(userMessage)
            .functions(calculatorCallback)
            .call()
            .content();
    }
    
    public String useMultipleTools(String userMessage) {
        List<FunctionCallbackWrapper<?, ?>> tools = List.of(
            FunctionCallbackWrapper.builder(calculatorTool)
                .withName(calculatorTool.getName())
                .withDescription(calculatorTool.getDescription())
                .build(),
            FunctionCallbackWrapper.builder(weatherTool)
                .withName(weatherTool.getName())
                .withDescription(weatherTool.getDescription())
                .build()
        );
        
        return chatClient.prompt()
            .user(userMessage)
            .functions(tools)
            .call()
            .content();
    }
}

@RestController
@RequestMapping("/api/tool-calling")
class ToolCallingController {
    
    @Autowired
    private ToolCallingService service;
    
    @PostMapping("/calculator")
    public String useCalculator(@RequestBody String userMessage) {
        return service.useCalculator(userMessage);
    }
    
    @PostMapping("/multi-tool")
    public String useMultipleTools(@RequestBody String userMessage) {
        return service.useMultipleTools(userMessage);
    }
}
```

## 测试方法

1. **启动应用**：运行 `ToolCallingApplication` 类
2. **使用计算器工具**：
   ```bash
   curl -X POST http://localhost:8080/api/tool-calling/calculator \
     -H "Content-Type: application/json" \
     -d "计算 123 加 456 等于多少？"
   ```
3. **使用多工具**：
   ```bash
   curl -X POST http://localhost:8080/api/tool-calling/multi-tool \
     -H "Content-Type: application/json" \
     -d "北京的天气怎么样？顺便帮我算一下 25 乘以 4 等于多少？"
   ```

## 总结

Spring AI 的工具调用功能允许大语言模型调用外部工具和函数，从而扩展模型的能力。通过定义自定义工具并注册到 ChatClient，模型可以根据需要调用这些工具来完成任务。这对于构建智能代理和自动化系统非常有用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI 函数调用文档](https://platform.openai.com/docs/guides/function-calling)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关工具调用的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Tools / Function Calling](https://docs.spring.io/spring-ai/reference/api/tools.html) - 工具调用统一抽象
- [Spring AI Chat Model — Ollama](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html) - Ollama 模型集成

### 示例模块
- **`spring-ai-ollama-tools`** - 本仓库中的工具调用示例模块
  - 主类：`com.github.teachingai.ollama.SpringAiOllamaApplication`
  - 工具类：`com.github.teachingai.ollama.tools.DateTimeTools`（使用 `@Tool` 注解）
  - 请求 DTO：`com.github.teachingai.ollama.request.ApiRequest`
  - 路径：`/v1/chat/completions`（流式接口，支持 tools 载荷）

### 核心概念
- **`@Tool` 注解**：声明式工具定义，供框架扫描注册
- **`ApiRequest`**：与 OpenAI 兼容的 tools、tool_choice、tool_calls 结构
- **工具调用闭环**：模型生成 tool_calls → 执行对应方法 → 结果以 tool 消息送回模型

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-ollama-tools

# 启动应用
mvn spring-boot:run

# 测试工具调用（携带 tools 定义的请求体）
curl -N -X POST "http://localhost:8080/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gemma3:4b",
    "messages": [
      {"role": "user", "content": "现在是什么时间？"}
    ],
    "tools": [{
      "type": "function",
      "function": {
        "name": "getCurrentDateTime",
        "description": "获取当前日期时间"
      }
    }]
  }'
```

### 最佳实践
1. **工具描述清晰**：`@Tool(description=...)` 与 JSON Schema 一致，减少误调用
2. **权限控制**：工具方法内校验调用方身份，避免模型被诱导执行危险操作
3. **幂等性**：确保重复 tool 调用安全
4. **兼容性设计**：`ApiRequest` 结构与 OpenAI 兼容，便于与前端或网关对接
5. **调试接口**：`GET /v1/generate` 和 `GET /v1/prompt` 提供简易调试能力