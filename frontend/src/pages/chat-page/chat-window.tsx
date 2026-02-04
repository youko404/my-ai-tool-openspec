import { Button, Empty, Select } from 'antd'
import { MessageOutlined } from '@ant-design/icons'
import { useEffect, useMemo, useState } from 'react'
import type { AiModel } from '../../types/model'
import type { Conversation } from '../../types/chat'
import MessageList from './message-list'
import MessageInput from './message-input'

interface ChatWindowProps {
    conversation: Conversation | null
    models: AiModel[]
    modelsLoading: boolean
    sending: boolean
    onLoadModels: () => void
    onSelectModel: (modelId: number, modelName: string) => void
    onSendMessage: (content: string) => Promise<boolean>
    onCreateConversation: () => void
    onToggleSidebar: () => void
}

type ModelOption = {
    value: number
    label: string
    modelName: string
}

function ChatWindow({
    conversation,
    models,
    modelsLoading,
    sending,
    onLoadModels,
    onSelectModel,
    onSendMessage,
    onCreateConversation,
    onToggleSidebar,
}: ChatWindowProps) {
    const [inputValue, setInputValue] = useState('')

    useEffect(() => {
        setInputValue('')
    }, [conversation?.id])

    const modelOptions = useMemo<ModelOption[]>(() => {
        const options: ModelOption[] = models.map((model) => ({
            value: model.id,
            label: `${model.modelName} · ${model.provider}`,
            modelName: model.modelName,
        }))
        if (conversation?.modelId && conversation.modelName) {
            const exists = options.some((option) => option.value === conversation.modelId)
            if (!exists) {
                return [{
                    value: conversation.modelId,
                    label: conversation.modelName,
                    modelName: conversation.modelName,
                }, ...options]
            }
        }
        return options
    }, [models, conversation])

    const handleSelectModel = (modelId: number) => {
        const selected = modelOptions.find((option) => option.value === modelId)
        onSelectModel(modelId, selected?.modelName ?? conversation?.modelName ?? String(modelId))
    }

    const handleSend = async () => {
        const success = await onSendMessage(inputValue)
        if (success) {
            setInputValue('')
        }
    }

    const modelLocked = Boolean(conversation && !conversation.isDraft && conversation.serverId)
    const disableInput = !conversation || sending

    return (
        <section className="chat-window">
            <div className="chat-window-header">
                <div className="chat-window-title">
                    <Button
                        type="text"
                        icon={<MessageOutlined />}
                        className="chat-sidebar-toggle"
                        onClick={onToggleSidebar}
                    >
                        会话
                    </Button>
                    <div>
                        <h3>{conversation?.title ?? '聊天'}</h3>
                        <p>{conversation?.modelName ? `模型：${conversation.modelName}` : '请选择模型开始对话'}</p>
                    </div>
                </div>
                <Select<number>
                    placeholder={conversation ? '请选择模型' : '请先新建对话'}
                    options={modelOptions}
                    value={conversation?.modelId ?? undefined}
                    onDropdownVisibleChange={(open) => {
                        if (open) {
                            onLoadModels()
                        }
                    }}
                    onChange={handleSelectModel}
                    loading={modelsLoading}
                    disabled={!conversation || modelLocked}
                    className="chat-model-select"
                    dropdownClassName="chat-model-select-dropdown"
                />
            </div>
            <div className="chat-window-body">
                {conversation ? (
                    <MessageList messages={conversation.messages} loading={sending} />
                ) : (
                    <div className="chat-window-empty">
                        <Empty description="暂无对话，点击新建对话开始聊天" />
                        <Button type="primary" onClick={onCreateConversation}>
                            新建对话
                        </Button>
                    </div>
                )}
            </div>
            <div className="chat-window-footer">
                <MessageInput
                    value={inputValue}
                    onChange={setInputValue}
                    onSend={handleSend}
                    disabled={disableInput}
                    sending={sending}
                />
            </div>
        </section>
    )
}

export default ChatWindow
