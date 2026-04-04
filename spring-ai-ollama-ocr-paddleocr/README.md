# spring-ai-ollama-ocr-paddleocr

> 基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[Spring AI](https://docs.spring.io/spring-ai/reference/index.html)、[Ollama](https://ollama.com/) 的 `PaddleOCR-VL` OCR 功能示例。

> 通过 Ollama 运行 PaddleOCR-VL 模型，实现本地化的文档 OCR 识别能力。

## 功能特性

- **文本识别**：识别图片中的文本内容
- **表格识别**：识别表格并转换为 Markdown 格式
- **公式识别**：识别数学公式
- **图表识别**：识别图表数据
- **Markdown 转换**：将文档转换为 Markdown 格式
- **批量处理**：支持批量图片处理

## 先决条件

### 1. 安装 Ollama

```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# 或访问 https://ollama.com 下载安装包
```

### 2. 拉取 PaddleOCR-VL 模型

```bash
ollama pull MedAIBase/PaddleOCR-VL
```

### 3. 验证模型

```bash
ollama run MedAIBase/PaddleOCR-VL
```

## 配置说明

### application.properties

```properties
# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.options.model=MedAIBase/PaddleOCR-VL
spring.ai.ollama.chat.options.temperature=0.1

# Spring AI 重试配置
spring.ai.retry.max-attempts=3
spring.ai.retry.backoff.initial-interval=2000
spring.ai.retry.backoff.multiplier=2
spring.ai.retry.backoff.max-interval=5000
```

## API 接口

### 1. 文本识别

```bash
POST /v1/ocr/text
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 2. 表格识别

```bash
POST /v1/ocr/table
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 3. 公式识别

```bash
POST /v1/ocr/formula
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 4. 图表识别

```bash
POST /v1/ocr/chart
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

### 5. Markdown 转换

```bash
POST /v1/ocr/markdown
Content-Type: application/json

{
  "imageBase64": "base64编码的图片数据"
}
```

## 使用示例

### cURL 示例

```bash
# 文本识别
curl -X POST http://localhost:8080/v1/ocr/text \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "'$(base64 -w 0 test.png)'"}'

# 表格识别
curl -X POST http://localhost:8080/v1/ocr/table \
  -H "Content-Type: application/json" \
  -d '{"imageBase64": "'$(base64 -w 0 table.png)'"}'
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

## Prompt 说明

PaddleOCR-VL 支持以下 Prompt：

| 任务类型 | Prompt | 说明 |
|---------|--------|------|
| 文本识别 | `OCR:` | 识别图片中的文本 |
| 表格识别 | `Table Recognition:` | 识别表格结构 |
| 公式识别 | `Formula Recognition:` | 识别数学公式 |
| 图表识别 | `Chart Recognition:` | 识别图表数据 |
| Markdown | `Convert to markdown:` | 转换为 Markdown |

## 运行项目

```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/spring-ai-ollama-ocr-paddleocr-1.0.0-SNAPSHOT.jar

# 或使用 Maven
mvn spring-boot:run
```

## 访问 API 文档

启动后访问：http://localhost:8080/swagger-ui.html

## 参考资源

- **PaddleOCR-VL 文档**：[Spring AI 集成 PaddleOCR-VL 本地部署](../docs/3-Spring%20AI%20增强扩展/5、Spring%20AI%20增强扩展：Spring%20AI%20集成%20PaddleOCR-VL%20本地部署.md)
- **Ollama 模型**：https://ollama.com/MedAIBase/PaddleOCR-VL
- **Spring AI 文档**：https://docs.spring.io/spring-ai/reference/
