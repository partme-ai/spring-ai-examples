# DeepSeek-OCR 本地部署实践指南

## 概述

DeepSeek-OCR 2 是 DeepSeek 团队发布的第二代视觉语言模型，采用了创新的 Visual Causal Flow 架构，在文档理解与 OCR 任务上实现了突破性进展。该模型在 OmniDocBench 等权威基准测试中取得了优异成绩，能够处理多种复杂文档场景。

### 核心特性

- **Visual Causal Flow 架构**：创新的视觉因果流设计，实现更高效的视觉信息提取
- **动态分辨率支持**：支持 (0-6)×768×768 + 1×1024×1024 的动态分辨率
- **多场景覆盖**：支持文本识别、表格识别、公式识别、图表理解等多种任务
- **高性能基准**：在 OmniDocBench 和 OCRBench 上达到 SOTA 水平
- **双语支持**：中英文文档处理能力突出

## 部署方案

本文介绍两种本地部署方案：

1. **HuggingFace Transformers 方案**（推荐）
2. **llama.cpp + GGUF 方案**

---

## 方案一：HuggingFace Transformers 方案（推荐）

### 环境要求

- Python 3.12+
- CUDA 11.8+（GPU 推理）
- PyTorch 2.6.0+
- transformers 4.46.3+

### 安装依赖

```bash
# 创建虚拟环境（推荐）
python -m venv venv
source venv/bin/activate  # Linux/macOS
# venv\Scripts\activate  # Windows

# 安装 PyTorch
pip install torch==2.6.0

# 安装 transformers 和相关依赖
pip install transformers==4.46.3
pip install tokenizers==0.20.3
pip install einops
pip install addict
pip install easydict

# 安装 flash-attn（可选，加速推理）
pip install flash-attn==2.7.3 --no-build-isolation
```

### 基本使用

```python
from transformers import AutoModel, AutoTokenizer
import torch
import os

os.environ["CUDA_VISIBLE_DEVICES"] = '0'

model_name = 'deepseek-ai/DeepSeek-OCR-2'

# 加载模型和分词器
tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
model = AutoModel.from_pretrained(
    model_name,
    _attn_implementation='flash_attention_2',
    trust_remote_code=True,
    use_safetensors=True
)
model = model.eval().cuda().to(torch.bfloat16)

# 准备输入
prompt = "<image>\n<|grounding|>Convert the document to markdown. "
image_file = 'your_image.jpg'
output_path = 'your/output/dir'

# 执行推理
res = model.infer(
    tokenizer,
    prompt=prompt,
    image_file=image_file,
    output_path=output_path,
    base_size=1024,
    image_size=768,
    crop_mode=True,
    save_results=True
)

print(res)
```

### Prompt 使用说明

DeepSeek-OCR 2 支持多种 Prompt 格式：

```python
# 文档转 Markdown（带布局）
prompt = "<image>\n<|grounding|>Convert the document to markdown."

# 纯 OCR（无布局）
prompt = "<image>\nFree OCR."

# 表格识别
prompt = "<image>\n<|grounding|>Extract the table content."

# 公式识别
prompt = "<image>\n<|grounding|>Extract the formula."
```

### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `prompt` | str | 必填 | 输入提示词 |
| `image_file` | str | 必填 | 图片路径或 URL |
| `output_path` | str | None | 输出目录 |
| `base_size` | int | 1024 | 基础分辨率 |
| `image_size` | int | 768 | 图片尺寸 |
| `crop_mode` | bool | True | 是否裁剪模式 |
| `save_results` | bool | True | 是否保存结果 |

### 处理批量图片

```python
from transformers import AutoModel, AutoTokenizer
import torch
import glob
import os

model_name = 'deepseek-ai/DeepSeek-OCR-2'

tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
model = AutoModel.from_pretrained(
    model_name,
    _attn_implementation='flash_attention_2',
    trust_remote_code=True,
    use_safetensors=True
)
model = model.eval().cuda().to(torch.bfloat16)

prompt = "<image>\n<|grounding|>Convert the document to markdown."

# 批量处理
image_files = glob.glob("documents/*.jpg")

for image_file in image_files:
    print(f"Processing: {image_file}")
    res = model.infer(
        tokenizer,
        prompt=prompt,
        image_file=image_file,
        output_path="output",
        save_results=True
    )
```

---

## 方案二：llama.cpp + GGUF 方案

### GGUF 模型下载

Reza2kn 对百度 Qianfan-OCR 进行了 GGUF 量化，Qianfan-OCR 基于 InternVL Chat 架构，使用 Qwen3 作为 LLM 骨干，约 4.7B 参数。

#### 模型列表

| 文件名 | 量化 | 大小 | 质量 |
|--------|------|------|------|
| Qianfan-OCR-f16.gguf | F16 | ~9.4 GB | 无损（半精度） |
| Qianfan-OCR-q8_0.gguf | Q8_0 | ~5.0 GB | 近无损 |
| Qianfan-OCR-q6_k.gguf | Q6_K | ~3.8 GB | 优秀 |
| Qianfan-OCR-q5_k_m.gguf | Q5_K_M | ~3.3 GB | 非常好 |
| Qianfan-OCR-q5_k_s.gguf | Q5_K_S | ~3.2 GB | 非常好 |
| Qianfan-OCR-q4_k_m.gguf | Q4_K_M | ~2.8 GB | 好（推荐） |
| Qianfan-OCR-q4_k_s.gguf | Q4_K_S | ~2.7 GB | 好 |
| Qianfan-OCR-q4_0.gguf | Q4_0 | ~2.6 GB | 传统 4 位 |
| Qianfan-OCR-q3_k_m.gguf | Q3_K_M | ~2.2 GB | 中等 |
| Qianfan-OCR-q3_k_s.gguf | Q3_K_S | ~2.1 GB | 中等 |
| Qianfan-OCR-q2_k.gguf | Q2_K | ~1.7 GB | 低质量 |

> **注意**：原始模型为 `baidu/Qianfan-OCR`，基准测试结果：OmniDocBench 93.12、OCRBench 880。

#### 从 HuggingFace 下载

```bash
# 安装 huggingface-cli
pip install huggingface-hub

# 下载模型
huggingface-cli download Reza2kn/Qianfan-OCR-GGUF --local-dir ./Qianfan-OCR-GGUF

# 或使用 Python
from huggingface_hub import snapshot_download
model_dir = snapshot_download(repo_id="Reza2kn/Qianfan-OCR-GGUF")
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
  -m Qianfan-OCR-q4_k_m.gguf \
  --mmproj Qianfan-OCR-mmproj.gguf \
  --image document.jpg \
  -p "Please OCR this document."
```

#### Server 方式

```bash
./build/bin/llama-server \
  -m Qianfan-OCR-q4_k_m.gguf \
  --mmproj Qianfan-OCR-mmproj.gguf \
  --port 8080 \
  --host 0.0.0.0 \
  --ctx-size 8192
```

#### API 调用

```python
import requests
import base64

def ocr_with_llama_server(image_path, server_url="http://localhost:8080"):
    with open(image_path, "rb") as f:
        image_base64 = base64.b64encode(f.read()).decode()
    
    response = requests.post(
        f"{server_url}/v1/chat/completions",
        json={
            "model": "Qianfan-OCR",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "Please OCR this document."}
                    ]
                }
            ]
        }
    )
    
    return response.json()

result = ocr_with_llama_server("test.png")
print(result)
```

---

## vLLM 加速推理

对于生产环境，推荐使用 vLLM 进行推理加速。

### 安装 vLLM

```bash
pip install vllm>=0.6.0
```

### 启动 vLLM 服务器

```bash
vllm serve deepseek-ai/DeepSeek-OCR-2 \
  --dtype half \
  --trust-remote-code \
  --enforce-eager
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
            "model": "deepseek-ai/DeepSeek-OCR-2",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{image_base64}"}},
                        {"type": "text", "text": "<|grounding|>Convert the document to markdown."}
                    ]
                }
            ],
            "max_tokens": 4096,
            "temperature": 0
        }
    )
    
    return response.json()

result = ocr_with_vllm("test.png")
print(result)
```

---

## 性能对比

### 模型规格

| 模型 | 参数量 | 模型大小 | 量化后大小 | 适用场景 |
|------|--------|----------|------------|----------|
| DeepSeek-OCR-2 | ~7B | ~14GB | - | 高精度场景 |
| Qianfan-OCR | 4.7B | ~9.4GB | 2.8GB (Q4_K_M) | 通用场景 |

### 基准测试结果

| 基准 | DeepSeek-OCR-2 | Qianfan-OCR |
|------|----------------|--------------|
| OmniDocBench | SOTA | 93.12 |
| OCRBench | SOTA | 880 |

### 推理速度

| 方案 | GPU | 批量大小 | 速度 |
|------|-----|----------|------|
| Transformers + FlashAttention | A100 | 1 | ~2s/page |
| vLLM | A100 | 8 | ~1s/page |
| llama.cpp (Q4_K_M) | RTX 4090 | 1 | ~3s/page |

---

## 常见问题

### Q1: Flash Attention 安装失败

```bash
# 方案1：使用预编译 wheel
pip install flash-attn --no-build-isolation

# 方案2：回退到 eager 模式
model = AutoModel.from_pretrained(
    model_name,
    _attn_implementation='eager',
    trust_remote_code=True
)
```

### Q2: CUDA 内存不足

```python
# 使用量化模型
model = AutoModel.from_pretrained(
    model_name,
    torch_dtype=torch.float16,  # 改为 float16
    trust_remote_code=True
)

# 或使用 GGUF 量化版本
```

### Q3: 模型输出格式不正确

检查 Prompt 格式，确保包含 `<image>` 和 `<|grounding|>` 标记：

```python
prompt = "<image>\n<|grounding|>Convert the document to markdown."
```

### Q4: 处理大图片内存溢出

调整 `image_size` 和 `base_size` 参数：

```python
res = model.infer(
    tokenizer,
    prompt=prompt,
    image_file=image_file,
    base_size=512,   # 减小基础分辨率
    image_size=384, # 减小图片尺寸
    crop_mode=True,  # 启用裁剪模式
)
```

---

## 最佳实践

### 1. 选择合适的部署方案

| 场景 | 推荐方案 |
|------|----------|
| 生产环境、高精度需求 | Transformers + FlashAttention + vLLM |
| 快速原型开发 | Transformers (eager 模式) |
| 低资源环境 | llama.cpp + GGUF (Q4_K_M) |

### 2. 优化推理性能

```python
# 使用量化
model = AutoModel.from_pretrained(
    model_name,
    torch_dtype=torch.bfloat16,
    quantization_config=...  # 使用 AWQ/GPTQ 量化
)

# 使用批处理
from transformers import AutoProcessor

processor = AutoProcessor.from_pretrained(model_name, trust_remote_code=True)

# 批量处理
images = [Image.open(f).convert("RGB") for f in image_files]
inputs = processor(images=images, return_tensors="pt").to("cuda")
```

### 3. 处理 PDF 文档

```python
from pdf2image import convert_from_path
import pytesseract
from PIL import Image

# PDF 转图片
images = convert_from_path("document.pdf")

# 逐页 OCR
for i, image in enumerate(images):
    image.save(f"page_{i}.jpg", "JPEG")
    # 使用 DeepSeek-OCR 处理
    res = model.infer(tokenizer, prompt=prompt, image_file=f"page_{i}.jpg")
```

---

## 参考资源

- **DeepSeek-OCR-2 官方**：https://huggingface.co/deepseek-ai/DeepSeek-OCR-2
- **Qianfan-OCR GGUF**：https://huggingface.co/Reza2kn/Qianfan-OCR-GGUF
- **原始模型 Qianfan-OCR**：https://huggingface.co/baidu/Qianfan-OCR
- **llama.cpp**：https://github.com/ggml-org/llama.cpp
- **技术报告 v1**：https://arxiv.org/abs/2510.18234
- **技术报告 v2**：https://arxiv.org/abs/2601.20552

---

## 引用

```bibtex
@article{wei2025deepseek,
  title={DeepSeek-OCR: Contexts Optical Compression},
  author={Wei, Haoran and Sun, Yaofeng and Li, Yukun},
  journal={arXiv preprint arXiv:2510.18234},
  year={2025}
}

@article{wei2026deepseek,
  title={DeepSeek-OCR 2: Visual Causal Flow},
  author={Wei, Haoran and Sun, Yaofeng and Li, Yukun},
  journal={arXiv preprint arXiv:2601.20552},
  year={2026}
}
```

---

## 对比总结

| 特性 | PaddleOCR-VL | DeepSeek-OCR-2 |
|------|--------------|----------------|
| 参数量 | 0.9B / 1.5B | ~7B |
| 语言支持 | 109 种 | 中英为主 |
| 部署方式 | PaddleOCR / Ollama / llama.cpp | Transformers / vLLM / llama.cpp |
| 量化支持 | GGUF | GGUF |
| 动态分辨率 | NaViT | Visual Causal Flow |
| 适用场景 | 轻量级、边缘设备 | 高精度、生产环境 |
