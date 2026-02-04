CREATE TABLE IF NOT EXISTS chat_conversation
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    title
    VARCHAR
(
    200
) NOT NULL,
    model_id BIGINT,
    model_name VARCHAR
(
    100
),
    last_message_preview VARCHAR
(
    500
),
    is_deleted TINYINT
(
    1
) NOT NULL DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR
(
    255
),
    updated_by VARCHAR
(
    255
)
    );

CREATE INDEX idx_chat_conversation_updated_at ON chat_conversation (updated_at);
CREATE INDEX idx_chat_conversation_is_deleted ON chat_conversation (is_deleted);

CREATE TABLE IF NOT EXISTS chat_message
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    conversation_id
    BIGINT
    NOT
    NULL,
    role
    VARCHAR
(
    20
) NOT NULL,
    content TEXT,
    status VARCHAR
(
    20
) NOT NULL,
    error_message VARCHAR
(
    1000
),
    provider_response LONGTEXT,
    model VARCHAR
(
    100
),
    created_at DATETIME,
    updated_at DATETIME,
    created_by VARCHAR
(
    255
),
    updated_by VARCHAR
(
    255
)
    );

CREATE INDEX idx_chat_message_conversation_id ON chat_message (conversation_id);
CREATE INDEX idx_chat_message_created_at ON chat_message (created_at);
