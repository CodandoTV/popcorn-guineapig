# Gemini Code Assist — Platform Instructions

## Init file
`.gemini/context.md` at project root points to `AGENTS.md`.  
Gemini should read `AGENTS.md` at session start for full context.

## Conventions
- Follow three-layer architecture: presentation → domain → data
- Domain must never import `org.gradle.api.*`
- All Kotlin code uses explicit API mode (`explicitApi()`)
