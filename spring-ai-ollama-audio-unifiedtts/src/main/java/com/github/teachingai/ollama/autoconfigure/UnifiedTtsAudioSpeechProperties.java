package com.github.partmeai.ollama.autoconfigure;

import com.github.partmeai.ollama.UnifiedTtsAudioSpeechOptions;
import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(UnifiedTtsAudioSpeechProperties.CONFIG_PREFIX)
public class UnifiedTtsAudioSpeechProperties {

    public static final String CONFIG_PREFIX = "spring.ai.unifiedtts.audio.speech";

    public static final UnifiedTtsAudioApi.TtsModel DEFAULT_SPEECH_MODEL = UnifiedTtsAudioApi.TtsModel.EDGE_TTS;

    private static final Float SPEED = 1.0f;

    private static final String VOICE = UnifiedTtsAudioApi.DEFAULT_VOICE;

    private static final UnifiedTtsAudioApi.SpeechRequest.AudioResponseFormat DEFAULT_RESPONSE_FORMAT = UnifiedTtsAudioApi.SpeechRequest.AudioResponseFormat.MP3;

    @NestedConfigurationProperty
    private final UnifiedTtsAudioSpeechOptions options = UnifiedTtsAudioSpeechOptions.builder()
            .model(DEFAULT_SPEECH_MODEL)
            .responseFormat(DEFAULT_RESPONSE_FORMAT)
            .voice(VOICE)
            .speed(SPEED)
            .build();

    public UnifiedTtsAudioSpeechOptions getOptions() {
        return this.options;
    }

}
