package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.Workflow;
import com.ai.content.dto.WorkflowDTO;
import com.ai.content.repository.mysql.WorkflowRepository;
import com.ai.content.service.WorkflowService;
import com.ai.content.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of Workflow Service
 */
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Override
    @Transactional
    public Workflow create(WorkflowDTO dto, String operator) {
        Workflow workflow = Workflow.builder().name(dto.getName()).description(dto.getDescription())
            .enabled(dto.getEnabled() != null ? dto.getEnabled() : true).deleted(false).creator(operator)
            .editor(operator).build();
        workflow.setCreatedBy(operator);
        workflow.setUpdatedBy(operator);

        return workflowRepository.save(workflow);
    }

    @Override
    @Transactional
    public Workflow update(Long id, WorkflowDTO dto, String operator) {
        Workflow workflow = getByIdOrThrow(id);

        if (StringUtils.hasText(dto.getName())) {
            workflow.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            workflow.setDescription(dto.getDescription());
        }
        if (dto.getEnabled() != null) {
            workflow.setEnabled(dto.getEnabled());
        }
        workflow.setEditor(operator);
        workflow.setUpdatedBy(operator);

        return workflowRepository.save(workflow);
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        Workflow workflow = getByIdOrThrow(id);
        workflow.setDeleted(true);
        workflow.setEditor(operator);
        workflow.setUpdatedBy(operator);
        workflowRepository.save(workflow);
    }

    @Override
    public Workflow getById(Long id) {
        return workflowRepository.findById(id).filter(w -> !w.getDeleted()).orElse(null);
    }

    @Override
    public PageResult<Workflow> getPage(int page, int pageSize, String keyword) {
        Pageable pageable = createPageable(page, pageSize);
        Page<Workflow> result;

        if (StringUtils.hasText(keyword)) {
            result = workflowRepository.findByKeyword(keyword.trim(), pageable);
        } else {
            result = workflowRepository.findByDeletedFalse(pageable);
        }

        return toPageResult(result, page, pageSize);
    }

    @Override
    @Transactional
    public Workflow copy(Long id, String operator) {
        Workflow original = getByIdOrThrow(id);

        Workflow copy = Workflow.builder().name(original.getName() + " (副本)").description(original.getDescription())
            .enabled(original.getEnabled()).deleted(false).creator(operator).editor(operator).build();
        copy.setCreatedBy(operator);
        copy.setUpdatedBy(operator);

        return workflowRepository.save(copy);
    }

    private Workflow getByIdOrThrow(Long id) {
        return workflowRepository.findById(id).filter(w -> !w.getDeleted())
            .orElseThrow(() -> new IllegalArgumentException("Workflow not found with id: " + id));
    }

    private Pageable createPageable(int page, int pageSize) {
        // Convert 1-based page to 0-based
        int pageIndex = Math.max(0, page - 1);
        int size = Math.max(1, Math.min(pageSize, 100)); // Limit page size to 100
        return PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PageResult<Workflow> toPageResult(Page<Workflow> page, int requestedPage, int pageSize) {
        return PageResult.of(page.getContent(), page.getTotalElements(), requestedPage, pageSize);
    }
}
