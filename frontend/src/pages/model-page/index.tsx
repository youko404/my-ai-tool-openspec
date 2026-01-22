import { useState, useEffect, useCallback } from 'react'
import { Table, Button, Input, Switch, Space, Modal, message } from 'antd'
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table'
import ModelFormModal from '../../components/model-form-modal'
import { modelApi } from '../../services/model-api'
import type { AiModel, AiModelDTO } from '../../types/model'
import './model-page.css'

function ModelPage() {
    const [models, setModels] = useState<AiModel[]>([])
    const [loading, setLoading] = useState(false)
    const [total, setTotal] = useState(0)
    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(10)
    const [keyword, setKeyword] = useState('')
    const [searchValue, setSearchValue] = useState('')

    // Modal state
    const [modalOpen, setModalOpen] = useState(false)
    const [editingModel, setEditingModel] = useState<AiModel | null>(null)

    const fetchModels = useCallback(async () => {
        setLoading(true)
        try {
            const result = keyword
                ? await modelApi.search(keyword, page, pageSize)
                : await modelApi.getPage(page, pageSize)
            setModels(result.list)
            setTotal(result.total)
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setLoading(false)
        }
    }, [page, pageSize, keyword])

    useEffect(() => {
        fetchModels()
    }, [fetchModels])

    const handleSearch = () => {
        setKeyword(searchValue)
        setPage(1)
    }

    const handleTableChange = (pagination: TablePaginationConfig) => {
        setPage(pagination.current || 1)
        setPageSize(pagination.pageSize || 10)
    }

    const handleAdd = () => {
        setEditingModel(null)
        setModalOpen(true)
    }

    const handleEdit = (record: AiModel) => {
        setEditingModel(record)
        setModalOpen(true)
    }

    const handleDelete = (record: AiModel) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除模型 "${record.modelName}" 吗？`,
            okText: '确认',
            cancelText: '取消',
            okButtonProps: { danger: true },
            className: 'model-page-confirm',
            onOk: async () => {
                try {
                    await modelApi.delete(record.id)
                    message.success('删除成功')
                    fetchModels()
                } catch (error) {
                    if (error instanceof Error) {
                        message.error(error.message)
                    }
                }
            },
        })
    }

    const handleToggleEnabled = async (record: AiModel, enabled: boolean) => {
        try {
            await modelApi.toggleEnabled(record.id, enabled)
            message.success(enabled ? '已启用' : '已禁用')
            fetchModels()
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }

    const handleCreate = async (dto: AiModelDTO) => {
        await modelApi.create(dto)
    }

    const handleUpdate = async (id: number, dto: AiModelDTO) => {
        await modelApi.update(id, dto)
    }

    const handleModalSuccess = () => {
        setModalOpen(false)
        setEditingModel(null)
        fetchModels()
    }

    const handleModalCancel = () => {
        setModalOpen(false)
        setEditingModel(null)
    }

    const columns: ColumnsType<AiModel> = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '模型名称',
            dataIndex: 'modelName',
            key: 'modelName',
        },
        {
            title: '供应商',
            dataIndex: 'provider',
            key: 'provider',
        },
        {
            title: '状态',
            dataIndex: 'isEnabled',
            key: 'isEnabled',
            width: 100,
            render: (enabled: boolean, record: AiModel) => (
                <Switch
                    checked={enabled}
                    onChange={(checked) => handleToggleEnabled(record, checked)}
                    checkedChildren="启用"
                    unCheckedChildren="禁用"
                />
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
            width: 180,
            render: (text: string) => text ? new Date(text).toLocaleString('zh-CN') : '-',
        },
        {
            title: '操作',
            key: 'actions',
            width: 150,
            render: (_: unknown, record: AiModel) => (
                <Space>
                    <Button
                        type="link"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    >
                        编辑
                    </Button>
                    <Button
                        type="link"
                        danger
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(record)}
                    >
                        删除
                    </Button>
                </Space>
            ),
        },
    ]

    return (
        <div className="model-page">
            <div className="model-page-header">
                <h2>模型管理</h2>
                <div className="model-page-actions">
                    <Input
                        placeholder="搜索模型名称"
                        value={searchValue}
                        onChange={(e) => setSearchValue(e.target.value)}
                        onPressEnter={handleSearch}
                        style={{ width: 200 }}
                        suffix={
                            <SearchOutlined
                                onClick={handleSearch}
                                style={{ cursor: 'pointer' }}
                            />
                        }
                    />
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={handleAdd}
                    >
                        新增模型
                    </Button>
                </div>
            </div>

            <Table
                columns={columns}
                dataSource={models}
                rowKey="id"
                loading={loading}
                pagination={{
                    current: page,
                    pageSize: pageSize,
                    total: total,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    showTotal: (total) => `共 ${total} 条`,
                }}
                onChange={handleTableChange}
            />

            <ModelFormModal
                open={modalOpen}
                editingModel={editingModel}
                onCancel={handleModalCancel}
                onSuccess={handleModalSuccess}
                onCreate={handleCreate}
                onUpdate={handleUpdate}
            />
        </div>
    )
}

export default ModelPage
