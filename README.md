# Automated Documentation Sync

## Project Overview

Automated Documentation Sync is a Java/Maven capstone that ingests user stories, validates and synchronizes canonical docs under docs/, and proposes changes via pull requests. It demonstrates an agentic SDLC using GitHub Copilot agents for requirements, architecture, design review, implementation, and verification.

## Business Problem

Keeping project documentation accurate and up-to-date is time-consuming. This project automates detection of missing, outdated, or inconsistent documentation and assists maintainers by generating PRs with proposed fixes while requiring human approval for significant changes.

## User Story

See docs/requirements.md for the full User Story and traceable requirements derived from it.

## Solution

A modular Java service that: ingests source artifacts (Word/JIRA), parses stories, validates docs, classifies changes, generates documentation updates, opens PRs, and produces verification reports. Significant changes require human approval before merge.

## Architecture

High-level components are described in docs/architecture.md: Ingestors, Parser/Normalizer, Validator, Change Classifier, Doc Generator, GitService/PR Manager, Report Generator, and RunStore.

## Technology Stack

- Java 17
- Maven
- JUnit 5
- GitHub Actions (CI)

## Project Structure

- docs/ — canonical SDLC artifacts (requirements, architecture, design-review, impl-plan, code-review, verification)
- src/main — production Java code
- src/test — unit & integration tests
- .github/ — Copilot customizations, workflows, and hooks

## How to Build

mvn -B -DskipTests=true clean package

## How to Run

Run the validator/runner locally after building:

java -cp target/classes com.capstone.validator.DocValidationRunner

## How to Test

Run unit and integration tests with:

mvn test

## Documentation

- Requirements: docs/requirements.md
- Architecture: docs/architecture.md
- Design Review: docs/design-review.md
- Implementation Plan: docs/impl-plan.md
- Code Review: docs/code-review.md
- Verification: docs/verification.md

## GitHub Copilot Agentic SDLC

Explain the agent-driven lifecycle in docs/ (high-level):
1. Requirements — Copilot Requirements Agent extracts FR/NFR/AC from user stories.
2. Architecture — Architecture Agent proposes component designs.
3. Design Review — Design Reviewer (human+agent) validates decisions.
4. Implementation Planning — Planning Agent breaks tasks into TSKs and orders them.
5. Implementation — Implementation Agent and humans produce code and tests.
6. Code Review — Code Review Agent evaluates production readiness; docs/code-review.md contains criteria.
7. Verification — QA/Verification Agent runs tests and generates evidence (docs/verification.md).
8. Pull Request — PR Agent opens PRs, triggers CI, and attaches validation artifacts.

## Copilot Customizations

### Instructions

Per-repo Copilot rules and specialized instructions are under .github/instructions/. See .github/copilot-instructions.md for global policies.

### Prompt Files

Reusable prompts for requirements, architecture, design-review, implementation-plan, code-review, verification, and PR generation live in .github/prompts/.

### Skills

Specialized agent skill: .github/skills/documentation-sync/SKILL.md — describes the Documentation Sync capability and expected behaviors.

### Hooks

Automated quality checks and agent controls are defined in .github/hooks/quality-checks.json.

## Known Limitations

- DocumentationValidator is regex-based and conservative; migration to a Markdown AST parser (flexmark) is recommended.
- Many components (Ingestors, Parser, PR Manager, RunStore) are planned but not yet implemented.
- Local builds require Maven; CI provides full test execution.

## Future Enhancements

- Full ingestors for Word/JIRA/Confluence
- Robust Markdown parsing and generation
- RunStore for tracking runs & PRs
- GitHub App-based PR manager and approval workflow
- Metrics, monitoring, and audit trail export

