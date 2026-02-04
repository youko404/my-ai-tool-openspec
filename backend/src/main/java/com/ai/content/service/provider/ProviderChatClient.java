package com.ai.content.service.provider;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;

public interface ProviderChatClient {

    ModelProvider getProvider();

    ChatResponseDTO chat(AiModel model, ChatRequestDTO request);
}
