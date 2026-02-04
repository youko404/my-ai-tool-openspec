package com.ai.content.dto;

import com.ai.content.domain.enums.ModelProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating AI Model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelDTO {

    /**
     * Model name (e.g., gpt-4, claude-3-opus)
     */
    @NotBlank(message = "Model name cannot be empty")
    @Size(max = 100, message = "Model name cannot exceed 100 characters")
    private String modelName;

    /**
     * Service provider (e.g., OpenAI, Anthropic, Google)
     */
    @NotNull(message = "Provider cannot be empty")
    private ModelProvider provider;

    /**
     * API key for the model (optional, stored encrypted)
     */
    @Size(max = 500, message = "API key cannot exceed 500 characters")
    private String apiKey;

    /**
     * Whether the model is enabled for use
     */
    private Boolean isEnabled;

}
