# Spring AI Examples 简介

> Spring AI Examples 是以 Spring Boot 3.4.x 为基础，构建的 Spring AI 项目快速学习示例；

- 1、继承 Spring Boot 3.4.x，具备 Spring Boot 的所有特性
- 2、集成 Spring AI 1.0.0，提供统一的AI模型接入能力
- 3、支持多种AI模型提供商：OpenAI、Anthropic、Azure OpenAI、Ollama等
- 4、集成各类向量数据库：Chroma、Elasticsearch、MongoDB、Neo4j、Pinecone等
- 5、支持多种AI能力：文本生成、对话、图像识别、语音处理、RAG检索等
- 6、提供完整的AI应用开发示例，快速构建AI应用

## Spring AI Examples 说明

该项目主要用于展示Spring AI的各种使用场景，提供完整的AI应用开发示例和最佳实践！

**Maven模块分类**：

| 分类  | 模块数量 | 说明  |
| ------------ | ------------ | ------------ |
| **AI模型集成** | 15个 | 集成各种AI模型提供商，包括OpenAI、Anthropic、Azure OpenAI、Ollama、智谱AI、百度千帆、华为AI等 |
| **Ollama核心功能** | 8个 | 基于Ollama的文本生成、对话、图像识别、函数调用、Embedding、微调等核心功能 |
| **RAG检索增强** | 15个 | 支持多种向量数据库的RAG实现，包括Chroma、Elasticsearch、MongoDB、Neo4j、Pinecone、Redis等 |
| **语音处理** | 6个 | 支持语音识别、语音合成、语音助手等功能，集成Whisper、ChatTTS、EdgeTTS等 |
| **监控与日志** | 2个 | 集成Prometheus监控和Langfuse日志追踪 |
| **应用示例** | 4个 | 提供SQL查询、命名服务、Agent代理、MVP客户端/服务器等应用示例 |

**注意事项**：

- 1、所有模块均基于Spring Boot 3.4.x和Spring AI 1.0.0构建
- 2、Ollama相关模块需要本地安装Ollama服务
- 3、部分模块需要配置相应的API密钥（如OpenAI、智谱AI等）
- 4、RAG模块需要配置相应的向量数据库服务
- 5、语音模块需要安装相应的语音处理工具

**结构说明**

```
|--spring-ai-examples
|----pom.xml                                    #父级POM，统一管理依赖版本
|----README.md                                  #项目说明文档
|----restart.sh                                 #项目重启脚本
|----spring-ai-anthropic                     #Anthropic Claude集成
|----spring-ai-azure-openai                  #Azure OpenAI集成
|----spring-ai-bedrockai                        #AWS Bedrock集成
|----spring-ai-coze                             #Coze平台集成
|----spring-ai-deepseek                         #DeepSeek模型集成
|----spring-ai-huaweiai-gallery                 #华为AI Gallery集成
|----spring-ai-huaweiai-pangu                   #华为盘古模型集成
|----spring-ai-huggingface                      #Hugging Face集成
|----spring-ai-llmsfreeapi                      #免费API集成
|----spring-ai-minimax                          #MiniMax集成
|----spring-ai-mistralai                        #Mistral AI集成
|----spring-ai-moonshotai                       #Moonshot AI集成
|----spring-ai-openai                           #OpenAI集成
|----spring-ai-qianfan                          #百度千帆集成
|----spring-ai-qwen                             #通义千问集成
|----spring-ai-sensenova                        #商汤科技集成
|----spring-ai-stabilityai                      #Stability AI集成
|----spring-ai-vertexai-gemini                  #Google Vertex AI Gemini集成
|----spring-ai-vertexai-palm2                   #Google Vertex AI PaLM2集成
|----spring-ai-watsonxai                        #IBM Watson AI集成
|----spring-ai-zhipuai                          #智谱AI集成
|----spring-ai-naming                           #AI命名服务示例
|----spring-ai-sql                              #AI SQL查询示例
|----spring-ai-stepfun                          #StepFun集成
|----spring-ai-pangu                            #盘古模型集成
|----spring-ai-ollama-generation                #Ollama文本生成
|----spring-ai-ollama-chat                      #Ollama对话功能
|----spring-ai-ollama-prompt                    #Ollama提示工程
|----spring-ai-ollama-vision                    #Ollama图像识别
|----spring-ai-ollama-local-model               #Ollama本地模型
|----spring-ai-ollama-function-calling          #Ollama函数调用
|----spring-ai-ollama-embedding                 #Ollama向量嵌入
|----spring-ai-ollama-fine-tuning               #Ollama模型微调
|----spring-ai-ollama-agents                    #Ollama智能代理
|----spring-ai-ollama-mvp-client                #Ollama MVP客户端
|----spring-ai-ollama-mvp-server                #Ollama MVP服务器
|----spring-ai-ollama-rag-cassandra             #RAG + Cassandra
|----spring-ai-ollama-rag-chroma                #RAG + Chroma
|----spring-ai-ollama-rag-es                    #RAG + Elasticsearch
|----spring-ai-ollama-rag-gemfire               #RAG + GemFire
|----spring-ai-ollama-rag-hanadb                #RAG + HANA DB
|----spring-ai-ollama-rag-mariadb               #RAG + MariaDB
|----spring-ai-ollama-rag-milvus                #RAG + Milvus
|----spring-ai-ollama-rag-mongodb               #RAG + MongoDB
|----spring-ai-ollama-rag-neo4j                 #RAG + Neo4j
|----spring-ai-ollama-rag-opensearch            #RAG + OpenSearch
|----spring-ai-ollama-rag-pgvector              #RAG + PostgreSQL Vector
|----spring-ai-ollama-rag-pinecone              #RAG + Pinecone
|----spring-ai-ollama-rag-qdrant                #RAG + Qdrant
|----spring-ai-ollama-rag-redis                 #RAG + Redis
|----spring-ai-ollama-rag-typesense             #RAG + Typesense
|----spring-ai-ollama-rag-weaviate              #RAG + Weaviate
|----spring-ai-ollama-voice-whisper             #语音识别(Whisper)
|----spring-ai-ollama-voice-chattts             #语音合成(ChatTTS)
|----spring-ai-ollama-voice-edgetts             #语音合成(EdgeTTS)
|----spring-ai-ollama-voice-emoti               #语音合成(Emoti)
|----spring-ai-ollama-voice-mars5tts            #语音合成(MARS5)
|----spring-ai-ollama-voice-assistant           #语音助手
|----spring-ai-ollama-langfuse                  #Langfuse日志追踪
|----spring-ai-ollama-prometheus                #Prometheus监控
```

## 核心功能特性

### 1. AI模型集成

- **OpenAI系列**：GPT-4、GPT-3.5等模型集成
- **Anthropic系列**：Claude系列模型集成
- **Azure OpenAI**：微软Azure云服务集成
- **Ollama本地模型**：支持本地部署的各种开源模型
- **国产AI模型**：智谱AI、百度千帆、华为盘古、通义千问等

### 2. 多模态AI能力

- **文本生成**：支持各种文本生成任务
- **对话系统**：多轮对话、上下文管理
- **图像识别**：基于Ollama的图像理解能力
- **语音处理**：语音识别、语音合成、语音助手
- **函数调用**：支持工具调用和外部API集成

### 3. RAG检索增强

- **向量数据库支持**：15种主流向量数据库
- **文档检索**：基于语义的文档检索
- **知识库构建**：自动化的知识库构建流程
- **检索优化**：多种检索策略和优化方案

### 4. 开发工具

- **监控告警**：Prometheus指标监控
- **日志追踪**：Langfuse日志分析
- **API文档**：Swagger自动生成
- **测试支持**：完整的测试用例

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Spring Boot 3.4.x
- Spring AI 1.0.0

### 安装Ollama（可选）

```bash
# 安装Ollama
curl -fsSL https://ollama.com/install.sh | sh

# 下载模型
ollama pull qwen2
ollama pull llama3
ollama pull mistral
```

### 运行示例

```bash
# 克隆项目
git clone https://github.com/your-repo/spring-ai-examples.git

# 进入项目目录
cd spring-ai-examples

# 运行特定模块
cd spring-ai-ollama-chat
mvn spring-boot:run
```

## 技术栈

### 核心框架

- **Spring Boot 3.4.x**：应用框架
- **Spring AI 1.0.0**：AI集成框架
- **Spring WebFlux**：响应式Web框架

### 数据库支持

- **向量数据库**：Chroma、Elasticsearch、MongoDB、Neo4j、Pinecone等
- **关系数据库**：PostgreSQL、MySQL、MariaDB等
- **内存数据库**：Redis、GemFire等

### 监控与日志

- **Prometheus**：指标监控
- **Langfuse**：AI应用日志追踪
- **Log4j2**：日志框架

### 工具库

- **Fastjson2**：JSON处理
- **Guava**：Google工具库
- **Caffeine**：缓存库
- **Commons Lang3**：Apache工具库

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 许可证

本项目采用MIT许可证，详见LICENSE文件。