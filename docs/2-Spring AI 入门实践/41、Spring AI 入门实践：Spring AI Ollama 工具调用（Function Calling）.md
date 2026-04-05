# Spring AI 入门实践：Spring AI Ollama 工具调用（Function Calling）

## 概述

工具调用（Function Calling）是大语言模型的重要功能，允许模型与外部系统交互，从而回答原本无法回答的问题。通过 Ollama，我们可以在本地运行支持 Function Calling 的模型，并使用 Spring AI 的工具调用框架构建强大的 AI 应用。

## 准备工作

### 1. Ollama 安装与配置

首先，您需要在本地计算机上运行 Ollama：

1. 访问 [Ollama 官网](https://ollama.com/)
2. 下载并安装适合您操作系统的版本
3. 启动 Ollama 服务
4. 拉取支持 Function Calling 的模型：

```bash
ollama pull qwen3.5
ollama pull llama3.1
ollama pull mistral
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

### 3. 配置 Ollama 连接

在 `application.properties` 文件中配置 Ollama 相关设置：

```properties
# Ollama 基础配置
spring.ai.ollama.base-url=http://localhost:11434

# Chat 模型配置
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=qwen3.5
```

## 核心功能

### 1. Function Calling 原理

函数调用是 OpenAI 的 GPT-4-0613 和 GPT-3.5 Turbo-0613 模型中的一项新功能。这些 AI 模型经过训练，可以根据用户的提示检测函数调用的需求，并以结构化的调用请求进行响应。

函数调用允许大语言模型与其他系统交互，从而使 LLMs 能够回答它们原本无法回答的问题，例如需要实时信息或训练集中未包含的数据的问题。

### 2. 定义工具函数

使用 Spring AI 定义工具函数：

```java
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    @Description("获取指定城市的当前天气信息")
    public FunctionCallbackWrapper<WeatherRequest, WeatherResponse> weatherFunction() {
        return FunctionCallbackWrapper.builder(new WeatherService())
                .withName("getWeather")
                .withDescription("获取指定城市的当前天气信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }

    @Bean
    @Description("获取指定地区的PC在线信息")
    public FunctionCallbackWrapper<RegionRequest, RegionResponse> regionFunction() {
        return FunctionCallbackWrapper.builder(new RegionService())
                .withName("getRegionInfo")
                .withDescription("获取指定地区的PC在线信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }
}
```

### 3. 工具函数实现

实现具体的工具函数：

```java
import java.util.function.Function;

public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    @Override
    public WeatherResponse apply(WeatherRequest request) {
        // 模拟天气查询
        WeatherResponse response = new WeatherResponse();
        response.setCity(request.getCity());
        response.setTemperature("25°C");
        response.setWeather("晴天");
        response.setHumidity("60%");
        return response;
    }
}

public class WeatherRequest {
    private String city;

    // getters and setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

public class WeatherResponse {
    private String city;
    private String temperature;
    private String weather;
    private String humidity;

    // getters and setters
    @Override
    public String toString() {
        return String.format("城市：%s，温度：%s，天气：%s，湿度：%s", 
            city, temperature, weather, humidity);
    }
}
```

### 4. 使用工具调用

在控制器中使用工具调用：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToolController {

    private final ChatModel chatModel;

    @Autowired
    public ToolController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/tool/call")
    public String callTool(@RequestParam(value = "message", defaultValue = "北京今天天气怎么样？") String message) {
        return chatModel.call(message);
    }
}
```

## 推荐模型

### Qwen3.5

Qwen3.5 是阿里巴巴推出的最新系列大型语言模型，在推理、代码生成和数学能力方面有显著提升，对 Function Calling 支持良好：

```bash
ollama run qwen3.5:0.5b
ollama run qwen3.5:1.8b  
ollama run qwen3.5:4b
ollama run qwen3.5:7b
ollama run qwen3.5:14b
ollama run qwen3.5:32b
ollama run qwen3.5:72b
```

### Llama 3.1

Meta 的最新开源模型，支持工具调用：

```bash
ollama run llama3.1
ollama run llama3.1:70b
```

### Mistral

Mistral AI 的开源模型，支持 Function Calling：

```bash
ollama run mistral
```

## 完整示例

### 项目结构

```
spring-ai-ollama-tools/
├── src/main/java/com/github/teachingai/ollama/
│   ├── controller/
│   │   └── ToolController.java
│   ├── functions/
│   │   ├── FunctionConfig.java
│   │   ├── GetWeatherFunction.java
│   │   └── PconlineRegionFunction.java
│   └── SpringAiOllamaApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-tools
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /tool/call?message=北京今天天气怎么样？` - 工具调用示例

## 最佳实践

1. **函数设计**：设计清晰、单一职责的函数
2. **描述准确**：为函数提供准确的描述信息
3. **错误处理**：实现完善的错误处理机制
4. **参数验证**：验证函数输入参数
5. **性能优化**：缓存频繁调用的结果

## 相关资源

- [Ollama 官方文档](https://ollama.com/docs)
- [Spring AI Function Calling 文档](https://docs.spring.io/spring-ai/reference/api/chat/functions.html)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-tools)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 工具调用基础](4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）.md)
- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)
- [Spring AI 智能体（Agents）](12、Spring AI 入门实践：Spring AI 智能体（Agents）.md)