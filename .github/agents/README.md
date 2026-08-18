# Copilot Agents Configuration

This directory contains configurations for GitHub Copilot agents that automate SDLC tasks in the Automated Documentation Sync project.

## Agent Architecture

### Two-Tier Agent System

```
┌─────────────────────────────────────────────────┐
│  SDLC Orchestrator Agent                        │
│  • Lifecycle Management                         │
│  • Workflow Coordination                        │
│  • Quality Gate Decisions                       │
│  • Progress Tracking                            │
└──────────────┬──────────────────────────────────┘
               │
               │ Delegates Tasks
               │
┌──────────────v──────────────────────────────────┐
│  SDLC Engineer Agent                            │
│  • Feature Development                          │
│  • Testing & Quality Assurance                  │
│  • Code Review                                  │
│  • Documentation Maintenance                    │
└─────────────────────────────────────────────────┘
```

## Agent Descriptions

### SDLC Orchestrator Agent
**File**: `sdlc-orchestrator.agent.md`

Controls the overall project lifecycle and orchestrates collaboration between agents. Makes strategic decisions about project progression based on quality metrics and completion criteria.

**Key Responsibilities**:
- Analyze requirements and create execution plans
- Coordinate between SDLC Engineer Agent and specialized agents
- Monitor task progress and quality gates
- Verify completion against acceptance criteria
- Manage project timeline and dependencies
- Escalate blockers and risks

### SDLC Engineer Agent
**File**: `sdlc-engineer.agent.md`

Executes engineering tasks assigned by the Orchestrator. Implements features, writes tests, performs code reviews, and maintains documentation.

**Key Responsibilities**:
- Implement assigned features or fixes
- Create comprehensive tests for changes
- Conduct code reviews for quality and security
- Keep documentation synchronized with code
- Ensure all quality checks pass
- Address feedback and iterate on solutions

## Workflow Integration

### CI/CD Pipeline
The agents are initialized and verified through the `copilot-setup-steps.yml` workflow which:
1. Sets up the Java development environment (JDK 17, Maven)
2. Configures the SDLC Orchestrator Agent
3. Configures the SDLC Engineer Agent
4. Validates agent integration and readiness

### Task Execution Flow
1. **Orchestrator** receives project requirements or issues
2. **Orchestrator** analyzes scope and creates execution plan
3. **Orchestrator** delegates specific tasks to **Engineer**
4. **Engineer** implements solution following quality standards
5. **Engineer** submits work through pull request
6. **Orchestrator** verifies against acceptance criteria
7. **Orchestrator** decides next phase or escalates if needed

## Quality Gates

Both agents must satisfy these criteria:
- ✅ All unit tests passing
- ✅ Code follows style guidelines
- ✅ No security vulnerabilities detected
- ✅ Documentation updated and validated
- ✅ PR review approved
- ✅ CI/CD pipeline checks pass

## Environment Setup

### Required Tools
- Java 17 (Temurin distribution)
- Maven 3.8.1+
- Git 2.30+

### Dependencies
- JUnit 5.9.3 for testing
- Maven Surefire 3.0.0-M7 for test execution
- Maven Compiler Plugin for compilation

## Configuration Files

- `.github/agents/sdlc-orchestrator.agent.md` - Orchestrator agent specification
- `.github/agents/sdlc-engineer.agent.md` - Engineer agent specification
- `.github/workflows/copilot-setup-steps.yml` - Agent initialization workflow
- `.github/workflows/quality-checks.yml` - Quality assurance workflow

## Task Assignment Protocol

### From Orchestrator to Engineer
Tasks should include:
- Clear acceptance criteria
- Required testing scope
- Documentation requirements
- Linked issue/requirement IDs
- Priority and timeline
- Any constraints or dependencies

### From Engineer to Orchestrator
Status updates should include:
- Completion status (done/blocked/in-progress)
- Quality metrics (test pass rate, code coverage)
- Any blockers or risks identified
- PR links for code review
- Requested guidance or escalation needs

## Customization

To add custom agent configurations:
1. Create new file in `.github/agents/` directory
2. Follow the existing agent specification format
3. Update `copilot-setup-steps.yml` to initialize new agent
4. Document agent role, capabilities, and responsibilities
5. Define integration points with existing agents

## Troubleshooting

### Agent Initialization Fails
- Check `copilot-setup-steps.yml` workflow logs
- Verify Java and Maven are correctly installed
- Ensure all dependencies are available

### Quality Checks Failing
- Review logs in `quality-checks.yml` workflow
- Check for missing tests or coverage gaps
- Verify documentation updates match code changes

### Task Coordination Issues
- Review agent communication protocol
- Check for circular dependencies
- Verify acceptance criteria are clear and measurable
