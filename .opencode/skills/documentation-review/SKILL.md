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

**Internal links** — for each markdown link that references a local path (e.g. `../docs/2-existing-rules.md`), verify the target file exists.

**External links** — check that URLs (e.g. `https://codandotv.github.io/popcorn-guineapig`, Maven Central, GitHub) are reachable by fetching the URL.

**Anchor links** — for internal links with anchors (e.g. `#22-parent-plugin`), verify the anchor string exists in the target document.

### 3. Verify JSON config examples

Search for JSON code blocks in documentation. Verify examples match the actual config schema:

- `DependencyRulesConfig` — should contain `rules` array with `rule` type and parameters
- `RuleConfig` — should contain `rule` (class name), `from`, `to`, etc.

Cross-check with actual source types in `domain/input/PopcornChildConfiguration.kt` and the rule classes in `domain/rules/`.

### 4. Confirm rules described in docs exist in source

Check that every rule documented in `docs/2-existing-rules.md` has a corresponding implementation in `domain/rules/`:

| Doc reference | Source file |
|---------------|-------------|
| `NoDependencyRule` | `domain/rules/NoDependencyRule.kt` |
| `JustWithRule` | `domain/rules/JustWithRule.kt` |
| `DoNotWithRule` | `domain/rules/DoNotWithRule.kt` |

Also verify custom rule instructions in `docs/3-custom-rules.md` match the `PopcornGuineaPigRule` interface in `domain/rules/PopcornGuineaPigRule.kt`.

### 5. Cross-check setup instructions with README

Compare the setup guide in `docs/1-getting-started.md` with `README.md`:

- Both must reference the same Maven coordinate: `io.github.codandotv:popcornguineapig:<version>`
- Both must use the same plugin ID: `io.github.codandotv.popcorngpparent`
- Both must reference the same task: `popcornParent`
- Any configuration DSL examples must match

### 6. Report issues

Return a summary of:

- Broken or missing links (file path + suggested fix)
- Config examples that don't match the schema (file path + what needs changing)
- Rules referenced in docs but missing from source (or vice versa)
- Inconsistencies between README and docs
