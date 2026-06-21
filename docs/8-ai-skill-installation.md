# AI Skill Installation 🤖

!!!note "AI skill installation available >= 3.3.0"
    Starting from version `3.3.0`, PopcornGP ships with an embedded AI skill file that AI coding tools can use to automatically help configure the plugin in your project.

## What it is

The `installPopcornSkill` Gradle task copies a bundled `SKILL.md` file from the plugin JAR into your project. This file contains step-by-step instructions for AI coding tools (such as OpenCode, Claude Code, Cursor, or GitHub Copilot) on how to:

- Detect your project infrastructure (`build-logic`, `buildSrc`, or direct root configuration)
- Apply the PopcornGpParentPlugin with the correct setup path
- Configure architectural rules based on your module structure
- Verify the setup with `./gradlew popcornParent`

## Running the task

```bash
./gradlew installPopcornSkill
```

This creates `.opencode/skills/setup-popcorn-plugin/SKILL.md` in your project root by default.

## Custom destination

You can configure where the skill file is placed using two methods:

### Via DSL extension

In your root `build.gradle.kts`:

```kotlin
popcornGuineapigParentConfig {
    type = ProjectType.KMP
    skillOutputDir = file(".cursor/skills")

    children = listOf(...)
}
```

### Via Gradle property

```bash
./gradlew installPopcornSkill -PpopcornSkillOutputDir=.agents/skills
```

If neither is set, the default is `.opencode/skills`.

### Priority

The DSL extension takes precedence. If both are set, the DSL value wins:
1. `popcornGuineapigParentConfig { skillOutputDir = ... }`
2. `-PpopcornSkillOutputDir=...`
3. `.opencode/skills` (default)

## Supported AI coding tools

The skill file follows the standard `SKILL.md` format recognized by several AI coding tools. You can direct it to the appropriate directory:

| AI Tool | Recommended `skillOutputDir` |
|---------|------------------------------|
| [OpenCode](https://opencode.ai) | `.opencode/skills` (default) |
| [Claude Code](https://claude.ai) | `.claude/skills` |
| [Cursor](https://cursor.com) | `.cursor/skills` |
| [GitHub Copilot](https://github.com/features/copilot) | `.github/copilot-instructions` |
| [Gemini Code Assist](https://cloud.google.com/gemini) | `.gemini/skills` |

> **Note:** GitHub Copilot uses a different instruction format. The bundled `SKILL.md` works best with tools that follow the OpenCode/Claude Code skill discovery pattern. For Copilot, you may want to reference the skill content manually.

## What the skill helps with

Once installed and discovered by your AI tool, you can prompt it with phrases like:

- "setup popcorn plugin for this project"
- "configure architectural rules for my modules"
- "add popcorn dependency checks to CI"

The AI will read the skill file and guide you through:

1. Choosing the right setup path (build-logic, buildSrc, or direct)
2. Adding the plugin dependency
3. Configuring `popcornGuineapigParentConfig` with your module structure
4. Mapping architectural layers to `NoDependencyRule`, `JustWithRule`, and `DoNotWithRule`
5. Running and verifying the setup

## Idempotency

The `installPopcornSkill` task is idempotent:

- If the file already exists with the same content, it is **not overwritten** (the file remains untouched)
- If the file exists but differs from the bundled version, it is **updated** so you always get the latest skill instructions

This means it is safe to run the task multiple times or include it as part of your build lifecycle.

## CI/CD integration

You can include the task in your CI pipeline to ensure all developers have the skill file available:

```yaml
- name: Install Popcorn AI skill
  run: ./gradlew installPopcornSkill
```

Combine with the existing architecture check for a complete setup:

```yaml
- name: Install AI skill
  run: ./gradlew installPopcornSkill
- name: Run architecture validation
  run: ./gradlew popcornParent -PerrorReportEnabled
```
