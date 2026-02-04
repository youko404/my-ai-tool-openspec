package com.ai.content.domain.entity.mysql;

import com.ai.content.domain.entity.BaseEntity;
import com.ai.content.domain.enums.ModelProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AI Provider configuration stored in database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_provider_config", uniqueConstraints = {@UniqueConstraint(columnNames = "provider")})
public class AiProviderConfig extends BaseEntity {

    /**
     * Provider identifier (e.g., OPENROUTER, OPENAI)
     */
    @Column(name = "provider", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ModelProvider provider;

    /**
     * API base URL for the provider
     */
    @Column(name = "api_base_url", nullable = false, length = 500)
    private String apiBaseUrl;

    /**
     * Encrypted API key for the provider
     */
    @Column(name = "api_key", nullable = false, length = 500)
    private String apiKey;
}
