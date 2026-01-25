#!/bin/bash
set -e

VERSION=$1

if [ -z "$VERSION" ]; then
    echo "Usage: ./release.sh v0.1.0"
    exit 1
fi

echo "Creating release $VERSION"

CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
  echo "You must be on the 'main' branch to release (current: $CURRENT_BRANCH)"
  exit 1
fi

if ! git diff --quiet || ! git diff --cached --quiet; then
  echo "Working tree is dirty. Commit or stash changes first."
  exit 1
fi

if git tag | grep -q "^$VERSION$"; then
  echo "Tag $VERSION already exists."
  exit 1
fi

if ! grep -q "$VERSION" CHANGELOG.md; then
  echo "CHANGELOG.md does not contain $VERSION"
  exit 1
fi

echo "Pre-flight checks passed"

# Commit release (if needed)
git commit --allow-empty -m "chore: prepare release $VERSION"

# Create annotated tag
git tag -a "$VERSION" -m "Release $VERSION"

# Push
git push origin main
git push origin "$VERSION"

echo "Release $VERSION created!"
echo "Create GitHub release: https://github.com/akashgill3/datastar-spring-boot-starter/releases/new?tag=$VERSION"