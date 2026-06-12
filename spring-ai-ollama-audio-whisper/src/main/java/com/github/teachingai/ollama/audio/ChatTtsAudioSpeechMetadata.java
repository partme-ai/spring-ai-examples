package com.github.partmeai.ollama.audio;


import org.springframework.ai.model.ResultMetadata;

public interface ChatTtsAudioSpeechMetadata extends ResultMetadata {

    ChatTtsAudioSpeechMetadata NULL = ChatTtsAudioSpeechMetadata.create();

    /**
     * Factory method used to construct a new {@link ChatTtsAudioSpeechMetadata}
     * @return a new {@link ChatTtsAudioSpeechMetadata}
     */
    static ChatTtsAudioSpeechMetadata create() {
        return new ChatTtsAudioSpeechMetadata() {
        };
    }

}
