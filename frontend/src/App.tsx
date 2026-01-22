import { useEffect, useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Layout } from 'antd'
import HomePage from './pages/home-page'
import KnowledgeBaseManagementPage from './pages/knowledge-base-management-page'
import './App.css'

const { Header, Content, Footer } = Layout

function App() {
    const [theme, setTheme] = useState<'light' | 'dark'>(() => {
        const stored = localStorage.getItem('theme')
        if (stored === 'light' || stored === 'dark') {
            return stored
        }
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            return 'dark'
        }
        return 'light'
    })

    useEffect(() => {
        document.documentElement.setAttribute('data-theme', theme)
        localStorage.setItem('theme', theme)
    }, [theme])

    const nextTheme = theme === 'dark' ? 'light' : 'dark'

    return (
        <BrowserRouter>
            <Layout className="app-layout">
                <Header className="app-header">
                    <div className="logo">
                        <span className="logo-icon">🤖</span>
                        <span className="logo-text">AI Content Platform</span>
                    </div>
                    <div className="header-actions">
                        <button
                            className="theme-toggle"
                            type="button"
                            onClick={() => setTheme(nextTheme)}
                            aria-label={`Switch to ${nextTheme} mode`}
                            title={`Switch to ${nextTheme} mode`}
                        >
                            {theme === 'dark' ? 'Dark' : 'Light'}
                        </button>
                    </div>
                </Header>
                <Content className="app-content">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/knowledge-base/management" element={<KnowledgeBaseManagementPage />} />
                    </Routes>
                </Content>
                <Footer className="app-footer">
                    AI Content Management Platform ©2025
                </Footer>
            </Layout>
        </BrowserRouter>
    )
}

export default App
