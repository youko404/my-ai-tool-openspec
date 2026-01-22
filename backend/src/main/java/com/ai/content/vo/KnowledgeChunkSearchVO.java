package com.ai.content.vo;

import com.ai.content.domain.entity.vector.KnowledgeChunkSearchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object for knowledge chunk search result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunkSearchVO {

    private String fileName;
    private Integer chunkIndex;
    private String content;
    private Double score;

    public static KnowledgeChunkSearchVO fromEntity(KnowledgeChunkSearchResult entity) {
        if (entity == null) {
            return null;
        }
        return KnowledgeChunkSearchVO.builder().fileName(entity.getFileName()).chunkIndex(entity.getChunkIndex())
            .content(entity.getContent()).score(entity.getScore()).build();
    }
}
