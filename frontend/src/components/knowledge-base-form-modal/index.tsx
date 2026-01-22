import { useEffect } from 'react'
import { Modal, Form, Input, Switch, message } from 'antd'
import type { KnowledgeBase, KnowledgeBaseDTO } from '../../types/knowledge-base'
import './knowledge-base-form-modal.css'

interface KnowledgeBaseFormModalProps {
    open: boolean
    editingBase: KnowledgeBase | null
    onCancel: () => void
    onSuccess: () => void
    onCreate: (dto: KnowledgeBaseDTO) => Promise<void>
    onUpdate: (id: number, dto: KnowledgeBaseDTO) => Promise<void>
}

function KnowledgeBaseFormModal({
    open,
    editingBase,
    onCancel,
    onSuccess,
    onCreate,
    onUpdate,
}: KnowledgeBaseFormModalProps) {
    const [form] = Form.useForm<KnowledgeBaseDTO>()
    const isEditing = !!editingBase

    useEffect(() => {
        if (open && editingBase) {
            form.setFieldsValue({
                name: editingBase.name,
                description: editingBase.description,
                isEnabled: editingBase.isEnabled,
            })
        } else if (open) {
            form.resetFields()
            form.setFieldsValue({ isEnabled: true })
        }
    }, [open, editingBase, form])

    const handleOk = async () => {
        try {
            const values = await form.validateFields()
            if (isEditing && editingBase) {
                await onUpdate(editingBase.id, values)
                message.success('知识库已更新')
            } else {
                await onCreate(values)
                message.success('知识库已创建')
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
            title={isEditing ? '编辑知识库' : '新建知识库'}
            open={open}
            onOk={handleOk}
            onCancel={onCancel}
            okText="确认"
            cancelText="取消"
            destroyOnClose
            className="knowledge-base-modal"
        >
            <Form form={form} layout="vertical" className="knowledge-base-form">
                <Form.Item
                    name="name"
                    label="知识库名称"
                    rules={[
                        { required: true, message: '请输入知识库名称' },
                        { max: 120, message: '名称不能超过120个字符' },
                    ]}
                >
                    <Input placeholder="例如: 产品手册" />
                </Form.Item>

                <Form.Item
                    name="description"
                    label="描述"
                    rules={[{ max: 500, message: '描述不能超过500个字符' }]}
                >
                    <Input.TextArea rows={3} placeholder="用途或内容范围" />
                </Form.Item>

                <Form.Item name="isEnabled" label="启用状态" valuePropName="checked">
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" />
                </Form.Item>
            </Form>
        </Modal>
    )
}

export default KnowledgeBaseFormModal
