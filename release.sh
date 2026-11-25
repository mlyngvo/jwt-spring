#!/bin/bash
set -e

LOG_PREFIX="[SH >]"

echo "${LOG_PREFIX} Checking untracked changes..."
if ! git diff-index --quiet HEAD --
then
    echo "Please commit your changes before running this script!"
    exit 1
fi

echo

echo "${LOG_PREFIX} Fetching tags..."
git fetch --tags

echo

echo "${LOG_PREFIX} Getting latest version tag..."
TAG=$(git tag --sort=-v:refname | head -n 1)
echo "${TAG}"

echo

echo "${LOG_PREFIX} Bump up version..."
RELEASE=$(echo "${TAG}" | awk -F. '/[0-9]+\./{$NF++;print}' OFS=.)
echo "${RELEASE}"

echo

read -p "${LOG_PREFIX} Are you sure you want to tag and release (${RELEASE}) to Maven Central? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    echo "Release canceled."
    exit 1
fi

echo

echo "${LOG_PREFIX} Creating and pushing git tag ${RELEASE}..."
git tag "${RELEASE}"
git push origin "${RELEASE}"
