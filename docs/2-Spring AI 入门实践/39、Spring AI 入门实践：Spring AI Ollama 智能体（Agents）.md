# 39、Spring AI 入门实践：Spring AI Ollama 智能体（Agents）

## 一、项目概述

智能体（Agents）是 Spring AI 的高级功能，允许大语言模型自主规划和执行多步骤任务。通过 Ollama，我们可以在本地运行各种大型语言模型，并利用 Spring AI 的智能体框架构建复杂的 AI 应用。

### 核心功能

- **自主任务规划**：模型能够自主规划复杂任务的执行步骤
- **多步骤执行**：支持分步执行复杂任务
- **工具集成**：与工具调用功能结合，实现与外部系统交互
- **本地部署**：基于 Ollama 的完全本地部署方案
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 复杂任务自动化
- 多步骤工作流执行
- 智能助手和客服
- 数据分析和报告生成
- 代码生成和审查

## 二、Ollama 智能体简介

智能体是能够自主感知环境、做出决策并执行行动的 AI 系统。

### 推荐模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Llama 3.1 | Meta 最新开源，工具调用支持好 | 通用智能体任务 |
| Qwen3.5 | 阿里巴巴最新，中文支持好 | 中文智能体应用 |
| Mistral | 高效轻量，响应快 | 快速响应场景 |
| DeepSeek-R1 | 数学和推理能力强 | 复杂推理任务 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 自主规划 | 模型自主规划任务步骤 |
| 多步骤执行 | 支持分步执行复杂任务 |
| 工具集成 | 可调用外部工具 |
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
spring-ai-ollama-agents/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── ollama/
│   │   │                   ├── SpringAiOllamaAgentsApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── AgentController.java
│   │   │                   ├── service/
│   │   │                   │   └── AgentService.java
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
    name: spring-ai-ollama-agents
  
  ai:
    ollama:
      base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
      chat:
        enabled: true
        options:
          model: ${OLLAMA_MODEL:llama3.1}
          temperature: 0.8

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

### 6.3 智能体服务

```java
package com.github.partmeai.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class AgentService {
    
    private final ChatClient chatClient;
    
    public AgentService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Map<String, Object> executeTask(String task) {
        String response = chatClient.prompt()
                .user(task)
                .call()
                .content();
        
        return Map.of(
                "task", task,
                "response", response
        );
    }
    
    public Flux<String> streamingExecute(String task) {
        return chatClient.prompt()
                .user(task)
                .stream()
                .content();
    }
}
```

### 6.4 REST 控制器

```java
package com.github.partmeai.ollama.controller;

import com.github.partmeai.ollama.service.AgentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AgentController {
    
    private final AgentService agentService;
    
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }
    
    @PostMapping("/agent/execute")
    public Map<String, Object> executeTask(@RequestBody Map<String, String> request) {
        String task = request.get("task");
        return agentService.executeTask(task);
    }
    
    @GetMapping(value = "/agent/stream", produces = "text/event-stream")
    public Flux<String> streamingExecute(@RequestParam String task) {
        return agentService.streamingExecute(task);
    }
}
```

### 6.5 主应用类

```java
package com.github.partmeai.ollama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOllamaAgentsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaAgentsApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 执行任务 | POST | `/api/agent/execute` | 执行智能体任务 |
| 流式执行 | GET | `/api/agent/stream` | 流式执行任务 |

### 7.2 接口使用示例

#### 执行任务

```bash
curl -X POST http://localhost:8080/api/agent/execute \
  -H "Content-Type: application/json" \
  -d '{"task": "帮我规划一个去北京的三日旅行计划"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
cd spring-ai-ollama-agents
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
mvn clean package -DskipTests
java -jar target/spring-ai-ollama-agents-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **任务分解**：将复杂任务分解为可管理的小任务
2. **工具设计**：设计清晰、单一职责的工具函数
3. **上下文管理**：合理管理对话上下文，避免超出模型限制
4. **错误处理**：实现重试机制和错误恢复

### 9.2 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 通用智能体 | llama3.1 |
| 中文应用 | qwen3.5 |
| 快速响应 | mistral |
| 复杂推理 | deepseek-r1 |

## 十、运行项目

### 10.1 启动应用

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.1
cd spring-ai-ollama-agents
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/agent/execute \
  -H "Content-Type: application/json" \
  -d '{"task": "你好，请介绍一下自己"}'
```

## 十一、常见问题

### 11.1 模型兼容性问题

**Q: 某些模型不支持工具调用怎么办？**

- 使用支持工具调用的模型（如 llama3.1、qwen3.5）
- 检查 Ollama 模型库的工具调用支持说明
- 参考 Spring AI 文档确认模型兼容性

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Ollama 官方文档：https://ollama.com/docs
- Spring AI Agents：参考官方文档
- 示例模块：spring-ai-ollama-agents
- 相关文档：文档12、文档41

## 十四、致谢

感谢 Ollama 团队和 Spring AI 团队提供的优秀工具。
