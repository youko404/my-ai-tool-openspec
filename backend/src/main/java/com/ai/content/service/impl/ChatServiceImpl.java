package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.service.AiModelService;
import com.ai.content.service.ChatService;
import com.ai.content.service.provider.ProviderChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AiModelService aiModelService;
    private final Map<ModelProvider, ProviderChatClient> providerChatClients;

    @Override
    public ChatResponseDTO chat(Long modelId, ChatRequestDTO request) {
        if (modelId == null) {
            throw new IllegalArgumentException("Model id is required");
        }
        if (request == null || CollectionUtils.isEmpty(request.getMessages())) {
            throw new IllegalArgumentException("Chat messages cannot be empty");
        }
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("Model not found with id: " + modelId);
        }
        if (Boolean.FALSE.equals(model.getIsEnabled())) {
            throw new IllegalArgumentException("Model is disabled");
        }

        ModelProvider provider = model.getProvider();
        if (provider == null) {
            throw new IllegalArgumentException("Model provider is required");
        }

        ProviderChatClient providerChatClient = providerChatClients.get(provider);
        if (providerChatClient == null) {
            throw new UnsupportedOperationException("Provider not implemented: " + provider);
        }
        return providerChatClient.chat(model, request);
    }
}
