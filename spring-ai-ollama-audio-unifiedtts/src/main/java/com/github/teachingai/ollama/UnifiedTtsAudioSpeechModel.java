package com.github.partmeai.ollama;

import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import com.github.partmeai.ollama.api.UnifiedTtsResponseHeaderExtractor;
import com.github.partmeai.ollama.audio.UnifiedTtsAudioSpeechResponseMetadata;
import com.github.partmeai.ollama.audio.speech.Speech;
import com.github.partmeai.ollama.audio.speech.SpeechModel;
import com.github.partmeai.ollama.audio.speech.SpeechPrompt;
import com.github.partmeai.ollama.audio.speech.SpeechResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

public class UnifiedTtsAudioSpeechModel implements SpeechModel {

    /**
     * The speed of the default voice synthesis.
     * @see UnifiedTtsAudioSpeechOptions
     */
    private static final Float SPEED = 1.0f;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The default options used for the audio completion requests.
     */
    private final UnifiedTtsAudioSpeechOptions defaultOptions;

    /**
     * The retry template used to retry the UnifiedTts Audio API calls.
     */
    private final RetryTemplate retryTemplate;

    /**
     * Low-level access to the UnifiedTts Audio API.
     */
    private final UnifiedTtsAudioApi audioApi;

    /**
     * Initializes a new instance of the UnifiedTtsAudioSpeechModel class with the provided
     * UnifiedTtsAudioApi. It uses the model tts-1, response format mp3, voice alloy, and the
     * default speed of 1.0.
     * @param audioApi The UnifiedTtsAudioApi to use for speech synthesis.
     */
    public UnifiedTtsAudioSpeechModel(UnifiedTtsAudioApi audioApi) {
        this(audioApi, UnifiedTtsAudioSpeechOptions.builder()
                    .model(UnifiedTtsAudioApi.TtsModel.EDGE_TTS)
                    .voice(UnifiedTtsAudioApi.DEFAULT_VOICE)
                    .speed(SPEED)
                    .pitch(UnifiedTtsAudioApi.DEFAULT_PITCH)
                    .volume(UnifiedTtsAudioApi.DEFAULT_VOLUME)
                    .responseFormat(UnifiedTtsAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                    .build());
    }

    /**
     * Initializes a new instance of the UnifiedTtsAudioSpeechModel class with the provided
     * UnifiedTtsAudioApi and options.
     * @param audioApi The UnifiedTtsAudioApi to use for speech synthesis.
     * @param options The UnifiedTtsAudioSpeechOptions containing the speech synthesis
     * options.
     */
    public UnifiedTtsAudioSpeechModel(UnifiedTtsAudioApi audioApi, UnifiedTtsAudioSpeechOptions options) {
        this(audioApi, options, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * Initializes a new instance of the UnifiedTtsAudioSpeechModel class with the provided
     * UnifiedTtsAudioApi and options.
     * @param audioApi The UnifiedTtsAudioApi to use for speech synthesis.
     * @param options The UnifiedTtsAudioSpeechOptions containing the speech synthesis
     * options.
     * @param retryTemplate The retry template.
     */
    public UnifiedTtsAudioSpeechModel(UnifiedTtsAudioApi audioApi, UnifiedTtsAudioSpeechOptions options,
                                  RetryTemplate retryTemplate) {
        Assert.notNull(audioApi, "UnifiedTtsAudioApi must not be null");
        Assert.notNull(options, "UnifiedTtsAudioSpeechOptions must not be null");
        Assert.notNull(options, "RetryTemplate must not be null");
        this.audioApi = audioApi;
        this.defaultOptions = options;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public UnifiedTtsAudioApi.SpeechResponse.AudioFile call(String text) {
        SpeechPrompt speechRequest = new SpeechPrompt(text);
        return call(speechRequest).getResult().getOutput();
    }

    @Override
    public SpeechResponse call(SpeechPrompt speechPrompt) {

        UnifiedTtsAudioApi.SpeechRequest speechRequest = createRequest(speechPrompt);

        ResponseEntity<UnifiedTtsAudioApi.SpeechResponse> speechEntity = this.retryTemplate
                .execute(ctx -> this.audioApi.createSpeech(speechRequest));

        var speech = speechEntity.getBody();

        if (speech == null) {
            logger.warn("No speech response returned for speechRequest: {}", speechRequest);
            return new SpeechResponse(new Speech(null));
        }

        RateLimit rateLimits = UnifiedTtsResponseHeaderExtractor.extractAiResponseHeaders(speechEntity);

        return new SpeechResponse(new Speech(speech.getData()), new UnifiedTtsAudioSpeechResponseMetadata(rateLimits));
    }

    private UnifiedTtsAudioApi.SpeechRequest createRequest(SpeechPrompt request) {
        UnifiedTtsAudioSpeechOptions options = this.defaultOptions;

        if (Objects.nonNull(request.getOptions())) {
            if (request.getOptions() instanceof UnifiedTtsAudioSpeechOptions runtimeOptions) {
                options = this.merge(runtimeOptions, options);
            }
            else {
                throw new IllegalArgumentException("Prompt options are not of type SpeechOptions: "
                        + request.getOptions().getClass().getSimpleName());
            }
        }

        String input = StringUtils.hasText(options.getInput()) ? options.getInput()
                : request.getInstructions().getText();

        UnifiedTtsAudioApi.SpeechRequest.Builder requestBuilder = UnifiedTtsAudioApi.SpeechRequest.builder()
                .model(options.getModel())
                .input(input)
                .voice(options.getVoice())
                .responseFormat(options.getResponseFormat())
                .speed(options.getSpeed());

        return requestBuilder.build();
    }

    private UnifiedTtsAudioSpeechOptions merge(UnifiedTtsAudioSpeechOptions source, UnifiedTtsAudioSpeechOptions target) {
        UnifiedTtsAudioSpeechOptions.Builder mergedBuilder = UnifiedTtsAudioSpeechOptions.builder();

        mergedBuilder.model(source.getModel() != null ? source.getModel() : target.getModel());
        mergedBuilder.input(source.getInput() != null ? source.getInput() : target.getInput());
        mergedBuilder.voice(source.getVoice() != null ? source.getVoice() : target.getVoice());
        mergedBuilder.responseFormat(
                source.getResponseFormat() != null ? source.getResponseFormat() : target.getResponseFormat());
        mergedBuilder.speed(source.getSpeed() != null ? source.getSpeed() : target.getSpeed());

        return mergedBuilder.build();
    }

}
