# Qianfan-OCR 本地部署实践指南

## 概述

Qianfan-OCR 是百度千帆团队开发的 4B 参数端到端文档智能模型。它将文档解析、布局分析和文档理解统一在单一视觉语言架构中。与传统的多阶段 OCR 流水线不同，Qianfan-OCR 执行直接的图像到 Markdown 转换，支持广泛的提示驱动任务——从结构化文档解析和表格提取到图表理解、文档问答和关键信息提取。

### 核心特性

- **端到端 SOTA**：OmniDocBench v1.5 得分 93.12，端到端模型排名第一
- **Layout-as-Thought**：创新的思考阶段机制，通过 `<think⟩` 标记恢复显式布局分析
- **192 种语言**：支持多语言 OCR
- **高效部署**：W8A8 量化下达到 1.024 PPS（单 A100 GPU）
- **多任务支持**：文档解析、布局分析、表格提取、图表理解、文档问答、关键信息提取

## 部署方案

本文介绍三种本地部署方案：

1. **vLLM 方案**（推荐）
2. **Transformers 方案**
3. **GGUF + llama.cpp 方案**

---

## 方案一：vLLM 方案（推荐）

### 安装 vLLM

```bash
pip install vllm>=0.10.2
```

### 启动服务

```bash
vllm serve baidu/Qianfan-OCR \
  --dtype half \
  --trust-remote-code \
  --max_model_len 32768
```

### API 调用

```python
import requests
import base64

def ocr_with_vllm(image_path, server_url="http://localhost:8000"):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        f"{server_url}/v1/chat/completions",
        json={
            "model": "baidu/Qianfan-OCR",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "Convert this document to markdown."}
                    ]
                }
            ],
            "max_tokens": 8192,
            "temperature": 0
        }
    )
    
    return response.json()

result = ocr_with_vllm("document.png")
print(result)
```

### 启用 Layout-as-Thought

```python
def ocr_with_thinking(image_path):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        "http://localhost:8000/v1/chat/completions",
        json={
            "model": "baidu/Qianfan-OCR",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "<think⟩Convert this document to markdown."}
                    ]
                }
            ]
        }
    )
    
    return response.json()
```

---

## 方案二：Transformers 方案

### 安装依赖

```bash
pip install transformers torch accelerate
```

### 基本使用

```python
from transformers import AutoModelForCausalLM, AutoTokenizer
from PIL import Image
import torch

model_name = "baidu/Qianfan-OCR"

tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.bfloat16,
    device_map="auto",
    trust_remote_code=True
)

image = Image.open("document.png")

conversation = [
    {
        "role": "user",
        "content": [
            {"type": "image", "image": image},
            {"type": "text", "text": "Convert this document to markdown."}
        ]
    }
]

inputs = tokenizer.apply_chat_template(
    conversation,
    add_generation_prompt=True,
    return_tensors="pt",
    return_dict=True
).to(model.device)

outputs = model.generate(
    **inputs,
    max_new_tokens=8192,
    do_sample=False
)

result = tokenizer.decode(outputs[0][inputs["input_ids"].shape[1]:], skip_special_tokens=True)
print(result)
```

---

## 方案三：GGUF + llama.cpp 方案

### 下载 GGUF 模型

```bash
# 从 HuggingFace 下载
pip install huggingface-hub

huggingface-cli download Reza2kn/Qianfan-OCR-GGUF --local-dir ./Qianfan-OCR-GGUF
```

### 模型列表

| 文件名 | 量化 | 大小 | 质量 |
|--------|------|------|------|
| Qianfan-OCR-f16.gguf | F16 | ~9.4 GB | 无损 |
| Qianfan-OCR-q8_0.gguf | Q8_0 | ~5.0 GB | 近无损 |
| Qianfan-OCR-q6_k.gguf | Q6_K | ~3.8 GB | 优秀 |
| Qianfan-OCR-q5_k_m.gguf | Q5_K_M | ~3.3 GB | 非常好 |
| Qianfan-OCR-q4_k_m.gguf | Q4_K_M | ~2.8 GB | 好（推荐） |

### 安装 llama.cpp

```bash
git clone https://github.com/ggml-org/llama.cpp
cd llama.cpp

# 编译（GPU 版本）
cmake -B build -DGGML_CUDA=ON
cmake --build build --config Release -j
```

### 运行推理

#### CLI 方式

```bash
./build/bin/llama-cli \
  -m Qianfan-OCR-q4_k_m.gguf \
  --mmproj Qianfan-OCR-mmproj.gguf \
  --image document.jpg \
  -p "Convert this document to markdown."
```

#### Server 方式

```bash
./build/bin/llama-server \
  -m Qianfan-OCR-q4_k_m.gguf \
  --mmproj Qianfan-OCR-mmproj.gguf \
  --port 8080 \
  --host 0.0.0.0 \
  --ctx-size 32768
```

---

## Prompt 使用说明

### 文档解析

| 任务 | Prompt |
|------|--------|
| 转 Markdown | `Convert this document to markdown.` |
| 转 JSON | `Convert this document to JSON.` |
| 转 HTML | `Convert this document to HTML.` |

### 布局分析

| 任务 | Prompt |
|------|--------|
| 获取布局 | `<think⟩Analyze the layout of this document.` |
| 获取边界框 | `<think⟩Detect all elements with bounding boxes.` |
| 获取阅读顺序 | `<think⟩Analyze the reading order.` |

### 表格提取

| 任务 | Prompt |
|------|--------|
| 提取表格 | `Extract all tables from this document.` |
| 表格转 Markdown | `Convert tables to markdown format.` |

### 图表理解

| 任务 | Prompt |
|------|--------|
| 图表分析 | `Analyze the charts in this document.` |
| 提取数据 | `Extract data from the charts.` |

### 文档问答

```python
prompt = "Question: What is the main topic of this document?"
```

### 关键信息提取

```python
prompt = "Extract the following information: company name, date, total amount."
```

---

## Layout-as-Thought 机制

### 工作原理

Layout-as-Thought 是 Qianfan-OCR 的创新机制，通过 `<think⟩` 标记触发思考阶段：

1. **功能**：在端到端范式中恢复布局分析能力
2. **输出**：结构化布局结果（边界框、元素类型、阅读顺序）
3. **增强**：针对复杂布局、混乱元素或非标准阅读顺序的文档提供针对性精度提升

### 使用场景

| 场景 | 是否启用 |
|------|----------|
| 异构页面（试卷、技术报告、报纸） | 启用 |
| 同构文档（单栏文本、简单表单） | 不启用 |

### 示例

```python
# 启用思考
prompt = "<think⟩Convert this document to markdown."

# 不启用思考
prompt = "Convert this document to markdown."
```

---

## 性能对比

### OmniDocBench v1.5

| 模型 | 类型 | 总分 | 文本编辑↓ | 公式CDM↑ | 表格TEDs↑ |
|------|------|------|----------|----------|----------|
| Qianfan-OCR | 端到端 | 93.12 | 0.041 | 92.43 | 91.02 |
| DeepSeek-OCR-v2 | 端到端 | 91.09 | 0.048 | 90.31 | 87.75 |
| Gemini-3 Pro | 端到端 | 90.33 | 0.065 | 89.18 | 88.28 |

### OCRBench

| 模型 | OCRBench | OCRBenchv2 (en/zh) |
|------|----------|-------------------|
| Qianfan-OCR | 880 | 56.0 / 60.77 |
| Qwen3-VL-4B | 873 | 60.68 / 59.13 |

### 关键信息提取

| 模型 | 总分 | OCRBench KIE | Nanonets KIE |
|------|------|--------------|--------------|
| Qianfan-OCR | 87.9 | 95.0 | 86.5 |
| Qwen3-VL-235B | 84.2 | 94.0 | 83.8 |

### 推理吞吐量

| 模型 | PPS (pages/sec) |
|------|-----------------|
| Qianfan-OCR (W8A8) | 1.024 |
| Qianfan-OCR (W16A16) | 0.503 |
| MinerU 2.5 | 1.057 |

---

## 常见问题

### Q1: 如何启用量化？

```bash
vllm serve baidu/Qianfan-OCR \
  --quantization awq \
  --dtype half
```

### Q2: 处理多页 PDF？

```python
from pdf2image import convert_from_path

images = convert_from_path("document.pdf")

for i, image in enumerate(images):
    image.save(f"page_{i}.jpg", "JPEG")
    # 使用 Qianfan-OCR 处理
```

### Q3: 内存不足？

- 使用 GGUF 量化版本
- 减小 `max_model_len`
- 使用 W8A8 量化

---

## 最佳实践

### 1. 选择合适的部署方案

| 场景 | 推荐方案 |
|------|----------|
| 生产环境、高吞吐 | vLLM + W8A8 量化 |
| 快速开发 | Transformers |
| 低资源环境 | GGUF + llama.cpp |

### 2. 优化推理性能

```python
# 使用量化
model = AutoModelForCausalLM.from_pretrained(
    model_name,
    torch_dtype=torch.bfloat16,
    load_in_8bit=True
)

# 批处理
images = [Image.open(f) for f in image_files]
```

### 3. 选择是否启用思考

```python
# 复杂布局 - 启用思考
prompt = "<think⟩Convert this document to markdown."

# 简单文档 - 不启用思考
prompt = "Convert this document to markdown."
```

---

## 参考资源

- **HuggingFace 模型**：https://huggingface.co/baidu/Qianfan-OCR
- **GGUF 量化**：https://huggingface.co/Reza2kn/Qianfan-OCR-GGUF
- **vLLM 文档**：https://docs.vllm.ai/
- **llama.cpp**：https://github.com/ggml-org/llama.cpp

---

## 引用

```bibtex
@misc{qianfan-ocr,
  title={Qianfan-OCR: A 4B-Parameter End-to-End Document Intelligence Model},
  author={Baidu Qianfan Team},
  year={2025},
  url={https://huggingface.co/baidu/Qianfan-OCR}
}
```

---

## 对比总结

| 特性 | GLM-OCR | Qianfan-OCR | PaddleOCR-VL | DeepSeek-OCR-2 |
|------|---------|-------------|--------------|----------------|
| 参数量 | 0.9B | 4B | 0.9B/1.5B | ~7B |
| OmniDocBench | 94.62 | 93.12 | 94.50 | 91.09 |
| 语言支持 | 多语言 | 192 种 | 109 种 | 中英为主 |
| 特色功能 | MTP 损失 | Layout-as-Thought | 动态分辨率 | Visual Causal Flow |
| 推荐场景 | 边缘部署 | 复杂文档 | 轻量级 | 高精度 |
