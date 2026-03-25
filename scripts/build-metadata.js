#!/usr/bin/env node

"use strict";

const fs = require("fs");
const path = require("path");
const { spawnSync } = require("child_process");

const PREFIX = "[build-metadata]";
const USAGE = "Usage: build-metadata.js <sync|verify> [--verbose|-v]";

function log(verbose, message) {
	if (verbose) {
		console.error(`${PREFIX} ${message}`);
	}
}

function parseArgs(argv) {
	if (argv.length !== 1 && argv.length !== 2) {
		return null;
	}

	const commandName = argv[0];
	if (!["sync", "verify"].includes(commandName)) {
		return null;
	}

	let verbose = false;
	if (argv.length === 2) {
		if (!["--verbose", "-v"].includes(argv[1])) {
			return null;
		}
		verbose = true;
	}

	return { commandName, verbose };
}

function collectJavaSources(sourceDir) {
	const collected = [];
	for (const entry of fs.readdirSync(sourceDir, { withFileTypes: true })) {
		const fullPath = path.join(sourceDir, entry.name);
		if (entry.isDirectory()) {
			collected.push(...collectJavaSources(fullPath));
			continue;
		}
		if (entry.isFile() && entry.name.endsWith(".java")) {
			collected.push(fullPath);
		}
	}
	return collected.sort();
}

function runCommand(command, args) {
	const result = spawnSync(command, args, { stdio: "inherit" });
	if (result.error) {
		throw result.error;
	}
	return result.status === null ? 1 : result.status;
}

function hasCommand(command) {
	const probeCommand = process.platform === "win32" ? "where" : "which";
	const result = spawnSync(probeCommand, [command], { stdio: "ignore" });
	return !result.error && result.status === 0;
}

function main(argv) {
	const parsed = parseArgs(argv);
	if (!parsed) {
		console.error(USAGE);
		return 1;
	}

	const { commandName, verbose } = parsed;
	log(verbose, "arguments parsed");

	const scriptDir = __dirname;
	const rootDir = path.resolve(scriptDir, "..");
	const sourceDir = path.join(scriptDir, "build-metadata", "src", "main", "java");
	const buildRootDir = path.join(rootDir, "build", "build-metadata-cli");
	const buildDir = path.join(buildRootDir, String(process.pid));
	const classesDir = path.join(buildDir, "classes");
	const sourceListFile = path.join(buildDir, "sources.txt");
	log(verbose, `paths ready: root=${rootDir}`);

	fs.mkdirSync(classesDir, { recursive: true });

	try {
		const javac = process.platform === "win32" ? "javac.exe" : "javac";
		const java = process.platform === "win32" ? "java.exe" : "java";

		if (!hasCommand(javac)) {
			console.error("Missing javac command. Please configure a JDK and ensure javac is on PATH.");
			return 1;
		}

		if (!hasCommand(java)) {
			console.error("Missing java command. Please configure a JDK and ensure java is on PATH.");
			return 1;
		}

		log(verbose, "toolchain ready");

		const sources = collectJavaSources(sourceDir);
		fs.writeFileSync(sourceListFile, `${sources.join("\n")}`, { encoding: "ascii" });
		log(verbose, `source list ready: ${sources.length} file(s)`);

		log(verbose, "javac start");
		let exitCode = runCommand(javac, ["-encoding", "UTF-8", "-d", classesDir, `@${sourceListFile}`]);
		log(verbose, `javac done: exit=${exitCode}`);
		if (exitCode !== 0) {
			return exitCode;
		}

		const javaArgs = ["-cp", classesDir, "org.zero.build.metadata.BuildMetadataCli", commandName, rootDir];
		if (verbose) {
			javaArgs.push("--verbose");
		}

		log(verbose, "java cli start");
		exitCode = runCommand(java, javaArgs);
		log(verbose, `java cli done: exit=${exitCode}`);
		return exitCode;
	} finally {
		log(verbose, "cleanup start");
		fs.rmSync(buildDir, { recursive: true, force: true });
		log(verbose, "cleanup done");
	}
}

process.exit(main(process.argv.slice(2)));
