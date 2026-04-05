package com.github.partmeai.ollama.audio.speech;

import com.github.partmeai.ollama.audio.UnifiedTtsAudioSpeechResponseMetadata;
import org.springframework.ai.model.ModelResponse;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Creates a new instance of SpeechResponse with the given speech result.
 */
public class SpeechResponse implements ModelResponse<Speech> {

    private final Speech speech;

    private final UnifiedTtsAudioSpeechResponseMetadata speechResponseMetadata;

    /**
     * Creates a new instance of SpeechResponse with the given speech result.
     * @param speech the speech result to be set in the SpeechResponse
     * @see Speech
     */
    public SpeechResponse(Speech speech) {
        this(speech, UnifiedTtsAudioSpeechResponseMetadata.NULL);
    }

    /**
     * Creates a new instance of SpeechResponse with the given speech result and speech
     * response metadata.
     * @param speech the speech result to be set in the SpeechResponse
     * @param speechResponseMetadata the speech response metadata to be set in the
     * SpeechResponse
     * @see Speech
     * @see UnifiedTtsAudioSpeechResponseMetadata
     */
    public SpeechResponse(Speech speech, UnifiedTtsAudioSpeechResponseMetadata speechResponseMetadata) {
        this.speech = speech;
        this.speechResponseMetadata = speechResponseMetadata;
    }

    @Override
    public Speech getResult() {
        return speech;
    }

    @Override
    public List<Speech> getResults() {
        return Collections.singletonList(speech);
    }

    @Override
    public UnifiedTtsAudioSpeechResponseMetadata getMetadata() {
        return speechResponseMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpeechResponse that)) {
            return false;
        }
        return Objects.equals(speech, that.speech)
                && Objects.equals(speechResponseMetadata, that.speechResponseMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speech, speechResponseMetadata);
    }

}
