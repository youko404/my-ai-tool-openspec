## 1. Specification

- [x] 1.1 Confirm open questions (resolved: no raw file storage, percentage status).

## 2. Backend - Knowledge Base API

- [x] 2.1 Add MySQL schema for knowledge bases and ingestion jobs.
- [x] 2.2 Add PgVector schema for file metadata, chunks, and embeddings (dimension 1024).
- [x] 2.3 Implement knowledge base CRUD + enable/disable endpoints.
- [x] 2.4 Implement batch file upload endpoint with validation (txt/md/docx, <=10MB).
- [x] 2.5 Implement async ingestion pipeline (chunking + embedding + vector insert) with job status updates.
- [x] 2.6 Expose ingestion job status endpoint for UI polling.

## 3. Backend - Integrations

- [x] 3.1 Add OllamaEmbeddingModel integration for `qwen3-embedding:0.6b`.
- [x] 3.2 Configure PgVector data source and repository access.

## 4. Frontend - Knowledge Base UI

- [x] 4.1 Build knowledge base list with create/edit/delete/enable actions.
- [x] 4.2 Add batch file upload UI with type/size validation and async status display.

## 5. Infra & Config

- [x] 5.1 Update `docker-compose.yml` with `ollama` and `vector_db` services.
- [x] 5.2 Add backend config for Postgres/PgVector and Ollama endpoints.

## 6. Validation

- [ ] 6.1 Add backend tests for ingestion pipeline and CRUD endpoints.
- [ ] 6.2 Add frontend smoke checks for knowledge base workflows.
