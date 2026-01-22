## ADDED Requirements

### Requirement: Knowledge Base List and Actions

The system SHALL provide a knowledge base management page that lists knowledge bases and supports create, edit, delete,
and enable/disable actions.

#### Scenario: View knowledge base list

- **WHEN** the user opens the knowledge base page
- **THEN** the system shows a list of knowledge bases with their enabled state

#### Scenario: Update knowledge base details

- **WHEN** the user edits a knowledge base name or configuration
- **THEN** the system saves the changes and refreshes the list

### Requirement: Batch File Upload UI

The system SHALL provide a batch upload UI for knowledge base files with type/size validation and ingestion status
visibility, including completion percentage.

#### Scenario: Upload valid files

- **WHEN** the user selects multiple files (txt, md, docx) within 10MB
- **THEN** the system starts the ingestion job and shows a queued or in-progress status

#### Scenario: Show ingestion status

- **WHEN** an ingestion job is running or completed
- **THEN** the system displays the current status (queued, in_progress, failed, completed) and percentage progress
