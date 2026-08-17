# Copilot Instructions (Agent Guidance)

Purpose

This file instructs GitHub Copilot cloud agent and other automation about repository conventions, allowed operations, and safety constraints for the Automated Documentation Sync project.

Scope

- Read-only access to repository content unless acting through the documented Git Service & PR Manager.
- Create feature branches, commit documentation updates, and open Pull Requests for all automated changes.
- Never merge PRs automatically. Human approval (Project Owner) is required to merge.

Agent responsibilities

- Ingest user story inputs (JIRA/Confluence/Word) via configured Ingestors.
- Parse, validate, and prepare documentation changes under docs/.
- Create branch and PR using the canonical .github/pull_request_template.md; include run-id and trace metadata.
- Generate consolidated verification reports under docs/verification-reports/<run-id>.md and <run-id>.json.

Constraints & Safety

- Do not store or commit secrets. Use environment/GitHub Actions secrets.
- Sanitize external content before committing. Strip scripts and unsafe markup.
- Respect the Significant-change rules: flag Significant changes and require human approval.
- Use configured branch naming: docs-sync/{source}/{id}/{short-hash}.

Operational notes

- Build: mvn clean package
- Test: mvn test
- Config via environment variables or application.yml in CI.
- Use least-privilege tokens (prefer GitHub App in production).

Contacts

- Project Owner / Approver: Capstone Developer (repository owner).