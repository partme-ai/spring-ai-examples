# Spring AI 增强扩展：Spring AI 集成 PaddleOCR-VL 本地部署

> 基于 Spring AI + Ollama/vLLM 实现 PaddleOCR-VL 的本地化文档解析服务，提供 RESTful API 接口，支持文本识别、表格提取、公式识别、图表理解和多语言文档处理等功能。

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 PaddleOCR-VL 视觉语言模型的示例，展示了如何在 Java/Spring Boot 应用中实现本地化的文档解析服务。PaddleOCR-VL 是百度 PaddlePaddle 团队发布的超紧凑视觉语言模型，专门针对文档解析任务优化。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| Ollama / vLLM | - | 模型推理服务 |
| PaddleOCR-VL | 0.9B/1.5B | 超紧凑视觉语言模型 |

### 1.3 核心功能

- ✅ **文档解析**：复杂文档转 Markdown、JSON、HTML
- ✅ **多元素识别**：文本、表格、公式、图表等元素识别
- ✅ **多语言支持**：支持 109 种语言文档处理
- ✅ **动态分辨率**：NaViT 风格的动态高分辨率视觉编码器
- ✅ **RESTful API**：标准化接口设计，支持批量处理
- ✅ **Swagger 文档**：在线 API 文档和交互式测试

---

## 二、PaddleOCR-VL 模型简介

> 本节内容来自 [ModelScope PaddleOCR-VL-1.5](https://www.modelscope.cn/models/PaddlePaddle/PaddleOCR-VL-1.5) 官方页面。

### 2.1 模型介绍

**PaddleOCR-VL-1.5: 0.9B 参数 SOTA 文档解析模型，全球首个支持异形框定位**

> **发布日期**：2026年1月29日，百度 PaddlePaddle 团队发布了 PaddleOCR-VL-1.5，这是一个多任务的 0.9B 视觉语言模型，专为鲁棒的真实世界文档解析而设计。

PaddleOCR-VL-1.5 是百度 PaddlePaddle 团队发布的新一代超紧凑视觉语言模型（VLM），在仅 0.9B 参数下实现了多项 SOTA 性能。该模型创新性地支持文档元素的异形框定位，解决了传统 OCR 模型在移动拍照、扫描件变形、复杂光照等真实场景中因文档形变导致的识别失败问题。

模型核心特性包括：
- **0.9B 超紧凑架构**：参数极少，资源消耗极低，适合边缘设备部署
- **异形框定位**：全球首个支持异形框定位的文档解析模型，攻克曲面文档识别难题
- **多场景适应**：在扫描、弯折、屏幕拍照、光线变化、倾斜等 5 大场景表现卓越
- **多元素识别**：支持文本、表格、公式、图表、印章等复杂元素识别
- **多语言支持**：支持 109 种语言，包括中、英、日、韩、俄、阿拉伯语等

PaddleOCR-VL-1.5 集成了 NaViT 风格的动态分辨率视觉编码器和 ERNIE-4.5-0.3B 语言模型，在保持轻量化的同时实现了全面的 SOTA 性能。

### 2.2 核心特性

| 特性 | 说明 |
|------|------|
| **超紧凑架构** | 仅 0.9B 参数，资源消耗极低，适合边缘设备部署 |
| **异形框定位** | 全球首个支持异形框定位的文档解析模型，攻克曲面文档识别难题 |
| **多场景适应** | 在扫描、弯折、屏幕拍照、光线变化、倾斜等 5 大场景表现卓越 |
| **多语言支持** | 支持 109 种语言，包括中、英、日、韩、俄、阿拉伯语等 |
| **动态分辨率** | NaViT 风格的动态高分辨率视觉编码器，适应不同尺寸文档 |
| **多元素识别** | 支持文本、表格、公式、图表、印章等复杂元素识别 |
| **SOTA 性能** | 在 OmniDocBench、Real5-OmniDocBench 等基准测试中表现 SOTA |

### 2.3 支持模式

PaddleOCR-VL 系列提供多个版本以满足不同场景需求：

**PaddleOCR-VL-1.5 (0.9B 参数)**
- **模型大小**: ~2GB (FP16)，~1.2GB (INT8 量化)
- **推理速度**: 快速，适合实时应用和边缘设备
- **主要特性**: 异形框定位、多场景适应、SOTA 性能
- **适用场景**: 移动端部署、边缘计算、实时文档解析

**PaddleOCR-VL-0.9B (原版)**
- **模型大小**: ~2GB，快速推理
- **适用场景**: 资源受限环境，基础文档解析需求

**PaddleOCR-VL-1.5B (1.5B 参数)**
- **模型大小**: ~3GB，中等精度
- **适用场景**: 通用文档解析，需要更高精度的场景

> **注**: PaddleOCR-VL-1.5 是百度 PaddlePaddle 团队最新发布的版本，在保持 0.9B 参数的同时实现了多项 SOTA 性能，特别是异形框定位能力。

### 2.4 主要 Prompt 格式

PaddleOCR-VL 支持多种 Prompt 格式：

```python
PROMPTS = {
    "ocr": "OCR:",                           # 文本识别
    "table": "Table Recognition:",           # 表格识别
    "formula": "Formula Recognition:",       # 公式识别
    "chart": "Chart Recognition:",           # 图表识别
    "markdown": "Convert to markdown:",      # 转换为 Markdown
}
```

### 2.5 使用要求

**环境要求**：
- **GPU 推理**：CUDA 11.8+ / CUDA 12.6+
- **Python 版本**：Python 3.8+
- **PaddlePaddle 框架**：3.2.1 或更高版本
- **特殊要求**：需要安装特殊版本的 safetensors
- **macOS 用户**：建议使用 Docker 设置环境

#### 安装依赖

根据 ModelScope 官方指南，安装步骤如下：

**1. 安装 PaddlePaddle GPU 版本 (CUDA 12.6)**

```bash
# 以下命令安装 CUDA 12.6 版本的 PaddlePaddle
# 对于其他 CUDA 版本和 CPU 版本，请参考 https://www.paddlepaddle.org.cn/en/install/quick?docurl=/documentation/docs/en/develop/install/pip/linux-pip_en.html
python -m pip install paddlepaddle-gpu==3.2.1 -i https://www.paddlepaddle.org.cn/packages/stable/cu126/
```

**2. 安装 PaddleOCR 文档解析功能**

```bash
python -m pip install -U "paddleocr[doc-parser]"
```

**3. 确保安装正确版本**

请确保安装 PaddlePaddle 框架版本 3.2.1 或以上，以及特殊版本的 safetensors。

### 2.6 基本使用示例

根据 ModelScope 官方文档，PaddleOCR-VL-1.5 提供多种使用方式：

#### 方式一：命令行接口 (CLI) 使用

```bash
# 使用在线图片示例
paddleocr doc_parser -i https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png
```

#### 方式二：Python API 使用

```python
from paddleocr import PaddleOCRVL

# 初始化管道
pipeline = PaddleOCRVL()

# 预测文档解析结果
output = pipeline.predict("https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png")

# 处理结果
for res in output:
    res.print()                              # 打印解析结果
    res.save_to_json(save_path="output")     # 保存为 JSON 格式
    res.save_to_markdown(save_path="output") # 保存为 Markdown 格式
```

#### 方式三：通过优化的推理服务器加速 VLM 推理

为了提升推理速度，可以通过 vLLM 推理服务进行加速：

**1. 启动 VLM 推理服务器**

有两种方法启动 vLLM 推理服务：

**方法一：PaddleOCR 官方方法**
```bash
docker run \
    --rm \
    --gpus all \
    --network host \
    ccr-2vdh3abv-pub.cnc.bj.baidubce.com/paddlepaddle/paddleocr-genai-vllm-server:latest-nvidia-gpu \
    paddleocr genai_server --model_name PaddleOCR-VL-1.5-0.9B --host 0.0.0.0 --port 8080 --backend vllm
```

**方法二：vLLM 原生方法**
```bash
# 参考 vLLM 官方文档：PaddleOCR-VL Usage Guide
```

**2. 调用 PaddleOCR CLI 或 Python API**

```bash
# CLI 调用
paddleocr doc_parser \
    -i https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png \
    --vl_rec_backend vllm-server \
    --vl_rec_server_url http://127.0.0.1:8080/v1
```

```python
# Python API 调用
from paddleocr import PaddleOCRVL

pipeline = PaddleOCRVL(
    vl_rec_backend="vllm-server", 
    vl_rec_server_url="http://127.0.0.1:8080/v1"
)
output = pipeline.predict("https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png")
```

#### 方式四：使用 transformers 库进行推理

> **注意**：目前推荐使用官方方法进行推理，因为速度更快且支持页面级文档解析。以下示例代码仅支持元素级识别和文本定位。

```python
# 确保已安装 transformers v5
# python -m pip install "transformers>=5.0.0"

# 使用 transformers 库加载和推理 PaddleOCR-VL-1.5-0.9B 模型
# （具体代码请参考 ModelScope 官方文档）
```

### 2.7 使用限制

虽然 PaddleOCR-VL-1.5 在文档解析任务中表现出色，但仍存在一些限制：

1. **图像质量依赖**：低分辨率、严重模糊或过度曝光的图像可能影响识别精度
2. **复杂布局挑战**：极端复杂的文档布局（如多栏混排、嵌套表格）可能解析不完整
3. **手写体识别**：对手写体文本的识别精度低于印刷体，特别是连笔字和潦草字体
4. **特殊符号支持**：某些罕见数学符号、化学式或专业符号可能无法正确识别
5. **语言覆盖限制**：虽然支持 109 种语言，但对某些低资源语言的识别精度有限
6. **实时性限制**：在高分辨率图像（>2048x2048）上处理速度可能下降
7. **硬件要求**：GPU 推理需要至少 8GB VRAM（FP16）或 4GB VRAM（INT8 量化）

建议在实际应用前进行充分的测试和评估。

---

## 三、性能基准

### 3.1 Real5-OmniDocBench 性能

为了严格评估模型在真实世界物理变形（包括扫描伪影、倾斜、弯曲、屏幕拍照和光照变化）下的鲁棒性，百度 PaddlePaddle 团队提出了 Real5-OmniDocBench 基准。实验结果表明，PaddleOCR-VL-1.5 在这一新基准上实现了 SOTA 性能。

**Real5-OmniDocBench 测试场景**：
1. **扫描伪影 (Scanning Artifacts)**：传统扫描件识别
2. **倾斜文档 (Skew)**：角度倾斜的文档识别
3. **弯曲文档 (Warping)**：曲面、折叠文档识别
4. **屏幕拍照 (Screen Photography)**：手机拍摄的屏幕内容识别
5. **光照变化 (Illumination)**：不同光照条件下的文档识别

PaddleOCR-VL-1.5 在以上五种真实场景中均表现出卓越性能，超越了主流开源和专有模型。

### 3.2 OmniDocBench v1.5 性能

PaddleOCR-VL-1.5 在 OmniDocBench v1.5 基准测试中取得了 **94.5%** 的准确率，创造了新的最先进（SOTA）性能记录，超越了之前的 SOTA 模型 PaddleOCR-VL。

| 模型 | 参数量 | OmniDocBench v1.5 | 性能提升 |
|------|--------|-------------------|----------|
| **PaddleOCR-VL-1.5** | **0.9B** | **94.5%** | **新的 SOTA** |
| PaddleOCR-VL (前版本) | 0.9B | 92.05% | 被超越 |
| 主流开源模型 | 0.9B-7B | < 90% | 被显著超越 |

**关键改进领域**：
- **表格识别**：在复杂表格识别任务上取得显著改进
- **公式识别**：在数学公式识别任务上表现突出
- **文本识别**：在多语言和多种文本类型上表现优异

> 注：WebReference中未提供Gemini 3 Pro等模型的详细对比数据，因此此处仅基于PaddleOCR-VL系列的官方数据进行比较。

### 3.3 元素级识别性能

PaddleOCR-VL-1.5 在保持 0.9B 超紧凑架构的同时，引入了多项创新能力：

- **文本定位 (Text Spotting)**：同时进行文字行定位和识别，在所有相关指标上创造了新的 SOTA 结果
- **印章识别 (Seal Recognition)**：支持印章检测和文字提取，在印章识别任务上实现 SOTA 性能
- **异形框定位 (Irregular-shaped Localization)**：全球首个支持多边形检测的文档解析模型，能够在倾斜和弯曲的文档条件下进行准确定位
- **跨页内容处理**：
  - **自动跨页表格合并**：有效缓解长文档解析中的内容碎片问题
  - **跨页段落标题识别**：识别跨页的段落标题结构
- **多语言增强**：
  - 扩展语言覆盖范围，包括中国的藏文和孟加拉语
  - 改进罕见字符、古文字、多语言表格、下划线和复选框的识别性能
- **传统元素识别**：
  - **表格识别**：在复杂表格识别任务上取得显著改进
  - **公式识别**：在数学公式识别任务上表现突出
  - **文本识别**：在多语言和多种文本类型上表现优异
  - **图表识别**：支持 11 种图表类型，包括柱状图、折线图、饼图等

### 3.4 推理速度对比

| 模型 | 参数量 | 模型大小 | 推理速度 | 量化支持 | 适用场景 |
|------|--------|----------|----------|----------|----------|
| **PaddleOCR-VL-1.5** | 0.9B | ~2GB (FP16) | 快速 | INT8/INT4 | 移动端、边缘设备、实时应用 |
| PaddleOCR-VL-0.9B | 0.9B | ~2GB | 快速 | INT8 | 资源受限环境 |
| PaddleOCR-VL-1.5B | 1.5B | ~3GB | 中等 | INT8 | 通用场景，需要更高精度 |

---

## 四、项目结构

```
spring-ai-examples/spring-ai-ollama-ocr-paddleocr/
├── src/main/java/com/example/paddleocr/
│   ├── controller/
│   │   └── PaddleOcrController.java      # REST API 控制器
│   ├── service/
│   │   └── PaddleOcrService.java         # 业务逻辑服务
│   ├── config/
│   │   └── PaddleOcrConfig.java          # Spring AI 配置
│   └── model/
│       ├── request/
│       │   ├── OcrRequest.java           # OCR 请求
│       │   └── ProcessRequest.java       # 处理请求
│       └── response/
│           ├── OcrResponse.java          # OCR 响应
│           └── ProcessResponse.java      # 处理响应
├── src/main/resources/
│   ├── application.yml                   # 主配置文件
│   └── static/                           # 静态资源
├── pom.xml                               # Maven 配置
└── README.md                             # 项目说明
```

### 文件说明

- `controller/PaddleOcrController.java` - REST API 控制器，提供文档解析接口
- `service/PaddleOcrService.java` - 业务逻辑服务，集成 OllamaChatModel
- `config/PaddleOcrConfig.java` - Spring AI Ollama 配置
- `model/request/*.java` - 请求数据模型
- `model/response/*.java` - 响应数据模型

---

## 五、核心配置

### 5.1 application.yml

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:8000/v1
      chat:
        options:
          model: MedAIBase/PaddleOCR-VL  # 对应 ModelScope 的 PaddleOCR-VL-1.5 模型
          temperature: 0
          max_tokens: 4096
server:
  port: 8080

paddleocr:
  prompt:
    ocr: "OCR:"
    table: "Table Recognition:"
    formula: "Formula Recognition:"
    chart: "Chart Recognition:"
    markdown: "Convert to markdown:"
```

### 5.2 pom.xml 依赖

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
        <artifactId>spring-ai-starter-model-ollama</artifactId>
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

---

## 六、代码实现详解

### 6.1 控制器实现

```java
@RestController
@RequestMapping("/v1/paddleocr")
@Tag(name = "PaddleOCR-VL API", description = "PaddleOCR-VL 文档解析接口")
public class PaddleOcrController {

    @Autowired
    private PaddleOcrService paddleOcrService;

    @PostMapping("/ocr")
    @Operation(summary = "文本识别", description = "识别图片中的文本内容")
    public ResponseEntity<OcrResponse> ocr(@RequestBody OcrRequest request) {
        String result = paddleOcrService.ocr(request.getImageBase64(), request.getLanguage());
        return ResponseEntity.ok(new OcrResponse(true, result, System.currentTimeMillis()));
    }

    @PostMapping("/process")
    @Operation(summary = "文档解析", description = "解析文档并转换为指定格式")
    public ResponseEntity<ProcessResponse> process(@RequestBody ProcessRequest request) {
        String result = paddleOcrService.process(
            request.getImageBase64(),
            request.getTaskType(),
            request.getOutputFormat()
        );
        return ResponseEntity.ok(new ProcessResponse(true, result, System.currentTimeMillis()));
    }
}
```

### 6.2 服务层实现

```java
@Service
public class PaddleOcrService {

    @Autowired
    private OllamaChatModel chatModel;

    @Value("${paddleocr.prompt.ocr}")
    private String ocrPrompt;

    @Value("${paddleocr.prompt.table}")
    private String tablePrompt;

    @Value("${paddleocr.prompt.formula}")
    private String formulaPrompt;

    @Value("${paddleocr.prompt.chart}")
    private String chartPrompt;

    @Value("${paddleocr.prompt.markdown}")
    private String markdownPrompt;

    public String ocr(String imageBase64, String language) {
        String prompt = String.format("%s (Language: %s)", ocrPrompt, language);
        return callModel(imageBase64, prompt);
    }

    public String process(String imageBase64, String taskType, String outputFormat) {
        String prompt = getPromptByTaskType(taskType);
        if ("markdown".equals(outputFormat)) {
            prompt = markdownPrompt;
        }
        return callModel(imageBase64, prompt);
    }

    private String callModel(String imageBase64, String prompt) {
        UserMessage userMessage = new UserMessage(prompt, 
            List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageBase64)));
        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage)));
        return response.getResult().getOutput().getContent();
    }

    private String getPromptByTaskType(String taskType) {
        return switch (taskType) {
            case "table" -> tablePrompt;
            case "formula" -> formulaPrompt;
            case "chart" -> chartPrompt;
            default -> ocrPrompt;
        };
    }
}
```

---

## 七、API 接口说明

### 7.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/v1/paddleocr/ocr` | 文本识别，支持多语言 |
| `POST` | `/v1/paddleocr/process` | 文档解析，支持多种任务类型 |
| `GET` | `/v1/paddleocr/health` | 服务健康检查 |

### 7.2 请求/响应示例

**OCR 请求**：
```json
{
  "imageBase64": "base64编码的图片数据",
  "language": "zh-CN",
  "taskType": "ocr"
}
```

**OCR 响应**：
```json
{
  "success": true,
  "result": "识别出的文本内容",
  "processingTime": 1234
}
```

**文档解析请求**：
```json
{
  "imageBase64": "base64编码的图片数据",
  "taskType": "table",
  "outputFormat": "markdown"
}
```

**文档解析响应**：
```json
{
  "success": true,
  "result": "| 列1 | 列2 | 列3 |\n|------|------|------|\n| 数据1 | 数据2 | 数据3 |",
  "processingTime": 2345
}
```

---

## 八、部署方式

### 方式一：Ollama 部署（推荐）

#### 1. 安装 Ollama

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

#### 2. 拉取 PaddleOCR-VL 模型

```bash
ollama pull MedAIBase/PaddleOCR-VL  # 对应 ModelScope 的 PaddleOCR-VL-1.5 模型
```

#### 3. 启动 Ollama 服务

```bash
ollama serve
```

### 方式二：通过优化的推理服务器加速 VLM 推理

为了提升推理速度，可以通过 vLLM 推理服务进行加速。根据 ModelScope 官方文档，有两种方法启动 vLLM 推理服务：

#### 方法一：PaddleOCR 官方 Docker 方法

```bash
docker run \
    --rm \
    --gpus all \
    --network host \
    ccr-2vdh3abv-pub.cnc.bj.baidubce.com/paddlepaddle/paddleocr-genai-vllm-server:latest-nvidia-gpu \
    paddleocr genai_server --model_name PaddleOCR-VL-1.5-0.9B --host 0.0.0.0 --port 8080 --backend vllm
```

#### 方法二：vLLM 原生方法

```bash
# 安装 vLLM
pip install vllm>=0.10.2

# 启动 vLLM 服务（具体参数请参考 vLLM 官方文档：PaddleOCR-VL Usage Guide）
# vllm serve [model_path] --dtype half --trust-remote-code --port 8000
```

#### 调用方式

启动 vLLM 服务器后，可以通过以下方式调用：

**CLI 调用**：
```bash
paddleocr doc_parser \
    -i https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png \
    --vl_rec_backend vllm-server \
    --vl_rec_server_url http://127.0.0.1:8080/v1
```

**Python API 调用**：
```python
from paddleocr import PaddleOCRVL

pipeline = PaddleOCRVL(
    vl_rec_backend="vllm-server", 
    vl_rec_server_url="http://127.0.0.1:8080/v1"
)
output = pipeline.predict("https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png")
```

### 方式三：PaddleOCR 官方部署

#### 1. 安装 PaddlePaddle

```bash
# 以下命令安装 CUDA 12.6 版本的 PaddlePaddle
# 对于其他 CUDA 版本和 CPU 版本，请参考 https://www.paddlepaddle.org.cn/en/install/quick?docurl=/documentation/docs/en/develop/install/pip/linux-pip_en.html
python -m pip install paddlepaddle-gpu==3.2.1 -i https://www.paddlepaddle.org.cn/packages/stable/cu126/
```

#### 2. 安装 PaddleOCR

```bash
python -m pip install -U "paddleocr[doc-parser]"
```

---

## 九、使用示例

### 9.1 cURL 调用

**文本识别**：
```bash
curl -X POST http://localhost:8080/v1/paddleocr/ocr \
  -H "Content-Type: application/json" \
  -d '{
    "imageBase64": "base64编码的图片数据",
    "language": "zh-CN"
  }'
```

**表格提取**：
```bash
curl -X POST http://localhost:8080/v1/paddleocr/process \
  -H "Content-Type: application/json" \
  -d '{
    "imageBase64": "base64编码的图片数据",
    "taskType": "table",
    "outputFormat": "markdown"
  }'
```

### 9.2 Java 客户端

```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/v1/paddleocr/process";

ProcessRequest request = new ProcessRequest();
request.setImageBase64(imageBase64);
request.setTaskType("formula");
request.setOutputFormat("latex");

ProcessResponse response = restTemplate.postForObject(url, request, ProcessResponse.class);
System.out.println("公式识别结果: " + response.getResult());
```

### 9.3 Python 客户端

```python
import requests
import base64

def process_document(image_path, task_type="ocr"):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8080/v1/paddleocr/process",
        json={
            "imageBase64": image_base64,
            "taskType": task_type,
            "outputFormat": "markdown"
        }
    )
    
    return response.json()

result = process_document("document.png", task_type="table")
print(result)
```

---

## 十、运行项目

### 10.1 编译

```bash
cd spring-ai-examples/spring-ai-ollama-ocr-paddleocr
mvn clean package -DskipTests
```

### 10.2 运行

```bash
java -Xmx4g -jar target/spring-ai-ollama-ocr-paddleocr-1.0.0-SNAPSHOT.jar
```

### 10.3 访问 API 文档

启动后访问：http://localhost:8080/swagger-ui.html

---

## 十一、常见问题

### Q1: Ollama 连接失败？

检查 Ollama 服务是否运行：

```bash
ollama list
```

确保 Ollama 服务在 http://localhost:11434 可访问。

### Q2: 内存不足？

增加 JVM 内存：

```bash
java -Xmx8g -jar target/spring-ai-ollama-ocr-paddleocr-1.0.0-SNAPSHOT.jar
```

### Q3: 模型加载失败？

检查模型是否已拉取：

```bash
ollama pull MedAIBase/PaddleOCR-VL  # 对应 ModelScope 的 PaddleOCR-VL-1.5 模型
```

### Q4: 如何输出 Markdown 格式？

使用 `outputFormat: "markdown"` 参数，或在 Prompt 中使用 `"Convert to markdown:"`。

### Q5: 处理多页 PDF？

先将 PDF 转换为图片，然后逐页处理：

```java
// 使用 PDFBox 或 pdf2image 库转换 PDF
List<String> pageImages = convertPdfToImages("document.pdf");
for (String imageBase64 : pageImages) {
    // 调用 PaddleOCR-VL API
}
```

---

## 十二、许可证

- **PaddleOCR-VL 模型**：Apache 2.0 许可证

PaddleOCR-VL 采用 Apache 2.0 开源许可证，用户在使用本项目时应遵守该许可证的相关条款。

---

## 十三、参考资源

- **PaddleOCR-VL 官方**：https://cloud.baidu.com/doc/OCR/s/Klxag8wiy
- **ModelScope 镜像 (PaddleOCR-VL-1.5)**：https://www.modelscope.cn/models/PaddlePaddle/PaddleOCR-VL-1.5
- **Ollama 模型**：https://ollama.com/MedAIBase/PaddleOCR-VL
- **Spring AI 文档**：https://docs.spring.io/spring-ai/reference/
- **vLLM 文档**：https://docs.vllm.ai/
- **技术报告**：https://arxiv.org/abs/2510.14528

---

## 十四、致谢

- **感谢百度 PaddlePaddle 团队**开源高质量的 PaddleOCR-VL 模型
- **感谢 Spring AI 社区**提供强大的 AI 集成框架
- **感谢 Ollama 项目**简化大模型本地部署
- **感谢 ModelScope 社区**提供模型镜像和中文支持