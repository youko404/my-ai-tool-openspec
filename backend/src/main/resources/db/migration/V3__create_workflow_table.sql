-- Create workflow table for workflow management feature
CREATE TABLE IF NOT EXISTS workflow
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    name
    VARCHAR
(
    255
) NOT NULL COMMENT 'Workflow name',
    description VARCHAR
(
    1000
) COMMENT 'Workflow description',
    enabled TINYINT
(
    1
) NOT NULL DEFAULT 1 COMMENT 'Whether the workflow is enabled',
    deleted TINYINT
(
    1
) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    creator VARCHAR
(
    100
) COMMENT 'Creator username',
    editor VARCHAR
(
    100
) COMMENT 'Last editor username',
    created_at DATETIME COMMENT 'Creation timestamp',
    updated_at DATETIME COMMENT 'Last update timestamp',
    created_by VARCHAR
(
    255
) COMMENT 'Created by user',
    updated_by VARCHAR
(
    255
) COMMENT 'Last updated by user'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Workflow configuration table';

-- Create indexes for better query performance
CREATE INDEX idx_workflow_name ON workflow (name);
CREATE INDEX idx_workflow_deleted ON workflow (deleted);
CREATE INDEX idx_workflow_enabled ON workflow (enabled);
CREATE INDEX idx_workflow_created_at ON workflow (created_at);
CREATE INDEX idx_workflow_updated_at ON workflow (updated_at);
