package com.ai.content.service;

import com.ai.content.dto.ChatConversationCreateDTO;
import com.ai.content.dto.ChatMessageCreateDTO;
import com.ai.content.vo.ChatConversationDetailVO;
import com.ai.content.vo.ChatConversationSummaryVO;

import java.util.List;

public interface ChatHistoryService {

    ChatConversationDetailVO createConversation(ChatConversationCreateDTO dto);

    ChatConversationDetailVO appendMessage(Long conversationId, ChatMessageCreateDTO dto);

    List<ChatConversationSummaryVO> listConversations();

    ChatConversationDetailVO getConversation(Long conversationId);

    void softDeleteConversation(Long conversationId);
}
