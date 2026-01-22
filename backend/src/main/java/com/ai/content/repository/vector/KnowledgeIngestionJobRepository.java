package com.ai.content.repository.vector;

import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for KnowledgeIngestionJob entity
 */
@Repository
public interface KnowledgeIngestionJobRepository extends JpaRepository<KnowledgeIngestionJob, Long> {

    Optional<KnowledgeIngestionJob> findByIdAndKnowledgeBaseId(Long id, Long knowledgeBaseId);

    List<KnowledgeIngestionJob> findTop10ByKnowledgeBaseIdOrderByCreatedAtDesc(Long knowledgeBaseId);
}
