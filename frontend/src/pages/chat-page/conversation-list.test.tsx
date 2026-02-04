import { render, screen } from '@testing-library/react'
import ConversationList from './conversation-list'
import type { Conversation } from '../../types/chat'

describe('ConversationList', () => {
    it('renders draft and preview content', () => {
        const conversations: Conversation[] = [
            {
                id: 'draft-1',
                title: '新对话',
                modelId: null,
                messages: [],
                createdAt: '2025-01-01T10:00:00',
                updatedAt: '2025-01-01T10:00:00',
                isDraft: true,
            },
            {
                id: '1',
                serverId: 1,
                title: '示例对话',
                modelId: 1,
                modelName: 'openai/gpt-4o-mini',
                lastMessagePreview: 'hello world',
                messages: [],
                createdAt: '2025-01-01T09:00:00',
                updatedAt: '2025-01-01T11:00:00',
            },
        ]

        render(
            <ConversationList
                conversations={conversations}
                activeConversationId={null}
                onCreate={() => undefined}
                onSelect={() => undefined}
                onDelete={() => undefined}
            />
        )

        expect(screen.getByText('草稿未发送')).toBeInTheDocument()
        expect(screen.getByText('hello world')).toBeInTheDocument()
        expect(screen.getByText('对话列表')).toBeInTheDocument()
    })
})
