package com.github.partmeai.ollama.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.partmeai.ollama.autoconfigure.UnifiedTtsConnectionProperties;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class UnifiedTtsAudioApi {

    public static final String DEFAULT_VOICE = "zh-CN-XiaoxiaoNeural";
    public static final Float DEFAULT_SPEED = 1.1F;
    public static final Float DEFAULT_PITCH = 1.0F;
    public static final Float DEFAULT_VOLUME = 0.9F;
    public static final String DEFAULT_FORMAT = "mp3";
    public static final String API_KEY = "X-API-Key";

    private final RestClient restClient;
    private final WebClient webClient;

    /**
     * Create a new audio api.
     * @param baseUrl api base URL.
     * @param apiKey UnifiedTts apiKey.
     * @param headers the http headers to use.
     * @param restClientBuilder RestClient builder.
     * @param webClientBuilder WebClient builder.
     * @param responseErrorHandler Response error handler.
     */
    public UnifiedTtsAudioApi(String baseUrl, ApiKey apiKey, MultiValueMap<String, String> headers,
                              RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder,
                              ResponseErrorHandler responseErrorHandler) {

        Consumer<HttpHeaders> authHeaders = h -> {
            h.addAll(headers);
        };

        // @formatter:off
        this.restClient = restClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .defaultStatusHandler(responseErrorHandler)
                .defaultRequest(requestHeadersSpec -> {
                    if (!(apiKey instanceof NoopApiKey)) {
                        requestHeadersSpec.header(API_KEY, apiKey.getValue());
                    }
                })
                .build();

        this.webClient = webClientBuilder.clone()
                .baseUrl(baseUrl)
                .defaultHeaders(authHeaders)
                .defaultRequest(requestHeadersSpec -> {
                    if (!(apiKey instanceof NoopApiKey)) {
                        requestHeadersSpec.header(API_KEY, apiKey.getValue());
                    }
                })
                .build(); // @formatter:on
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * TTS is an AI model that converts text to natural sounding spoken text.
     * <a href="https://unifiedtts.com/zh/api-docs/models">TTS</a>
     */
    public enum TtsModel {

        // @formatter:off
        /**
         * Edge TTS model
         */
        @JsonProperty("edge-tts")
        EDGE_TTS("edge-tts"),
        /**
         * Azure TTS model
         */
        @JsonProperty("azure-tts")
        AZURE_TTS("azure-tts"),
        /**
         * CosyVoice v1 model
         */
        @JsonProperty("cosyvoice-v1")
        COSYVOICE_V1("cosyvoice-v1"),
        /**
         * CosyVoice v2 model
         */
        @JsonProperty("cosyvoice-v2")
        COSYVOICE_V2("cosyvoice-v2"),
        /**
         * Sambert v1 model
         */
        @JsonProperty("sambert-v1")
        SAMBERT_V1("sambert-v1"),
        /**
         * Speech 2.5 HD Preview model
         */
        @JsonProperty("speech-2.5-hd-preview")
        SPEECH_2_5_HD_PREVIEW("speech-2.5-hd-preview"),
        /**
         * Speech 2.5 Turbo Preview model
         */
        @JsonProperty("speech-2.5-turbo-preview")
        SPEECH_2_5_TURBO_PREVIEW("speech-2.5-turbo-preview"),
        /**
         * Speech 02 HD model
         */
        @JsonProperty("speech-02-hd")
        SPEECH_02_HD("speech-02-hd"),
        /**
         * Speech 02 Turbo model
         */
        @JsonProperty("speech-02-turbo")
        SPEECH_02_TURBO("speech-02-turbo"),
        /**
         * Speech 01 HD model
         */
        @JsonProperty("speech-01-hd")
        SPEECH_01_HD("speech-01-hd"),
        /**
         * Speech 01 Turbo model
         */
        @JsonProperty("speech-01-turbo")
        SPEECH_01_TURBO("speech-01-turbo"),
        /**
         * Eleven Flash v2 model
         */
        @JsonProperty("eleven_flash_v2")
        ELEVEN_FLASH_V2("eleven_flash_v2"),
        /**
         * Eleven Flash v2.5 model
         */
        @JsonProperty("eleven_flash_v2_5")
        ELEVEN_FLASH_V2_5("eleven_flash_v2_5"),
        /**
         * Eleven Turbo v2 model
         */
        @JsonProperty("eleven_turbo_v2")
        ELEVEN_TURBO_V2("eleven_turbo_v2"),
        /**
         * Eleven Turbo v2.5 model
         */
        @JsonProperty("eleven_turbo_v2_5")
        ELEVEN_TURBO_V2_5("eleven_turbo_v2_5"),
        /**
         * Eleven Multilingual v2 model
         */
        @JsonProperty("eleven_multilingual_v2")
        ELEVEN_MULTILINGUAL_V2("eleven_multilingual_v2");

        // @formatter:on

        public final String value;

        TtsModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechModelResponse(
            @JsonProperty("success") Boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("data") List<String> data) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechModelVoiceResponse(
            @JsonProperty("success") Boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("errorCode") String errorCode,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("data") List<ModelVoice> data) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record ModelVoice(
                @JsonProperty("voiceId") String voiceId,
                @JsonProperty("voiceName") String voiceName,
                @JsonProperty("gender") String gender,
                @JsonProperty("description") String description) {
        }

    }

    /**
     * Request to generates audio from the input text.
     * @param text The input text to convert to speech.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechRequest(
            @JsonProperty("model") TtsModel model, // TTS模型名称，可通过模型列表接口获取
            @JsonProperty("text") String text, // 要转换为语音的文本内容（最大支持10,000字符）
            @JsonProperty("voice") String voice, // 音色ID，每个模型下的音色唯一标识
            @JsonProperty("format") AudioResponseFormat format, // 输出音频格式，支持mp3、wav等，默认mp3
            @JsonProperty("speed") float speed, // 语速倍率，范围0.5-2.0，默认1.0
            @JsonProperty("pitch") float pitch, // 音调倍率，范围0.5-2.0，默认1.0
            @JsonProperty("volume") float volume // 音量倍率，范围0.5-2.0，默认1.0
    ) {

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder for the SpeechRequest.
         */
        public static class Builder {

            private TtsModel model = TtsModel.EDGE_TTS;

            private String input;

            private String voice;

            private AudioResponseFormat responseFormat = AudioResponseFormat.MP3;

            private Float speed;

            private Float pitch;

            private Float volume;

            public Builder model(TtsModel model) {
                this.model = model;
                return this;
            }

            public Builder input(String input) {
                this.input = input;
                return this;
            }

            public Builder voice(String voice) {
                this.voice = voice;
                return this;
            }

            public Builder responseFormat(AudioResponseFormat responseFormat) {
                this.responseFormat = responseFormat;
                return this;
            }

            public Builder speed(Float speed) {
                this.speed = speed;
                return this;
            }

            public Builder pitch(Float pitch) {
                this.pitch = pitch;
                return this;
            }

            public Builder volume(Float volume) {
                this.volume = volume;
                return this;
            }

            public SpeechRequest build() {
                Assert.notNull(this.model, "model must not be empty");
                Assert.hasText(this.input, "input must not be empty");

                return new SpeechRequest(this.model, this.input, this.voice, this.responseFormat, this.speed, this.pitch, this.volume);
            }

        }

        /**
         * The format to audio in. Supported formats are mp3, opus, aac, wav, pcm and
         * flac. Defaults to mp3.
         */
        public enum AudioResponseFormat {

            // @formatter:off
            @JsonProperty("mp3")
            MP3("mp3"),
            @JsonProperty("wav")
            WAV("wav");
            // @formatter:on

            public final String value;

            AudioResponseFormat(String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

        }
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechResponse (
            @JsonProperty("success") Boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("data") AudioFile data) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record AudioFile(
                @JsonProperty("request_id") String requestId,
                @JsonProperty("audio_url") String audioUrl,
                @JsonProperty("file_size") Long fileSize) {
        }

        public SpeechResponse(Boolean success, String message, Long timestamp, AudioFile data) {
            this.success = success;
            this.message = message;
            this.timestamp = timestamp;
            this.data = data;
        }

        public AudioFile getData() {
            return data;
        }
        
    }

    /**
     * Request to get the list of available models.
     * @return Response entity containing the list of available models.
     */
    public ResponseEntity<SpeechModelResponse> fetchModels() {
        return this.restClient.get()
                .uri("/tools/models")
                .retrieve()
                .toEntity(SpeechModelResponse.class);
    }

    /**
     * Request to get the list of available models.
     * @return Response entity containing the list of available models.
     */
    public ResponseEntity<SpeechModelVoiceResponse> fetchModelVoices(String  model) {
        return this.restClient.get()
                .uri(String.format("/tools/voices/%s", model))
                .retrieve()
                .toEntity(SpeechModelVoiceResponse.class);
    }

    /**
     * 根据返回的 {@code audio_url} 下载音频字节。
     * <p>作为可覆写的保护方法，方便单元测试替换真实下载行为。
     */
    public byte[] fetchAudio(String audioUrl) {
        ResponseEntity<byte[]> response = this.restClient
                .get()
                .uri(audioUrl)
                .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.valueOf("audio/mpeg"), MediaType.valueOf("audio/mp3"))
                .retrieve()
                .toEntity(byte[].class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new IllegalStateException("Download audio failed: " + response.getStatusCode());
    }

    /**
     * 调用合成并将音频写入指定文件。
     *
     * <p>若输出路径的父目录不存在，会自动创建；失败时抛出运行时异常。
     *
     * @param audioFile TTS 请求参数
     * @param outputPath 目标文件路径（例如 output.mp3）
     * @return 实际写入的文件路径
     */
    public Path saveToFile(UnifiedTtsAudioApi.SpeechResponse.AudioFile audioFile, Path outputPath) {
        if (audioFile == null || audioFile.audioUrl() == null) {
            throw new IllegalStateException("UnifiedTTS response missing audio_url");
        }
        byte[] data = this.fetchAudio(audioFile.audioUrl());
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.write(outputPath, data);
            return outputPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write TTS output to file: " + outputPath, e);
        }
    }

    /**
     * 调用 UnifiedTTS 同步 TTS 接口，返回 JSON 响应。
     * Request to generates audio from the input text.
     * @param request The request body.
     * @return Response entity containing the audio binary.
     * @throws IllegalStateException 当服务端返回非 2xx、无内容、或 {@code success=false} 时抛出
     */
    public ResponseEntity<UnifiedTtsAudioApi.SpeechResponse> createSpeech(SpeechRequest request) {
        ResponseEntity<UnifiedTtsAudioApi.SpeechResponse> response = this.restClient
                .post()
                .uri("/v1/common/tts-sync")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UnifiedTtsAudioApi.SpeechResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response;
        }
        throw new IllegalStateException("UnifiedTTS synthesize failed: " + response.getStatusCode());
    }

    /**
     * Builder to construct {@link UnifiedTtsAudioApi} instance.
     */
    public static class Builder {

        private String baseUrl = UnifiedTtsConnectionProperties.DEFAULT_BASE_URL;

        private ApiKey apiKey;

        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private WebClient.Builder webClientBuilder = WebClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        public Builder baseUrl(String baseUrl) {
            Assert.hasText(baseUrl, "baseUrl cannot be null or empty");
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(ApiKey apiKey) {
            Assert.notNull(apiKey, "apiKey cannot be null");
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiKey(String simpleApiKey) {
            Assert.notNull(simpleApiKey, "simpleApiKey cannot be null");
            this.apiKey = new SimpleApiKey(simpleApiKey);
            return this;
        }

        public Builder headers(MultiValueMap<String, String> headers) {
            Assert.notNull(headers, "headers cannot be null");
            this.headers = headers;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            Assert.notNull(restClientBuilder, "restClientBuilder cannot be null");
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder webClientBuilder(WebClient.Builder webClientBuilder) {
            Assert.notNull(webClientBuilder, "webClientBuilder cannot be null");
            this.webClientBuilder = webClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            Assert.notNull(responseErrorHandler, "responseErrorHandler cannot be null");
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public UnifiedTtsAudioApi build() {
            Assert.notNull(this.apiKey, "apiKey must be set");
            return new UnifiedTtsAudioApi(this.baseUrl, this.apiKey, this.headers, this.restClientBuilder,
                    this.webClientBuilder, this.responseErrorHandler);
        }

    }
}
