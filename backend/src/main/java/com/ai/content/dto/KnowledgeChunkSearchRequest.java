package com.ai.content.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for knowledge chunk search
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunkSearchRequest {

    @NotBlank(message = "Query text cannot be empty")
    private String queryText;

    @NotNull(message = "Minimum score is required")
    private Double minScore;

    @NotNull(message = "Limit is required")
    @Min(value = 1, message = "Limit must be at least 1")
    private Integer limit;
}
