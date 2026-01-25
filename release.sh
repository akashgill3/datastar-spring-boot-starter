#!/bin/bash
VERSION=$1

if [ -z "$VERSION" ]; then
    echo "Usage: ./release.sh v0.1.0"
    exit 1
fi

echo "Creating release $VERSION"

# Commit
git add .
git commit -m "chore: prepare release $VERSION"

# Tag
git tag -a "$VERSION" -m "Release $VERSION"

# Push
git push origin main
git push origin "$VERSION"

echo "Release $VERSION created!"
echo "Create GitHub release: https://github.com/akashgill3/datastar-spring-boot-starter/releases/new?tag=$VERSION"