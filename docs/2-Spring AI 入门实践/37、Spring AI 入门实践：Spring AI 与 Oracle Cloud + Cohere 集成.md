# Spring AI 入门实践：Spring AI 与 Oracle Cloud + Cohere 集成

## 概述

Oracle Cloud Infrastructure (OCI) Generative AI 服务结合了 Cohere 的大语言模型，提供了强大的文本生成和嵌入能力。Spring AI 提供了对 OCI Gen AI + Cohere API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用这些模型。

## 准备工作

### 1. Oracle Cloud 账号配置

首先，您需要配置 Oracle Cloud 账号并获取访问权限：

1. 访问 [Oracle Cloud](https://cloud.oracle.com/)
2. 注册账号并登录
3. 启用 Generative AI 服务
4. 配置 API 密钥和租户ID
5. 确保账号有足够的配额

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-oci-genai</artifactId>
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

### 3. 配置 OCI Gen AI 连接

在 `application.properties` 文件中配置 OCI Gen AI 相关设置：

```properties
# Oracle Cloud 配置
spring.ai.oci.genai.tenant-id=你的租户ID
spring.ai.oci.genai.user-id=你的用户ID
spring.ai.oci.genai.fingerprint=你的指纹
spring.ai.oci.genai.private-key=file:///path/to/private_key.pem

# Cohere 模型配置
spring.ai.oci.genai.chat.enabled=true
spring.ai.oci.genai.chat.options.model=cohere.command-r-plus
```

## 核心功能

### 1. 文本生成

使用 OCI Gen AI + Cohere 进行文本生成：

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

1. 配置好 `application.properties` 中的 Oracle Cloud 凭证
2. 启动应用：

```bash
cd spring-ai-oci-genai-cohere
mvn spring-boot:run
```

3. 访问 API 端点：
   - `GET /v1/generate?message=Hello` - 文本生成

## 相关资源

- [Oracle Cloud Generative AI 文档](https://docs.oracle.com/en-us/iaas/Content/generative-ai/home.htm)
- [Cohere 模型介绍](https://cohere.com/)
- [示例项目源码](https://github.com/teachingai/spring-ai-examples/tree/main/spring-ai-oci-genai-cohere)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 与 IBM Watsonx AI 集成](36、Spring AI 入门实践：Spring AI 与 IBM Watsonx AI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)