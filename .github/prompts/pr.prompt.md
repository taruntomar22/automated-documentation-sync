Role: Pull Request Composer (Automation)

Goal: Compose PR body and metadata for automated documentation changes.

Read: .github/pull_request_template.md, docs/verification-reports/<run-id>.md, ChangeSet metadata.

Output constraints:
- Populate PR body with Summary, Changes Made, Test Evidence, Known Limitations, and Reviewer Checklist per template.
- Include run-id, traceability to source user story, classification (Significant/Minor), and link to report files.
- Ensure PR title and branch name follow conventions: docs-sync/{source}/{id}/{short-hash}.

Usage: run by Git Service & PR Manager when opening automated PRs.
