# Cursor — Platform Instructions

## Init file
`.cursorrules` at project root points to `AGENTS.md`.  
Cursor should read `AGENTS.md` at session start for full context, then load
the relevant skill from `ai/skills/` before each task.

## Conventions
- Follow three-layer architecture: presentation → domain → data
- Domain must never import `org.gradle.api.*`
- All Kotlin code uses explicit API mode (`explicitApi()`)
