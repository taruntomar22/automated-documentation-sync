# Documentation Sync Skill

Purpose

This skill provides guidance and automation hooks for synchronizing and validating documentation for the repository. It is intended to be used by automation agents and developers to understand how the Documentation Sync Engine operates and how to safely interact with it.

Capabilities

- Ingest user stories from JIRA, Confluence, or Word.
- Parse and normalize content to canonical docs/ templates.
- Validate mandatory sections and produce consolidated verification reports.
- Create feature branches and open Pull Requests for automated changes.
- Generate verification reports under docs/verification-reports/.

Usage

- The skill is invoked by the Documentation Sync Engine during processing runs.
- Follow .github/copilot-instructions.md and .github/instructions/*.md for operational rules.

Safety

- Never auto-merge PRs.
- Sanitize external inputs and redact PII.
- Use least-privilege tokens and GitHub Secrets for credentials.
