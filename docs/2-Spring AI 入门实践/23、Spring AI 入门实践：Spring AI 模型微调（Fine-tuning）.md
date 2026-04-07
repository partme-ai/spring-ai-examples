# Spring AI 模型微调（Fine-tuning）

> **对外标题**：Spring AI 入门实践：Spring AI 模型微调（Fine-tuning）

> 系列第十四篇，对应 **`spring-ai-ollama-fine-tuning`**：侧重 **Ollama 自定义模型** 与本地对话演示；**LoRA / QLoRA** 训练多在离线环境完成，再导入 Ollama（详见模块 README 与外部教程链接）。

## 目录

- [流程概览](#流程概览)
- [官方文档](#官方文档)
- [本模块内容](#本模块内容)
- [运行](#运行)
- [扩展阅读](#扩展阅读)

## 流程概览

1. **离线**：在训练框架中对基座模型做 LoRA/QLoRA 等微调，导出权重。  
2. **Ollama**：通过 **Modelfile** 或导入方式构建自定义标签。  
3. **Spring AI**：`spring.ai.ollama.chat.options.model` 指向该标签，照常 `ChatModel` 调用。

## 官方文档

- [Chat Model — Ollama](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)  
- Ollama 官方：[Importing Models](https://github.com/ollama/ollama)（以当前文档为准）

## 本模块内容

包内包含 **`ChatController`**、**`ChatService`**、**`ChatRequest`** 与 **`static/index.html`** 等，用于验证 **自定义 Ollama 模型** 的对话效果；微调数学细节见 `spring-ai-ollama-fine-tuning/README.md` 中的外部链接。

## 运行

```bash
cd spring-ai-ollama-fine-tuning
mvn spring-boot:run
```

浏览器打开静态页或调用接口，确认模型名与本地 **`ollama list`** 一致。

## 扩展阅读

- 上一篇：[Observability](./spring-ai-observability.md)  
- 下一篇：[应用实战](../4-Spring%20AI%20项目实践/spring-ai-application-practice.md)
