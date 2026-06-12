package com.github.partmeai.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import lombok.Data;
import lombok.ToString;
import org.springframework.ai.model.ModelOptions;

/**
 * Options for ChatTTS text to audio - speech synthesis.
 * @since 2024.06.28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@ToString
public class UnifiedTtsAudioSpeechOptions implements ModelOptions {

    /**
     * TTS模型名称，可通过模型列表接口获取
     */
    @JsonProperty("model")
    private UnifiedTtsAudioApi.TtsModel model;

    /**
     * 要转换为语音的文本内容（最大支持10,000字符）
     */
    @JsonProperty("input")
    private String input;

    /**
     * 音色ID，每个模型下的音色唯一标识
     */
    @JsonProperty("voice")
    private String voice;

    /**
     * 语速倍率，范围0.5-2.0，默认1.0
     */
    @JsonProperty("speed")
    private Float speed = 1.0F;

    /**
     * 音调倍率，范围0.5-2.0，默认1.0
     */
    @JsonProperty("pitch")
    private Float pitch = 1.0F;

    /**
     * 音量倍率，范围0.5-2.0，默认1.0
     */
    @JsonProperty("volume")
    private Float volume = 1.0F;

    /**
     * 输出音频格式，支持mp3、wav等，默认mp3
     */
    @JsonProperty("response_format")
    private UnifiedTtsAudioApi.SpeechRequest.AudioResponseFormat responseFormat;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final UnifiedTtsAudioSpeechOptions options = new UnifiedTtsAudioSpeechOptions();

        public Builder model(UnifiedTtsAudioApi.TtsModel model) {
            options.model = model;
            return this;
        }

        public Builder input(String input) {
            options.input = input;
            return this;
        }

        public Builder voice(String voice) {
            options.voice = voice;
            return this;
        }

        public Builder speed(Float speed) {
            options.speed = speed;
            return this;
        }

        public Builder pitch(Float pitch) {
            options.pitch = pitch;
            return this;
        }

        public Builder volume(Float volume) {
            options.volume = volume;
            return this;
        }

        public Builder responseFormat(UnifiedTtsAudioApi.SpeechRequest.AudioResponseFormat format) {
            options.responseFormat = format;
            return this;
        }

        public UnifiedTtsAudioSpeechOptions build() {
            return options;
        }

    }

}
