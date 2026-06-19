# OpenCode — Platform Instructions

## Init
OpenCode reads `AGENTS.md` as its system prompt (via `openspec.json`).
Skills are loaded from `ai/skills/` (configured in `openspec.json`).

## Skill loading
- Skills live in `ai/skills/<name>/SKILL.md`
- AGENTS.md lists all available skills — load the matching one before each task
- Automatic skills (like `popcorn-reference`) load at session start

## Customization
- `openspec.json` at root controls skill paths and instructions
- `.opencode/` directory contains tool-specific helper files (node_modules, etc.)

## Conventions
- Use `skill` tool to load a skill when a task matches its description
- Follow three-layer architecture; never import Gradle in domain
