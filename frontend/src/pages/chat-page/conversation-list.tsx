import { Button, Empty } from 'antd'
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import type { Conversation } from '../../types/chat'

interface ConversationListProps {
    conversations: Conversation[]
    activeConversationId: string | null
    onCreate: () => void
    onSelect: (id: string) => void
    onDelete: (id: string) => void
}

const formatTimestamp = (timestamp?: string) => {
    if (!timestamp) {
        return ''
    }
    return new Date(timestamp).toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    })
}

const getPreview = (conversation: Conversation) => {
    if (conversation.isDraft) {
        return '草稿未发送'
    }
    if (conversation.lastMessagePreview) {
        return conversation.lastMessagePreview
    }
    const lastMessage = conversation.messages[conversation.messages.length - 1]
    if (!lastMessage) {
        return '暂无消息'
    }
    const content = lastMessage.content.trim()
    if (content.length <= 50) {
        return content
    }
    return `${content.slice(0, 50)}...`
}

function ConversationList({
    conversations,
    activeConversationId,
    onCreate,
    onSelect,
    onDelete,
}: ConversationListProps) {
    return (
        <div className="conversation-list">
            <div className="conversation-list-header">
                <div>
                    <h3>对话列表</h3>
                    <p>共 {conversations.length} 个对话</p>
                </div>
                <Button type="primary" icon={<PlusOutlined />} onClick={onCreate}>
                    新建对话
                </Button>
            </div>
            <div className="conversation-list-body">
                {conversations.length === 0 ? (
                    <Empty description="暂无对话，点击新建对话开始聊天" />
                ) : (
                    conversations.map((conversation) => (
                        <div
                            key={conversation.id}
                            className={`conversation-item ${
                                activeConversationId === conversation.id ? 'conversation-item-active' : ''
                            }`}
                            role="button"
                            tabIndex={0}
                            onClick={() => onSelect(conversation.id)}
                            onKeyDown={(event) => {
                                if (event.key === 'Enter') {
                                    onSelect(conversation.id)
                                }
                            }}
                        >
                            <div className="conversation-item-title">{conversation.title}</div>
                            <div className="conversation-item-preview">{getPreview(conversation)}</div>
                            <div className="conversation-item-meta">
                                <span>{formatTimestamp(conversation.updatedAt || conversation.createdAt)}</span>
                                <Button
                                    type="text"
                                    danger
                                    icon={<DeleteOutlined />}
                                    className="conversation-item-delete"
                                    onClick={(event) => {
                                        event.stopPropagation()
                                        onDelete(conversation.id)
                                    }}
                                />
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    )
}

export default ConversationList
