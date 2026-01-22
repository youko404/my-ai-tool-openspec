package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.dto.AiModelDTO;
import com.ai.content.repository.mysql.AiModelRepository;
import com.ai.content.service.AiModelService;
import com.ai.content.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementation of AI Model Service
 */
@Service
@RequiredArgsConstructor
public class AiModelServiceImpl implements AiModelService {

    private final AiModelRepository aiModelRepository;

    @Override
    @Transactional
    public AiModel create(AiModelDTO dto, String operator) {
        // Check for duplicate model name + provider combination
        if (aiModelRepository.existsByModelNameAndProviderAndIsDeletedFalse(dto.getModelName(), dto.getProvider())) {
            throw new IllegalArgumentException(
                "Model '" + dto.getModelName() + "' already exists for provider '" + dto.getProvider() + "'");
        }

        AiModel model = AiModel.builder().modelName(dto.getModelName()).provider(dto.getProvider())
            .isEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true).isDeleted(false).build();
        model.setCreatedBy(operator);
        model.setUpdatedBy(operator);

        return aiModelRepository.save(model);
    }

    @Override
    @Transactional
    public AiModel update(Long id, AiModelDTO dto, String operator) {
        AiModel model = getByIdOrThrow(id);

        // Check for duplicate if name or provider changed
        if ((dto.getModelName() != null && !dto.getModelName()
            .equals(model.getModelName())) || (dto.getProvider() != null && !dto.getProvider()
            .equals(model.getProvider()))) {

            String newName = dto.getModelName() != null ? dto.getModelName() : model.getModelName();
            String newProvider = dto.getProvider() != null ? dto.getProvider() : model.getProvider();

            if (aiModelRepository.existsByModelNameAndProviderExcludingId(newName, newProvider, id)) {
                throw new IllegalArgumentException(
                    "Model '" + newName + "' already exists for provider '" + newProvider + "'");
            }
        }

        if (StringUtils.hasText(dto.getModelName())) {
            model.setModelName(dto.getModelName());
        }
        if (StringUtils.hasText(dto.getProvider())) {
            model.setProvider(dto.getProvider());
        }
        if (dto.getIsEnabled() != null) {
            model.setIsEnabled(dto.getIsEnabled());
        }
        model.setUpdatedBy(operator);

        return aiModelRepository.save(model);
    }

    @Override
    @Transactional
    public void delete(Long id, String operator) {
        AiModel model = getByIdOrThrow(id);
        model.setIsDeleted(true);
        model.setUpdatedBy(operator);
        aiModelRepository.save(model);
    }

    @Override
    public AiModel getById(Long id) {
        return aiModelRepository.findByIdAndIsDeletedFalse(id).orElse(null);
    }

    @Override
    public List<AiModel> getAll() {
        return aiModelRepository.findByIsDeletedFalse();
    }

    @Override
    public PageResult<AiModel> getPage(int page, int pageSize) {
        Pageable pageable = createPageable(page, pageSize);
        Page<AiModel> result = aiModelRepository.findByIsDeletedFalse(pageable);
        return toPageResult(result, page, pageSize);
    }

    @Override
    public PageResult<AiModel> search(String keyword, int page, int pageSize) {
        Pageable pageable = createPageable(page, pageSize);
        Page<AiModel> result;

        if (StringUtils.hasText(keyword)) {
            result = aiModelRepository.searchByKeyword(keyword.trim(), pageable);
        } else {
            result = aiModelRepository.findByIsDeletedFalse(pageable);
        }

        return toPageResult(result, page, pageSize);
    }

    @Override
    public List<AiModel> getEnabledModels() {
        return aiModelRepository.findByIsEnabledTrueAndIsDeletedFalse();
    }

    @Override
    public List<AiModel> getByProvider(String provider) {
        return aiModelRepository.findByProviderAndIsDeletedFalse(provider);
    }

    @Override
    @Transactional
    public AiModel toggleEnabled(Long id, boolean enabled, String operator) {
        AiModel model = getByIdOrThrow(id);
        model.setIsEnabled(enabled);
        model.setUpdatedBy(operator);
        return aiModelRepository.save(model);
    }

    private AiModel getByIdOrThrow(Long id) {
        return aiModelRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Model not found with id: " + id));
    }

    private Pageable createPageable(int page, int pageSize) {
        // Convert 1-based page to 0-based
        int pageIndex = Math.max(0, page - 1);
        int size = Math.max(1, Math.min(pageSize, 100)); // Limit page size to 100
        return PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private PageResult<AiModel> toPageResult(Page<AiModel> page, int requestedPage, int pageSize) {
        return PageResult.of(page.getContent(), page.getTotalElements(), requestedPage, pageSize);
    }

}
