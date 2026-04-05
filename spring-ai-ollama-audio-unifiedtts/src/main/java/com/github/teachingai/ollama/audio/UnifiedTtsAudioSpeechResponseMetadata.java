package com.github.partmeai.ollama.audio;


import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import org.springframework.ai.chat.metadata.EmptyRateLimit;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.model.MutableResponseMetadata;
import org.springframework.ai.moderation.ModerationGenerationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Audio speech metadata implementation for {@literal OpenAI}.
 *
 * @author Ahmed Yousri
 * @see RateLimit
 */
public class UnifiedTtsAudioSpeechResponseMetadata extends MutableResponseMetadata {

    public static final UnifiedTtsAudioSpeechResponseMetadata NULL = new UnifiedTtsAudioSpeechResponseMetadata() {

    };

    protected static final String AI_METADATA_STRING = "{ @type: %1$s, requestsLimit: %2$s }";

    @Nullable
    private RateLimit rateLimit;

    public UnifiedTtsAudioSpeechResponseMetadata() {
        this(null);
    }

    public UnifiedTtsAudioSpeechResponseMetadata(@Nullable RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public static UnifiedTtsAudioSpeechResponseMetadata from(String result) {
        Assert.notNull(result, "OpenAI speech must not be null");
        UnifiedTtsAudioSpeechResponseMetadata speechResponseMetadata = new UnifiedTtsAudioSpeechResponseMetadata();
        return speechResponseMetadata;
    }

    @Nullable
    public RateLimit getRateLimit() {
        RateLimit rateLimit = this.rateLimit;
        return rateLimit != null ? rateLimit : new EmptyRateLimit();
    }

    public UnifiedTtsAudioSpeechResponseMetadata withRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
        return this;
    }

    @Override
    public String toString() {
        return AI_METADATA_STRING.formatted(getClass().getName(), getRateLimit());
    }

}
