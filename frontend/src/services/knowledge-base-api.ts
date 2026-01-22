import http from './http'
import type {
    KnowledgeBase,
    KnowledgeBaseDTO,
    KnowledgeChunkSearchRequest,
    KnowledgeChunkSearchResult,
    KnowledgeIngestionFile,
    KnowledgeIngestionJob,
    PageResult,
} from '../types/knowledge-base'

/**
 * Knowledge base API service
 */
export const knowledgeBaseApi = {
    getAll(): Promise<KnowledgeBase[]> {
        return http.get<KnowledgeBase[]>('/knowledge-bases')
    },

    getPage(page: number, pageSize: number): Promise<PageResult<KnowledgeBase>> {
        return http.get<PageResult<KnowledgeBase>>('/knowledge-bases/page', {
            params: { page, pageSize },
        })
    },

    getById(id: number): Promise<KnowledgeBase> {
        return http.get<KnowledgeBase>(`/knowledge-bases/${id}`)
    },

    create(dto: KnowledgeBaseDTO): Promise<KnowledgeBase> {
        return http.post<KnowledgeBase>('/knowledge-bases', dto)
    },

    update(id: number, dto: KnowledgeBaseDTO): Promise<KnowledgeBase> {
        return http.put<KnowledgeBase>(`/knowledge-bases/${id}`, dto)
    },

    delete(id: number): Promise<void> {
        return http.delete<void>(`/knowledge-bases/${id}`)
    },

    toggleEnabled(id: number, enabled: boolean): Promise<KnowledgeBase> {
        return http.patch<KnowledgeBase>(`/knowledge-bases/${id}/toggle`, { enabled })
    },

    uploadFiles(id: number, files: File[]): Promise<KnowledgeIngestionJob> {
        const formData = new FormData()
        files.forEach((file) => formData.append('files', file))
        return http.post<KnowledgeIngestionJob>(`/knowledge-bases/${id}/files`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
    },

    getJobStatus(id: number, jobId: number): Promise<KnowledgeIngestionJob> {
        return http.get<KnowledgeIngestionJob>(`/knowledge-bases/${id}/jobs/${jobId}`)
    },

    getFileProgressPage(id: number, page: number, pageSize: number): Promise<PageResult<KnowledgeIngestionFile>> {
        return http.get<PageResult<KnowledgeIngestionFile>>(`/knowledge-bases/${id}/files`, {
            params: { page, pageSize },
        })
    },

    searchChunks(id: number, payload: KnowledgeChunkSearchRequest): Promise<KnowledgeChunkSearchResult[]> {
        return http.post<KnowledgeChunkSearchResult[]>(`/knowledge-bases/${id}/search`, payload)
    },
}

export default knowledgeBaseApi
