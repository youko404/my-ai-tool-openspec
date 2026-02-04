/**
 * Workflow entity type
 */
export interface Workflow {
  id: number;
  name: string;
  description?: string;
  enabled: boolean;
  deleted: boolean;
  creator?: string;
  editor?: string;
  createdBy?: string;
  updatedBy?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Workflow DTO for create/update operations
 */
export interface WorkflowDTO {
  name: string;
  description?: string;
  enabled?: boolean;
}

/**
 * Paginated workflow list response
 */
export interface WorkflowListResponse {
  list: Workflow[];
  total: number;
  page: number;
  pageSize: number;
}
