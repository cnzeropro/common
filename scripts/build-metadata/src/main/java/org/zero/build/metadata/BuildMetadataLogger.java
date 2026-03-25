package org.zero.build.metadata;

import java.io.File;

final class BuildMetadataLogger {
	private static final String PREFIX = "[build-metadata] ";

	private final boolean verbose;

	BuildMetadataLogger(boolean verbose) {
		this.verbose = verbose;
	}

	public void info(String message) {
		if (verbose) {
			System.err.println(PREFIX + message);
		}
	}

	public void info(String action, File file) {
		info(action + " " + file.getPath());
	}
}
