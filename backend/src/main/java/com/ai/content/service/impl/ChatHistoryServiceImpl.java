package com.ai.content.service.impl;

import com.ai.content.domain.entity.mysql.AiModel;
import com.ai.content.domain.entity.mysql.ChatConversation;
import com.ai.content.domain.entity.mysql.ChatMessage;
import com.ai.content.domain.enums.ChatMessageRole;
import com.ai.content.domain.enums.ChatMessageStatus;
import com.ai.content.domain.enums.ModelProvider;
import com.ai.content.dto.ChatConversationCreateDTO;
import com.ai.content.dto.ChatMessageCreateDTO;
import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;
import com.ai.content.exception.ProviderRequestException;
import com.ai.content.repository.mysql.ChatConversationRepository;
import com.ai.content.repository.mysql.ChatMessageRepository;
import com.ai.content.service.AiModelService;
import com.ai.content.service.ChatHistoryService;
import com.ai.content.service.provider.ProviderChatClient;
import com.ai.content.vo.ChatConversationDetailVO;
import com.ai.content.vo.ChatConversationSummaryVO;
import com.ai.content.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private static final int MAX_TITLE_LENGTH = 20;
    private static final int MAX_PREVIEW_LENGTH = 50;
    private static final int MAX_MESSAGE_LENGTH = 5000;
    private static final int MAX_CONTEXT_MESSAGES = 10;
    private static final int MAX_CONTEXT_CHARS = 5000;

    private final AiModelService aiModelService;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final Map<ModelProvider, ProviderChatClient> providerChatClients;

    @Override
    public ChatConversationDetailVO createConversation(ChatConversationCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Chat request cannot be null");
        }
        Long modelId = dto.getModelId();
        String content = normalizeContent(dto.getContent());
        AiModel model = resolveModel(modelId);

        ChatConversation conversation =
            ChatConversation.builder().title(buildTitle(content)).modelId(model.getId()).modelName(model.getModelName())
                .build();
        conversation = chatConversationRepository.save(conversation);

        saveUserMessage(conversation.getId(), content);
        List<ChatRequestDTO.Message> contextMessages = buildContextMessages(conversation.getId());

        ChatResponseDTO response = null;
        String errorMessage = null;
        String providerResponse = null;
        try {
            response = sendToProvider(model, dto.getTemperature(), dto.getMaxTokens(), contextMessages);
        } catch (ProviderRequestException ex) {
            errorMessage = ex.getMessage();
            providerResponse = ex.getResponseBody();
        } catch (RuntimeException ex) {
            errorMessage = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "Chat request failed";
        }

        ChatMessage assistantMessage =
            buildAssistantMessage(conversation.getId(), response, errorMessage, providerResponse);
        chatMessageRepository.save(assistantMessage);
        updateConversationAfterReply(conversation, assistantMessage, response);
        return buildDetail(conversation);
    }

    @Override
    public ChatConversationDetailVO appendMessage(Long conversationId, ChatMessageCreateDTO dto) {
        if (conversationId == null) {
            throw new IllegalArgumentException("Conversation id is required");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Chat request cannot be null");
        }
        ChatConversation conversation = chatConversationRepository.findByIdAndIsDeletedFalse(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
        if (conversation.getModelId() == null) {
            throw new IllegalArgumentException("Conversation model is required");
        }
        String content = normalizeContent(dto.getContent());
        AiModel model = resolveModel(conversation.getModelId());

        saveUserMessage(conversation.getId(), content);
        List<ChatRequestDTO.Message> contextMessages = buildContextMessages(conversation.getId());

        ChatResponseDTO response = null;
        String errorMessage = null;
        String providerResponse = null;
        try {
            response = sendToProvider(model, dto.getTemperature(), dto.getMaxTokens(), contextMessages);
        } catch (ProviderRequestException ex) {
            errorMessage = ex.getMessage();
            providerResponse = ex.getResponseBody();
        } catch (RuntimeException ex) {
            errorMessage = StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "Chat request failed";
        }

        ChatMessage assistantMessage =
            buildAssistantMessage(conversation.getId(), response, errorMessage, providerResponse);
        chatMessageRepository.save(assistantMessage);
        updateConversationAfterReply(conversation, assistantMessage, response);
        return buildDetail(conversation);
    }

    @Override
    public List<ChatConversationSummaryVO> listConversations() {
        return chatConversationRepository.findByIsDeletedFalseOrderByUpdatedAtDesc().stream()
            .map(ChatConversationSummaryVO::fromEntity).toList();
    }

    @Override
    public ChatConversationDetailVO getConversation(Long conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("Conversation id is required");
        }
        ChatConversation conversation = chatConversationRepository.findByIdAndIsDeletedFalse(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
        return buildDetail(conversation);
    }

    @Override
    public void softDeleteConversation(Long conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("Conversation id is required");
        }
        ChatConversation conversation = chatConversationRepository.findByIdAndIsDeletedFalse(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found with id: " + conversationId));
        conversation.setIsDeleted(true);
        chatConversationRepository.save(conversation);
    }

    private AiModel resolveModel(Long modelId) {
        if (modelId == null) {
            throw new IllegalArgumentException("Model id is required");
        }
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("Model not found with id: " + modelId);
        }
        if (Boolean.FALSE.equals(model.getIsEnabled())) {
            throw new IllegalArgumentException("Model is disabled");
        }
        if (model.getProvider() == null) {
            throw new IllegalArgumentException("Model provider is required");
        }
        return model;
    }

    private ChatResponseDTO sendToProvider(AiModel model, Double temperature, Integer maxTokens,
        List<ChatRequestDTO.Message> messages) {
        ProviderChatClient providerChatClient = providerChatClients.get(model.getProvider());
        if (providerChatClient == null) {
            throw new IllegalArgumentException("Provider not implemented: " + model.getProvider());
        }
        ChatRequestDTO request =
            ChatRequestDTO.builder().messages(messages).temperature(temperature).maxTokens(maxTokens).build();
        return providerChatClient.chat(model, request);
    }

    private void saveUserMessage(Long conversationId, String content) {
        ChatMessage userMessage = ChatMessage.builder().conversationId(conversationId).role(ChatMessageRole.USER)
            .status(ChatMessageStatus.SUCCESS).content(content).promptTokens(0).completionTokens(0).totalTokens(0)
            .build();
        chatMessageRepository.save(userMessage);
    }

    private ChatMessage buildAssistantMessage(Long conversationId, ChatResponseDTO response, String errorMessage,
        String providerResponse) {
        int promptTokens = resolveUsageValue(response, usage -> usage.getPromptTokens());
        int completionTokens = resolveUsageValue(response, usage -> usage.getCompletionTokens());
        int totalTokens = resolveUsageValue(response, usage -> usage.getTotalTokens());
        if (StringUtils.hasText(errorMessage)) {
            return ChatMessage.builder().conversationId(conversationId).role(ChatMessageRole.ASSISTANT)
                .status(ChatMessageStatus.ERROR).content(errorMessage).errorMessage(errorMessage)
                .providerResponse(providerResponse).promptTokens(promptTokens).completionTokens(completionTokens)
                .totalTokens(totalTokens).build();
        }
        String content = response != null ? response.getContent() : "";
        return ChatMessage.builder().conversationId(conversationId).role(ChatMessageRole.ASSISTANT)
            .status(ChatMessageStatus.SUCCESS).content(content).model(response != null ? response.getModel() : null)
            .promptTokens(promptTokens).completionTokens(completionTokens).totalTokens(totalTokens).build();
    }

    private void updateConversationAfterReply(ChatConversation conversation, ChatMessage assistantMessage,
        ChatResponseDTO response) {
        if (assistantMessage == null) {
            return;
        }
        String previewSource = assistantMessage.getContent();
        conversation.setLastMessagePreview(buildPreview(previewSource));
        if (response != null && StringUtils.hasText(response.getModel())) {
            conversation.setModelName(response.getModel());
        }
        chatConversationRepository.save(conversation);
    }

    private ChatConversationDetailVO buildDetail(ChatConversation conversation) {
        List<ChatMessage> messages =
            chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        List<ChatMessageVO> messageVos =
            CollectionUtils.isEmpty(messages) ? List.of() : messages.stream().map(ChatMessageVO::fromEntity).toList();
        return ChatConversationDetailVO.fromEntity(conversation, messageVos);
    }

    private List<ChatRequestDTO.Message> buildContextMessages(Long conversationId) {
        List<ChatMessage> history = chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        if (CollectionUtils.isEmpty(history)) {
            return List.of();
        }
        List<ChatRequestDTO.Message> selected = new ArrayList<>();
        int totalChars = 0;
        int totalMessages = 0;
        for (int index = history.size() - 1; index >= 0; index--) {
            ChatMessage message = history.get(index);
            String content = message.getContent() != null ? message.getContent() : "";
            if (totalMessages >= MAX_CONTEXT_MESSAGES || totalChars + content.length() > MAX_CONTEXT_CHARS) {
                break;
            }
            selected.add(
                ChatRequestDTO.Message.builder().role(message.getRole() != null ? message.getRole().getValue() : null)
                    .content(content).build());
            totalMessages++;
            totalChars += content.length();
        }
        Collections.reverse(selected);
        return selected;
    }

    private String normalizeContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Chat content cannot be empty");
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Chat content exceeds 5000 characters");
        }
        return trimmed;
    }

    private String buildTitle(String content) {
        return truncate(content, MAX_TITLE_LENGTH, "新对话");
    }

    private String buildPreview(String content) {
        return truncate(content, MAX_PREVIEW_LENGTH, "");
    }

    private String truncate(String content, int maxLength, String fallback) {
        if (!StringUtils.hasText(content)) {
            return fallback;
        }
        String trimmed = content.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength) + "...";
    }

    private int resolveUsageValue(ChatResponseDTO response, Function<ChatResponseDTO.Usage, Integer> extractor) {
        if (response == null || response.getUsage() == null) {
            return 0;
        }
        Integer value = extractor.apply(response.getUsage());
        return value != null ? value : 0;
    }
}
