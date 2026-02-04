package com.ai.content.service.provider;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.service.ApiKeyCipher;
import com.ai.content.service.ProviderConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public abstract class AbstractProviderChatClient implements ProviderChatClient {

    private final ProviderConfigService providerConfigService;
    private final ApiKeyCipher apiKeyCipher;

    protected ProviderCredentials resolveProviderCredentials() {
        return resolveProviderCredentials(getProvider());
    }

    protected ProviderCredentials resolveProviderCredentials(ModelProvider provider) {
        AiProviderConfig config = providerConfigService.getByProvider(provider);
        if (config == null || !StringUtils.hasText(config.getApiBaseUrl()) || !StringUtils.hasText(
            config.getApiKey())) {
            throw new IllegalArgumentException("Provider config not found for " + provider);
        }
        String apiKey = apiKeyCipher.decrypt(config.getApiKey());
        String apiBaseUrl = normalizeBaseUrl(config.getApiBaseUrl());
        return new ProviderCredentials(apiKey, apiBaseUrl);
    }

    protected String normalizeBaseUrl(String apiBaseUrl) {
        if (!StringUtils.hasText(apiBaseUrl)) {
            return apiBaseUrl;
        }
        String trimmed = apiBaseUrl.trim();
        if (trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    protected static final class ProviderCredentials {
        private final String apiKey;
        private final String apiBaseUrl;

        private ProviderCredentials(String apiKey, String apiBaseUrl) {
            this.apiKey = apiKey;
            this.apiBaseUrl = apiBaseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }
    }
}
