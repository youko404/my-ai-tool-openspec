package com.ai.content.controller;

import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ProviderConfigDTO;
import com.ai.content.service.ProviderConfigService;
import com.ai.content.vo.ProviderConfigVO;
import com.ai.content.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for provider configuration management
 */
@RestController
@RequestMapping("/provider-configs")
@RequiredArgsConstructor
public class ProviderConfigController {

    private final ProviderConfigService providerConfigService;

    @GetMapping("/{provider}")
    public Result<ProviderConfigVO> getByProvider(@PathVariable String provider) {
        ModelProvider modelProvider = ModelProvider.fromValue(provider);
        AiProviderConfig config = providerConfigService.getByProvider(modelProvider);
        return Result.success(ProviderConfigVO.fromEntity(config, modelProvider));
    }

    @PostMapping
    public Result<ProviderConfigVO> save(@Valid @RequestBody ProviderConfigDTO dto,
        @RequestHeader(value = "X-User-Id", defaultValue = "system") String operator) {
        AiProviderConfig saved = providerConfigService.save(dto, operator);
        return Result.success(ProviderConfigVO.fromEntity(saved, saved.getProvider()));
    }
}
