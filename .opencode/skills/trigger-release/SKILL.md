---
name: trigger-release
description: Cut and publish a PopcornGP release. Use when the user asks to trigger a release, cut a release, or publish a new version.
---

# Trigger Release

Publishes the Popcorn GuineaPig Gradle plugin to Maven Central through the
repository's manual `publish.yml` workflow.

## When to use

- The user asks to trigger, cut, or publish a release.
- A release is already prepared (`version.properties` + `CHANGELOG.md`) and
  needs to go out.
- A released version needs to be verified on Maven Central.

## Pipeline facts (read before acting)

- `.github/workflows/publish.yml` is `workflow_dispatch` only. A tag push or a
  push to `main` does not publish anything.
- The workflow publishes with `./gradlew popcornguineapigplugin:publish`, then
  runs `bundle exec fastlane update_tag` (`PREFIX: 'v'`).
- `fastlane` reads `popcornguineapigplugin/version.properties`, creates the
  `v<version>` tag on a new commit, and pushes the tag. **The workflow owns the
  tag:** never create or push `v<version>` yourself.
- Publishing happens before tagging. A failing tag step can leave the artifact
  on Maven Central with no tag.
- `fastlane`'s no-tag branch hardcodes `1.0.0`, and re-dispatching without a
  version bump can derive an unintended tag. The guards below prevent both.

## Preflight (abort on any failure)

Run from the repository root.

```bash
git branch --show-current                 # must be main
git status --porcelain                    # must be empty
git fetch origin main --tags
git rev-list --count HEAD..origin/main    # must be 0
git log --format='%s' origin/main..HEAD   # release-prep commits only
gh auth status                            # must show the workflow scope
```

- JDK 17 must be active; `./gradlew` must work.
- At least one `v*` tag must exist:
  ```bash
  git tag --list 'v*' --sort=-v:refname | head -n 1
  ```
  If none exists, stop: `fastlane update_tag` hardcodes `1.0.0` for the first
  tag, which is not what this skill should release.
- Local commits ahead of `origin/main` are allowed only when every subject is
  a release-prep commit (`chore: bump to ...; update CHANGELOG` or
  `Update version to ...`). Anything else means unreleased work is on `main`;
  abort and ask the user to merge it first.
- If `gh` is missing or `gh auth status` does not list the `workflow` scope,
  stop before dispatching and give the user this fallback:
  `https://github.com/CodandoTV/popcorn-guineapig/actions/workflows/publish.yml`
  → **Run workflow** → branch `main`.

## Detect the release state

```bash
VERSION=$(sed -n 's/^VERSION=//p' popcornguineapigplugin/version.properties | tr -d '[:space:]')
LATEST_TAG=$(git tag --list 'v*' --sort=-v:refname | head -n 1)
git rev-parse -q --verify "refs/tags/v$VERSION" >/dev/null
grep -Fq "## [$VERSION]" CHANGELOG.md
```

- `VERSION` must match `X.Y.Z`; otherwise abort.
- The top `## [...]` header in `CHANGELOG.md` must equal `VERSION`; otherwise
  abort and ask the user to reconcile the two files.

| State | Detection | Action |
|---|---|---|
| Released | `v$VERSION` tag exists | Require commits after `LATEST_TAG`, then prepare the next version |
| Prepared | no `v$VERSION` tag, CHANGELOG has `## [$VERSION]` | Publish as-is |
| Inconsistent | no tag and no CHANGELOG entry | Abort; run `release-notes` and commit |
| Downgrade | `VERSION` is lower than `LATEST_TAG` | Abort; the version file is behind the tags |
| No tags | `git tag --list 'v*'` is empty | Abort; align the fastlane no-tag path first |

For the **Released** state, check that there is something to release:

```bash
git log --oneline "$LATEST_TAG..HEAD"
```

If it is empty, abort with "nothing to release". The tag lives on a
workflow-created commit outside `main`; do not pull or merge that commit.

## Prepare (Released state only)

Read `.opencode/skills/release-notes/SKILL.md` and follow sections 1–9. Skip
its section 10: `trigger-release` owns the commit and push.

- If the user requested a specific version, use it instead of the computed
  bump, provided it is valid and greater than `LATEST_TAG`.
- The new version must be greater than `LATEST_TAG` and `v<new-version>` must
  not exist; otherwise abort.
- After preparation, `git status --porcelain` must list only `CHANGELOG.md` and
  `popcornguineapigplugin/version.properties`. Any other path means the
  preparation touched unrelated files; abort.

For the **Prepared** state, do not bump anything. Use the existing
`version.properties` and CHANGELOG entry.

## Run the CI gate

Before committing or pushing, run the same gate as `.github/workflows/pr.yml`:

```bash
./gradlew popcornguineapigplugin:koverHtmlReport popcornguineapigplugin:verifyNoRuntimeDependencyLeak
```

Abort on failure. The only expected changes are version/CHANGELOG, but the
release must not go out from a checkout that fails the build.

## Review and confirm

Show the user:

- the version to publish and the previous tag;
- the unreleased commits (Released state) or the existing CHANGELOG entry
  (Prepared state);
- `git diff --stat` and the generated release notes;
- what will happen: commit and/or push only when release-prep changes are not on
  `origin/main` yet, then dispatch `publish.yml`, which publishes to Maven Central
  and creates the tag.

Ask one question: approve, edit the version or notes, or cancel. Require an
explicit affirmative answer before any commit, push, or dispatch. If the user
cancels, leave the checkout untouched.

## Publish

Only when this run prepared a new version/changelog entry:

```bash
git add CHANGELOG.md popcornguineapigplugin/version.properties
git commit -m "chore: bump to <version>; update CHANGELOG"
```

Push whenever there are local release-prep commits:

```bash
if [ "$(git rev-list --count origin/main..HEAD)" -gt 0 ]; then
  git push origin main
fi
```

For a fully prepared state with nothing ahead, skip both the commit and the
push: the release commit is already on `origin/main`.

If `git push origin main` is rejected by branch protection, stop. Do not
force-push. Route the release commit through a pull request, then rerun this
skill after the merge; it will detect **Prepared** and dispatch.

Dispatch and watch:

```bash
BEFORE=$(gh run list --workflow=publish.yml --branch main --limit 1 \
  --json databaseId --jq '.[0].databaseId')
gh workflow run publish.yml --ref main

# The new run may not appear immediately; wait for an id different from BEFORE.
RUN_ID=""
for _ in $(seq 1 20); do
  RUN_ID=$(gh run list --workflow=publish.yml --branch main --limit 1 \
    --json databaseId --jq '.[0].databaseId')
  [ -n "$RUN_ID" ] && [ "$RUN_ID" != "$BEFORE" ] && break
  RUN_ID=""
  sleep 3
done

if [ -z "$RUN_ID" ]; then
  echo "No new publish run found; check https://github.com/CodandoTV/popcorn-guineapig/actions/workflows/publish.yml"
  exit 1
fi

gh run watch "$RUN_ID" --exit-status
```

On failure:

```bash
gh run view "$RUN_ID" --log-failed
```

Report the run URL and stop. Do not re-dispatch blindly: the publish may have
succeeded before a later step failed, and the tag may already exist.

## Verify and report

```bash
git fetch --tags
git show -s --format='%h %s' "v$VERSION"
```

Confirm the tag exists and the workflow concluded successfully. Report:

- version, release commit, tag and workflow run URL;
- artifact link:
  `https://central.sonatype.com/artifact/io.github.codandotv/popcornguineapig/<version>`.

Optionally confirm the artifact on Maven Central:

```bash
curl -sS -o /dev/null -w '%{http_code}\n' \
  "https://repo1.maven.org/maven2/io/github/codandotv/popcornguineapig/$VERSION/popcornguineapig-$VERSION.pom"
```

Central can take several minutes to sync. Treat an immediate `404` as normal
propagation lag, not a release failure.

## Guardrails

- Never create or push `v<version>`; `publish.yml`'s `fastlane update_tag` owns it.
- Never dispatch when `v<version>` exists or when the version is not strictly
  greater than the latest tag.
- Never force-push, and never publish from a branch or fork.
- Never re-dispatch after a partial failure without reading the failed log.
- Do not pull or merge the workflow-created tag commit into `main`.
