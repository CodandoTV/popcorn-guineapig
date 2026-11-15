---
name: consumer-app-test
description: Use when validating the current plugin checkout against a real consumer Gradle project end-to-end — publishes the local build to mavenLocal, injects a temporary violation, checks the generated error report and how GitHub renders it, then reverts every temporary edit. Triggers on phrases like "test with consumer app", "test this PR against a project", "consumer smoke test", "validate the report in a real project".
---

# Consumer App Test

Validates the plugin **as checked out here** against a consumer project, without
publishing a release. Use it to prove a consumer-facing change (error report,
task behaviour, DSL/API compatibility) works in a real multi-module build.

The consumer project is an **input**, never a constant: always ask for it unless
the user already named it in the request.

## Inputs to collect first

1. **Consumer project path** — the directory containing `gradlew` (some repos keep
   it under `src/`, so the git root may be one level above). Relative or absolute
   both work.
2. **Baseline version** — default: whatever version the consumer pins today. This
   is the "before" run that documents the current behaviour to compare against.
3. **Post results to a PR?** — only when the user asked. Posting is public; do not
   do it unprompted.

Write everything to a scratch directory: `ART=$(mktemp -d)`. Print `$ART` in the
final report. Never commit, stash or push in the consumer repo.

## Step 0 — Validate the consumer

Run these checks and stop with a clear message if any fails:

```bash
test -x "$CONSUMER/gradlew" || echo "no wrapper"
grep -rn "io.github.codandotv" "$CONSUMER" --include="*.toml"       # catalog coordinate + version key
grep -rln "popcorngpparent" "$CONSUMER" --include="*.kts"           # convention plugin that applies it
git -C "$CONSUMER" status --porcelain                               # record pre-existing changes
git -C "$CONSUMER" rev-parse HEAD
grep distributionUrl "$CONSUMER/gradle/wrapper/gradle-wrapper.properties"
java -version
```

- If the consumer resolves the plugin from source (`pluginManagement { includeBuild(...) }`),
  this flow does not apply — the artifact would not be used. Report that.
- Locate **the build that declares the popcorn dependency** (usually a `build-logic`
  included build): that is where `mavenLocal()` must be added. Locate the catalog
  key holding the popcorn version: that is what gets repinned.
- The plugin needs Gradle >= 8.11. Pass `--no-configuration-cache` on every
  `popcornParent` call (see the plugin README).

## Step 1 — Publish the checkout under test

```bash
PLUGIN=<this repo>
VERSION=$(grep '^VERSION=' "$PLUGIN/popcornguineapigplugin/version.properties" | cut -d= -f2)
SHA=$(git -C "$PLUGIN" rev-parse --short=8 HEAD)
LOCAL_VERSION="$VERSION-local-$SHA"
git -C "$PLUGIN" status --porcelain    # record: uncommitted source changes are included
```

Temporarily set `VERSION=$LOCAL_VERSION` in `popcornguineapigplugin/version.properties`,
then:

```bash
cd "$PLUGIN" && ./gradlew popcornguineapigplugin:publishToMavenLocal
git -C "$PLUGIN" checkout -- popcornguineapigplugin/version.properties
```

If the publish fails with `Cannot perform signing task ... no configured signatory`
(the Vanniktech `signAllPublications()` setup), temporarily comment that line out
in `popcornguineapigplugin/build.gradle.kts`, publish, then restore it with
`git checkout --`. Local artifacts do not need signatures and `-x` does not
exclude tasks of an included build.

The sha suffix keeps the artifact distinct from published versions and from any
older local artifact with the plain version. Verify the publish really contains
the code under test:

```bash
ls -l ~/.m2/repository/io/github/codandotv/popcornguineapig/$LOCAL_VERSION/
unzip -p ~/.m2/repository/io/github/codandotv/popcornguineapig/$LOCAL_VERSION/*-sources.jar \
  '*<changed file path>.kt' | grep <symbol introduced by the change>
```

If the change has no greppable symbol, compare the sources-jar mtime to the publish
start and record `git rev-parse HEAD`.

## Step 2 — Inject a temporary violation in the consumer

The report is only produced for failing rules, and escaped characters only appear
through user-supplied rule patterns. Add **temporary** `PopcornChildConfiguration`
entries to the consumer's popcorn convention plugin, appended **last** in
`children`:

- 2.0.x–3.2.x keep only the last matching configuration, so appending last makes
  the injected rule win; 3.3.0+ unions all matching rules.
- Prefer modules **not already matched** by an existing config, so the injection
  is the only source of violations and cannot mask/merge with existing rules.
- Include one plain alternation pattern (the bug you are reproducing) and one
  pattern containing inline code/backticks if the change touches escaping.

Example shape (`PopcornChildConfiguration` + `DoNotWithRule` are already imported
in a typical convention plugin):

```kotlin
PopcornChildConfiguration(
    moduleNameRegex = ":someModule",
    rules = listOf(DoNotWithRule(notWith = listOf("depA|depB"))),
),
PopcornChildConfiguration(
    moduleNameRegex = ":other-(one|two)",
    rules = listOf(DoNotWithRule(notWith = listOf("depA|`depB`"))),
),
```

The rule message interpolates the pattern list, so the markdown cell will contain
pipes, brackets and (optionally) backticks. Do **not** edit module build files:
adding dependencies is not needed and changes the build under test.

## Step 3 — Baseline run (consumer's pinned version)

```bash
cd "$CONSUMER"
set +e
./gradlew popcornParent -PerrorReportEnabled --no-configuration-cache
echo "exit=$?"
cp build/reports/popcornguineapig/errorReport.md "$ART/baseline.md"
```

Non-zero exit is expected: violations fail the build. The report is written
**before** the failure, so it still exists.

Run the checker and keep its output as evidence. On the baseline report it is
expected to fail (8 cells instead of 6, unescaped pipes, duplicated sections):

```bash
python3 .opencode/skills/consumer-app-test/scripts/check_error_report.py --report "$ART/baseline.md"
```

## Step 4 — Run with the checkout under test

Repin the catalog key to `$LOCAL_VERSION` and add `mavenLocal()` to the build that
resolves the popcorn artifact. When the plugin is applied through an included
convention build, also add `mavenLocal()` to the consumer's root
`buildscript { repositories }`: the artifact is resolved a second time on the
applying project's buildscript classpath. Re-run the same command, save
`$ART/fixed.md`, then:

```bash
python3 .opencode/skills/consumer-app-test/scripts/check_error_report.py --report "$ART/fixed.md"   # must exit 0
grep -c '^## Analysis:' "$ART/fixed.md"
grep -n '<injected pattern with escaped pipes>' "$ART/fixed.md"
```

Render it with GitHub's own GFM renderer (this is what a PR comment would show)
and assert the **cell contents**, not only the column count:

```bash
python3 -c 'import json,sys;print(json.dumps({"text":open(sys.argv[1]).read(),"mode":"gfm"}))' \
  "$ART/fixed.md" | gh api -X POST /markdown --input - > "$ART/fixed.html"
# Description cell: the complete pattern, closing brackets included
# Status cell: exactly "Failed ❌"
```

GitHub drops cells beyond the header width, so a broken row can still render 6
`<td>` cells: the description gets cut at the first unescaped pipe and the Status
column shows the pattern remainder instead of the result. Count cells *and* read
the Description/Status values of every row.

Checks that must hold for a fixed report:

- one `## Analysis:` section per violating module, labeled with the project path on
  3.3.0+ (older versions label with the module name);
- one `- **Violations Found**: N` line and one `## Violation Details` table per
  section, rows numbered `1..N` consecutively;
- every table row splits into exactly 6 cells; pipes appear as `\|`;
- backticks are **not** escaped (inline code in a description must render).

## Step 5 — Pass-case regression

Remove **only** the injected configuration (keep the local build pinned) and re-run
`popcornParent -PerrorReportEnabled --no-configuration-cache`:

- exit `0` and `No violation issues found!`;
- `build/reports/popcornguineapig/errorReport.md` exists and is empty (0 bytes) —
  the GitHub Action deletes its comment on success and only reads the report on
  failure.

Optional smoke: `./gradlew popcornModuleMetrics --no-configuration-cache` must still
write `build/reports/metrics/metrics.csv`.

## Step 6 — Cleanup (always, even on failure)

```bash
git -C "$CONSUMER" checkout -- <every consumer file you edited>
git -C "$PLUGIN" checkout -- popcornguineapigplugin/version.properties
ls -d ~/.m2/repository/io/github/codandotv/popcornguineapig/"$LOCAL_VERSION" \
      ~/.m2/repository/io/github/codandotv.popcorngpparent.gradle.plugin/"$LOCAL_VERSION"  # print + verify before rm
rm -rf ~/.m2/repository/io/github/codandotv/popcornguineapig/"$LOCAL_VERSION" \
       ~/.m2/repository/io/github/codandotv.popcorngpparent.gradle.plugin/"$LOCAL_VERSION"
git -C "$CONSUMER" status --porcelain   # must equal the Step 0 snapshot
git -C "$PLUGIN" status --porcelain     # only the skill files (or prior changes) remain
```

Never delete older local artifacts that you did not publish.

## Step 7 — Report

Summarise:

- environment (OS, JDK, Gradle, consumer SHA, plugin SHA and local version);
- the injected scenario (rule pattern, modules, expected violations);
- baseline vs build-under-test: cell counts, escaping, duplicate sections, render
  result, exit codes, pass-case result;
- limitations (state what could not be triggered end-to-end and which unit test
  covers it; the consumer-side CI Action itself is not executed);
- the artifacts directory.

If — and only if — the user asked, post the summary to the PR:

```bash
gh pr comment <number> --repo <owner>/<repo> --body-file "$ART/pr-comment.md"
```

## Rollback when a step fails

Restore the consumer and plugin files (`git checkout --`), delete the temp
mavenLocal coordinates, do **not** post a PR comment, and report the failing
command with its output. A failure in the baseline run may mean the consumer's
pinned version does not support the feature being tested — say so explicitly
instead of treating it as a defect.

## Notes

- Commands that touch the consumer directory, `~/.gradle` or `~/.m2` are outside
  this repo's workspace. In sandboxed agents they need wider file-access approval;
  request it rather than re-homing `GRADLE_USER_HOME` (the warm Gradle cache is
  usually many GB).
- Example consumer shape (illustration only — never default to it): a KMP app with
  ten modules, Gradle wrapper under `src/`, popcorn pinned in
  `src/gradle/libs.versions.toml`, applied by `src/build-logic`'s
  `popcorngp-setup-plugin.gradle.kts`.
