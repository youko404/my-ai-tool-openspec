package com.ai.content.service.provider;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.dto.OpenRouterChatRequest;
import com.ai.content.dto.OpenRouterChatResponse;
import com.ai.content.service.ApiKeyCipher;
import com.ai.content.service.OpenRouterClient;
import com.ai.content.service.ProviderConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class OpenRouterProviderChatClient extends AbstractProviderChatClient {

    private final OpenRouterClient openRouterClient;

    public OpenRouterProviderChatClient(ProviderConfigService providerConfigService, ApiKeyCipher apiKeyCipher,
        OpenRouterClient openRouterClient) {
        super(providerConfigService, apiKeyCipher);
        this.openRouterClient = openRouterClient;
    }

    @Override
    public ModelProvider getProvider() {
        return ModelProvider.OPENROUTER;
    }

    @Override
    public ChatResponseDTO chat(AiModel model, ChatRequestDTO request) {
        OpenRouterChatRequest openRouterRequest = OpenRouterChatRequest.builder().model(model.getModelName())
            .messages(toOpenRouterMessages(request.getMessages())).temperature(request.getTemperature())
            .maxTokens(request.getMaxTokens()).build();

        ProviderCredentials credentials = resolveProviderCredentials();
        OpenRouterChatResponse response =
            openRouterClient.chatCompletion(openRouterRequest, credentials.getApiKey(), credentials.getApiBaseUrl());

        String content = null;
        if (response.getChoices() != null && !response.getChoices().isEmpty() && response.getChoices().get(0)
            .getMessage() != null) {
            content = response.getChoices().get(0).getMessage().getContent();
        }

        if (!StringUtils.hasText(content)) {
            log.warn("OpenRouter response missing content for model {}", model.getModelName());
            content = "";
        }

        ChatResponseDTO.Usage usage = null;
        if (response.getUsage() != null) {
            usage = ChatResponseDTO.Usage.builder().promptTokens(response.getUsage().getPromptTokens())
                .completionTokens(response.getUsage().getCompletionTokens())
                .totalTokens(response.getUsage().getTotalTokens()).build();
        }

        return ChatResponseDTO.builder().content(content)
            .model(StringUtils.hasText(response.getModel()) ? response.getModel() : model.getModelName()).usage(usage)
            .build();
    }

    private List<OpenRouterChatRequest.Message> toOpenRouterMessages(List<ChatRequestDTO.Message> messages) {
        return messages.stream().map(
            message -> OpenRouterChatRequest.Message.builder().role(message.getRole()).content(message.getContent())
                .build()).toList();
    }
}
