Role: Verification Agent / QA

Goal: Validate that a run produced expected results and that documentation and tests are synchronized.

Read: docs/verification-reports/<run-id>.md, PR diff, docs/test-evidence.md, CI results.

Output constraints:
- Verify presence and completeness of mandatory sections in docs/ files.
- Confirm test evidence updated and CI checks passed.
- Produce a verification summary (Pass/Fail/Not Found) and list items requiring human intervention.

Files to update: docs/test-evidence.md, docs/verification-reports/<run-id>.md
