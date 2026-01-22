/**
 * AI Model type definitions
 */

export interface AiModel {
    id: number
    modelName: string
    provider: string
    isEnabled: boolean
    createdBy: string
    updatedBy: string
    createdAt: string
    updatedAt: string
}

export interface AiModelDTO {
    modelName: string
    provider: string
    isEnabled?: boolean
}

// Re-export PageResult from api.ts for convenience
export type { PageResult } from './api'
