# 6、Spring AI 入门实践：Spring AI 图片生成（Image Generation API）

## 一、项目概述

图片生成功能允许开发者通过文本描述来生成高质量的图像。Spring AI 提供了统一的图片生成接口，支持多种图像生成模型。本文将介绍如何使用 Spring AI 集成 Stability AI 进行图片生成，包括简单生成、自定义选项、批量生成等功能。

### 核心功能

- **文本到图像**：通过描述生成图像
- **自定义选项**：配置尺寸、质量、风格等
- **批量生成**：一次请求生成多张图片
- **图像编辑**：基于现有图片进行编辑
- **图像变体**：生成现有图片的变体
- **异步处理**：支持异步生成任务

### 适用场景

- 内容创作与设计
- 原型快速生成
- 教育培训材料制作
- 创意广告设计
- 游戏美术素材生成

## 二、图片生成简介

图片生成技术通过深度学习模型将文本描述转换为视觉图像。Spring AI 提供了统一的抽象层，使得切换不同的图像生成提供商变得简单。

### 常用图像生成模型

| 模型 | 提供商 | 特点 |
|------|--------|------|
| Stable Diffusion | Stability AI | 开源、可自定义 |
| DALL-E | OpenAI | 高质量、易用 |
| Midjourney | Midjourney | 艺术风格强 |
| Imagen | Google | 真实感强 |

### 核心概念

| 概念 | 说明 |
|------|------|
| ImageModel | 图像生成的统一抽象接口 |
| ImagePrompt | 封装文本提示和生成选项 |
| ImageResponse | 包含生成结果和元数据 |
| ImageOptions | 配置生成参数（尺寸、质量等） |

## 三、环境准备          

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Stability AI API 密钥

### 3.2 获取 API 密钥

访问 [Stability AI](https://platform.stability.ai/) 注册账户并获取 API 密钥。

## 四、项目结构

### 4.1 标准项目结构

```
spring-ai-stabilityai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── partmeai/
│   │   │               └── stabilityai/
│   │   │                   ├── SpringAiStabilityAiApplication.java
│   │   │                   └── controller/
│   │   │                       └── ChatController.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

### 4.2 核心类说明

| 类名 | 职责 |
|------|------|
| `SpringAiStabilityAiApplication` | 主应用类 |
| `ChatController` | REST API 控制器 |

## 五、核心配置

### 5.1 Maven 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.6</version>
    </parent>
    
    <groupId>com.github.partmeai</groupId>
    <artifactId>spring-ai-stabilityai</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    
    <properties>
        <java.version>17</java.version>
        <spring-ai.version>1.1.4</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-stability-ai</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>${spring-ai.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 5.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-stabilityai
  
  ai:
    stabilityai:
      api-key: ${STABILITYAI_API_KEY:your-api-key}
      image:
        enabled: true
        options:
          model: stable-diffusion-xl
          width: 1024
          height: 1024
          n: 1

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 主应用类

```java
package com.github.partmeai.stabilityai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiStabilityAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAiStabilityAiApplication.class, args);
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.stabilityai.controller;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class ChatController {
    
    private final StabilityAiImageModel imageModel;
    
    public ChatController(StabilityAiImageModel imageModel) {
        this.imageModel = imageModel;
    }
    
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam String message) {
        ImagePrompt imagePrompt = new ImagePrompt(message);
        ImageResponse response = imageModel.call(imagePrompt);
        Image image = response.getResult().getOutput();
        
        return Map.of(
            "url", image.getUrl(),
            "revisedPrompt", image.getRevisedPrompt()
        );
    }
    
    @PostMapping("/generate")
    public Map<String, Object> generateWithOptions(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        Integer width = (Integer) request.getOrDefault("width", 1024);
        Integer height = (Integer) request.getOrDefault("height", 1024);
        Integer n = (Integer) request.getOrDefault("n", 1);
        
        StabilityAiImageOptions options = StabilityAiImageOptions.builder()
                .withModel("stable-diffusion-xl")
                .withWidth(width)
                .withHeight(height)
                .withN(Math.min(n, 4))
                .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        
        List<Map<String, Object>> results = response.getResults().stream()
                .map(result -> {
                    Image image = result.getOutput();
                    return Map.<String, Object>of(
                        "url", image.getUrl(),
                        "revisedPrompt", image.getRevisedPrompt()
                    );
                })
                .collect(Collectors.toList());
        
        return Map.of(
            "count", results.size(),
            "images", results
        );
    }
}
```

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 简单生成 | GET | `/v1/generate` | 通过 URL 参数生成图片 |
| 选项生成 | POST | `/v1/generate` | 带自定义选项的图片生成 |

### 7.2 接口使用示例

#### 简单生成

```bash
curl -G "http://localhost:8080/v1/generate" \
  --data-urlencode "message=A red apple on white background"
```

响应：
```json
{
  "url": "https://...",
  "revisedPrompt": "A red apple on white background"
}
```

#### 自定义选项生成

```bash
curl -X POST http://localhost:8080/v1/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "A beautiful sunset over the ocean",
    "width": 1024,
    "height": 768,
    "n": 2
  }'
```

响应：
```json
{
  "count": 2,
  "images": [
    {
      "url": "https://...",
      "revisedPrompt": "..."
    }
  ]
}
```

## 八、部署方式

### 8.1 本地运行

```bash
cd spring-ai-stabilityai
export STABILITYAI_API_KEY=your-api-key
mvn spring-boot:run
```

### 8.2 打包部署

```bash
mvn clean package -DskipTests
export STABILITYAI_API_KEY=your-api-key
java -jar target/spring-ai-stabilityai-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 Python 客户端

```python
import requests
import urllib.parse

class ImageGenerationClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def generate(self, prompt):
        encoded_prompt = urllib.parse.quote(prompt)
        response = requests.get(
            f"{self.base_url}/v1/generate",
            params={"message": encoded_prompt}
        )
        return response.json()
    
    def generate_with_options(self, prompt, width=1024, height=1024, n=1):
        data = {
            "prompt": prompt,
            "width": width,
            "height": height,
            "n": n
        }
        response = requests.post(
            f"{self.base_url}/v1/generate",
            json=data
        )
        return response.json()

client = ImageGenerationClient()

# 简单生成
result = client.generate("一只可爱的猫咪")
print(f"图片 URL: {result['url']}")

# 自定义选项
result = client.generate_with_options(
    "美丽的山水风景",
    width=1024,
    height=768,
    n=2
)
print(f"生成了 {result['count']} 张图片")
```

### 9.2 最佳实践

1. **提示词设计**：描述越具体，生成效果越好
2. **尺寸选择**：根据用途选择合适的宽高比
3. **批量控制**：一次生成不超过4张图片
4. **安全检查**：对提示词进行合规性检查
5. **成本控制**：根据预算合理使用API

## 十、运行项目

### 10.1 前置检查

```bash
# 设置 API 密钥
export STABILITYAI_API_KEY=your-api-key
```

### 10.2 启动应用

```bash
cd spring-ai-stabilityai
mvn spring-boot:run
```

### 10.3 简单测试

```bash
curl -G "http://localhost:8080/v1/generate" \
  --data-urlencode "message=Hello world"
```

## 十一、常见问题

### 11.1 生成质量问题

**Q: 生成的图片质量不高怎么办？**

- 提供更详细和具体的描述
- 选择合适的模型和尺寸
- 调整生成参数（如果支持）
- 参考提示词设计指南

**Q: 如何生成特定风格的图片？**

在提示词中明确说明风格，例如：
- "印象派风格的风景画"
- "赛博朋克风格的城市夜景"
- "水彩画风格的花卉"

### 11.2 API 使用问题

**Q: API 调用失败怎么办？**

- 检查 API 密钥是否正确设置
- 确认网络连接正常
- 查看是否达到使用限额
- 检查提示词是否符合要求

**Q: 如何降低 API 使用成本？**

- 使用较小的图片尺寸
- 减少批量生成数量
- 缓存常用生成结果
- 使用本地模型替代云服务

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Spring AI Image Client：https://docs.spring.io/spring-ai/reference/api/imageclient.html
- Stability AI 文档：https://platform.stability.ai/docs
- 示例模块：spring-ai-stabilityai

## 十四、致谢

感谢 Stability AI 和 Spring AI 团队提供的优秀工具，让图片生成变得如此简单易用。
