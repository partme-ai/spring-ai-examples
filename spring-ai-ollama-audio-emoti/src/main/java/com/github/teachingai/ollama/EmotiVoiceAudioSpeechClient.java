package com.github.partmeai.ollama;

import com.github.partmeai.ollama.api.EmotiVoiceAudioApi;
import com.github.partmeai.ollama.api.EmotiVoiceAudioApi.SpeechRequest.AudioResponseFormat;
import com.github.partmeai.ollama.api.EmotiVoiceResponseHeaderExtractor;
import com.github.partmeai.ollama.api.common.OllamaApiException;
import com.github.partmeai.ollama.metadata.audio.EmotiVoiceAudioSpeechResponseMetadata;
import com.github.partmeai.ollama.audio.speech.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class EmotiVoiceAudioSpeechClient implements SpeechClient, StreamingSpeechClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmotiVoiceAudioSpeechOptions defaultOptions;

    private static final Float SPEED = 1.0f;

    public final RetryTemplate retryTemplate = RetryTemplate.builder()
            .maxAttempts(10)
            .retryOn(OllamaApiException.class)
            .exponentialBackoff(Duration.ofMillis(2000), 5, Duration.ofMillis(3 * 60000))
            .build();

    private final EmotiVoiceAudioApi audioApi;

    /**
     * Initializes a new instance of the EmotiVoiceAudioSpeechClient class with the provided
     * EmotiVoiceAudioApi. It uses the model tts-1, response format mp3, voice alloy, and the
     * default speed of 1.0.
     * @param audioApi The EmotiVoiceAudioApi to use for speech synthesis.
     */
    public EmotiVoiceAudioSpeechClient(EmotiVoiceAudioApi audioApi) {
        this(audioApi,
                EmotiVoiceAudioSpeechOptions.builder()
                        .model(EmotiVoiceAudioApi.TtsModel.TTS_1.getValue())
                        .withResponseFormat(AudioResponseFormat.MP3)
                        .withVoice(EmotiVoiceAudioApi.SpeechRequest.Voice.ALLOY)
                        .withSpeed(SPEED)
                        .build());
    }

    /**
     * Initializes a new instance of the EmotiVoiceAudioSpeechClient class with the provided
     * EmotiVoiceAudioApi and options.
     * @param audioApi The EmotiVoiceAudioApi to use for speech synthesis.
     * @param options The EmotiVoiceAudioSpeechOptions containing the speech synthesis
     * options.
     */
    public EmotiVoiceAudioSpeechClient(EmotiVoiceAudioApi audioApi, EmotiVoiceAudioSpeechOptions options) {
        Assert.notNull(audioApi, "EmotiVoiceAudioApi must not be null");
        Assert.notNull(options, "EmotiVoiceSpeechOptions must not be null");
        this.audioApi = audioApi;
        this.defaultOptions = options;
    }

    @Override
    public byte[] call(String text) {
        SpeechPrompt speechRequest = new SpeechPrompt(text);
        return call(speechRequest).getResult().getOutput();
    }

    @Override
    public SpeechResponse call(SpeechPrompt speechPrompt) {

        return this.retryTemplate.execute(ctx -> {

            EmotiVoiceAudioApi.SpeechRequest speechRequest = createRequestBody(speechPrompt);

            ResponseEntity<byte[]> speechEntity = this.audioApi.createSpeech(speechRequest);
            var speech = speechEntity.getBody();

            if (speech == null) {
                logger.warn("No speech response returned for speechRequest: {}", speechRequest);
                return new SpeechResponse(new Speech(new byte[0]));
            }

            RateLimit rateLimits = EmotiVoiceResponseHeaderExtractor.extractAiResponseHeaders(speechEntity);

            return new SpeechResponse(new Speech(speech), new EmotiVoiceAudioSpeechResponseMetadata(rateLimits));

        });
    }

    /**
     * Streams the audio response for the given speech prompt.
     * @param prompt The speech prompt containing the text and options for speech
     * synthesis.
     * @return A Flux of SpeechResponse objects containing the streamed audio and
     * metadata.
     */
    @Override
    public Flux<SpeechResponse> stream(SpeechPrompt prompt) {
        return this.audioApi.stream(this.createRequestBody(prompt))
                .map(entity -> new SpeechResponse(new Speech(entity.getBody()), new EmotiVoiceAudioSpeechResponseMetadata(
                        EmotiVoiceResponseHeaderExtractor.extractAiResponseHeaders(entity))));
    }

    private EmotiVoiceAudioApi.SpeechRequest createRequestBody(SpeechPrompt request) {
        EmotiVoiceAudioSpeechOptions options = this.defaultOptions;

        if (request.getOptions() != null) {
            if (request.getOptions() instanceof EmotiVoiceAudioSpeechOptions runtimeOptions) {
                options = this.merge(options, runtimeOptions);
            }
            else {
                throw new IllegalArgumentException("Prompt options are not of type SpeechOptions: "
                        + request.getOptions().getClass().getSimpleName());
            }
        }

        String input = StringUtils.isNotBlank(options.getInput()) ? options.getInput()
                : request.getInstructions().getText();

        EmotiVoiceAudioApi.SpeechRequest.Builder requestBuilder = EmotiVoiceAudioApi.SpeechRequest.builder()
                .model(options.getModel())
                .withInput(input)
                .withVoice(options.getVoice())
                .withResponseFormat(options.getResponseFormat())
                .withSpeed(options.getSpeed());

        return requestBuilder.build();
    }

    private EmotiVoiceAudioSpeechOptions merge(EmotiVoiceAudioSpeechOptions source, EmotiVoiceAudioSpeechOptions target) {
        EmotiVoiceAudioSpeechOptions.Builder mergedBuilder = EmotiVoiceAudioSpeechOptions.builder();

        mergedBuilder.model(source.getModel() != null ? source.getModel() : target.getModel());
        mergedBuilder.withInput(source.getInput() != null ? source.getInput() : target.getInput());
        mergedBuilder.withVoice(source.getVoice() != null ? source.getVoice() : target.getVoice());
        mergedBuilder.withResponseFormat(
                source.getResponseFormat() != null ? source.getResponseFormat() : target.getResponseFormat());
        mergedBuilder.withSpeed(source.getSpeed() != null ? source.getSpeed() : target.getSpeed());

        return mergedBuilder.build();
    }

}
