#!/usr/bin/env python3
"""Compile and run the build metadata Java CLI."""

import os
import shutil
import subprocess
import sys
from pathlib import Path


PREFIX = "[manage-metadata]"
USAGE = "Usage: manage-metadata.py <sync|verify> [--verbose|-v]"


def log(verbose, message):
    if verbose:
        print(f"{PREFIX} {message}", file=sys.stderr)


def parse_args(argv):
    if len(argv) not in {1, 2}:
        return None

    command = argv[0]
    if command not in {"sync", "verify"}:
        return None

    verbose = False
    if len(argv) == 2:
        if argv[1] not in {"--verbose", "-v"}:
            return None
        verbose = True

    return command, verbose


def main(argv):
    parsed = parse_args(argv)
    if parsed is None:
        print(USAGE, file=sys.stderr)
        return 1

    command, verbose = parsed
    log(verbose, "arguments parsed")

    script_dir = Path(__file__).resolve().parent
    root_dir = script_dir.parent
    source_dir = script_dir / "build-metadata" / "src" / "main" / "java"
    build_root_dir = root_dir / "build" / "build-metadata-cli"
    build_dir = build_root_dir / str(os.getpid())
    classes_dir = build_dir / "classes"
    source_list_file = build_dir / "sources.txt"
    log(verbose, f"paths ready: root={root_dir}")

    classes_dir.mkdir(parents=True, exist_ok=True)

    try:
        javac = shutil.which("javac")
        if not javac:
            print("Missing javac command. Please configure a JDK and ensure javac is on PATH.", file=sys.stderr)
            return 1

        java = shutil.which("java")
        if not java:
            print("Missing java command. Please configure a JDK and ensure java is on PATH.", file=sys.stderr)
            return 1

        log(verbose, f"toolchain ready: javac={javac}, java={java}")

        sources = sorted(str(path_item) for path_item in source_dir.rglob("*.java"))
        source_list_file.write_text("\n".join(sources), encoding="ascii")
        log(verbose, f"source list ready: {len(sources)} file(s)")

        log(verbose, "javac start")
        compile_result = subprocess.run(
            [javac, "-encoding", "UTF-8", "-d", str(classes_dir), f"@{source_list_file}"],
            check=False,
        )
        log(verbose, f"javac done: exit={compile_result.returncode}")
        if compile_result.returncode != 0:
            return compile_result.returncode

        java_args = [
            java,
            "-cp",
            str(classes_dir),
            "org.zero.build.metadata.BuildMetadataCli",
            command,
            str(root_dir),
        ]
        if verbose:
            java_args.append("--verbose")

        log(verbose, "java cli start")
        run_result = subprocess.run(java_args, check=False)
        log(verbose, f"java cli done: exit={run_result.returncode}")
        return run_result.returncode
    finally:
        log(verbose, "cleanup start")
        shutil.rmtree(build_dir, ignore_errors=True)
        log(verbose, "cleanup done")


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
