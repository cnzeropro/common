#!/usr/bin/env python3
"""Forward the sync command to the shared metadata entrypoint."""

import subprocess
import sys
from pathlib import Path


PREFIX = "[manage-metadata]"
USAGE = "Usage: sync.py [gradle|maven] [--verbose|-v]"


def parse_args(argv):
    target = None
    verbose = False
    for arg in argv:
        if arg in {"gradle", "maven"}:
            if target is not None:
                return None
            target = arg
            continue
        if arg in {"--verbose", "-v"}:
            if verbose:
                return None
            verbose = True
            continue
        return None
    return target, verbose


def main(argv):
    parsed = parse_args(argv)
    if parsed is None:
        print(USAGE, file=sys.stderr)
        return 1

    target, verbose = parsed
    command_name = "sync"
    if target == "gradle":
        command_name = "sync-gradle"
    elif target == "maven":
        command_name = "sync-maven"

    script = Path(__file__).resolve().parent / "internal" / "manage.py"
    command = [sys.executable, str(script), command_name]
    if verbose:
        print(f"{PREFIX} wrapper dispatch {command_name}", file=sys.stderr)
        command.append("--verbose")

    result = subprocess.run(command, check=False)
    return result.returncode


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
