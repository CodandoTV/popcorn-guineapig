# ADR-0001: Migrate documentation from MkDocs/Material for MkDocs to Zensical

## Status

Accepted

## Context

MkDocs, the underlying static site generator that Material for MkDocs builds on, is being deprecated. The Material for MkDocs team (who also created Zensical) recommends migrating to Zensical — a modern static site generator that consolidates both MkDocs and Material for MkDocs into one coherent stack. Zensical is open source (MIT), reads existing `mkdocs.yml` as-is, and is designed for seamless compatibility with existing MkDocs projects. As of the decision date, Zensical is in alpha (v0.0.46) with 44 releases over 13 months, showing active development.

## Decision

Migrate the project's documentation toolchain from MkDocs + Material for MkDocs to Zensical.

Specifically:
- Replace `pip install mkdocs-material` with a pinned `pip install zensical==<version>` in CI
- Replace `mkdocs gh-deploy --force` with `zensical build` followed by `peaceiris/actions-gh-pages` for GitHub Pages deployment
- Keep `mkdocs.yml` unchanged — Zensical reads it directly
- Pin the Zensical version and bump manually

## Considered Options

- **Stay on MkDocs/Material for MkDocs**: viable short-term but the upstream project (MkDocs) is being deprecated, creating a maintenance dead end.
- **Switch to a different static site generator** (e.g., Docusaurus, Hugo): would require a full content and configuration rewrite.
- **Zensical**: minimal migration cost (drop-in compatibility), same team, same ecosystem, same configuration format.

## Consequences

- Zensical is alpha software — there is risk of breaking changes between versions, which is why we pin and bump manually
- No built-in `gh-deploy` command — must use `peaceiris/actions-gh-pages` action instead
- `mkdocs serve` workflow becomes `zensical serve` for local development
- Configuration (`mkdocs.yml`) and content (`docs/`) require no changes
- MkDocs can remain installed locally as a fallback during the transition
