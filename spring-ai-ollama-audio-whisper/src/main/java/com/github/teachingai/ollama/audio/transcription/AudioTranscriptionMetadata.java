package com.github.partmeai.ollama.audio.transcription;

import org.springframework.ai.model.ResultMetadata;

public interface AudioTranscriptionMetadata extends ResultMetadata {

    AudioTranscriptionMetadata NULL = AudioTranscriptionMetadata.create();

    /**
     * Factory method used to construct a new {@link AudioTranscriptionMetadata}
     * @return a new {@link AudioTranscriptionMetadata}
     */
    static AudioTranscriptionMetadata create() {
        return new AudioTranscriptionMetadata() {
        };
    }

}
