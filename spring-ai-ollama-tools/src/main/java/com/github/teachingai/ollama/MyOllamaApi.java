package com.github.teachingai.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class MyOllamaApi {


    private static final Log logger = LogFactory.getLog(MyOllamaApi.class);

    private final static String DEFAULT_BASE_URL = "http://localhost:11434";

    public static final String REQUEST_BODY_NULL_ERROR = "The request body can not be null.";

    private final ResponseErrorHandler responseErrorHandler;

    private final RestClient restClient;

    private final WebClient webClient;

    private static class OllamaResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }

        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().isError()) {
                int statusCode = response.getStatusCode().value();
                String statusText = response.getStatusText();
                String message = StreamUtils.copyToString(response.getBody(), java.nio.charset.StandardCharsets.UTF_8);
                logger.warn(String.format("[%s] %s - %s", statusCode, statusText, message));
                throw new RuntimeException(String.format("[%s] %s - %s", statusCode, statusText, message));
            }
        }

    }

    /**
     * Default constructor that uses the default localhost url.
     */
    public MyOllamaApi() {
        this(DEFAULT_BASE_URL);
    }

    /**
     * Crate a new OllamaApi instance with the given base url.
     * @param baseUrl The base url of the Ollama server.
     */
    public MyOllamaApi(String baseUrl) {
        this(baseUrl, RestClient.builder());
    }

    /**
     * Crate a new OllamaApi instance with the given base url and
     * {@link RestClient.Builder}.
     * @param baseUrl The base url of the Ollama server.
     * @param restClientBuilder The {@link RestClient.Builder} to use.
     */
    public MyOllamaApi(String baseUrl, RestClient.Builder restClientBuilder) {

        this.responseErrorHandler = new MyOllamaApi.OllamaResponseErrorHandler();

        Consumer<HttpHeaders> defaultHeaders = headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        };

        this.restClient = restClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();

        this.webClient = WebClient.builder().baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();
    }

    // --------------------------------------------------------------------------
    // Generate & Streaming Generate
    // --------------------------------------------------------------------------
    /**
     * The request object sent to the /generate endpoint.
     *
     * @param model (required) The model to use for completion.
     * @param prompt (required) The prompt(s) to generate completions for.
     * @param format (optional) The format to return the response in. Currently the only
     * accepted value is "json".
     * @param options (optional) additional model parameters listed in the documentation
     * for the Modelfile such as temperature.
     * @param system (optional) system prompt to (overrides what is defined in the Modelfile).
     * @param template (optional) the full prompt or prompt template (overrides what is
     *  defined in the Modelfile).
     * @param context the context parameter returned from a previous request to /generate,
     * this can be used to keep a short conversational memory.
     * @param stream (optional) if false the response will be returned as a single
     * response object, rather than a stream of objects.
     * @param raw (optional) if true no formatting will be applied to the prompt and no
     * context will be returned. You may choose to use the raw parameter if you are
     * specifying a full templated prompt in your request to the API, and are managing
     * history yourself.
     */
    @JsonInclude(Include.NON_NULL)
    public record GenerateRequest(
            @JsonProperty("model") String model,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("format") String format,
            @JsonProperty("options") Map<String, Object> options,
            @JsonProperty("system") String system,
            @JsonProperty("template") String template,
            @JsonProperty("context") List<Integer> context,
            @JsonProperty("stream") Boolean stream,
            @JsonProperty("raw") Boolean raw,
            @JsonProperty("images") List<String> images,
            @JsonProperty("keep_alive") String keepAlive) {

        /**
         * Short cut constructor to create a CompletionRequest without options.
         * @param model The model used for completion.
         * @param prompt The prompt(s) to generate completions for.
         * @param stream Whether to stream the response.
         */
        public GenerateRequest(String model, String prompt, Boolean stream) {
            this(model, prompt, null, null, null, null, null, stream, null, null, null);
        }

        /**
         * Short cut constructor to create a CompletionRequest without options.
         * @param model The model used for completion.
         * @param prompt The prompt(s) to generate completions for.
         * @param enableJsonFormat Whether to return the response in json format.
         * @param stream Whether to stream the response.
         */
        public GenerateRequest(String model, String prompt, boolean enableJsonFormat, Boolean stream) {
            this(model, prompt, (enableJsonFormat) ? "json" : null, null, null, null, null, stream, null, null, null);
        }

        /**
         * Create a CompletionRequest builder.
         * @param prompt The prompt(s) to generate completions for.
         */
        public static MyOllamaApi.GenerateRequest.Builder builder(String prompt) {
            return new MyOllamaApi.GenerateRequest.Builder(prompt);
        }

        public static class Builder {

            private String model;
            private final String prompt;
            private String format;
            private Map<String, Object> options;
            private String system;
            private String template;
            private List<Integer> context;
            private Boolean stream;
            private Boolean raw;
            private List<String> images;
            private String keepAlive;

            public Builder(String prompt) {
                this.prompt = prompt;
            }

            public MyOllamaApi.GenerateRequest.Builder withModel(String model) {
                this.model = model;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withFormat(String format) {
                this.format = format;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withOptions(Map<String, Object> options) {
                this.options = options;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withOptions(OllamaChatOptions options) {
                this.options = options.toMap();
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withSystem(String system) {
                this.system = system;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withTemplate(String template) {
                this.template = template;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withContext(List<Integer> context) {
                this.context = context;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withStream(Boolean stream) {
                this.stream = stream;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withRaw(Boolean raw) {
                this.raw = raw;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withImages(List<String> images) {
                this.images = images;
                return this;
            }

            public MyOllamaApi.GenerateRequest.Builder withKeepAlive(String keepAlive) {
                this.keepAlive = keepAlive;
                return this;
            }

            public MyOllamaApi.GenerateRequest build() {
                return new MyOllamaApi.GenerateRequest(model, prompt, format, options, system, template, context, stream, raw, images, keepAlive);
            }

        }
    }

    /**
     * The response object returned from the /generate endpoint. To calculate how fast the
     * response is generated in tokens per second (token/s), divide eval_count /
     * eval_duration.
     *
     * @param model The model used for completion.
     * @param createdAt When the request was made.
     * @param response The completion response. Empty if the response was streamed, if not
     * streamed, this will contain the full response
     * @param done Whether this is the final response. If true, this response may be
     * followed by another response with the following, additional fields: context,
     * prompt_eval_count, prompt_eval_duration, eval_count, eval_duration.
     * @param context Encoding of the conversation used in this response, this can be sent
     * in the next request to keep a conversational memory.
     * @param totalDuration Time spent generating the response.
     * @param loadDuration Time spent loading the model.
     * @param promptEvalCount Number of times the prompt was evaluated.
     * @param promptEvalDuration Time spent evaluating the prompt.
     * @param evalCount Number of tokens in the response.
     * @param evalDuration Time spent generating the response.
     */
    @JsonInclude(Include.NON_NULL)
    public record GenerateResponse(
            @JsonProperty("model") String model,
            @JsonProperty("created_at") Instant createdAt,
            @JsonProperty("response") String response,
            @JsonProperty("done") Boolean done,
            @JsonProperty("context") List<Integer> context,
            @JsonProperty("total_duration") Duration totalDuration,
            @JsonProperty("load_duration") Duration loadDuration,
            @JsonProperty("prompt_eval_count") Integer promptEvalCount,
            @JsonProperty("prompt_eval_duration") Duration promptEvalDuration,
            @JsonProperty("eval_count") Integer evalCount,
            @JsonProperty("eval_duration") Duration evalDuration) {
    }

    /**
     * Generate a completion for the given prompt.
     * @param completionRequest Completion request.
     * @return Completion response.
     */
    public MyOllamaApi.GenerateResponse generate(MyOllamaApi.GenerateRequest completionRequest) {
        Assert.notNull(completionRequest, REQUEST_BODY_NULL_ERROR);
        Assert.isTrue(completionRequest.stream() == false, "Stream mode must be disabled.");

        return this.restClient.post()
                .uri("/api/generate")
                .body(completionRequest)
                .retrieve()
                .onStatus(this.responseErrorHandler)
                .body(MyOllamaApi.GenerateResponse.class);
    }

    /**
     * Generate a streaming completion for the given prompt.
     * @param completionRequest Completion request. The request must set the stream
     * property to true.
     * @return Completion response as a {@link Flux} stream.
     */
    public Flux<MyOllamaApi.GenerateResponse> generateStreaming(MyOllamaApi.GenerateRequest completionRequest) {
        Assert.notNull(completionRequest, REQUEST_BODY_NULL_ERROR);
        Assert.isTrue(completionRequest.stream(), "Request must set the steam property to true.");

        return webClient.post()
                .uri("/api/generate")
                .body(Mono.just(completionRequest), MyOllamaApi.GenerateRequest.class)
                .retrieve()
                .bodyToFlux(MyOllamaApi.GenerateResponse.class)
                .handle((data, sink) -> {
                    if (logger.isTraceEnabled()) {
                        logger.trace(data);
                    }
                    sink.next(data);
                });
    }

    // --------------------------------------------------------------------------
    // Chat & Streaming Chat
    // --------------------------------------------------------------------------
    /**
     * Chat message object.
     *
     * @param role The role of the message of type {@link MyOllamaApi.Message.Role}.
     * @param content The content of the message.
     * @param images The list of base64-encoded images to send with the message.
     * 				 Requires multimodal models such as llava or bakllava.
     */
    @JsonInclude(Include.NON_NULL)
    public record Message(
            @JsonProperty("role") MyOllamaApi.Message.Role role,
            @JsonProperty("content") String content,
            @JsonProperty("images") List<String> images) {

        /**
         * The role of the message in the conversation.
         */
        public enum Role {

            /**
             * System message type used as instructions to the model.
             */
            @JsonProperty("system") SYSTEM,
            /**
             * User message type.
             */
            @JsonProperty("user") USER,
            /**
             * Assistant message type. Usually the response from the model.
             */
            @JsonProperty("assistant") ASSISTANT;

        }

        public static MyOllamaApi.Message.Builder builder(MyOllamaApi.Message.Role role) {
            return new MyOllamaApi.Message.Builder(role);
        }

        public static class Builder {

            private final MyOllamaApi.Message.Role role;
            private String content;
            private List<String> images;

            public Builder(MyOllamaApi.Message.Role role) {
                this.role = role;
            }

            public MyOllamaApi.Message.Builder withContent(String content) {
                this.content = content;
                return this;
            }

            public MyOllamaApi.Message.Builder withImages(List<String> images) {
                this.images = images;
                return this;
            }

            public MyOllamaApi.Message build() {
                return new MyOllamaApi.Message(role, content, images);
            }

        }
    }

    /**
     * Chat request object.
     *
     * @param model The model to use for completion.
     * @param messages The list of messages to chat with.
     * @param stream Whether to stream the response.
     * @param format The format to return the response in. Currently, the only accepted
     * value is "json".
     * @param keepAlive The duration to keep the model loaded in ollama while idle. https://pkg.go.dev/time#ParseDuration
     * @param options Additional model parameters. You can use the {@link OllamaChatOptions} builder
     * to create the options then {@link OllamaChatOptions#toMap()} to convert the options into a
     * map.
     */
    @JsonInclude(Include.NON_NULL)
    public record ChatRequest(
            @JsonProperty("model") String model,
            @JsonProperty("messages") List<MyOllamaApi.Message> messages,
            @JsonProperty("stream") Boolean stream,
            @JsonProperty("format") String format,
            @JsonProperty("keep_alive") String keepAlive,
            @JsonProperty("options") Map<String, Object> options) {

        public static MyOllamaApi.ChatRequest.Builder builder(String model) {
            return new MyOllamaApi.ChatRequest.Builder(model);
        }

        public static class Builder {

            private final String model;
            private List<MyOllamaApi.Message> messages = List.of();
            private boolean stream = false;
            private String format;
            private String keepAlive;
            private Map<String, Object> options = Map.of();

            public Builder(String model) {
                Assert.notNull(model, "The model can not be null.");
                this.model = model;
            }

            public MyOllamaApi.ChatRequest.Builder withMessages(List<MyOllamaApi.Message> messages) {
                this.messages = messages;
                return this;
            }

            public MyOllamaApi.ChatRequest.Builder withStream(boolean stream) {
                this.stream = stream;
                return this;
            }

            public MyOllamaApi.ChatRequest.Builder withFormat(String format) {
                this.format = format;
                return this;
            }

            public MyOllamaApi.ChatRequest.Builder withKeepAlive(String keepAlive) {
                this.keepAlive = keepAlive;
                return this;
            }

            public MyOllamaApi.ChatRequest.Builder withOptions(Map<String, Object> options) {
                Objects.requireNonNull(options, "The options can not be null.");

                this.options = OllamaChatOptions.filterNonSupportedFields(options);
                return this;
            }

            public MyOllamaApi.ChatRequest.Builder withOptions(OllamaChatOptions options) {
                Objects.requireNonNull(options, "The options can not be null.");
                this.options = OllamaChatOptions.filterNonSupportedFields(options.toMap());
                return this;
            }

            public MyOllamaApi.ChatRequest build() {
                return new MyOllamaApi.ChatRequest(model, messages, stream, format, keepAlive, options);
            }
        }
    }

    /**
     * Ollama chat response object.
     *
     * @param model The model name used for completion.
     * @param createdAt When the request was made.
     * @param message The response {@link MyOllamaApi.Message} with {@link MyOllamaApi.Message.Role#ASSISTANT}.
     * @param done Whether this is the final response. For streaming response only the
     * last message is marked as done. If true, this response may be followed by another
     * response with the following, additional fields: context, prompt_eval_count,
     * prompt_eval_duration, eval_count, eval_duration.
     * @param totalDuration Time spent generating the response.
     * @param loadDuration Time spent loading the model.
     * @param promptEvalCount number of tokens in the prompt.(*)
     * @param promptEvalDuration time spent evaluating the prompt.
     * @param evalCount number of tokens in the response.
     * @param evalDuration time spent generating the response.
     * @see <a href=
     * "https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-chat-completion">Chat
     * Completion API</a>
     * @see <a href="https://github.com/jmorganca/ollama/blob/main/api/types.go">Ollama
     * Types</a>
     */
    @JsonInclude(Include.NON_NULL)
    public record ChatResponse(
            @JsonProperty("model") String model,
            @JsonProperty("created_at") Instant createdAt,
            @JsonProperty("message") MyOllamaApi.Message message,
            @JsonProperty("done") Boolean done,
            @JsonProperty("total_duration") Duration totalDuration,
            @JsonProperty("load_duration") Duration loadDuration,
            @JsonProperty("prompt_eval_count") Integer promptEvalCount,
            @JsonProperty("prompt_eval_duration") Duration promptEvalDuration,
            @JsonProperty("eval_count") Integer evalCount,
            @JsonProperty("eval_duration") Duration evalDuration) {
    }

    /**
     * Generate the next message in a chat with a provided model.
     *
     * This is a streaming endpoint (controlled by the 'stream' request property), so
     * there will be a series of responses. The final response object will include
     * statistics and additional data from the request.
     * @param chatRequest Chat request.
     * @return Chat response.
     */
    public MyOllamaApi.ChatResponse chat(MyOllamaApi.ChatRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);
        Assert.isTrue(!chatRequest.stream(), "Stream mode must be disabled.");

        return this.restClient.post()
                .uri("/api/chat")
                .body(chatRequest)
                .retrieve()
                .onStatus(this.responseErrorHandler)
                .body(MyOllamaApi.ChatResponse.class);
    }

    /**
     * Streaming response for the chat completion request.
     * @param chatRequest Chat request. The request must set the stream property to true.
     * @return Chat response as a {@link Flux} stream.
     */
    public Flux<MyOllamaApi.ChatResponse> streamingChat(MyOllamaApi.ChatRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);
        Assert.isTrue(chatRequest.stream(), "Request must set the steam property to true.");

        return webClient.post()
                .uri("/api/chat")
                .body(Mono.just(chatRequest), MyOllamaApi.GenerateRequest.class)
                .retrieve()
                .bodyToFlux(MyOllamaApi.ChatResponse.class)
                .handle((data, sink) -> {
                    if (logger.isTraceEnabled()) {
                        logger.trace(data);
                    }
                    sink.next(data);
                });
    }

    // --------------------------------------------------------------------------
    // Embeddings
    // --------------------------------------------------------------------------
    /**
     * Generate embeddings from a model.
     *
     * @param model The name of model to generate embeddings from.
     * @param prompt The text to generate embeddings for.
     * @param options Additional model parameters listed in the documentation for the
     * Modelfile such as temperature.
     */
    @JsonInclude(Include.NON_NULL)
    public record EmbeddingRequest(
            @JsonProperty("model") String model,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("keep_alive") Duration keepAlive,
            @JsonProperty("options") Map<String, Object> options) {

        /**
         * short cut constructor to create a EmbeddingRequest without options.
         * @param model The name of model to generate embeddings from.
         * @param prompt The text to generate embeddings for.
         */
        public EmbeddingRequest(String model, String prompt) {
            this(model, prompt, null, null);
        }
    }

    /**
     * The response object returned from the /embedding endpoint.
     *
     * @param embedding The embedding generated from the model.
     */
    @JsonInclude(Include.NON_NULL)
    public record EmbeddingResponse(
            @JsonProperty("embedding") List<Double> embedding) {
    }

    /**
     * Generate embeddings from a model.
     * @param embeddingRequest Embedding request.
     * @return Embedding response.
     */
    public MyOllamaApi.EmbeddingResponse embeddings(MyOllamaApi.EmbeddingRequest embeddingRequest) {
        Assert.notNull(embeddingRequest, REQUEST_BODY_NULL_ERROR);

        return this.restClient.post()
                .uri("/api/embeddings")
                .body(embeddingRequest)
                .retrieve()
                .onStatus(this.responseErrorHandler)
                .body(MyOllamaApi.EmbeddingResponse.class);
    }

}
