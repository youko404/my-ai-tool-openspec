package com.ai.content.service;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.dto.AiModelDTO;
import com.ai.content.vo.PageResult;

import java.util.List;

/**
 * Service interface for AI Model management
 */
public interface AiModelService {

    /**
     * Create a new AI model
     *
     * @param dto      Model data
     * @param operator Current operator username
     * @return Created model
     */
    AiModel create(AiModelDTO dto, String operator);

    /**
     * Update an existing AI model
     *
     * @param id       Model ID
     * @param dto      Updated model data
     * @param operator Current operator username
     * @return Updated model
     */
    AiModel update(Long id, AiModelDTO dto, String operator);

    /**
     * Soft delete an AI model
     *
     * @param id       Model ID
     * @param operator Current operator username
     */
    void delete(Long id, String operator);

    /**
     * Get model by ID
     *
     * @param id Model ID
     * @return Model entity or null if not found
     */
    AiModel getById(Long id);

    /**
     * Get all models (non-deleted)
     *
     * @return List of models
     */
    List<AiModel> getAll();

    /**
     * Get models with pagination
     *
     * @param page     Page number (1-based)
     * @param pageSize Page size
     * @return Paginated result
     */
    PageResult<AiModel> getPage(int page, int pageSize);

    /**
     * Search models by keyword with pagination
     *
     * @param keyword  Search keyword (matches name or provider)
     * @param page     Page number (1-based)
     * @param pageSize Page size
     * @return Paginated result
     */
    PageResult<AiModel> search(String keyword, int page, int pageSize);

    /**
     * Get all enabled models
     *
     * @return List of enabled models
     */
    List<AiModel> getEnabledModels();

    /**
     * Get models by provider
     *
     * @param provider Provider name
     * @return List of models from the provider
     */
    List<AiModel> getByProvider(String provider);

    /**
     * Toggle model enabled status
     *
     * @param id       Model ID
     * @param enabled  New enabled status
     * @param operator Current operator username
     * @return Updated model
     */
    AiModel toggleEnabled(Long id, boolean enabled, String operator);

}
