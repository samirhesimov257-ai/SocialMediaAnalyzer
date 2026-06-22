package com.socialmediaanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "meta.graph.api")
public record MetaApiProperties(
        String baseUrl,
        String accessToken,
        String pageId,
        int postLimit,
        boolean useDemoOnEmpty
) {
    public boolean isConfigured() {
        return accessToken != null && !accessToken.isBlank();
    }
}
