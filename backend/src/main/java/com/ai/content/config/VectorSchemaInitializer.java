package com.ai.content.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Initializes PgVector schema for knowledge base storage
 */
@Component
@RequiredArgsConstructor
public class VectorSchemaInitializer {

    private final JdbcTemplate vectorJdbcTemplate;

    @PostConstruct
    public void initSchema() {
        vectorJdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        vectorJdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS knowledge_files (
                id BIGSERIAL PRIMARY KEY,
                knowledge_base_id BIGINT NOT NULL,
                job_id BIGINT NOT NULL,
                file_name TEXT NOT NULL,
                content_type TEXT,
                size_bytes BIGINT,
                status VARCHAR(32) NOT NULL,
                total_chunks INT NOT NULL DEFAULT 0,
                processed_chunks INT NOT NULL DEFAULT 0,
                progress_percent INT NOT NULL DEFAULT 0,
                created_at TIMESTAMP NOT NULL DEFAULT NOW()
            )
            """);
        vectorJdbcTemplate.execute("""
            ALTER TABLE IF EXISTS knowledge_files
            ADD COLUMN IF NOT EXISTS total_chunks INT NOT NULL DEFAULT 0
            """);
        vectorJdbcTemplate.execute("""
            ALTER TABLE IF EXISTS knowledge_files
            ADD COLUMN IF NOT EXISTS processed_chunks INT NOT NULL DEFAULT 0
            """);
        vectorJdbcTemplate.execute("""
            ALTER TABLE IF EXISTS knowledge_files
            ADD COLUMN IF NOT EXISTS progress_percent INT NOT NULL DEFAULT 0
            """);
        vectorJdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS knowledge_chunks (
                id BIGSERIAL PRIMARY KEY,
                file_id BIGINT NOT NULL,
                knowledge_base_id BIGINT NOT NULL,
                chunk_index INT NOT NULL,
                content TEXT NOT NULL,
                embedding VECTOR(1024) NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW()
            )
            """);
        vectorJdbcTemplate.execute(
            "CREATE INDEX IF NOT EXISTS idx_kb_chunks_kb ON knowledge_chunks (knowledge_base_id)");
        vectorJdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_kb_chunks_file ON knowledge_chunks (file_id)");
    }
}
