package com.ai.content.repository.mysql;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for AI provider configuration
 */
public interface AiProviderConfigRepository extends JpaRepository<AiProviderConfig, Long> {

    Optional<AiProviderConfig> findByProvider(ModelProvider provider);
}
