#!/bin/bash
set -euo pipefail
IMAGE_TAG=${BUILD_VERSION:-'latest'}
# Build tar file with Gradle Wrapper
./gradlew build
# Build Docker Images
docker build -f ./docker/app.dockerfile -t wallet-web-api:${IMAGE_TAG} ./app/build/distributions
# Run App
docker run -p 8080:8080 wallet-web-api