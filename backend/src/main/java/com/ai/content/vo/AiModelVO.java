package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View Object for AI Model responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelVO {

    private Long id;
    private String modelName;
    private ModelProvider provider;
    private Boolean isEnabled;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert entity to VO
     */
    public static AiModelVO fromEntity(AiModel entity) {
        if (entity == null) {
            return null;
        }
        return AiModelVO.builder().id(entity.getId()).modelName(entity.getModelName()).provider(entity.getProvider())
            .isEnabled(entity.getIsEnabled()).createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy())
            .createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build();
    }

}
