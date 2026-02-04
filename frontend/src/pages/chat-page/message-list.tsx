import { Empty, Spin } from 'antd'
import { useEffect, useRef } from 'react'
import type { ChatMessage } from '../../types/chat'

interface MessageListProps {
    messages: ChatMessage[]
    loading: boolean
}

const formatTime = (timestamp: string) => {
    return new Date(timestamp).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
    })
}

function MessageList({ messages, loading }: MessageListProps) {
    const endRef = useRef<HTMLDivElement | null>(null)

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, [messages, loading])

    if (messages.length === 0 && !loading) {
        return (
            <div className="message-list message-list-empty">
                <Empty description="暂无消息，开始对话吧" />
            </div>
        )
    }

    return (
        <div className="message-list">
            {messages.map((message, index) => (
                <div
                    key={`${message.id ?? message.createdAt}-${index}`}
                    className={`message-row message-row-${message.role}`}
                >
                    <div className={`message-bubble message-bubble-${message.role}`}>
                        <div className="message-content">{message.content}</div>
                        <div className="message-meta">
                            <div className="message-meta-row">
                                <span>{formatTime(message.createdAt)}</span>
                                {message.role === 'assistant' && message.model && (
                                    <span className="message-model">模型：{message.model}</span>
                                )}
                            </div>
                            {message.role === 'assistant' && (
                                <div className="message-meta-row message-meta-tokens">
                                    Tokens：总 {message.totalTokens ?? 0}
                                    （提示 {message.promptTokens ?? 0} / 回复 {message.completionTokens ?? 0}）
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            ))}
            {loading && (
                <div className="message-row message-row-assistant">
                    <div className="message-bubble message-bubble-assistant message-bubble-loading">
                        <Spin size="small" />
                        <span>AI 正在回复...</span>
                    </div>
                </div>
            )}
            <div ref={endRef} />
        </div>
    )
}

export default MessageList
