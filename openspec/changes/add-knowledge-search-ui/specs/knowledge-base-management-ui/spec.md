## ADDED Requirements

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

## MODIFIED Requirements

### Requirement: Knowledge Base List and Actions

The system SHALL provide a knowledge base page that defaults to the query view and offers a management list via a header
button, supporting create, edit, delete, and enable/disable actions.

#### Scenario: Open knowledge base page

- **WHEN** the user opens the knowledge base page
- **THEN** the system shows the query view and a management button for accessing the knowledge base list

#### Scenario: View knowledge base list

- **WHEN** the user opens the management list from the button
- **THEN** the system shows a list of knowledge bases with their enabled state

#### Scenario: Update knowledge base details

- **WHEN** the user edits a knowledge base name or configuration from the management list
- **THEN** the system saves the changes and refreshes the list
