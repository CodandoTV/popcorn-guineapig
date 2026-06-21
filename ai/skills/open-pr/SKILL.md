---
name: open-pr
description: Use when opening a new pull request. Triggers on phrases like "open PR", "create PR", "submit PR".
---

# Open Pull Request

Opens a pull request from the current feature branch to `main` with a
description auto-generated from the git diff and commit history.

## When to use

- Opening a PR for a feature branch
- Before requesting review
- After making changes and pushing

## Steps

### 1. Verify prerequisites

```bash
git status
git branch --show-current
```

- Must NOT be on `main`
- Working tree must be clean (no uncommitted changes)
- Remote `origin` should point to `CodandoTV/popcorn-guineapig`

### 2. Collect commits between main and current branch

```bash
git log main..HEAD --oneline
```

### 3. Collect changed files

```bash
git diff --stat main..HEAD
```

### 4. Categorize commits

Assign each commit to a category based on its prefix:

| Prefix | Category |
|--------|----------|
| `feat`, `feature`, `Add` | Added |
| `fix`, `patch` | Fixed |
| `chore`, `Bump`, `Update`, `Merge` | Changed |
| `refactor`, `Refactor` | Changed |
| `docs`, `Docs` | Documentation |
| `breaking`, `BREAKING CHANGE`, `!` | Breaking |

### 5. Generate PR title

Derive from the branch name: strip common prefixes (`feature/`, `fix/`,
`gmoro/`, `improvement/`, `update/`), replace hyphens with spaces,
capitalize words.

Fallback: use the first commit message.

### 6. Generate PR description

Format the description using the categorized commits and changed files:

```markdown
## What changed

### <Category>
- <commit message without prefix>

## Files changed
- `<path>` (+N/-M)

## Checklist
- [ ] Architecture: files in correct layer? No Gradle imports in domain?
- [ ] Tests: new tests added? Coverage maintained?
- [ ] Build: `./gradlew popcornguineapigplugin:build` passes?
```

### 7. Open the PR

```bash
gh pr create --base main --title "<title>" --body "<body>"
```

Return the PR URL.

### 8. Prompt for labels or reviewers

Ask the user if they want to add labels or reviewers:

```bash
gh pr edit <url> --add-label "<label>" --add-reviewer "<reviewer>"
```

Do **not** add labels or reviewers automatically — ask first.
