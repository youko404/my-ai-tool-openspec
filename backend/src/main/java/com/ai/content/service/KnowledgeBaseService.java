package com.ai.content.service;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.dto.KnowledgeBaseDTO;
import com.ai.content.vo.PageResult;

import java.util.List;

/**
 * Service interface for Knowledge Base management
 */
public interface KnowledgeBaseService {

    KnowledgeBase create(KnowledgeBaseDTO dto, String operator);

    KnowledgeBase update(Long id, KnowledgeBaseDTO dto, String operator);

    void delete(Long id, String operator);

    KnowledgeBase getById(Long id);

    List<KnowledgeBase> getAll();

    PageResult<KnowledgeBase> getPage(int page, int pageSize);

    KnowledgeBase toggleEnabled(Long id, boolean enabled, String operator);
}
