# knowledge-base-management-api Specification

## Purpose

TBD - created by archiving change add-knowledge-base-pgvector. Update Purpose after archive.
## Requirements
### Requirement: Knowledge Base Management API

The system SHALL provide APIs to create, list, update, delete, and enable/disable knowledge bases stored in MySQL.

#### Scenario: Create a knowledge base

- **WHEN** a client submits a valid knowledge base name and configuration
- **THEN** the system creates the knowledge base and returns its identifier

#### Scenario: Enable or disable a knowledge base

- **WHEN** a client toggles the enabled state
- **THEN** the system persists the new state and returns the updated knowledge base

### Requirement: Batch File Upload for Ingestion

The system SHALL accept batch uploads of files (txt, md, docx) up to 10MB per file for a specific knowledge base and
MUST NOT persist raw files after ingestion completes.

#### Scenario: Upload valid files for ingestion

- **WHEN** a client uploads multiple valid files within size/type limits
- **THEN** the system accepts the files and starts an async ingestion job

#### Scenario: Reject invalid files

- **WHEN** a client uploads a file with an unsupported type or size > 10MB
- **THEN** the system rejects the file with a validation error

### Requirement: Async Ingestion with PgVector Storage

The system SHALL asynchronously split uploaded files into chunks, generate embeddings using `qwen3-embedding:0.6b`, and
store chunk metadata and vectors (dimension 1024) in PgVector.

#### Scenario: Ingestion completes successfully

- **WHEN** the ingestion job runs for uploaded files
- **THEN** the system stores chunk metadata and embeddings in PgVector and marks the job as completed

#### Scenario: Ingestion fails

- **WHEN** an embedding or storage error occurs
- **THEN** the system marks the job as failed and records the error reason

### Requirement: Ingestion Job Status API

The system SHALL provide an API to fetch ingestion job status for a knowledge base, including completion percentage.

#### Scenario: Query job status

- **WHEN** a client requests the status for an ingestion job
- **THEN** the system returns the current state (queued, in_progress, failed, completed) and a percentage value

### Requirement: Knowledge Base Chunk Search API

The system SHALL provide an API to search knowledge base chunks by cosine similarity using a query text.

#### Scenario: Search chunks by similarity

- **WHEN** a client submits a knowledgeBaseId, queryText, minScore, and limit
- **THEN** the system returns up to limit chunks with cosine similarity >= minScore for the knowledge base, including
  the chunk content, source file name, chunkIndex, and similarity score

### Requirement: Ingestion File Progress List API

The system SHALL provide an API to list ingestion files for a knowledge base, including file metadata, status, and
percentage progress, ordered with in-progress and most recent files first, and support pagination.

#### Scenario: Query file progress list

- **WHEN** a client requests the file progress list for a knowledge base
- **THEN** the system returns the files with status and progress percent

