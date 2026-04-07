# Spring AI 基础教程

> **定位**：以 [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html) 为主线，做**章节对照、术语与官方链接**；与「入门实践」长文区分——此处偏**官方脉络与速查**，不写与仓库示例逐行对齐的大篇。

## 中文导读（可选）

- [CSDN：Spring AI 入门相关总览](https://blog.csdn.net/isea533/article/details/136744359) — 与当前 Spring AI 版本可能不完全一致，**以 Reference 与本仓库父 `pom.xml` 为准**。

## Spring AI Reference 章节映射（速查）

以下按官方文档常见结构列出入口链接，便于与 **1 基础教程 → 2 入门实践** 对照学习。

| 主题 | Reference 入口 | 入门实践对应文（深度与代码） |
|------|----------------|------------------------------|
| 总览 | [Introduction](https://docs.spring.io/spring-ai/reference/index.html) | [spring-ai-quick-start.md](../2-Spring%20AI%20入门实践/spring-ai-quick-start.md) |
| Chat Model / ChatClient | [Chat Model API](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)、[ChatClient](https://docs.spring.io/spring-ai/reference/api/chatclient.html) | [spring-ai-text-generation.md](../2-Spring%20AI%20入门实践/spring-ai-text-generation.md)、[spring-ai-chat-completion.md](../2-Spring%20AI%20入门实践/spring-ai-chat-completion.md) |
| Prompt | [Prompt](https://docs.spring.io/spring-ai/reference/api/prompt.html) | [spring-ai-prompt-engineering.md](../2-Spring%20AI%20入门实践/spring-ai-prompt-engineering.md) |
| Tools / Function Calling | [Tools](https://docs.spring.io/spring-ai/reference/api/tools.html) | [spring-ai-tool-calling.md](../2-Spring%20AI%20入门实践/spring-ai-tool-calling.md) |
| Embeddings | [Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings.html) | [spring-ai-embeddings.md](../2-Spring%20AI%20入门实践/spring-ai-embeddings.md) |
| Vector Store | [Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html) | [spring-ai-vector-databases.md](../2-Spring%20AI%20入门实践/spring-ai-vector-databases.md) |
| RAG（概念与 ChatClient） | [ChatClient — RAG](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation) | [spring-ai-rag.md](../2-Spring%20AI%20入门实践/spring-ai-rag.md) |
| Image（文生图等） | [AI Concepts — Models](https://docs.spring.io/spring-ai/reference/concepts.html#models)（图像类模型见官方各厂商 Image API 章节） | [spring-ai-image-generation.md](../2-Spring%20AI%20入门实践/spring-ai-image-generation.md) |
| MCP | [MCP](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html) | [spring-ai-mcp.md](../2-Spring%20AI%20入门实践/spring-ai-mcp.md) |
| Observability | [Observability](https://docs.spring.io/spring-ai/reference/observability/index.html) | [spring-ai-observability.md](../2-Spring%20AI%20入门实践/spring-ai-observability.md) |
| Structured Output | [Structured Output](https://docs.spring.io/spring-ai/reference/api/structured-output-converter.html) | 各厂商示例与 ChatClient 篇内提及 |

## 说明

- 本目录可后续按上表**拆短文**（仅最小代码 + 链接），避免与 [2-Spring AI 入门实践](../2-Spring%20AI%20入门实践/README.md) 重复。
- 增强集成与网关见 **[3-Spring AI 增强扩展](../3-Spring%20AI%20增强扩展/Spring%20AI%20增强扩展.md)**；端到端项目见 **[4-Spring AI 项目实践](../4-Spring%20AI%20项目实践/README.md)**。
