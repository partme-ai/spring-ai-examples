package com.github.partmeai.ollama;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.ai.model.ModelOptions;


/**
 * Options for Edge-TTS text to audio - speech synthesis.
 * @since 2024.07.03
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@ToString
public class EdgeTtsAudioSpeechOptions implements ModelOptions {

    /**
     * The input text to synthesize.
     */
    @JsonProperty("text")
    private String text;

    /**
     * The voice to use for synthesis. Default: en-US-AriaNeural
     * @see <a href="https://docs.microsoft.com/en-us/azure/cognitive-services/speech-service/language-support#text-to-speech">Text-to-Speech voice support</a>
     */
    @JsonProperty("voice")
    private String voice;

    /**
     * set TTS rate. Default +0%.
     */
    @JsonProperty("rate")
    private String rate;

    /**
     * set TTS volume. Default +0%.
     */
    @JsonProperty("volume")
    private String volume;

    /**
     * set TTS pitch. Default +0Hz.
     */
    @JsonProperty("pitch")
    private String pitch;

    /**
     * number of words in a subtitle cue. Default: 10.
     */
    @JsonProperty("words_in_cue")
    private Integer wordsInCue;

    /**
     * send subtitle output to provided file instead of stderr
     */
    @JsonProperty("write_subtitles")
    private String writeSubtitles;

    /**
     * use a proxy for TTS and voice list.
     */
    @JsonProperty("proxy")
    private String proxy;

    /**
     * The output dir to write the synthesized audio to.
     */
    @JsonProperty("output")
    private String output;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final EdgeTtsAudioSpeechOptions options = new EdgeTtsAudioSpeechOptions();

        public Builder withText(String text) {
            options.text = text;
            return this;
        }

        public Builder withVoice(String voice) {
            options.voice = voice;
            return this;
        }

        public Builder withRate(String rate) {
            options.rate = rate;
            return this;
        }

        public Builder withVolume(String volume) {
            options.volume = volume;
            return this;
        }

        public Builder withPitch(String pitch) {
            options.pitch = pitch;
            return this;
        }

        public Builder withWordsInCue(Integer wordsInCue) {
            options.wordsInCue = wordsInCue;
            return this;
        }

        public Builder withWriteSubtitles(String writeSubtitles) {
            options.writeSubtitles = writeSubtitles;
            return this;
        }

        public Builder withProxy(String proxy) {
            options.proxy = proxy;
            return this;
        }

        public Builder withOutput(String output) {
            options.output = output;
            return this;
        }

        public EdgeTtsAudioSpeechOptions build() {
            return options;
        }

    }

}
