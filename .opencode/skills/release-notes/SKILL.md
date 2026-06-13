---
name: release-notes
description: Automated release workflow — reads version, categorizes unreleased commits, bumps semver, and updates CHANGELOG.md.
---

# Release Notes

Automates version bumping and changelog generation for the Popcorn GuineaPig plugin.

## When to use

- Preparing a new release
- Before publishing to Maven Central

## Steps

### 1. Read current version

Read `popcornguineapigplugin/version.properties`:

```
VERSION=<current>
```

### 2. Get latest git tag

```bash
git describe --tags --abbrev=0
```

### 3. List unreleased commits

```bash
git log <latest-tag>..HEAD --oneline
```

### 4. Categorize commits for semver bump

Scan each commit message for conventional prefixes:

| Prefix | Bump | Example |
|--------|------|---------|
| `breaking`, `BREAKING CHANGE`, `!` | **Major** | `feat!: change API signature` |
| `feat`, `feature`, `Add` | **Minor** | `feat: add metrics csv` |
| `fix`, `patch`, `chore`, `Bump`, `Update`, `Merge`, `Refactor`, `Docs` | **Patch** | `Bump version; Update SCM URL` |

- If **any** commit is breaking → major bump
- Else if **any** commit is a feature → minor bump
- Else → patch bump

### 5. Increment version

Update the `VERSION` property in `popcornguineapigplugin/version.properties`.

### 6. Create or update CHANGELOG.md

Prepend a new entry to `CHANGELOG.md` in this format:

```markdown
## [<new-version>] - <date>

### Breaking
- ...

### Added
- ...

### Fixed
- ...

### Changed
- ...
```

Group commits by category under the appropriate heading.

### 7. Prompt user

After changes are made, prompt the user to:

```bash
git add popcornguineapigplugin/version.properties CHANGELOG.md
git commit -m "Bump to <new-version>; Update CHANGELOG"
git tag v<new-version>
```

Do **not** commit or tag automatically — only present the commands.
