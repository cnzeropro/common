#!/usr/bin/env python3
"""Dispatch Maven metadata verify."""

import subprocess
import sys
from pathlib import Path


USAGE = "Usage: verify-maven.py [--verbose|-v]"
verbose = False
if len(sys.argv) > 2 or (len(sys.argv) == 2 and sys.argv[1] not in {"--verbose", "-v"}):
    print(USAGE, file=sys.stderr)
    raise SystemExit(1)
if len(sys.argv) == 2:
    verbose = True
if verbose:
    print("[manage-metadata] wrapper dispatch verify-maven", file=sys.stderr)
args = [sys.executable, str(Path(__file__).resolve().with_name("manage-metadata.py")), "verify-maven"]
if verbose:
    args.append("--verbose")
raise SystemExit(subprocess.run(args, check=False).returncode)
