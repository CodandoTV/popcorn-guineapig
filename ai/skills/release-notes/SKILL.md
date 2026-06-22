---
name: release-notes
description: Automated release workflow — reads version, categorizes unreleased commits, bumps semver, and updates CHANGELOG.md.
---

# Release Notes

Automates version bumping and changelog generation for the Popcorn GuineaPig plugin.

## When to use

- Preparing a new release
- Generating a changelog description for the latest unreleased changes
- Before publishing to Maven Central

## Steps

### 1. Read current version

Read `popcornguineapigplugin/version.properties`:

```
VERSION=<current>
```

### 2. Find the previous version

Use the **CHANGELOG.md** as the source of truth (tags may not exist):

1. Read `CHANGELOG.md`
2. Extract the first `## [<version>]` header — that is the **previous** released version

Fallback: if CHANGELOG.md has no version headers, use `git describe --tags --abbrev=0`.

### 3. Find the commit that last updated CHANGELOG.md

```bash
git log --oneline --diff-filter=M -- CHANGELOG.md | head -1
```

This gives the commit where the previous version's changelog entry was written.

### 4. List unreleased commits

```bash
git log <changelog-commit>..HEAD --oneline --no-merges
```

Exclude merge commits (they don't carry meaningful change descriptions).

### 5. Show changed files

```bash
git diff --stat <changelog-commit>..HEAD
```

Use this to understand the scope and identify areas affected (docs, source, tests, CI).

### 6. Categorize commits for semver bump

| Prefix / Pattern | Bump | Category |
|------------------|------|----------|
| `breaking`, `BREAKING CHANGE`, `!` | **Major** | Breaking |
| `feat`, `feature`, `Add` | **Minor** | Added |
| `fix`, `patch`, `Fixed` | **Patch** | Fixed |
| `chore`, `Bump`, `Update`, `Merge` | **Patch** | Changed |
| `refactor`, `Refactor`, `improve` | **Patch** | Changed |
| `docs`, `Docs`, `update docs` | **Patch** | Changed |

- If **any** commit is breaking → major bump
- Else if **any** commit is a feature → minor bump
- Else → patch bump

### 7. Categorize changes for CHANGELOG

Review both the commit messages AND the `git diff --stat` output to write meaningful changelog entries. Group by category:

- **Added** — new features, tasks, DSL fields, skills
- **Fixed** — bug fixes
- **Changed** — refactors, docs updates, minor improvements, dependency bumps

Tips:
- Look at the diff of key source files to understand new features at the architectural level (domain/data/presentation)
- New files under `docs/` → documentation updates
- New files under `ai/skills/` → new AI skills
- Changes in `ServiceLocator.kt` → new dependencies wired in

### 8. Increment version

Update the `VERSION` property in `popcornguineapigplugin/version.properties`.

### 9. Update CHANGELOG.md

Prepend a new entry to `CHANGELOG.md` **after the header line** (before the previous version entry):

```markdown
## [<new-version>]

### Added
- ...

### Fixed
- ...

### Changed
- ...
```

Omit sections that have no entries. Follow the existing format — no date suffix needed (unlike earlier versions).

### 10. Prompt user

After changes are made, prompt the user to:

```bash
git add popcornguineapigplugin/version.properties CHANGELOG.md
git commit -m "chore: bump to <new-version>; update CHANGELOG"
git tag v<new-version>
```

Do **not** commit or tag automatically — only present the commands.
