package com.ai.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRouter chat completion request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterChatRequest {

    private String model;

    private List<Message> messages;

    private Boolean stream;

    private Double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
