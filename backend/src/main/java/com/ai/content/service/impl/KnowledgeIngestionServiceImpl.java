package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import com.ai.content.domain.entity.vector.KnowledgeIngestionFile;
import com.ai.content.domain.entity.KnowledgeIngestionStatus;
import com.ai.content.repository.mysql.KnowledgeBaseRepository;
import com.ai.content.repository.vector.KnowledgeIngestionJobRepository;
import com.ai.content.repository.KnowledgeVectorRepository;
import com.ai.content.service.KnowledgeIngestionService;
import com.ai.content.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of knowledge ingestion service
 */
@Service
@RequiredArgsConstructor
public class KnowledgeIngestionServiceImpl implements KnowledgeIngestionService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeIngestionJobRepository jobRepository;
    private final KnowledgeIngestionWorker ingestionWorker;
    private final KnowledgeVectorRepository vectorRepository;

    @Override
    public KnowledgeIngestionJob createJob(Long knowledgeBaseId, List<MultipartFile> files, String operator) {
        KnowledgeBase knowledgeBase = knowledgeBaseRepository.findByIdAndIsDeletedFalse(knowledgeBaseId)
            .orElseThrow(() -> new IllegalArgumentException("Knowledge base not found"));
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("No files uploaded");
        }
        List<KnowledgeIngestionWorker.IngestionFile> ingestionFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            validateFile(file);
            try {
                ingestionFiles.add(
                    new KnowledgeIngestionWorker.IngestionFile(file.getOriginalFilename(), file.getContentType(),
                        file.getBytes()));
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read uploaded file");
            }
        }

        KnowledgeIngestionJob job = KnowledgeIngestionJob.builder().knowledgeBaseId(knowledgeBase.getId())
            .status(KnowledgeIngestionStatus.QUEUED).totalChunks(0).processedChunks(0).progressPercent(0).build();
        job.setCreatedBy(operator);
        job.setUpdatedBy(operator);
        KnowledgeIngestionJob savedJob = jobRepository.save(job);

        ingestionWorker.processAsync(savedJob.getId(), knowledgeBaseId, ingestionFiles, operator);
        return savedJob;
    }

    @Override
    public KnowledgeIngestionJob getJob(Long knowledgeBaseId, Long jobId) {
        return jobRepository.findByIdAndKnowledgeBaseId(jobId, knowledgeBaseId).orElse(null);
    }

    @Override
    public PageResult<KnowledgeIngestionFile> getFiles(Long knowledgeBaseId, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        int normalizedPageSize = pageSize > 0 ? pageSize : 10;
        int offset = (normalizedPage - 1) * normalizedPageSize;
        List<KnowledgeIngestionFile> files = vectorRepository.findFiles(knowledgeBaseId, normalizedPageSize, offset);
        long total = vectorRepository.countFiles(knowledgeBaseId);
        return PageResult.of(files, total, normalizedPage, normalizedPageSize);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Unsupported file type");
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!ext.equals("txt") && !ext.equals("md") && !ext.equals("docx")) {
            throw new IllegalArgumentException("Unsupported file type");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
    }
}
