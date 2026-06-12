# Spring AI 入门实践：Spring AI 与 Coze 集成

> 基于 Spring AI 框架实现与 Coze（扣子）平台的集成，提供丰富的模型生态、工具集成、工作流编排、多渠道发布等功能，支持智能客服和企业助手应用。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Coze 平台的示例，展示了如何在 Java/Spring Boot 应用中使用 Coze 的各种功能。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Coze | - | 字节跳动 AI 对话平台 |
| Coze API | - | Coze REST API |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-coze
**本地路径**：`spring-ai-coze/`

### 1.4 核心功能

- **丰富的模型生态**：支持多种国内外大语言模型
- **工具集成**：内置丰富的插件和工具生态
- **工作流编排**：可视化的 Bot 编排能力
- **多渠道发布**：支持多种平台一键发布
- **统一 API**：Spring AI 提供的统一抽象接口
- **中文优化**：针对中文场景的深度优化

### 适用场景

- 智能客服和对话机器人
- 企业内部助手
- 多平台 AI 应用发布
- 中文内容生成和处理
- 知识问答和信息检索

## 二、Coze 简介

Coze 是一个一站式 AI Bot 开发平台，提供从 Bot 开发、调试到发布的完整解决方案。

### 2.1 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Coze 官方文档](https://www.coze.cn/docs) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| Bot 编排 | 可视化拖拽式 Bot 构建 |
| 插件生态 | 丰富的内置和第三方插件 |
| 知识库 | 支持文档上传和语义检索 |
| 多端发布 | 支持微信、飞书、Slack 等平台 |
|  API 访问 | 提供完整的 REST API |

### 可用模型

Coze 平台集成了多种优质模型，包括：
- 豆包模型（ByteDance）
- Claude（Anthropic）
- GPT（OpenAI）
- 及其他主流模型

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Coze 平台账号

### 3.2 Coze 账号准备

1. **访问 Coze 平台**：https://www.coze.cn/
2. **注册/登录账号**
3. **创建 Bot**：在 Coze 平台创建一个 Bot
4. **获取 API 密钥**：在 Bot 设置中获取 API Key
5. **记录 Bot ID**：记录 Bot 的唯一标识

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-coze/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── coze/
│   │   │                   ├── SpringAiCozeApplication.java
│   │   │                   ├── controller/
│   │   │                   │   └── ChatController.java
│   │   │                   └── service/
│   │   │                       └── ChatService.java
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
        <artifactId>spring-ai-starter-model-anthropic</artifactId>
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
    name: spring-ai-coze
  
  ai:
    coze:
      api-key: ${COZE_API_KEY:your-api-key}
      bot-id: ${COZE_BOT_ID:your-bot-id}
      chat:
        enabled: true
        options:
          temperature: 0.7

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 对话服务

```java
package com.github.partmeai.coze.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Map<String, Object> chat(String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        return Map.of(
                "message", message,
                "response", response
        );
    }
    
    public Map<String, Object> chatWithAdditionalParams(String message, 
                                                          Map<String, Object> additionalParams) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        
        return Map.of(
                "message", message,
                "additionalParams", additionalParams,
                "response", response
        );
    }
    
    public Flux<String> streamingChat(String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.coze.controller;

import com.github.partmeai.coze.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatService.chat(message);
    }
    
    @PostMapping("/params")
    public Map<String, Object> chatWithParams(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        Map<String, Object> additionalParams = (Map<String, Object>) request.getOrDefault("params", Map.of());
        return chatService.chatWithAdditionalParams(message, additionalParams);
    }
    
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<String> streamingChat(@RequestParam String message) {
        return chatService.streamingChat(message);
    }
}
```

### 6.3 主应用类

```java
package com.github.partmeai.coze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiCozeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringAiCozeApplication.class, args);
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单对话 | POST | `/api/chat` | 简单的单次对话 |
| 带参对话 | POST | `/api/chat/params` | 带额外参数的对话 |
| 流式响应 | GET | `/api/chat/stream` | 流式输出响应 |

### 7.2 接口使用示例

#### 简单对话

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "请介绍一下 Coze"}'
```

#### 带参对话

```bash
curl -X POST http://localhost:8080/api/chat/params \
  -H "Content-Type: application/json" \
  -d '{
    "message": "查询天气",
    "params": {
      "city": "北京",
      "date": "2024-01-01"
    }
  }'
```

#### 流式响应

```bash
curl -N "http://localhost:8080/api/chat/stream?message=详细介绍一下Coze平台"
```

## 八、部署方式

### 8.1 本地运行

```bash
export COZE_API_KEY=your-api-key
export COZE_BOT_ID=your-bot-id
cd spring-ai-coze
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export COZE_API_KEY=your-api-key
export COZE_BOT_ID=your-bot-id
mvn clean package -DskipTests
java -jar target/spring-ai-coze-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests

class CozeClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def chat(self, message):
        response = requests.post(
            f"{self.base_url}/api/chat",
            json={"message": message}
        )
        return response.json()
    
    def chat_with_params(self, message, params=None):
        if params is None:
            params = {}
        response = requests.post(
            f"{self.base_url}/api/chat/params",
            json={"message": message, "params": params}
        )
        return response.json()

client = CozeClient()

result = client.chat("请介绍一下 Coze")
print(f"Response: {result['response']}")
```

### 9.2 最佳实践

1. **Bot 配置**：
   - 在 Coze 平台精心配置 Bot 的人设和功能
   - 配置合适的插件和工具
   - 上传相关的知识库文档

2. **API 使用**：
   - 合理使用额外参数传递上下文
   - 利用 Coze 的知识库功能增强效果
   - 配置适当的工作流处理复杂任务

3. **多平台发布**：
   - 利用 Coze 的多平台发布能力
   - 同时在多个渠道提供服务
   - 统一管理和维护 Bot

### 9.3 应用场景推荐

| 场景 | 推荐方式 |
|------|----------|
| 智能客服 | Coze Bot + 插件 + 知识库 |
| 企业助手 | Coze Bot + 企业插件 + 内部文档 |
| 内容生成 | Coze Bot + 专门人设配置 |
| 多渠道服务 | Coze Bot + 多端发布 |

## 十、运行项目

### 10.1 启动应用

```bash
export COZE_API_KEY=your-api-key
export COZE_BOT_ID=your-bot-id
cd spring-ai-coze
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'
```

## 十一、常见问题

### 11.1 Bot 配置问题

**Q: 提示 Bot 不存在怎么办？**

- 确认 Bot ID 配置正确
- 确认 Bot 在 Coze 平台已发布
- 检查 API 密钥是否正确
- 确认 Bot 状态正常

**Q: 插件不工作怎么办？**

- 确认在 Coze 平台已启用相关插件
- 检查插件配置是否正确
- 确认插件权限设置适当
- 查看 Coze 平台的调试日志

### 11.2 API 调用问题

**Q: API 调用失败怎么办？**

- 确认 API 密钥正确且有效
- 检查网络连接是否正常
- 查看 API 调用限额是否足够
- 检查请求格式是否正确

**Q: 响应超时怎么办？**

- 考虑简化 Bot 配置和减少插件
- 检查知识库大小是否合理
- 调整任务复杂度
- 查看 Coze 平台状态

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Coze 官方文档：https://www.coze.cn/docs
- Spring AI Coze：https://docs.spring.io/spring-ai/reference/api/chat/coze-chat.html
- Coze 平台：https://www.coze.cn/
- 示例模块：spring-ai-coze

---

## 十四、Java 客户端示例

### 14.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class CozeClient {

    private static final String BASE_URL = "http://localhost:8080/api/coze";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String botId, String message) {
        String url = BASE_URL + "/chat?botId={botId}&message={message}";
        return restTemplate.getForObject(url, String.class, botId, message);
    }

    public Map<String, Object> getBotStatus(String botId) {
        return restTemplate.getForObject(BASE_URL + "/bot/{botId}/status", Map.class, botId);
    }
}
```

---

## 十五、致谢

- **感谢字节跳动 Coze 团队** 提供强大的 AI 对话平台
- **感谢 Spring AI 团队** 提供 Coze 集成框架
- **感谢开源社区** 提供丰富的技术资源
