package com.ai.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationCreateDTO {

    private Long modelId;

    private String content;

    private Double temperature;

    private Integer maxTokens;
}
