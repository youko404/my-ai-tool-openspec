package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.domain.entity.vector.KnowledgeChunkSearchResult;
import com.ai.content.repository.KnowledgeVectorRepository;
import com.ai.content.repository.mysql.KnowledgeBaseRepository;
import com.ai.content.service.KnowledgeSearchService;
import com.ai.content.service.OllamaEmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of knowledge chunk search service
 */
@Service
@RequiredArgsConstructor
public class KnowledgeSearchServiceImpl implements KnowledgeSearchService {

    private static final int EMBEDDING_DIMENSION = 1024;

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeVectorRepository vectorRepository;
    private final OllamaEmbeddingModel embeddingModel;

    @Override
    public List<KnowledgeChunkSearchResult> searchChunks(Long knowledgeBaseId, String queryText, double minScore,
        int limit) {
        KnowledgeBase knowledgeBase = knowledgeBaseRepository.findByIdAndIsDeletedFalse(knowledgeBaseId)
            .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
        if (queryText == null || queryText.isBlank()) {
            throw new IllegalArgumentException("Query text cannot be empty");
        }
        List<Double> embedding = embeddingModel.embed(queryText);
        if (embedding.size() != EMBEDDING_DIMENSION) {
            throw new IllegalStateException("Embedding dimension mismatch");
        }
        String vectorLiteral = toVectorLiteral(embedding);
        return vectorRepository.searchChunks(knowledgeBase.getId(), vectorLiteral, minScore, limit);
    }

    private String toVectorLiteral(List<Double> embedding) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding.get(i));
        }
        builder.append(']');
        return builder.toString();
    }
}
