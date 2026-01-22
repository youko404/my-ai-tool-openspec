package com.ai.content.service;

import com.ai.content.config.OllamaProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Ollama embedding model client
 */
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingModel {

    private final RestClient ollamaRestClient;
    private final OllamaProperties properties;

    public List<Double> embed(String input) {
        OllamaEmbeddingResponse response =
            ollamaRestClient.post().uri("/api/embeddings").body(Map.of("model", properties.getModel(), "prompt", input))
                .retrieve().body(OllamaEmbeddingResponse.class);
        if (response == null || response.getEmbedding() == null) {
            throw new IllegalStateException("Failed to get embedding from Ollama");
        }
        return response.getEmbedding();
    }

    @Data
    public static class OllamaEmbeddingResponse {
        private List<Double> embedding;
    }
}
