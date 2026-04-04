# Spring AI 增强扩展

> **定位**：在 Spring AI 能力边界之外或需二次集成的能力——观测栈、厂商适配、网关、与外部 GenAI 栈组合等。**`spring-ai-examples`** 内以独立子模块提供可运行示例；monorepo 根目录另有网关、Starter 等工程。

## 与「入门实践」的分工

- **入门实践**长文（如 Observability、多厂商 Chat）侧重：**按 Spring AI 官方能力跑通**、与 Reference 章节对齐。
- **本页**侧重：**集成对象**（Prometheus、Langfuse、特定云厂商、网关）的**索引、仓库路径、与生产化差异**；无独立代码的条目标注为 **待建设** 或 **外链**。

## 示例清单与代码落点

名称按课程列表用语（中文）：

| 名称 | 代码 / 文档 | 说明 |
|------|-------------|------|
| Spring AI 集成 Prometheus | [`spring-ai-ollama-observation-prometheus`](../../spring-ai-ollama-observation-prometheus/) | Micrometer / Actuator 导出；深度用法见入门实践 [spring-ai-observability.md](../2-Spring%20AI%20入门实践/spring-ai-observability.md) |
| Spring AI 集成 LangFuse | [`spring-ai-ollama-observation-langfuse`](../../spring-ai-ollama-observation-langfuse/) | 追踪与 Prompt 版本对比；配置见模块 `application.properties` |
| Spring AI 集成 Pangu 大模型 | [`spring-ai-huaweiai-pangu`](../../spring-ai-huaweiai-pangu/) | 与 PartMe 维护的 [`spring-ai-starter-model-huawei-pangu`](../../../spring-ai-starter-model-huawei-pangu/) 配套使用时可对照 Starter 文档 |
| Spring AI 集成【 文心一言】大模型 | [`spring-ai-qianfan`](../../spring-ai-qianfan/) | 百度千帆 / 文心产品线以[官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/)为准 |
| Spring AI 集成 PaddleOCR-VL 本地部署 | [文档](./5、Spring%20AI%20增强扩展：Spring%20AI%20集成%20PaddleOCR-VL%20本地部署.md) | PaddleOCR-VL 本地部署实践，支持 Ollama / llama.cpp 方案 |
| Spring AI 集成 DeepSeek-OCR 本地部署 | [文档](./6、Spring%20AI%20增强扩展：Spring%20AI%20集成%20DeepSeek-OCR%20本地部署.md) | DeepSeek-OCR 本地部署实践，支持 Transformers / vLLM / llama.cpp 方案 |
| Spring AI 集成 GLM-OCR 本地部署 | [`spring-ai-ollama-ocr-glm`](../../spring-ai-ollama-ocr-glm/) | GLM-OCR 本地部署实践，OmniDocBench V1.5 第一名，支持 Ollama / vLLM / SGLang |
| Spring AI 集成 Qianfan-OCR 本地部署 | [`spring-ai-ollama-ocr-qianfan`](../../spring-ai-ollama-ocr-qianfan/) | Qianfan-OCR 本地部署实践，端到端模型 SOTA，支持 vLLM / GGUF 方案 |

## Spring AI 入门实践：Spring AI 生成式人工智能堆栈（GenAI Stack）的 GenAI Stack Neo4j、LangChain、Ollama

多栈组合与部署属**架构级主题**，本仓库 **无独立 `spring-ai-examples` 模块** 覆盖「LangChain + Spring AI」混合栈。建议以外链与概念说明为主：

- [Neo4j GenAI](https://neo4j.com/docs/genai/)、[LangChain](https://python.langchain.com/)、[Ollama](https://ollama.com/) 各自文档。
- 若仅使用 **Neo4j 向量索引**，可对照 [`spring-ai-ollama-rag-neo4j`](../../spring-ai-ollama-rag-neo4j/) 模块（纯 Spring AI 路径）。

## 云厂商对话 / 多模态示例（增量索引）

以下模块均在 **`spring-ai-examples/`** 根下，可按「**Spring AI 集成 &lt;厂商&gt;**」类中文标题扩展子模块 README 或短文：

| 模块目录 | 说明 |
|----------|------|
| `spring-ai-openai` | OpenAI |
| `spring-ai-azure-openai` | Azure OpenAI |
| `spring-ai-anthropic` | Anthropic Claude |
| `spring-ai-amazon-bedrock` | AWS Bedrock |
| `spring-ai-vertexai-gemini` | Google Gemini（Vertex） |
| `spring-ai-zhipuai` | 智谱 |
| `spring-ai-moonshotai` | Moonshot |
| `spring-ai-deepseek` | DeepSeek |
| `spring-ai-mistralai` | Mistral |
| `spring-ai-minimax` | MiniMax |
| `spring-ai-coze` | Coze / 扣子 |
| `spring-ai-huggingface` | Hugging Face |
| `spring-ai-watsonxai` | IBM watsonx |
| `spring-ai-oci-genai-cohere` | Oracle OCI GenAI |
| `spring-ai-llmsfreeapi` | LLMs Free API 聚合 |

## 网关与基础设施（仓库外）

| 对外标题 | 路径 | 说明 |
|----------|------|------|
| Spring AI 增强扩展：Spring AI Gateway（网关能力） | monorepo：`spring-ai-gateway/` | 与示例工程分离；路由、限流、观测等见该工程 README |

## 深度应用

- 端到端业务场景见 **[4-Spring AI 项目实践](../4-Spring%20AI%20项目实践/README.md)**。
