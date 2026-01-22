# Change: Update knowledge base management page layout and navigation

## Why

The knowledge base UI currently uses a modal for management and lacks consistent layout width, input sizing, and dark
mode styling. We need a dedicated management page in a new browser tab and consistent layout/theming across the query
and management views.

## What Changes

- Open the knowledge base management page in a new browser tab instead of a modal.
- Center query and management content to 80% width on desktop with responsive full-width layout on smaller screens.
- Constrain the search query input width on desktop.
- Align dark mode colors using existing CSS variables for text and inputs.

## Impact

- Affected specs: knowledge-base-management-ui
- Affected code: frontend knowledge base pages, routing/navigation, styles, management entry point
- Dependencies: add-knowledge-search-ui (query view default)
