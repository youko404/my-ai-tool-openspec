package com.ai.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageCreateDTO {

    private String content;

    private Double temperature;

    private Integer maxTokens;
}
