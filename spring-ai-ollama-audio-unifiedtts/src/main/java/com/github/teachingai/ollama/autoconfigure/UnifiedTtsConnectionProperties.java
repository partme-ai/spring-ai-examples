package com.github.partmeai.ollama.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(UnifiedTtsConnectionProperties.CONFIG_PREFIX)
public class UnifiedTtsConnectionProperties {

    public static final String CONFIG_PREFIX = "spring.ai.unifiedtts";

    public static final String DEFAULT_BASE_URL = "https://unifiedtts.com";

    private String apiKey;

    private String baseUrl;

    public UnifiedTtsConnectionProperties() {
        this.setBaseUrl(DEFAULT_BASE_URL);
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


}
