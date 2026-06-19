---
name: documentation-review
description: Validate docs/ content — check links, config examples, rule references, and cross-reference with README.
---

# Documentation Review

Scans all documentation files in `docs/` and validates links, config examples, rule references, and cross-references with `README.md`.

## When to use

- Before a release
- After adding new rules or features
- After restructuring documentation

## Steps

### 1. Scan documentation files

Collect all `.md` files under `docs/`:

```bash
ls docs/*.md
```

Common files: `index.md`, `1-getting-started.md`, `2-existing-rules.md`, `3-custom-rules.md`, `4-error-report.md`, `5-metrics-report.md`, `6-contributions.md`, `7-automating-popcorngp-with-github-cicd.md`

### 2. Validate links

**Internal links** — for each markdown link that references a local path, verify the target file exists.

**External links** — check that URLs are reachable by fetching the URL.

**Anchor links** — for internal links with anchors, verify the anchor string exists in the target document.

### 3. Verify JSON config examples

Search for JSON code blocks in documentation. Verify examples match the actual config schema:
- `DependencyRulesConfig` — should contain `rules` array with `rule` type and parameters
- `RuleConfig` — should contain `rule` (class name), `from`, `to`, etc.

Cross-check with actual source types in `domain/input/PopcornChildConfiguration.kt` and the rule classes in `domain/rules/`.

### 4. Confirm rules described in docs exist in source

| Doc reference | Source file |
|---------------|-------------|
| `NoDependencyRule` | `domain/rules/NoDependencyRule.kt` |
| `JustWithRule` | `domain/rules/JustWithRule.kt` |
| `DoNotWithRule` | `domain/rules/DoNotWithRule.kt` |

### 5. Cross-check setup instructions with README

- Both must reference the same Maven coordinate: `io.github.codandotv:popcornguineapig:<version>`
- Both must use the same plugin ID: `io.github.codandotv.popcorngpparent`
- Both must reference the same task: `popcornParent`
- Any configuration DSL examples must match

### 6. Report issues

Return a summary of broken or missing links, config mismatches, rule discrepancies, and inconsistencies between README and docs.
