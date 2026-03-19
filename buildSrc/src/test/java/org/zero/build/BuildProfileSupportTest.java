package org.zero.build;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.provider.Provider;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildProfileSupportTest {
	@Test
	void resolveProfileShouldHonorExplicitProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfile(project, "java17", "1.8.0_451");

		assertEquals("java17", resolvedProfile);
	}

	@Test
	void resolveActiveProfileChainShouldRepresentCurrentHigherProfiles(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		assertEquals(
			Arrays.asList("java8", "java11", "java17", "java21"),
			BuildProfileSupport.resolveActiveProfileChain(project, null, "21.0.1")
		);
	}

	@Test
	void resolveActiveProfileChainShouldHonorExplicitProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		assertEquals(
			Arrays.asList("java8", "java11", "java17"),
			BuildProfileSupport.resolveActiveProfileChain(project, "java17", "21.0.1")
		);
	}

	@Test
	void selectLibraryShouldResolveAgainstActiveHigherChain(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);
		TestVersionCatalogRegistry registry = new TestVersionCatalogRegistry();
		Provider<MinimalExternalModuleDependency> java17Only = registry.register("demo-java17-only_java17");
		Provider<MinimalExternalModuleDependency> java11Only = registry.register("demo-java11-only_java11");
		Provider<MinimalExternalModuleDependency> baseOnly = registry.register("demo-base-only");
		Provider<MinimalExternalModuleDependency> preferHighestJava17 = registry.register("demo-prefer-highest_java17");
		Provider<MinimalExternalModuleDependency> preferHighestJava21 = registry.register("demo-prefer-highest_java21");

		project.getExtensions().add(VersionCatalogsExtension.class, "versionCatalogs", registry.extension());

		assertSame(java17Only, BuildProfileSupport.selectLibrary(project, "demo-java17-only", "java21"));
		assertSame(java11Only, BuildProfileSupport.selectLibrary(project, "demo-java11-only", "java21"));
		assertSame(baseOnly, BuildProfileSupport.selectLibrary(project, "demo-base-only", "java21"));
		assertSame(preferHighestJava21, BuildProfileSupport.selectLibrary(project, "demo-prefer-highest", "java21"));
		assertSame(preferHighestJava17.get(), BuildProfileSupport.selectLibrary(project, "demo-prefer-highest", "java17").get());
	}

	@Test
	void resolveProfileAtLeastShouldUseBaselineWhenCurrentJvmIsLower(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfileAtLeast(project, null, "java17", "1.8.0_451");

		assertEquals("java17", resolvedProfile);
	}

	@Test
	void resolveProfileAtLeastShouldKeepHigherCompatibleProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfileAtLeast(project, null, "java17", "21.0.1");

		assertEquals("java21", resolvedProfile);
	}

	@Test
	void resolveProfileAtLeastShouldRejectExplicitProfileBelowBaseline(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildProfileSupport.resolveProfileAtLeast(project, "java8", "java17", "1.8.0_451")
		);

		assertTrue(exception.getMessage().contains("required baseline 'java17'"));
	}

	@Test
	void resolveProfileShouldFailWhenSupportedProfilesPropertyMissing(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, false);

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> BuildProfileSupport.resolveProfile(project, null, "17.0.10")
		);

		assertTrue(exception.getMessage().contains("build.profiles"));
	}

	private Project createProject(Path tempDir, boolean writeSupportedProfiles) throws IOException {
		Files.createDirectories(tempDir);
		if (writeSupportedProfiles) {
			Files.write(
				tempDir.resolve("gradle.properties"),
				Arrays.asList(
					"org.gradle.jvmargs=-Xmx1g",
					"build.profiles=java8,java11,java17,java21",
					"build.revision=1.0.0"
				),
				StandardCharsets.UTF_8
			);
		} else {
			Files.write(
				tempDir.resolve("gradle.properties"),
				Arrays.asList(
					"org.gradle.jvmargs=-Xmx1g",
					"build.revision=1.0.0"
				),
				StandardCharsets.UTF_8
			);
		}
		Files.write(
			tempDir.resolve("settings.gradle"),
			Arrays.asList("rootProject.name = 'test-project'"),
			StandardCharsets.UTF_8
		);
		Project project = ProjectBuilder.builder().withProjectDir(tempDir.toFile()).build();
		if (writeSupportedProfiles) {
			Properties properties = new Properties();
			try (java.io.Reader reader = Files.newBufferedReader(tempDir.resolve("gradle.properties"), StandardCharsets.UTF_8)) {
				properties.load(reader);
			}
			project.getExtensions().getExtraProperties().set(
				"build.profiles",
				properties.getProperty("build.profiles")
			);
		}
		return project;
	}

	private static Object defaultValue(Class<?> returnType) {
		if (!returnType.isPrimitive()) {
			return null;
		}
		if (boolean.class.equals(returnType)) {
			return false;
		}
		if (char.class.equals(returnType)) {
			return Character.valueOf('\0');
		}
		if (byte.class.equals(returnType)) {
			return Byte.valueOf((byte) 0);
		}
		if (short.class.equals(returnType)) {
			return Short.valueOf((short) 0);
		}
		if (int.class.equals(returnType)) {
			return Integer.valueOf(0);
		}
		if (long.class.equals(returnType)) {
			return Long.valueOf(0L);
		}
		if (float.class.equals(returnType)) {
			return Float.valueOf(0.0F);
		}
		return Double.valueOf(0.0D);
	}

	private static final class TestVersionCatalogRegistry {
		private final Map<String, Provider<MinimalExternalModuleDependency>> libraries =
			new LinkedHashMap<String, Provider<MinimalExternalModuleDependency>>();
		private final VersionCatalog catalog = (VersionCatalog) Proxy.newProxyInstance(
			VersionCatalog.class.getClassLoader(),
			new Class<?>[] { VersionCatalog.class },
			(proxy, method, args) -> {
				String methodName = method.getName();
				if ("findLibrary".equals(methodName)) {
					return Optional.ofNullable(libraries.get((String) args[0]));
				}
				if ("toString".equals(methodName)) {
					return "testVersionCatalog";
				}
				return defaultValue(method.getReturnType());
			}
		);
		private final VersionCatalogsExtension extension = (VersionCatalogsExtension) Proxy.newProxyInstance(
			VersionCatalogsExtension.class.getClassLoader(),
			new Class<?>[] { VersionCatalogsExtension.class },
			(proxy, method, args) -> {
				String methodName = method.getName();
				if ("named".equals(methodName)) {
					return catalog;
				}
				if ("toString".equals(methodName)) {
					return "testVersionCatalogsExtension";
				}
				return defaultValue(method.getReturnType());
			}
		);

		private Provider<MinimalExternalModuleDependency> register(String alias) {
			MinimalExternalModuleDependency dependency = (MinimalExternalModuleDependency) Proxy.newProxyInstance(
				MinimalExternalModuleDependency.class.getClassLoader(),
				new Class<?>[] { MinimalExternalModuleDependency.class },
				(proxy, method, args) -> {
					if ("toString".equals(method.getName())) {
						return "dependency(" + alias + ")";
					}
					return defaultValue(method.getReturnType());
				}
			);
			@SuppressWarnings("unchecked")
			Provider<MinimalExternalModuleDependency> provider = (Provider<MinimalExternalModuleDependency>) Proxy.newProxyInstance(
				Provider.class.getClassLoader(),
				new Class<?>[] { Provider.class },
				(proxy, method, args) -> {
					String methodName = method.getName();
					if ("get".equals(methodName) || "getOrNull".equals(methodName)) {
						return dependency;
					}
					if ("isPresent".equals(methodName)) {
						return true;
					}
					if ("toString".equals(methodName)) {
						return "provider(" + alias + ")";
					}
					return defaultValue(method.getReturnType());
				}
			);
			libraries.put(alias, provider);
			return provider;
		}

		private VersionCatalogsExtension extension() {
			return extension;
		}
	}
}
