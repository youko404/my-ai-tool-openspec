## MODIFIED Requirements

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

## ADDED Requirements

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
