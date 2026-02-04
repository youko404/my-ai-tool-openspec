import type { TabKey } from '../../components/tab-navigation'
import ChatPage from '../chat-page'
import WorkflowPage from '../workflow-page'
import ModelPage from '../model-page'
import KnowledgeBasePage from '../knowledge-base-page'
import ToolPage from '../tool-page'
import SkillsPage from '../skills-page'
import './home-page.css'

interface HomePageProps {
    activeTab: TabKey
}

function HomePage({ activeTab }: HomePageProps) {
    const renderContent = () => {
        switch (activeTab) {
            case 'chat':
                return <ChatPage />
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
            <main className="tab-content">
                {renderContent()}
            </main>
        </div>
    )
}

export default HomePage
