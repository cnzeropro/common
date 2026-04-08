#!/usr/bin/env python3
"""Dispatch Gradle metadata sync."""

import subprocess
import sys
from pathlib import Path


USAGE = "Usage: sync-gradle.py [--verbose|-v]"
verbose = False
if len(sys.argv) > 2 or (len(sys.argv) == 2 and sys.argv[1] not in {"--verbose", "-v"}):
    print(USAGE, file=sys.stderr)
    raise SystemExit(1)
if len(sys.argv) == 2:
    verbose = True
if verbose:
    print("[manage-metadata] wrapper dispatch sync-gradle", file=sys.stderr)
args = [sys.executable, str(Path(__file__).resolve().with_name("manage-metadata.py")), "sync-gradle"]
if verbose:
    args.append("--verbose")
raise SystemExit(subprocess.run(args, check=False).returncode)
