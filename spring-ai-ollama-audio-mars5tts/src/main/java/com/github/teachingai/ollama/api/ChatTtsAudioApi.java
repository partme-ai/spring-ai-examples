package com.github.partmeai.ollama.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ChatTtsAudioApi {

    private final RestClient restClient;

    private final WebClient webClient;

    /**
     * Create an new audio api.
     */
    public ChatTtsAudioApi() {
        this(ApiUtils.DEFAULT_BASE_URL, RestClient.builder(), WebClient.builder(),
                RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER);
    }

    /**
     * Create an new chat completion api.
     * @param baseUrl api base URL.
     * @param restClientBuilder RestClient builder.
     * @param responseErrorHandler Response error handler.
     */
    public ChatTtsAudioApi(String baseUrl,
                           RestClient.Builder restClientBuilder,
                           ResponseErrorHandler responseErrorHandler) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).messageConverters((x) -> {
            x.add(new FormHttpMessageConverter());
        }).defaultStatusHandler(responseErrorHandler).build();
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Create an new chat completion api.
     * @param baseUrl api base URL.
     * @param restClientBuilder RestClient builder.
     * @param webClientBuilder WebClient builder.
     * @param responseErrorHandler Response error handler.
     */
    public ChatTtsAudioApi(String baseUrl,
                           RestClient.Builder restClientBuilder,
                           WebClient.Builder webClientBuilder,
                           ResponseErrorHandler responseErrorHandler) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).messageConverters((x) -> {
            x.add(new FormHttpMessageConverter());
        }).defaultStatusHandler(responseErrorHandler).build();
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Request to generates audio from the input text.
     * @param text The input text to convert to speech.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechRequest(
            @JsonProperty("text") String text,
            @JsonProperty("prompt") String prompt,
            @JsonProperty("voice") String voice,
            @JsonProperty("speed") Integer speed,
            @JsonProperty("temperature") Float temperature,
            @JsonProperty("top_p") Float topP,
            @JsonProperty("top_k") Integer topK,
            @JsonProperty("refine_max_new_token") Integer maxRefineTokens,
            @JsonProperty("infer_max_new_token") Integer maxInferTokens,
            @JsonProperty("text_seed") Float textSeed,
            @JsonProperty("skip_refine") Integer skipRefine,
            @JsonProperty("is_stream") Integer stream,
            @JsonProperty("custom_voice") Integer customVoice) {

        public SpeechRequest(String text) {
            this(text, null, null, null, null, null, null, null, null, null, null, null, null);
        }

        /**
         * The voice to use for synthesis.
         */
        public enum Voice {

            VOICE_11("11.csv"),
            VOICE_1111("1111.csv"),
            VOICE_12("12.csv"),
            VOICE_1234("1234.csv"),
            VOICE_13("13.csv"),
            VOICE_14("14.csv"),
            VOICE_16("16.csv"),
            VOICE_2222("2222.csv"),
            VOICE_2279("2279.csv"),
            VOICE_3333("3333.csv"),
            VOICE_4099("4099.csv"),
            VOICE_5("5.csv"),
            VOICE_5099("5099.csv"),
            VOICE_5555("5555.csv"),
            VOICE_6653("6653.csv"),
            VOICE_6666("6666.csv"),
            VOICE_7777("7777.csv"),
            VOICE_7869("7869.csv"),
            VOICE_8888("8888.csv"),
            VOICE_9999("9999.csv"),
            VOICE_SEED_1518_RESTORED_EMB("seed_1518_restored_emb.pt"),
            VOICE_SEED_1983_RESTORED_EMB("seed_1983_restored_emb.pt"),
            ;

            public final String value;

            private Voice(String value) {
                this.value = value;
            }

            public String getValue() {
                return this.value;
            }

        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpeechResponse(
            @JsonProperty("code") Integer code,
            @JsonProperty("msg") String msg,
            @JsonProperty("filename") String filename,
            @JsonProperty("url") String url,
            @JsonProperty("audio_files") List<AudioFile> audioFiles) {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record AudioFile(
                @JsonProperty("audio_duration") Float audioDuration,
                @JsonProperty("filename") String filename,
                @JsonProperty("inference_time") Float inferenceTime,
                @JsonProperty("url") String url) {
        }

    }

    /**
     * Request to generates audio from the input text.
     * @param speechRequest The request body.
     * @return Response entity containing the audio SpeechResponse.
     */
    public ResponseEntity<SpeechResponse> createSpeech(SpeechRequest speechRequest) {

        Assert.notNull(speechRequest, "The request body can not be null.");
        Assert.isTrue(speechRequest.stream() == 0, "Request must set the steam property to 0.");
        MultiValueMap body = ApiUtils.toMultiValueMap(speechRequest);
        return this.restClient.post()
                .uri("/tts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(SpeechResponse.class);
    }

    /**
     * Streams audio generated from the input text.
     *
     * This method sends a POST request to the OpenAI API to generate audio from the
     * provided text. The audio is streamed back as a Flux of ResponseEntity objects, each
     * containing a byte array of the audio data.
     * @param requestBody The request body containing the details for the audio
     * generation, such as the input text, model, voice, and response format.
     * @return A Flux of ResponseEntity objects, each containing a byte array of the audio
     * data.
     */
    public Flux<ResponseEntity<SpeechResponse>> stream(SpeechRequest requestBody) {

        return webClient.post()
                .uri("/tts")
                .body(Mono.just(requestBody), SpeechRequest.class)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToFlux(clientResponse -> {
                    HttpHeaders headers = clientResponse.headers().asHttpHeaders();
                    return clientResponse.bodyToFlux(SpeechResponse.class)
                            .map(bytes -> ResponseEntity.ok().headers(headers).body(bytes));
                });
    }

}
