package com.github.partmeai.ollama.autoconfigure;


import com.github.partmeai.ollama.UnifiedTtsAudioSpeechModel;
import com.github.partmeai.ollama.api.UnifiedTtsAudioApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link AutoConfiguration Auto-configuration} for UnifiedTts.
 *
 * @author Christian Tzolov
 * @author Stefan Vassilev
 * @author Thomas Vitale
 * @author Ilayaperumal Gopinathan
 */
@AutoConfiguration(after = { RestClientAutoConfiguration.class, WebClientAutoConfiguration.class, SpringAiRetryAutoConfiguration.class })
@ConditionalOnClass(UnifiedTtsAudioApi.class)
@ConditionalOnProperty(name = SpringAIModelProperties.AUDIO_SPEECH_MODEL, havingValue = "unifiedtts", matchIfMissing = true)
@EnableConfigurationProperties({ UnifiedTtsConnectionProperties.class, UnifiedTtsAudioSpeechProperties.class })
@ImportAutoConfiguration(classes = { SpringAiRetryAutoConfiguration.class, RestClientAutoConfiguration.class, WebClientAutoConfiguration.class })
public class UnifiedTtsAudioSpeechAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UnifiedTtsAudioSpeechModel unifiedttsAudioSpeechModel(UnifiedTtsConnectionProperties commonProperties,
                                                             UnifiedTtsAudioSpeechProperties speechProperties,
                                                             ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                                                             ObjectProvider<WebClient.Builder> webClientBuilderProvider, ResponseErrorHandler responseErrorHandler) {

        var unifiedTtsAudioApi = UnifiedTtsAudioApi.builder()
                .baseUrl(commonProperties.getBaseUrl())
                .apiKey(new SimpleApiKey(commonProperties.getApiKey()))
                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
                .webClientBuilder(webClientBuilderProvider.getIfAvailable(WebClient::builder))
                .responseErrorHandler(responseErrorHandler)
                .build();

        return new UnifiedTtsAudioSpeechModel(unifiedTtsAudioApi, speechProperties.getOptions());
    }

}
