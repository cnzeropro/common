#!/usr/bin/env sh
set -eu

: "command guard - only accept sync or verify"
command_name="${1:-}"
if [ "$command_name" != "sync" ] && [ "$command_name" != "verify" ]; then
  echo "Usage: build-metadata.sh <sync|verify>" >&2
  exit 1
fi

: "path bootstrap - derive the repository root and temp directories"
script_dir=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
root_dir=$(CDPATH= cd -- "$script_dir/.." && pwd)
source_dir="$script_dir/build-metadata/src/main/java"
build_root_dir="$root_dir/build/build-metadata-cli"
build_dir="$build_root_dir/$$"
classes_dir="$build_dir/classes"
source_list_file="$build_dir/sources.txt"

mkdir -p "$classes_dir"
cleanup() {
  : "temp cleanup - remove per-run compiled classes"
  rm -rf "$build_dir"
}
trap cleanup EXIT

: "JDK command guard - require local javac and java"
if ! command -v javac >/dev/null 2>&1; then
  echo "Missing javac command. Please configure a JDK and ensure javac is on PATH." >&2
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "Missing java command. Please configure a JDK and ensure java is on PATH." >&2
  exit 1
fi

: "source list file - feed javac via an argument file"
find "$source_dir" -name '*.java' | LC_ALL=C sort > "$source_list_file"
source_list_arg="@${source_list_file}"

: "compile and run - execute the metadata CLI"
javac -encoding UTF-8 -d "$classes_dir" "$source_list_arg"
java -cp "$classes_dir" org.zero.build.metadata.BuildMetadataCli "$command_name" "$root_dir"
