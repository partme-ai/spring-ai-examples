package com.github.partmeai.ollama.audio.speech;


import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import com.github.partmeai.ollama.audio.UnifiedTtsAudioSpeechMetadata;
import org.springframework.ai.model.ModelResult;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * The Speech class represents the result of speech synthesis from an AI model. It
 * implements the ModelResult interface with the output type of byte array.
 */
public class Speech implements ModelResult<UnifiedTtsAudioApi.SpeechResponse.AudioFile> {

    private final UnifiedTtsAudioApi.SpeechResponse.AudioFile audio;

    private UnifiedTtsAudioSpeechMetadata speechMetadata;

    public Speech(UnifiedTtsAudioApi.SpeechResponse.AudioFile audio) {
        this.audio = audio;
    }

    @Override
    public UnifiedTtsAudioApi.SpeechResponse.AudioFile getOutput() {
        return this.audio;
    }

    @Override
    public UnifiedTtsAudioSpeechMetadata getMetadata() {
        return this.speechMetadata != null ? this.speechMetadata : UnifiedTtsAudioSpeechMetadata.NULL;
    }

    public Speech withSpeechMetadata(@Nullable UnifiedTtsAudioSpeechMetadata speechMetadata) {
        this.speechMetadata = speechMetadata;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Speech that)) {
            return false;
        }

        return Objects.equals(this.audio.requestId(), that.audio.requestId()) && Objects.equals(this.speechMetadata, that.speechMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.audio, this.speechMetadata);
    }

    @Override
    public String toString() {
        return "Speech{" + "text=" + this.audio + ", speechMetadata=" + this.speechMetadata + '}';
    }

}