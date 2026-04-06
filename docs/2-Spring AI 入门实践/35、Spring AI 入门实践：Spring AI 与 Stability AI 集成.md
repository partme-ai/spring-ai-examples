# 35、Spring AI 入门实践：Spring AI 与 Stability AI 集成

## 一、项目概述

Stability AI 是图像生成领域的领先公司，提供了 Stable Diffusion 系列模型。Spring AI 提供了对 Stability AI API 的集成支持，使得开发者可以轻松地在 Spring 应用中使用 Stable Diffusion 进行图像生成。

### 核心功能

- **Stable Diffusion 系列**：SDXL、SD 3 等多种图像生成模型
- **强大的图像生成能力**：支持文本到图像、图像到图像、图像编辑等
- **多种风格支持**：支持多种艺术风格和图像类型
- **图像编辑**：支持修图、扩展、补全等功能
- **统一 API**：Spring AI 提供的统一抽象接口

### 适用场景

- 图像创作和艺术设计
- 产品原型和概念图
- 图像编辑和修图
- 多模态内容生成
- 创意内容生成

## 二、Stability AI 简介

Stability AI 是图像生成领域的领先公司，提供 Stable Diffusion 系列大模型。

### 可用模型对比

| 模型 | 特点 | 适用场景 |
|------|------|---------|
| Stable Image Ultra | 最高质量，专业级别 | 专业图像创作、商业用途 |
| Stable Image Core | 均衡性能，高质量 | 通用高质量图像生成 |
| Stable Diffusion 3.5 Large | 最新旗舰，能力强 | 复杂场景、高质量需求 |
| Stable Diffusion 3.5 Large Turbo | 旗舰+快速，平衡 | 快速高质量生成 |
| Stable Diffusion 3.5 Medium | 中等规模，性价比 | 通用场景、平衡性能 |
| Stable Diffusion 3.5 Flash | 极速，低延迟 | 快速生成、实时应用 |

### 核心特性

| 特性 | 说明 |
|------|------|
| 高质量图像 | 支持高分辨率图像生成 |
| 多种风格 | 支持多种艺术风格 |
| 图像编辑 | 支持修图、扩展等 |
| 开源模型 | 社区生态丰富 |

## 三、环境准备

### 3.1 开发环境

确保已安装：
- JDK 17+
- Maven 3.8+
- IntelliJ IDEA 或 Eclipse
- Stability AI API 密钥

### 3.2 获取 Stability AI API 密钥

1. **创建账号**
   - 访问 Stability AI 平台：https://platform.stability.ai/
   - 可以使用用户名和密码注册，也可以使用 Google 账号
   - 使用 Google 账号社交登录可获得 25 个免费积分

2. **获取 API 密钥**
   - 登录后进入 API Keys 页面
   - 创建新的 API 密钥
   - 所有 API 都使用相同的认证机制：通过 Authorization 头传递 API 密钥
   - ⚠️ 重要：API 密钥需要保密！如果怀疑密钥泄露，先在 API Keys 页面创建新密钥，然后删除旧密钥

3. **积分管理**
   - 调用 API 会消耗积分，积分是 API 调用的计费单位
   - 不同模型和模式的积分消耗量不同
   - 免费积分用完后，可通过 Billing 仪表板购买额外积分，价格为 $1 USD 每 100 积分
   - 可参考 Pricing 页面了解具体模型的积分使用情况

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
│   │   │                   ├── controller/
│   │   │                   │   └── ImageController.java
│   │   │                   └── service/
│   │   │                       └── ImageService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 五、核心配置

### 5.1 Maven 依赖

```xml
<dependencies>
    <!-- For Spring AI Common -->
    <dependency>
        <groupId>com.github.partmeai</groupId>
        <artifactId>spring-ai-common</artifactId>
        <version>${revision}</version>
    </dependency>
    <!-- For Chat Completion & Embedding -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-stability-ai</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-autoconfigure-retry</artifactId>
    </dependency>
    <!-- For Chat Memory -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
    </dependency>
    <!-- For Vector Store  -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-azure-cosmos-db</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-cassandra</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-couchbase</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-elasticsearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-gemfire</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mongodb-atlas</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-neo4j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-opensearch</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-oracle</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-qdrant</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-typesense</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-weaviate</artifactId>
    </dependency>
    <!-- For Log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <!-- For Knife4j -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
    <!-- For Embed Undertow -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>test</scope>
    </dependency>
    <!-- For Testcontainers : https://testcontainers.com/ -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>ollama</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>typesense</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 5.2 应用配置

```yaml
spring:
  application:
    name: spring-ai-stabilityai
  
  ai:
    stabilityai:
      api-key: ${STABILITY_API_KEY:your-api-key}
      image:
        enabled: true
        options:
          model: stable-diffusion-xl-1024-v1-0

server:
  port: 8080
```

## 六、代码实现详解

### 6.1 图像生成服务

```java
package com.github.partmeai.stabilityai.service;

import org.springframework.ai.image.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImageService {
    
    private final ImageModel imageModel;
    
    public ImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }
    
    public Map<String, Object> generateImage(String prompt) {
        ImageOptions options = ImageOptionsBuilder.builder()
                .model("stable-diffusion-xl-1024-v1-0")
                .build();
        
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageModel.call(imagePrompt);
        String imageUrl = response.getResult().getOutput().getUrl();
        
        return Map.of(
                "prompt", prompt,
                "imageUrl", imageUrl
        );
    }
}
```

### 6.2 REST 控制器

```java
package com.github.partmeai.stabilityai.controller;

import com.github.partmeai.stabilityai.service.ImageService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageController {
    
    private final ImageService imageService;
    
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    
    @PostMapping("/image/generate")
    public Map<String, Object> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        return imageService.generateImage(prompt);
    }
}
```

### 6.3 主应用类

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

## 七、API 接口说明

### 7.1 接口总览

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 图像生成 | POST | `/api/image/generate` | 文本到图像生成 |

### 7.2 接口使用示例

#### 图像生成

```bash
curl -X POST http://localhost:8080/api/image/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "一个美丽的日落风景"}'
```

## 八、部署方式

### 8.1 本地运行

```bash
export STABILITY_API_KEY=your-api-key
cd spring-ai-stabilityai
mvn spring-boot:run
```

### 8.2 打包部署

```bash
export STABILITY_API_KEY=your-api-key
mvn clean package -DskipTests
java -jar target/spring-ai-stabilityai-1.0.0-SNAPSHOT.jar
```

## 九、使用示例

### 9.1 最佳实践

1. **模型选择**：
   - 专业/商业用途：Stable Image Ultra
   - 通用高质量：Stable Image Core
   - 复杂场景：Stable Diffusion 3.5 Large
   - 快速高质量：Stable Diffusion 3.5 Large Turbo
   - 平衡性能：Stable Diffusion 3.5 Medium
   - 极速/实时：Stable Diffusion 3.5 Flash

2. **提示词工程**：清晰描述图像内容，添加风格和质量要求

3. **应用场景**：创意设计、概念图、产品原型

### 9.2 Python SDK 使用示例

Stability AI 提供了 Python gRPC SDK，可以直接用于图像生成。

#### 基础安装

```bash
pip install stability-sdk
```

#### 开发者安装（可选）

如果需要访问最新的前沿特性，可以从源代码构建：

```bash
# 1. 创建并激活 Python 虚拟环境
python3 -m venv venv
source venv/bin/activate

# 2. 克隆 stability-sdk 仓库
git clone --recurse-submodules https://github.com/Stability-AI/stability-sdk
cd stability-sdk

# 3. 安装 Python SDK
pip install .

# 或者以可编辑模式安装（用于开发）
pip install -e .

# 4. 设置环境变量
export STABILITY_HOST=grpc.stability.ai:443
export STABILITY_KEY=yourkeyhere

# 5. 测试设置
python -m stability_sdk generate "A stunning house."
```

#### Text-to-Image Python 示例

```python
import os
import io
import warnings
from PIL import Image
from stability_sdk import client
import stability_sdk.interfaces.gooseai.generation.generation_pb2 as generation

# 设置环境变量
os.environ['STABILITY_HOST'] = 'grpc.stability.ai:443'
os.environ['STABILITY_KEY'] = 'key-goes-here'

# 建立 API 连接
stability_api = client.StabilityInference(
    key=os.environ['STABILITY_KEY'],
    verbose=True,
    engine="stable-diffusion-xl-1024-v1-0",
)

# 设置生成参数，保存生成的图像，如果安全过滤器被触发则发出警告
answers = stability_api.generate(
    prompt="expansive landscape rolling greens with gargantuan yggdrasil, intricate world-spanning roots towering under a blue alien sky, masterful, ghibli",
    seed=4253978046,
    steps=50,
    cfg_scale=8.0,
    width=1024,
    height=1024,
    samples=1,
    sampler=generation.SAMPLER_K_DPMPP_2M
)

# 处理响应
for resp in answers:
    for artifact in resp.artifacts:
        if artifact.finish_reason == generation.FILTER:
            warnings.warn(
                "Your request activated the API's safety filters and could not be processed."
                "Please modify the prompt and try again.")
        if artifact.type == generation.ARTIFACT_IMAGE:
            img = Image.open(io.BytesIO(artifact.binary))
            img.save(str(artifact.seed)+ ".png")
```

### 9.3 API 参数详解

| 参数 | 默认值 | 典型值 | 允许范围 | 影响定价? | 说明 |
|------|--------|--------|---------|----------|------|
| prompt | 无 | - | - | 否 | 接受单个或多个带权重的提示词 |
| height | 512 | 512 | 见下文 | 是 | 高度（像素），像素限制为 1048576 |
| width | 512 | 512 | 见下文 | 是 | 宽度（像素），像素限制为 1048576 |
| steps | 30 | 30-50 | 10-150 | 是 | 扩散步数 |
| num_samples | 1 | 1-9 | 1-10 | 是 | 生成图像数量 |
| cfg_scale | 7.0 | 4-14 | 1-35 | 否 | CFG 缩放，控制与提示词的匹配程度 |
| engine | stable-diffusion-xl-1024-v0-9 | 见下文 | 见下文 | 见下文 | 引擎（模型）选择 |
| sampler | k_dpmpp_2m | k_dpmpp_2m | 见注释 | 否 | 采样器选择 |
| seed | 0（随机） | - | 0-2147483647 | 否 | 随机潜空间噪声生成种子 |

#### 尺寸建议

对于 512px 模型，推荐使用 64px 增量的宽高比，常见比例包括 1536 x 512 和 1536 x 384。

对于 768px 模型，推荐 1536 x 640 和 1024 x 576。

#### 可用引擎

当前可用引擎：
- stable-diffusion-xl-1024-v0-9
- stable-diffusion-xl-1024-v1-0
- esrgan-v1-x2plus

#### 可用采样器

- ddim
- plms
- k_euler
- k_euler_ancestral
- k_heun
- k_dpm_2
- k_dpm_2_ancestral
- k_dpmpp_2s_ancestral
- k_dpmpp_2m
- k_dpmpp_sde

### 9.4 模型推荐表

| 场景 | 推荐模型 |
|------|----------|
| 专业/商业用途 | Stable Image Ultra |
| 通用高质量 | Stable Image Core |
| 复杂场景 | Stable Diffusion 3.5 Large |
| 快速高质量 | Stable Diffusion 3.5 Large Turbo |
| 平衡性能 | Stable Diffusion 3.5 Medium |
| 极速/实时 | Stable Diffusion 3.5 Flash |

## 十、运行项目

### 10.1 启动应用

```bash
export STABILITY_API_KEY=your-api-key
cd spring-ai-stabilityai
mvn spring-boot:run
```

### 10.2 简单测试

```bash
curl -X POST http://localhost:8080/api/image/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "A beautiful sunset landscape"}'
```

## 十一、常见问题

### 11.1 API 密钥问题

**Q: 提示 API 密钥无效怎么办？**

- 确认 API 密钥正确且有效
- 检查环境变量或配置文件中的密钥设置
- 确保账号有足够的余额或使用额度
- 查看 Stability AI 平台的使用记录

## 十二、许可证

本项目采用 Apache License 2.0 许可证。

## 十三、参考资源

- Stability AI 官方文档：https://platform.stability.ai/docs
- Spring AI Stability AI：参考官方文档
- 示例模块：spring-ai-stabilityai

## 十四、致谢

感谢 Stability AI 团队和 Spring AI 团队提供的优秀工具。
