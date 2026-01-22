package com.ai.content.domain.entity.vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Knowledge chunk search result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunkSearchResult {

    private String fileName;
    private Integer chunkIndex;
    private String content;
    private Double score;
}
