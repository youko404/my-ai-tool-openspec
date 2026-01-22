package com.ai.content.repository;

import com.ai.content.domain.entity.KnowledgeIngestionStatus;
import com.ai.content.domain.entity.vector.KnowledgeChunkSearchResult;
import com.ai.content.domain.entity.vector.KnowledgeIngestionFile;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for vector DB operations
 */
@Repository
@RequiredArgsConstructor
public class KnowledgeVectorRepository {

    private final JdbcTemplate vectorJdbcTemplate;

    public long insertFile(Long knowledgeBaseId, Long jobId, String fileName, String contentType, long sizeBytes,
        String status, int totalChunks, int processedChunks, int progressPercent) {
        String sql = """
            INSERT INTO knowledge_files (
                knowledge_base_id,
                job_id,
                file_name,
                content_type,
                size_bytes,
                status,
                total_chunks,
                processed_chunks,
                progress_percent
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        vectorJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setLong(1, knowledgeBaseId);
            ps.setLong(2, jobId);
            ps.setString(3, fileName);
            ps.setString(4, contentType);
            ps.setLong(5, sizeBytes);
            ps.setString(6, status);
            ps.setInt(7, totalChunks);
            ps.setInt(8, processedChunks);
            ps.setInt(9, progressPercent);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.longValue();
        }
        if (keyHolder.getKeys() != null && keyHolder.getKeys().get("id") instanceof Number id) {
            return id.longValue();
        }
        return 0L;
    }

    public void updateFileStatus(long fileId, String status) {
        vectorJdbcTemplate.update("UPDATE knowledge_files SET status = ? WHERE id = ?", status, fileId);
    }

    public void updateFileProgress(long fileId, int processedChunks, int totalChunks) {
        int percent = totalChunks > 0 ? (int)Math.round(processedChunks * 100.0 / totalChunks) : 100;
        vectorJdbcTemplate.update(
            "UPDATE knowledge_files SET processed_chunks = ?, progress_percent = ?, status = ? WHERE id = ?",
            processedChunks, Math.min(percent, 100), KnowledgeIngestionStatus.IN_PROGRESS.name(), fileId);
    }

    public void updateFileCompleted(long fileId, int totalChunks) {
        vectorJdbcTemplate.update(
            "UPDATE knowledge_files SET processed_chunks = ?, progress_percent = ?, status = ? WHERE id = ?",
            totalChunks, 100, KnowledgeIngestionStatus.COMPLETED.name(), fileId);
    }

    public void updateFileFailed(long fileId, int processedChunks, int totalChunks) {
        int percent = totalChunks > 0 ? (int)Math.round(processedChunks * 100.0 / totalChunks) : 100;
        vectorJdbcTemplate.update(
            "UPDATE knowledge_files SET processed_chunks = ?, progress_percent = ?, status = ? WHERE id = ?",
            processedChunks, Math.min(percent, 100), KnowledgeIngestionStatus.FAILED.name(), fileId);
    }

    public void insertChunk(long fileId, long knowledgeBaseId, int chunkIndex, String content, String vectorLiteral) {
        String sql = """
            INSERT INTO knowledge_chunks (file_id, knowledge_base_id, chunk_index, content, embedding)
            VALUES (?, ?, ?, ?, ?::vector)
            """;
        vectorJdbcTemplate.update(sql, fileId, knowledgeBaseId, chunkIndex, content, vectorLiteral);
    }

    public List<KnowledgeIngestionFile> findFiles(Long knowledgeBaseId, int limit, int offset) {
        String sql = """
            SELECT id,
                   knowledge_base_id,
                   job_id,
                   file_name,
                   content_type,
                   size_bytes,
                   status,
                   total_chunks,
                   processed_chunks,
                   progress_percent,
                   created_at
            FROM knowledge_files
            WHERE knowledge_base_id = ?
            ORDER BY CASE
                WHEN status = 'IN_PROGRESS' THEN 0
                WHEN status = 'QUEUED' THEN 1
                ELSE 2
            END,
            created_at DESC
            LIMIT ? OFFSET ?
            """;
        return vectorJdbcTemplate.query(sql, (rs, rowNum) -> {
            String status = rs.getString("status");
            Timestamp createdAt = rs.getTimestamp("created_at");
            LocalDateTime createdTime = createdAt != null ? createdAt.toLocalDateTime() : null;
            Long sizeBytes = rs.getObject("size_bytes", Long.class);
            return KnowledgeIngestionFile.builder().id(rs.getLong("id"))
                .knowledgeBaseId(rs.getLong("knowledge_base_id")).jobId(rs.getLong("job_id"))
                .fileName(rs.getString("file_name")).contentType(rs.getString("content_type")).sizeBytes(sizeBytes)
                .status(status).totalChunks(rs.getInt("total_chunks")).processedChunks(rs.getInt("processed_chunks"))
                .progressPercent(rs.getInt("progress_percent")).createdAt(createdTime).build();
        }, knowledgeBaseId, limit, offset);
    }

    public long countFiles(Long knowledgeBaseId) {
        Long total =
            vectorJdbcTemplate.queryForObject("SELECT COUNT(*) FROM knowledge_files WHERE knowledge_base_id = ?",
                Long.class, knowledgeBaseId);
        return total == null ? 0L : total;
    }

    public List<KnowledgeChunkSearchResult> searchChunks(Long knowledgeBaseId, String queryVectorLiteral,
        double minScore, int limit) {
        String sql = """
            WITH query AS (SELECT ?::vector AS embedding)
            SELECT f.file_name,
                   c.chunk_index,
                   c.content,
                   1 - (c.embedding <=> query.embedding) AS score
            FROM knowledge_chunks c
            JOIN knowledge_files f ON f.id = c.file_id
            JOIN query ON true
            WHERE c.knowledge_base_id = ?
              AND (1 - (c.embedding <=> query.embedding)) >= ?
            ORDER BY score DESC
            LIMIT ?
            """;
        return vectorJdbcTemplate.query(sql,
            (rs, rowNum) -> KnowledgeChunkSearchResult.builder().fileName(rs.getString("file_name"))
                .chunkIndex(rs.getInt("chunk_index")).content(rs.getString("content")).score(rs.getDouble("score"))
                .build(), queryVectorLiteral, knowledgeBaseId, minScore, limit);
    }
}
