package org.zero.build.metadata;

import java.io.File;
import java.io.IOException;

/**
 * Java entrypoint for build metadata sync and verification.
 */
public final class BuildMetadataCli {
	private static final String VERBOSE_FLAG = "--verbose";

	private BuildMetadataCli() {
	}

	public static void main(String[] args) {
		if (args.length < 2 || args.length > 3) {
			throw new IllegalArgumentException("Usage: BuildMetadataCli <sync|verify> <repoRoot> [--verbose]");
		}

		String command = args[0];
		File rootDir = new File(args[1]);
		boolean verbose = args.length == 3;
		if (verbose && !VERBOSE_FLAG.equals(args[2])) {
			throw new IllegalArgumentException("Unsupported flag '" + args[2] + "'");
		}

		BuildMetadataLogger logger = new BuildMetadataLogger(verbose);
		BuildMetadataGenerator generator = new BuildMetadataGenerator(rootDir, logger);
		if ("sync".equals(command)) {
			logger.info("run sync");
			generator.sync();
			logger.info("run verify");
			generator.verify();
			runSmokeChecks(rootDir, logger);
			return;
		}
		if ("verify".equals(command)) {
			logger.info("run verify");
			generator.verify();
			return;
		}

		throw new IllegalArgumentException("Unsupported command '" + command + "'");
	}

	private static void runSmokeChecks(File rootDir, BuildMetadataLogger logger) {
		runExternalCommand(rootDir, logger, "run gradle smoke", gradleSmokeCommand());
		runExternalCommand(rootDir, logger, "run maven smoke", mavenSmokeCommand());
	}

	private static String[] gradleSmokeCommand() {
		return isWindows()
			? new String[] { "cmd.exe", "/c", "gradlew.bat", "-q", "help", "-PexcludeProjects=common-test" }
			: new String[] { "./gradlew", "-q", "help", "-PexcludeProjects=common-test" };
	}

	private static String[] mavenSmokeCommand() {
		return isWindows()
			? new String[] { "cmd.exe", "/c", "mvnw.cmd", "-q", "validate", "-DskipTests" }
			: new String[] { "./mvnw", "-q", "validate", "-DskipTests" };
	}

	private static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	private static void runExternalCommand(File rootDir, BuildMetadataLogger logger, String stepName, String[] command) {
		logger.info(stepName);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(rootDir);
		processBuilder.inheritIO();
		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new IllegalStateException(stepName + " failed with exit code " + exitCode);
			}
		} catch (IOException ex) {
			throw new IllegalStateException(stepName + " failed to start", ex);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(stepName + " was interrupted", ex);
		}
	}
}
