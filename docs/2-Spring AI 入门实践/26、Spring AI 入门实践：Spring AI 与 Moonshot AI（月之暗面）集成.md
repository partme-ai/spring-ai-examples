# Spring AI 入门实践：Spring AI 与 Moonshot AI（月之暗面）集成

> 基于 Spring AI 框架实现与 Moonshot AI（月之暗面）的集成，提供文本生成、对话交互、嵌入计算等功能，支持 Kimi 等知名大语言模型。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Moonshot AI 的示例，展示了如何在 Java/Spring Boot 应用中使用 Moonshot AI 的大语言模型进行文本生成、对话和嵌入计算。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Moonshot AI | - | Kimi Chat 大语言模型 |
| spring-ai-autoconfigure-model-moonshot | - | Spring AI Moonshot 集成 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-moonshot
**本地路径**：`spring-ai-moonshot/`

### 1.4 核心功能

- ✅ 文本生成：支持高质量的中文文本生成
- ✅ 对话交互：支持多轮对话和上下文管理
- ✅ 嵌入计算：支持文本向量化
- ✅ 长文本处理：支持长上下文处理
- ✅ 流式响应：支持实时流式输出
- ✅ 高性能API：优化的 API 调用性能

---

## 二、Moonshot AI 简介

### 2.1 Moonshot AI 介绍

Moonshot AI（月之暗面）是中国领先的人工智能公司，推出了 Kimi Chat 等知名产品。Kimi 支持超长上下文输入（20万字以上），在中文理解、长文本处理、代码生成等方面表现优秀。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **长上下文** | 支持 20 万字以上超长上下文 |
| **中文优化** | 针对中文场景深度优化 |
| **代码能力** | 强大的代码生成和理解能力 |
| **多模态** | 支持文本和图像输入 |
| **高性能** | 优化的推理性能 |
| **API 稳定** | 企业级 API 稳定性 |

### 2.3 模型规格

| 模型 | 上下文长度 | 特点 |
|------|----------|------|
| moonshot-v1-8k | 8K | 轻量级，快速响应 |
| moonshot-v1-32k | 32K | 标准长文本处理 |
| moonshot-v1-128k | 128K | 超长上下文处理 |

---

## 三、性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Moonshot AI 官方文档](https://platform.moonshot.cn/docs) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 四、应用案例

### 长文档 RAG 系统
- **业务场景**：长文档分析、合同审查、研究报告
- **性能指标**：支持 20 万字上下文，响应时间 1-5秒
- **技术方案**：Moonshot AI 长上下文 + 向量检索

### 智能客服系统
- **业务场景**：企业智能客服、知识问答
- **性能指标**：对话响应 < 1秒，准确率 90%+
- **技术方案**：Moonshot AI + 知识库

---

## 五、项目结构

```
spring-ai-moonshot/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── moonshot/
│   │   │               ├── MoonshotApplication.java
│   │   │               ├── controller/
│   │   │               │   ├── ChatController.java
│   │   │               │   └── EmbeddingController.java
│   │   │               └── service/
│   │   │                   └── MoonshotService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── moonshot/
│                       └── MoonshotApplicationTests.java
```

### 文件说明

- `pom.xml` - Maven 依赖配置
- `MoonshotApplication.java` - Spring Boot 应用入口
- `ChatController.java` - 对话控制器
- `EmbeddingController.java` - 嵌入控制器
- `MoonshotService.java` - Moonshot AI 服务
- `application.properties` - 应用配置文件

---

## 六、核心配置
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Moonshot（月之暗面）聊天示例
 */
@RestController
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map<String, Object> generate(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

### 2. 提示模板使用

使用 Spring AI 的提示模板功能：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @GetMapping("/v1/prompt")
    public List<Generation> prompt(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {adjective} joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("adjective", "funny", "topic", "cats"));
        return chatModel.call(prompt).getResults();
    }
}
```

### 3. 流式对话

支持流式响应，适用于实时对话场景：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @PostMapping("/v1/chat/completions")
    public Flux<ChatResponse> chatCompletions(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

### 4. 工具调用（Function Calling）

Moonshot AI 支持工具调用功能，可以与外部工具集成：

```java
package com.github.partmeai.moonshotai.functions;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    @Description("获取指定城市的天气信息")
    public FunctionCallbackWrapper<GetWeatherFunction.Request, GetWeatherFunction.Response> weatherFunction() {
        return FunctionCallbackWrapper.builder(new GetWeatherFunction())
                .withName("getWeather")
                .withDescription("获取指定城市的天气信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }

    @Bean
    @Description("获取指定地区的PC在线信息")
    public FunctionCallbackWrapper<PconlineRegionFunction.Request, PconlineRegionFunction.Response> pconlineRegionFunction() {
        return FunctionCallbackWrapper.builder(new PconlineRegionFunction())
                .withName("getPconlineRegion")
                .withDescription("获取指定地区的PC在线信息")
                .withResponseConverter((response) -> response.toString())
                .build();
    }
}
```

### 5. 文本嵌入

使用 Moonshot AI 进行文本嵌入计算：

```java
package com.github.partmeai.moonshotai.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/v1/embedding")
    public Map<String, Object> embedding(@RequestParam(value = "message", defaultValue = "需要嵌入的文本") String message) {
        return Map.of("embeddings", embeddingModel.embed(message));
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-moonshotai/
├── src/main/java/com/github/teachingai/moonshotai/
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── EmbeddingController.java
│   ├── functions/
│   │   ├── FunctionConfig.java
│   │   ├── GetWeatherFunction.java
│   │   └── PconlineRegionFunction.java
│   ├── router/
│   │   └── RouterFunctionConfig.java
│   └── SpringAiMoonshotAiApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-moonshotai
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成
   - `GET /v1/prompt?message=测试` - 提示模板使用
   - `POST /v1/chat/completions?message=讲个故事` - 流式对话
   - `GET /v1/embedding?message=需要嵌入的文本` - 文本嵌入

## 测试方法

### 单元测试

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.chat.model.ChatModel;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MoonshotAiIntegrationTest {

    @Autowired
    private ChatModel chatModel;

    @Test
    void testChatGeneration() {
        String response = chatModel.call("你好");
        assertThat(response).isNotEmpty();
    }
}
```

## 最佳实践

1. **API Key 管理**：使用环境变量或配置中心管理敏感信息
2. **模型选择**：根据需求选择合适的模型，moonshot-v1-8k 适用于大多数场景
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额
5. **性能优化**：对于批量处理，考虑使用异步调用

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 无效或过期 | 检查 API Key 是否正确，是否有足够的余额 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |
| 模型不可用 | 指定的模型不存在或不可访问 | 检查模型名称是否正确 |

## 相关资源

- [Moonshot AI 官方文档](https://platform.moonshot.cn/docs)
- [Spring AI Moonshot 集成](https://docs.spring.io/spring-ai/reference/api/clients/moonshot.html)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-moonshotai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

---

## 七、Java 客户端示例

### 7.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

public class MoonshotClient {

    private static final String BASE_URL = "http://localhost:8080/api/moonshot";
    private final RestTemplate restTemplate = new RestTemplate();

    public String chat(String message) {
        String url = BASE_URL + "/chat?message={message}";
        return restTemplate.getForObject(url, String.class, message);
    }

    public Map<String, Object> chatCompletion(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(BASE_URL + "/completions", entity, Map.class);
    }

    public List<Float> embed(String text) {
        String url = BASE_URL + "/embed?text={text}";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class, text);
        return (List<Float>) response.get("embedding");
    }
}
```

---

## 八、许可证

- **Moonshot AI**：商业许可
- **Spring AI**：Apache 2.0
- **本项目**：Apache 2.0

---

## 九、致谢

- **感谢 Moonshot AI 团队** 提供优秀的中文大语言模型
- **感谢 Spring AI 团队** 提供 AI 能力集成框架
- **感谢开源社区** 提供丰富的技术资源

---

## 扩展阅读

- [Spring AI 与 OpenAI 集成](25、Spring AI 入门实践：Spring AI 与 OpenAI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)
- [Spring AI 工具调用](4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）.md)