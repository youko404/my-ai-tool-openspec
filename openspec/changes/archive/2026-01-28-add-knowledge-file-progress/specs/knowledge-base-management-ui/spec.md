## ADDED Requirements

### Requirement: Knowledge Base File Progress Modal

The system SHALL provide a per-knowledge-base action that opens a modal listing all uploaded files with their ingestion
status and percentage progress, ordered with the most recent and in-progress files first.

#### Scenario: View file progress list

- **WHEN** the user clicks the file progress action for a knowledge base
- **THEN** the system shows a modal containing the file list with status and progress percent for each file

#### Scenario: Poll in-progress files

- **WHEN** the modal is open and any file is in progress
- **THEN** the system refreshes the list every 5 seconds until all files are completed or failed
