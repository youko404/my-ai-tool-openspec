import { useEffect, useMemo, useState } from 'react'
import { Modal, Form, Input, Select, message } from 'antd'
import { providerConfigApi } from '../../services/provider-config-api'
import type { ProviderConfigDTO } from '../../types/provider-config'
import '../model-form-modal/model-form-modal.css'

interface ProviderConfigModalProps {
    open: boolean
    providers: string[]
    onCancel: () => void
    onSuccess: () => void
}

function ProviderConfigModal({ open, providers, onCancel, onSuccess }: ProviderConfigModalProps) {
    const [form] = Form.useForm<ProviderConfigDTO>()
    const [loading, setLoading] = useState(false)
    const [saving, setSaving] = useState(false)
    const [apiKeyConfigured, setApiKeyConfigured] = useState(false)
    const [selectedProvider, setSelectedProvider] = useState<string | null>(null)

    const providerOptions = useMemo(
        () => providers.map((provider) => ({ label: provider, value: provider })),
        [providers]
    )

    useEffect(() => {
        if (!open) {
            return
        }
        const nextProvider = selectedProvider ?? providers[0] ?? null
        if (nextProvider) {
            setSelectedProvider(nextProvider)
            form.setFieldsValue({ provider: nextProvider })
        } else {
            form.resetFields()
        }
    }, [open, providers, selectedProvider, form])

    useEffect(() => {
        if (!open || !selectedProvider) {
            return
        }
        const loadConfig = async () => {
            setLoading(true)
            try {
                const config = await providerConfigApi.getByProvider(selectedProvider)
                form.setFieldsValue({
                    provider: selectedProvider,
                    apiBaseUrl: config.apiBaseUrl ?? '',
                    apiKey: '',
                })
                setApiKeyConfigured(Boolean(config.apiKeyConfigured))
            } catch (error) {
                if (error instanceof Error) {
                    message.error(error.message)
                }
                form.setFieldsValue({
                    provider: selectedProvider,
                    apiBaseUrl: '',
                    apiKey: '',
                })
                setApiKeyConfigured(false)
            } finally {
                setLoading(false)
            }
        }
        loadConfig()
    }, [open, selectedProvider, form])

    const handleOk = async () => {
        try {
            const values = await form.validateFields()
            setSaving(true)
            await providerConfigApi.save(values)
            message.success('保存成功')
            onSuccess()
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setSaving(false)
        }
    }

    const handleProviderChange = (value: string) => {
        setSelectedProvider(value)
        setApiKeyConfigured(false)
        form.setFieldsValue({ provider: value, apiKey: '' })
    }

    const apiKeyPlaceholder = apiKeyConfigured ? '已保存，重新填写将更新' : '请输入 API Key'

    return (
        <Modal
            title="服务商配置"
            open={open}
            onOk={handleOk}
            onCancel={onCancel}
            okText="保存"
            cancelText="取消"
            confirmLoading={saving}
            destroyOnClose
            className="model-page-modal"
        >
            <Form
                form={form}
                layout="vertical"
                className="model-form"
            >
                <Form.Item
                    name="provider"
                    label="服务商"
                    rules={[{ required: true, message: '请选择服务商' }]}
                >
                    <Select
                        options={providerOptions}
                        placeholder="请选择服务商"
                        onChange={handleProviderChange}
                        loading={loading}
                        disabled={loading || providers.length === 0}
                    />
                </Form.Item>

                <Form.Item
                    name="apiBaseUrl"
                    label="API 地址"
                    rules={[
                        { required: true, message: '请输入 API 地址' },
                        { max: 500, message: 'API 地址不能超过500个字符' },
                    ]}
                >
                    <Input placeholder="例如: https://openrouter.ai/api/v1" />
                </Form.Item>

                <Form.Item
                    name="apiKey"
                    label="API Key"
                    rules={[
                        { required: true, message: '请输入 API Key' },
                        { max: 500, message: 'API Key 不能超过500个字符' },
                    ]}
                >
                    <Input.Password placeholder={apiKeyPlaceholder} />
                </Form.Item>
            </Form>
        </Modal>
    )
}

export default ProviderConfigModal
