## ADDED Requirements

### Requirement: Knowledge Base Chunk Search API

The system SHALL provide an API to search knowledge base chunks by cosine similarity using a query text.

#### Scenario: Search chunks by similarity

- **WHEN** a client submits a knowledgeBaseId, queryText, minScore, and limit
- **THEN** the system returns up to limit chunks with cosine similarity >= minScore for the knowledge base, including
  the chunk content, source file name, chunkIndex, and similarity score
