---
agent: 'agent'
description: 'Perform a structured senior Java code review'
---

Act as a senior Java code reviewer.

Read:
- docs/requirements.md
- docs/architecture.md
- docs/design-review.md
- The current implementation in the repository

Review the current implementation for:

1. Correctness
2. Security
3. Error handling
4. Test coverage
5. Code clarity
6. DRY (Don't Repeat Yourself)
7. Dependency safety

Do not modify code.

Produce a structured findings list where each entry contains:
- Finding (short title)
- Severity (Critical / High / Medium / Low)
- Evidence (file paths, code snippets, test names, logs)
- Recommendation (concrete fix or mitigation)

Use requirement IDs (FR-..., NFR-..., AC-...) when the finding maps to a requirement. Prioritize findings by severity.
