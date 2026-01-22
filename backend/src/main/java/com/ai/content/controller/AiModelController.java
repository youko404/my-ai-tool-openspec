package com.ai.content.controller;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.dto.AiModelDTO;
import com.ai.content.service.AiModelService;
import com.ai.content.vo.AiModelVO;
import com.ai.content.vo.PageResult;
import com.ai.content.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for AI Model management
 */
@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
public class AiModelController {

    private final AiModelService aiModelService;

    /**
     * Create a new AI model
     */
    @PostMapping
    public Result<AiModelVO> create(@Valid @RequestBody AiModelDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        AiModel model = aiModelService.create(dto, operator);
        return Result.success(AiModelVO.fromEntity(model));
    }

    /**
     * Update an existing AI model
     */
    @PutMapping("/{id}")
    public Result<AiModelVO> update(@PathVariable Long id, @Valid @RequestBody AiModelDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        AiModel model = aiModelService.update(id, dto, operator);
        return Result.success(AiModelVO.fromEntity(model));
    }

    /**
     * Delete an AI model (soft delete)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        aiModelService.delete(id, operator);
        return Result.success();
    }

    /**
     * Get model by ID
     */
    @GetMapping("/{id}")
    public Result<AiModelVO> getById(@PathVariable Long id) {
        AiModel model = aiModelService.getById(id);
        if (model == null) {
            return Result.error(404, "Model not found");
        }
        return Result.success(AiModelVO.fromEntity(model));
    }

    /**
     * Get all models
     */
    @GetMapping
    public Result<List<AiModelVO>> getAll() {
        List<AiModel> models = aiModelService.getAll();
        List<AiModelVO> voList = models.stream().map(AiModelVO::fromEntity).collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * Get models with pagination
     */
    @GetMapping("/page")
    public Result<PageResult<AiModelVO>> getPage(@RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<AiModel> result = aiModelService.getPage(page, pageSize);
        return Result.success(toVoPageResult(result));
    }

    /**
     * Search models by keyword with pagination
     */
    @GetMapping("/search")
    public Result<PageResult<AiModelVO>> search(@RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<AiModel> result = aiModelService.search(keyword, page, pageSize);
        return Result.success(toVoPageResult(result));
    }

    /**
     * Get all enabled models
     */
    @GetMapping("/enabled")
    public Result<List<AiModelVO>> getEnabledModels() {
        List<AiModel> models = aiModelService.getEnabledModels();
        List<AiModelVO> voList = models.stream().map(AiModelVO::fromEntity).collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * Get models by provider
     */
    @GetMapping("/provider/{provider}")
    public Result<List<AiModelVO>> getByProvider(@PathVariable String provider) {
        List<AiModel> models = aiModelService.getByProvider(provider);
        List<AiModelVO> voList = models.stream().map(AiModelVO::fromEntity).collect(Collectors.toList());
        return Result.success(voList);
    }

    /**
     * Toggle model enabled status
     */
    @PatchMapping("/{id}/toggle")
    public Result<AiModelVO> toggleEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return Result.error(400, "Missing 'enabled' field in request body");
        }
        AiModel model = aiModelService.toggleEnabled(id, enabled, operator);
        return Result.success(AiModelVO.fromEntity(model));
    }

    private PageResult<AiModelVO> toVoPageResult(PageResult<AiModel> result) {
        List<AiModelVO> voList = result.getList().stream().map(AiModelVO::fromEntity).collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), result.getPage(), result.getPageSize());
    }

}
