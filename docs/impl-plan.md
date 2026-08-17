# Implementation Plan

## Implementation Overview

This implementation plan breaks the work into dependency-ordered tasks necessary to deliver the Automated Documentation Sync system. Tasks are small, traceable to requirements in docs/requirements.md, and prioritized for incremental delivery.

## Tasks

TSK-001 — Project bootstrap & conventions
- Description: Finalize Maven layout, .gitignore, and basic repo conventions.
- Files to create/change: pom.xml (verify), .gitignore, src/ dirs
- Dependencies: None
- Requirements covered: NFR-002, NFR-003
- Test requirements: mvn clean package; sample unit test
- Priority: High
- Blocked: No

TSK-002 — Configuration framework
- Description: Externalized config (application.yml) and Config class
- Files: src/main/resources/application.yml, src/main/java/com/capstone/config/Config.java
- Dependencies: TSK-001
- Req: NFR-003, NFR-004, FR-006
- Tests: Config loading unit tests
- Priority: High
- Blocked: No

TSK-003 — Logging & observability baseline
- Description: SLF4J+Logback setup; metrics stub
- Files: src/main/resources/logback.xml, src/main/java/com/capstone/logging/*.java
- Dependencies: TSK-001, TSK-002
- Req: NFR-006, FR-011
- Tests: Log format unit tests
- Priority: High
- Blocked: No

TSK-004 — Persistence: lightweight run store (SQLite)
- Description: Run store for run-id, dedup, PR mapping
- Files: src/main/resources/db/schema.sql, src/main/java/com/capstone/persistence/*
- Dependencies: TSK-001, TSK-002
- Req: FR-011, FR-005
- Tests: DAO unit tests
- Priority: High
- Blocked: No

TSK-005 — Security & secrets manager abstraction
- Description: Secrets provider abstraction, rotation hooks
- Files: src/main/java/com/capstone/security/SecretsManager.java, docs/SECURITY.md
- Dependencies: TSK-002
- Req: NFR-001, NFR-006, FR-012
- Tests: Secrets handling unit tests
- Priority: High
- Blocked: No

TSK-006 — DTOs and JSON Schema
- Description: Define StructuredStoryModel and JSON Schema
- Files: src/main/java/com/capstone/model/StructuredStory.java, src/main/resources/schemas/*
- Dependencies: TSK-002
- Req: FR-001
- Tests: Schema validation unit tests
- Priority: High
- Blocked: No

TSK-007 — Sanitization & content-safety module
- Description: Sanitize incoming content, PII classification
- Files: src/main/java/com/capstone/safety/Sanitizer.java
- Dependencies: TSK-006
- Req: NFR-006
- Tests: Sanitizer unit tests
- Priority: High
- Blocked: No

TSK-008 — HTTP client and resiliency wrapper
- Description: Centralized HTTP client with retry/backoff
- Files: src/main/java/com/capstone/http/HttpClientWrapper.java
- Dependencies: TSK-002, TSK-003
- Req: NFR-004
- Tests: Retry/backoff unit tests
- Priority: High
- Blocked: No

TSK-009 — Ingestor: Word (.docx)
- Description: Apache POI adapter to extract text and structure
- Files: src/main/java/com/capstone/ingest/WordIngestor.java
- Dependencies: TSK-006, TSK-007
- Req: FR-001, AC-001
- Tests: Integration/unit with .docx fixtures
- Priority: High
- Blocked: No

TSK-010 — Ingestors: JIRA & Confluence adapters
- Description: REST adapters to fetch stories/pages
- Files: src/main/java/com/capstone/ingest/JiraIngestor.java, ConfluenceIngestor.java
- Dependencies: TSK-008, TSK-006, TSK-005
- Req: FR-001, AC-001
- Tests: Mocked API tests
- Priority: High
- Blocked: No

TSK-011 — Parser / Normalizer
- Description: Parse raw content into StructuredStoryModel
- Files: src/main/java/com/capstone/parser/StoryParser.java
- Dependencies: TSK-006, TSK-007, TSK-009, TSK-010
- Req: FR-001, FR-003
- Tests: Parsing unit tests
- Priority: High
- Blocked: No

TSK-012 — Validator (document checks)
- Description: Validate canonical docs for mandatory headings
- Files: src/main/java/com/capstone/validator/DocValidator.java
- Dependencies: TSK-001, TSK-006, TSK-011
- Req: FR-002, FR-003, AC-002, AC-003
- Tests: Validator unit tests with fixtures
- Priority: High
- Blocked: No

TSK-013 — Document templates / mandatory headings
- Description: Add canonical templates and ensure validator compatibility
- Files: .github/pull_request_template.md, docs/templates/*.md
- Dependencies: TSK-001, TSK-012
- Req: FR-002
- Tests: Template loading test
- Priority: High
- Blocked: No

TSK-014 — Change Classifier and diff engine
- Description: Line-based diff and significance classification
- Files: src/main/java/com/capstone/classifier/ChangeClassifier.java
- Dependencies: TSK-004, TSK-012, TSK-011
- Req: FR-006, FR-007, AC-006
- Tests: Diff/classification unit tests
- Priority: High
- Blocked: No

TSK-015 — Doc Generator / Synchronizer
- Description: Compose updates preserving headings and skeletons
- Files: src/main/java/com/capstone/generator/DocGenerator.java
- Dependencies: TSK-011, TSK-012, TSK-014
- Req: FR-004, AC-004
- Tests: Generator unit tests
- Priority: High
- Blocked: No

TSK-016 — Git Service & PR Manager
- Description: JGit local ops + GitHub API PR creation; check DB for open PRs
- Files: src/main/java/com/capstone/git/GitService.java, PRManager.java
- Dependencies: TSK-004, TSK-015, TSK-013
- Req: FR-005, FR-009, AC-004, AC-005
- Tests: Integration tests (test repo)
- Priority: High
- Blocked: No

TSK-017 — Report Generator & storage policy
- Description: Write MD+JSON reports under docs/verification-reports/, implement retention
- Files: src/main/java/com/capstone/report/ReportGenerator.java
- Dependencies: TSK-004, TSK-015, TSK-016
- Req: FR-011, AC-007
- Tests: Report generation tests
- Priority: High
- Blocked: No

TSK-018 — CI integration: GitHub Actions workflows
- Description: Maven build/test workflow and docs-sync run workflow (manual/schedule)
- Files: .github/workflows/maven-ci.yml, docs-sync-run.yml
- Dependencies: TSK-001, TSK-016, TSK-005
- Req: FR-013, NFR-001
- Tests: CI passes on feature branches
- Priority: High
- Blocked: No

TSK-019 — Approval Manager & notification integration
- Description: Set reviewer, post PR summary comment, optional Slack/email notifications
- Files: src/main/java/com/capstone/approval/ApprovalManager.java
- Dependencies: TSK-016, TSK-017, TSK-005
- Req: FR-007, AC-005
- Tests: Mock PR review tests
- Priority: High
- Blocked: No

TSK-020 — Rate-limiter & request throttler
- Description: Centralize API rate-limiting and request throttling
- Files: src/main/java/com/capstone/http/RateLimiter.java
- Dependencies: TSK-008
- Req: F-010
- Tests: Throttling unit tests
- Priority: Medium
- Blocked: No

TSK-021 — Conflict & rebase handling in CI
- Description: Attempt rebase in CI and annotate conflicts
- Files: .github/workflows/maven-ci.yml (rebase step), src/main/java/com/capstone/git/RebaseHelper.java
- Dependencies: TSK-016, TSK-018
- Req: F-004
- Tests: Conflict simulation tests
- Priority: Medium
- Blocked: No

TSK-022 — Metrics, tracing & alerts
- Description: Add Micrometer metrics and basic alerting docs
- Files: src/main/java/com/capstone/metrics/*, docs/ALERTING.md
- Dependencies: TSK-003, TSK-017
- Req: F-009
- Tests: Metrics unit tests
- Priority: Medium
- Blocked: No

TSK-023 — Tests: fixtures and integration harness
- Description: Create test fixtures and CI harness for E2E tests
- Files: src/test/resources/fixtures/*, docs/test-integration.md
- Dependencies: TSK-009, TSK-010, TSK-016, TSK-018
- Req: AC-001..AC-010
- Tests: E2E CI tests
- Priority: High
- Blocked: No

TSK-024 — UX: concise PR comment and report linking
- Description: Post concise PR comment with link to report and list of Significant changes
- Files: integrate into PRManager & ApprovalManager
- Dependencies: TSK-016, TSK-017, TSK-019
- Req: FR-009, FR-011
- Tests: PR body/comment unit tests
- Priority: Medium
- Blocked: No

TSK-025 — Documentation & runbook
- Description: Create runbook, security, and revert helper scripts
- Files: docs/operations/README.md, docs/SECURITY.md, scripts/revert-run.sh
- Dependencies: TSK-004, TSK-005, TSK-017
- Req: F-019, NFR-006
- Tests: Runbook smoke test
- Priority: Medium
- Blocked: No

## Task Dependencies

- Core boot tasks: TSK-001 -> TSK-002 -> TSK-003 -> TSK-004 -> TSK-005 -> TSK-006 -> TSK-007 -> TSK-008
- Ingest & parse: TSK-009, TSK-010 -> TSK-011
- Validate & classify: TSK-011 -> TSK-012 -> TSK-014
- Generate & commit: TSK-012, TSK-014 -> TSK-015 -> TSK-016 -> TSK-017
- CI & approval: TSK-016 -> TSK-018 -> TSK-019
- Observability & testing: TSK-003 -> TSK-022, TSK-023

## Task Priority

High: TSK-001..TSK-017, TSK-023
Medium: TSK-020, TSK-021, TSK-022, TSK-024, TSK-025

## Blocked Tasks

None of the high-priority tasks are blocked given access to repository and test credentials. External integration tests require test credentials and test repos (TSK-023 may be partially blocked until those are provided).


