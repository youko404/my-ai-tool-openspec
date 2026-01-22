package com.ai.content.controller;

import com.ai.content.domain.entity.mysql.KnowledgeBase;
import com.ai.content.domain.entity.vector.KnowledgeIngestionFile;
import com.ai.content.domain.entity.vector.KnowledgeIngestionJob;
import com.ai.content.dto.KnowledgeBaseDTO;
import com.ai.content.dto.KnowledgeChunkSearchRequest;
import com.ai.content.service.KnowledgeBaseService;
import com.ai.content.service.KnowledgeIngestionService;
import com.ai.content.service.KnowledgeSearchService;
import com.ai.content.vo.KnowledgeBaseVO;
import com.ai.content.vo.KnowledgeChunkSearchVO;
import com.ai.content.vo.KnowledgeIngestionFileVO;
import com.ai.content.vo.KnowledgeIngestionJobVO;
import com.ai.content.vo.PageResult;
import com.ai.content.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Knowledge Base management
 */
@RestController
@RequestMapping("/knowledge-bases")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;
    private final KnowledgeIngestionService ingestionService;
    private final KnowledgeSearchService knowledgeSearchService;

    @PostMapping
    public Result<KnowledgeBaseVO> create(@Valid @RequestBody KnowledgeBaseDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.create(dto, operator);
        return Result.success(KnowledgeBaseVO.fromEntity(knowledgeBase));
    }

    @PutMapping("/{id}")
    public Result<KnowledgeBaseVO> update(@PathVariable Long id, @Valid @RequestBody KnowledgeBaseDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.update(id, dto, operator);
        return Result.success(KnowledgeBaseVO.fromEntity(knowledgeBase));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        knowledgeBaseService.delete(id, operator);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<KnowledgeBaseVO> getById(@PathVariable Long id) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.getById(id);
        if (knowledgeBase == null) {
            return Result.error(404, "Knowledge base not found");
        }
        return Result.success(KnowledgeBaseVO.fromEntity(knowledgeBase));
    }

    @GetMapping
    public Result<List<KnowledgeBaseVO>> getAll() {
        List<KnowledgeBaseVO> items =
            knowledgeBaseService.getAll().stream().map(KnowledgeBaseVO::fromEntity).collect(Collectors.toList());
        return Result.success(items);
    }

    @GetMapping("/page")
    public Result<PageResult<KnowledgeBaseVO>> getPage(@RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<KnowledgeBase> result = knowledgeBaseService.getPage(page, pageSize);
        List<KnowledgeBaseVO> items =
            result.getList().stream().map(KnowledgeBaseVO::fromEntity).collect(Collectors.toList());
        return Result.success(PageResult.of(items, result.getTotal(), result.getPage(), result.getPageSize()));
    }

    @PatchMapping("/{id}/toggle")
    public Result<KnowledgeBaseVO> toggleEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return Result.error(400, "Missing 'enabled' field in request body");
        }
        KnowledgeBase knowledgeBase = knowledgeBaseService.toggleEnabled(id, enabled, operator);
        return Result.success(KnowledgeBaseVO.fromEntity(knowledgeBase));
    }

    @PostMapping("/{id}/files")
    public Result<KnowledgeIngestionJobVO> uploadFiles(@PathVariable Long id,
        @RequestParam("files") List<MultipartFile> files,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        KnowledgeIngestionJob job = ingestionService.createJob(id, files, operator);
        return Result.success(KnowledgeIngestionJobVO.fromEntity(job));
    }

    @GetMapping("/{id}/jobs/{jobId}")
    public Result<KnowledgeIngestionJobVO> getJobStatus(@PathVariable Long id, @PathVariable Long jobId) {
        KnowledgeIngestionJob job = ingestionService.getJob(id, jobId);
        if (job == null) {
            return Result.error(404, "Ingestion job not found");
        }
        return Result.success(KnowledgeIngestionJobVO.fromEntity(job));
    }

    @GetMapping("/{id}/files")
    public Result<PageResult<KnowledgeIngestionFileVO>> getFileProgress(@PathVariable Long id,
        @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<KnowledgeIngestionFile> result = ingestionService.getFiles(id, page, pageSize);
        List<KnowledgeIngestionFileVO> items =
            result.getList().stream().map(KnowledgeIngestionFileVO::fromEntity).collect(Collectors.toList());
        return Result.success(PageResult.of(items, result.getTotal(), result.getPage(), result.getPageSize()));
    }

    @PostMapping("/{id}/search")
    public Result<List<KnowledgeChunkSearchVO>> searchChunks(@PathVariable Long id,
        @Valid @RequestBody KnowledgeChunkSearchRequest request) {
        List<KnowledgeChunkSearchVO> items =
            knowledgeSearchService.searchChunks(id, request.getQueryText(), request.getMinScore(), request.getLimit())
                .stream().map(KnowledgeChunkSearchVO::fromEntity).collect(Collectors.toList());
        return Result.success(items);
    }
}
