import { useCallback, useEffect, useMemo, useState } from 'react'
import { message, Modal } from 'antd'
import type { AiModel } from '../../types/model'
import type { ChatMessage, Conversation, ConversationDetail, ConversationSummary } from '../../types/chat'
import { chatApi } from '../../services/chat-api'
import { modelApi } from '../../services/model-api'
import ConversationList from './conversation-list'
import ChatWindow from './chat-window'
import './chat-page.css'

const MAX_MESSAGES = 100

const sortConversations = (items: Conversation[]) => {
    return [...items].sort((a, b) => {
        const aTime = new Date(a.updatedAt).getTime()
        const bTime = new Date(b.updatedAt).getTime()
        return bTime - aTime
    })
}

const normalizeMessages = (messages: ChatMessage[]) => {
    if (messages.length <= MAX_MESSAGES) {
        return messages
    }
    return messages.slice(messages.length - MAX_MESSAGES)
}

const toConversation = (summary: ConversationSummary): Conversation => {
    return {
        id: String(summary.id),
        serverId: summary.id,
        title: summary.title,
        modelId: summary.modelId,
        modelName: summary.modelName,
        lastMessagePreview: summary.lastMessagePreview,
        messages: [],
        createdAt: summary.createdAt,
        updatedAt: summary.updatedAt,
    }
}

const toConversationDetail = (detail: ConversationDetail): Conversation => {
    return {
        id: String(detail.id),
        serverId: detail.id,
        title: detail.title,
        modelId: detail.modelId,
        modelName: detail.modelName,
        lastMessagePreview: detail.lastMessagePreview,
        messages: normalizeMessages(detail.messages ?? []),
        createdAt: detail.createdAt,
        updatedAt: detail.updatedAt,
    }
}

function ChatPage() {
    const [conversations, setConversations] = useState<Conversation[]>([])
    const [activeConversationId, setActiveConversationId] = useState<string | null>(null)
    const [models, setModels] = useState<AiModel[]>([])
    const [modelsLoading, setModelsLoading] = useState(false)
    const [sendingConversationId, setSendingConversationId] = useState<string | null>(null)
    const [sidebarOpen, setSidebarOpen] = useState(false)

    const loadConversationDetail = useCallback(async (serverId: number) => {
        try {
            const detail = await chatApi.getConversation(serverId)
            const nextConversation = toConversationDetail(detail)
            setConversations((prev) => {
                const updated = prev.map((item) =>
                    item.serverId === serverId ? { ...nextConversation, isDraft: false } : item
                )
                return sortConversations(updated)
            })
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }, [])

    const loadConversations = useCallback(async () => {
        try {
            const data = await chatApi.listConversations()
            const mapped = data.map(toConversation)
            setConversations((prev) => {
                const drafts = prev.filter((item) => item.isDraft)
                return sortConversations([...drafts, ...mapped])
            })
            setActiveConversationId((prev) => {
                if (prev && (prev.startsWith('draft-') || mapped.some((item) => item.id === prev))) {
                    return prev
                }
                return mapped[0]?.id ?? null
            })
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }, [])

    useEffect(() => {
        loadConversations()
    }, [loadConversations])

    const activeConversation = useMemo(
        () => conversations.find((item) => item.id === activeConversationId) ?? null,
        [conversations, activeConversationId]
    )

    useEffect(() => {
        if (!activeConversation || activeConversation.isDraft || !activeConversation.serverId) {
            return
        }
        if (activeConversation.messages.length > 0) {
            return
        }
        loadConversationDetail(activeConversation.serverId)
    }, [activeConversation, loadConversationDetail])

    const handleCreateConversation = useCallback(() => {
        const now = new Date().toISOString()
        const newConversation: Conversation = {
            id: `draft-${Date.now()}`,
            title: '新对话',
            modelId: null,
            messages: [],
            createdAt: now,
            updatedAt: now,
            isDraft: true,
        }
        setConversations((prev) => sortConversations([newConversation, ...prev]))
        setActiveConversationId(newConversation.id)
        setSidebarOpen(false)
    }, [])

    const handleSelectConversation = useCallback(
        (id: string) => {
            setActiveConversationId(id)
            setSidebarOpen(false)
            const selected = conversations.find((item) => item.id === id)
            if (selected && !selected.isDraft && selected.serverId && selected.messages.length === 0) {
                loadConversationDetail(selected.serverId)
            }
        },
        [conversations, loadConversationDetail]
    )

    const handleDeleteConversation = useCallback((id: string) => {
        const target = conversations.find((item) => item.id === id)
        if (!target) {
            return
        }
        Modal.confirm({
            title: '确认删除对话',
            content: '删除后无法恢复，是否继续？',
            okText: '确认',
            cancelText: '取消',
            okButtonProps: { danger: true },
            className: 'chat-page-confirm',
            onOk: async () => {
                try {
                    if (!target.isDraft && target.serverId) {
                        await chatApi.deleteConversation(target.serverId)
                    }
                    let remaining: Conversation[] = []
                    setConversations((prev) => {
                        remaining = prev.filter((item) => item.id !== id)
                        return sortConversations(remaining)
                    })
                    setActiveConversationId((prev) => {
                        if (prev === id) {
                            return remaining[0]?.id ?? null
                        }
                        return prev
                    })
                    setSidebarOpen(false)
                } catch (error) {
                    if (error instanceof Error) {
                        message.error(error.message)
                    }
                }
            },
        })
    }, [conversations])

    const loadModels = useCallback(async () => {
        if (modelsLoading || models.length > 0) {
            return
        }
        setModelsLoading(true)
        try {
            const data = await modelApi.getEnabled()
            setModels(data)
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setModelsLoading(false)
        }
    }, [modelsLoading, models.length])

    const handleSelectModel = useCallback(
        (modelId: number, modelName: string) => {
            if (!activeConversationId) {
                return
            }
            setConversations((prev) => {
                const next = prev.map((item) => {
                    if (item.id !== activeConversationId) {
                        return item
                    }
                    return {
                        ...item,
                        modelId,
                        modelName,
                        updatedAt: new Date().toISOString(),
                    }
                })
                return sortConversations(next)
            })
        },
        [activeConversationId]
    )

    const handleSendMessage = useCallback(async (content: string) => {
        if (!activeConversation) {
            return false
        }
        if (!activeConversation.modelId) {
            message.error('请先选择模型')
            return false
        }
        const trimmed = content.trim()
        if (!trimmed) {
            return false
        }
        const now = new Date().toISOString()
        const userMessage: ChatMessage = {
            role: 'user',
            content: trimmed,
            status: 'success',
            createdAt: now,
        }

        setConversations((prev) => {
            const next = prev.map((item) => {
                if (item.id !== activeConversation.id) {
                    return item
                }
                const messages = normalizeMessages([...item.messages, userMessage])
                return {
                    ...item,
                    messages,
                    lastMessagePreview: trimmed,
                    updatedAt: now,
                }
            })
            return sortConversations(next)
        })

        setSendingConversationId(activeConversation.id)
        try {
            let detail: ConversationDetail
            if (activeConversation.isDraft) {
                detail = await chatApi.createConversation({
                    modelId: activeConversation.modelId,
                    content: trimmed,
                })
            } else if (activeConversation.serverId) {
                detail = await chatApi.sendMessage(activeConversation.serverId, { content: trimmed })
            } else {
                message.error('会话不可用，请重新创建')
                return false
            }
            const updated = toConversationDetail(detail)
            setConversations((prev) => {
                const filtered = prev.filter((item) => item.id !== activeConversation.id && item.serverId !== detail.id)
                return sortConversations([updated, ...filtered])
            })
            setActiveConversationId(updated.id)
            const lastMessage = updated.messages[updated.messages.length - 1]
            if (lastMessage?.status === 'error') {
                message.error(lastMessage.errorMessage || '发送失败，请重试')
            }
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message || '发送失败，请重试')
            } else {
                message.error('发送失败，请重试')
            }
        } finally {
            setSendingConversationId(null)
        }

        return true
    }, [activeConversation])

    const handleToggleSidebar = useCallback(() => {
        setSidebarOpen((prev) => !prev)
    }, [])

    return (
        <div className={`chat-page ${sidebarOpen ? 'chat-page--sidebar-open' : ''}`}>
            <div className="chat-layout">
                <aside className="chat-sidebar">
                    <ConversationList
                        conversations={conversations}
                        activeConversationId={activeConversationId}
                        onCreate={handleCreateConversation}
                        onSelect={handleSelectConversation}
                        onDelete={handleDeleteConversation}
                    />
                </aside>
                {sidebarOpen && (
                    <button
                        type="button"
                        className="chat-sidebar-overlay"
                        aria-label="关闭会话列表"
                        onClick={() => setSidebarOpen(false)}
                    />
                )}
                <ChatWindow
                    conversation={activeConversation}
                    models={models}
                    modelsLoading={modelsLoading}
                    onLoadModels={loadModels}
                    onSelectModel={handleSelectModel}
                    onSendMessage={handleSendMessage}
                    onCreateConversation={handleCreateConversation}
                    onToggleSidebar={handleToggleSidebar}
                    sending={sendingConversationId === activeConversation?.id}
                />
            </div>
        </div>
    )
}

export default ChatPage
