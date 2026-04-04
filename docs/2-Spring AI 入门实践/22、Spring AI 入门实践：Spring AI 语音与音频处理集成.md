# Spring AI 语音与音频处理集成

> **对外标题**：Spring AI 入门实践：Spring AI 语音与音频处理集成

> 系列第十二篇（文稿文件：`spring-ai-audio-integration.md`）。仓库使用 **`spring-ai-ollama-audio-*`** 前缀（非 `voice`），覆盖 **语音识别（Whisper）**、**多种 TTS（EdgeTTS、ChatTTS、Emoti、MARS5、Unified）** 等与 Ollama 文本链路组合。

## 目录

- [能力划分](#能力划分)
- [官方文档](#官方文档)
- [子模块一览](#子模块一览)
- [运行与依赖](#运行与依赖)
- [扩展阅读](#扩展阅读)

## 能力划分

- **Speech-to-Text**：`spring-ai-ollama-audio-whisper` 等，对接 Whisper/本地模型。  
- **Text-to-Speech**：`spring-ai-ollama-audio-edgetts`、`chattts`、`emoti`、`mars5tts`、`unifiedtts` 等，各厂商 SDK 与音频管线不同。

## 官方文档

- [Audio Transcriptions](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)  
- [Audio Speech](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)

## 子模块一览

| 模块 | 说明（以各模块 README 为准） |
|------|-------------------------------|
| `spring-ai-ollama-audio-whisper` | 语音识别 |
| `spring-ai-ollama-audio-edgetts` | Edge-TTS |
| `spring-ai-ollama-audio-chattts` | ChatTTS |
| `spring-ai-ollama-audio-emoti` | Emoti |
| `spring-ai-ollama-audio-mars5tts` | MARS5 |
| `spring-ai-ollama-audio-unifiedtts` | 统一 TTS 抽象 |

## 运行与依赖

```bash
cd spring-ai-ollama-audio-edgetts
mvn spring-boot:run
```

各模块可能依赖 **本机 Python / Edge-TTS CLI / Ollama** 等，详见子目录 **`README.md`**（如 `spring-ai-ollama-audio-edgetts/README.md` 中的环境与端口说明）。

## 扩展阅读

- 上一篇：[MCP](./spring-ai-mcp.md)  
- 下一篇：[Observability](./spring-ai-observability.md)
