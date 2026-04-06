# Spring AI 入门实践：Spring AI 与 LLMs Free API 集成

## 概述

LLMs Free API 是由 LLM Red Team 开源组织开发的免费 API 聚合服务，提供了对多个大模型平台的免费访问接口。Spring AI 提供了对 LLMs Free API 的集成支持，使得开发者可以免费使用 Kimi、通义千问、智谱清言、阶跃星辰等模型。

## 重要声明

**⚠️ 重要提示：**
- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！
- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！
- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！

## LLM Red Team 简介

**LLM Red Team 意为 LLM 大模型红队，大模型应用发展速度超乎了所有人的预料，在这样的表象下是日益严重的安全风险。**

该组织成立的愿景是通过各厂商大模型应用中已公开的信息挖掘潜在的安全问题并公开一些技术细节。

### 已公开的仓库

- Moonshot AI（Kimi.ai）接口转API [kimi-free-api](https://github.com/LLM-Red-Team/kimi-free-api)
- 阶跃星辰 (跃问StepChat) 接口转API [step-free-api](https://github.com/LLM-Red-Team/step-free-api)
- 阿里通义 (Qwen) 接口转API [qwen-free-api](https://github.com/LLM-Red-Team/qwen-free-api)
- ZhipuAI (智谱清言) 接口转API [glm-free-api](https://github.com/LLM-Red-Team/glm-free-api)
- 秘塔AI (metaso) 接口转API [metaso-free-api](https://github.com/LLM-Red-Team/metaso-free-api)
- 聆心智能 (Emohaa) 接口转API [emohaa-free-api](https://github.com/LLM-Red-Team/emohaa-free-api)

## 准备工作

### 1. 部署 Free API 服务

首先，您需要部署相应的 Free API 服务：

1. 选择需要的 Free API 项目（如 kimi-free-api）
2. 按照项目 README 部署服务
3. 获取服务的访问地址（如 http://localhost:8000）

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>com.github.hiwepy</groupId>
        <artifactId>spring-ai-starter-model-llms-free-api</artifactId>
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

### 3. 配置 LLMs Free API 连接

在 `application.properties` 文件中配置 Free API 相关设置：

```properties
# LLMs Free API 配置
spring.ai.llms-free-api.base-url=http://localhost:8000/v1
spring.ai.llms-free-api.api-key=your-api-key

# Chat 模型配置
spring.ai.llms-free-api.chat.enabled=true
spring.ai.llms-free-api.chat.options.model=kimi
```

## 核心功能

### 1. 文本生成

使用 LLMs Free API 进行文本生成：

```java
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

## 完整示例

### 运行应用

1. 先启动对应的 Free API 服务
2. 配置好 `application.properties` 中的服务地址
3. 启动应用：

```bash
cd spring-ai-llmsfreeapi
mvn spring-boot:run
```

4. 访问 API 端点：
   - `GET /v1/generate?message=你好` - 文本生成

## 最佳实践

1. **仅供学习**：仅用于学习研究，不要用于生产环境
2. **遵守条款**：遵守各平台的使用条款
3. **合理使用**：避免对官方服务造成压力
4. **风险自担**：使用风险由使用者自行承担

## 相关资源

- [LLM Red Team GitHub](https://github.com/LLM-Red-Team)
- [spring-ai-llms-free-api-spring-boot-starter](https://github.com/hiwepy/spring-ai-llms-free-api-spring-boot-starter)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-llmsfreeapi)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 Oracle Cloud + Cohere 集成](37、Spring AI 入门实践：Spring AI 与 Oracle Cloud + Cohere 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)