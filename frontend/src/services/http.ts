import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'

/**
 * API Response wrapper type
 */
interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

/**
 * HTTP Client instance with default configuration
 */
const httpClient: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Request interceptor
httpClient.interceptors.request.use(
    (config) => {
        // Add auth token if available
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// Response interceptor
httpClient.interceptors.response.use(
    (response: AxiosResponse<ApiResponse<unknown>>) => {
        const { data } = response
        if (data.code !== 200) {
            return Promise.reject(new Error(data.message || '请求失败'))
        }
        return response
    },
    (error) => {
        const message = error.response?.data?.message || error.message || '网络错误'
        return Promise.reject(new Error(message))
    }
)

/**
 * HTTP request methods
 */
export const http = {
    get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return httpClient.get(url, config).then((res) => res.data.data as T)
    },

    post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
        return httpClient.post(url, data, config).then((res) => res.data.data as T)
    },

    put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
        return httpClient.put(url, data, config).then((res) => res.data.data as T)
    },

    delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return httpClient.delete(url, config).then((res) => res.data.data as T)
    },

    patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
        return httpClient.patch(url, data, config).then((res) => res.data.data as T)
    },
}

export default http
