#!/usr/bin/env node

"use strict";

const path = require("path");
const { spawnSync } = require("child_process");

const PREFIX = "[build-metadata]";
const USAGE = "Usage: sync-build-metadata.js [--verbose|-v]";

function parseArgs(argv) {
	if (argv.length > 1) {
		return null;
	}
	if (argv.length === 0) {
		return false;
	}
	if (argv[0] === "--verbose" || argv[0] === "-v") {
		return true;
	}
	return null;
}

const verbose = parseArgs(process.argv.slice(2));
if (verbose === null) {
	console.error(USAGE);
	process.exit(1);
}

if (verbose) {
	console.error(`${PREFIX} wrapper dispatch sync`);
}

const args = [path.join(__dirname, "build-metadata.js"), "sync"];
if (verbose) {
	args.push("--verbose");
}

const result = spawnSync(process.execPath, args, { stdio: "inherit" });
if (result.error) {
	throw result.error;
}

process.exit(result.status === null ? 1 : result.status);
