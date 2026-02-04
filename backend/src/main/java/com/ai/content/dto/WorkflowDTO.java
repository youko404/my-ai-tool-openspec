package com.ai.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Workflow Data Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDTO {

    /**
     * Workflow name (required)
     */
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name is too long")
    private String name;

    /**
     * Workflow description (optional)
     */
    @Size(max = 1000, message = "Description is too long")
    private String description;

    /**
     * Whether the workflow is enabled
     */
    private Boolean enabled;
}
