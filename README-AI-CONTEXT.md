# AI Context Structure — What & Why

This repository keeps its AI coding context OpenCode-only, in a single source
of truth: `AGENTS.md` for project context and `.opencode/skills/` for
task-specific skills.

## Flow

```
opencode.json
├── instructions: ["AGENTS.md"]        # project context, rules, commands
└── skills.paths: [".opencode/skills"] # task-specific skills
```

## Files

| File | Purpose |
|------|---------|
| `AGENTS.md` | Project context — architecture, commands, hard rules, PR checklist |
| `opencode.json` | Loads `AGENTS.md` and points skill discovery at `.opencode/skills/` |
| `.opencode/skills/<name>/SKILL.md` | Task-specific skill (build, test, review, release, …) |

## Maintenance

- Update `AGENTS.md` for project-wide context.
- Add a skill by creating `.opencode/skills/<name>/SKILL.md`; discovery is
  automatic via the configured path. Add it to the skill list in `AGENTS.md`.
- Do not add per-assistant files (Claude Code, Cursor, Copilot, Gemini, …);
  this repo is configured for OpenCode only.
