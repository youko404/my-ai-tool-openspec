package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.service.AiModelService;
import com.ai.content.service.provider.ProviderChatClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private AiModelService aiModelService;

    @Mock
    private ProviderChatClient openRouterProviderChatClient;

    @Mock
    private ProviderChatClient openAiProviderChatClient;

    private ChatServiceImpl chatService;

    private Map<ModelProvider, ProviderChatClient> providerChatClients;

    @BeforeEach
    void setUp() {
        providerChatClients = new EnumMap<>(ModelProvider.class);
        providerChatClients.put(ModelProvider.OPENROUTER, openRouterProviderChatClient);
        providerChatClients.put(ModelProvider.OPENAI, openAiProviderChatClient);
        chatService = new ChatServiceImpl(aiModelService, providerChatClients);
    }

    @Test
    void chatUsesProviderClientWhenPresent() {
        AiModel model = new AiModel();
        model.setId(1L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(1L)).thenReturn(model);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        ChatResponseDTO response = ChatResponseDTO.builder().content("hello").model("openai/gpt-4o-mini").build();
        when(openRouterProviderChatClient.chat(model, request)).thenReturn(response);

        ChatResponseDTO result = chatService.chat(1L, request);

        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getModel()).isEqualTo("openai/gpt-4o-mini");
        verify(openRouterProviderChatClient).chat(model, request);
    }

    @Test
    void chatThrowsWhenProviderClientErrors() {
        AiModel model = new AiModel();
        model.setId(2L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(2L)).thenReturn(model);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        when(openRouterProviderChatClient.chat(model, request)).thenThrow(
            new IllegalArgumentException("Provider config not found for OPENROUTER"));

        assertThatThrownBy(() -> chatService.chat(2L, request)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Provider config not found for OPENROUTER");
    }

    @Test
    void chatThrowsWhenModelDisabled() {
        AiModel model = new AiModel();
        model.setId(3L);
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(false);
        when(aiModelService.getById(3L)).thenReturn(model);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        assertThatThrownBy(() -> chatService.chat(3L, request)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Model is disabled");
    }

    @Test
    void chatThrowsWhenModelMissing() {
        when(aiModelService.getById(4L)).thenReturn(null);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        assertThatThrownBy(() -> chatService.chat(4L, request)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Model not found with id: 4");
    }

    @Test
    void chatThrowsWhenProviderNotImplemented() {
        AiModel model = new AiModel();
        model.setId(5L);
        model.setModelName("gpt-4o");
        model.setProvider(ModelProvider.OPENAI);
        model.setIsEnabled(true);
        when(aiModelService.getById(5L)).thenReturn(model);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        when(openAiProviderChatClient.chat(model, request)).thenThrow(
            new UnsupportedOperationException("Provider not implemented: OPENAI"));

        assertThatThrownBy(() -> chatService.chat(5L, request)).isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Provider not implemented: OPENAI");
    }
}
