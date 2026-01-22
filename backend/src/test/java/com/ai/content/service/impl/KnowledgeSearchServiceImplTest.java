package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.domain.entity.vector.KnowledgeChunkSearchResult;
import com.ai.content.repository.KnowledgeVectorRepository;
import com.ai.content.repository.mysql.KnowledgeBaseRepository;
import com.ai.content.service.OllamaEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeSearchServiceImplTest {

    @Mock
    private KnowledgeBaseRepository knowledgeBaseRepository;

    @Mock
    private KnowledgeVectorRepository vectorRepository;

    @Mock
    private OllamaEmbeddingModel embeddingModel;

    @InjectMocks
    private KnowledgeSearchServiceImpl service;

    @Test
    void searchChunksReturnsResults() {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setId(1L);
        when(knowledgeBaseRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(knowledgeBase));

        List<Double> embedding = IntStream.range(0, 1024).mapToObj(i -> 0.1d).toList();
        when(embeddingModel.embed("hello")).thenReturn(embedding);

        List<KnowledgeChunkSearchResult> expected = List.of(
            KnowledgeChunkSearchResult.builder().fileName("file.txt").chunkIndex(3).content("chunk content").score(0.9d)
                .build());
        when(vectorRepository.searchChunks(eq(1L), anyString(), eq(0.8d), eq(3))).thenReturn(expected);

        List<KnowledgeChunkSearchResult> result = service.searchChunks(1L, "hello", 0.8d, 3);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void searchChunksThrowsWhenKnowledgeBaseMissing() {
        when(knowledgeBaseRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.searchChunks(2L, "hello", 0.8d, 3)).isInstanceOf(
            IllegalArgumentException.class).hasMessage("Knowledge base not found");
    }
}
