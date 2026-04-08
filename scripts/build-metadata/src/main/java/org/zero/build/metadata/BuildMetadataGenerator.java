package org.zero.build.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Build metadata generator - 统一协调 Gradle/Maven 两侧生成器的同步与校验。
 */
public final class BuildMetadataGenerator {
	private final File buildMetadataFile;
	private final BuildMetadataLogger logger;
	private final GradleMetadataWriter gradleWriter;
	private final MavenMetadataWriter mavenWriter;

	public BuildMetadataGenerator(File rootDir) {
		this(rootDir, new BuildMetadataLogger(false));
	}

	public BuildMetadataGenerator(File rootDir, BuildMetadataLogger logger) {
		this.buildMetadataFile = new File(new File(rootDir, "metadata"), "build-metadata.toml");
		this.logger = logger;
		this.gradleWriter = new GradleMetadataWriter(rootDir, logger);
		this.mavenWriter = new MavenMetadataWriter(rootDir, logger);
	}

	public void sync() {
		BuildMetadataModel metadata = loadMetadata();
		gradleWriter.sync(metadata);
		mavenWriter.sync(metadata);
	}

	public void verify() {
		BuildMetadataModel metadata = loadMetadata();
		List<String> mismatches = new ArrayList<String>();
		gradleWriter.verify(metadata, mismatches);
		mavenWriter.verify(metadata, mismatches);
		if (!mismatches.isEmpty()) {
			throw new IllegalStateException("Build metadata is out of sync:\n - " + MetadataFileSupport.join(mismatches));
		}
	}

	public void syncGradle() {
		gradleWriter.sync(loadMetadata());
	}

	public void verifyGradle() {
		List<String> mismatches = new ArrayList<String>();
		gradleWriter.verify(loadMetadata(), mismatches);
		if (!mismatches.isEmpty()) {
			throw new IllegalStateException("Gradle metadata is out of sync:\n - " + MetadataFileSupport.join(mismatches));
		}
	}

	public void syncMaven() {
		mavenWriter.sync(loadMetadata());
	}

	public void verifyMaven() {
		List<String> mismatches = new ArrayList<String>();
		mavenWriter.verify(loadMetadata(), mismatches);
		if (!mismatches.isEmpty()) {
			throw new IllegalStateException("Maven metadata is out of sync:\n - " + MetadataFileSupport.join(mismatches));
		}
	}

	private BuildMetadataModel loadMetadata() {
		logger.info("load metadata", buildMetadataFile);
		if (!buildMetadataFile.exists()) {
			throw new IllegalStateException("Missing build metadata file: " + buildMetadataFile);
		}
		return BuildMetadataParser.parse(buildMetadataFile);
	}
}
