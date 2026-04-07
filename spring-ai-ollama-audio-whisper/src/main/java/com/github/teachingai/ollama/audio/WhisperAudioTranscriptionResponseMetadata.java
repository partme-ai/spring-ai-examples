package com.github.teachingai.ollama.audio;


import com.github.teachingai.ollama.api.ChatTtsAudioApi;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.model.AbstractResponseMetadata;
import org.springframework.ai.model.ResponseMetadata;
import org.springframework.util.Assert;

/**
 * Audio transcription metadata implementation for {@literal OpenAI}.
 * @since 0.8.1
 * @see RateLimit
 */
public class WhisperAudioTranscriptionResponseMetadata extends AbstractResponseMetadata implements ResponseMetadata {

    protected static final String AI_METADATA_STRING = "{ @type: %1$s, rateLimit: %4$s }";

    public static final WhisperAudioTranscriptionResponseMetadata NULL = new WhisperAudioTranscriptionResponseMetadata() {
    };

    public static WhisperAudioTranscriptionResponseMetadata from(ChatTtsAudioApi.StructuredResponse result) {
        Assert.notNull(result, "ChatTTS Transcription must not be null");
        WhisperAudioTranscriptionResponseMetadata transcriptionResponseMetadata = new WhisperAudioTranscriptionResponseMetadata();
        return transcriptionResponseMetadata;
    }

    public static WhisperAudioTranscriptionResponseMetadata from(String result) {
        Assert.notNull(result, "ChatTTS Transcription must not be null");
        WhisperAudioTranscriptionResponseMetadata transcriptionResponseMetadata = new WhisperAudioTranscriptionResponseMetadata();
        return transcriptionResponseMetadata;
    }

    protected WhisperAudioTranscriptionResponseMetadata() {

    }

}
