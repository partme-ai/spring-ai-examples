package com.github.partmeai.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ChatTtsAudioSpeechOptions implements ModelOptions {

    /**
     * The input text to synthesize. Must be at most 4096 tokens long.
     */
    @JsonProperty("text")
    private String text;

    /**
     * The voice to use for synthesis.
     */
    @JsonProperty("voice")
    private String voice;

    /**
     * 填写prompt，如 [oral_2][laugh_0][break_6]
     */
    @JsonProperty("prompt")
    private String prompt;

    /**
     * The speed of the voice synthesis. The acceptable range is from 1 (slowest) to 9 (fastest). Defaults to 1.
     */
    @JsonProperty("speed")
    private Integer speed = 1;

    @JsonProperty("temperature")
    private Float temperature = 0.7F;

    @JsonProperty("top_p")
    private Float topP = 0.7F;

    @JsonProperty("top_k")
    private Integer topK = 20;

    @JsonProperty("refine_max_new_token")
    private Integer maxRefineTokens;

    @JsonProperty("infer_max_new_token")
    private Integer maxInferTokens = 2048;

    @JsonProperty("audio_seed")
    private Float audioSeed;

    @JsonProperty("text_seed")
    private Float textSeed;

    @JsonProperty("skip_refine")
    private Integer skipRefine;

    @JsonProperty("is_stream")
    private Integer stream;

    /**
     * 填写后将忽略音色选择，以该填写值获取音色,例如 2000，8000等
     */
    @JsonProperty("custom_voice")
    private Integer customVoice;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ChatTtsAudioSpeechOptions options = new ChatTtsAudioSpeechOptions();

        public Builder withText(String text) {
            options.text = text;
            return this;
        }

        public Builder withVoice(String voice) {
            options.voice = voice;
            return this;
        }

        public Builder withPrompt(String prompt) {
            options.prompt = prompt;
            return this;
        }

        public Builder withSpeed(Integer speed) {
            options.speed = speed;
            return this;
        }

        public Builder withTemperature(Float temperature) {
            options.temperature = temperature;
            return this;
        }

        public Builder temperature(Float temperature) {
            return withTemperature(temperature);
        }

        public Builder withTopP(Float topP) {
            options.topP = topP;
            return this;
        }

        public Builder topP(Float topP) {
            return withTopP(topP);
        }

        public Builder withTopK(Integer topK) {
            options.topK = topK;
            return this;
        }

        public Builder topK(Integer topK) {
            return withTopK(topK);
        }

        public Builder withMaxRefineTokens(Integer maxRefineTokens) {
            options.maxRefineTokens = maxRefineTokens;
            return this;
        }

        public Builder withMaxInferTokens(Integer maxInferTokens) {
            options.maxInferTokens = maxInferTokens;
            return this;
        }

        public Builder withTextSeed(Float textSeed) {
            options.textSeed = textSeed;
            return this;
        }

        public Builder withSkipRefine(Integer skipRefine) {
            options.skipRefine = skipRefine;
            return this;
        }

        public Builder withStream(Integer stream) {
            options.stream = stream;
            return this;
        }

        public Builder withCustomVoice(Integer customVoice) {
            options.customVoice = customVoice;
            return this;
        }

        public ChatTtsAudioSpeechOptions build() {
            return options;
        }

    }

}
