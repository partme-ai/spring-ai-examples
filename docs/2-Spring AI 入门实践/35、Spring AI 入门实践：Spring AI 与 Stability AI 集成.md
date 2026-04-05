# Spring AI 入门实践：Spring AI 与 Stability AI 集成

## 概述

Stability AI 是图像生成领域的领先公司，提供了 Stable Diffusion 系列模型。Spring AI 提供了对 Stability AI API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Stable Diffusion 进行图像生成。

## 准备工作

### 1. Stability AI 账号配置

首先，您需要配置 Stability AI 账号并获取 API Key：

1. 访问 [Stability AI 平台](https://platform.stability.ai/)
2. 注册账号并登录
3. 创建 API Key
4. 确保账号有足够的余额或使用额度

### 2. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-stability-ai</artifactId>
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

### 3. 配置 Stability AI 连接

在 `application.properties` 文件中配置 Stability AI 相关设置：

```properties
# Stability AI 配置
spring.ai.stabilityai.api-key=你的API密钥
spring.ai.stabilityai.image.enabled=true
spring.ai.stabilityai.image.options.model=stable-diffusion-xl-1024-v1-0
```

## 核心功能

### 1. 图像生成

使用 Stability AI 进行图像生成：

```java
import org.springframework.ai.image.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ImageController {

    private final ImageModel imageModel;

    @Autowired
    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @PostMapping("/v1/image/generate")
    public Map<String, Object> generateImage(@RequestBody ImageRequest request) {
        ImageOptions options = ImageOptionsBuilder.builder()
                .model("stable-diffusion-xl-1024-v1-0")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(request.prompt(), options);
        ImageResponse response = imageModel.call(imagePrompt);
        String imageUrl = response.getResult().getOutput().getUrl();

        return Map.of("imageUrl", imageUrl);
    }
}
```

## 完整示例

### 运行应用

1. 配置好 `application.properties` 中的 API Key
2. 启动应用：

```bash
cd spring-ai-stabilityai
mvn spring-boot:run
```

3. 访问 API 端点：
   - `POST /v1/image/generate` - 图像生成

## 相关资源

- [Stability AI 官方文档](https://platform.stability.ai/docs)
- [Stable Diffusion 模型介绍](https://stability.ai/)
- [示例项目源码](https://github.com/partme-ai/spring-ai-examples/tree/main/spring-ai-stabilityai)
- [Spring AI 参考文档](https://docs.spring.io/spring-ai/reference/index.html)

## 扩展阅读

- [Spring AI 图片生成 API](6、Spring AI 入门实践：Spring AI 图片生成（Image Generation API）.md)
- [Spring AI 与 Google Vertex AI Gemini 集成](34、Spring AI 入门实践：Spring AI 与 Google Vertex AI Gemini 集成.md)