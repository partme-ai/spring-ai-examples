package com.github.teachingai.ollama.metadata.audio;


import com.github.teachingai.ollama.api.EmotiVoiceAudioApi;
import org.springframework.ai.chat.metadata.EmptyRateLimit;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.model.AbstractResponseMetadata;
import org.springframework.ai.model.ResponseMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Audio speech metadata implementation for {@literal OpenAI}.
 *
 * @author Ahmed Yousri
 * @see RateLimit
 */
public class EmotiVoiceAudioSpeechResponseMetadata extends AbstractResponseMetadata implements ResponseMetadata {

    protected static final String AI_METADATA_STRING = "{ @type: %1$s, requestsLimit: %2$s }";

    public static final EmotiVoiceAudioSpeechResponseMetadata NULL = new EmotiVoiceAudioSpeechResponseMetadata() {
    };

    public static EmotiVoiceAudioSpeechResponseMetadata from(EmotiVoiceAudioApi.StructuredResponse result) {
        Assert.notNull(result, "ChatTTS speech must not be null");
        EmotiVoiceAudioSpeechResponseMetadata speechResponseMetadata = new EmotiVoiceAudioSpeechResponseMetadata();
        return speechResponseMetadata;
    }

    public static EmotiVoiceAudioSpeechResponseMetadata from(String result) {
        Assert.notNull(result, "ChatTTS speech must not be null");
        EmotiVoiceAudioSpeechResponseMetadata speechResponseMetadata = new EmotiVoiceAudioSpeechResponseMetadata();
        return speechResponseMetadata;
    }

    @Nullable
    private RateLimit rateLimit;

    public EmotiVoiceAudioSpeechResponseMetadata() {
        this(null);
    }

    public EmotiVoiceAudioSpeechResponseMetadata(@Nullable RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    @Nullable
    public RateLimit getRateLimit() {
        RateLimit rateLimit = this.rateLimit;
        return rateLimit != null ? rateLimit : new EmptyRateLimit();
    }

    public EmotiVoiceAudioSpeechResponseMetadata withRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
        return this;
    }

    @Override
    public String toString() {
        return AI_METADATA_STRING.formatted(getClass().getName(), getRateLimit());
    }

}
