package com.github.partmeai.ollama.audio;


import org.springframework.ai.model.ResultMetadata;

public interface UnifiedTtsAudioSpeechMetadata extends ResultMetadata {

    UnifiedTtsAudioSpeechMetadata NULL = UnifiedTtsAudioSpeechMetadata.create();

    /**
     * Factory method used to construct a new {@link UnifiedTtsAudioSpeechMetadata}
     * @return a new {@link UnifiedTtsAudioSpeechMetadata}
     */
    static UnifiedTtsAudioSpeechMetadata create() {
        return new UnifiedTtsAudioSpeechMetadata() {
        };
    }

}
