# Change: Add knowledge base management with PgVector ingestion

## Why

The platform needs a managed knowledge base that can ingest documents into a vector store for downstream RAG use cases.

## What Changes

- Add knowledge base CRUD with enable/disable state.
- Support batch file uploads (txt, md, docx, <=10MB) and async ingestion (chunking + embedding + vector storage).
- Integrate OllamaEmbeddingModel using `qwen3-embedding:0.6b` and store vectors in PgVector.
- Update docker-compose with `ollama` and `vector_db` services and backend configuration.

## Impact

- Affected specs: `knowledge-base-management-api`, `knowledge-base-management-ui`.
- Affected code: backend knowledge-base domain, ingestion pipeline, file upload handling, Postgres/PgVector access,
  frontend knowledge base page, `docker-compose.yml`.
