package com.github.partmeai.ollama.metadata.audio;


import org.springframework.ai.model.ResultMetadata;

public interface EmotiVoiceAudioSpeechMetadata extends ResultMetadata {

    EmotiVoiceAudioSpeechMetadata NULL = EmotiVoiceAudioSpeechMetadata.create();

    /**
     * Factory method used to construct a new {@link EmotiVoiceAudioSpeechMetadata}
     * @return a new {@link EmotiVoiceAudioSpeechMetadata}
     */
    static EmotiVoiceAudioSpeechMetadata create() {
        return new EmotiVoiceAudioSpeechMetadata() {
        };
    }

}
