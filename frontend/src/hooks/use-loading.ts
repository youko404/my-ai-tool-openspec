import { useState, useCallback } from 'react'

interface UseLoadingReturn {
    loading: boolean
    withLoading: <T>(fn: () => Promise<T>) => Promise<T>
}

/**
 * Hook for managing loading state
 */
export function useLoading(initialState = false): UseLoadingReturn {
    const [loading, setLoading] = useState(initialState)

    const withLoading = useCallback(async <T>(fn: () => Promise<T>): Promise<T> => {
        setLoading(true)
        try {
            return await fn()
        } finally {
            setLoading(false)
        }
    }, [])

    return { loading, withLoading }
}
