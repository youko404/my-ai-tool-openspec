package com.ai.content.service;

import com.ai.content.dto.OpenRouterChatRequest;
import com.ai.content.dto.OpenRouterChatResponse;
import com.ai.content.exception.ProviderRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenRouterClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private OpenRouterClient client;

    @BeforeEach
    void setUp() {
        client = new OpenRouterClient(restClient);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(OpenRouterChatRequest.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void chatCompletionReturnsResponse() {
        OpenRouterChatResponse response = new OpenRouterChatResponse();
        response.setId("resp-1");
        response.setModel("openai/gpt-4o-mini");
        when(responseSpec.body(OpenRouterChatResponse.class)).thenReturn(response);

        OpenRouterChatRequest request = OpenRouterChatRequest.builder().model("openai/gpt-4o-mini")
            .messages(List.of(OpenRouterChatRequest.Message.builder().role("user").content("hello").build())).build();

        ArgumentCaptor<OpenRouterChatRequest> captor = ArgumentCaptor.forClass(OpenRouterChatRequest.class);
        when(requestBodySpec.body(captor.capture())).thenReturn(requestBodySpec);

        OpenRouterChatResponse result = client.chatCompletion(request, "test-key", "https://openrouter.ai/api/v1");

        assertThat(result).isEqualTo(response);
        OpenRouterChatRequest sent = captor.getValue();
        assertThat(sent.getModel()).isEqualTo("openai/gpt-4o-mini");
        assertThat(sent.getStream()).isFalse();
        verify(requestBodyUriSpec).uri("https://openrouter.ai/api/v1/chat/completions");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 500})
    void chatCompletionThrowsOnHttpError(int statusCode) {
        byte[] responseBody = "rate limit".getBytes(StandardCharsets.UTF_8);
        RestClientResponseException ex =
            new RestClientResponseException("Error", statusCode, "Error", HttpHeaders.EMPTY, responseBody,
                StandardCharsets.UTF_8);
        when(responseSpec.body(OpenRouterChatResponse.class)).thenThrow(ex);

        OpenRouterChatRequest request = OpenRouterChatRequest.builder().model("openai/gpt-4o-mini")
            .messages(List.of(OpenRouterChatRequest.Message.builder().role("user").content("hello").build())).build();

        assertThatThrownBy(
            () -> client.chatCompletion(request, "test-key", "https://openrouter.ai/api/v1")).isInstanceOf(
                ProviderRequestException.class).hasMessage("OpenRouter request failed: HTTP " + statusCode)
            .extracting("responseBody").isEqualTo("rate limit");
    }

    @Test
    void chatCompletionThrowsOnMissingApiKey() {
        OpenRouterChatRequest request = OpenRouterChatRequest.builder().model("openai/gpt-4o-mini")
            .messages(List.of(OpenRouterChatRequest.Message.builder().role("user").content("hello").build())).build();

        assertThatThrownBy(() -> client.chatCompletion(request, " ", "https://openrouter.ai/api/v1")).isInstanceOf(
            IllegalArgumentException.class).hasMessage("OpenRouter API key is required");
    }

    @Test
    void chatCompletionThrowsOnMissingBaseUrl() {
        OpenRouterChatRequest request = OpenRouterChatRequest.builder().model("openai/gpt-4o-mini")
            .messages(List.of(OpenRouterChatRequest.Message.builder().role("user").content("hello").build())).build();

        assertThatThrownBy(() -> client.chatCompletion(request, "test-key", " ")).isInstanceOf(
            IllegalArgumentException.class).hasMessage("OpenRouter base URL is required");
    }
}
