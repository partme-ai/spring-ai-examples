# 6、Spring AI 入门实践：Spring AI 图片生成（Image Generation API）

## 概述

Spring AI 的图片生成功能基于 OpenAI 的 DALL-E 等 API，允许开发者通过简单的 API 调用来生成图片。本文将介绍如何使用 Spring AI 进行图片生成。

## 技术栈

- **Spring Boot 3.2+**
- **Spring AI 1.1.4+**
- **OpenAI Image Generation API**

## 准备工作

### 1. 添加依赖

在 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
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

### 2. 配置 API 密钥

在 `application.properties` 中配置：

```properties
spring.ai.openai.api-key=your-api-key
spring.ai.openai.image.options.model=dall-e-3
spring.ai.openai.image.options.size=1024x1024
spring.ai.openai.image.options.quality=standard
spring.ai.openai.image.options.n=1
```

## 基本使用

### 1. 简单图片生成

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public Image generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse response = imageModel.call(imagePrompt);
        return response.getResult().getOutput();
    }
}
```

### 2. 使用自定义选项

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvancedImageGenerationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public Image generateImageWithCustomOptions(String prompt) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-3")
            .withQuality("hd")
            .withN(1)
            .withHeight(1024)
            .withWidth(1024)
            .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        return response.getResult().getOutput();
    }
}
```

## 高级功能

### 1. 批量生成

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BatchImageGenerationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public List<Image> generateMultipleImages(String prompt, int count) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-3")
            .withN(Math.min(count, 10))
            .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        
        return response.getResults().stream()
            .map(result -> result.getOutput())
            .collect(Collectors.toList());
    }
}
```

### 2. 图片编辑

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageEditPrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImageEditService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public Image editImage(Resource originalImage, String prompt) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-2")
            .withN(1)
            .build();
        
        ImageEditPrompt editPrompt = new ImageEditPrompt(originalImage, prompt, options);
        ImageResponse response = imageModel.call(editPrompt);
        
        return response.getResult().getOutput();
    }
}
```

### 3. 图片变体

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageVariationPrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImageVariationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public Image createVariation(Resource originalImage) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-2")
            .withN(1)
            .build();
        
        ImageVariationPrompt variationPrompt = new ImageVariationPrompt(originalImage, options);
        ImageResponse response = imageModel.call(variationPrompt);
        
        return response.getResult().getOutput();
    }
}
```

### 4. 异步生成

```java
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AsyncImageGenerationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    public Mono<Image> generateImageAsync(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        return Mono.fromCallable(() -> {
            return imageModel.call(imagePrompt).getResult().getOutput();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
```

## 示例代码

完整的示例代码如下：

```java
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ImageGenerationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageGenerationApplication.class, args);
    }
}

@Service
class ImageGenerationService {
    
    @Autowired
    private OpenAiImageModel imageModel;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    
    public Image generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse response = imageModel.call(imagePrompt);
        return response.getResult().getOutput();
    }
    
    public Image generateImageWithCustomOptions(String prompt, String size, String quality) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-3")
            .withQuality(quality)
            .withN(1)
            .withHeight(Integer.parseInt(size.split("x")[1]))
            .withWidth(Integer.parseInt(size.split("x")[0]))
            .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        return response.getResult().getOutput();
    }
    
    public List<Image> generateMultipleImages(String prompt, int count) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
            .withModel("dall-e-3")
            .withN(Math.min(count, 10))
            .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        
        return response.getResults().stream()
            .map(result -> result.getOutput())
            .collect(Collectors.toList());
    }
    
    public CompletableFuture<Image> generateImageAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> {
            ImagePrompt imagePrompt = new ImagePrompt(prompt);
            ImageResponse response = imageModel.call(imagePrompt);
            return response.getResult().getOutput();
        }, executor);
    }
}

@RestController
@RequestMapping("/api/image-generation")
class ImageGenerationController {
    
    @Autowired
    private ImageGenerationService service;
    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        Image image = service.generateImage(prompt);
        
        return ResponseEntity.ok(Map.of(
            "url", image.getUrl(),
            "revisedPrompt", image.getRevisedPrompt()
        ));
    }
    
    @PostMapping("/generate-custom")
    public ResponseEntity<Map<String, Object>> generateImageWithCustomOptions(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String size = request.getOrDefault("size", "1024x1024");
        String quality = request.getOrDefault("quality", "standard");
        
        Image image = service.generateImageWithCustomOptions(prompt, size, quality);
        
        return ResponseEntity.ok(Map.of(
            "url", image.getUrl(),
            "revisedPrompt", image.getRevisedPrompt()
        ));
    }
    
    @PostMapping("/generate-multiple")
    public ResponseEntity<List<Map<String, Object>>> generateMultipleImages(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        int count = (int) request.getOrDefault("count", 1);
        
        List<Image> images = service.generateMultipleImages(prompt, count);
        
        List<Map<String, Object>> result = images.stream()
            .map(image -> Map.of(
                "url", image.getUrl(),
                "revisedPrompt", image.getRevisedPrompt()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/generate-async")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> generateImageAsync(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        
        return service.generateImageAsync(prompt)
            .thenApply(image -> ResponseEntity.ok(Map.of(
                "url", image.getUrl(),
                "revisedPrompt", image.getRevisedPrompt()
            )));
    }
}
```

## 测试方法

1. **启动应用**：运行 `ImageGenerationApplication` 类
2. **生成图片**：
   ```bash
   curl -X POST http://localhost:8080/api/image-generation/generate \
     -H "Content-Type: application/json" \
     -d '{"prompt":"一只可爱的猫咪在花园里玩耍"}'
   ```
3. **自定义选项生成**：
   ```bash
   curl -X POST http://localhost:8080/api/image-generation/generate-custom \
     -H "Content-Type: application/json" \
     -d '{"prompt":"美丽的日落风景","size":"1024x1024","quality":"hd"}'
   ```
4. **批量生成**：
   ```bash
   curl -X POST http://localhost:8080/api/image-generation/generate-multiple \
     -H "Content-Type: application/json" \
     -d '{"prompt":"春天的花朵","count":3}'
   ```

## 总结

Spring AI 的图片生成功能提供了简单易用的 API，支持基本的图片生成、自定义选项、批量生成和异步生成等功能。通过合理配置和使用这些功能，可以快速构建各种基于图片生成的应用。

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [OpenAI Image Generation API](https://platform.openai.com/docs/guides/images)

## 扩展阅读

本文档内容基于 Spring AI 1.1.x 版本。有关图片生成的更多详细信息和更新，请参考以下资源：

### 官方文档
- [Spring AI Image Client / Image Model](https://docs.spring.io/spring-ai/reference/api/imageclient.html) - 图像模型接口详解

### 示例模块
- **`spring-ai-stabilityai`** - 本仓库中的图片生成示例模块
  - 主类：`com.github.teachingai.stabilityai.SpringAiStabilityAiApplication`
  - 控制器：`com.github.teachingai.stabilityai.controller.ChatController`（历史遗留类名，实际为图像生成）
  - 路径：`/v1/generate`（图像生成接口）

### 核心概念
- **`ImageModel`**：图像生成的统一抽象，厂商实现由 Starter 注入
- **`ImagePrompt`**：封装文本提示与图像选项
- **`ImageResponse`**：包含生成结果与元数据

### 运行与验证
```bash
# 进入示例模块目录
cd spring-ai-stabilityai

# 启动应用
mvn spring-boot:run

# 测试图片生成
curl -G "http://localhost:8080/v1/generate" --data-urlencode "message=A red apple on white background"
```

### 最佳实践
1. **提示词验证**：对提示词长度与合规性做校验
2. **生产环境安全**：对生图接口做限流与鉴权
3. **版本一致性**：与文本模块版本一致，依赖父 POM 的 `spring-ai.version`
4. **API 密钥安全**：勿将密钥提交到版本库，建议使用环境变量