package org.zero.build.metadata;

import java.io.File;

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
			return;
		}
		if ("verify".equals(command)) {
			logger.info("run verify");
			generator.verify();
			return;
		}

		throw new IllegalArgumentException("Unsupported command '" + command + "'");
	}
}
