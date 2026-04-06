# 41、Spring AI 入门实践：Spring AI Ollama 工具调用（Function Calling）

## 一、项目概述

工具调用（Function Calling）是大语言模型的重要功能，允许模型与外部系统交互，从而回答原本无法回答的问题。通过 Ollama，我们可以在本地运行支持 Function Calling 的模型，并使用 Spring AI 的工具调用框架构建强大的 AI 应用。

### 核心功能

- **函数调用支持**：模型能够检测和调用外部函数
- **多工具集成**：支持注册和调用多个工具函数
- **结构化响应**：模型以结构化方式返回函数调用请求
- **本地部署**：基于 Ollama 的完全本地部署方案
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 实时信息查询（天气、股票等）
- 数据库查询和操作
- 第三方 API 调用
- 自动化任务执行
- 智能助手和客服

## 二、Ollama 工具调用简介

函数调用允许大语言模型与其他系统交互，从而使 LLMs 能够回答它们原本无法回答的问题。

### 推荐模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Llama 3.1 | Meta 最新开源，工具调用支持好 | 通用工具调用任务 |
| Qwen3.5 | 阿里巴巴最新，中文支持好 | 中文工具调用应用 |
| Mistral | 高效轻量，响应快 | 快速响应场景 |
| DeepSeek-R1 | 数学和推理能力强 | 复杂推理任务 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 函数检测 | 模型自动检测函数调用需求 |
| 结构化响应 | 以结构化方式返回调用请求 |
| 多工具支持 | 支持注册多个工具函数 |
| 本地部署 | 完全本地运行，保护隐私 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Ollama 本地服务

### 3.2 配置 Ollama

1. **访问 Ollama 官网**：https://ollama.com/
2. **下载并安装适合操作系统的版本**
3. **启动 Ollama 服务**
4. **拉取支持的模型**：

```bash
ollama pull llama3.1
ollama pull qwen3.5
ollama pull mistral
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
│   │   │                   ├── SpringAiOllamaToolsApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ToolController.java
│   │   │                   ├── service/
│   │   │                   │   └── ToolService.java
│   │   │                   └── functions/
│   │   │                       └── FunctionConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

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
    <!-- For Vector Store  -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure-cosmos-db</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-cassandra</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-couchbase</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-gemfire</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mongodb-atlas</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-neo4j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-opensearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-oracle</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-typesense</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-weaviate</artifactId>
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
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        enabled: true
        options:
          model: ${OLLAMA_MODEL:llama3.1}
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 工具函数配置

```java
package com.github.partmeai.ollama.functions;

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

### 6.2 工具函数实现

```java
package com.github.partmeai.ollama.functions;

import java.util.function.Function;

public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    @Override
    public WeatherResponse apply(WeatherRequest request) {
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
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}

public class WeatherResponse {
    private String city;
    private String temperature;
    private String weather;
    private String humidity;
    
    @Override
    public String toString() {
        return String.format("城市：%s，温度：%s，天气：%s，湿度：%s",
            city, temperature, weather, humidity);
    }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }
}
```

### 6.3 工具服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ToolService {
    
    private final ChatClient chatClient;
    
    public ToolService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Map<String, Object> callTool(String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        return Map.of(
                "message", message,
                "response", response
        );
    }
    
    public Flux<String> streamingCall(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
```

### 6.4 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.ToolService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ToolController {
    
    private final ToolService toolService;
    
    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }
    
    @GetMapping("/tool/call")
    public Map<String, Object> callTool(@RequestParam String message) {
        return toolService.callTool(message);
    }
    
    @GetMapping(value = "/tool/stream", produces = "text/event-stream")
    public Flux<String> streamingCall(@RequestParam String message) {
        return toolService.streamingCall(message);
    }
}
```

### 6.5 主应用类

```java
package com.github.partmeai.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaToolsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaToolsApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 工具调用 | GET | `/api/tool/call` | 调用工具函数 |
| 流式调用 | GET | `/api/tool/stream` | 流式工具调用 |

### 7.2 接口使用示例

#### 工具调用

```bash
curl -X GET "http://localhost:8080/api/tool/call?message=北京今天天气怎么样？"
```

## 八、部署方式

### 8.1 本地运行

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
cd spring-ai-ollama-tools
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-tools-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **函数设计**：设计清晰、单一职责的函数
2. **描述准确**：为函数提供准确的描述信息
3. **参数验证**：验证函数输入参数
4. **错误处理**：实现完善的错误处理机制

### 9.2 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 通用工具调用 | llama3.1 |
| 中文应用 | qwen3.5 |
| 快速响应 | mistral |
| 复杂推理 | deepseek-r1 |

## 十、运行项目

### 10.1 启动应用

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
cd spring-ai-ollama-tools
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X GET "http://localhost:8080/api/tool/call?message=你好"
```

## 十一、常见问题

### 11.1 工具调用失败

**Q: 模型无法正确调用工具函数怎么办？**

- 使用支持工具调用的模型（如 llama3.1、qwen3.5）
- 检查函数描述是否准确清晰
- 验证函数参数定义是否正确
- 参考 Spring AI 文档确认模型兼容性

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Ollama 官方文档：https://ollama.com/docs
- Spring AI Function Calling：参考官方文档
- 示例模块：spring-ai-ollama-tools
- 相关文档：文档4、文档12

## 十四、致谢

感谢 Ollama 团队和 Spring AI 团队提供的优秀工具。
