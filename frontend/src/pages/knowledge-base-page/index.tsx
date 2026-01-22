import { useCallback, useEffect, useState } from 'react'
import { Button, Col, Form, Input, InputNumber, Row, Select, Table, message } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { SearchOutlined, SettingOutlined } from '@ant-design/icons'
import { knowledgeBaseApi } from '../../services/knowledge-base-api'
import type { KnowledgeBase, KnowledgeChunkSearchResult } from '../../types/knowledge-base'
import './knowledge-base-page.css'

type KnowledgeChunkSearchRow = KnowledgeChunkSearchResult & { key: string }

function KnowledgeBasePage() {
    const [allKnowledgeBases, setAllKnowledgeBases] = useState<KnowledgeBase[]>([])
    const [baseOptionsLoading, setBaseOptionsLoading] = useState(false)

    const [searchBaseId, setSearchBaseId] = useState<number | undefined>(undefined)
    const [queryText, setQueryText] = useState('')
    const [minScore, setMinScore] = useState(0.7)
    const [limit, setLimit] = useState(5)
    const [searchLoading, setSearchLoading] = useState(false)
    const [searchResults, setSearchResults] = useState<KnowledgeChunkSearchRow[]>([])
    const [expandedRowKeys, setExpandedRowKeys] = useState<(string | number)[]>([])

    const fetchAllKnowledgeBases = useCallback(async () => {
        setBaseOptionsLoading(true)
        try {
            const result = await knowledgeBaseApi.getAll()
            setAllKnowledgeBases(result)
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setBaseOptionsLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchAllKnowledgeBases()
    }, [fetchAllKnowledgeBases])

    const handleOpenManagement = () => {
        window.open('/knowledge-base/management', '_blank', 'noopener,noreferrer')
    }

    const handleSearch = async () => {
        if (!searchBaseId) {
            message.warning('请先选择知识库')
            return
        }
        const trimmedQuery = queryText.trim()
        if (!trimmedQuery) {
            message.warning('请输入搜索文本')
            return
        }
        setSearchLoading(true)
        try {
            const result = await knowledgeBaseApi.searchChunks(searchBaseId, {
                queryText: trimmedQuery,
                minScore,
                limit,
            })
            const sorted = [...result].sort((a, b) => (b.score || 0) - (a.score || 0))
            const rows = sorted.map((item, index) => ({
                ...item,
                key: `${item.fileName}-${item.chunkIndex}-${index}`,
            }))
            setSearchResults(rows)
            setExpandedRowKeys(rows.length > 0 ? [rows[0].key] : [])
        } catch (error) {
            if (error instanceof Error) {
                message.error(error.message)
            }
        } finally {
            setSearchLoading(false)
        }
    }

    const handleToggleExpand = (key: string) => {
        setExpandedRowKeys((prev) => {
            if (prev.includes(key)) {
                return prev.filter((item) => item !== key)
            }
            return [...prev, key]
        })
    }

    const searchColumns: ColumnsType<KnowledgeChunkSearchRow> = [
        {
            title: '来源文件名',
            dataIndex: 'fileName',
            key: 'fileName',
            width: 220,
            render: (text: string) => text || '-',
        },
        {
            title: 'chunkIndex',
            dataIndex: 'chunkIndex',
            key: 'chunkIndex',
            width: 120,
        },
        {
            title: '分数',
            dataIndex: 'score',
            key: 'score',
            width: 120,
            render: (score?: number) => (typeof score === 'number' ? score.toFixed(4) : '-'),
        },
        {
            title: '分片内容',
            dataIndex: 'content',
            key: 'content',
            render: (text: string, record) => {
                if (!text) {
                    return '-'
                }
                const isExpanded = expandedRowKeys.includes(record.key)
                return (
                    <div className="knowledge-base-content-cell">
                        <span className="knowledge-base-chunk-preview">{text}</span>
                        <Button
                            type="link"
                            size="small"
                            className="knowledge-base-content-toggle"
                            onClick={() => handleToggleExpand(record.key)}
                        >
                            {isExpanded ? '收起' : '展开'}
                        </Button>
                    </div>
                )
            },
        },
    ]

    return (
        <div className="knowledge-base-page">
            <div className="knowledge-base-container">
                <div className="knowledge-base-header">
                    <div className="knowledge-base-title">
                        <h2>知识库查询</h2>
                    </div>
                    <Button
                        icon={<SettingOutlined />}
                        onClick={handleOpenManagement}
                        className="knowledge-base-manage-button"
                    >
                        知识库管理
                    </Button>
                </div>

                <div className="knowledge-base-search-panel">
                    <Form layout="vertical" className="knowledge-base-search-form">
                        <Row gutter={[16, 16]}>
                            <Col xs={24} md={12} lg={6} xxl={5}>
                                <Form.Item label="知识库" required>
                                    <Select
                                        value={searchBaseId}
                                        options={allKnowledgeBases.map((base) => ({
                                            value: base.id,
                                            label: base.name,
                                        }))}
                                        placeholder="请选择知识库"
                                        loading={baseOptionsLoading}
                                        showSearch
                                        allowClear
                                        optionFilterProp="label"
                                        dropdownClassName="knowledge-base-select-dropdown"
                                        onChange={(value) => setSearchBaseId(value ?? undefined)}
                                    />
                                </Form.Item>
                            </Col>
                            <Col xs={24} md={12} lg={6} xxl={5}>
                                <Form.Item label="搜索文本" required>
                                    <Input
                                        value={queryText}
                                        placeholder="请输入查询文本"
                                        onChange={(event) => setQueryText(event.target.value)}
                                        className="knowledge-base-search-input"
                                    />
                                </Form.Item>
                            </Col>
                            <Col xs={12} md={8} lg={4} xxl={4}>
                                <Form.Item label="分数阈值">
                                    <InputNumber
                                        min={0}
                                        max={1}
                                        step={0.05}
                                        value={minScore}
                                        onChange={(value) => setMinScore(value ?? 0)}
                                        className="knowledge-base-search-number"
                                    />
                                </Form.Item>
                            </Col>
                            <Col xs={12} md={8} lg={4} xxl={4}>
                                <Form.Item label="数量">
                                    <InputNumber
                                        min={1}
                                        max={50}
                                        step={1}
                                        value={limit}
                                        onChange={(value) => setLimit(value ?? 1)}
                                        className="knowledge-base-search-number"
                                    />
                                </Form.Item>
                            </Col>
                            <Col xs={24} md={8} lg={4} xxl={4} className="knowledge-base-search-actions">
                                <Button
                                    type="primary"
                                    icon={<SearchOutlined />}
                                    loading={searchLoading}
                                    onClick={handleSearch}
                                >
                                    查询
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </div>

                <div className="knowledge-base-results">
                    <Table
                        columns={searchColumns}
                        dataSource={searchResults}
                        rowKey="key"
                        loading={searchLoading}
                        pagination={false}
                        expandable={{
                            expandedRowKeys,
                            onExpandedRowsChange: (keys) => setExpandedRowKeys([...keys]),
                            expandedRowRender: (record) => (
                                <div className="knowledge-base-chunk-content">{record.content}</div>
                            ),
                            rowExpandable: (record) => Boolean(record.content),
                        }}
                    />
                </div>
            </div>
        </div>
    )
}

export default KnowledgeBasePage
