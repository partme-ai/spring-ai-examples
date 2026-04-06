# Spring AI 增强扩展：Spring AI 集成 Qianfan-OCR 本地部署

> 基于 Spring AI + vLLM/Ollama 实现 Qianfan-OCR 的本地化文档智能服务，提供 RESTful API 接口，支持文档解析、布局分析、表格提取、图表理解、文档问答和关键信息提取等功能。

## 一、项目概述

### 1.1 项目定位

本项目是 Spring AI 框架下集成 Qianfan-OCR 端到端文档智能模型的示例，展示了如何在 Java/Spring Boot 应用中实现本地化的文档智能服务。Qianfan-OCR 是百度千帆团队开发的 4B 参数端到端文档智能模型，将文档解析、布局分析和文档理解统一在单一视觉语言架构中。

### 1.2 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.1.4 | AI 能力集成 |
| vLLM / Ollama | - | 模型推理服务 |
| Qianfan-OCR | 4B | 端到端文档智能模型 |

### 1.3 核心功能

- ✅ **端到端文档解析**：图像直接到 Markdown/JSON/HTML 转换
- ✅ **Layout-as-Thought**：创新的思考阶段机制，恢复显式布局分析
- ✅ **多任务支持**：文档解析、布局分析、表格提取、图表理解、文档问答、关键信息提取
- ✅ **多语言 OCR**：支持 192 种语言文档处理
- ✅ **高效推理**：W8A8 量化下达到 1.024 PPS（单 A100 GPU）
- ✅ **RESTful API**：标准化接口设计，支持批量处理
- ✅ **Swagger 文档**：在线 API 文档和交互式测试

---

## 二、Qianfan-OCR 模型简介

### 2.1 模型介绍

**Qianfan-OCR: 4B 参数端到端文档智能模型，OmniDocBench v1.5 端到端模型排名第一**

Qianfan-OCR 是百度千帆团队开发的 4B 参数端到端文档智能模型，将文档解析、布局分析和文档理解统一在单一的视觉语言架构中。

与传统的多阶段 OCR 流水线（链接独立的布局检测、文本识别和语言理解模块）不同，Qianfan-OCR 执行直接的图像到 Markdown 转换，并支持广泛的提示驱动任务——从结构化文档解析和表格提取到图表理解、文档问答和关键信息提取——所有这些都在一个模型中完成。


### 2.2 关键亮点

Qianfan-OCR 在多个维度上展现出卓越性能，以下是其核心优势的全面对比：

| 亮点 | 具体表现 | 对比优势 |
|------|----------|----------|
| 🏆 **OmniDocBench v1.5 排名第一** | 93.12 总分 | 超越 DeepSeek-OCR-v2 (91.09)、Gemini-3 Pro (90.33) 和所有其他端到端模型 |
| 🏆 **OlmOCR Bench 排名第一** | 79.8 分 | 在端到端模型中表现最佳 |
| 🏆 **关键信息提取领先** | 87.9 平均分（五个公共 KIE 基准） | 超越 Gemini-3.1-Pro、Gemini-3-Pro、Seed-2.0 和 Qwen3-VL-235B-A22B |
| 🧠 **Layout-as-Thought 机制** | 通过 ⟨think⟩ 标记触发思考阶段 | 在端到端范式中恢复显式布局分析，提升复杂文档处理精度 |
| 🌍 **多语言支持广泛** | 支持 192 种语言 | 覆盖全球主要文字体系，满足国际化文档处理需求 |
| ⚡ **部署效率卓越** | 1.024 PPS（W8A8 量化，单 A100 GPU） | 在保持高精度的同时实现高效推理，适合生产环境部署 |

### 2.3 模型架构

Qianfan-OCR 采用 Qianfan-VL 的多模态桥接架构，由三个核心组件组成：

| 组件 | 详细规格 |
|------|----------|
| **视觉编码器** | **Qianfan-ViT**：24 层 Transformer，AnyResolution 设计（最高 4K 分辨率），每 448×448 图块 256 个视觉标记，每张图像最多 4,096 个标记 |
| **语言模型** | **Qwen3-4B**：3.6B 非嵌入参数，36 层，2560 隐藏维度，GQA（32 查询头 / 8 KV 头），32K 上下文长度（可扩展至 131K） |
| **跨模态适配器** | 2 层 MLP 带 GELU 激活，从 1024 维投影到 2560 维 |

### 2.4 Layout-as-Thought 机制

**Layout-as-Thought** 是 Qianfan-OCR 的一项关键创新：一个由 ⟨think⟩ 标记触发的可选思考阶段，模型在此阶段生成结构化布局表示（边界框、元素类型、阅读顺序），然后再生成最终输出。

此机制有两个主要目的：
1. **功能性**：在端到端范式中恢复布局分析能力 — 用户可以直接获得结构化布局结果
2. **增强性**：针对复杂布局、元素杂乱或非标准阅读顺序的文档提供有针对性的精度改进

**何时使用**：
- **启用思考**：对于包含混合元素类型的异构页面（试卷、技术报告、报纸）
- **禁用思考**：对于同质文档（单列文本、简单表格），以获得更好的结果和更低的延迟

### 2.5 模型规格

- **参数量**: 4B 参数
- **模型大小**: ~9.4 GB（F16），~2.8 GB（Q4_K_M 量化）
- **上下文长度**: 支持 32,768 tokens（可扩展至 131K）
- **视觉分辨率**: AnyResolution 设计，支持最高 4K 分辨率图像
- **量化支持**: W8A8、W16A16、Q4_K_M 等多种量化格式

### 2.6 主要 Prompt 格式

Qianfan-OCR 支持灵活的 Prompt 驱动任务：

```python
# 文档解析
prompt = "Convert this document to markdown."

# 启用 Layout-as-Thought
prompt = "<think⟩Convert this document to markdown."

# 表格提取
prompt = "Extract all tables from this document."

# 文档问答
prompt = "Question: What is the main topic of this document?"
```

### 2.7 支持的任务类别

| 任务类别 | 具体任务 |
|----------|----------|
| **文档解析** | 图像到 Markdown 转换、多页解析、结构化输出（JSON/HTML） |
| **布局分析** | 边界框检测、元素类型分类（25 个类别）、阅读顺序 |
| **表格识别** | 复杂表格提取（合并单元格、旋转表格）、HTML 输出 |
| **公式识别** | 行内和显示数学公式、LaTeX 输出 |
| **图表理解** | 图表问答、趋势分析、数据提取 |
| **文档问答** | 基于文档内容回答问题 |
| **关键信息提取** | 从文档中提取特定信息字段 |
| **文本定位** | 文字行定位和识别 |
| **印章识别** | 印章检测和识别 |

### 2.8 使用要求

**环境要求**：CUDA 11.8+，Python 3.8+，torch>=2.0.0，vLLM>=0.10.2

```bash
# 核心依赖
torch>=2.0.0
transformers>=4.36.0
vllm>=0.10.2
accelerate
```

---

## 三、性能基准

### 3.0 总体性能概览

Qianfan-OCR 在多个权威基准测试中均表现出色，以下是其在各主要类别中的表现汇总：

| 基准类别 | 测试项目 | Qianfan-OCR 表现 | 排名 | 主要竞争对手 |
|----------|----------|------------------|------|------------|
| **文档解析** | OmniDocBench v1.5 | 93.12 总分 | #1（端到端） | DeepSeek-OCR-v2 (91.09)、Gemini-3 Pro (90.33) |
| **通用 OCR** | OCRBench | 880 分 | #1 | Qwen3-VL-4B (873)、MonkeyOCR (655) |
| **文档理解** | DocVQA | 92.8 分 | #2 | Qwen3-VL-4B (94.9)、Qwen3-VL-2B (92.7) |
| **关键信息提取** | KIE 平均分 | 87.9 分 | #1 | Qwen3-VL-235B-A22B (84.2)、Gemini-3.1-Pro (79.2) |
| **推理效率** | 推理吞吐量 (W8A8) | 1.024 PPS | #2 | MinerU 2.5 (1.057)、MonkeyOCR-pro-1.2B (0.673) |
| **多语言支持** | 语言覆盖 | 192 种语言 | 领先 | 主流模型通常支持 50-100 种语言 |

**性能总结**：Qianfan-OCR 在端到端文档解析、关键信息提取和多语言支持方面处于领先地位，在推理效率方面表现优异，综合性能在 4B 参数级别的文档智能模型中排名前列。

### 3.2 OmniDocBench v1.5（文档解析）

Qianfan-OCR 在 OmniDocBench v1.5 基准测试中获得了 **93.12** 总分，在端到端模型中排名第一，超越了 DeepSeek-OCR-v2 (91.09)、Gemini-3 Pro (90.33) 和所有其他端到端模型。

| 模型 | 类型 | 总分↑ | 文本编辑↓ | 公式CDM↑ | 表格TEDs↑ | 表格TEDss↑ | R-orderEdit↓ |
|------|------|-------|----------|----------|----------|-----------|------------|
| **Qianfan-OCR (Ours)** | 端到端 | **93.12** | 0.041 | 92.43 | 91.02 | 93.85 | 0.049 |
| DeepSeek-OCR-v2 | 端到端 | 91.09 | 0.048 | 90.31 | 87.75 | 92.06 | 0.057 |
| Gemini-3 Pro | 端到端 | 90.33 | 0.065 | 89.18 | 88.28 | 90.29 | 0.071 |
| Qwen3-VL-235B | 端到端 | 89.15 | 0.069 | 88.14 | 86.21 | 90.55 | 0.068 |
| dots.ocr | 端到端 | 88.41 | 0.048 | 83.22 | 86.78 | 90.62 | 0.053 |
| **PaddleOCR-VL 1.5** | 流水线 | 94.50 | 0.035 | 94.21 | 92.76 | 95.79 | 0.042 |

**注意**：PaddleOCR-VL 1.5 是流水线模型，不是端到端模型。在端到端模型中，Qianfan-OCR 排名第一。

### 3.3 通用 OCR 基准

| 模型 | OCRBench | OCRBenchv2 (en/zh) | CCOCR-multilan | CCOCR-overall |
|------|----------|-------------------|---------------|--------------|
| **Qianfan-OCR (Ours)** | 880 | 56.0 / 60.77 | 76.7 | 79.3 |
| Qwen3-VL-4B | 873 | 60.68 / 59.13 | 74.2 | 76.5 |
| MonkeyOCR | 655 | 21.78 / 38.91 | 43.8 | 35.2 |
| DeepSeek-OCR | 459 | 15.98 / 38.31 | 32.5 | 27.6 |

### 3.4 文档理解能力

| 基准 | Qianfan-OCR | Qwen3-VL-4B | Qwen3-VL-2B |
|------|-------------|-------------|-------------|
| DocVQA | 92.8 | 94.9 | 92.7 |
| CharXiv_DQ | 94.0 | 81.8 | 69.7 |
| CharXiv_RQ | 85.2 | 48.5 | 41.3 |
| ChartQA | 88.1 | 83.3 | 78.3 |
| ChartQAPro | 42.9 | 36.2 | 24.5 |
| ChartBench | 85.9 | 74.9 | 73.2 |
| TextVQA | 80.0 | 81.8 | 79.9 |
| OCRVQA | 66.8 | 64.7 | 59.3 |

> 💡 **重要发现**：两阶段 OCR+LLM 系统在 CharXiv（DQ 和 RQ）上得分为 0.0，这表明在文本提取过程中丢弃的图表结构对于推理至关重要。Qianfan-OCR 的端到端架构避免了这个问题。

### 3.5 关键信息提取（KIE）性能

| 模型 | 总分 | OCRBench KIE | OCRBenchv2 KIE (en) | OCRBenchv2 KIE (zh) | CCOCR KIE | Nanonets KIE (F1) |
|------|------|--------------|-------------------|-------------------|----------|-----------------|
| **Qianfan-OCR (Ours)** | **87.9** | 95.0 | 82.8 | 82.3 | 92.8 | 86.5 |
| Qwen3-VL-235B-A22B | 84.2 | 94.0 | 85.6 | 62.9 | 95.1 | 83.8 |
| Qwen3-4B-VL | 83.5 | 89.0 | 82.1 | 71.3 | 91.6 | 83.3 |
| Gemini-3.1-Pro | 79.2 | 96.0 | 87.8 | 63.4 | 72.5 | 76.1 |

Qianfan-OCR 在五个公共 KIE 基准上的平均分为 **87.9**，超越了 Gemini-3.1-Pro、Gemini-3-Pro、Seed-2.0 和 Qwen3-VL-235B-A22B。

### 3.6 推理吞吐量

| 模型 | PPS（每秒页数） |
|------|-----------------|
| **Qianfan-OCR (W8A8)** | **1.024** |
| Qianfan-OCR (W16A16) | 0.503 |
| MinerU 2.5 | 1.057 |
| MonkeyOCR-pro-1.2B | 0.673 |
| Dots OCR | 0.352 |

> **测试环境**：所有基准测试均在单 NVIDIA A100 GPU 上使用 vLLM 0.10.2 进行。

---

## 四、项目结构

```
spring-ai-examples/spring-ai-ollama-ocr-qianfan/
├── src/main/java/com/example/qianfanocr/
│   ├── controller/
│   │   └── QianfanOcrController.java      # REST API 控制器
│   ├── service/
│   │   └── QianfanOcrService.java         # 业务逻辑服务
│   ├── config/
│   │   └── QianfanOcrConfig.java          # Spring AI 配置
│   └── model/
│       ├── request/
│       │   ├── DocumentParseRequest.java  # 文档解析请求
│       │   ├── LayoutAnalysisRequest.java # 布局分析请求
│       │   └── TableExtractRequest.java   # 表格提取请求
│       └── response/
│           ├── DocumentParseResponse.java # 文档解析响应
│           ├── LayoutAnalysisResponse.java# 布局分析响应
│           └── TableExtractResponse.java  # 表格提取响应
├── src/main/resources/
│   ├── application.yml                    # 主配置文件
│   └── static/                            # 静态资源
├── pom.xml                                # Maven 配置
└── README.md                              # 项目说明
```

### 文件说明

- `controller/QianfanOcrController.java` - REST API 控制器，提供文档智能接口
- `service/QianfanOcrService.java` - 业务逻辑服务，集成 vLLM/Ollama 模型
- `config/QianfanOcrConfig.java` - Spring AI 配置，支持 Layout-as-Thought
- `model/request/*.java` - 各种文档处理任务的请求数据模型
- `model/response/*.java` - 各种文档处理任务的响应数据模型

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
          model: baidu/Qianfan-OCR
          temperature: 0
          max_tokens: 8192
    vllm:
      enabled: true
      base-url: http://localhost:8000
      model: baidu/Qianfan-OCR
      max-model-len: 32768

server:
  port: 8080

qianfan:
  thinking:
    enabled: true
    prefix: "<think⟩"
  tasks:
    document-parse: "Convert this document to {format}."
    layout-analysis: "<think⟩Analyze the layout of this document."
    table-extract: "Extract all tables from this document."
    chart-understand: "Analyze the charts in this document."
    document-qa: "Question: {question}"
    key-info-extract: "Extract the following information: {fields}"
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
        <groupId>org.springaicommunity</groupId>
        <artifactId>spring-ai-autoconfigure-model-qianfan</artifactId>
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
@RequestMapping("/v1/qianfan")
@Tag(name = "Qianfan-OCR API", description = "Qianfan-OCR 文档智能接口")
public class QianfanOcrController {

    @Autowired
    private QianfanOcrService qianfanOcrService;

    @PostMapping("/parse")
    @Operation(summary = "文档解析", description = "解析文档并转换为指定格式")
    public ResponseEntity<DocumentParseResponse> parseDocument(
            @RequestBody DocumentParseRequest request) {
        String result = qianfanOcrService.parseDocument(
            request.getImageBase64(),
            request.getOutputFormat(),
            request.isEnableThinking()
        );
        return ResponseEntity.ok(new DocumentParseResponse(true, result, System.currentTimeMillis()));
    }

    @PostMapping("/analyze-layout")
    @Operation(summary = "布局分析", description = "分析文档布局结构")
    public ResponseEntity<LayoutAnalysisResponse> analyzeLayout(
            @RequestBody LayoutAnalysisRequest request) {
        String result = qianfanOcrService.analyzeLayout(request.getImageBase64());
        return ResponseEntity.ok(new LayoutAnalysisResponse(true, result, System.currentTimeMillis()));
    }

    @PostMapping("/extract-tables")
    @Operation(summary = "表格提取", description = "提取文档中的表格")
    public ResponseEntity<TableExtractResponse> extractTables(
            @RequestBody TableExtractRequest request) {
        String result = qianfanOcrService.extractTables(
            request.getImageBase64(),
            request.getOutputFormat()
        );
        return ResponseEntity.ok(new TableExtractResponse(true, result, System.currentTimeMillis()));
    }

    @PostMapping("/ask")
    @Operation(summary = "文档问答", description = "基于文档内容回答问题")
    public ResponseEntity<DocumentQAResponse> askDocument(
            @RequestBody DocumentQARequest request) {
        String result = qianfanOcrService.askDocument(
            request.getImageBase64(),
            request.getQuestion()
        );
        return ResponseEntity.ok(new DocumentQAResponse(true, result, System.currentTimeMillis()));
    }
}
```

### 6.2 服务层实现

```java
@Service
public class QianfanOcrService {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Autowired(required = false)
    private VllmChatModel vllmChatModel;

    @Value("${qianfan.thinking.enabled}")
    private boolean thinkingEnabled;

    @Value("${qianfan.thinking.prefix}")
    private String thinkingPrefix;

    @Value("${qianfan.tasks.document-parse}")
    private String documentParsePrompt;

    @Value("${qianfan.tasks.layout-analysis}")
    private String layoutAnalysisPrompt;

    @Value("${qianfan.tasks.table-extract}")
    private String tableExtractPrompt;

    @Value("${qianfan.tasks.document-qa}")
    private String documentQAPrompt;

    public String parseDocument(String imageBase64, String outputFormat, boolean enableThinking) {
        String prompt = documentParsePrompt.replace("{format}", outputFormat);
        if (enableThinking && thinkingEnabled) {
            prompt = thinkingPrefix + prompt;
        }
        return callModel(imageBase64, prompt, true);
    }

    public String analyzeLayout(String imageBase64) {
        return callModel(imageBase64, layoutAnalysisPrompt, true);
    }

    public String extractTables(String imageBase64, String outputFormat) {
        String prompt = tableExtractPrompt;
        if ("markdown".equals(outputFormat)) {
            prompt = prompt + " Convert to markdown format.";
        }
        return callModel(imageBase64, prompt, false);
    }

    public String askDocument(String imageBase64, String question) {
        String prompt = documentQAPrompt.replace("{question}", question);
        return callModel(imageBase64, prompt, false);
    }

    public String extractKeyInfo(String imageBase64, List<String> fields) {
        String fieldsStr = String.join(", ", fields);
        String prompt = "Extract the following information: " + fieldsStr;
        return callModel(imageBase64, prompt, false);
    }

    private String callModel(String imageBase64, String prompt, boolean useVllm) {
        UserMessage userMessage = new UserMessage(prompt, 
            List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageBase64)));
        
        ChatResponse response;
        if (useVllm && vllmChatModel != null) {
            response = vllmChatModel.call(new Prompt(List.of(userMessage)));
        } else {
            response = ollamaChatModel.call(new Prompt(List.of(userMessage)));
        }
        
        return response.getResult().getOutput().getContent();
    }
}
```

---

## 七、API 接口说明

### 7.1 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/v1/qianfan/parse` | 文档解析，支持多种输出格式 |
| `POST` | `/v1/qianfan/analyze-layout` | 布局分析，启用 Layout-as-Thought |
| `POST` | `/v1/qianfan/extract-tables` | 表格提取，支持 Markdown/JSON 输出 |
| `POST` | `/v1/qianfan/ask` | 文档问答，基于文档内容回答问题 |
| `POST` | `/v1/qianfan/extract-key-info` | 关键信息提取，指定字段提取 |
| `GET` | `/v1/qianfan/health` | 服务健康检查 |

### 7.2 请求/响应示例

**文档解析请求**：
```json
{
  "imageBase64": "base64编码的图片数据",
  "outputFormat": "markdown",
  "enableThinking": true
}
```

**文档解析响应**：
```json
{
  "success": true,
  "result": "# 文档标题\n\n文档内容...",
  "processingTime": 3456,
  "thinkingUsed": true
}
```

**布局分析请求**：
```json
{
  "imageBase64": "base64编码的图片数据"
}
```

**布局分析响应**：
```json
{
  "success": true,
  "result": "文档包含以下元素：\n1. 标题区域 (x: 100, y: 50, width: 800, height: 60)\n2. 正文区域 (x: 100, y: 120, width: 800, height: 600)",
  "processingTime": 2345
}
```

**文档问答请求**：
```json
{
  "imageBase64": "base64编码的图片数据",
  "question": "这份文档的主要议题是什么？"
}
```

**文档问答响应**：
```json
{
  "success": true,
  "result": "这份文档主要讨论人工智能在文档处理中的应用。",
  "processingTime": 1234
}
```

---

## 八、部署方式

### 方式一：vLLM 部署（推荐）

#### 1. 安装 vLLM

```bash
pip install vllm>=0.10.2
```

#### 2. 启动 vLLM 服务

```bash
vllm serve baidu/Qianfan-OCR \
  --dtype half \
  --trust-remote-code \
  --max_model_len 32768 \
  --port 8000
```

#### 3. 启用量化（可选）

```bash
vllm serve baidu/Qianfan-OCR \
  --quantization awq \
  --dtype half \
  --max_model_len 16384
```

### 方式二：Ollama 部署

#### 1. 安装 Ollama

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

#### 2. 拉取 Qianfan-OCR 模型

```bash
ollama pull baidu/Qianfan-OCR
```

#### 3. 启动 Ollama 服务

```bash
ollama serve
```

### 方式三：Transformers 部署

#### 1. 安装依赖

```bash
pip install transformers torch accelerate
```

#### 2. 直接使用 Transformers

```python
from transformers import AutoModelForCausalLM, AutoTokenizer

model = AutoModelForCausalLM.from_pretrained(
    "baidu/Qianfan-OCR",
    torch_dtype=torch.bfloat16,
    device_map="auto",
    trust_remote_code=True
)
```

---

## 九、使用示例

### 9.1 cURL 调用

**文档解析（启用思考）**：
```bash
curl -X POST http://localhost:8080/v1/qianfan/parse \
  -H "Content-Type: application/json" \
  -d '{
    "imageBase64": "base64编码的图片数据",
    "outputFormat": "markdown",
    "enableThinking": true
  }'
```

**表格提取**：
```bash
curl -X POST http://localhost:8080/v1/qianfan/extract-tables \
  -H "Content-Type: application/json" \
  -d '{
    "imageBase64": "base64编码的图片数据",
    "outputFormat": "markdown"
  }'
```

**文档问答**：
```bash
curl -X POST http://localhost:8080/v1/qianfan/ask \
  -H "Content-Type: application/json" \
  -d '{
    "imageBase64": "base64编码的图片数据",
    "question": "这份合同的总金额是多少？"
  }'
```

### 9.2 Java 客户端

```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/v1/qianfan/parse";

DocumentParseRequest request = new DocumentParseRequest();
request.setImageBase64(imageBase64);
request.setOutputFormat("markdown");
request.setEnableThinking(true);

DocumentParseResponse response = restTemplate.postForObject(url, request, DocumentParseResponse.class);
System.out.println("解析结果: " + response.getResult());
System.out.println("是否启用思考: " + response.isThinkingUsed());
```

### 9.3 Python 客户端

```python
import requests
import base64

class QianfanOCRClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def parse_document(self, image_path, output_format="markdown", enable_thinking=True):
        with open(image_path, "rb") as f:
            image_base64 = base64.b64encode(f.read()).decode()
        
        response = requests.post(
            f"{self.base_url}/v1/qianfan/parse",
            json={
                "imageBase64": image_base64,
                "outputFormat": output_format,
                "enableThinking": enable_thinking
            }
        )
        
        return response.json()
    
    def ask_document(self, image_path, question):
        with open(image_path, "rb") as f:
            image_base64 = base64.b64encode(f.read()).decode()
        
        response = requests.post(
            f"{self.base_url}/v1/qianfan/ask",
            json={
                "imageBase64": image_base64,
                "question": question
            }
        )
        
        return response.json()

client = QianfanOCRClient()
result = client.parse_document("contract.pdf", output_format="markdown", enable_thinking=True)
print(result)
```

---

## 十、运行项目

### 10.1 编译

```bash
cd spring-ai-examples/spring-ai-ollama-ocr-qianfan
mvn clean package -DskipTests
```

### 10.2 运行

```bash
java -Xmx8g -jar target/spring-ai-ollama-ocr-qianfan-1.0.0-SNAPSHOT.jar
```

### 10.3 访问 API 文档

启动后访问：http://localhost:8080/swagger-ui.html

---

## 十一、常见问题

### Q1: 如何启用 Layout-as-Thought？

在请求中设置 `enableThinking: true`，或在 Prompt 前添加 `<think⟩` 前缀。

### Q2: vLLM 和 Ollama 有什么区别？

- **vLLM**：高性能推理引擎，支持连续批处理和 PagedAttention，适合生产环境
- **Ollama**：简单易用的模型管理工具，适合开发和测试

### Q3: 内存不足怎么办？

- 使用量化模型（Q4_K_M，~2.8 GB）
- 减小 `max_model_len` 参数
- 使用 W8A8 量化

### Q4: 如何处理多页 PDF？

```python
from pdf2image import convert_from_path

images = convert_from_path("document.pdf")
for i, image in enumerate(images):
    image.save(f"page_{i}.jpg", "JPEG")
    # 逐页调用 Qianfan-OCR API
```

### Q5: 如何提高推理速度？

- 启用 vLLM 的连续批处理
- 使用 GPU 加速
- 调整批处理大小
- 使用量化模型

### Q6: 模型出现答非所问怎么办？

- 检查 Prompt 是否正确
- 调整 `temperature` 参数（建议设置为 0）
- 启用 Layout-as-Thought 处理复杂布局

---

## 十二、许可证

- **Qianfan-OCR 模型**：参考 [ModelScope 页面](https://www.modelscope.cn/models/baidu-qianfan/Qianfan-OCR) 的许可证信息

Qianfan-OCR 模型的使用应遵守百度千帆团队规定的许可证条款，用户在使用本项目时应仔细阅读并遵守相关许可证。

---

## 十三、参考资源

- **Qianfan-OCR 官方 (ModelScope)**：https://www.modelscope.cn/models/baidu-qianfan/Qianfan-OCR
- **Qianfan-OCR 官方 (HuggingFace)**：https://huggingface.co/baidu/Qianfan-OCR
- **GGUF 量化版本**：https://huggingface.co/Reza2kn/Qianfan-OCR-GGUF
- **vLLM 文档**：https://docs.vllm.ai/
- **Ollama 官网**：https://ollama.com/
- **Spring AI 文档**：https://docs.spring.io/spring-ai/reference/
- **Layout-as-Thought 技术**：相关技术报告和论文

---

## 十四、致谢

- **感谢百度千帆团队**开源高质量的 Qianfan-OCR 模型
- **感谢 Spring AI 社区**提供强大的 AI 集成框架
- **感谢 vLLM 团队**提供高性能推理引擎
- **感谢 Ollama 项目**简化大模型本地部署
- **感谢开源社区**的贡献和支持