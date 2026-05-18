#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
BUILD_FILE="$PROJECT_DIR/popcornguineapigplugin/build.gradle.kts"
VERSION_FILE="$PROJECT_DIR/popcornguineapigplugin/version.properties"

# Allow optional version override
ORIGINAL_VERSION=""
if [ $# -ge 1 ]; then
  ORIGINAL_VERSION=$(grep "^VERSION=" "$VERSION_FILE" | cut -d'=' -f2)
  NEW_VERSION="$1"
  echo "Updating version to $NEW_VERSION..."
  sed -i '' "s/^VERSION=.*/VERSION=$NEW_VERSION/" "$VERSION_FILE"
fi

CURRENT_VERSION=$(grep "^VERSION=" "$VERSION_FILE" | cut -d'=' -f2)
echo "Publishing popcornguineapig v$CURRENT_VERSION locally..."

# Comment out publishToMavenCentral and signAllPublications
sed -i '' -E 's/^([[:space:]]*)publishToMavenCentral/\1\/\/publishToMavenCentral/' "$BUILD_FILE"
sed -i '' -E 's/^([[:space:]]*)signAllPublications/\1\/\/signAllPublications/' "$BUILD_FILE"

cleanup() {
  echo "Restoring build.gradle.kts..."
  sed -i '' -E 's|^([[:space:]]*)//publishToMavenCentral|\1publishToMavenCentral|' "$BUILD_FILE"
  sed -i '' -E 's|^([[:space:]]*)//signAllPublications|\1signAllPublications|' "$BUILD_FILE"
  if [ -n "$ORIGINAL_VERSION" ]; then
    echo "Restoring version.properties..."
    sed -i '' "s/^VERSION=.*/VERSION=$ORIGINAL_VERSION/" "$VERSION_FILE"
  fi
}
trap cleanup EXIT

"$PROJECT_DIR/gradlew" -p "$PROJECT_DIR" :popcornguineapigplugin:publishToMavenLocal

echo ""
echo "Publish complete!"
echo "Artifact: io.github.codandotv:popcornguineapig:$CURRENT_VERSION"
echo "Local path: $HOME/.m2/repository/io/github/codandotv/popcornguineapig/$CURRENT_VERSION/"
echo ""
echo "To use it in your project, add mavenLocal() in:"
echo "  - settings.gradle.kts (pluginManagement > repositories)"
echo "  - build-logic/build.gradle.kts (repositories)"
echo ""
echo "Then add the dependency:"
echo "  implementation(\"io.github.codandotv:popcornguineapig:$CURRENT_VERSION\")"
