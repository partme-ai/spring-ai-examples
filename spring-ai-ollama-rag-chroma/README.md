## spring-ai-ollama-rag-chroma

> 基于 **Spring AI** [Ollama](https://ollama.com/) 嵌入与对话，以及 **Chroma** 向量存储的 RAG 示例。版本与 BOM 由父工程 `spring-ai-examples/pom.xml` 统一管理（Spring AI 1.1.x）。

## 官方文档对应

- [Spring AI Reference — Introduction](https://docs.spring.io/spring-ai/reference/index.html)
- [Embeddings](https://docs.spring.io/spring-ai/reference/api/embeddings.html)
- [Vector Databases](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [ChatClient — RAG](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_retrieval_augmented_generation)

通用 RAG 说明（Ollama + 向量库）见：[docs/2-Spring AI 入门实践/RAG-SHARED.md](../docs/2-Spring AI 入门实践/RAG-SHARED.md)


### 本模块要点

- 向量存储 Starter：`spring-ai-starter-vector-store-chroma`
- 请先启动 **Ollama** 与本模块所需的 **Chroma** 服务，再修改 `src/main/resources/application.yml` 中的连接与模型名。

### 运行

```bash
cd spring-ai-ollama-rag-chroma
mvn spring-boot:run
```

### 故障排除

- 无法连接向量库：检查地址、端口、认证与网络。
- 嵌入失败：确认 Ollama 已拉取 `application.yml` 中配置的嵌入模型。
