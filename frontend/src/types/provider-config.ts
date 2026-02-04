export interface ProviderConfig {
    provider: string
    apiBaseUrl?: string
    apiKeyConfigured?: boolean
}

export interface ProviderConfigDTO {
    provider: string
    apiBaseUrl: string
    apiKey: string
}
