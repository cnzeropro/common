#!/usr/bin/env sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname "$0")/../.." && pwd)
GRADLEW="$ROOT_DIR/gradlew"
MVNW="$ROOT_DIR/mvnw"
COMMON_DATA_TEST='org.zero.common.data.enumeration.HttpStatusTest'
CORE_BASE_TEST='org.zero.common.core.util.java.EnumUtilTest'

log() {
  printf '[ci-verify] %s\n' "$1" >&2
}

print_env_var() {
  var_name="$1"
  eval "var_value=\${$var_name-}"
  if [ -n "${var_value:-}" ]; then
    printf '%s=%s\n' "$var_name" "$var_value" >&2
  else
    printf '%s=%s\n' "$var_name" '<unset>' >&2
  fi
}

run_step() {
  description="$1"
  shift
  log "$description"
  "$@"
}

cd "$ROOT_DIR"
log "repository root: $ROOT_DIR"
print_env_var JAVA_HOME
print_env_var JDK11_HOME
print_env_var JDK17_HOME
print_env_var JDK21_HOME

run_step 'java -version' java -version
run_step 'javac -version' javac -version
run_step 'gradle wrapper version' "$GRADLEW" --version
run_step 'maven wrapper version' "$MVNW" -version

run_step 'verify metadata projections' "$ROOT_DIR/scripts/metadata/verify.sh" --verbose

run_step \
  'run stable Gradle test slice' \
  "$GRADLEW" \
  --no-daemon \
  -PexcludeProjects=common-test \
  :common-data:test \
  --tests "$COMMON_DATA_TEST" \
  :core-base:test \
  --tests "$CORE_BASE_TEST"

run_step \
  'run stable Maven test slice for common-data' \
  "$MVNW" \
  -B \
  -ntp \
  -pl common-data \
  -am \
  -Dtest=HttpStatusTest \
  test

run_step \
  'run stable Maven test slice for core-base' \
  "$MVNW" \
  -B \
  -ntp \
  -pl common-core/core-base \
  -am \
  -Dtest=EnumUtilTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test

log 'verification completed'
