package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ProviderConfigDTO;
import com.ai.content.repository.mysql.AiProviderConfigRepository;
import com.ai.content.service.ApiKeyCipher;
import com.ai.content.service.ProviderConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Provider configuration service implementation
 */
@Service
@RequiredArgsConstructor
public class ProviderConfigServiceImpl implements ProviderConfigService {

    private final AiProviderConfigRepository repository;
    private final ApiKeyCipher apiKeyCipher;

    @Override
    public AiProviderConfig save(ProviderConfigDTO dto, String operator) {
        if (dto == null || dto.getProvider() == null) {
            throw new IllegalArgumentException("Provider is required");
        }
        if (!StringUtils.hasText(dto.getApiBaseUrl())) {
            throw new IllegalArgumentException("API base URL is required");
        }
        if (!StringUtils.hasText(dto.getApiKey())) {
            throw new IllegalArgumentException("API key is required");
        }

        AiProviderConfig config = repository.findByProvider(dto.getProvider()).orElseGet(AiProviderConfig::new);
        config.setProvider(dto.getProvider());
        config.setApiBaseUrl(dto.getApiBaseUrl().trim());
        config.setApiKey(apiKeyCipher.encrypt(dto.getApiKey()));
        if (config.getId() == null) {
            config.setCreatedBy(operator);
        }
        config.setUpdatedBy(operator);
        return repository.save(config);
    }

    @Override
    public AiProviderConfig getByProvider(ModelProvider provider) {
        if (provider == null) {
            return null;
        }
        return repository.findByProvider(provider).orElse(null);
    }
}
