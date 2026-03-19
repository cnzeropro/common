package org.zero.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.Project;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.provider.Provider;

/**
 * Build profile support - 统一处理 effective profile 解析、active higher chain 构造与 catalog alias 回退。
 */
public final class BuildProfileSupport {
	private static final String SUPPORTED_PROFILES_PROPERTY = "build.profiles";
	private static final Pattern JAVA_PROFILE_PATTERN = Pattern.compile("^java(\\d+)$");
	private static final Map<String, ProfileMetadata> PROFILE_METADATA_CACHE = new ConcurrentHashMap<String, ProfileMetadata>();

	private BuildProfileSupport() {
	}

	public static String resolveProfile(Project project, Object explicitProfile) {
		return resolveProfile(project, explicitProfile, System.getProperty("java.version"));
	}

	public static List<String> resolveActiveProfileChain(Project project, Object explicitProfile) {
		return resolveActiveProfileChain(project, explicitProfile, System.getProperty("java.version"));
	}

	public static String resolveProfileAtLeast(Project project, Object explicitProfile, String baselineProfile) {
		return resolveProfileAtLeast(project, explicitProfile, baselineProfile, System.getProperty("java.version"));
	}

	public static String resolveProfile(Project project, Object explicitProfile, String javaVersionText) {
		return lastActiveProfile(resolveActiveProfileChain(project, explicitProfile, javaVersionText));
	}

	public static List<String> resolveActiveProfileChain(Project project, Object explicitProfile, String javaVersionText) {
		ProfileMetadata profileMetadata = profileMetadata(project);
		String effectiveProfile = resolveEffectiveProfile(profileMetadata, explicitProfile, javaVersionText);
		/*
		 * active higher chain - 以 effective profile 为上限，显式表示等价于 Maven xxx-higher 累积激活的 profile 链。
		 */
		return activeProfileChain(profileMetadata, effectiveProfile);
	}

	public static String resolveProfileAtLeast(
		Project project,
		Object explicitProfile,
		String baselineProfile,
		String javaVersionText
	) {
		ProfileMetadata profileMetadata = profileMetadata(project);
		List<String> supportedProfiles = profileMetadata.getSupportedProfiles();
		int baselineIndex = profileIndex(supportedProfiles, baselineProfile);
		String normalizedExplicitProfile = normalizeExplicitProfile(explicitProfile, supportedProfiles);
		if (normalizedExplicitProfile != null) {
			if (profileIndex(supportedProfiles, normalizedExplicitProfile) < baselineIndex) {
				throw new IllegalArgumentException(
					"Build profile '" + normalizedExplicitProfile + "' is lower than required baseline '" + baselineProfile
						+ "'. Supported values: " + String.join(", ", supportedProfiles)
				);
			}
			return normalizedExplicitProfile;
		}

		String resolvedProfile = highestCompatibleProfile(profileMetadata, detectJavaVersion(javaVersionText));
		return profileIndex(supportedProfiles, resolvedProfile) < baselineIndex ? baselineProfile : resolvedProfile;
	}

	public static int detectJavaVersion(String javaVersionText) {
		if (javaVersionText == null || javaVersionText.trim().isEmpty()) {
			return 8;
		}
		String trimmed = javaVersionText.trim();
		/*
		 * legacy 1.x syntax - 兼容 Java 8 常见的 1.8.0_xxx 版本号格式。
		 */
		if (trimmed.startsWith("1.")) {
			return Integer.parseInt(trimmed.split("\\.")[1]);
		}
		/*
		 * modern version syntax - Java 9+ 只取主版本号即可完成 effective profile 推导。
		 */
		String major = trimmed.split("[-+_.]")[0];
		return Integer.parseInt(major);
	}

	public static Provider<MinimalExternalModuleDependency> selectLibrary(Project project, String baseAlias, Object explicitProfile) {
		VersionCatalog catalog = project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs");
		List<String> activeProfileChain = resolveActiveProfileChain(project, explicitProfile);
		String effectiveProfile = lastActiveProfile(activeProfileChain);
		for (String alias : profileAliasCandidates(baseAlias, activeProfileChain)) {
			Optional<Provider<MinimalExternalModuleDependency>> library = catalog.findLibrary(alias);
			if (library.isPresent()) {
				return library.get();
			}
		}
		throw new IllegalArgumentException(
			"Missing Gradle catalog alias '" + baseAlias + "' for effective build profile '" + effectiveProfile + "'."
		);
	}

	public static boolean isAtLeast(Project project, String profile, String baselineProfile) {
		List<String> supportedProfiles = profileMetadata(project).getSupportedProfiles();
		int currentIndex = profileIndex(supportedProfiles, profile);
		int baselineIndex = profileIndex(supportedProfiles, baselineProfile);
		return currentIndex >= baselineIndex;
	}

	public static int resolveToolchainLanguageVersion(Project project, int requiredVersion) {
		ProfileMetadata profileMetadata = profileMetadata(project);
		for (String supportedProfile : profileMetadata.getSupportedProfiles()) {
			int supportedJavaVersion = profileJavaVersion(supportedProfile);
			if (supportedJavaVersion >= requiredVersion) {
				return supportedJavaVersion;
			}
		}
		return requiredVersion;
	}

	private static ProfileMetadata profileMetadata(Project project) {
		File rootDir = project.getRootProject().getProjectDir();
		String rawProfiles = requiredSupportedProfilesProperty(project);
		return loadProfileMetadata(rootDir, rawProfiles);
	}

	private static ProfileMetadata loadProfileMetadata(File rootDir, String rawProfiles) {
		String cacheKey = rootDir.getAbsolutePath() + "|" + rawProfiles;
		ProfileMetadata cachedMetadata = PROFILE_METADATA_CACHE.get(cacheKey);
		if (cachedMetadata != null) {
			return cachedMetadata;
		}

		ProfileMetadata immutableMetadata = parseProfileMetadata(rawProfiles);
		ProfileMetadata existingMetadata = PROFILE_METADATA_CACHE.putIfAbsent(cacheKey, immutableMetadata);
		return existingMetadata != null ? existingMetadata : immutableMetadata;
	}

	private static String resolveEffectiveProfile(ProfileMetadata profileMetadata, Object explicitProfile, String javaVersionText) {
		List<String> supportedProfiles = profileMetadata.getSupportedProfiles();
		/*
		 * explicit profile priority - 命令行显式传入 buildProfile 时，直接锁定 effective profile。
		 */
		String normalizedExplicitProfile = normalizeExplicitProfile(explicitProfile, supportedProfiles);
		if (normalizedExplicitProfile != null) {
			return normalizedExplicitProfile;
		}

		/*
		 * current JDK fallback - 未显式指定时，按当前运行 JDK 推导命中的最高 higher 阈值 profile。
		 */
		int currentJavaVersion = detectJavaVersion(javaVersionText);
		return highestCompatibleProfile(profileMetadata, currentJavaVersion);
	}

	private static String requiredSupportedProfilesProperty(Project project) {
		Object rawProfiles = project.findProperty(SUPPORTED_PROFILES_PROPERTY);
		if (rawProfiles == null || rawProfiles.toString().trim().isEmpty()) {
			throw new IllegalStateException(
				"Missing Gradle property '" + SUPPORTED_PROFILES_PROPERTY + "'."
			);
		}
		return rawProfiles.toString();
	}

	private static ProfileMetadata parseProfileMetadata(String rawProfiles) {
		List<String> profiles = parseProfiles(rawProfiles);
		if (profiles.isEmpty()) {
			throw new IllegalStateException(
				"Gradle property '" + SUPPORTED_PROFILES_PROPERTY + "' is empty."
			);
		}
		String baseProfile = profiles.get(0);
		return new ProfileMetadata(baseProfile, profiles);
	}

	private static String highestCompatibleProfile(ProfileMetadata profileMetadata, int currentJavaVersion) {
		String resolvedProfile = profileMetadata.getBaseProfile();
		for (String supportedProfile : profileMetadata.getSupportedProfiles()) {
			if (profileJavaVersion(supportedProfile) <= currentJavaVersion) {
				resolvedProfile = supportedProfile;
			}
		}
		return resolvedProfile;
	}

	private static List<String> activeProfileChain(ProfileMetadata profileMetadata, String effectiveProfile) {
		List<String> supportedProfiles = profileMetadata.getSupportedProfiles();
		int effectiveProfileIndex = profileIndex(supportedProfiles, effectiveProfile);
		return Collections.unmodifiableList(new ArrayList<String>(supportedProfiles.subList(0, effectiveProfileIndex + 1)));
	}

	private static String lastActiveProfile(List<String> activeProfileChain) {
		return activeProfileChain.get(activeProfileChain.size() - 1);
	}

	private static List<String> profileAliasCandidates(String baseAlias, List<String> activeProfileChain) {
		String baseProfile = activeProfileChain.get(0);
		List<String> aliases = new ArrayList<String>();
		for (int index = activeProfileChain.size() - 1; index >= 0; index--) {
			aliases.add(profileAlias(baseAlias, activeProfileChain.get(index), baseProfile));
		}
		return aliases;
	}

	private static String profileAlias(String baseAlias, String profile, String baseProfile) {
		return baseProfile.equals(profile) ? baseAlias : baseAlias + "_" + profile;
	}

	private static int profileJavaVersion(String profile) {
		Matcher matcher = JAVA_PROFILE_PATTERN.matcher(profile);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
				"Unsupported build profile naming '" + profile + "'. Expected Gradle property '" + SUPPORTED_PROFILES_PROPERTY + "' values like java8/java17"
			);
		}
		return Integer.parseInt(matcher.group(1));
	}

	private static int profileIndex(List<String> supportedProfiles, String profile) {
		int profileIndex = supportedProfiles.indexOf(profile);
		if (profileIndex < 0) {
			throw new IllegalArgumentException(
				"Unsupported build profile '" + profile + "'. Supported values: " + String.join(", ", supportedProfiles)
			);
		}
		return profileIndex;
	}

	private static String normalizeExplicitProfile(Object explicitProfile, List<String> supportedProfiles) {
		if (explicitProfile == null) {
			return null;
		}
		String normalized = explicitProfile.toString().trim();
		if (!supportedProfiles.contains(normalized)) {
			throw new IllegalArgumentException(
				"Unsupported build profile '" + normalized + "'. Supported values: " + String.join(", ", supportedProfiles)
			);
		}
		return normalized;
	}

	private static List<String> parseProfiles(String value) {
		if (value == null || value.trim().isEmpty()) {
			return Collections.emptyList();
		}

		List<String> profiles = new ArrayList<String>();
		for (String token : value.split(",")) {
			String profile = token.trim();
			if (!profile.isEmpty()) {
				profiles.add(profile);
			}
		}
		return profiles;
	}

	private static final class ProfileMetadata {
		private final String baseProfile;
		private final List<String> supportedProfiles;

		private ProfileMetadata(String baseProfile, List<String> supportedProfiles) {
			this.baseProfile = baseProfile;
			this.supportedProfiles = Collections.unmodifiableList(new ArrayList<String>(supportedProfiles));
		}

		public String getBaseProfile() {
			return baseProfile;
		}

		public List<String> getSupportedProfiles() {
			return supportedProfiles;
		}
	}
}
