package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO {

    private Long id;
    private String role;
    private String content;
    private String status;
    private String errorMessage;
    private String model;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private LocalDateTime createdAt;

    public static ChatMessageVO fromEntity(ChatMessage entity) {
        if (entity == null) {
            return null;
        }
        return ChatMessageVO.builder().id(entity.getId())
            .role(entity.getRole() != null ? entity.getRole().getValue() : null).content(entity.getContent())
            .status(entity.getStatus() != null ? entity.getStatus().getValue() : null)
            .errorMessage(entity.getErrorMessage()).model(entity.getModel())
            .promptTokens(entity.getPromptTokens() != null ? entity.getPromptTokens() : 0)
            .completionTokens(entity.getCompletionTokens() != null ? entity.getCompletionTokens() : 0)
            .totalTokens(entity.getTotalTokens() != null ? entity.getTotalTokens() : 0).createdAt(entity.getCreatedAt())
            .build();
    }
}
