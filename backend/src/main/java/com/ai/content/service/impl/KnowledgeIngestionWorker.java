package com.ai.content.service.impl;

import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import com.ai.content.domain.entity.KnowledgeIngestionStatus;
import com.ai.content.repository.vector.KnowledgeIngestionJobRepository;
import com.ai.content.repository.KnowledgeVectorRepository;
import com.ai.content.service.OllamaEmbeddingModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Async ingestion worker
 */
@Component
@RequiredArgsConstructor
public class KnowledgeIngestionWorker {

    private static final int CHUNK_SIZE = 800;
    private static final int CHUNK_OVERLAP = 100;
    private static final int EMBEDDING_DIMENSION = 1024;
    private static final int FILE_PROGRESS_UPDATE_STEP = 5;

    private final KnowledgeIngestionJobRepository jobRepository;
    private final KnowledgeVectorRepository vectorRepository;
    private final OllamaEmbeddingModel embeddingModel;

    @Async("ingestionExecutor")
    public void processAsync(Long jobId, Long knowledgeBaseId, List<IngestionFile> files, String operator) {
        KnowledgeIngestionJob job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return;
        }
        try {
            job.setStatus(KnowledgeIngestionStatus.IN_PROGRESS);
            job.setUpdatedBy(operator);
            jobRepository.save(job);

            List<FileChunks> chunkPlan = new ArrayList<>();
            int totalChunks = 0;
            for (IngestionFile file : files) {
                String text = extractText(file);
                List<String> chunks = splitToChunks(text);
                chunkPlan.add(new FileChunks(file, chunks));
                totalChunks += chunks.size();
            }
            job.setTotalChunks(totalChunks);
            job.setProcessedChunks(0);
            job.setProgressPercent(totalChunks == 0 ? 100 : 0);
            jobRepository.save(job);

            int processed = 0;
            for (FileChunks fileChunks : chunkPlan) {
                int fileProcessed = 0;
                int fileTotal = fileChunks.chunks.size();
                int initialProgress = fileTotal == 0 ? 100 : 0;
                String initialStatus = fileTotal == 0 ? KnowledgeIngestionStatus.COMPLETED.name()
                    : KnowledgeIngestionStatus.IN_PROGRESS.name();
                long fileId = vectorRepository.insertFile(knowledgeBaseId, jobId, fileChunks.file.fileName,
                    fileChunks.file.contentType, fileChunks.file.bytes.length, initialStatus, fileTotal, fileProcessed,
                    initialProgress);
                if (fileTotal == 0) {
                    continue;
                }
                int index = 0;
                try {
                    for (String chunk : fileChunks.chunks) {
                        List<Double> embedding = embeddingModel.embed(chunk);
                        if (embedding.size() != EMBEDDING_DIMENSION) {
                            throw new IllegalStateException("Embedding dimension mismatch");
                        }
                        vectorRepository.insertChunk(fileId, knowledgeBaseId, index, chunk, toVectorLiteral(embedding));
                        index++;
                        processed++;
                        fileProcessed++;
                        if (fileProcessed % FILE_PROGRESS_UPDATE_STEP == 0 || fileProcessed == fileTotal) {
                            vectorRepository.updateFileProgress(fileId, fileProcessed, fileTotal);
                        }
                        updateProgress(job, processed, totalChunks, operator);
                    }
                    vectorRepository.updateFileCompleted(fileId, fileTotal);
                } catch (Exception ex) {
                    vectorRepository.updateFileFailed(fileId, fileProcessed, fileTotal);
                    throw ex;
                }
            }

            job.setStatus(KnowledgeIngestionStatus.COMPLETED);
            job.setProgressPercent(100);
            job.setUpdatedBy(operator);
            jobRepository.save(job);
        } catch (Exception ex) {
            job.setStatus(KnowledgeIngestionStatus.FAILED);
            job.setErrorMessage(truncate(ex.getMessage(), 1000));
            job.setUpdatedBy(operator);
            jobRepository.save(job);
        }
    }

    private void updateProgress(KnowledgeIngestionJob job, int processed, int total, String operator) {
        job.setProcessedChunks(processed);
        if (total > 0) {
            int percent = (int)Math.round(processed * 100.0 / total);
            job.setProgressPercent(Math.min(percent, 100));
        } else {
            job.setProgressPercent(100);
        }
        job.setUpdatedBy(operator);
        jobRepository.save(job);
    }

    private String extractText(IngestionFile file) {
        String name = file.fileName == null ? "" : file.fileName.toLowerCase();
        if (name.endsWith(".docx")) {
            return extractDocxText(file.bytes);
        }
        return new String(file.bytes, StandardCharsets.UTF_8);
    }

    private String extractDocxText(byte[] data) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder builder = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.isBlank()) {
                    builder.append(text).append('\n');
                }
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse DOCX file");
        }
    }

    private List<String> splitToChunks(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        String trimmed = text.trim();
        int start = 0;
        while (start < trimmed.length()) {
            int end = Math.min(start + CHUNK_SIZE, trimmed.length());
            String chunk = trimmed.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end == trimmed.length()) {
                break;
            }
            start = Math.max(0, end - CHUNK_OVERLAP);
        }
        return chunks;
    }

    private String toVectorLiteral(List<Double> embedding) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding.get(i));
        }
        builder.append(']');
        return builder.toString();
    }

    private String truncate(String message, int maxLength) {
        if (message == null) {
            return null;
        }
        return message.length() <= maxLength ? message : message.substring(0, maxLength);
    }

    @Data
    @AllArgsConstructor
    public static class IngestionFile {
        private String fileName;
        private String contentType;
        private byte[] bytes;
    }

    @Data
    @AllArgsConstructor
    private static class FileChunks {
        private IngestionFile file;
        private List<String> chunks;
    }
}
