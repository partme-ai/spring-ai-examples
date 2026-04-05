package com.github.partmeai.ollama.audio.speech;


import com.github.partmeai.ollama.api.common.OllamaApiException;
import com.github.partmeai.ollama.metadata.audio.ChatTtsAudioSpeechMetadata;
import org.springframework.ai.model.ModelResult;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * The Speech class represents the result of speech synthesis from an AI model. It
 * implements the ModelResult interface with the output type of byte array.
 *
 * @since 2024.06.28
 */
public class Speech implements ModelResult<InputStream> {

    private String file;
    private String url;

    private ChatTtsAudioSpeechMetadata speechMetadata;

    public Speech() {
    }

    public Speech(String file, String url) {
        this.file = file;
        this.url = url;
    }

    @Override
    public InputStream getOutput() {
        if(StringUtils.hasText(this.file)){
            File file1 = new File(this.file);
            if(file1.exists() && file1.isFile()){
                try {
                    return new FileInputStream(file1);
                } catch (Exception e) {
                    throw new OllamaApiException("Audio File Read Error, ", e);
                }
            }
        }
        if(StringUtils.hasText(this.url)){
            try {
                return new URL(this.url).openStream();
            } catch (Exception e) {
                throw new OllamaApiException("Audio File Read Error, ", e);
            }
        }
        return null;
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
        return StringUtils.endsWithIgnoreCase(file, that.file)
                && Objects.equals(speechMetadata, that.speechMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, speechMetadata);
    }

    @Override
    public String toString() {
        return "Speech{" + "audio=" + file + ", speechMetadata=" + speechMetadata + '}';
    }

}
