# Code Review Guidance

This document describes the code-review checklist, expected standards, and mapping to requirements for the Automated Documentation Sync project. Use this checklist when reviewing PRs produced by automation or developers.

## Purpose
Provide reviewers a consistent checklist to evaluate correctness, security, maintainability, test coverage, and requirement traceability.

## Process
- Verify PR includes a clear Summary and Changes Made per .github/pull_request_template.md.
- Run local or CI tests before approving.
- Use the automated prompts in .github/prompts/ for structured review assistance.

## Checklist (must review every item)
- Correctness
  - [ ] Code implements the claimed requirement IDs (FR-/NFR-/AC-). Map files to requirement IDs in PR comments.
  - [ ] Edge cases handled (null, empty, malformed input).
- Security
  - [ ] No secrets or credentials in code or tests.
  - [ ] Sensitive values masked in logs and reports.
  - [ ] Least-privilege tokens used and documented.
- Error handling
  - [ ] External calls have retries/backoff and timeouts.
  - [ ] Exceptions are not swallowed silently.
  - [ ] Failures produce helpful telemetry and are surfaced to reports.
- Tests
  - [ ] Unit tests exist for new logic and pass in CI.
  - [ ] Integration tests for adapters exist or are planned with fixtures.
  - [ ] Test names describe behavior and map to requirement IDs.
- Code clarity & structure
  - [ ] Classes follow single responsibility.
  - [ ] DTOs are immutable where practical.
  - [ ] No large methods; well-named variables and methods.
- DRY & dependencies
  - [ ] No duplicated logic; common utilities extracted.
  - [ ] Dependencies are pinned and reviewed for vulnerabilities.
- Documentation
  - [ ] New public APIs and behaviors documented.
  - [ ] Mandatory docs headings preserved and updated if changes are functional.

## Common review findings and suggestions
- Missing tests: Ask for unit tests covering happy and error paths.
- Unclear error handling: Request explicit error mapping and user-facing messages in reports.
- Overly broad catch blocks: Request narrowing of exception types and rethrow where appropriate.
- Untested external calls: Request WireMock or recorded fixtures.

## PR comment templates
- Failure summary (example):
  "Automated quality checks detected failing unit tests: see target/surefire-reports. Key failure: com.capstone.XTest#shouldHandleY — NullPointerException at X.java:42. Recommendation: add null guard and unit test." 

## Mapping to requirements
- Use this mapping to reference requirement IDs during review:
  - Parser/Normalizer -> FR-001
  - Validator -> FR-002, FR-003
  - DocGenerator, GitService -> FR-004, FR-005
  - Classifier -> FR-006, FR-007
  - Reporter -> FR-011
  - Secrets & Auth -> NFR-001


