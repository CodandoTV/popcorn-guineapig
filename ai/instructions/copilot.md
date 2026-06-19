# GitHub Copilot — Platform Instructions

## Init file
`.github/copilot-instructions.md` at project root points to `AGENTS.md`.  
Copilot should read `AGENTS.md` at session start for full context.

## Conventions
- Follow three-layer architecture: presentation → domain → data
- Domain must never import `org.gradle.api.*`
- All Kotlin code uses explicit API mode (`explicitApi()`)
