package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.Workflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View Object for Workflow responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowVO {

    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Boolean deleted;
    private String creator;
    private String editor;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert entity to VO
     */
    public static WorkflowVO fromEntity(Workflow entity) {
        if (entity == null) {
            return null;
        }
        return WorkflowVO.builder().id(entity.getId()).name(entity.getName()).description(entity.getDescription())
            .enabled(entity.getEnabled()).deleted(entity.getDeleted()).creator(entity.getCreator())
            .editor(entity.getEditor()).createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy())
            .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }
}
