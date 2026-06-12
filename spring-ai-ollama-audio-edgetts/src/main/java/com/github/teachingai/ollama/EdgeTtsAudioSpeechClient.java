package com.github.partmeai.ollama;

import com.github.partmeai.ollama.api.ApiUtils;
import com.github.partmeai.ollama.api.EdgeTtsAudioApi;
import com.github.partmeai.ollama.api.EdgeTtsResponseHeaderExtractor;
import com.github.partmeai.ollama.api.common.OllamaApiException;
import com.github.partmeai.ollama.audio.speech.*;
import com.github.partmeai.ollama.metadata.audio.EdgeTtsAudioSpeechResponseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.model.ModelOptions;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.time.Duration;

public class EdgeTtsAudioSpeechClient implements SpeechClient, StreamingSpeechClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EdgeTtsAudioSpeechOptions defaultOptions;

    public final RetryTemplate retryTemplate = RetryTemplate.builder()
            .maxAttempts(10)
            .retryOn(OllamaApiException.class)
            .exponentialBackoff(Duration.ofMillis(2000), 5, Duration.ofMillis(3 * 60000))
            .build();

    private final EdgeTtsAudioApi audioApi;

    /**
     * Initializes a new instance of the EdgeTtsAudioSpeechClient class with the provided
     * EdgeTtsAudioApi. It uses the model chatTTS, response format mp3, voice alloy, and the
     * default speed of 1.0.
     *
     * @param audioApi The EdgeTtsAudioApi to use for speech synthesis.
     */
    public EdgeTtsAudioSpeechClient(EdgeTtsAudioApi audioApi) {
        this(audioApi, EdgeTtsAudioSpeechOptions.builder()
                        .withVoice(ApiUtils.DEFAULT_VOICE)
                        .build());
    }

    /**
     * Initializes a new instance of the EdgeTtsAudioSpeechClient class with the provided
     * EdgeTtsAudioApi and options.
     *
     * @param audioApi The EdgeTtsAudioApi to use for speech synthesis.
     * @param options  The EdgeTtsAudioSpeechOptions containing the speech synthesis
     *                 options.
     */
    public EdgeTtsAudioSpeechClient(EdgeTtsAudioApi audioApi, EdgeTtsAudioSpeechOptions options) {
        Assert.notNull(audioApi, "EdgeTtsAudioApi must not be null");
        Assert.notNull(options, "EdgeTtsAudioSpeechOptions must not be null");
        this.audioApi = audioApi;
        this.defaultOptions = options;
    }

    @Override
    public InputStream call(String text) {
        SpeechPrompt speechRequest = new SpeechPrompt(text);
        return call(speechRequest).getResult().getOutput();
    }

    @Override
    public SpeechResponse call(SpeechPrompt speechPrompt) {

        return this.retryTemplate.execute(ctx -> {

            EdgeTtsAudioApi.SpeechRequest speechRequest = this.createRequestBody(speechPrompt);

            ResponseEntity<EdgeTtsAudioApi.SpeechResponse> speechEntity = this.audioApi.createSpeech(speechRequest);
            if (speechEntity == null) {
                logger.warn("No speech response returned for speechRequest: {}", speechRequest);
                return new SpeechResponse(new Speech());
            }
            if (speechEntity.getStatusCode().isError()) {
                logger.error("Error response returned for speechRequest: {}, StatusCode: {}", speechRequest, speechEntity.getStatusCode());
                throw new OllamaApiException("Error response returned for speechRequest: " + speechRequest);
            }
            var speech = speechEntity.getBody();
            if (speech == null) {
                logger.warn("No speech response returned for speechRequest: {}", speechRequest);
                return new SpeechResponse(new Speech());
            }
            logger.info("Speech response: {}", speech);
            if(speech.code() == 1){
                logger.error("Error response returned, Code: {}, Msg: {}", speech.code(), speech.msg());
                throw new OllamaApiException("Error response returned, Code: " + speech.code() + ", Msg: " + speech.msg());
            }

            RateLimit rateLimits = EdgeTtsResponseHeaderExtractor.extractAiResponseHeaders(speechEntity);

            return new SpeechResponse(new Speech(speech.filename(), speech.url()), new EdgeTtsAudioSpeechResponseMetadata(rateLimits));

        });
    }

    /**
     * Streams the audio response for the given speech prompt.
     *
     * @param prompt The speech prompt containing the text and options for speech
     *               synthesis.
     * @return A Flux of SpeechResponse objects containing the streamed audio and
     * metadata.
     */
    @Override
    public Flux<SpeechResponse> stream(SpeechPrompt prompt) {
        return this.audioApi.stream(this.createRequestBody(prompt))
                .map(entity -> {

                    var speech = entity.getBody();

                    if (speech == null) {
                        logger.warn("No speech response returned for prompt: {}", prompt);
                        return new SpeechResponse(new Speech());
                    }
                    return new SpeechResponse(new Speech(speech.filename(), speech.url()), new EdgeTtsAudioSpeechResponseMetadata());
                });
    }

    private EdgeTtsAudioApi.SpeechRequest createRequestBody(SpeechPrompt prompt) {

        String input = prompt.getInstructions().getText();
        var request = new EdgeTtsAudioApi.SpeechRequest(input);

        if (this.defaultOptions != null) {
            request = ModelOptionsUtils.merge(request, this.defaultOptions, EdgeTtsAudioApi.SpeechRequest.class);
        }

        if (prompt.getOptions() != null) {
            if (prompt.getOptions() instanceof EdgeTtsAudioSpeechOptions runtimeOptions) {
                var updatedRuntimeOptions = ModelOptionsUtils.copyToTarget(runtimeOptions, ModelOptions.class,
                        EdgeTtsAudioSpeechOptions.class);
                request = ModelOptionsUtils.merge(request, updatedRuntimeOptions, EdgeTtsAudioApi.SpeechRequest.class);
            }
            else {
                throw new IllegalArgumentException("Prompt options are not of type SpeechOptions: "
                        + prompt.getOptions().getClass().getSimpleName());
            }
        }


        return request;

    }

    private EdgeTtsAudioSpeechOptions merge(EdgeTtsAudioSpeechOptions source, EdgeTtsAudioSpeechOptions target) {
        EdgeTtsAudioSpeechOptions.Builder mergedBuilder = EdgeTtsAudioSpeechOptions.builder();

        mergedBuilder.withText(source.getText() != null ? source.getText() : target.getText());
        mergedBuilder.withVoice(source.getVoice() != null ? source.getVoice() : target.getVoice());

        return mergedBuilder.build();
    }
}
