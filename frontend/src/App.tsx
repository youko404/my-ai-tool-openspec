import { useEffect, useState } from 'react'
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import { Layout } from 'antd'
import TabNavigation, { TabKey } from './components/tab-navigation'
import HomePage from './pages/home-page'
import KnowledgeBaseManagementPage from './pages/knowledge-base-management-page'
import './App.css'

const { Header, Content, Footer } = Layout

function AppLayout() {
    const location = useLocation()
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
    const [activeTab, setActiveTab] = useState<TabKey>('chat')
    const showTabs = location.pathname === '/'

    return (
        <Layout className="app-layout">
            <Header className="app-header">
                <div className="logo">
                    <span className="logo-icon">🤖</span>
                    <span className="logo-text">AI Content Platform</span>
                </div>
                {showTabs && (
                    <div className="header-tabs">
                        <TabNavigation activeTab={activeTab} onTabChange={setActiveTab} />
                    </div>
                )}
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
                    <Route path="/" element={<HomePage activeTab={activeTab} />} />
                    <Route path="/knowledge-base/management" element={<KnowledgeBaseManagementPage />} />
                </Routes>
            </Content>
            <Footer className="app-footer">
                AI Content Management Platform ©2025
            </Footer>
        </Layout>
    )
}

function App() {
    return (
        <BrowserRouter>
            <AppLayout />
        </BrowserRouter>
    )
}

export default App
