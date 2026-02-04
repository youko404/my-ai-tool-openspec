package com.ai.content.repository.mysql;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AiModel entity
 */
@Repository
public interface AiModelRepository extends JpaRepository<AiModel, Long> {

    /**
     * Find model by ID excluding deleted ones
     */
    Optional<AiModel> findByIdAndIsDeletedFalse(Long id);

    /**
     * Find all models excluding deleted ones
     */
    List<AiModel> findByIsDeletedFalse();

    /**
     * Find all models with pagination excluding deleted ones
     */
    Page<AiModel> findByIsDeletedFalse(Pageable pageable);

    /**
     * Find all enabled models excluding deleted ones
     */
    List<AiModel> findByIsEnabledTrueAndIsDeletedFalse();

    /**
     * Find models by provider excluding deleted ones
     */
    List<AiModel> findByProviderAndIsDeletedFalse(ModelProvider provider);

    /**
     * Find models by provider with pagination excluding deleted ones
     */
    Page<AiModel> findByProviderAndIsDeletedFalse(ModelProvider provider, Pageable pageable);

    /**
     * Search models by name or provider with pagination
     */
    @Query(
        "SELECT m FROM AiModel m WHERE m.isDeleted = false " + "AND (LOWER(m.modelName) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "OR LOWER(m.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<AiModel> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Check if model name exists for a provider (excluding deleted)
     */
    boolean existsByModelNameAndProviderAndIsDeletedFalse(String modelName, ModelProvider provider);

    /**
     * Check if model name exists for a provider excluding a specific ID (for update validation)
     */
    @Query(
        "SELECT COUNT(m) > 0 FROM AiModel m WHERE m.modelName = :modelName " + "AND m.provider = :provider AND m.id != :excludeId AND m.isDeleted = false")
    boolean existsByModelNameAndProviderExcludingId(@Param("modelName") String modelName,
        @Param("provider") ModelProvider provider, @Param("excludeId") Long excludeId);
}
