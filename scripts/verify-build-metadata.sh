#!/usr/bin/env sh
set -eu

: "verify wrapper - delegate to build-metadata.sh"
"$(CDPATH= cd -- "$(dirname "$0")" && pwd)/build-metadata.sh" verify
