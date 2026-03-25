#!/usr/bin/env sh
set -eu

USAGE='Usage: verify-metadata.sh [--verbose|-v]'
verbose_flag=''

if [ "$#" -gt 1 ]; then
  echo "$USAGE" >&2
  exit 1
fi

if [ "$#" -eq 1 ]; then
  case "$1" in
    --verbose|-v)
      verbose_flag="$1"
      ;;
    *)
      echo "$USAGE" >&2
      exit 1
      ;;
  esac
fi

script_path="$(CDPATH= cd -- "$(dirname "$0")" && pwd)/manage-metadata.sh"
if [ -n "$verbose_flag" ]; then
  exec "$script_path" verify "$verbose_flag"
fi
exec "$script_path" verify
