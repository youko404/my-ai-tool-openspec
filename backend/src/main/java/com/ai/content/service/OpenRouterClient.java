package com.ai.content.service;

import com.ai.content.dto.OpenRouterChatRequest;
import com.ai.content.dto.OpenRouterChatResponse;
import com.ai.content.exception.ProviderRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenRouter chat completion client
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenRouterClient {

    private final RestClient openRouterRestClient;

    public OpenRouterChatResponse chatCompletion(OpenRouterChatRequest request, String apiKey, String apiBaseUrl) {
        OpenRouterChatRequest prepared = prepareRequest(request, false);
        String resolvedApiKey = resolveApiKey(apiKey);
        String endpoint = resolveEndpoint(apiBaseUrl);
        log.debug("OpenRouter chatCompletion request: model={}, stream={}, messages={}", prepared.getModel(),
            prepared.getStream(), prepared.getMessages().size());
        try {
            OpenRouterChatResponse response =
                openRouterRestClient.post().uri(endpoint).header(HttpHeaders.AUTHORIZATION, "Bearer " + resolvedApiKey)
                    .body(prepared).retrieve().body(OpenRouterChatResponse.class);
            if (response == null) {
                throw new IllegalStateException("OpenRouter response is empty");
            }
            log.debug("OpenRouter chatCompletion success: id={}, model={}", response.getId(), response.getModel());
            return response;
        } catch (RestClientResponseException ex) {
            String responseBody = ex.getResponseBodyAsString();
            log.warn("OpenRouter request failed: status={}, body={}", ex.getRawStatusCode(), responseBody);
            throw new ProviderRequestException("OpenRouter request failed: HTTP " + ex.getRawStatusCode(),
                ex.getRawStatusCode(), responseBody, ex);
        } catch (ResourceAccessException ex) {
            log.warn("OpenRouter request failed: {}", ex.getMessage());
            throw new ProviderRequestException("OpenRouter request failed: network error", null, null, ex);
        }
    }

    public List<String> chatCompletionStream(OpenRouterChatRequest request, String apiKey, String apiBaseUrl) {
        OpenRouterChatRequest prepared = prepareRequest(request, true);
        String resolvedApiKey = resolveApiKey(apiKey);
        String endpoint = resolveEndpoint(apiBaseUrl);
        log.debug("OpenRouter chatCompletionStream request: model={}, messages={}", prepared.getModel(),
            prepared.getMessages().size());
        try {
            return openRouterRestClient.post().uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + resolvedApiKey).body(prepared)
                .exchange((req, response) -> {
                    if (response.getStatusCode().isError()) {
                        throw new ProviderRequestException(
                            "OpenRouter request failed: HTTP " + response.getStatusCode().value(),
                            response.getStatusCode().value(), null, null);
                    }
                    List<String> chunks = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.startsWith("data:")) {
                                continue;
                            }
                            String data = line.substring(5).trim();
                            if (data.isEmpty()) {
                                continue;
                            }
                            if ("[DONE]".equals(data)) {
                                break;
                            }
                            chunks.add(data);
                        }
                    } catch (IOException ex) {
                        throw new ProviderRequestException("OpenRouter request failed: response read error", null, null,
                            ex);
                    }
                    return chunks;
                });
        } catch (RestClientResponseException ex) {
            String responseBody = ex.getResponseBodyAsString();
            log.warn("OpenRouter stream request failed: status={}, body={}", ex.getRawStatusCode(), responseBody);
            throw new ProviderRequestException("OpenRouter request failed: HTTP " + ex.getRawStatusCode(),
                ex.getRawStatusCode(), responseBody, ex);
        } catch (ResourceAccessException ex) {
            log.warn("OpenRouter stream request failed: {}", ex.getMessage());
            throw new ProviderRequestException("OpenRouter request failed: network error", null, null, ex);
        }
    }

    private OpenRouterChatRequest prepareRequest(OpenRouterChatRequest request, boolean stream) {
        if (request == null) {
            throw new IllegalArgumentException("OpenRouter request cannot be null");
        }
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new IllegalArgumentException("OpenRouter messages cannot be empty");
        }
        String model = request.getModel();
        if (!StringUtils.hasText(model)) {
            throw new IllegalArgumentException("OpenRouter model is required");
        }
        request.setStream(stream);
        return request;
    }

    private String resolveApiKey(String apiKey) {
        if (StringUtils.hasText(apiKey)) {
            return apiKey;
        }
        throw new IllegalArgumentException("OpenRouter API key is required");
    }

    private String resolveEndpoint(String apiBaseUrl) {
        if (!StringUtils.hasText(apiBaseUrl)) {
            throw new IllegalArgumentException("OpenRouter base URL is required");
        }
        String trimmed = apiBaseUrl.trim();
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + "/chat/completions";
    }
}
