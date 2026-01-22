package com.ai.content.repository.mysql;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for KnowledgeBase entity
 */
@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

    Optional<KnowledgeBase> findByIdAndIsDeletedFalse(Long id);

    List<KnowledgeBase> findByIsDeletedFalse();

    Page<KnowledgeBase> findByIsDeletedFalse(Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);

    boolean existsByNameAndIdNotAndIsDeletedFalse(String name, Long id);
}
