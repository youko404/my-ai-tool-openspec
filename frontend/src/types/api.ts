/**
 * API Response wrapper type
 */
export interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

/**
 * Pagination response type
 */
export interface PageResult<T> {
    list: T[]
    total: number
    page: number
    pageSize: number
}

/**
 * Pagination query params
 */
export interface PageQuery {
    page?: number
    pageSize?: number
}
