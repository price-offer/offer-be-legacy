#!/bin/bash

VERSION=$(git log -1 --pretty=%h)
REPO=$1
TAG="${REPO}:${VERSION}"
LATEST="${REPO}:latest"
BUILD_TIMESTAMP=$( date '+%F_%H:%M:%S' )
DOCKER_FILE=docker/Dockerfile-dev

docker build -t "$TAG" -t "$LATEST" --build-arg VERSION="$VERSION" --build-arg BUILD_TIMESTAMP="$BUILD_TIMESTAMP" -f "$DOCKER_FILE" .
docker push "$TAG"
docker push "$LATEST"