package com.github.teachingai.ollama.audio.speech;


import com.github.teachingai.ollama.audio.ChatTtsAudioSpeechMetadata;
import org.springframework.ai.model.ModelResult;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * The Speech class represents the result of speech synthesis from an AI model. It
 * implements the ModelResult interface with the output type of byte array.
 *
 * @author Ahmed Yousri
 * @since 1.0.0-M1
 */
public class Speech implements ModelResult<byte[]> {

    private final byte[] audio;

    private ChatTtsAudioSpeechMetadata speechMetadata;

    public Speech(byte[] audio) {
        this.audio = audio;
    }

    @Override
    public byte[] getOutput() {
        return this.audio;
    }

    @Override
    public ChatTtsAudioSpeechMetadata getMetadata() {
        return speechMetadata != null ? speechMetadata : ChatTtsAudioSpeechMetadata.NULL;
    }

    public Speech withSpeechMetadata(@Nullable ChatTtsAudioSpeechMetadata speechMetadata) {
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
        return Arrays.equals(audio, that.audio) && Objects.equals(speechMetadata, that.speechMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(audio), speechMetadata);
    }

    @Override
    public String toString() {
        return "Speech{" + "text=" + audio + ", speechMetadata=" + speechMetadata + '}';
    }

}
