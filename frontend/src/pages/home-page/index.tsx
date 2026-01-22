import { useState } from 'react'
import TabNavigation, { TabKey } from '../../components/tab-navigation'
import WorkflowPage from '../workflow-page'
import ModelPage from '../model-page'
import KnowledgeBasePage from '../knowledge-base-page'
import ToolPage from '../tool-page'
import SkillsPage from '../skills-page'
import './home-page.css'

function HomePage() {
    const [activeTab, setActiveTab] = useState<TabKey>('workflow')

    const renderContent = () => {
        switch (activeTab) {
            case 'workflow':
                return <WorkflowPage />
            case 'model':
                return <ModelPage />
            case 'knowledge-base':
                return <KnowledgeBasePage />
            case 'tool':
                return <ToolPage />
            case 'skills':
                return <SkillsPage />
            default:
                return <WorkflowPage />
        }
    }

    return (
        <div className="home-page">
            <TabNavigation activeTab={activeTab} onTabChange={setActiveTab} />
            <main className="tab-content">
                {renderContent()}
            </main>
        </div>
    )
}

export default HomePage
