package org.zero.build.metadata;

import java.io.File;
import java.io.PrintStream;

/**
 * Java entrypoint for build metadata sync and verification.
 */
public final class BuildMetadataCli {
	private static final String USAGE = "Usage: BuildMetadataCli <sync|verify|sync-gradle|verify-gradle|sync-maven|verify-maven> <repoRoot> [--verbose]";
	private static final String SYNC_COMMAND = "sync";
	private static final String VERIFY_COMMAND = "verify";
	private static final String SYNC_GRADLE_COMMAND = "sync-gradle";
	private static final String VERIFY_GRADLE_COMMAND = "verify-gradle";
	private static final String SYNC_MAVEN_COMMAND = "sync-maven";
	private static final String VERIFY_MAVEN_COMMAND = "verify-maven";
	private static final String VERBOSE_FLAG = "--verbose";

	private BuildMetadataCli() {
	}

	public static void main(String[] args) {
		int exitCode = execute(args, System.err);
		if (exitCode != 0) {
			System.exit(exitCode);
		}
	}

	static int execute(String[] args, PrintStream errorStream) {
		try {
			return run(args);
		} catch (IllegalArgumentException ex) {
			errorStream.println(ex.getMessage());
			return 1;
		} catch (IllegalStateException ex) {
			errorStream.println(ex.getMessage());
			return 1;
		}
	}

	private static int run(String[] args) {
		if (args.length < 2 || args.length > 3) {
			throw new IllegalArgumentException(USAGE);
		}

		String commandName = args[0];
		File rootDir = new File(args[1]);
		boolean verbose = args.length == 3;
		if (verbose && !VERBOSE_FLAG.equals(args[2])) {
			throw new IllegalArgumentException("Unsupported flag '" + args[2] + "'");
		}

		BuildMetadataLogger logger = new BuildMetadataLogger(verbose);
		BuildMetadataGenerator generator = new BuildMetadataGenerator(rootDir, logger);
		if (SYNC_COMMAND.equals(commandName)) {
			logger.info("run sync");
			generator.sync();
			return 0;
		}
		if (VERIFY_COMMAND.equals(commandName)) {
			logger.info("run verify");
			generator.verify();
			return 0;
		}
		if (SYNC_GRADLE_COMMAND.equals(commandName)) {
			logger.info("run sync-gradle");
			generator.syncGradle();
			return 0;
		}
		if (VERIFY_GRADLE_COMMAND.equals(commandName)) {
			logger.info("run verify-gradle");
			generator.verifyGradle();
			return 0;
		}
		if (SYNC_MAVEN_COMMAND.equals(commandName)) {
			logger.info("run sync-maven");
			generator.syncMaven();
			return 0;
		}
		if (VERIFY_MAVEN_COMMAND.equals(commandName)) {
			logger.info("run verify-maven");
			generator.verifyMaven();
			return 0;
		}

		throw new IllegalArgumentException("Unsupported command '" + commandName + "'");
	}
}
