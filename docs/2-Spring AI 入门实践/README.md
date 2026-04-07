# Spring AI 入门实践

> 基于 Spring Boot 3.5.6 和 Spring AI 1.1.4 的完整学习指南（具体版本以仓库根 [`pom.xml`](../../pom.xml) 中 `spring-boot-starter-parent` / `spring-ai.version` 为准）。**权威文档**请优先查阅 [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)。

## 📚 文章目录

表格中 **「对外标题」** 采用课程/你方提供的**中文名称**（如「Spring AI 文本生成（Chat Completion API）」「Spring AI 集成 Prometheus」等）；「文档」列为各篇 Markdown 文件名（与本目录同级，第 15 篇在 **[4-Spring AI 项目实践](../4-Spring%20AI%20项目实践/)**）。

### 第一阶段：基础入门

| 序号 | 对外标题 | 文章标题 | 文档 | 状态 | 对应模块 | 预计字数 |
|------|----------|----------|------|------|----------|----------|
| 1 | Spring AI 入门实践：环境准备与快速开始 | Spring AI 环境准备与快速开始 | [spring-ai-quick-start.md](./spring-ai-quick-start.md) | ✅ 已完成 | - | 5000+ |
| 2 | Spring AI 入门实践：Spring AI 文本生成（Chat Completion API） | Spring AI 文本生成（Text Generation API） | [spring-ai-text-generation.md](./spring-ai-text-generation.md) | ✅ 已完成 | `spring-ai-ollama-generation` | 4000+ |
| 3 | Spring AI 入门实践：Spring AI 多轮对话（Chat Completion API）；Spring AI 使用 Ollama Chat | Spring AI 多轮对话（Chat Completion API） | [spring-ai-chat-completion.md](./spring-ai-chat-completion.md) | ✅ 已完成 | `spring-ai-ollama-chat` | 4500+ |
| 4 | Spring AI 入门实践：Spring AI 提示工程（Prompt Engineering） | Spring AI 提示工程（Prompt Engineering） | [spring-ai-prompt-engineering.md](./spring-ai-prompt-engineering.md) | ✅ 已完成 | `spring-ai-ollama-prompt` | 5000+ |
| 5 | Spring AI 入门实践：Spring AI 工具调用（Tool Calling） | Spring AI 工具调用（Tool Calling） | [spring-ai-tool-calling.md](./spring-ai-tool-calling.md) | ✅ 已完成 | `spring-ai-ollama-tools` | 4500+ |

### 第二阶段：核心功能

| 序号 | 对外标题 | 文章标题 | 文档 | 状态 | 对应模块 | 预计字数 |
|------|----------|----------|------|------|----------|----------|
| 6 | Spring AI 入门实践：Spring AI 文本嵌入（Embeddings）；Spring AI 使用 Ollama Embeddings | Spring AI 文本嵌入（Embeddings） | [spring-ai-embeddings.md](./spring-ai-embeddings.md) | ✅ 已完成 | `spring-ai-ollama-embedding` | 4000+ |
| 7 | Spring AI 入门实践：Spring AI 图片生成（Image Generation API） | Spring AI 图片生成（Image Generation API） | [spring-ai-image-generation.md](./spring-ai-image-generation.md) | ✅ 已完成 | `spring-ai-stabilityai` | 4500+ |
| 8 | Spring AI 入门实践：Spring AI 向量数据库（Vector Databases） | Spring AI 向量数据库集成（Vector Databases） | [spring-ai-vector-databases.md](./spring-ai-vector-databases.md) | ✅ 已完成 | `spring-ai-ollama-rag-*` | 6000+ |
| 9 | Spring AI 入门实践：Spring AI 检索增强生成（RAG） | Spring AI 检索增强生成（RAG） | [spring-ai-rag.md](./spring-ai-rag.md) | ✅ 已完成 | `spring-ai-ollama-rag-chroma` | 5500+ |
| 10 | Spring AI 入门实践：Spring AI 智能体（Agents） | Spring AI 智能体（Agents） | [spring-ai-agents.md](./spring-ai-agents.md) | ✅ 已完成 | `spring-ai-ollama-agents` | 5000+ |

### 第三阶段：高级应用

| 序号 | 对外标题 | 文章标题 | 文档 | 状态 | 对应模块 | 预计字数 |
|------|----------|----------|------|------|----------|----------|
| 11 | Spring AI 入门实践：Spring AI 模型上下文协议 (MCP) | Spring AI 模型上下文协议（MCP） | [spring-ai-mcp.md](./spring-ai-mcp.md) | ✅ 已完成 | `spring-ai-ollama-mcp-webmvc-server` 等 `spring-ai-ollama-mcp-*` | 4500+ |
| 12 | Spring AI 入门实践：Spring AI 语音与音频处理集成 | Spring AI 语音与音频处理集成 | [spring-ai-audio-integration.md](./spring-ai-audio-integration.md) | ✅ 已完成 | `spring-ai-ollama-audio-*` | 5000+ |
| 13 | Spring AI 入门实践：Spring AI 监控与可观察性（衔接「Spring AI 集成 Prometheus / LangFuse」） | Spring AI 监控与可观察性 | [spring-ai-observability.md](./spring-ai-observability.md) | ✅ 已完成 | `spring-ai-ollama-observation-prometheus`、`spring-ai-ollama-observation-langfuse` | 4000+ |
| 14 | Spring AI 入门实践：Spring AI 模型微调（Fine-tuning） | Spring AI 模型微调（Fine-tuning） | [spring-ai-fine-tuning.md](./spring-ai-fine-tuning.md) | ✅ 已完成 | `spring-ai-ollama-fine-tuning` | 4000+ |
| 15 | Spring AI 应用实战（自然语言转 SQL、酒店推荐等综合案例） | Spring AI 应用实战案例 | [spring-ai-application-practice.md](../4-Spring%20AI%20项目实践/spring-ai-application-practice.md) | ✅ 已完成 | `spring-ai-sql`、`spring-ai-project-hotel-recommend`（可选扩展 `spring-ai-postgresml`） | 6000+ |

## 🎯 学习目标

### 知识目标

- 掌握 Spring AI 核心概念和 API
- 理解 AI 应用开发的最佳实践
- 学会集成各种 AI 模型和服务
- 掌握 RAG 和向量数据库的使用

### 技能目标

- 能够独立开发 Spring AI 应用
- 掌握多模态 AI 能力集成
- 学会构建智能代理和工具调用
- 具备 AI 应用性能优化能力

### 项目目标

- 完成 15 篇高质量技术文章
- 构建完整的 AI 应用示例
- 形成可复用的开发模板
- 建立最佳实践指南

## 📖 学习路径

### 基础阶段（第 1–5 篇）

**目标**：掌握 Spring AI 基础概念和核心 API。

**学习重点**：环境搭建、文本生成与对话、提示工程、工具调用、基础项目结构。

**实践项目**：由浅入深的对话与工具示例（见各模块 `README.md`）。

### 进阶阶段（第 6–10 篇）

**目标**：深入理解嵌入、图像、向量库、RAG 与 Agent。

**学习重点**：向量嵌入、图像生成、向量数据库、RAG 检索增强、智能体编排。

**实践项目**：智能文档助手（RAG + Ollama）。

### 高级阶段（第 11–15 篇）

**目标**：掌握 MCP、音频、可观测性、微调与综合实战。

**学习重点**：MCP 协议、语音/音频链路、Prometheus / Langfuse、Ollama 微调、NL2SQL 与业务综合案例。

**实践项目**：企业级 AI 能力组合（按模块拆分演练）。

## 🛠️ 技术栈

### 核心框架

- **Spring Boot 3.5.6** — 应用框架（见父 POM）
- **Spring AI 1.1.4** — AI 集成框架（`spring-ai.version`）
- **Java 17** — 编程语言

### AI 模型

- **Ollama** — 本地推理
- **OpenAI / Azure / Anthropic / Bedrock** 等 — 云厂商示例模块
- **Stability AI** 等 — 图像示例

### 向量数据库（示例覆盖）

- **Chroma、Elasticsearch、Redis、PgVector、Milvus** 等 — 见各 `spring-ai-ollama-rag-*` 模块

### 开发工具

- **Maven** — 构建
- **Docker** — 可选，用于向量库 / 中间件
- **Prometheus / Langfuse** — 可观测性示例

## 📁 项目结构（文档与示例）

```
spring-ai-examples/
├── docs/
│   ├── README.md                                    # 文档总入口（四层归类）
│   ├── 2-Spring AI 入门实践/                        # 第 1–14 篇 + RAG-SHARED.md
│   ├── 4-Spring AI 项目实践/                        # 第 15 篇
│   ├── README-TEMPLATE.md、architecture-diagrams.md
│   └── …
├── spring-ai-ollama-generation/
├── spring-ai-ollama-chat/
├── …
└── pom.xml
```

## 🚀 快速开始

### 1. 环境准备

```bash
java -version
mvn -version
# 可选：本地 Ollama
curl -fsSL https://ollama.com/install.sh | sh
```

### 2. 进入工程

```bash
cd spring-ai-examples
```

### 3. 运行示例（节选）

```bash
cd spring-ai-ollama-generation && mvn spring-boot:run
cd ../spring-ai-ollama-chat && mvn spring-boot:run
```

### 4. 阅读顺序

按上表序号阅读 **`docs/2-Spring AI 入门实践/`** 下第 1–14 篇及 **`4-Spring AI 项目实践/`** 下第 15 篇；每篇含配置说明、与仓库代码的对应关系及验证步骤。

## 📝 写作规范

1. **引言** — 背景与学习目标
2. **理论基础** — 核心概念
3. **环境准备** — JDK、Ollama、向量库/API Key 等
4. **官方文档** — [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html) 相关章节链接
5. **代码与项目结构** — 须与 `spring-ai-examples` 中真实包路径、类名一致
6. **运行与验证** — `mvn spring-boot:run`、端口、接口或 Knife4j
7. **最佳实践与扩展阅读**

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](../../LICENSE)。

---

建议按文章顺序学习；RAG 相关请先阅读 [RAG-SHARED.md](./RAG-SHARED.md)。
