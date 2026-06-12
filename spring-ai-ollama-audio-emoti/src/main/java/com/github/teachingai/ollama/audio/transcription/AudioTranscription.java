package com.github.partmeai.ollama.audio.transcription;


import org.springframework.ai.model.ModelResult;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Represents a response returned by the AI.
 */
public class AudioTranscription implements ModelResult<String> {

    private String text;

    private AudioTranscriptionMetadata transcriptionMetadata;

    public AudioTranscription(String text) {
        this.text = text;
    }

    @Override
    public String getOutput() {
        return this.text;
    }

    @Override
    public AudioTranscriptionMetadata getMetadata() {
        return transcriptionMetadata != null ? transcriptionMetadata : AudioTranscriptionMetadata.NULL;
    }

    public AudioTranscription withTranscriptionMetadata(
            @Nullable AudioTranscriptionMetadata transcriptionMetadata) {
        this.transcriptionMetadata = transcriptionMetadata;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AudioTranscription that)) {
            return false;
        }
        return Objects.equals(text, that.text) && Objects.equals(transcriptionMetadata, that.transcriptionMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, transcriptionMetadata);
    }

    @Override
    public String toString() {
        return "Transcript{" + "text=" + text + ", transcriptionMetadata=" + transcriptionMetadata + '}';
    }

}
