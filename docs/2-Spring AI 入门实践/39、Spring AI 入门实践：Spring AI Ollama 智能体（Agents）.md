# Spring AI 入门实践：Spring AI Ollama 智能体（Agents）

## 概述

智能体（Agents）是 Spring AI 的高级功能，允许大语言模型自主规划和执行多步骤任务。通过 Ollama，我们可以在本地运行各种大型语言模型，并利用 Spring AI 的智能体框架构建复杂的 AI 应用。

## 准备工作

### 1. Ollama 安装与配置

首先，您需要在本地安装和运行 Ollama：

1. 访问 [Ollama 官网](https://ollama.com/)
2. 下载并安装适合您操作系统的版本
3. 启动 Ollama 服务
4. 拉取所需的模型：

```bash
ollama pull llama2
ollama pull mistral
ollama pull qwen3.5
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
spring.ai.ollama.chat.options.model=llama2
spring.ai.ollama.chat.options.temperature=0.8
```

## 核心功能

### 1. 智能体基础

智能体是能够自主执行任务的 AI 系统：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AgentController {

    private final ChatModel chatModel;

    @Autowired
    public AgentController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/agent/execute")
    public String executeTask(@RequestBody String task) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一个智能助手，能够自主规划和执行任务。"),
            new UserMessage(task)
        ));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
```

### 2. 多步骤任务执行

智能体可以分解复杂任务：

```java
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class TaskPlanningService {

    private final ChatModel chatModel;

    public String planAndExecute(String complexTask) {
        // 第一步：分解任务
        String planPrompt = "请将以下任务分解为具体步骤：" + complexTask;
        String plan = chatModel.call(planPrompt);

        // 第二步：执行每个步骤
        String executionPrompt = "根据以下计划执行任务：" + plan;
        return chatModel.call(executionPrompt);
    }
}
```

## 完整示例

### 项目结构

```
spring-ai-ollama-agents/
├── src/main/java/com/github/teachingai/ollama/
│   ├── controller/
│   │   ├── ChatController.java
│   │   └── EmbeddingController.java
│   ├── request/
│   │   └── ApiRequest.java
│   ├── router/
│   │   └── RouterFunctionConfig.java
│   ├── SpringAiOllamaApplication.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
└── src/main/resources/
    ├── application.properties
    └── conf/
        └── log4j2-dev.xml
```

### 运行应用

1. 确保 Ollama 服务正在运行
2. 启动应用：

```bash
cd spring-ai-ollama-agents
mvn spring-boot:run
```

3. 访问 API 端点：
   - `POST /agent/execute` - 执行智能体任务

## 最佳实践

1. **任务分解**：将复杂任务分解为可管理的小任务
2. **上下文管理**：合理管理对话上下文，避免超出模型限制
3. **错误处理**：实现重试机制和错误恢复
4. **性能优化**：使用异步处理提高响应速度
5. **模型选择**：根据任务复杂度选择合适的模型

## 相关资源

- [Ollama 官方文档](https://ollama.com/docs)
- [Spring AI Agents 文档](https://docs.spring.io/spring-ai/reference/api/agents.html)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-ollama-agents)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 使用 Ollama Chat](10、Spring AI 入门实践：Spring AI 使用 Ollama Chat.md)
- [Spring AI 工具调用](4、Spring AI 入门实践：Spring AI 工具调用（Tool Calling）.md)
- [Spring AI 智能体（Agents）](12、Spring AI 入门实践：Spring AI 智能体（Agents）.md)