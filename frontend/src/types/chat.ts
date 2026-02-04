/**
 * Chat type definitions
 */

export type ChatRole = 'user' | 'assistant'
export type ChatStatus = 'success' | 'error'

export interface ChatMessage {
    id?: number
    role: ChatRole
    content: string
    status?: ChatStatus
    errorMessage?: string
    model?: string
    promptTokens?: number
    completionTokens?: number
    totalTokens?: number
    createdAt: string
}

export interface Conversation {
    id: string
    serverId?: number
    title: string
    modelId: number | null
    modelName?: string
    lastMessagePreview?: string
    messages: ChatMessage[]
    createdAt: string
    updatedAt: string
    isDraft?: boolean
}

export interface ChatConversationCreateRequest {
    modelId: number
    content: string
    temperature?: number
    maxTokens?: number
}

export interface ChatMessageCreateRequest {
    content: string
    temperature?: number
    maxTokens?: number
}

export interface ConversationSummary {
    id: number
    title: string
    modelId: number | null
    modelName?: string
    lastMessagePreview?: string
    createdAt: string
    updatedAt: string
}

export interface ConversationDetail extends ConversationSummary {
    messages: ChatMessage[]
}
