package com.ai.content.service;

import com.ai.content.dto.ChatRequestDTO;
import com.ai.content.dto.ChatResponseDTO;

public interface ChatService {

    ChatResponseDTO chat(Long modelId, ChatRequestDTO request);
}
