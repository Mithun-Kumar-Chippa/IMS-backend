#!/bin/sh
# Simple Maven wrapper fallback for unix environments
if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
else
  echo "Maven not found in PATH" >&2
  exit 1
fi
