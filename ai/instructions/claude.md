# Claude Code — Platform Instructions

## Init file
`CLAUDE.md` at project root points to `AGENTS.md`.  
Claude should read `AGENTS.md` at session start for full context, then load
the relevant skill from `ai/skills/` before each task.

## Conventions
- Follow three-layer architecture: presentation → domain → data
- Domain must never import `org.gradle.api.*`
- All Kotlin code uses explicit API mode (`explicitApi()`)
- Tests use JUnit 4 + Kotlin Test, mirror source structure under `src/test/`
