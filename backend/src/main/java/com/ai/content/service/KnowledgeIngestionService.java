package com.ai.content.service;

import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import com.ai.content.domain.entity.vector.KnowledgeIngestionFile;
import com.ai.content.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Knowledge ingestion service
 */
public interface KnowledgeIngestionService {

    KnowledgeIngestionJob createJob(Long knowledgeBaseId, List<MultipartFile> files, String operator);

    KnowledgeIngestionJob getJob(Long knowledgeBaseId, Long jobId);

    PageResult<KnowledgeIngestionFile> getFiles(Long knowledgeBaseId, int page, int pageSize);
}
