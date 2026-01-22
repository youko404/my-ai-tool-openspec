package com.ai.content.vo;

import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import com.ai.content.domain.entity.KnowledgeIngestionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View object for ingestion job status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeIngestionJobVO {
    private Long id;
    private Long knowledgeBaseId;
    private KnowledgeIngestionStatus status;
    private Integer totalChunks;
    private Integer processedChunks;
    private Integer progressPercent;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KnowledgeIngestionJobVO fromEntity(KnowledgeIngestionJob entity) {
        if (entity == null) {
            return null;
        }
        return KnowledgeIngestionJobVO.builder().id(entity.getId()).knowledgeBaseId(entity.getKnowledgeBaseId())
            .status(entity.getStatus()).totalChunks(entity.getTotalChunks())
            .processedChunks(entity.getProcessedChunks()).progressPercent(entity.getProgressPercent())
            .errorMessage(entity.getErrorMessage()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
            .build();
    }
}
