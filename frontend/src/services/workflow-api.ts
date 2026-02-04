import axios from 'axios';
import { Workflow, WorkflowDTO, WorkflowListResponse } from '../types/workflow';

const API_BASE_URL = '/api/workflows';

/**
 * Get workflows with pagination and optional keyword search
 */
export const getWorkflows = async (
  page: number = 1,
  pageSize: number = 10,
  keyword?: string
): Promise<WorkflowListResponse> => {
  const params: any = { page, pageSize };
  if (keyword) {
    params.keyword = keyword;
  }
  const response = await axios.get(API_BASE_URL, { params });
  return response.data.data;
};

/**
 * Get a single workflow by ID
 */
export const getWorkflow = async (id: number): Promise<Workflow> => {
  const response = await axios.get(`${API_BASE_URL}/${id}`);
  return response.data.data;
};

/**
 * Create a new workflow
 */
export const createWorkflow = async (data: WorkflowDTO): Promise<Workflow> => {
  const response = await axios.post(API_BASE_URL, data);
  return response.data.data;
};

/**
 * Update an existing workflow
 */
export const updateWorkflow = async (id: number, data: WorkflowDTO): Promise<Workflow> => {
  const response = await axios.put(`${API_BASE_URL}/${id}`, data);
  return response.data.data;
};

/**
 * Delete a workflow (soft delete)
 */
export const deleteWorkflow = async (id: number): Promise<void> => {
  await axios.delete(`${API_BASE_URL}/${id}`);
};

/**
 * Copy an existing workflow
 */
export const copyWorkflow = async (id: number): Promise<Workflow> => {
  const response = await axios.post(`${API_BASE_URL}/${id}/copy`);
  return response.data.data;
};
