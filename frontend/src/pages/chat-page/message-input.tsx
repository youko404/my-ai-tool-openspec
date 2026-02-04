import { Button, Input } from 'antd'
import { SendOutlined } from '@ant-design/icons'

interface MessageInputProps {
    value: string
    onChange: (value: string) => void
    onSend: () => void
    disabled?: boolean
    sending?: boolean
}

function MessageInput({ value, onChange, onSend, disabled, sending }: MessageInputProps) {
    const isDisabled = disabled || !value.trim()

    return (
        <div className="message-input">
            <Input.TextArea
                value={value}
                onChange={(event) => onChange(event.target.value)}
                placeholder="输入消息，Enter 发送，Shift+Enter 换行"
                autoSize={{ minRows: 2, maxRows: 6 }}
                onKeyDown={(event) => {
                    if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
                        event.preventDefault()
                        if (!isDisabled) {
                            onSend()
                        }
                    }
                }}
                disabled={disabled}
            />
            <div className="message-input-actions">
                <Button
                    type="primary"
                    icon={<SendOutlined />}
                    onClick={onSend}
                    disabled={isDisabled}
                    loading={sending}
                >
                    发送
                </Button>
            </div>
        </div>
    )
}

export default MessageInput
