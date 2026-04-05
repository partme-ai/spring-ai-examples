package com.github.partmeai.ollama.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;
import java.util.Map;

/**
 * API 请求对象定义
 *
 * <p>包含 Ollama API 交互所需的请求数据结构，如函数工具定义和聊天补全请求。</p>
 *
 * @author teachingai
 */
public class ApiRequest {


    // --------------------------------------------------------------------------
    // Chat & Streaming Chat
    // --------------------------------------------------------------------------

    /**
     * Represents a tool the model may call. Currently, only functions are supported as a
     * tool.
     *
     * @param type The type of the tool. Currently, only 'function' is supported.
     * @param function The function definition.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FunctionTool(@JsonProperty("type") Type type, @JsonProperty("function") Function function) {

        /**
         * Create a tool of type 'function' and the given function definition.
         * @param function function definition.
         */
        @ConstructorBinding
        public FunctionTool(Function function) {
            this(Type.FUNCTION, function);
        }

        /**
         * Create a tool of type 'function' and the given function definition.
         */
        public enum Type {

            /**
             * Function tool type.
             */
            @JsonProperty("function")
            FUNCTION

        }

        /**
         * Function definition.
         *
         * @param description A description of what the function does, used by the model
         * to choose when and how to call the function.
         * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or
         * contain underscores and dashes, with a maximum length of 64.
         * @param parameters The parameters the functions accepts, described as a JSON
         * Schema object. To describe a function that accepts no parameters, provide the
         * value {"type": "object", "properties": {}}.
         */
        public record Function(@JsonProperty("description") String description, @JsonProperty("name") String name,
                               @JsonProperty("parameters") Map<String, Object> parameters) {

            /**
             * Create tool function definition.
             * @param description tool function description.
             * @param name tool function name.
             * @param jsonSchema tool function schema as json.
             */
            @ConstructorBinding
            public Function(String description, String name, String jsonSchema) {
                this(description, name, ModelOptionsUtils.jsonToMap(jsonSchema));
            }
        }
    }

    /**
     * 聊天补全请求对象
     *
     * <p>封装调用聊天模型所需的参数。</p>
     *
     * @param model 所要调用的模型编码
     * @param messages 调用语言模型时，将当前对话信息列表作为提示输入给模型， 按照 {"role": "user", "content": "你好"} 的json 数组形式进行传参； 可能的消息类型包括 System message、User message、Assistant message 和 Tool message。
     * @param requestId 由用户端传参，需保证唯一性；用于区分每次请求的唯一标识，用户端不传时平台会默认生成。
     * @param doSample do_sample 为 true 时启用采样策略，do_sample 为 false 时采样策略 temperature、top_p 将不生效。默认值为 true。
     * @param stream 使用同步调用时，此参数应当设置为 fasle 或者省略。表示模型生成完所有内容后一次性返回所有内容。默认值为 false。
     *               如果设置为 true，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     * @param temperature 采样温度，控制输出的随机性，必须为正数。
     *                    取值范围是：(0.0,1.0]，不能等于 0，默认值为 0.95,值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     *                    建议您根据应用场景调整 top_p 或 temperature 参数，但不要同时调整两个参数
     * @param topP 用温度取样的另一种方法，称为核取样。
     *             取值范围是：(0.0, 1.0) 开区间，不能等于 0 或 1，默认值为 0.7
     *             模型考虑具有 top_p 概率质量tokens的结果
     *             例如：0.1 意味着模型解码器只考虑从前 10% 的概率的候选集中取tokens
     *             建议您根据应用场景调整 top_p 或 temperature 参数，但不要同时调整两个参数
     * @param maxTokens 模型输出最大 tokens，最大输出为8192，默认值为1024
     * @param stop 停止生成的标识列表，当生成内容包含这些标识时停止生成
     * @param tools 可供模型调用的工具列表
     * @param toolChoice 模型选择工具的策略
     * @param user 用户标识，用于追踪和监控
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequest(
            @JsonProperty("request_id") String requestId,
            @JsonProperty("model") String model,
            @JsonProperty("messages") List<ChatCompletionMessage> messages,
            @JsonProperty("do_sample") Boolean doSample,
            @JsonProperty("stream") Boolean stream,
            @JsonProperty("temperature") Double temperature,
            @JsonProperty("top_p") Double topP,
            @JsonProperty("max_tokens") Integer maxTokens,
            @JsonProperty("stop") List<String> stop,
            @JsonProperty("tools") List<FunctionTool> tools,
            @JsonProperty("tool_choice") String toolChoice,
            @JsonProperty("user_id") String user) {

        /**
         * Shortcut constructor for a chat completion request with the given messages and model.
         *
         * @param requestId   A unique identifier for the request.
         * @param model       ID of the model to use.
         * @param messages    A list of messages comprising the conversation so far.
         * @param temperature What sampling temperature to use, between 0 and 1.
         */
        public ChatCompletionRequest(String requestId, String model, List<ChatCompletionMessage> messages, Double temperature) {
            this(requestId, model, messages, null, null, temperature, null,
                    null, null, null, null, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model and control for streaming.
         *
         * @param requestId   A unique identifier for the request.
         * @param model       ID of the model to use.
         * @param messages    A list of messages comprising the conversation so far.
         * @param temperature What sampling temperature to use, between 0 and 1.
         * @param stream      If set, partial message deltas will be sent.Tokens will be sent as data-only server-sent events
         *                    as they become available, with the stream terminated by a data: [DONE] message.
         */
        public ChatCompletionRequest(String requestId, String model, List<ChatCompletionMessage> messages, Double temperature, boolean stream) {
            this(requestId, model, messages, null,  stream, temperature, null, null, null, null, null, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model, tools and tool choice.
         * Streaming is set to false, temperature to 0.8 and all other parameters are null.
         *
         * @param requestId   A unique identifier for the request.
         * @param model      ID of the model to use.
         * @param messages   A list of messages comprising the conversation so far.
         * @param tools      A list of tools the model may call. Currently, only functions are supported as a tool.
         * @param toolChoice Controls which (if any) function is called by the model.
         */
        public ChatCompletionRequest(String requestId,
                                     String model,
                                     List<ChatCompletionMessage> messages,
                                     List<FunctionTool> tools,
                                     String toolChoice) {
            this(requestId, model, messages, null, false, 0.95d, null, null, null, tools, toolChoice, null);
        }

        /**
         * Shortcut constructor for a chat completion request with the given messages, model, tools and tool choice.
         * Streaming is set to false, temperature to 0.8 and all other parameters are null.
         *
         * @param requestId   A unique identifier for the request.
         * @param messages A list of messages comprising the conversation so far.
         * @param stream   If set, partial message deltas will be sent.Tokens will be sent as data-only server-sent events
         *                 as they become available, with the stream terminated by a data: [DONE] message.
         */
        public ChatCompletionRequest(String requestId, List<ChatCompletionMessage> messages, Boolean stream) {
            this(requestId, null, messages, null, stream, null, null, null, null, null, null, null);
        }

        /**
         * 用于控制模型是如何选择要调用的函数，仅当工具类型为function时补充。默认为auto，当前仅支持auto
         */
        public enum ToolChoice {

            @JsonProperty("auto") AUTO

        }

        /**
         * Helper factory that creates a tool_choice of type 'none', 'auto' or selected function by name.
         */
        public static class ToolChoiceBuilder {
            /**
             * Model can pick between generating a message or calling a function.
             */
            public static final String AUTO = "none";
            /**
             * Model will not call a function and instead generates a message
             */
            public static final String NONE = "none";

            /**
             * Specifying a particular function forces the model to call that function.
             */
            public static String FUNCTION(String functionName) {
                return ModelOptionsUtils.toJsonString(Map.of("type", "function", "function", Map.of("name", functionName)));
            }
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionMessage(
            @JsonProperty("content") String content,
            @JsonProperty("role") Role role,
            @JsonProperty("name") String name,
            @JsonProperty("tool_calls") List<ToolCall> toolCalls) {

        /**
         * Create a chat completion message with the given content and role. All other fields are null.
         *
         * @param content The contents of the message.
         * @param role    The role of the author of this message.
         */
        public ChatCompletionMessage(String content, Role role) {
            this(content, role, null, null);
        }

        /**
         * The role of the author of this message.
         */
        public enum Role {
            /**
             * System message.
             */
            @JsonProperty("system") SYSTEM,
            /**
             * User message.
             */
            @JsonProperty("user") USER,
            /**
             * Assistant message.
             */
            @JsonProperty("assistant") ASSISTANT
        }

        /**
         * The relevant tool call.
         *
         * @param id       The ID of the tool call. This ID must be referenced when you submit the tool outputs in using the
         *                 Submit tool outputs to run endpoint.
         * @param type     The type of tool call the output is required for. For now, this is always function.
         * @param function The function definition.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ToolCall(
                @JsonProperty("id") String id,
                @JsonProperty("type") String type,
                @JsonProperty("function") ChatCompletionFunction function) {
        }

        /**
         * The function definition.
         *
         * @param name      The name of the function.
         * @param arguments The arguments that the model expects you to pass to the function.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ChatCompletionFunction(
                @JsonProperty("name") String name,
                @JsonProperty("arguments") String arguments) {
        }
    }
    /**
     * The reason the model stopped generating tokens.
     * 模型推理终止的原因
     */
    public enum ChatCompletionFinishReason {

        /**
         * 推理自然结束或触发停止词
         */
        @JsonProperty("stop") STOP,
        /**
         * 到达 tokens 长度上限
         */
        @JsonProperty("length") LENGTH,
        /**
         * 模型推理内容被安全审核接口拦截。请注意，针对此类内容，请用户自行判断并决定是否撤回已公开的内容
         */
        @JsonProperty("sensitive") SENSITIVE,
        /**
         * 模型命中函数
         */
        @JsonProperty("tool_calls") TOOL_CALLS,
        /**
         * 模型推理异常
         */
        @JsonProperty("network_error") NETWORK_ERROR

    }


    /**
     * 模型同步调用响应内容
     * Represents a chat completion response returned by model, based on the provided
     * input.
     *
     * @param id A unique identifier for the chat completion.
     * @param created The Unix timestamp (in seconds) of when the chat completion was
     * created.
     * @param model The model used for the chat completion.
     * @param choices A list of chat completion choices.
     * @param usage Usage statistics for the completion request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletion(
            @JsonProperty("id") String id,
            @JsonProperty("object") String object,
            @JsonProperty("created") Long created,
            @JsonProperty("model") String model,
            @JsonProperty("choices") List<Choice> choices,
            @JsonProperty("request_id") String requestId,
            @JsonProperty("usage") Usage usage) {
        // @formatter:on

        /**
         * Chat completion choice.
         *
         * @param index The index of the choice in the list of choices.
         * @param message A chat completion message generated by the model.
         * @param finishReason The reason the model stopped generating tokens.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Choice(
                // @formatter:off
                @JsonProperty("index") Integer index,
                @JsonProperty("message") ChatCompletionMessage message,
                @JsonProperty("finish_reason") ChatCompletionFinishReason finishReason) {
            // @formatter:on
        }
    }

    /**
     * Represents a streamed chunk of a chat completion response returned by model, based
     * on the provided input.
     *
     * @param id A unique identifier for the chat completion. Each chunk has the same ID.
     * @param object The object type, which is always 'chat.completion.chunk'.
     * @param created The Unix timestamp (in seconds) of when the chat completion was
     * created. Each chunk has the same timestamp.
     * @param model The model used for the chat completion.
     * @param choices A list of chat completion choices. Can be more than one if n is
     * greater than 1.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionChunk(
            // @formatter:off
            @JsonProperty("id") String id,
            @JsonProperty("object") String object,
            @JsonProperty("created") Long created,
            @JsonProperty("model") String model,
            @JsonProperty("request_id") String requestId,
            @JsonProperty("choices") List<ChunkChoice> choices) {
        // @formatter:on

        /**
         * Chat completion choice.
         *
         * @param index The index of the choice in the list of choices.
         * @param delta A chat completion delta generated by streamed model responses.
         * @param finishReason The reason the model stopped generating tokens.
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ChunkChoice(
                // @formatter:off
                @JsonProperty("index") Integer index,
                @JsonProperty("delta") ChatCompletionMessage delta,
                @JsonProperty("finish_reason") ChatCompletionFinishReason finishReason) {
            // @formatter:on
        }
    }


    /**
     * List of well-known 智普AI chat models.
     * https://open.bigmodel.cn/dev/api#language
     *
     * <p>
     * 智普AI AI provides three API endpoints featuring five leading Large Language
     * Models:
     * </p>
     * <ul>
     * <li><b>GLM_3_TURBO</b> - chatglm3</li>
     * <li><b>GLM_4</b> - chatglm4</li>
     * <li><b>GLM_4V</b> - chatglm4v</li>
     * </ul>
     */
    public enum ChatModel {

        GLM_3_TURBO("glm-3-turbo"),
        GLM_4("glm-4"),
        GLM_4V("glm-4v");

        private final String value;

        ChatModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    /**
     * List of well-known 智普AI embedding models.
     * https://open.bigmodel.cn/dev/api#text_embedding
     */
    public enum EmbeddingModel {

        @JsonProperty("embedding-2") EMBED("embedding-2");

        private final String value;

        EmbeddingModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    // --------------------------------------------------------------------------
    // Embeddings
    // --------------------------------------------------------------------------

    /**
     * Usage statistics.
     *
     * @param promptTokens     Number of tokens in the prompt.
     * @param totalTokens      Total number of tokens used in the request (prompt +
     *                         completion).
     * @param completionTokens Number of tokens in the generated completion. Only
     *                         applicable for completion requests.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Usage(
            // @formatter:off
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("total_tokens") Integer totalTokens,
            @JsonProperty("completion_tokens") Integer completionTokens) {
        // @formatter:on
    }

    /**
     * Represents an embedding vector returned by embedding endpoint.
     *
     * @param index The index of the embedding in the list of embeddings.
     * @param embedding The embedding vector, which is a list of floats. The length of
     * vector depends on the model.
     * @param object The object type, which is always 'embedding'.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Embedding(
            @JsonProperty("index") Integer index,
            @JsonProperty("embedding") List<Double> embedding,
            @JsonProperty("object") String object) {

        /**
         * Create an embedding with the given index, embedding and object type set to
         * 'embedding'.
         * @param index The index of the embedding in the list of embeddings.
         * @param embedding The embedding vector, which is a list of floats. The length of
         * vector depends on the model.
         */
        public Embedding(Integer index, List<Double> embedding) {
            this(index, embedding, "embedding");
        }
    }

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param input Input text to embed, encoded as a string or array of tokens
     * @param model ID of the model to use.
     * or base64.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequest(
            @JsonProperty("input") String input,
            @JsonProperty("model") String model) {

    }

    /**
     * List of multiple embedding responses.
     *
     * @param <T> Type of the entities in the data list.
     * @param object Must have value "list".
     * @param data List of entities.
     * @param model ID of the model to use.
     * @param usage Usage statistics for the completion request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingList<T>(
            // @formatter:off
            @JsonProperty("object") String object,
            @JsonProperty("data") List<T> data,
            @JsonProperty("model") String model,
            @JsonProperty("usage") Usage usage) {
        // @formatter:on
    }


    /**
     * The response object returned from the /embedding endpoint.
     * @param model 模型名称
     * @param data 模型生成的数组结果
     * @param usage 本次模型调用的 tokens 数量统计
     * @param object 结果类型，目前为 "list"
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponse(
        @JsonProperty("model") String model,
        @JsonProperty("data") List<EmbeddingResponseData> data,
        @JsonProperty("usage") EmbeddingResponseUsage usage,
        @JsonProperty("object") String object) {
    }

    /**
     * The response object returned from the /embeddings endpoint.
     * @param index 结果下标
     * @param object 结果类型，目前为"embedding"
     * @param embedding embedding 的处理结果，返回向量化表征的数组，数组长度为1024
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponseData(
        @JsonProperty("index") Integer index,
        @JsonProperty("object") String object,
        @JsonProperty("embedding") List<Double> embedding) {
    }

    /**
     * 本次模型调用的 tokens 数量统计
     * @param promptTokens 用户输入的 tokens 数量
     * @param completionTokens 模型输出的 tokens 数量
     * @param totalTokens 总 tokens 数量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingResponseUsage(
        @JsonProperty("prompt_tokens") Integer promptTokens,
        @JsonProperty("completion_tokens") Integer completionTokens,
        @JsonProperty("total_tokens") Integer totalTokens) {
    }

}
