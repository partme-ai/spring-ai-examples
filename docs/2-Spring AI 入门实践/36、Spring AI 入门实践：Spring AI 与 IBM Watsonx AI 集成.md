# Spring AI 入门实践：Spring AI 与 IBM Watsonx AI 集成

> 基于 Spring AI 框架实现与 IBM Watsonx AI 的集成，提供企业级 AI 平台、多种大语言模型、文本生成、对话交互等功能，支持企业级 AI 应用开发。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 IBM Watsonx AI 的示例，展示了如何在 Java/Spring Boot 应用中使用 Watsonx 模型进行文本生成和对话。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| IBM Watsonx AI | - | IBM 企业级 AI 平台 |
| spring-ai-starter-model-watsonx-ai | - | Spring AI Watsonx 集成 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-watsonx
**本地路径**：`spring-ai-watsonx/`

### 1.4 核心功能

- ✅ 企业级平台：IBM Cloud 稳定可靠的服务
- ✅ 多模型支持：Granite 系列等多种模型
- ✅ 企业安全：企业级安全保障
- ✅ 可定制性：支持模型微调和定制
- ✅ 多语言支持：支持多种语言
- ✅ 工具集成：与企业工具链集成

---

## 二、IBM Watsonx AI 简介

### 2.1 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [IBM Watsonx 官方文档](https://www.ibm.com/products/watsonx-ai) 或 [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)。

## 三、环境准备

### 1. IBM Cloud 账号配置

首先，您需要配置 IBM Cloud 账号并获取访问权限：

1. 访问 [IBM Cloud](https://cloud.ibm.com/)
2. 注册账号并登录
3. 创建 Watsonx AI 服务实例
4. 获取 API Key 和项目ID
5. 确保账号有足够的配额

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-watsonx-ai</artifactId>
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

### 3. 配置 IBM Watsonx AI 连接

在 `application.properties` 文件中配置 Watsonx AI 相关设置：

```properties
# IBM Watsonx AI 配置
spring.ai.watsonx.api-key=你的API密钥
spring.ai.watsonx.project-id=你的项目ID
spring.ai.watsonx.url=https://us-south.ml.cloud.ibm.com

# Chat 模型配置
spring.ai.watsonx.chat.enabled=true
spring.ai.watsonx.chat.options.model=ibm/granite-13b-chat-v2
```

## 核心功能

### 1. 文本生成

使用 IBM Watsonx AI 进行文本生成：

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
    public Map<String, Object> generate(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return Map.of("generation", chatModel.call(message));
    }
}
```

## 完整示例

### 运行应用

1. 配置好 `application.properties` 中的 API Key 和项目ID
2. 启动应用：

```bash
cd spring-ai-watsonxai
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=Hello` - 文本生成

## 相关资源

- [IBM Watsonx AI 官方文档](https://cloud.ibm.com/docs/watsonx)
- [Granite 模型介绍](https://www.ibm.com/products/watsonx-ai)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-watsonxai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

---

## 四、Java 客户端示例

### 4.1 REST 客户端

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class WatsonxClient {

    private static final String BASE_URL = "http://localhost:8080/api/watsonx";
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
}
```

---

## 五、许可证

- **IBM Watsonx AI**：商业许可
- **Spring AI**：Apache 2.0
- **本项目**：Apache 2.0

---

## 六、致谢

- **感谢 IBM 团队** 提供企业级 AI 平台
- **感谢 Spring AI 团队** 提供 Watsonx 集成框架
- **感谢开源社区** 提供丰富的技术资源

## 扩展阅读

- [Spring AI 与 Stability AI 集成](35、Spring AI 入门实践：Spring AI 与 Stability AI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)