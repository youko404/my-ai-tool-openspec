package com.ai.content.service;

import com.ai.content.domain.entity.vector.KnowledgeChunkSearchResult;

import java.util.List;

/**
 * Service for knowledge chunk search
 */
public interface KnowledgeSearchService {

    List<KnowledgeChunkSearchResult> searchChunks(Long knowledgeBaseId, String queryText, double minScore, int limit);
}
