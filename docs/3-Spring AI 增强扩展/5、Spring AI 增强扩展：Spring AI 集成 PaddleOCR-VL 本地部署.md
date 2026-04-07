# PaddleOCR-VL 本地部署实践指南

## 概述

PaddleOCR-VL 是百度 PaddlePaddle 团队发布的一款超紧凑视觉语言模型（VLM），专门针对文档解析任务优化。其核心组件 PaddleOCR-VL-0.9B 是一个仅 0.9B 参数的轻量级模型，集成了 NaViT 风格的动态分辨率视觉编码器和 ERNIE-4.5-0.3B 语言模型，支持 109 种语言，在文档解析和元素识别任务上达到了 SOTA 性能。

### 核心特性

- **超紧凑架构**：仅 0.9B 参数，资源消耗极低
- **多语言支持**：支持 109 种语言，包括中、英、日、韩、俄、阿拉伯语等
- **动态分辨率**：NaViT 风格的动态高分辨率视觉编码器
- **多元素识别**：支持文本、表格、公式、图表等复杂元素识别
- **SOTA 性能**：在 OmniDocBench 等基准测试中表现优异

## 部署方案

本文介绍三种本地部署方案：

1. **PaddleOCR 官方方案**（推荐）
2. **Ollama 方案**
3. **llama.cpp + GGUF 方案**

---

## 方案一：PaddleOCR 官方方案（推荐）

### 环境要求

- Python 3.8+
- CUDA 11.8+ / CUDA 12.6+（GPU 推理）
- PaddlePaddle 3.2.0+

### 安装步骤

#### 1. 安装 PaddlePaddle

**GPU 版本（CUDA 12.6）：**

```bash
python -m pip install paddlepaddle-gpu==3.2.0 -i https://www.paddlepaddle.org.cn/packages/stable/cu126/
```

**CPU 版本：**

```bash
python -m pip install paddlepaddle==3.2.0 -i https://www.paddlepaddle.org.cn/packages/stable/cpu/
```

#### 2. 安装 PaddleOCR

```bash
python -m pip install -U "paddleocr[doc-parser]"
```

#### 3. 安装 safetensors（GPU 版本）

```bash
python -m pip install https://paddle-whl.bj.bcebos.com/nightly/cu126/safetensors/safetensors-0.6.2.dev0-cp38-abi3-linux_x86_64.whl
```

> **注意**：Windows 用户建议使用 WSL 或 Docker 容器。

### 基本使用

#### CLI 命令行方式

```bash
paddleocr doc_parser -i https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png
```

#### Python API 方式

```python
from paddleocr import PaddleOCRVL

pipeline = PaddleOCRVL()

output = pipeline.predict("https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png")

for res in output:
    res.print()
    res.save_to_json(save_path="output")
    res.save_to_markdown(save_path="output")
```

### 加速推理（vLLM Server）

对于大规模生产环境，可以使用 vLLM 推理服务器加速：

#### 1. 启动 vLLM 服务器

```bash
docker run \
  --rm \
  --gpus all \
  --network host \
  ccr-2vdh3abv-pub.cnc.bj.baidubce.com/paddlepaddle/paddlex-genai-vllm-server
```

默认端口为 8080。

#### 2. 使用加速服务

**CLI 方式：**

```bash
paddleocr doc_parser \
  -i https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png \
  --vl_rec_backend vllm-server \
  --vl_rec_server_url http://127.0.0.1:8080/v1
```

**Python API 方式：**

```python
from paddleocr import PaddleOCRVL

pipeline = PaddleOCRVL(
    vl_rec_backend="vllm-server",
    vl_rec_server_url="http://127.0.0.1:8080/v1"
)

output = pipeline.predict("https://paddle-model-ecology.bj.bcebos.com/paddlex/imgs/demo_image/paddleocr_vl_demo.png")

for res in output:
    res.print()
    res.save_to_json(save_path="output")
    res.save_to_markdown(save_path="output")
```

### 使用 transformers 库

PaddleOCR-VL-0.9B 也支持通过 transformers 库进行推理：

```python
from PIL import Image
import torch
from modelscope import AutoModelForCausalLM, AutoProcessor

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

CHOSEN_TASK = "ocr"  # 可选: 'ocr' | 'table' | 'chart' | 'formula'

PROMPTS = {
    "ocr": "OCR:",
    "table": "Table Recognition:",
    "formula": "Formula Recognition:",
    "chart": "Chart Recognition:",
}

model_path = "PaddlePaddle/PaddleOCR-VL"
image_path = "test.png"

image = Image.open(image_path).convert("RGB")

model = AutoModelForCausalLM.from_pretrained(
    model_path, trust_remote_code=True, torch_dtype=torch.bfloat16
).to(DEVICE).eval()

processor = AutoProcessor.from_pretrained(model_path, trust_remote_code=True)

messages = [
    {
        "role": "user",
        "content": [
            {"type": "image", "image": image},
            {"type": "text", "text": PROMPTS[CHOSEN_TASK]},
        ]
    }
]

inputs = processor.apply_chat_template(
    messages,
    tokenize=True,
    add_generation_prompt=True,
    return_dict=True,
    return_tensors="pt"
).to(DEVICE)

outputs = model.generate(**inputs, max_new_tokens=1024)
outputs = processor.batch_decode(outputs, skip_special_tokens=True)[0]

print(outputs)
```

> **注意**：transformers 方式目前仅支持元素级识别，推荐使用官方方式以获得更快的推理速度和页面级文档解析能力。

---

## 方案二：Ollama 方案

### 安装 Ollama

```bash
# Linux/macOS
curl -fsSL https://ollama.com/install.sh | sh

# 或访问 https://ollama.com 下载安装包
```

### 拉取 PaddleOCR-VL 模型

```bash
ollama pull MedAIBase/PaddleOCR-VL
```

### 使用方式

#### CLI 方式

```bash
ollama run MedAIBase/PaddleOCR-VL
```

然后在交互式界面中输入图片路径和提示词。

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
            "model": "MedAIBase/PaddleOCR-VL",
            "prompt": "OCR:",
            "images": [image_data]
        }
    )
    
    return response.json()

result = ocr_with_ollama("test.png")
print(result)
```

---

## 方案三：llama.cpp + GGUF 方案

### 下载 GGUF 模型

#### 从 ModelScope 下载

```bash
# 安装 ModelScope
pip install modelscope

# SDK 下载
from modelscope import snapshot_download
model_dir = snapshot_download('megemini/PaddleOCR-VL-1.5-GGUF')
```

#### 或 Git 克隆

```bash
git clone https://www.modelscope.cn/megemini/PaddleOCR-VL-1.5-GGUF.git
```

### 安装 llama.cpp

```bash
# 克隆仓库
git clone https://github.com/ggml-org/llama.cpp
cd llama.cpp

# 编译（GPU 版本）
cmake -B build -DGGML_CUDA=ON
cmake --build build --config Release -j

# 编译（CPU 版本）
cmake -B build
cmake --build build --config Release -j
```

### 运行推理

#### CLI 方式

```bash
./build/bin/llama-cli \
  -m PaddleOCR-VL-1.5-GGUF.gguf \
  --mmproj PaddleOCR-VL-1.5-GGUF-mmproj.gguf \
  --image document.jpg \
  -p "OCR:"
```

#### Server 方式

```bash
./build/bin/llama-server \
  -m PaddleOCR-VL-1.5-GGUF.gguf \
  --mmproj PaddleOCR-VL-1.5-GGUF-mmproj.gguf \
  --port 8111 \
  --host 0.0.0.0 \
  --ctx-size 131072 \
  -n 4096 \
  --temp 0 \
  --jinja
```

然后通过 HTTP API 调用：

```python
import requests
import base64

def ocr_with_llama_server(image_path):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8111/v1/chat/completions",
        json={
            "model": "PaddleOCR-VL-1.5",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "OCR:"}
                    ]
                }
            ]
        }
    )
    
    return response.json()

result = ocr_with_llama_server("test.png")
print(result)
```

### Prompt 使用说明

PaddleOCR-VL-1.5 支持多种 Prompt：

```python
PROMPTS = {
    "ocr": "OCR:",                           # 文本识别
    "table": "Table Recognition:",           # 表格识别
    "formula": "Formula Recognition:",       # 公式识别
    "chart": "Chart Recognition:",           # 图表识别
    "markdown": "Convert to markdown:",      # 转换为 Markdown
}
```

---

## 性能对比

### 模型规格

| 模型 | 参数量 | 模型大小 | 推理速度 | 适用场景 |
|------|--------|----------|----------|----------|
| PaddleOCR-VL-0.9B | 0.9B | ~2GB | 快速 | 边缘设备、实时应用 |
| PaddleOCR-VL-1.5 | 1.5B | ~3GB | 中等 | 通用场景 |

### 基准测试结果

#### OmniDocBench v1.5

PaddleOCR-VL 在文本、公式、表格和阅读顺序等指标上均达到 SOTA 性能。

#### 元素级识别

- **文本识别**：在多语言和多种文本类型上表现优异
- **表格识别**：支持中英文混合、无边框表格等复杂场景
- **公式识别**：支持印刷体、手写体、复杂公式
- **图表识别**：支持 11 种图表类型

---

## 常见问题

### Q1: 为什么输出格式不正确？

PaddleOCR-VL 是一个 VLM 架构的模型，输出取决于 Prompt。请确保使用正确的 Prompt 格式。

### Q2: 如何输出 Markdown 格式？

使用 `Convert to markdown:` 作为 Prompt，或在官方 API 中使用 `save_to_markdown()` 方法。

### Q3: 模型出现答非所问或死循环怎么办？

作为小模型，PaddleOCR-VL 可能存在以下问题：
- 答非所问：检查 Prompt 是否正确
- 死循环：调整 `max_new_tokens` 或 `temperature` 参数

### Q4: Windows 下如何部署？

建议使用以下方式之一：
- WSL（Windows Subsystem for Linux）
- Docker 容器
- 虚拟机

---

## 最佳实践

### 1. 选择合适的部署方案

| 场景 | 推荐方案 |
|------|----------|
| 生产环境、高性能需求 | PaddleOCR 官方 + vLLM Server |
| 快速原型开发 | Ollama |
| 低资源环境 | llama.cpp + GGUF |

### 2. 优化推理性能

- 使用 GPU 加速
- 启用 vLLM 推理服务器
- 调整批处理大小
- 使用量化模型（GGUF）

### 3. 处理不同文档类型

```python
from paddleocr import PaddleOCRVL

pipeline = PaddleOCRVL()

# 处理图片
output = pipeline.predict("document.png")

# 处理 PDF
output = pipeline.predict("document.pdf")

# 处理 URL
output = pipeline.predict("https://example.com/document.png")
```

---

## 参考资源

- **官方文档**：https://cloud.baidu.com/doc/OCR/s/Klxag8wiy
- **Ollama 模型**：https://ollama.com/MedAIBase/PaddleOCR-VL
- **ModelScope GGUF**：https://www.modelscope.cn/models/megemini/PaddleOCR-VL-1.5-GGUF
- **HuggingFace 模型**：https://huggingface.co/PaddlePaddle/PaddleOCR-VL
- **技术报告**：https://arxiv.org/abs/2510.14528
- **GitHub PR**：https://github.com/ggml-org/llama.cpp/pull/18825

---

## 引用

```bibtex
@misc{cui2025paddleocrvlboostingmultilingualdocument,
  title={PaddleOCR-VL: Boosting Multilingual Document Parsing via a 0.9B Ultra-Compact Vision-Language Model},
  author={Cheng Cui and Ting Sun and Suyin Liang and Tingquan Gao and Zelun Zhang and Jiaxuan Liu and Xueqing Wang and Changda Zhou and Hongen Liu and Manhui Lin and Yue Zhang and Yubo Zhang and Handong Zheng and Jing Zhang and Jun Zhang and Yi Liu and Dianhai Yu and Yanjun Ma},
  year={2025},
  eprint={2510.14528},
  archivePrefix={arXiv},
  primaryClass={cs.CV},
  url={https://arxiv.org/abs/2510.14528},
}
```
