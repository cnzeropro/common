#!/usr/bin/env python3
"""Forward the verify command to the shared metadata entrypoint."""

import subprocess
import sys
from pathlib import Path


PREFIX = "[manage-metadata]"
USAGE = "Usage: verify-metadata.py [--verbose|-v]"


def parse_args(argv):
    if len(argv) > 1:
        return None
    if not argv:
        return False
    if argv[0] in {"--verbose", "-v"}:
        return True
    return None


def main(argv):
    verbose = parse_args(argv)
    if verbose is None:
        print(USAGE, file=sys.stderr)
        return 1

    script = Path(__file__).resolve().with_name("manage-metadata.py")
    command = [sys.executable, str(script), "verify"]
    if verbose:
        print(f"{PREFIX} wrapper dispatch verify", file=sys.stderr)
        command.append("--verbose")

    result = subprocess.run(command, check=False)
    return result.returncode


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
