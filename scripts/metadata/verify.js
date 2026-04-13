#!/usr/bin/env node

"use strict";

const path = require("path");
const {spawnSync} = require("child_process");

const PREFIX = "[manage-metadata]";
const USAGE = "Usage: verify.js [gradle|maven] [--verbose|-v]";

function parseArgs(argv) {
    let target = null;
    let verbose = false;
    for (const arg of argv) {
        if (arg === "gradle" || arg === "maven") {
            if (target !== null) {
                return null;
            }
            target = arg;
            continue;
        }
        if (arg === "--verbose" || arg === "-v") {
            if (verbose) {
                return null;
            }
            verbose = true;
            continue;
        }
        return null;
    }
    return {target, verbose};
}

const parsed = parseArgs(process.argv.slice(2));
if (parsed === null) {
    console.error(USAGE);
    process.exit(1);
}

let commandName = "verify";
if (parsed.target === "gradle") {
    commandName = "verify-gradle";
} else if (parsed.target === "maven") {
    commandName = "verify-maven";
}

if (parsed.verbose) {
    console.error(`${PREFIX} wrapper dispatch ${commandName}`);
}

const args = [path.join(__dirname, "internal", "manage.js"), commandName];
if (parsed.verbose) {
    args.push("--verbose");
}

const result = spawnSync(process.execPath, args, {stdio: "inherit"});
if (result.error) {
    throw result.error;
}

process.exit(result.status === null ? 1 : result.status);
