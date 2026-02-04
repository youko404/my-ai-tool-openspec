package com.ai.content.controller;

import com.ai.content.domain.entity.mysql.Workflow;
import com.ai.content.dto.WorkflowDTO;
import com.ai.content.service.WorkflowService;
import com.ai.content.vo.PageResult;
import com.ai.content.vo.Result;
import com.ai.content.vo.WorkflowVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Workflow management
 */
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * Create a new workflow
     */
    @PostMapping
    public Result<WorkflowVO> create(@Valid @RequestBody WorkflowDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        Workflow workflow = workflowService.create(dto, operator);
        return Result.success(WorkflowVO.fromEntity(workflow));
    }

    /**
     * Update an existing workflow
     */
    @PutMapping("/{id}")
    public Result<WorkflowVO> update(@PathVariable Long id, @Valid @RequestBody WorkflowDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        Workflow workflow = workflowService.update(id, dto, operator);
        return Result.success(WorkflowVO.fromEntity(workflow));
    }

    /**
     * Delete a workflow (soft delete)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        workflowService.delete(id, operator);
        return Result.success();
    }

    /**
     * Get workflow by ID
     */
    @GetMapping("/{id}")
    public Result<WorkflowVO> getById(@PathVariable Long id) {
        Workflow workflow = workflowService.getById(id);
        if (workflow == null) {
            return Result.error(404, "Workflow not found");
        }
        return Result.success(WorkflowVO.fromEntity(workflow));
    }

    /**
     * Get workflows with pagination and optional keyword search
     */
    @GetMapping
    public Result<PageResult<WorkflowVO>> getPage(@RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false) String keyword) {
        PageResult<Workflow> result = workflowService.getPage(page, pageSize, keyword);
        return Result.success(toVoPageResult(result));
    }

    /**
     * Copy an existing workflow
     */
    @PostMapping("/{id}/copy")
    public Result<WorkflowVO> copy(@PathVariable Long id,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        Workflow workflow = workflowService.copy(id, operator);
        return Result.success(WorkflowVO.fromEntity(workflow));
    }

    private PageResult<WorkflowVO> toVoPageResult(PageResult<Workflow> result) {
        List<WorkflowVO> voList = result.getList().stream().map(WorkflowVO::fromEntity).collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), result.getPage(), result.getPageSize());
    }
}
