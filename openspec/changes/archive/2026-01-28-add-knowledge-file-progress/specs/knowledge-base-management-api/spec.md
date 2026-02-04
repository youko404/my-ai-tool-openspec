## ADDED Requirements

### Requirement: Ingestion File Progress List API

The system SHALL provide an API to list ingestion files for a knowledge base, including file metadata, status, and
percentage progress, ordered with in-progress and most recent files first, and support pagination.

#### Scenario: Query file progress list

- **WHEN** a client requests the file progress list for a knowledge base
- **THEN** the system returns the files with status and progress percent
