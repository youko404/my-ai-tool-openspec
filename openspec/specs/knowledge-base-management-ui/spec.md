# knowledge-base-management-ui Specification

## Purpose

TBD - created by archiving change add-knowledge-base-pgvector. Update Purpose after archive.
## Requirements
### Requirement: Knowledge Base List and Actions

The system SHALL provide a knowledge base page that defaults to the query view and offers a management entry that opens
the knowledge base management page in a new browser tab, supporting create, edit, delete, enable/disable, batch upload,
and file progress actions.

#### Scenario: Open knowledge base page

- **WHEN** the user opens the knowledge base page
- **THEN** the system shows the query view and a management entry for accessing the knowledge base management page

#### Scenario: Open management page in new tab

- **WHEN** the user activates the management entry
- **THEN** the system opens the knowledge base management page in a new browser tab and does not show a modal

#### Scenario: Manage knowledge bases

- **WHEN** the user performs create, edit, delete, enable/disable, batch upload, or file progress actions on the
  management page
- **THEN** the system applies the change and refreshes the list state

### Requirement: Batch File Upload UI

The system SHALL provide a batch upload UI for knowledge base files with type/size validation and ingestion status
visibility, including completion percentage.

#### Scenario: Upload valid files

- **WHEN** the user selects multiple files (txt, md, docx) within 10MB
- **THEN** the system starts the ingestion job and shows a queued or in-progress status

#### Scenario: Show ingestion status

- **WHEN** an ingestion job is running or completed
- **THEN** the system displays the current status (queued, in_progress, failed, completed) and percentage progress

### Requirement: Knowledge Base Chunk Search View

The system SHALL provide a knowledge base query view that lets users submit chunk search parameters and view results
from the chunk search API.

#### Scenario: Default search form and layout

- **WHEN** the user opens the knowledge base page
- **THEN** the system shows a query form with required knowledgeBaseId selection, queryText empty by default, minScore
  default 0.7, limit default 5, and a results table area

#### Scenario: Manual query execution

- **WHEN** the user triggers search with knowledgeBaseId, queryText, minScore, and limit
- **THEN** the system requests the knowledge base chunk search API and updates the results table

#### Scenario: Display search results

- **WHEN** the search API returns results
- **THEN** the system shows rows with chunk content, similarity score, chunkIndex, and source file name, sorted by score
  descending, with the highest-scoring result expanded and others collapsed

### Requirement: Knowledge Base File Progress Modal

The system SHALL provide a per-knowledge-base action that opens a modal listing all uploaded files with their ingestion
status and percentage progress, ordered with the most recent and in-progress files first.

#### Scenario: View file progress list

- **WHEN** the user clicks the file progress action for a knowledge base
- **THEN** the system shows a modal containing the file list with status and progress percent for each file

#### Scenario: Poll in-progress files

- **WHEN** the modal is open and any file is in progress
- **THEN** the system refreshes the list every 5 seconds until all files are completed or failed

### Requirement: Knowledge Base Page Layout and Input Sizing

The system SHALL center the main content of the knowledge base query page and management page to 80% of the viewport
width on desktop, provide a full-width layout on narrow screens, and constrain the search query input to a readable
maximum width on desktop.

#### Scenario: Desktop layout width

- **WHEN** the viewport width is at least 1024px
- **THEN** the knowledge base query page and management page content containers are centered and occupy 80% of the
  viewport width

#### Scenario: Mobile layout width

- **WHEN** the viewport width is below 768px
- **THEN** the content containers use full width with standard page padding

#### Scenario: Search input sizing

- **WHEN** the viewport width is at least 1024px
- **THEN** the search query input width does not exceed 520px and does not stretch to the full row width

### Requirement: Knowledge Base Dark Mode Colors

The system SHALL apply dark mode styling on the knowledge base query page and management page using existing CSS
variables for text, borders, and input backgrounds to maintain contrast.

#### Scenario: Dark mode readability

- **WHEN** dark mode is active
- **THEN** text uses theme text variables and inputs use theme background variables with readable placeholders and
  borders

