## Context

We need a knowledge base management feature that supports document ingestion into a PgVector-backed store using an
Ollama embedding model. The system already runs on Spring Boot + MySQL and will add PgVector and Ollama services via
docker-compose.

## Goals / Non-Goals

- Goals:
    - CRUD knowledge bases with enable/disable state stored in MySQL.
    - Batch upload files (txt, md, docx, <=10MB) per knowledge base.
    - Async ingestion pipeline: split into chunks, embed with Ollama (qwen3-embedding:0.6b), store vectors and chunk
      metadata in PgVector.
    - Provide ingestion status for UI visibility.
- Non-Goals:
    - Retrieval/search APIs or UI for querying the vector store.
    - Advanced chunking strategies beyond a simple default (until requested).

## Decisions

- Decision: Store knowledge base records in MySQL; store file metadata, chunk metadata, and embeddings in PgVector.
    - Rationale: Keep relational metadata in the primary DB while vectors live in PgVector for similarity search.
- Decision: Do not persist raw uploaded files after ingestion completes.
    - Rationale: Reduce storage overhead and simplify data lifecycle management.
- Decision: Use OllamaEmbeddingModel with `qwen3-embedding:0.6b`, vector dimension 1024.
    - Rationale: Matches the requested model and known dimension.
- Decision: Async ingestion via backend job runner (queue or executor) with persisted status.
    - Rationale: Avoid blocking uploads and allow progress tracking.
- Decision: Batch uploads are accepted per knowledge base and validated for type/size upfront.

## Risks / Trade-offs

- Async jobs require persistent status and retry handling to avoid silent failures.
- PgVector schema and dimension must match model output (dimension mismatch will fail inserts).
- Without raw file storage, re-ingestion requires re-upload from the client.

## Migration Plan

- Introduce new tables in MySQL for knowledge bases and ingestion jobs.
- Introduce PgVector tables for file metadata, chunks, and embeddings.
- Add docker-compose services for `ollama` and `vector_db`.

## Open Questions

- None.
