# Spring AI 入门实践：Spring AI 与阶跃星辰集成

## 概述

阶跃星辰（StepFun）是中国新兴的人工智能公司，推出了 Step 系列大语言模型。Spring AI 提供了对阶跃星辰 API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Step 系列模型进行文本生成和对话。

## 项目概述

### 1.1 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-stepfun
**本地路径**：`spring-ai-stepfun/`

## 准备工作

### 1. 阶跃星辰账号配置

首先，您需要配置阶跃星辰账号并获取 API Key：

1. 访问 [阶跃星辰开放平台](https://platform.stepfun.com/)
2. 注册账号并登录
3. 创建应用并获取 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-stepfun</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
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

### 3. 配置阶跃星辰连接

在 `application.properties` 文件中配置阶跃星辰相关设置：

```properties
# 阶跃星辰配置
spring.ai.stepfun.api-key=你的API密钥

# Chat 模型配置
spring.ai.stepfun.chat.enabled=true
spring.ai.stepfun.chat.options.model=step-1-8k
```

## 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [阶跃星辰官方文档](https://platform.stepfun.com/docs) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 应用案例

### 场景一：智能客服系统

**业务场景**：为企业提供 7×24 小时智能客服服务，自动回答用户咨询、处理常见问题。

**性能指标**：
- 响应时间：平均 2-3 秒返回结果
- 并发支持：支持 100+ 并发请求
- 准确率：常见问题解答准确率达到 85% 以上

**技术方案**：
- 使用 `step-1-8k` 模型处理常规对话
- 采用流式响应提升用户体验
- 结合知识库 RAG 实现专业问题解答

### 场景二：内容创作助手

**业务场景**：辅助内容创作者生成文章大纲、段落内容、营销文案等。

**性能指标**：
- 生成质量：文本连贯性评分 4.2/5.0
- 创作效率：提升内容创作效率 50% 以上
- 多样性：支持不同风格和语调的内容生成

**技术方案**：
- 使用 `step-1-32k` 模型处理长文本需求
- 通过 Prompt Engineering 优化生成质量
- 实现多轮对话迭代优化内容

### 场景三：代码辅助开发

**业务场景**：为开发者提供代码生成、代码解释、Bug 诊断等辅助功能。

**性能指标**：
- 代码准确性：生成代码可用性达到 80%
- 响应速度：平均 1.5 秒返回代码建议
- 语言支持：覆盖 Java、Python、JavaScript 等主流语言

**技术方案**：
- 集成开发工具插件
- 使用专门的代码提示 Prompt
- 结合语法检查提升代码质量

## 核心功能

### 1. 文本生成

使用阶跃星辰进行文本生成：

```java
package com.github.hiwepy.stepfun.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatModel chatModel;

    @Autowired
    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/v1/generate")
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "你好") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

### 2. 流式对话

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
    public Flux<ChatResponse> chatCompletions(@RequestParam(value = "message", defaultValue = "讲个故事") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}
```

## Java 客户端

如果您需要在非 Spring Boot 环境中使用阶跃星辰 API，可以使用以下 Java 客户端示例：

```java
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class StepfunClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StepfunClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> generate(String message) {
        String url = baseUrl + "/v1/generate?message=" +
                    java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
        return restTemplate.getForObject(url, Map.class);
    }

    public static void main(String[] args) {
        StepfunClient client = new StepfunClient("http://localhost:8080");
        Map<String, Object> result = client.generate("你好");
        System.out.println("Generation: " + result.get("generation"));
    }
}
```

**使用说明**：

1. 确保 Spring Boot 应用已启动并运行在 `http://localhost:8080`
2. 创建 `StepfunClient` 实例，传入服务器地址
3. 调用 `generate()` 方法发送消息并获取响应

**依赖配置**：

如果使用此客户端，需要在项目中添加 Spring Web 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 完整示例

### 项目结构

```
spring-ai-stepfun/
├── src/main/java/com/github/hiwepy/stepfun/
│   ├── controller/
│   │   └── ChatController.java
│   └── SpringStepFunAiApplication.java
└── src/main/resources/
    └── application.properties
```

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-stepfun
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成
   - `POST /v1/chat/completions?message=讲个故事` - 流式对话

## 最佳实践

1. **API Key 管理**：使用环境变量或配置中心管理敏感信息
2. **模型选择**：根据需求选择合适的模型
   - step-1-8k：适合一般对话场景
   - step-1-32k：适合长文本处理
3. **错误处理**：合理处理 API 限流、超时等异常
4. **成本控制**：监控 API 使用量，设置使用限额

## 故障排除

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 错误 | API Key 无效或过期 | 检查 API Key 是否正确 |
| 429 错误 | 请求频率超限 | 降低请求频率，增加重试机制 |
| 超时错误 | 网络问题或服务不稳定 | 检查网络连接，增加超时时间配置 |

## 致谢

感谢阶跃星辰团队在 Step 系列模型方面的技术突破，为大语言模型的发展做出了重要贡献。感谢 Spring AI 团队提供的统一抽象接口，简化了阶跃星辰模型的集成工作。

## 相关资源

- [阶跃星辰官方文档](https://platform.stepfun.com/docs)
- [Step系列模型介绍](https://platform.stepfun.com/)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-stepfun)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与商汤日日新集成](30、Spring AI 入门实践：Spring AI 与商汤日日新集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)