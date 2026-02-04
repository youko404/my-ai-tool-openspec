import http from './http'
import type { ProviderConfig, ProviderConfigDTO } from '../types/provider-config'

/**
 * Provider configuration API service
 */
export const providerConfigApi = {
    getByProvider(provider: string): Promise<ProviderConfig> {
        return http.get<ProviderConfig>(`/provider-configs/${provider}`)
    },

    save(dto: ProviderConfigDTO): Promise<ProviderConfig> {
        return http.post<ProviderConfig>('/provider-configs', dto)
    },
}

export default providerConfigApi
