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
import com.ai.content.service.provider.ProviderChatClient;
import com.ai.content.vo.ChatConversationDetailVO;
import com.ai.content.vo.ChatConversationSummaryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatHistoryServiceImplTest {

    @Mock
    private AiModelService aiModelService;

    @Mock
    private ChatConversationRepository chatConversationRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ProviderChatClient providerChatClient;

    private ChatHistoryServiceImpl chatHistoryService;

    @BeforeEach
    void setUp() {
        Map<ModelProvider, ProviderChatClient> providerChatClients = new EnumMap<>(ModelProvider.class);
        providerChatClients.put(ModelProvider.OPENROUTER, providerChatClient);
        chatHistoryService =
            new ChatHistoryServiceImpl(aiModelService, chatConversationRepository, chatMessageRepository,
                providerChatClients);
    }

    @Test
    void createConversationStoresMessagesOnSuccess() {
        AiModel model = new AiModel();
        model.setId(1L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(1L)).thenReturn(model);

        when(chatConversationRepository.save(org.mockito.ArgumentMatchers.any(ChatConversation.class))).thenAnswer(
            invocation -> {
                ChatConversation conversation = invocation.getArgument(0);
                if (conversation.getId() == null) {
                    conversation.setId(10L);
                }
                return conversation;
            });

        when(chatMessageRepository.save(org.mockito.ArgumentMatchers.any(ChatMessage.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        ChatResponseDTO response = ChatResponseDTO.builder().content("hello").model("openai/gpt-4o-mini")
            .usage(ChatResponseDTO.Usage.builder().promptTokens(3).completionTokens(4).totalTokens(7).build()).build();
        when(providerChatClient.chat(org.mockito.ArgumentMatchers.eq(model),
            org.mockito.ArgumentMatchers.any())).thenReturn(response);

        when(chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(10L)).thenReturn(
            List.of(buildMessage(1L, 10L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "hi", null, null)),
            List.of(buildMessage(1L, 10L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "hi", null, null),
                buildMessage(2L, 10L, ChatMessageRole.ASSISTANT, ChatMessageStatus.SUCCESS, "hello", null,
                    "openai/gpt-4o-mini", null, 3, 4, 7)));

        ChatConversationCreateDTO dto = ChatConversationCreateDTO.builder().modelId(1L).content("hi").build();

        ChatConversationDetailVO result = chatHistoryService.createConversation(dto);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getMessages()).hasSize(2);
        assertThat(result.getLastMessagePreview()).isEqualTo("hello");

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository, times(2)).save(messageCaptor.capture());
        List<ChatMessage> savedMessages = messageCaptor.getAllValues();
        assertThat(savedMessages.get(0).getRole()).isEqualTo(ChatMessageRole.USER);
        assertThat(savedMessages.get(1).getStatus()).isEqualTo(ChatMessageStatus.SUCCESS);
        assertThat(savedMessages.get(1).getProviderResponse()).isNull();
        assertThat(savedMessages.get(1).getPromptTokens()).isEqualTo(3);
        assertThat(savedMessages.get(1).getCompletionTokens()).isEqualTo(4);
        assertThat(savedMessages.get(1).getTotalTokens()).isEqualTo(7);

        ArgumentCaptor<ChatRequestDTO> requestCaptor = ArgumentCaptor.forClass(ChatRequestDTO.class);
        verify(providerChatClient).chat(org.mockito.ArgumentMatchers.eq(model), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getMessages()).hasSize(1);
        assertThat(requestCaptor.getValue().getMessages().get(0).getRole()).isEqualTo("user");
        assertThat(requestCaptor.getValue().getMessages().get(0).getContent()).isEqualTo("hi");
    }

    @Test
    void createConversationStoresProviderError() {
        AiModel model = new AiModel();
        model.setId(2L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(2L)).thenReturn(model);

        when(chatConversationRepository.save(org.mockito.ArgumentMatchers.any(ChatConversation.class))).thenAnswer(
            invocation -> {
                ChatConversation conversation = invocation.getArgument(0);
                if (conversation.getId() == null) {
                    conversation.setId(11L);
                }
                return conversation;
            });

        when(chatMessageRepository.save(org.mockito.ArgumentMatchers.any(ChatMessage.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        when(providerChatClient.chat(org.mockito.ArgumentMatchers.eq(model),
            org.mockito.ArgumentMatchers.any())).thenThrow(
            new ProviderRequestException("OpenRouter request failed: HTTP 429", 429, "rate limit", null));

        when(chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(11L)).thenReturn(
            List.of(buildMessage(1L, 11L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "hi", null, null)),
            List.of(buildMessage(1L, 11L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "hi", null, null),
                buildMessage(2L, 11L, ChatMessageRole.ASSISTANT, ChatMessageStatus.ERROR,
                    "OpenRouter request failed: HTTP 429", "rate limit", null, "OpenRouter request failed: HTTP 429", 0,
                    0, 0)));

        ChatConversationCreateDTO dto = ChatConversationCreateDTO.builder().modelId(2L).content("hi").build();

        ChatConversationDetailVO result = chatHistoryService.createConversation(dto);

        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository, times(2)).save(messageCaptor.capture());
        ChatMessage assistantMessage = messageCaptor.getAllValues().get(1);
        assertThat(assistantMessage.getStatus()).isEqualTo(ChatMessageStatus.ERROR);
        assertThat(assistantMessage.getProviderResponse()).isEqualTo("rate limit");
        assertThat(assistantMessage.getTotalTokens()).isEqualTo(0);
        assertThat(result.getMessages().get(1).getStatus()).isEqualTo("error");
    }

    @Test
    void createConversationRejectsOversizedMessage() {
        ChatConversationCreateDTO dto =
            ChatConversationCreateDTO.builder().modelId(1L).content("a".repeat(5001)).build();

        assertThatThrownBy(() -> chatHistoryService.createConversation(dto)).isInstanceOf(
            IllegalArgumentException.class).hasMessage("Chat content exceeds 5000 characters");
    }

    @Test
    void listConversationsReturnsSortedList() {
        ChatConversation first = ChatConversation.builder().title("first").build();
        first.setId(1L);
        first.setUpdatedAt(LocalDateTime.now().minusHours(1));

        ChatConversation second = ChatConversation.builder().title("second").build();
        second.setId(2L);
        second.setUpdatedAt(LocalDateTime.now());

        when(chatConversationRepository.findByIsDeletedFalseOrderByUpdatedAtDesc()).thenReturn(List.of(second, first));

        List<ChatConversationSummaryVO> result = chatHistoryService.listConversations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(1).getId()).isEqualTo(1L);
    }

    @Test
    void appendMessageUsesExistingConversation() {
        ChatConversation conversation =
            ChatConversation.builder().title("hello").modelId(3L).modelName("openai/gpt-4o-mini").build();
        conversation.setId(20L);

        when(chatConversationRepository.findByIdAndIsDeletedFalse(20L)).thenReturn(Optional.of(conversation));

        AiModel model = new AiModel();
        model.setId(3L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(3L)).thenReturn(model);

        when(providerChatClient.chat(org.mockito.ArgumentMatchers.eq(model),
            org.mockito.ArgumentMatchers.any())).thenReturn(
            ChatResponseDTO.builder().content("ok").model("openai/gpt-4o-mini").build());

        when(chatConversationRepository.save(org.mockito.ArgumentMatchers.any(ChatConversation.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        when(chatMessageRepository.save(org.mockito.ArgumentMatchers.any(ChatMessage.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        when(chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(20L)).thenReturn(
            List.of(buildMessage(1L, 20L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "ping", null, null),
                buildMessage(2L, 20L, ChatMessageRole.ASSISTANT, ChatMessageStatus.SUCCESS, "ok", null, null),
                buildMessage(3L, 20L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "ping", null, null)),
            List.of(buildMessage(1L, 20L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "ping", null, null),
                buildMessage(2L, 20L, ChatMessageRole.ASSISTANT, ChatMessageStatus.SUCCESS, "ok", null, null),
                buildMessage(3L, 20L, ChatMessageRole.USER, ChatMessageStatus.SUCCESS, "ping", null, null),
                buildMessage(4L, 20L, ChatMessageRole.ASSISTANT, ChatMessageStatus.SUCCESS, "ok", null, null)));

        ChatMessageCreateDTO dto = ChatMessageCreateDTO.builder().content("ping").build();
        ChatConversationDetailVO result = chatHistoryService.appendMessage(20L, dto);

        assertThat(result.getMessages()).hasSize(4);
        verify(chatMessageRepository, times(2)).save(org.mockito.ArgumentMatchers.any(ChatMessage.class));

        ArgumentCaptor<ChatRequestDTO> requestCaptor = ArgumentCaptor.forClass(ChatRequestDTO.class);
        verify(providerChatClient).chat(org.mockito.ArgumentMatchers.eq(model), requestCaptor.capture());
        List<ChatRequestDTO.Message> messages = requestCaptor.getValue().getMessages();
        assertThat(messages.get(messages.size() - 1).getContent()).isEqualTo("ping");
    }

    @Test
    void appendMessageBuildsContextWindowWithLimits() {
        ChatConversation conversation =
            ChatConversation.builder().title("hello").modelId(3L).modelName("openai/gpt-4o-mini").build();
        conversation.setId(30L);

        when(chatConversationRepository.findByIdAndIsDeletedFalse(30L)).thenReturn(Optional.of(conversation));

        AiModel model = new AiModel();
        model.setId(3L);
        model.setModelName("openai/gpt-4o-mini");
        model.setProvider(ModelProvider.OPENROUTER);
        model.setIsEnabled(true);
        when(aiModelService.getById(3L)).thenReturn(model);

        when(providerChatClient.chat(org.mockito.ArgumentMatchers.eq(model),
            org.mockito.ArgumentMatchers.any())).thenReturn(
            ChatResponseDTO.builder().content("ok").model("openai/gpt-4o-mini").build());

        when(chatConversationRepository.save(org.mockito.ArgumentMatchers.any(ChatConversation.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        when(chatMessageRepository.save(org.mockito.ArgumentMatchers.any(ChatMessage.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        List<ChatMessage> history = buildHistory(30L, 12, 1000);
        when(chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(30L)).thenReturn(history, history);

        ChatMessageCreateDTO dto = ChatMessageCreateDTO.builder().content("ping").build();
        chatHistoryService.appendMessage(30L, dto);

        ArgumentCaptor<ChatRequestDTO> requestCaptor = ArgumentCaptor.forClass(ChatRequestDTO.class);
        verify(providerChatClient).chat(org.mockito.ArgumentMatchers.eq(model), requestCaptor.capture());
        List<ChatRequestDTO.Message> messages = requestCaptor.getValue().getMessages();
        assertThat(messages).hasSize(5);
        assertThat(messages.get(0).getContent()).startsWith("msg-8");
        assertThat(messages.get(4).getContent()).startsWith("msg-12");
    }

    private ChatMessage buildMessage(Long id, Long conversationId, ChatMessageRole role, ChatMessageStatus status,
        String content, String providerResponse, String model) {
        return buildMessage(id, conversationId, role, status, content, providerResponse, model, null, 0, 0, 0);
    }

    private ChatMessage buildMessage(Long id, Long conversationId, ChatMessageRole role, ChatMessageStatus status,
        String content, String providerResponse, String model, String errorMessage) {
        return buildMessage(id, conversationId, role, status, content, providerResponse, model, errorMessage, 0, 0, 0);
    }

    private ChatMessage buildMessage(Long id, Long conversationId, ChatMessageRole role, ChatMessageStatus status,
        String content, String providerResponse, String model, String errorMessage, Integer promptTokens,
        Integer completionTokens, Integer totalTokens) {
        ChatMessage message =
            ChatMessage.builder().conversationId(conversationId).role(role).status(status).content(content)
                .providerResponse(providerResponse).model(model).errorMessage(errorMessage).promptTokens(promptTokens)
                .completionTokens(completionTokens).totalTokens(totalTokens).build();
        message.setId(id);
        return message;
    }

    private List<ChatMessage> buildHistory(Long conversationId, int count, int contentLength) {
        List<ChatMessage> messages = new java.util.ArrayList<>();
        for (int index = 1; index <= count; index++) {
            String content = buildContent("msg-" + index, contentLength);
            ChatMessageRole role = index % 2 == 0 ? ChatMessageRole.ASSISTANT : ChatMessageRole.USER;
            messages.add(
                buildMessage((long)index, conversationId, role, ChatMessageStatus.SUCCESS, content, null, null));
        }
        return messages;
    }

    private String buildContent(String prefix, int length) {
        int remaining = Math.max(length - prefix.length(), 0);
        return prefix + "a".repeat(remaining);
    }
}
