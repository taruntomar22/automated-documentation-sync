# Architecture

## Architecture overview

This document defines the approved architecture for the Automated Documentation Sync service. The system is a modular, event-driven Java application that ingests user stories from external sources (JIRA, Confluence, Word), normalizes and validates content against canonical repository documentation in docs/, generates or updates documentation in feature branches, opens Pull Requests for every change, and produces consolidated verification reports. All substantive changes require explicit human approval before merge.

## Architecture diagram (Mermaid)

```mermaid
flowchart LR
  subgraph External
    JIRA[JIRA]
    Confluence[Confluence]
    Word[Word (.docx)]
  end

  Trigger[Trigger: webhook/schedule/manual]
  Ingestor[Ingestors / Adapters]
  Parser[Parser / Normalizer]
  Validator[Validator]
  Classifier[Change Classifier]
  Generator[Doc Generator / Synchronizer]
  GitSvc[Git Service & PR Manager]
  CI[CI Orchestrator (GitHub Actions)]
  Reporter[Report Generator]
  Auth[Auth & Secrets Manager]
  Logs[Audit & Logging]

  Trigger --> Ingestor
  JIRA --> Ingestor
  Confluence --> Ingestor
  Word --> Ingestor
  Ingestor --> Parser
  Parser --> Validator
  Validator --> Classifier
  Classifier --> Generator
  Generator --> GitSvc
  GitSvc --> CI
  CI --> Reporter
  GitSvc --> Reporter
  Parser --> Logs
  GitSvc --> Logs
  Reporter --> Logs
  Auth --> Ingestor
  Auth --> GitSvc
```

## Component diagram (Mermaid)

```mermaid
flowchart TB
  subgraph Core
    Orchestrator[Orchestration / Scheduler / Event Listener]
    Ingestor[Ingestors / Adapters]
    Parser[Parser / Normalizer]
    Validator[Validator]
    Classifier[Change Classifier]
    Generator[Doc Generator / Synchronizer]
    GitSvc[Git Service & PR Manager]
    Reporter[Report Generator]
  end

  subgraph Infra
    Auth[Auth & Secrets Manager]
    CI[CI Orchestrator (GitHub Actions)]
    Logs[Audit & Logging]
  end

  Orchestrator --> Ingestor --> Parser --> Validator --> Classifier --> Generator --> GitSvc --> CI
  GitSvc --> Reporter --> Logs
  Parser --> Logs
  Auth --> Ingestor
  Auth --> GitSvc
```

## Components and responsibilities

- Orchestration / Scheduler / Event Listener
  - Receive triggers: webhooks (preferred), scheduled runs, or manual CLI/CI invocations.
  - Manage run lifecycle, assign run-id, implement debounce window for event batching.

- Ingestors / Adapters
  - Connect to external sources. For JIRA/Confluence, prefer REST APIs; for Word, parse .docx uploads with Apache POI.
  - Retrieve raw content and attachments and forward to Parser.

- Parser / Normalizer
  - Convert raw input into a structured model (title, description, acceptance criteria, fields).
  - Map fields to canonical doc headings (per templates) using configurable rules.

- Validator
  - Inspect repository canonical docs and verify mandatory headings/sections.
  - Produce validation findings (Passed / Failed / Not Found).

- Change Classifier
  - Compute diffs between existing docs and proposed content.
  - Classify changes as Significant or Minor using configured rules (e.g., edits to requirements.md/architecture.md, changes >10 lines).

- Doc Generator / Synchronizer
  - Produce updated document content that preserves mandatory headings.
  - Insert skeletons for missing sections.
  - Prepare commit metadata linking changes to run-id and source.

- Git Service & PR Manager
  - Use JGit for local operations and GitHub REST API for PR creation.
  - Create feature branch, commit changes, push, and open PR populated by .github/pull_request_template.md.

- CI Orchestrator
  - Trigger GitHub Actions workflows to run build and test suites for each PR.
  - Surface gating status within PR checks.

- Report Generator
  - Produce consolidated verification reports (Markdown for human review and JSON for machine-reading) stored under docs/verification-reports/<run-id>.
  - Include traceability metadata (source id, run-id, diffs, files changed, PR link).

- Auth & Secrets Manager
  - Acquire credentials from environment/GitHub Secrets and provide tokens with least privilege to components.

- Audit & Logging
  - Structured logs with timestamps, run-id, actor, and change metadata. Store logs in CI run output and optionally persist reports in docs/.

## Data flow

1. Trigger (webhook, schedule, or manual) initiates run.
2. Orchestrator assigns run-id and invokes Ingestor(s) for the target user story.
3. Ingestor fetches content and passes to Parser.
4. Parser normalizes content into structured model.
5. Validator compares model to canonical docs and identifies gaps.
6. Classifier computes diffs and significance.
7. Generator prepares updated docs preserving mandatory headings and creates commit metadata.
8. GitSvc creates feature branch, commits updates, and pushes branch.
9. GitSvc opens Pull Request using canonical template.
10. CI runs test suite; results are attached to PR checks.
11. Reporter writes reports to docs/verification-reports/<run-id>.md and <run-id>.json and links report in PR.
12. Project Owner reviews PR, approves, and merges into main.

## API / Interface boundaries

- Ingestor adapters expose a common interface: ingest(sourceDescriptor) -> StructuredStory
- Parser exposes: parse(rawContent) -> StructuredStoryModel
- Validator exposes: validate(StructuredStoryModel) -> ValidationFindings
- Generator exposes: generateChanges(StructuredStoryModel, ValidationFindings) -> ChangeSet
- GitSvc exposes: createBranch(commitMeta, ChangeSet) -> BranchInfo; openPullRequest(BranchInfo, PRBody) -> PRInfo
- Reporter exposes: generateReport(runId, ChangeSet, PRInfo, Findings) -> {mdPath, jsonPath}
- Components communicate via in-process method calls or well-defined DTOs; for scalability, message queueing (optional) isolates components via messages.

## Error handling

- Transient external errors: retry with exponential backoff (resilience4j) and configurable max attempts.
- Fatal parsing errors: abort update, produce diagnostic report with error details and stacktrace, and create no PR for that run (optionally open an "investigate" issue).
- Partial failure: operations are transactional per run; Git commits are atomic per ChangeSet; if commit/push fails, leave no changes in repo and include failure in report.
- Merge conflicts: GitSvc creates PR and annotates conflict details; do not auto-resolve.

## Security architecture

- Secrets management: read tokens from environment/GitHub Actions secrets; never store secrets in repo or logs.
- Least-privilege: tokens scoped to necessary APIs (GitHub: repo and pull_request scopes; JIRA/Confluence: read-only unless configured otherwise).
- Network: use HTTPS/TLS for all calls.
- Data minimization: mask sensitive fields in reports; redact secrets from logs.
- Audit: include run-id, user story source, and change metadata in every report and log entry.

## Logging

- Use structured logging (JSON) via SLF4J + Logback for easy parsing.
- Log levels: DEBUG for local runs, INFO for production runs, WARN/ERROR for exceptions.
- Include run-id, component, correlation id, and user story id in each log entry.
- Persist logs via CI run artifacts; store verification reports in repo for traceability.

## Configuration

- Externalized configuration (application.yml or environment variables):
  - GitHub token & repo details
  - JIRA/Confluence endpoints & credentials
  - Debounce window and trigger settings
  - Significant-change thresholds (line count N)
  - Report storage path and retention policy
  - Retry/backoff settings

## Test architecture

- Unit tests: isolated, fast, mock external adapters
- Integration tests: run against a test GitHub repo and sample JIRA/Confluence/Word fixtures
- Acceptance tests: end-to-end run in GitHub Actions (ingest -> PR -> report)
- Test data: stored under test/resources with sample user stories and expected outputs

## Deployment considerations

- Runs as a scheduled/triggered job inside GitHub Actions or as a standalone service invoked by webhooks.
- For capstone: implement as GitHub Actions workflow that executes Java application; Runner will provide required secrets via Actions secrets.
- For production: deploy as stateless microservice (container) behind an API endpoint; use message queue for high throughput.

## Design trade-offs

- JGit + GitHub REST vs direct shell git: JGit is pure Java and cross-platform; chosen for maintainability at the cost of learning curve.
- REST APIs vs Selenium scraping: REST is more reliable and preserves intent; Selenium avoided per requirement.
- In-process vs message-driven: in-process is simpler for capstone; message queue adds complexity but improves resilience and scalability.

## Alternatives considered

- Python implementation: faster prototyping, but rejected due to Java requirement.
- GitHub App for finer permissions: recommended for production but not required for capstone.
- Persisting reports to external dashboard: deferred; storing reports in repo provides traceability.

## Requirement traceability

- Maps to requirements in docs/requirements.md (FR-001 .. FR-013, NFR-001 .. NFR-007, AC-001 .. AC-010)
- Orchestration, Ingestor, Parser -> FR-001
- Validator -> FR-002, FR-003
- Generator & GitSvc -> FR-004, FR-005, FR-009
- Classifier -> FR-006, FR-007, FR-008
- Reporter -> FR-011
- Auth & Secrets Manager -> NFR-001, NFR-006
- CI Orchestrator -> FR-013, AC-008

