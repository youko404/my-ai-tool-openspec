package com.ai.content.service;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ProviderConfigDTO;

/**
 * Service for provider configuration
 */
public interface ProviderConfigService {

    AiProviderConfig save(ProviderConfigDTO dto, String operator);

    AiProviderConfig getByProvider(ModelProvider provider);
}
