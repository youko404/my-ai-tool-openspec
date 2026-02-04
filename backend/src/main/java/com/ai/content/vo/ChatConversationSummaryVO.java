package com.ai.content.vo;

import com.ai.content.domain.entity.mysql.ChatConversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationSummaryVO {

    private Long id;
    private String title;
    private Long modelId;
    private String modelName;
    private String lastMessagePreview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChatConversationSummaryVO fromEntity(ChatConversation entity) {
        if (entity == null) {
            return null;
        }
        return ChatConversationSummaryVO.builder().id(entity.getId()).title(entity.getTitle())
            .modelId(entity.getModelId()).modelName(entity.getModelName())
            .lastMessagePreview(entity.getLastMessagePreview()).createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt()).build();
    }
}
