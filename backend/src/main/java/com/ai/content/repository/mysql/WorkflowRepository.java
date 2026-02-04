package com.ai.content.repository.mysql;

import com.ai.content.domain.entity.mysql.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Workflow entity
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    /**
     * Find all workflows that are not deleted
     */
    Page<Workflow> findByDeletedFalse(Pageable pageable);

    /**
     * Find workflows by keyword search (name or description) excluding deleted
     */
    @Query(
        "SELECT w FROM Workflow w WHERE w.deleted = false AND (w.name LIKE %:keyword% OR w.description LIKE %:keyword%)")
    Page<Workflow> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
