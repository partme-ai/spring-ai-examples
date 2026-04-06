# 4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）

## 一、项目概述

工具调用（Tool Calling）功能允许大语言模型调用外部工具和函数，从而扩展模型的能力。通过工具调用，模型可以执行计算、查询数据库、调用 API 等操作。本文将介绍如何在 Spring AI 中实现工具调用功能，包括单工具使用、多工具组合以及自定义工具开发。

### 核心功能

- **@Tool 注解**：声明式工具定义
- **单工具调用**：让模型调用单个工具
- **多工具组合**：让模型在多个工具中选择
- **工具回调**：完整的工具调用闭环
- **类型安全**：基于记录类型的工具定义
- **OpenAI 兼容接口**：支持标准的 tools、tool_calls 结构

### 适用场景

- 智能客服系统（查询订单、物流等）
- 数据查询与分析
- 外部 API 集成
- 自动化工作流
- 智能代理开发

## 二、工具调用简介

工具调用的工作原理是让模型根据用户请求判断需要调用什么工具，然后执行对应的函数，最后将结果返回给模型继续处理。

### 工具调用流程

1. 用户发送请求
2. 模型判断是否需要调用工具
3. 模型生成 tool_calls
4. 执行对应的工具函数
5. 将工具执行结果返回给模型
6. 模型基于工具结果生成最终回复

### @Tool 注解

Spring AI 提供了 `@Tool` 注解，用于声明式地定义工具。

| 特性 | 说明 |
|------|------|
| 声明式定义 | 使用注解标注方法 |
| 自动注册 | Spring 自动扫描并注册 |
| 类型安全 | 使用 Java 记录类型 |
| JSON Schema | 自动生成工具描述 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama（本地模型）

### 3.2 Ollama 配置

```bash
# 安装 Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 启动服务
ollama serve

# 拉取模型，推荐支持工具调用的模型
ollama pull llama3.1:8b
```

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-ollama-tools/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ToolController.java
│   │   │                   ├── tools/
│   │   │                   │   ├── DateTimeTools.java
│   │   │                   │   ├── CalculatorTools.java
│   │   │                   │   └── WeatherTools.java
│   │   │                   └── request/
│   │   │                       └── ApiRequest.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `DateTimeTools` | 日期时间相关工具 |
| `CalculatorTools` | 计算器工具 |
| `WeatherTools` | 天气查询工具 |
| `ToolController` | REST API 控制器 |
| `ApiRequest` | OpenAI 兼容的请求结构 |

## 五、核心配置

### 5.1 Maven 依赖

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

### 5.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-ollama-tools
  
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        enabled: true
        options:
          model: llama3.1:8b
          temperature: 0.7

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
    
    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
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

### 6.3 天气查询工具

```java
package com.github.partmeai.ollama.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class WeatherTools {
    
    public record WeatherRequest(String city) {}
    public record WeatherResponse(String city, double temperature, String condition, int humidity) {}
    
    private static final Map<String, String> WEATHER_CONDITIONS = Map.of(
        "北京", "晴天",
        "上海", "多云",
        "广州", "小雨",
        "深圳", "阴天"
    );
    
    private final Random random = new Random();
    
    @Tool(description = "获取指定城市的天气信息")
    public WeatherResponse getWeather(WeatherRequest request) {
        String city = request.city();
        String condition = WEATHER_CONDITIONS.getOrDefault(city, "晴天");
        double temperature = 15 + random.nextDouble() * 20;
        int humidity = 40 + random.nextInt(40);
        
        return new WeatherResponse(city, temperature, condition, humidity);
    }
}
```

### 6.4 OpenAI 兼容请求结构

```java
package com.github.partmeai.ollama.request;

import java.util.List;
import java.util.Map;

public record ApiRequest(
    String model,
    List<Message> messages,
    List<Tool> tools,
    String tool_choice
) {
    public record Message(String role, String content) {}
    
    public record Tool(String type, Function function) {}
    
    public record Function(String name, String description, Map<String, Object> parameters) {}
}
```

### 6.5 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.request.ApiRequest;
import com.github.partmeai.ollama.tools.CalculatorTools;
import com.github.partmeai.ollama.tools.DateTimeTools;
import com.github.partmeai.ollama.tools.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class ToolController {
    
    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    private final CalculatorTools calculatorTools;
    private final WeatherTools weatherTools;
    
    public ToolController(ChatClient.Builder chatClientBuilder,
                          DateTimeTools dateTimeTools,
                          CalculatorTools calculatorTools,
                          WeatherTools weatherTools) {
        this.chatClient = chatClientBuilder.build();
        this.dateTimeTools = dateTimeTools;
        this.calculatorTools = calculatorTools;
        this.weatherTools = weatherTools;
    }
    
    @GetMapping("/v1/generate")
    public String simpleGenerate(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
    
    @GetMapping("/v1/prompt")
    public String simplePrompt(@RequestParam String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    
    @PostMapping("/v1/chat/completions")
    public ChatResponse chatCompletions(@RequestBody ApiRequest request) {
        List<FunctionCallbackWrapper<?, ?>> tools = List.of(
            FunctionCallbackWrapper.builder(dateTimeTools::getCurrentDateTime)
                .withName("getCurrentDateTime")
                .withDescription("获取当前日期时间")
                .build(),
            FunctionCallbackWrapper.builder(dateTimeTools::getCurrentDate)
                .withName("getCurrentDate")
                .withDescription("获取当前日期")
                .build(),
            FunctionCallbackWrapper.builder(dateTimeTools::getCurrentTime)
                .withName("getCurrentTime")
                .withDescription("获取当前时间")
                .build(),
            FunctionCallbackWrapper.builder(calculatorTools::calculate)
                .withName("calculate")
                .withDescription("执行基本的数学运算：加、减、乘、除")
                .withInputType(CalculatorTools.CalculationRequest.class)
                .withOutputType(CalculatorTools.CalculationResponse.class)
                .build(),
            FunctionCallbackWrapper.builder(weatherTools::getWeather)
                .withName("getWeather")
                .withDescription("获取指定城市的天气信息")
                .withInputType(WeatherTools.WeatherRequest.class)
                .withOutputType(WeatherTools.WeatherResponse.class)
                .build()
        );
        
        return chatClient.prompt()
                .messages(request.messages().stream()
                    .map(msg -> new org.springframework.ai.chat.messages.Message(
                        org.springframework.ai.chat.messages.MessageType.valueOf(msg.role().toUpperCase()),
                        msg.content()
                    ))
                    .toList())
                .functions(tools)
                .call()
                .chatResponse();
    }
    
    @PostMapping(value = "/v1/chat/completions", produces = "text/event-stream")
    public Flux<String> chatCompletionsStream(@RequestBody ApiRequest request) {
        List<FunctionCallbackWrapper<?, ?>> tools = List.of(
            FunctionCallbackWrapper.builder(dateTimeTools::getCurrentDateTime)
                .withName("getCurrentDateTime")
                .withDescription("获取当前日期时间")
                .build(),
            FunctionCallbackWrapper.builder(calculatorTools::calculate)
                .withName("calculate")
                .withDescription("执行基本的数学运算：加、减、乘、除")
                .withInputType(CalculatorTools.CalculationRequest.class)
                .withOutputType(CalculatorTools.CalculationResponse.class)
                .build()
        );
        
        return chatClient.prompt()
                .messages(request.messages().stream()
                    .map(msg -> new org.springframework.ai.chat.messages.Message(
                        org.springframework.ai.chat.messages.MessageType.valueOf(msg.role().toUpperCase()),
                        msg.content()
                    ))
                    .toList())
                .functions(tools)
                .stream()
                .content();
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单生成 | GET | `/v1/generate` | 简单文本生成 |
| 简单提示 | GET | `/v1/prompt` | 简单提示调用 |
| 聊天完成 | POST | `/v1/chat/completions` | OpenAI 兼容接口 |
| 流式聊天 | POST | `/v1/chat/completions` | 流式响应 |

### 7.2 接口使用示例

#### 查询当前时间

```bash
curl -N -X POST "http://localhost:8080/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.1:8b",
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

#### 使用计算器

```bash
curl -N -X POST "http://localhost:8080/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.1:8b",
    "messages": [
      {"role": "user", "content": "计算 123 加 456 等于多少？"}
    ]
  }'
```

#### 查询天气

```bash
curl -N -X POST "http://localhost:8080/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.1:8b",
    "messages": [
      {"role": "user", "content": "北京的天气怎么样？"}
    ]
  }'
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-ollama-tools
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-tools-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import json

class ToolCallingClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message, tools=None):
        data = {
            "model": "llama3.1:8b",
            "messages": [{"role": "user", "content": message}]
        }
        if tools:
            data["tools"] = tools
        
        response = requests.post(
            f"{self.base_url}/v1/chat/completions",
            json=data
        )
        return response.json()

client = ToolCallingClient()

# 查询时间
result = client.chat("现在是什么时间？")
print(result)

# 计算
result = client.chat("计算 25 乘以 4")
print(result)
```

### 9.2 最佳实践

1. **工具描述清晰**：`@Tool(description=...) 与 JSON Schema 一致，减少误调用
2. **权限控制**：工具方法内校验调用方身份，避免模型被诱导执行危险操作
3. **幂等性**：确保重复 tool 调用安全
4. **兼容性设计**：`ApiRequest` 结构与 OpenAI 兼容，便于与前端或网关对接
5. **调试接口**：`GET /v1/generate` 和 `GET /v1/prompt` 提供简易调试能力

## 十、运行项目

### 10.1 前置检查

```bash
curl http://localhost:11434/api/tags
```

### 10.2 启动应用

```bash
cd spring-ai-ollama-tools
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl "http://localhost:8080/v1/generate?message=你好"
```

## 十一、常见问题

### 11.1 工具调用问题

**Q: 模型不调用工具怎么办？**

- 确保工具描述清晰明确
- 检查工具参数类型是否合理
- 尝试在用户提示中明确说明可以使用工具
- 使用支持工具调用的模型（如 llama3.1）

**Q: 工具执行失败怎么调试？**

- 先使用 `GET /v1/generate` 调试基础对话
- 检查工具方法参数是否正确
- 查看应用日志中的错误信息

### 11.2 安全性问题

**Q: 如何防止模型被诱导执行危险操作？**

- 在工具方法内部添加权限校验
- 限制工具能访问的资源范围
- 对敏感操作添加人工确认

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Tools 文档：https://docs.spring.io/spring-ai/reference/api/tools.html
- Spring AI Ollama Chat：https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html
- 示例模块：spring-ai-ollama-tools

## 十四、致谢

感谢 Spring AI 团队提供的优秀框架，让工具调用功能变得如此简单易用。
