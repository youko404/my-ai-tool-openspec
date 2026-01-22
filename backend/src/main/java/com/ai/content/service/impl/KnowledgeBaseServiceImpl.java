package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.dto.KnowledgeBaseDTO;
import com.ai.content.repository.mysql.KnowledgeBaseRepository;
import com.ai.content.service.KnowledgeBaseService;
import com.ai.content.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of Knowledge Base service
 */
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    @Override
    public KnowledgeBase create(KnowledgeBaseDTO dto, String operator) {
        if (knowledgeBaseRepository.existsByNameAndIsDeletedFalse(dto.getName())) {
            throw new IllegalArgumentException("Knowledge base name already exists");
        }
        KnowledgeBase entity = KnowledgeBase.builder().name(dto.getName()).description(dto.getDescription())
            .isEnabled(dto.getIsEnabled() == null ? true : dto.getIsEnabled()).isDeleted(false).build();
        entity.setCreatedBy(operator);
        entity.setUpdatedBy(operator);
        return knowledgeBaseRepository.save(entity);
    }

    @Override
    public KnowledgeBase update(Long id, KnowledgeBaseDTO dto, String operator) {
        KnowledgeBase entity = knowledgeBaseRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
        if (knowledgeBaseRepository.existsByNameAndIdNotAndIsDeletedFalse(dto.getName(), id)) {
            throw new IllegalArgumentException("Knowledge base name already exists");
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        entity.setUpdatedBy(operator);
        return knowledgeBaseRepository.save(entity);
    }

    @Override
    public void delete(Long id, String operator) {
        KnowledgeBase entity = knowledgeBaseRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
        entity.setIsDeleted(true);
        entity.setUpdatedBy(operator);
        knowledgeBaseRepository.save(entity);
    }

    @Override
    public KnowledgeBase getById(Long id) {
        return knowledgeBaseRepository.findByIdAndIsDeletedFalse(id).orElse(null);
    }

    @Override
    public List<KnowledgeBase> getAll() {
        return knowledgeBaseRepository.findByIsDeletedFalse();
    }

    @Override
    public PageResult<KnowledgeBase> getPage(int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            return PageResult.empty();
        }
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<KnowledgeBase> result = knowledgeBaseRepository.findByIsDeletedFalse(pageable);
        return PageResult.of(result.getContent(), result.getTotalElements(), page, pageSize);
    }

    @Override
    public KnowledgeBase toggleEnabled(Long id, boolean enabled, String operator) {
        KnowledgeBase entity = knowledgeBaseRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
        entity.setIsEnabled(enabled);
        entity.setUpdatedBy(operator);
        return knowledgeBaseRepository.save(entity);
    }
}
