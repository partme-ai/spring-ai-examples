# spring-ai-ollama-ocr-deepseek

> 基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[Spring AI](https://docs.spring.io/spring-ai/reference/index.html)、[Ollama](https://ollama.com/) 的 `DeepSeek-OCR` OCR 功能示例。

> 通过 Ollama 运行 DeepSeek-OCR 模型，实现本地化的高精度文档 OCR 识别能力。

## 功能特性

- **文档解析**：解析复杂文档并转换为 Markdown
- **多模态识别**：支持文本、表格、公式、图表
- **流式输出**：支持流式响应，实时返回识别结果
- **批量处理**：支持批量文档处理
- **高精度识别**：基于 DeepSeek-OCR-2 的 SOTA 性能

## 先决条件

### 1. 安装 Ollama

```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# 或访问 https://ollama.com 下载安装包
```

### 2. 拉取 DeepSeek-OCR 模型

由于 DeepSeek-OCR-2 暂未在 Ollama 官方仓库发布，可使用以下替代方案：

#### 方案 A：使用 Qianfan-OCR（推荐）

```bash
# Qianfan-OCR 是基于 InternVL 的 OCR 模型
# 可通过 llama.cpp 运行 GGUF 版本
```

#### 方案 B：通过 vLLM 运行

```bash
pip install vllm>=0.6.0

vllm serve deepseek-ai/DeepSeek-OCR-2 \
  --dtype half \
  --trust-remote-code \
  --enforce-eager
```

### 3. 配置 Ollama 兼容服务

如果使用 vLLM 或其他 OpenAI 兼容服务，修改 `application.properties`：

```properties
spring.ai.ollama.base-url=http://localhost:8000/v1
```

## 配置说明

### application.properties

```properties
# Ollama / vLLM 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=deepseek-ai/DeepSeek-OCR-2
spring.ai.ollama.chat.options.temperature=0

# Spring AI 重试配置
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=2000
spring.ai.retry.backoff.multiplier=2
spring.ai.retry.backoff.max-interval=5000
```

## API 接口

### 1. 文档解析

```bash
POST /v1/ocr/document
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 2. Markdown 转换

```bash
POST /v1/ocr/markdown
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 3. 流式输出

```bash
POST /v1/ocr/stream
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 4. 自定义 Prompt

```bash
POST /v1/ocr/custom
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据",
  "prompt": "<|grounding|>Convert the document to markdown."
}
```

## 使用示例

### cURL 示例

```bash
# 文档解析
curl -X POST http://localhost:8080/v1/ocr/document \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "'$(base64 -w 0 document.png)'"}'

# 流式输出
curl -X POST http://localhost:8080/v1/ocr/stream \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "'$(base64 -w 0 document.png)'"}'
```

### Python 示例

```python
import requests
import base64

def ocr_document(image_path):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8080/v1/ocr/document",
        json={"imageBase64": image_base64}
    )
    
    return response.json()

result = ocr_document("document.png")
print(result)
```

### 流式输出示例

```python
import requests
import base64
import json

def ocr_stream(image_path):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8080/v1/ocr/stream",
        json={"imageBase64": image_base64},
        stream=True
    )
    
    for line in response.iter_lines():
        if line:
            print(line.decode('utf-8'), end='')

ocr_stream("document.png")
```

## Prompt 说明

DeepSeek-OCR 支持以下 Prompt：

| 任务类型 | Prompt | 说明 |
|---------|--------|------|
| 文档转 Markdown | `<image>\n<|grounding|>Convert the document to markdown.` | 带布局的 Markdown 转换 |
| 纯 OCR | `<image>\nFree OCR.` | 无布局的纯文本识别 |
| 表格识别 | `<image>\n<|grounding|>Extract the table content.` | 提取表格内容 |
| 公式识别 | `<image>\n<|grounding|>Extract the formula.` | 提取数学公式 |

## 运行项目

```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/spring-ai-ollama-ocr-deepseek-1.0.0-SNAPSHOT.jar

# 或使用 Maven
mvn spring-boot:run
```

## 访问 API 文档

启动后访问：http://localhost:8080/swagger-ui.html

## 性能对比

| 特性 | PaddleOCR-VL | DeepSeek-OCR-2 |
|------|--------------|----------------|
| 参数量 | 0.9B / 1.5B | ~7B |
| 语言支持 | 109 种 | 中英为主 |
| 精度 | 高 | SOTA |
| 推理速度 | 快 | 中等 |
| 适用场景 | 轻量级、边缘设备 | 高精度、生产环境 |

## 参考资源

- **DeepSeek-OCR 文档**：[Spring AI 集成 DeepSeek-OCR 本地部署](../docs/3-Spring%20AI%20增强扩展/6、Spring%20AI%20增强扩展：Spring%20AI%20集成%20DeepSeek-OCR%20本地部署.md)
- **DeepSeek-OCR-2 官方**：https://huggingface.co/deepseek-ai/DeepSeek-OCR-2
- **Spring AI 文档**：https://docs.spring.io/spring-ai/reference/
