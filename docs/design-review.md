# Design Review

This document records the formal design-review findings and recommended actions for the Automated Documentation Sync project. Findings are based on reviewing docs/requirements.md and docs/architecture.md and the current implementation.

## Summary
The architecture is appropriate for the project's goals (modular, event-driven Java service). Several high-severity gaps were identified (persistence, idempotency, conflict handling, secrets/permission model, sanitization, and rate-limit resilience) that must be addressed before production-readiness.

## Findings

F-001 — Requirement coverage gap
- Severity: Medium
- Description: Missing explicit Approval Manager for human approval workflow and notifications.
- Impact: PRs may be hard to triage; human approval workflow is implicit.
- Recommendation: Implement Approval Manager to tag, notify, and track PRs requiring owner approval.

F-002 — Missing persistent state store
- Severity: High
- Description: No persistent run store for deduplication and audit trail.
- Impact: Duplicate PRs and lack of run history.
- Recommendation: Add lightweight persistence (SQLite for capstone) to store run metadata and PR mappings.

F-003 — Idempotency & deduplication not defined
- Severity: High
- Description: No mechanism to avoid re-processing same story or duplicate PRs.
- Impact: Duplicate PRs and branches.
- Recommendation: Define canonical branch naming and check DB for existing open PRs; update existing PRs where appropriate.

F-004 — Conflict resolution and concurrent edits
- Severity: High
- Description: No rebase or conflict workflow defined.
- Impact: Blocked merges and overwritten changes.
- Recommendation: Add CI rebase attempts and mark PRs as needing rebase when unresolved.

F-005 — Security: token and permission model underspecified
- Severity: Critical
- Description: No concrete minimal scopes, rotation, or GitHub App guidance implemented.
- Impact: Elevated blast radius for leaked tokens.
- Recommendation: Prefer GitHub App or least-privilege PATs; enforce rotation and auditing.

F-006 — Injection / content-safety risk from external content
- Severity: High
- Description: No sanitization pipeline for untrusted input.
- Impact: Malicious content could be committed or create unsafe reports.
- Recommendation: Implement sanitization and PII redaction pipeline.

F-007 — Not Found / creation policy could cause noisy PRs
- Severity: Medium
- Description: Auto-creating missing files may produce low-value PRs.
- Impact: Reviewer fatigue.
- Recommendation: Add confidence scoring and batching of small fixes.

F-008 — Error handling gaps for partial runs
- Severity: Medium
- Description: No explicit rollback/transaction policy for multi-file runs.
- Impact: Orphaned branches or partial changes.
- Recommendation: Treat runs atomically at PR creation time; only push when all changes prepared.

F-009 — Observability and alerting limited
- Severity: Medium
- Description: No metrics, tracing, or alerts specified.
- Impact: Hard to detect systemic failures.
- Recommendation: Add metrics (Micrometer) and simple alerts for repeated failures.

F-010 — API rate-limit and dependency resilience
- Severity: High
- Description: No rate-limit-aware clients or centralized throttling.
- Impact: Throttling/failures on heavy usage.
- Recommendation: Implement rate-limit-aware HTTP wrapper and throttling.

F-011 — Data validation and schema gaps
- Severity: High
- Description: No JSON Schema for StructuredStoryModel; parser heuristics risk mis-mapping.
- Impact: Incorrect mappings and invalid docs.
- Recommendation: Define JSON Schema and validate parsed content; surface low-confidence mappings.

F-012 — Single points of failure (orchestrator and secrets)
- Severity: High
- Description: Orchestrator and secrets store are single points if run as single service.
- Impact: System outage or secret compromise.
- Recommendation: Use managed secrets and redundant orchestrators for production.

F-013 — Testability: integration test environment lacking isolation
- Severity: Medium
- Description: Integration tests may modify production; no sandbox specified.
- Impact: Possible data corruption.
- Recommendation: Use test repos and recorded fixtures.

F-014 — Maintainability: tight coupling risk between components
- Severity: Medium
- Description: Risk of tight coupling if DTOs are not versioned.
- Impact: Refactor difficulty.
- Recommendation: Define stable DTOs and contract tests.

F-015 — Dependency risks: third-party libs
- Severity: Medium
- Description: Risk from vulnerabilities in libs (POI, JGit, etc.)
- Impact: Security and maintenance burden.
- Recommendation: Lock versions and scan dependencies.

F-016 — Observability: insufficient user-facing report design
- Severity: Low
- Description: Report discoverability/UX not specified.
- Impact: Reviewers may miss reports.
- Recommendation: Attach reports to PRs and add concise summary comments.

F-017 — Edge cases: large docs, binary attachments, LFS
- Severity: Medium
- Description: Large/binary assets and LFS not handled.
- Impact: Failures for large commits.
- Recommendation: Detect large/binary content early; reference artifacts instead of committing.

F-018 — Not Found semantics for external sources
- Severity: Low
- Description: Behavior ambiguous when external source unreachable.
- Impact: Spurious PRs.
- Recommendation: Mark run as Error on ingestion failure; do not create docs unless ingestion succeeds.

F-019 — Release/rollback policy missing
- Severity: Medium
- Description: No guidance to revert bad automated merges.
- Impact: Slow recovery from broken docs.
- Recommendation: Include run-id in commits to allow automated reverts.

F-020 — Privacy / PII leakage in reports
- Severity: High
- Description: Reports may contain PII from user stories.
- Impact: Privacy/compliance risk.
- Recommendation: Add classification and redaction before publishing reports.

## Proposed Design Decisions (Actionable)
1. Add a persistent RunStore (SQLite for capstone) to track run-id, source-story-id, PR mappings, and status. (TSK-004)
2. Use JSON Schema for StructuredStoryModel and validate parsed content; include confidence scores. (TSK-006)
3. Implement an Approval Manager component that integrates with GitHub PR review APIs and posts reviewer notifications. (TSK-019)
4. Enforce branch naming docs-sync/{source}/{id}/{short-hash} and check RunStore for existing PRs to update instead of creating duplicates. (TSK-016)
5. Implement a central HTTP client wrapper with resilience4j for retries, backoff, and rate-limit awareness. (TSK-008)
6. Implement Sanitizer pipeline to strip HTML/scripts, redact PII, and escape markdown. (TSK-007)
7. Run documentation validations and secret-scans in CI; fail the PR on critical findings. (CI workflow)
8. Persist verification reports under docs/verification-reports/<run-id>.md and <run-id>.json and retain last N runs (configurable). (TSK-017)
9. Use GitHub Actions for CI during capstone; recommend GitHub App and managed secrets for production.
10. Add metrics and tracing (Micrometer) and basic alerting for repeated failures. (TSK-022)

## Traceability
This review maps to requirements and the implementation plan; recommended TSK items reference tasks in docs/impl-plan.md.
