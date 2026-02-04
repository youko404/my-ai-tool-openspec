package com.ai.content.service;

import com.ai.content.domain.entity.mysql.Workflow;
import com.ai.content.dto.WorkflowDTO;
import com.ai.content.vo.PageResult;

/**
 * Service interface for Workflow management
 */
public interface WorkflowService {

    /**
     * Create a new workflow
     *
     * @param dto      Workflow data
     * @param operator Current operator username
     * @return Created workflow
     */
    Workflow create(WorkflowDTO dto, String operator);

    /**
     * Update an existing workflow
     *
     * @param id       Workflow ID
     * @param dto      Updated workflow data
     * @param operator Current operator username
     * @return Updated workflow
     */
    Workflow update(Long id, WorkflowDTO dto, String operator);

    /**
     * Soft delete a workflow
     *
     * @param id       Workflow ID
     * @param operator Current operator username
     */
    void delete(Long id, String operator);

    /**
     * Get workflow by ID
     *
     * @param id Workflow ID
     * @return Workflow entity or null if not found
     */
    Workflow getById(Long id);

    /**
     * Get workflows with pagination and optional keyword search
     *
     * @param page     Page number (1-based)
     * @param pageSize Page size
     * @param keyword  Optional search keyword (searches name and description)
     * @return Paginated result
     */
    PageResult<Workflow> getPage(int page, int pageSize, String keyword);

    /**
     * Copy an existing workflow
     *
     * @param id       Workflow ID to copy
     * @param operator Current operator username
     * @return New workflow copy
     */
    Workflow copy(Long id, String operator);
}
