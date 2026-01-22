package com.ai.content.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Provider cannot be empty")
    @Size(max = 50, message = "Provider cannot exceed 50 characters")
    private String provider;

    /**
     * Whether the model is enabled for use
     */
    private Boolean isEnabled;

}
