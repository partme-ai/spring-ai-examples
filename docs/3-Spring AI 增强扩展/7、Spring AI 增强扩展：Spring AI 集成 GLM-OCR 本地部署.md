# GLM-OCR 本地部署实践指南

## 概述

GLM-OCR 是基于 GLM-V 编码器-解码器架构的多模态 OCR 模型，专为复杂文档理解设计。该模型引入了多令牌预测（MTP）损失和稳定的全任务强化学习，以提高训练效率、识别准确性和泛化能力。GLM-OCR 仅 0.9B 参数，在 OmniDocBench V1.5 上得分 94.62，排名第一。

### 核心特性

- **SOTA 性能**：OmniDocBench V1.5 得分 94.62，排名第一
- **轻量级架构**：仅 0.9B 参数，支持边缘部署
- **高效推理**：支持 vLLM、SGLang、Ollama 部署
- **多场景优化**：针对复杂表格、代码文档、印章等实际场景优化
- **易于使用**：完全开源，提供完整 SDK 和推理工具链

## 部署方案

本文介绍四种本地部署方案：

1. **Ollama 方案**（推荐）
2. **vLLM 方案**
3. **SGLang 方案**
4. **Transformers 方案**

---

## 方案一：Ollama 方案（推荐）

### 安装 Ollama

```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# 或访问 https://ollama.com 下载安装包
```

### 运行 GLM-OCR

```bash
ollama run glm-ocr
```

### 使用方式

#### CLI 方式

Ollama 会自动识别图片文件路径：

```bash
ollama run glm-ocr Text Recognition: ./image.png
```

#### API 方式

```python
import requests
import base64

def ocr_with_ollama(image_path):
    with open(image_path, "rb") as f:
        image_data = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:11434/api/generate",
        json={
            "model": "glm-ocr",
            "prompt": "Text Recognition:",
            "images": [image_data]
        }
    )
    
    return response.json()

result = ocr_with_ollama("test.png")
print(result)
```

---

## 方案二：vLLM 方案

### 安装 vLLM

```bash
pip install -U vllm --extra-index-url https://wheels.vllm.ai/nightly
```

或使用 Docker：

```bash
docker pull vllm/vllm-openai:nightly
```

### 安装依赖

```bash
pip install git+https://github.com/huggingface/transformers.git
```

### 启动服务

```bash
vllm serve zai-org/GLM-OCR --allowed-local-media-path / --port 8080
```

### API 调用

```python
import requests
import base64

def ocr_with_vllm(image_path, server_url="http://localhost:8080"):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        f"{server_url}/v1/chat/completions",
        json={
            "model": "zai-org/GLM-OCR",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "Text Recognition:"}
                    ]
                }
            ],
            "max_tokens": 8192
        }
    )
    
    return response.json()

result = ocr_with_vllm("test.png")
print(result)
```

---

## 方案三：SGLang 方案

### 安装 SGLang

使用 Docker：

```bash
docker pull lmsysorg/sglang:dev
```

或从源码安装：

```bash
pip install git+https://github.com/sgl-project/sglang.git#subdirectory=python
```

### 安装依赖

```bash
pip install git+https://github.com/huggingface/transformers.git
```

### 启动服务

```bash
python -m sglang.launch_server --model zai-org/GLM-OCR --port 8080
```

### API 调用

```python
import requests
import base64

def ocr_with_sglang(image_path, server_url="http://localhost:8080"):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        f"{server_url}/v1/chat/completions",
        json={
            "model": "zai-org/GLM-OCR",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "Text Recognition:"}
                    ]
                }
            ]
        }
    )
    
    return response.json()

result = ocr_with_sglang("test.png")
print(result)
```

---

## 方案四：Transformers 方案

### 安装依赖

```bash
pip install git+https://github.com/huggingface/transformers.git
pip install torch
```

### 基本使用

```python
from transformers import AutoProcessor, AutoModelForImageTextToText
import torch

MODEL_PATH = "zai-org/GLM-OCR"

messages = [
    {
        "role": "user",
        "content": [
            {"type": "image", "url": "test_image.png"},
            {"type": "text", "text": "Text Recognition:"}
        ],
    }
]

processor = AutoProcessor.from_pretrained(MODEL_PATH)
model = AutoModelForImageTextToText.from_pretrained(
    pretrained_model_name_or_path=MODEL_PATH,
    torch_dtype="auto",
    device_map="auto",
)

inputs = processor.apply_chat_template(
    messages,
    tokenize=True,
    add_generation_prompt=True,
    return_dict=True,
    return_tensors="pt"
).to(model.device)

inputs.pop("token_type_ids", None)

generated_ids = model.generate(**inputs, max_new_tokens=8192)
output_text = processor.decode(
    generated_ids[0][inputs["input_ids"].shape[1]:],
    skip_special_tokens=False
)

print(output_text)
```

---

## Prompt 使用说明

GLM-OCR 支持两类 Prompt 场景：

### 1. 文档解析

提取文档原始内容：

| 任务 | Prompt |
|------|--------|
| 文本识别 | `Text Recognition:` |
| 公式识别 | `Formula Recognition:` |
| 表格识别 | `Table Recognition:` |
| 图表识别 | `Chart Recognition:` |

### 2. 信息提取

从文档中提取特定信息：

| 任务 | Prompt |
|------|--------|
| 关键信息提取 | `Key Information Extraction:` |
| 文档问答 | `Question: {your_question}` |

---

## 性能对比

### OmniDocBench V1.5

| 模型 | 得分 | 排名 |
|------|------|------|
| GLM-OCR | 94.62 | #1 |
| PaddleOCR-VL 1.5 | 94.50 | #2 |
| Qianfan-OCR | 93.12 | #3 |

### 推理速度

| 输入类型 | 吞吐量 |
|---------|--------|
| PDF 文档 | 1.86 pages/sec |
| 图片 | 0.67 images/sec |

---

## 官方 SDK

对于文档解析任务，推荐使用官方 SDK：

```python
# 安装 SDK
pip install glm-ocr-sdk

from glm_ocr import GLMOCR

ocr = GLMOCR()

# 文档解析
result = ocr.parse_document("document.pdf")
print(result.markdown)

# 图片解析
result = ocr.parse_image("image.png")
print(result.text)
```

SDK 集成了 PP-DocLayoutV3，提供完整的文档解析流水线，包括布局分析和结构化输出生成。

---

## 常见问题

### Q1: Ollama 模型下载慢？

使用国内镜像：

```bash
export OLLAMA_MIRRORS=https://ollama.ai-clone.cn
ollama pull glm-ocr
```

### Q2: vLLM 启动失败？

确保安装了最新版 transformers：

```bash
pip install git+https://github.com/huggingface/transformers.git
```

### Q3: 内存不足？

使用量化模型或调整 `max_model_len`：

```bash
vllm serve zai-org/GLM-OCR --max_model_len 4096
```

---

## 最佳实践

### 1. 选择合适的部署方案

| 场景 | 推荐方案 |
|------|----------|
| 快速开发测试 | Ollama |
| 生产环境 | vLLM / SGLang |
| 自定义集成 | Transformers |

### 2. 优化推理性能

- 使用 GPU 加速
- 启用批处理
- 调整 `max_new_tokens` 参数
- 使用官方 SDK 获得完整流水线

### 3. 处理不同文档类型

```python
# 文本识别
prompt = "Text Recognition:"

# 表格识别
prompt = "Table Recognition:"

# 公式识别
prompt = "Formula Recognition:"

# 图表识别
prompt = "Chart Recognition:"
```

---

## 参考资源

- **HuggingFace 模型**：https://huggingface.co/zai-org/GLM-OCR
- **技术报告**：https://arxiv.org/abs/xxx
- **官方 SDK**：https://github.com/zai-org/glm-ocr-sdk
- **Ollama 模型**：https://ollama.com/library/glm-ocr

---

## 引用

```bibtex
@misc{glm-ocr,
  title={GLM-OCR: A Multimodal OCR Model for Complex Document Understanding},
  author={ZAI Team},
  year={2025},
  url={https://huggingface.co/zai-org/GLM-OCR}
}
```
