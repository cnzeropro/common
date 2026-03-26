package org.zero.build.metadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildMetadataCliTest {
	@Test
	void executeShouldPrintUsageForMissingArguments() {
		CliResult result = runCli();

		assertEquals(1, result.exitCode);
		assertEquals("Usage: BuildMetadataCli <sync|verify> <repoRoot> [--verbose]", result.stderr.trim());
	}

	@Test
	void executeShouldPrintControlledMessageForUnsupportedFlag(@TempDir Path tempDir) {
		CliResult result = runCli("sync", tempDir.toString(), "--debug");

		assertEquals(1, result.exitCode);
		assertEquals("Unsupported flag '--debug'", result.stderr.trim());
		assertFalse(result.stderr.contains("Exception"));
	}

	@Test
	void executeShouldPrintControlledMessageForUnsupportedCommand(@TempDir Path tempDir) {
		CliResult result = runCli("plan", tempDir.toString());

		assertEquals(1, result.exitCode);
		assertEquals("Unsupported command 'plan'", result.stderr.trim());
		assertFalse(result.stderr.contains("Exception"));
	}

	@Test
	void executeShouldPrintBusinessErrorWithoutStackTrace(@TempDir Path tempDir) throws IOException {
		CliResult result = runCli("verify", tempDir.toString());

		assertEquals(1, result.exitCode);
		assertTrue(result.stderr.contains("Missing build metadata file: "));
		assertFalse(result.stderr.contains("Exception in thread"));
	}

	private CliResult runCli(String... args) {
		ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
		PrintStream errorStream = new PrintStream(errorBytes, true);
		int exitCode = BuildMetadataCli.execute(args, errorStream);
		errorStream.flush();
		return new CliResult(exitCode, new String(errorBytes.toByteArray(), StandardCharsets.UTF_8));
	}

	private static final class CliResult {
		private final int exitCode;
		private final String stderr;

		private CliResult(int exitCode, String stderr) {
			this.exitCode = exitCode;
			this.stderr = stderr;
		}
	}
}
