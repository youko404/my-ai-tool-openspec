# Change: Add knowledge base search UI

## Why

Users need to search knowledge base chunks from the UI and see results without leaving the knowledge base page.

## What Changes

- Default the knowledge base page to a query view with a manual search form and results table.
- Display chunk search results with content, score, chunkIndex, and source file name, sorted by score descending.
- Expand the highest-scoring result by default and collapse the rest.
- Move the knowledge base management list behind a header button entry point.
- Depend on the knowledge base chunk search API from change add-knowledge-search-api.

## Impact

- Affected specs: knowledge-base-management-ui
- Affected code: frontend knowledge base page, API client/types, styles
