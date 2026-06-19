# AI Context Structure — What & Why

This directory centralizes all AI coding assistant context into a single
source of truth. Instead of maintaining separate, diverging files for
each AI tool (OpenCode, Claude Code, Cursor, Copilot, Gemini), every
assistant reads from the same `AGENTS.md` -> `ai/` hierarchy.

## Flow

```
                  ┌─────────────────┐
                  │   AGENTS.md     │  ← Master initializer (root)
                  │  (one file)     │
                  └────────┬────────┘
                           │
                    points to ai/
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ai/instructions/  ai/skills/  ai/module-graph.md
     (per-platform)   (per-task)   (dependency map)
```

Each native init file (`CLAUDE.md`, `.cursorrules`, etc.) is a lightweight
pointer: "Read AGENTS.md at the start of every session for full context."
No rules or structure lives there — everything is in `AGENTS.md` + `ai/`.

## Files

| File | Read by | Purpose |
|------|---------|---------|
| `AGENTS.md` | All AI assistants | Master initializer — project info, rules, skills, workflow |
| `ai/module-graph.md` | All AI assistants | Explicit module dependency graph for the project |
| `ai/instructions/opencode.md` | OpenCode | OpenCode-specific patterns and conventions |
| `ai/instructions/claude.md` | Claude Code | Claude Code-specific patterns and conventions |
| `ai/instructions/cursor.md` | Cursor | Cursor-specific patterns and conventions |
| `ai/instructions/copilot.md` | GitHub Copilot | Copilot-specific patterns and conventions |
| `ai/instructions/gemini.md` | Gemini Code Assist | Gemini-specific patterns and conventions |
| `ai/skills/<name>/SKILL.md` | All AI assistants | Task-specific skill (build, test, review, etc.) |
| `CLAUDE.md` | Claude Code | Pointer to AGENTS.md |
| `.cursorrules` | Cursor | Pointer to AGENTS.md |
| `.github/copilot-instructions.md` | GitHub Copilot | Pointer to AGENTS.md |
| `.gemini/context.md` | Gemini Code Assist | Pointer to AGENTS.md |
| `openspec.json` | OpenCode | Config: loads AGENTS.md + discovers skills in `ai/skills/` |

## Maintenance

**Only `AGENTS.md` needs ongoing updates.** All other files either:
- Point to `AGENTS.md` (native init files), or
- Are migrated/consolidated content that rarely changes (skills, instructions)

When adding a new skill, create `ai/skills/<name>/SKILL.md` and add it to
the table in `AGENTS.md`. No changes needed in any native init file.
