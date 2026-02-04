package com.ai.content.service.provider;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.entity.mysql.AiProviderConfig;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.dto.OpenRouterChatRequest;
import com.ai.content.dto.OpenRouterChatResponse;
import com.ai.content.service.ApiKeyCipher;
import com.ai.content.service.OpenRouterClient;
import com.ai.content.service.ProviderConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenRouterProviderChatClientTest {

    @Mock
    private ProviderConfigService providerConfigService;

    @Mock
    private ApiKeyCipher apiKeyCipher;

    @Mock
    private OpenRouterClient openRouterClient;

    @InjectMocks
    private OpenRouterProviderChatClient providerChatClient;

    @Test
    void chatUsesProviderConfigAndNormalizesBaseUrl() {
        AiModel model = new AiModel();
        model.setId(1L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);

        AiProviderConfig config = new AiProviderConfig();
        config.setProvider(ModelProvider.OPENROUTER);
        config.setApiBaseUrl("https://openrouter.ai/api/v1/");
        config.setApiKey("encrypted");
        when(providerConfigService.getByProvider(ModelProvider.OPENROUTER)).thenReturn(config);
        when(apiKeyCipher.decrypt("encrypted")).thenReturn("provider-key");

        OpenRouterChatResponse response = buildResponse("hello");
        when(openRouterClient.chatCompletion(any(OpenRouterChatRequest.class), eq("provider-key"),
            eq("https://openrouter.ai/api/v1"))).thenReturn(response);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        ChatResponseDTO result = providerChatClient.chat(model, request);

        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getModel()).isEqualTo("openai/gpt-4o-mini");
        assertThat(result.getUsage()).isNotNull();
        assertThat(result.getUsage().getTotalTokens()).isEqualTo(3);

        ArgumentCaptor<OpenRouterChatRequest> captor = ArgumentCaptor.forClass(OpenRouterChatRequest.class);
        verify(openRouterClient).chatCompletion(captor.capture(), eq("provider-key"),
            eq("https://openrouter.ai/api/v1"));
        assertThat(captor.getValue().getModel()).isEqualTo("openai/gpt-4o-mini");
    }

    @Test
    void chatThrowsWhenProviderConfigMissing() {
        AiModel model = new AiModel();
        model.setId(2L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);

        ChatRequestDTO request = ChatRequestDTO.builder()
            .messages(List.of(ChatRequestDTO.Message.builder().role("user").content("hi").build())).build();

        assertThatThrownBy(() -> providerChatClient.chat(model, request)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Provider config not found for OPENROUTER");
    }

    private OpenRouterChatResponse buildResponse(String content) {
        OpenRouterChatResponse response = new OpenRouterChatResponse();
        response.setModel("openai/gpt-4o-mini");
        OpenRouterChatResponse.Message message = new OpenRouterChatResponse.Message();
        message.setRole("assistant");
        message.setContent(content);
        OpenRouterChatResponse.Choice choice = new OpenRouterChatResponse.Choice();
        choice.setMessage(message);
        response.setChoices(List.of(choice));
        OpenRouterChatResponse.Usage usage = new OpenRouterChatResponse.Usage();
        usage.setPromptTokens(1);
        usage.setCompletionTokens(2);
        usage.setTotalTokens(3);
        response.setUsage(usage);
        return response;
    }
}
