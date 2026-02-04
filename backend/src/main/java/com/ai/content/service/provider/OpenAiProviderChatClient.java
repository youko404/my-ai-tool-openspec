package com.ai.content.service.provider;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.service.ApiKeyCipher;
import com.ai.content.service.ProviderConfigService;
import org.springframework.stereotype.Service;

@Service
public class OpenAiProviderChatClient extends AbstractProviderChatClient {

    public OpenAiProviderChatClient(ProviderConfigService providerConfigService, ApiKeyCipher apiKeyCipher) {
        super(providerConfigService, apiKeyCipher);
    }

    @Override
    public ModelProvider getProvider() {
        return ModelProvider.OPENAI;
    }

    @Override
    public ChatResponseDTO chat(AiModel model, ChatRequestDTO request) {
        throw new UnsupportedOperationException("Provider not implemented: OPENAI");
    }
}
