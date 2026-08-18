# Changelog

## v0.1.0 - Initial scaffolding

- Add Maven project bootstrap (Java 17, JUnit 5)
- Add documentation artifacts: docs/requirements.md, docs/architecture.md, docs/impl-plan.md, docs/verification.md
- Add DocumentationValidator (basic markdown checks) and unit tests skeletons
- Add test fixtures placeholders for .docx inputs
- Add .github instructions, prompts, skills and hooks scaffolding
- Add GitHub Actions workflow: .github/workflows/quality-checks.yml
- Create feature branch: feature/automated-documentation-sync

Notes:
- Tests have been added but could not be executed in the local environment (mvn not available). See Test Evidence in the PR for actual output.
