package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * View Object for provider configuration responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfigVO {

    private ModelProvider provider;
    private String apiBaseUrl;
    private boolean apiKeyConfigured;

    public static ProviderConfigVO fromEntity(AiProviderConfig entity, ModelProvider provider) {
        if (entity == null) {
            return ProviderConfigVO.builder().provider(provider).apiBaseUrl(null).apiKeyConfigured(false).build();
        }
        return ProviderConfigVO.builder().provider(entity.getProvider()).apiBaseUrl(entity.getApiBaseUrl())
            .apiKeyConfigured(StringUtils.hasText(entity.getApiKey())).build();
    }
}
