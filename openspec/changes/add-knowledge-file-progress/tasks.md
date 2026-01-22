## 1. Backend: data + API

- [x] 1.1 Extend `knowledge_files` schema to persist file progress fields.
- [x] 1.2 Update ingestion worker to compute and store per-file progress.
- [x] 1.3 Add repository/service method to fetch paged file progress list.
- [x] 1.4 Expose `GET /knowledge-bases/{id}/files` with pagination.

## 2. Frontend: file progress modal

- [x] 2.1 Add API client/types for knowledge base file progress list.
- [x] 2.2 Add "文件进度" action per knowledge base row.
- [x] 2.3 Implement modal with file progress table and conditional polling refresh (only when in-progress exists).

## 3. Validation

- [ ] 3.1 Add/adjust tests for API service and UI rendering as needed.
- [x] 3.2 Run `openspec validate add-knowledge-file-progress --strict --no-interactive`.
