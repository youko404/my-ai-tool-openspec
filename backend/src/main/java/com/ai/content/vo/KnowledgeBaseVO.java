package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View object for KnowledgeBase
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseVO {
    private Long id;
    private String name;
    private String description;
    private Boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KnowledgeBaseVO fromEntity(KnowledgeBase entity) {
        if (entity == null) {
            return null;
        }
        return KnowledgeBaseVO.builder().id(entity.getId()).name(entity.getName()).description(entity.getDescription())
            .isEnabled(entity.getIsEnabled()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }
}
