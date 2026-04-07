# spring-ai-ollama-ocr-glm

> 基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[Spring AI](https://docs.spring.io/spring-ai/reference/index.html)、[Ollama](https://ollama.com/) 的 `GLM-OCR` OCR 功能示例。

> 通过 Ollama 运行 GLM-OCR 模型，实现本地化的高精度文档 OCR 识别能力。

## 功能特性

- **SOTA 性能**：OmniDocBench V1.5 得分 94.62，排名第一
- **轻量级架构**：仅 0.9B 参数，支持边缘部署
- **多场景优化**：文本、表格、公式、图表识别
- **高效推理**：支持 Ollama 快速部署

## 先决条件

### 1. 安装 Ollama

```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh
```

### 2. 拉取 GLM-OCR 模型

```bash
ollama run glm-ocr
```

## 配置说明

### application.properties

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=glm-ocr
spring.ai.ollama.chat.options.temperature=0.1
```

## API 接口

### 1. 文本识别

```bash
POST /v1/ocr/text
```

### 2. 表格识别

```bash
POST /v1/ocr/table
```

### 3. 公式识别

```bash
POST /v1/ocr/formula
```

### 4. 图表识别

```bash
POST /v1/ocr/chart
```

## Prompt 说明

| 任务类型 | Prompt |
|---------|--------|
| 文本识别 | `Text Recognition:` |
| 表格识别 | `Table Recognition:` |
| 公式识别 | `Formula Recognition:` |
| 图表识别 | `Chart Recognition:` |
| 关键信息提取 | `Key Information Extraction:` |

## 使用示例

### cURL 示例

```bash
curl -X POST http://localhost:8080/v1/ocr/text \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "'$(base64 -w 0 test.png)'"}'
```

### Python 示例

```python
import requests
import base64

def ocr_text(image_path):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8080/v1/ocr/text",
        json={"imageBase64": image_base64}
    )
    
    return response.json()

result = ocr_text("document.png")
print(result)
```

## 运行项目

```bash
mvn spring-boot:run
```

## 访问 API 文档

http://localhost:8080/swagger-ui.html

## 参考资源

- **GLM-OCR 文档**：[Spring AI 集成 GLM-OCR 本地部署](../docs/3-Spring%20AI%20增强扩展/7、Spring%20AI%20增强扩展：Spring%20AI%20集成%20GLM-OCR%20本地部署.md)
- **HuggingFace 模型**：https://huggingface.co/zai-org/GLM-OCR
