import {
    ApartmentOutlined,
    RobotOutlined,
    DatabaseOutlined,
    ToolOutlined,
    ThunderboltOutlined,
    MessageOutlined,
} from '@ant-design/icons'
import './tab-navigation.css'

export type TabKey = 'chat' | 'workflow' | 'model' | 'knowledge-base' | 'tool' | 'skills'

interface TabItem {
    key: TabKey
    label: string
    icon: React.ReactNode
}

interface TabNavigationProps {
    activeTab: TabKey
    onTabChange: (key: TabKey) => void
}

const tabs: TabItem[] = [
    {
        key: 'chat',
        label: '聊天',
        icon: <MessageOutlined />,
    },
    {
        key: 'workflow',
        label: '工作流',
        icon: <ApartmentOutlined />,
    },
    {
        key: 'model',
        label: '模型',
        icon: <RobotOutlined />,
    },
    {
        key: 'knowledge-base',
        label: '知识库',
        icon: <DatabaseOutlined />,
    },
    {
        key: 'tool',
        label: 'Tool',
        icon: <ToolOutlined />,
    },
    {
        key: 'skills',
        label: 'Skills',
        icon: <ThunderboltOutlined />,
    },
]

function TabNavigation({ activeTab, onTabChange }: TabNavigationProps) {
    return (
        <nav className="tab-navigation">
            <div className="tab-navigation-container">
                {tabs.map((tab) => (
                    <button
                        key={tab.key}
                        className={`tab-item ${activeTab === tab.key ? 'tab-item-active' : ''}`}
                        onClick={() => onTabChange(tab.key)}
                    >
                        <span className="tab-icon">{tab.icon}</span>
                        <span className="tab-label">{tab.label}</span>
                    </button>
                ))}
            </div>
        </nav>
    )
}

export default TabNavigation
