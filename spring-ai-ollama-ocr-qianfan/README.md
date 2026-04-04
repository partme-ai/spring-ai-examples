# spring-ai-ollama-ocr-qianfan

> 基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[Spring AI](https://docs.spring.io/spring-ai/reference/index.html)、[Ollama](https://ollama.com/) 的 `Qianfan-OCR` OCR 功能示例。

> 通过 Ollama/vLLM 运行 Qianfan-OCR 模型，实现本地化的端到端文档智能能力。

## 功能特性

- **端到端 SOTA**：OmniDocBench v1.5 得分 93.12，端到端模型排名第一
- **Layout-as-Thought**：创新的思考阶段机制
- **192 种语言**：支持多语言 OCR
- **多任务支持**：文档解析、布局分析、表格提取、图表理解、文档问答、关键信息提取

## 先决条件

### 方案 A：使用 vLLM（推荐）

```bash
pip install vllm>=0.10.2

vllm serve baidu/Qianfan-OCR --dtype half --trust-remote-code
```

### 方案 B：使用 GGUF + llama.cpp

```bash
# 下载 GGUF 模型
huggingface-cli download Reza2kn/Qianfan-OCR-GGUF --local-dir ./Qianfan-OCR-GGUF

# 启动 llama-server
./llama-server -m Qianfan-OCR-q4_k_m.gguf --mmproj Qianfan-OCR-mmproj.gguf --port 8080
```

## 配置说明

### application.properties

```properties
# vLLM / Ollama 配置
spring.ai.ollama.base-url=http://localhost:8000/v1
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=baidu/Qianfan-OCR
spring.ai.ollama.chat.options.temperature=0
```

## API 接口

### 1. 文档解析

```bash
POST /v1/ocr/document
```

### 2. 布局分析（启用思考）

```bash
POST /v1/ocr/layout
```

### 3. 表格提取

```bash
POST /v1/ocr/table
```

### 4. 图表理解

```bash
POST /v1/ocr/chart
```

### 5. 关键信息提取

```bash
POST /v1/ocr/kie
```

## Prompt 说明

| 任务类型 | Prompt |
|---------|--------|
| 转 Markdown | `Convert this document to markdown.` |
| 布局分析（启用思考） | `<think⟩Analyze the layout of this document.` |
| 表格提取 | `Extract all tables from this document.` |
| 图表理解 | `Analyze the charts in this document.` |
| 关键信息提取 | `Extract the following information: ...` |

## Layout-as-Thought 机制

启用 `<think⟩` 标记可恢复显式布局分析：

```python
# 启用思考 - 适用于复杂布局
prompt = "<think⟩Convert this document to markdown."

# 不启用思考 - 适用于简单文档
prompt = "Convert this document to markdown."
```

## 使用示例

### cURL 示例

```bash
curl -X POST http://localhost:8080/v1/ocr/document \
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

## 运行项目

```bash
mvn spring-boot:run
```

## 访问 API 文档

http://localhost:8080/swagger-ui.html

## 参考资源

- **Qianfan-OCR 文档**：[Spring AI 集成 Qianfan-OCR 本地部署](../docs/3-Spring%20AI%20增强扩展/8、Spring%20AI%20增强扩展：Spring%20AI%20集成%20Qianfan-OCR%20本地部署.md)
- **HuggingFace 模型**：https://huggingface.co/baidu/Qianfan-OCR
- **GGUF 量化**：https://huggingface.co/Reza2kn/Qianfan-OCR-GGUF
