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
 * DTO for provider configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfigDTO {

    @NotNull(message = "Provider cannot be empty")
    private ModelProvider provider;

    @NotBlank(message = "API base URL cannot be empty")
    @Size(max = 500, message = "API base URL cannot exceed 500 characters")
    private String apiBaseUrl;

    @NotBlank(message = "API key cannot be empty")
    @Size(max = 500, message = "API key cannot exceed 500 characters")
    private String apiKey;
}
