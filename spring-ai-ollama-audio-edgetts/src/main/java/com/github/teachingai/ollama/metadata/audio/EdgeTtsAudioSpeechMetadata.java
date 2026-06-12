package com.github.partmeai.ollama.metadata.audio;


import org.springframework.ai.model.ResultMetadata;

public interface EdgeTtsAudioSpeechMetadata extends ResultMetadata {

    EdgeTtsAudioSpeechMetadata NULL = EdgeTtsAudioSpeechMetadata.create();

    /**
     * Factory method used to construct a new {@link EdgeTtsAudioSpeechMetadata}
     * @return a new {@link EdgeTtsAudioSpeechMetadata}
     */
    static EdgeTtsAudioSpeechMetadata create() {
        return new EdgeTtsAudioSpeechMetadata() {
        };
    }

}
