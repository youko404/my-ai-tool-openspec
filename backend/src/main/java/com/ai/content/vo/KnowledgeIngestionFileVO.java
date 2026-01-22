package com.ai.content.vo;

import com.ai.content.domain.entity.vector.KnowledgeIngestionFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View object for ingestion file progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeIngestionFileVO {

    private Long id;
    private Long knowledgeBaseId;
    private Long jobId;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String status;
    private Integer totalChunks;
    private Integer processedChunks;
    private Integer progressPercent;
    private LocalDateTime createdAt;

    public static KnowledgeIngestionFileVO fromEntity(KnowledgeIngestionFile entity) {
        if (entity == null) {
            return null;
        }
        return KnowledgeIngestionFileVO.builder().id(entity.getId()).knowledgeBaseId(entity.getKnowledgeBaseId())
            .jobId(entity.getJobId()).fileName(entity.getFileName()).contentType(entity.getContentType())
            .sizeBytes(entity.getSizeBytes()).status(entity.getStatus()).totalChunks(entity.getTotalChunks())
            .processedChunks(entity.getProcessedChunks()).progressPercent(entity.getProgressPercent())
            .createdAt(entity.getCreatedAt()).build();
    }
}
