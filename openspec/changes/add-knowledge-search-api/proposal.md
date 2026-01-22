# Change: Add knowledge base chunk search API

## Why

Users need to retrieve relevant knowledge base chunks by cosine similarity for downstream RAG workflows.

## What Changes

- Add a knowledge base chunk search API that accepts knowledgeBaseId, queryText, minScore, and limit.
- Add request/response models for chunk search results (including chunk content).
- Add vector repository support for similarity filtering.

## Impact

- Affected specs: knowledge-base-management-api
- Affected code: backend controller/service/repository for knowledge base search
