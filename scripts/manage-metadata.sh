#!/usr/bin/env sh
set -eu

PREFIX='[manage-metadata]'
USAGE='Usage: manage-metadata.sh <sync|verify> [--verbose|-v]'
command_name="${1:-}"
verbose=false

usage() {
  echo "$USAGE" >&2
}

log() {
  if [ "$verbose" = "true" ]; then
    echo "$PREFIX $1" >&2
  fi
}

if [ "$command_name" != "sync" ] && [ "$command_name" != "verify" ]; then
  usage
  exit 1
fi

if [ "$#" -gt 2 ]; then
  usage
  exit 1
fi

if [ "$#" -eq 2 ]; then
  case "$2" in
    --verbose|-v)
      verbose=true
      ;;
    *)
      usage
      exit 1
      ;;
  esac
fi

log 'arguments parsed'

script_dir=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
root_dir=$(CDPATH= cd -- "$script_dir/.." && pwd)
source_dir="$script_dir/build-metadata/src/main/java"
build_root_dir="$root_dir/build/build-metadata-cli"
build_dir="$build_root_dir/$$"
classes_dir="$build_dir/classes"
source_list_file="$build_dir/sources.txt"
log "paths ready: root=$root_dir"

to_java_path() {
  if command -v cygpath >/dev/null 2>&1; then
    cygpath -w "$1"
    return
  fi
  printf '%s\n' "$1"
}

mkdir -p "$classes_dir"
cleanup() {
  log 'cleanup start'
  rm -rf "$build_dir"
  log 'cleanup done'
}
trap cleanup EXIT

javac_path=$(command -v javac || true)
if [ -z "$javac_path" ]; then
  echo "Missing javac command. Please configure a JDK and ensure javac is on PATH." >&2
  exit 1
fi

java_path=$(command -v java || true)
if [ -z "$java_path" ]; then
  echo "Missing java command. Please configure a JDK and ensure java is on PATH." >&2
  exit 1
fi

log "toolchain ready: javac=$javac_path, java=$java_path"

# 使用相对 ASCII 路径写入参数文件，避免绝对路径里的非 ASCII 字符影响 javac @argfile。
find "$source_dir" -name '*.java' | LC_ALL=C sort | while IFS= read -r java_source_path; do
  relative_source_path=${java_source_path#"$root_dir"/}
  printf '%s\n' "$relative_source_path"
done > "$source_list_file"
source_list_arg="@$(to_java_path "$source_list_file")"
source_count=$(wc -l < "$source_list_file" | tr -d ' ')
log "source list ready: ${source_count:-0} relative file(s)"

log 'javac start'
if (
  cd "$root_dir"
  "$javac_path" -encoding UTF-8 -d "$(to_java_path "$classes_dir")" "$source_list_arg"
); then
  compile_exit=0
else
  compile_exit=$?
fi
log "javac done: exit=$compile_exit"
if [ "$compile_exit" -ne 0 ]; then
  exit "$compile_exit"
fi

log 'java cli start'
if [ "$verbose" = "true" ]; then
  if "$java_path" -cp "$(to_java_path "$classes_dir")" org.zero.build.metadata.BuildMetadataCli "$command_name" "$(to_java_path "$root_dir")" --verbose; then
    java_exit=0
  else
    java_exit=$?
  fi
else
  if "$java_path" -cp "$(to_java_path "$classes_dir")" org.zero.build.metadata.BuildMetadataCli "$command_name" "$(to_java_path "$root_dir")"; then
    java_exit=0
  else
    java_exit=$?
  fi
fi
log "java cli done: exit=$java_exit"
exit "$java_exit"
