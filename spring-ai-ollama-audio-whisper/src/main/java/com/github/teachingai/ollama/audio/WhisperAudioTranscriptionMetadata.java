package com.github.partmeai.ollama.audio;

import org.springframework.ai.model.ResultMetadata;

public interface WhisperAudioTranscriptionMetadata extends ResultMetadata {

    WhisperAudioTranscriptionMetadata NULL = WhisperAudioTranscriptionMetadata.create();

    /**
     * Factory method used to construct a new {@link WhisperAudioTranscriptionMetadata}
     * @return a new {@link WhisperAudioTranscriptionMetadata}
     */
    static WhisperAudioTranscriptionMetadata create() {
        return new WhisperAudioTranscriptionMetadata() {
        };
    }

}
