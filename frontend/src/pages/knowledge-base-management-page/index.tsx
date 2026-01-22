import { useCallback, useEffect, useRef, useState } from 'react'
import {
    Button,
    Modal,
    Progress,
    Space,
    Switch,
    Table,
    Tag,
    Upload,
    message,
} from 'antd'
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table'
import type { UploadFile } from 'antd/es/upload/interface'
import {
    DeleteOutlined,
    EditOutlined,
    FileSearchOutlined,
    PlusOutlined,
    UploadOutlined,
} from '@ant-design/icons'
import KnowledgeBaseFormModal from '../../components/knowledge-base-form-modal'
import { knowledgeBaseApi } from '../../services/knowledge-base-api'
import type {
    KnowledgeBase,
    KnowledgeBaseDTO,
    KnowledgeIngestionFile,
    KnowledgeIngestionJob,
} from '../../types/knowledge-base'
import '../knowledge-base-page/knowledge-base-page.css'

function KnowledgeBaseManagementPage() {
    const [knowledgeBases, setKnowledgeBases] = useState<KnowledgeBase[]>([])
    const [loading, setLoading] = useState(false)
    const [page, setPage] = useState(1)
    const [pageSize, setPageSize] = useState(10)
    const [total, setTotal] = useState(0)

    const [modalOpen, setModalOpen] = useState(false)
    const [editingBase, setEditingBase] = useState<KnowledgeBase | null>(null)

    const [uploadOpen, setUploadOpen] = useState(false)
    const [uploadingBase, setUploadingBase] = useState<KnowledgeBase | null>(null)
    const [fileList, setFileList] = useState<UploadFile[]>([])
    const [currentJob, setCurrentJob] = useState<KnowledgeIngestionJob | null>(null)
    const pollingRef = useRef<number | null>(null)

    const [fileProgressOpen, setFileProgressOpen] = useState(false)
    const [fileProgressBase, setFileProgressBase] = useState<KnowledgeBase | null>(null)
    const [fileProgressLoading, setFileProgressLoading] = useState(false)
    const [fileProgressPage, setFileProgressPage] = useState(1)
    const [fileProgressPageSize, setFileProgressPageSize] = useState(10)
    const [fileProgressTotal, setFileProgressTotal] = useState(0)
    const [fileProgressList, setFileProgressList] = useState<KnowledgeIngestionFile[]>([])
    const fileProgressPollingRef = useRef<number | null>(null)
    const fileProgressPageRef = useRef(1)
    const fileProgressPageSizeRef = useRef(10)

    const fetchKnowledgeBases = useCallback(async () => {
        setLoading(true)
        try {
            const result = await knowledgeBaseApi.getPage(page, pageSize)
            setKnowledgeBases(result.list)
            setTotal(result.total)
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setLoading(false)
        }
    }, [page, pageSize])

    useEffect(() => {
        fetchKnowledgeBases()
    }, [fetchKnowledgeBases])

    useEffect(() => {
        return () => {
            if (pollingRef.current) {
                clearInterval(pollingRef.current)
            }
            if (fileProgressPollingRef.current) {
                clearInterval(fileProgressPollingRef.current)
            }
        }
    }, [])

    useEffect(() => {
        fileProgressPageRef.current = fileProgressPage
        fileProgressPageSizeRef.current = fileProgressPageSize
    }, [fileProgressPage, fileProgressPageSize])

    const handleTableChange = (pagination: TablePaginationConfig) => {
        setPage(pagination.current || 1)
        setPageSize(pagination.pageSize || 10)
    }

    const handleAdd = () => {
        setEditingBase(null)
        setModalOpen(true)
    }

    const handleEdit = (record: KnowledgeBase) => {
        setEditingBase(record)
        setModalOpen(true)
    }

    const handleDelete = (record: KnowledgeBase) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除知识库 "${record.name}" 吗？`,
            okText: '确认',
            cancelText: '取消',
            okButtonProps: { danger: true },
            className: 'knowledge-base-confirm',
            onOk: async () => {
                try {
                    await knowledgeBaseApi.delete(record.id)
                    message.success('删除成功')
                    fetchKnowledgeBases()
                } catch (error) {
                    if (error instanceof Error) {
                        message.error(error.message)
                    }
                }
            },
        })
    }

    const handleToggleEnabled = async (record: KnowledgeBase, enabled: boolean) => {
        try {
            await knowledgeBaseApi.toggleEnabled(record.id, enabled)
            message.success(enabled ? '已启用' : '已禁用')
            fetchKnowledgeBases()
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }

    const handleCreate = async (dto: KnowledgeBaseDTO) => {
        await knowledgeBaseApi.create(dto)
    }

    const handleUpdate = async (id: number, dto: KnowledgeBaseDTO) => {
        await knowledgeBaseApi.update(id, dto)
    }

    const handleModalSuccess = () => {
        setModalOpen(false)
        setEditingBase(null)
        fetchKnowledgeBases()
    }

    const handleModalCancel = () => {
        setModalOpen(false)
        setEditingBase(null)
    }

    const handleOpenUpload = (record: KnowledgeBase) => {
        setUploadingBase(record)
        setFileList([])
        setCurrentJob(null)
        setUploadOpen(true)
    }

    const handleUploadCancel = () => {
        setUploadOpen(false)
        setUploadingBase(null)
        setFileList([])
        setCurrentJob(null)
        if (pollingRef.current) {
            clearInterval(pollingRef.current)
            pollingRef.current = null
        }
    }

    const handleOpenFileProgress = (record: KnowledgeBase) => {
        setFileProgressBase(record)
        setFileProgressOpen(true)
        setFileProgressList([])
        setFileProgressTotal(0)
        setFileProgressPage(1)
        setFileProgressPageSize(10)
    }

    const handleFileProgressCancel = () => {
        setFileProgressOpen(false)
        setFileProgressBase(null)
        setFileProgressList([])
        setFileProgressTotal(0)
        if (fileProgressPollingRef.current) {
            clearInterval(fileProgressPollingRef.current)
            fileProgressPollingRef.current = null
        }
    }

    const fetchFileProgress = useCallback(
        async (
            targetPage = fileProgressPageRef.current,
            targetPageSize = fileProgressPageSizeRef.current,
        ) => {
            if (!fileProgressBase) {
                return
            }
            setFileProgressLoading(true)
            try {
                const result = await knowledgeBaseApi.getFileProgressPage(
                    fileProgressBase.id,
                    targetPage,
                    targetPageSize,
                )
                setFileProgressList(result.list)
                setFileProgressTotal(result.total)
                setFileProgressPage(result.page)
                setFileProgressPageSize(result.pageSize)
                const hasInProgress = result.list.some(
                    (item) => item.status === 'IN_PROGRESS' || item.status === 'QUEUED',
                )
                if (fileProgressOpen && hasInProgress && !fileProgressPollingRef.current) {
                    fileProgressPollingRef.current = window.setInterval(() => {
                        fetchFileProgress(fileProgressPageRef.current, fileProgressPageSizeRef.current)
                    }, 5000)
                }
                if ((!fileProgressOpen || !hasInProgress) && fileProgressPollingRef.current) {
                    clearInterval(fileProgressPollingRef.current)
                    fileProgressPollingRef.current = null
                }
            } catch (error) {
                if (error instanceof Error) {
                    message.error(error.message)
                }
            } finally {
                setFileProgressLoading(false)
            }
        },
        [fileProgressBase, fileProgressOpen],
    )

    useEffect(() => {
        if (fileProgressOpen && fileProgressBase) {
            fetchFileProgress(1, fileProgressPageSize)
        }
    }, [fileProgressOpen, fileProgressBase, fileProgressPageSize, fetchFileProgress])

    const startPolling = (baseId: number, jobId: number) => {
        if (pollingRef.current) {
            clearInterval(pollingRef.current)
        }
        pollingRef.current = window.setInterval(async () => {
            try {
                const job = await knowledgeBaseApi.getJobStatus(baseId, jobId)
                setCurrentJob(job)
                if (job.status === 'COMPLETED' || job.status === 'FAILED') {
                    if (pollingRef.current) {
                        clearInterval(pollingRef.current)
                        pollingRef.current = null
                    }
                }
            } catch (error) {
                if (error instanceof Error) {
                    message.error(error.message)
                }
            }
        }, 2000)
    }

    const handleUpload = async () => {
        if (!uploadingBase) {
            return
        }
        if (fileList.length === 0) {
            message.warning('请先选择文件')
            return
        }
        try {
            const files = fileList.map((file) => file.originFileObj as File)
            const job = await knowledgeBaseApi.uploadFiles(uploadingBase.id, files)
            setCurrentJob(job)
            startPolling(uploadingBase.id, job.id)
            message.success('已提交解析任务')
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        }
    }

    const validateFile = (file: File) => {
        const ext = file.name.split('.').pop()?.toLowerCase()
        if (!ext || !['txt', 'md', 'docx'].includes(ext)) {
            message.error('仅支持 txt、md、docx 文件')
            return Upload.LIST_IGNORE
        }
        if (file.size > 10 * 1024 * 1024) {
            message.error('文件大小不能超过 10MB')
            return Upload.LIST_IGNORE
        }
        return false
    }

    const handleFileProgressTableChange = (pagination: TablePaginationConfig) => {
        const nextPage = pagination.current || 1
        const nextPageSize = pagination.pageSize || 10
        setFileProgressPage(nextPage)
        setFileProgressPageSize(nextPageSize)
        fetchFileProgress(nextPage, nextPageSize)
    }

    const formatFileSize = (size?: number) => {
        if (size === undefined || size === null) {
            return '-'
        }
        if (size < 1024) {
            return `${size} B`
        }
        if (size < 1024 * 1024) {
            return `${(size / 1024).toFixed(1)} KB`
        }
        return `${(size / (1024 * 1024)).toFixed(1)} MB`
    }

    const jobStatusTag = (status?: KnowledgeIngestionJob['status']) => {
        if (!status) {
            return null
        }
        switch (status) {
            case 'COMPLETED':
                return <Tag color="green">完成</Tag>
            case 'FAILED':
                return <Tag color="red">失败</Tag>
            case 'IN_PROGRESS':
                return <Tag color="blue">处理中</Tag>
            default:
                return <Tag color="gold">排队中</Tag>
        }
    }

    const managementColumns: ColumnsType<KnowledgeBase> = [
        {
            title: '名称',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
            render: (text?: string) => text || '-',
        },
        {
            title: '状态',
            dataIndex: 'isEnabled',
            key: 'isEnabled',
            width: 120,
            render: (enabled: boolean, record: KnowledgeBase) => (
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
            render: (text: string) => (text ? new Date(text).toLocaleString('zh-CN') : '-'),
        },
        {
            title: '操作',
            key: 'actions',
            width: 280,
            render: (_: unknown, record: KnowledgeBase) => (
                <Space>
                    <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
                        编辑
                    </Button>
                    <Button type="link" icon={<FileSearchOutlined />} onClick={() => handleOpenFileProgress(record)}>
                        文件进度
                    </Button>
                    <Button type="link" icon={<UploadOutlined />} onClick={() => handleOpenUpload(record)}>
                        上传文件
                    </Button>
                    <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
                        删除
                    </Button>
                </Space>
            ),
        },
    ]

    const fileProgressColumns: ColumnsType<KnowledgeIngestionFile> = [
        {
            title: '文件名',
            dataIndex: 'fileName',
            key: 'fileName',
            render: (text: string) => text || '-',
        },
        {
            title: '大小',
            dataIndex: 'sizeBytes',
            key: 'sizeBytes',
            width: 120,
            render: (size?: number) => formatFileSize(size),
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 120,
            render: (status: KnowledgeIngestionFile['status']) => jobStatusTag(status),
        },
        {
            title: '进度',
            dataIndex: 'progressPercent',
            key: 'progressPercent',
            width: 200,
            render: (percent: number, record: KnowledgeIngestionFile) => {
                if (record.status === 'FAILED') {
                    return <Tag color="red">失败</Tag>
                }
                let status: 'success' | 'exception' | 'active' = 'active'
                if (record.status === 'COMPLETED' || percent === 100) {
                    status = 'success'
                }
                return <Progress percent={percent} size="small" status={status} />
            },
        },
        {
            title: '上传时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
            width: 180,
            render: (text: string) => (text ? new Date(text).toLocaleString('zh-CN') : '-'),
        },
    ]

    return (
        <div className="knowledge-base-page knowledge-base-management-page">
            <div className="knowledge-base-container">
                <div className="knowledge-base-header">
                    <div className="knowledge-base-title">
                        <h2>知识库管理</h2>
                    </div>
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                        新建知识库
                    </Button>
                </div>
                <Table
                    columns={managementColumns}
                    dataSource={knowledgeBases}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                        current: page,
                        pageSize,
                        total,
                        showSizeChanger: true,
                        showQuickJumper: true,
                        showTotal: (count) => `共 ${count} 条`,
                    }}
                    onChange={handleTableChange}
                />
            </div>

            <KnowledgeBaseFormModal
                open={modalOpen}
                editingBase={editingBase}
                onCancel={handleModalCancel}
                onSuccess={handleModalSuccess}
                onCreate={handleCreate}
                onUpdate={handleUpdate}
            />

            <Modal
                title={uploadingBase ? `上传文件 - ${uploadingBase.name}` : '上传文件'}
                open={uploadOpen}
                onOk={handleUpload}
                onCancel={handleUploadCancel}
                okText="开始解析"
                cancelText="关闭"
                className="knowledge-base-modal"
            >
                <div className="knowledge-base-upload">
                    <Upload
                        multiple
                        beforeUpload={validateFile}
                        fileList={fileList}
                        onChange={({ fileList: newList }) => setFileList(newList)}
                        className="knowledge-base-upload-list"
                    >
                        <Button icon={<UploadOutlined />}>选择文件</Button>
                    </Upload>
                    <p className="knowledge-base-upload-tip">
                        支持 txt / md / docx，单文件不超过 10MB，可批量上传。
                    </p>
                </div>

                {currentJob && (
                    <div className="knowledge-base-job-status">
                        <div className="knowledge-base-job-header">
                            <span>解析状态</span>
                            {jobStatusTag(currentJob.status)}
                        </div>
                        <Progress
                            percent={currentJob.progressPercent}
                            status={currentJob.status === 'FAILED' ? 'exception' : 'active'}
                            showInfo
                        />
                        {currentJob.errorMessage && (
                            <div className="knowledge-base-job-error">{currentJob.errorMessage}</div>
                        )}
                    </div>
                )}
            </Modal>

            <Modal
                title={fileProgressBase ? `文件进度 - ${fileProgressBase.name}` : '文件进度'}
                open={fileProgressOpen}
                onCancel={handleFileProgressCancel}
                footer={null}
                className="knowledge-base-modal"
            >
                <Table
                    columns={fileProgressColumns}
                    dataSource={fileProgressList}
                    rowKey="id"
                    loading={fileProgressLoading}
                    pagination={{
                        current: fileProgressPage,
                        pageSize: fileProgressPageSize,
                        total: fileProgressTotal,
                        showSizeChanger: true,
                        showQuickJumper: true,
                        showTotal: (count) => `共 ${count} 条`,
                    }}
                    onChange={handleFileProgressTableChange}
                />
            </Modal>
        </div>
    )
}

export default KnowledgeBaseManagementPage
