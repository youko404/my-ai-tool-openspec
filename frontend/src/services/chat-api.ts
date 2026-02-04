import http from './http'
import type {
    ChatConversationCreateRequest,
    ChatMessageCreateRequest,
    ConversationDetail,
    ConversationSummary,
} from '../types/chat'

/**
 * Chat API service
 */
export const chatApi = {
    listConversations(): Promise<ConversationSummary[]> {
        return http.get<ConversationSummary[]>('/chat/conversations')
    },

    getConversation(id: number): Promise<ConversationDetail> {
        return http.get<ConversationDetail>(`/chat/conversations/${id}`)
    },

    createConversation(request: ChatConversationCreateRequest): Promise<ConversationDetail> {
        return http.post<ConversationDetail>('/chat/conversations', request)
    },

    sendMessage(conversationId: number, request: ChatMessageCreateRequest): Promise<ConversationDetail> {
        return http.post<ConversationDetail>(`/chat/conversations/${conversationId}/messages`, request)
    },

    deleteConversation(conversationId: number): Promise<void> {
        return http.delete<void>(`/chat/conversations/${conversationId}`)
    },
}

export default chatApi
