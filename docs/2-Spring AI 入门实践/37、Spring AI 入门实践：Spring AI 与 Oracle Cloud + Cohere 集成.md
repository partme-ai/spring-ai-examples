# Spring AI 入门实践：Spring AI 与 Oracle Cloud + Cohere 集成

> 基于 Spring AI 框架实现与 Oracle Cloud Infrastructure (OCI) Generative AI + Cohere 的集成，提供企业级 AI 服务、文本生成、嵌入计算等功能，支持 Oracle 云生态集成。

---

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Oracle Cloud + Cohere 的示例，展示了如何在 Java/Spring Boot 应用中使用 OCI Gen AI 结合 Cohere 模型。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Oracle Cloud | - | OCI 云平台 |
| Cohere | - | Cohere 大语言模型 |
| spring-ai-starter-model-oci-genai | - | Spring AI OCI 集成 |

### 1.3 代码地址
**GitHub**：https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-oci-genai
**本地路径**：`spring-ai-oci-genai/`

### 1.4 核心功能

- ✅ 企业级服务：Oracle Cloud 企业级服务
- ✅ Cohere 模型：Command、Embed 等模型
- ✅ 云原生集成：与 OCI 生态深度集成
- ✅ 高可用性：支持高可用部署
- ✅ 安全保障：企业级安全保障
- ✅ 灵活部署：支持多种部署方式

---

## 二、Oracle Cloud + Cohere 简介

### 2.1 性能基准

> ⚠️ 注：性能基准数据待补充。如需性能数据，请参考 [Oracle Cloud 官方文档](https://www.oracle.com/cloud/) 或 [Cohere 官方文档](https://docs.cohere.com/)。

## 三、环境准备

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
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-oci-genai-cohere)
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

public class OCIGenAIClient {

    private static final String BASE_URL = "http://localhost:8080/api/oci";
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

- **Oracle Cloud**：商业许可
- **Cohere**：商业许可
- **Spring AI**：Apache 2.0
- **本项目**：Apache 2.0

---

## 六、致谢

- **感谢 Oracle 团队** 提供企业级云服务平台
- **感谢 Cohere 团队** 提供强大的大语言模型
- **感谢 Spring AI 团队** 提供 OCI 集成框架
- **感谢开源社区** 提供丰富的技术资源

## 扩展阅读

- [Spring AI 与 IBM Watsonx AI 集成](36、Spring AI 入门实践：Spring AI 与 IBM Watsonx AI 集成.md)
- [Spring AI 文本生成基础](1、Spring AI 入门实践：Spring AI 文本生成（Chat Completion API）.md)