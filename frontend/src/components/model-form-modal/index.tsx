import { useEffect } from 'react'
import { Modal, Form, Input, Switch, message } from 'antd'
import type { AiModel, AiModelDTO } from '../../types/model'
import './model-form-modal.css'

interface ModelFormModalProps {
    open: boolean
    editingModel: AiModel | null
    onCancel: () => void
    onSuccess: () => void
    onCreate: (dto: AiModelDTO) => Promise<void>
    onUpdate: (id: number, dto: AiModelDTO) => Promise<void>
}

function ModelFormModal({
    open,
    editingModel,
    onCancel,
    onSuccess,
    onCreate,
    onUpdate,
}: ModelFormModalProps) {
    const [form] = Form.useForm<AiModelDTO>()
    const isEditing = !!editingModel

    useEffect(() => {
        if (open && editingModel) {
            form.setFieldsValue({
                modelName: editingModel.modelName,
                provider: editingModel.provider,
                isEnabled: editingModel.isEnabled,
            })
        } else if (open) {
            form.resetFields()
            form.setFieldsValue({ isEnabled: true })
        }
    }, [open, editingModel, form])

    const handleOk = async () => {
        try {
            const values = await form.validateFields()
            if (isEditing && editingModel) {
                await onUpdate(editingModel.id, values)
                message.success('模型更新成功')
            } else {
                await onCreate(values)
                message.success('模型创建成功')
            }
            onSuccess()
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }

    return (
        <Modal
            title={isEditing ? '编辑模型' : '新增模型'}
            open={open}
            onOk={handleOk}
            onCancel={onCancel}
            okText="确认"
            cancelText="取消"
            destroyOnClose
            className="model-page-modal"
        >
            <Form
                form={form}
                layout="vertical"
                className="model-form"
            >
                <Form.Item
                    name="modelName"
                    label="模型名称"
                    rules={[
                        { required: true, message: '请输入模型名称' },
                        { max: 100, message: '模型名称不能超过100个字符' },
                    ]}
                >
                    <Input placeholder="例如: gpt-4, claude-3-opus" />
                </Form.Item>

                <Form.Item
                    name="provider"
                    label="供应商"
                    rules={[
                        { required: true, message: '请输入供应商' },
                        { max: 50, message: '供应商名称不能超过50个字符' },
                    ]}
                >
                    <Input placeholder="例如: OpenAI, Anthropic, Google" />
                </Form.Item>

                <Form.Item
                    name="isEnabled"
                    label="启用状态"
                    valuePropName="checked"
                >
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" />
                </Form.Item>
            </Form>
        </Modal>
    )
}

export default ModelFormModal
