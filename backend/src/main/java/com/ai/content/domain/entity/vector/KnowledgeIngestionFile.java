package com.ai.content.domain.entity.vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Knowledge ingestion file progress
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeIngestionFile {

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
}
