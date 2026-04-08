#!/usr/bin/env node
"use strict";

const path = require("path");
const {spawnSync} = require("child_process");

const USAGE = "Usage: sync-maven.js [--verbose|-v]";
const verbose = process.argv.length === 3 ? process.argv[2] : "";
if (process.argv.length > 3 || (verbose && !["--verbose", "-v"].includes(verbose))) {
    console.error(USAGE);
    process.exit(1);
}
if (verbose) {
    console.error("[manage-metadata] wrapper dispatch sync-maven");
}
const args = [path.join(__dirname, "manage-metadata.js"), "sync-maven"];
if (verbose) {
    args.push("--verbose");
}
const result = spawnSync(process.execPath, args, {stdio: "inherit"});
if (result.error) {
    throw result.error;
}
process.exit(result.status === null ? 1 : result.status);
