# GitHub Actions Workflow Triggers

## Branch Naming Conventions

The GitHub Actions workflows in this repository are configured to trigger on pushes to branches that follow specific naming patterns. This ensures that CI/CD processes run for all relevant branches while maintaining control over when workflows execute.

## Supported Branch Patterns

Both the SonarQube Analyze (`maven.yml`) and SonarCloud (`build.yml`) workflows will trigger on pushes to branches matching these patterns:

### Core Branches
- `main` - The main/default branch

### Feature Branches  
- `feature*` - Any branch starting with "feature" (e.g., `feature/new-login`, `feature-123`)
- `features*` - Any branch starting with "features" (e.g., `features/data-store`)
- `feature/data-store` - Specifically included for backward compatibility

### Development Branches
- `branch*` - Any branch starting with "branch" (e.g., `branch/experimental`)

### Patch and Hotfix Branches
- `*-patch-*` - Any branch containing "-patch-" (e.g., `john-doe-patch-1`, `security-patch-urgent`)

### Testing Branches  
- `test-*` - Any branch starting with "test" (e.g., `test-copilot`, `test-performance`)

### Tool-Generated Branches
- `copilot/*` - Branches created by GitHub Copilot (e.g., `copilot/fix-bug-123`)
- `adeo*` - Tool-specific branches (e.g., `adeo`, `adeo-test`)

### Maintenance Branches
- `dependency-*` - Dependency update branches (e.g., `dependency-issues-pr`)
- `update-*` - General update branches (e.g., `update-commons-io`)

## Workflow Behavior

### On Push Events
Workflows will automatically trigger when code is pushed to any branch matching the above patterns.

### On Pull Request Events  
Workflows will trigger for pull requests regardless of the source branch name when:
- Pull request is opened
- New commits are pushed to the pull request
- Pull request is reopened

### Manual Triggers
Both workflows support manual triggering via `workflow_dispatch` from the GitHub Actions UI.

## Examples

✅ **These branches WILL trigger workflows on push:**
- `main`
- `feature/user-authentication`
- `features/data-processing`
- `branch/experimental-ui`
- `jane-smith-patch-1`
- `test-performance`
- `copilot/fix-security-issue`
- `adeo-integration`
- `dependency-updates`
- `update-spring-boot`

❌ **These branches will NOT trigger workflows on push:**
- `bugfix/critical-issue` (doesn't match any pattern)
- `hotfix/urgent` (doesn't match any pattern)
- `develop` (doesn't match any pattern)
- `release/v1.2.0` (doesn't match any pattern)

> **Note:** Even if a branch doesn't trigger workflows on push, it will still trigger workflows when a pull request is created from that branch.

## Updating Patterns

If your team uses different branch naming conventions, you can update the patterns in:
- `.github/workflows/maven.yml` (SonarQube Analyze workflow)
- `.github/workflows/build.yml` (SonarCloud workflow)

Look for the `on.push.branches` section in each file and add or modify the patterns as needed.