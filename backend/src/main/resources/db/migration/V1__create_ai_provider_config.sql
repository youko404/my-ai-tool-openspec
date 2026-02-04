CREATE TABLE IF NOT EXISTS ai_provider_config
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    provider
    VARCHAR
(
    50
) NOT NULL UNIQUE,
    api_base_url VARCHAR
(
    500
) NOT NULL,
    api_key VARCHAR
(
    500
) NOT NULL,
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
