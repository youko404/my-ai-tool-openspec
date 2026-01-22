/**
 * Knowledge base type definitions
 */

export interface KnowledgeBase {
    id: number
    name: string
    description?: string
    isEnabled: boolean
    createdAt: string
    updatedAt: string
}

export interface KnowledgeBaseDTO {
    name: string
    description?: string
    isEnabled?: boolean
}

export interface KnowledgeIngestionJob {
    id: number
    knowledgeBaseId: number
    status: 'QUEUED' | 'IN_PROGRESS' | 'FAILED' | 'COMPLETED'
    totalChunks: number
    processedChunks: number
    progressPercent: number
    errorMessage?: string
    createdAt: string
    updatedAt: string
}

export interface KnowledgeIngestionFile {
    id: number
    knowledgeBaseId: number
    jobId: number
    fileName: string
    contentType?: string
    sizeBytes?: number
    status: 'QUEUED' | 'IN_PROGRESS' | 'FAILED' | 'COMPLETED'
    totalChunks: number
    processedChunks: number
    progressPercent: number
    createdAt: string
}

export interface KnowledgeChunkSearchRequest {
    queryText: string
    minScore: number
    limit: number
}

export interface KnowledgeChunkSearchResult {
    fileName: string
    chunkIndex: number
    content: string
    score: number
}

export type { PageResult } from './api'
