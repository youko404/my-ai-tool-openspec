import React, { useEffect } from 'react';
import { Modal, Form, Input, Switch, message } from 'antd';
import { Workflow, WorkflowDTO } from '../../types/workflow';
import { createWorkflow, updateWorkflow } from '../../services/workflow-api';

interface WorkflowFormModalProps {
  visible: boolean;
  mode: 'create' | 'edit';
  workflow?: Workflow;
  onSuccess: () => void;
  onCancel: () => void;
}

const WorkflowFormModal: React.FC<WorkflowFormModalProps> = ({
  visible,
  mode,
  workflow,
  onSuccess,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    if (visible) {
      if (mode === 'edit' && workflow) {
        form.setFieldsValue({
          name: workflow.name,
          description: workflow.description,
          enabled: workflow.enabled,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ enabled: true });
      }
    }
  }, [visible, mode, workflow, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const dto: WorkflowDTO = {
        name: values.name,
        description: values.description,
        enabled: values.enabled,
      };

      if (mode === 'create') {
        await createWorkflow(dto);
        message.success('工作流创建成功');
      } else if (mode === 'edit' && workflow) {
        await updateWorkflow(workflow.id, dto);
        message.success('工作流更新成功');
      }

      form.resetFields();
      onSuccess();
    } catch (error: any) {
      if (error.response) {
        message.error(error.response.data.message || '操作失败');
      } else if (error.errorFields) {
        // Form validation error
        message.error('请检查表单输入');
      } else {
        message.error('操作失败');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title={mode === 'create' ? '新增工作流' : '编辑工作流'}
      open={visible}
      onOk={handleSubmit}
      onCancel={handleCancel}
      confirmLoading={loading}
      destroyOnClose
      className="workflow-page-modal"
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{ enabled: true }}
      >
        <Form.Item
          name="name"
          label="工作流名称"
          rules={[
            { required: true, message: '请输入工作流名称' },
            { max: 255, message: '名称长度不能超过255个字符' },
          ]}
        >
          <Input placeholder="请输入工作流名称" />
        </Form.Item>

        <Form.Item
          name="description"
          label="描述"
          rules={[{ max: 1000, message: '描述长度不能超过1000个字符' }]}
        >
          <Input.TextArea
            placeholder="请输入工作流描述（可选）"
            rows={4}
          />
        </Form.Item>

        <Form.Item
          name="enabled"
          label="启用状态"
          valuePropName="checked"
        >
          <Switch />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default WorkflowFormModal;
