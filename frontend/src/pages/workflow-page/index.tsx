import React, { useEffect, useState } from 'react';
import { Table, Button, Input, Space, message, Modal } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, CopyOutlined, SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { Workflow } from '../../types/workflow';
import { getWorkflows, deleteWorkflow, copyWorkflow } from '../../services/workflow-api';
import WorkflowFormModal from '../../components/workflow-form-modal';
import './workflow-page.css';

const WorkflowPage: React.FC = () => {
  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [searchValue, setSearchValue] = useState('');

  const [modalVisible, setModalVisible] = useState(false);
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create');
  const [currentWorkflow, setCurrentWorkflow] = useState<Workflow | undefined>();

  // Load workflows
  const loadWorkflows = async () => {
    try {
      setLoading(true);
      const response = await getWorkflows(page, pageSize, keyword || undefined);
      setWorkflows(response.list);
      setTotal(response.total);
    } catch (error: any) {
      message.error(error.response?.data?.message || '加载工作流列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadWorkflows();
  }, [page, pageSize, keyword]);

  // Handle search
  const handleSearch = () => {
    setKeyword(searchValue);
    setPage(1); // Reset to first page when searching
  };

  const handleSearchClear = () => {
    setSearchValue('');
    setKeyword('');
    setPage(1);
  };

  // Handle create
  const handleCreate = () => {
    setModalMode('create');
    setCurrentWorkflow(undefined);
    setModalVisible(true);
  };

  // Handle edit
  const handleEdit = (workflow: Workflow) => {
    setModalMode('edit');
    setCurrentWorkflow(workflow);
    setModalVisible(true);
  };

  // Handle delete
  const handleDelete = (workflow: Workflow) => {
    Modal.confirm({
      title: '删除确认',
      content: `确认删除工作流 "${workflow.name}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      className: 'workflow-page-modal',
      onOk: async () => {
        try {
          await deleteWorkflow(workflow.id);
          message.success('删除成功');
          loadWorkflows();
        } catch (error: any) {
          message.error(error.response?.data?.message || '删除失败');
        }
      },
    });
  };

  // Handle copy
  const handleCopy = async (workflow: Workflow) => {
    try {
      await copyWorkflow(workflow.id);
      message.success('复制成功');
      loadWorkflows();
    } catch (error: any) {
      message.error(error.response?.data?.message || '复制失败');
    }
  };

  // Handle modal success
  const handleModalSuccess = () => {
    setModalVisible(false);
    loadWorkflows();
  };

  // Handle modal cancel
  const handleModalCancel = () => {
    setModalVisible(false);
  };

  // Table columns
  const columns: ColumnsType<Workflow> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: '25%',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: '45%',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: '30%',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            icon={<CopyOutlined />}
            onClick={() => handleCopy(record)}
          >
            复制
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
  ];

  return (
    <div className="workflow-page">
      <div className="workflow-header">
        <h2>工作流管理</h2>
        <Space>
          <Input
            placeholder="搜索工作流名称或描述"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onPressEnter={handleSearch}
            style={{ width: 300 }}
            allowClear
            onClear={handleSearchClear}
          />
          <Button
            type="default"
            icon={<SearchOutlined />}
            onClick={handleSearch}
          >
            搜索
          </Button>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            新增工作流
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={workflows}
        rowKey="id"
        loading={loading}
        pagination={{
          current: page,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            setPage(page);
            setPageSize(pageSize);
          },
        }}
        locale={{
          emptyText: '暂无工作流',
        }}
      />

      <WorkflowFormModal
        visible={modalVisible}
        mode={modalMode}
        workflow={currentWorkflow}
        onSuccess={handleModalSuccess}
        onCancel={handleModalCancel}
      />
    </div>
  );
};

export default WorkflowPage;
