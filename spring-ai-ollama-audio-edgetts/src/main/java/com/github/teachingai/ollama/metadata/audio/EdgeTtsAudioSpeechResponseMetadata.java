package com.github.teachingai.ollama.metadata.audio;


import com.github.teachingai.ollama.api.EdgeTtsNativeAudioApi;
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
public class EdgeTtsAudioSpeechResponseMetadata extends AbstractResponseMetadata implements ResponseMetadata {

    protected static final String AI_METADATA_STRING = "{ @type: %1$s, requestsLimit: %2$s }";

    public static final EdgeTtsAudioSpeechResponseMetadata NULL = new EdgeTtsAudioSpeechResponseMetadata() {
    };

    public static EdgeTtsAudioSpeechResponseMetadata from(EdgeTtsNativeAudioApi.SpeechResponse result) {
        Assert.notNull(result, "ChatTTS speech must not be null");
        EdgeTtsAudioSpeechResponseMetadata speechResponseMetadata = new EdgeTtsAudioSpeechResponseMetadata();
        return speechResponseMetadata;
    }

    public static EdgeTtsAudioSpeechResponseMetadata from(String result) {
        Assert.notNull(result, "ChatTTS speech must not be null");
        EdgeTtsAudioSpeechResponseMetadata speechResponseMetadata = new EdgeTtsAudioSpeechResponseMetadata();
        return speechResponseMetadata;
    }

    @Nullable
    private RateLimit rateLimit;

    public EdgeTtsAudioSpeechResponseMetadata() {
        this(null);
    }

    public EdgeTtsAudioSpeechResponseMetadata(@Nullable RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    @Nullable
    public RateLimit getRateLimit() {
        RateLimit rateLimit = this.rateLimit;
        return rateLimit != null ? rateLimit : new EmptyRateLimit();
    }

    public EdgeTtsAudioSpeechResponseMetadata withRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
        return this;
    }

    @Override
    public String toString() {
        return AI_METADATA_STRING.formatted(getClass().getName(), getRateLimit());
    }

}
