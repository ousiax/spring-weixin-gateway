#!/bin/sh

set -e

PROJECT_ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
TAG=$(echo ${PROJECT_ARTIFACT_ID}:${PROJECT_VERSION} | awk '{print tolower($0)}')
./mvnw package -Dmaven.test.skip=true \
    && docker build . -f Dockerfile -t ${TAG}
