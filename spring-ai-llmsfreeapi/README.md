# spring-ai-llmsfreeapi

### 项目说明

> 该示例是 基于 [LLM Red Team](https://github.com/LLM-Red-Team) 开源组织开发的 [LLMs Free API](https://github.com/orgs/LLM-Red-Team/repositories?q=free-api) 服务 和 Spring AI 集成的 代码示例

### LLM Red Team

**LLM Red Team 意为 LLM大模型红队，大模型应用发展速度超乎了所有人的预料，在这样得表象下是日益严重的安全风险。**

该组织成立的愿景是通过各厂商大模型应用中已公开的信息挖掘潜在的安全问题并公开一些技术细节，如果影响到您应用的正常运营请联系组织负责人，我们会及时下线相关仓库。

#### 以下是 LLM Red Team 已公开的仓库：

- Moonshot AI（Kimi.ai）接口转API [kimi-free-api](https://github.com/LLM-Red-Team/kimi-free-api)
- 阶跃星辰 (跃问StepChat) 接口转API [step-free-api](https://github.com/LLM-Red-Team/step-free-api)
- 阿里通义 (Qwen) 接口转API [qwen-free-api](https://github.com/LLM-Red-Team/qwen-free-api)
- ZhipuAI (智谱清言) 接口转API [glm-free-api](https://github.com/LLM-Red-Team/glm-free-api)
- 秘塔AI (metaso) 接口转API [metaso-free-api](https://github.com/LLM-Red-Team/metaso-free-api)
- 聆心智能 (Emohaa) 接口转API [emohaa-free-api](https://github.com/LLM-Red-Team/emohaa-free-api)

### 声明

- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！
- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！
- 仅限学习研究，禁止对外提供服务或商用，避免对官方造成服务压力，否则风险自担！

### 操作流程

#### 引入 Maven

作者 为 Spring AI 与 [LLMs Free API](https://github.com/orgs/LLM-Red-Team/repositories?q=free-api) 开发了客户端 [spring-ai-llms-free-api-spring-boot-starter](https://github.com/hiwepy/spring-ai-llms-free-api-spring-boot-starter)

LLMs Free API 客户端提供 Spring Boot 自动配置。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
<dependency>
    <groupId>com.github.partmeai</groupId>
    <artifactId>spring-ai-llms-free-api-spring-boot-starter</artifactId>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
    implementation 'com.github.hiwepy:spring-ai-llms-free-api-spring-boot-starter'
}
```
