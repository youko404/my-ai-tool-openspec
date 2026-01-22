import http from './http'
import type { AiModel, AiModelDTO, PageResult } from '../types/model'

/**
 * Model API service
 */
export const modelApi = {
    /**
     * Get all models
     */
    getAll(): Promise<AiModel[]> {
        return http.get<AiModel[]>('/models')
    },

    /**
     * Get models with pagination
     */
    getPage(page: number, pageSize: number): Promise<PageResult<AiModel>> {
        return http.get<PageResult<AiModel>>('/models/page', {
            params: { page, pageSize },
        })
    },

    /**
     * Search models by keyword
     */
    search(keyword: string, page: number, pageSize: number): Promise<PageResult<AiModel>> {
        return http.get<PageResult<AiModel>>('/models/search', {
            params: { keyword, page, pageSize },
        })
    },

    /**
     * Get model by ID
     */
    getById(id: number): Promise<AiModel> {
        return http.get<AiModel>(`/models/${id}`)
    },

    /**
     * Create a new model
     */
    create(dto: AiModelDTO): Promise<AiModel> {
        return http.post<AiModel>('/models', dto)
    },

    /**
     * Update an existing model
     */
    update(id: number, dto: AiModelDTO): Promise<AiModel> {
        return http.put<AiModel>(`/models/${id}`, dto)
    },

    /**
     * Delete a model
     */
    delete(id: number): Promise<void> {
        return http.delete<void>(`/models/${id}`)
    },

    /**
     * Toggle model enabled status
     */
    toggleEnabled(id: number, enabled: boolean): Promise<AiModel> {
        return http.patch<AiModel>(`/models/${id}/toggle`, { enabled })
    },
}

export default modelApi
