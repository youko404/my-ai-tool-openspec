package com.ai.content.controller;

import com.ai.content.dto.ChatConversationCreateDTO;
import com.ai.content.dto.ChatMessageCreateDTO;
import com.ai.content.service.ChatHistoryService;
import com.ai.content.vo.ChatConversationDetailVO;
import com.ai.content.vo.ChatConversationSummaryVO;
import com.ai.content.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/conversations")
@RequiredArgsConstructor
public class ChatConversationController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping
    public Result<List<ChatConversationSummaryVO>> listConversations() {
        return Result.success(chatHistoryService.listConversations());
    }

    @GetMapping("/{id}")
    public Result<ChatConversationDetailVO> getConversation(@PathVariable Long id) {
        return Result.success(chatHistoryService.getConversation(id));
    }

    @PostMapping
    public Result<ChatConversationDetailVO> createConversation(@RequestBody ChatConversationCreateDTO dto) {
        return Result.success(chatHistoryService.createConversation(dto));
    }

    @PostMapping("/{id}/messages")
    public Result<ChatConversationDetailVO> appendMessage(@PathVariable Long id,
        @RequestBody ChatMessageCreateDTO dto) {
        return Result.success(chatHistoryService.appendMessage(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteConversation(@PathVariable Long id) {
        chatHistoryService.softDeleteConversation(id);
        return Result.success();
    }
}
