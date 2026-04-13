#!/usr/bin/env sh
set -eu

USAGE='Usage: sync.sh [gradle|maven] [--verbose|-v]'
verbose_flag=''
target=''
command_name='sync'

usage() {
  echo "$USAGE" >&2
}

for arg in "$@"; do
  case "$arg" in
    gradle|maven)
      if [ -n "$target" ]; then
        usage
        exit 1
      fi
      target="$arg"
      ;;
    --verbose|-v)
      if [ -n "$verbose_flag" ]; then
        usage
        exit 1
      fi
      verbose_flag='--verbose'
      ;;
    *)
      usage
      exit 1
      ;;
  esac
done

if [ "$target" = 'gradle' ]; then
  command_name='sync-gradle'
elif [ "$target" = 'maven' ]; then
  command_name='sync-maven'
fi

script_path="$(CDPATH= cd -- "$(dirname "$0")" && pwd)/internal/manage.sh"
if [ -n "$verbose_flag" ]; then
  echo "[manage-metadata] wrapper dispatch $command_name" >&2
  exec "$script_path" "$command_name" "$verbose_flag"
fi
exec "$script_path" "$command_name"
