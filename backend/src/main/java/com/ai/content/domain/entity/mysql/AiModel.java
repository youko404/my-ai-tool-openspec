package com.ai.content.domain.entity.mysql;

import com.ai.content.domain.entity.BaseEntity;
import com.ai.content.domain.enums.ModelProvider;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AI Model Entity Represents AI model configuration with provider information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_model")
public class AiModel extends BaseEntity {

    /**
     * Model name (e.g., gpt-4, claude-3-opus)
     */
    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    /**
     * Service provider (e.g., OpenAI, Anthropic, Google)
     */
    @Column(name = "provider", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ModelProvider provider;

    /**
     * Encrypted API key for this model (optional)
     */
    @Column(name = "api_key", length = 500)
    private String apiKey;

    /**
     * Whether the model is enabled for use
     */
    @Column(name = "is_enabled", nullable = false)
    @Builder.Default
    private Boolean isEnabled = true;

    /**
     * Soft delete flag
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

}
