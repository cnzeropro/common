package org.zero.build.metadata;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Build metadata model - 聚合 values/libraries/plugins 条目并提供 profile 解析能力。
 */
public final class BuildMetadataModel {
	private static final Pattern JAVA_PROFILE_PATTERN = Pattern.compile("^java(\\d+)$");

	private String revision;
	private String javaVersion;
	private final List<String> supportedProfiles = new ArrayList<String>();
	private final Map<String, String> mavenProperties = new LinkedHashMap<String, String>();
	private final Map<String, ValueEntry> values = new LinkedHashMap<String, ValueEntry>();
	private final Map<String, LibraryEntry> libraries = new LinkedHashMap<String, LibraryEntry>();
	private final Map<String, MavenPluginEntry> mavenPlugins = new LinkedHashMap<String, MavenPluginEntry>();
	private final Map<String, GradlePluginEntry> gradlePlugins = new LinkedHashMap<String, GradlePluginEntry>();
	private final Map<String, VersionedEntry> referenceableEntries = new LinkedHashMap<String, VersionedEntry>();

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public void setSupportedProfiles(List<String> profiles) {
		supportedProfiles.clear();
		if (profiles != null) {
			supportedProfiles.addAll(profiles);
		}
	}

	public String getBaseProfile() {
		if (supportedProfiles.isEmpty()) {
			throw new IllegalStateException("Missing supported profiles");
		}
		return supportedProfiles.get(0);
	}

	public List<String> getSupportedProfiles() {
		return Collections.unmodifiableList(supportedProfiles);
	}

	public Map<String, String> getMavenProperties() {
		return mavenProperties;
	}

	public void putMavenProperty(String name, String value) {
		mavenProperties.put(name, value);
	}

	public Map<String, ValueEntry> getValues() {
		return values;
	}

	public Map<String, LibraryEntry> getLibraries() {
		return libraries;
	}

	public Map<String, MavenPluginEntry> getMavenPlugins() {
		return mavenPlugins;
	}

	public Map<String, GradlePluginEntry> getGradlePlugins() {
		return gradlePlugins;
	}

	public ValueEntry getOrCreateValue(String alias) {
		ValueEntry entry = values.get(alias);
		if (entry != null) {
			return entry;
		}
		ValueEntry created = new ValueEntry(alias);
		values.put(alias, created);
		return created;
	}

	public LibraryEntry getOrCreateLibrary(String alias) {
		LibraryEntry entry = libraries.get(alias);
		if (entry != null) {
			return entry;
		}
		LibraryEntry created = new LibraryEntry(alias);
		libraries.put(alias, created);
		return created;
	}

	public MavenPluginEntry getOrCreateMavenPlugin(String alias) {
		MavenPluginEntry entry = mavenPlugins.get(alias);
		if (entry != null) {
			return entry;
		}
		MavenPluginEntry created = new MavenPluginEntry(alias);
		mavenPlugins.put(alias, created);
		return created;
	}

	public GradlePluginEntry getOrCreateGradlePlugin(String alias) {
		GradlePluginEntry entry = gradlePlugins.get(alias);
		if (entry != null) {
			return entry;
		}
		GradlePluginEntry created = new GradlePluginEntry(alias);
		gradlePlugins.put(alias, created);
		return created;
	}

	public int profileIndex(String profile) {
		int index = supportedProfiles.indexOf(profile);
		if (index < 0) {
			throw new IllegalArgumentException(
				"Unsupported build profile '" + profile + "'. Supported values: " + String.join(", ", supportedProfiles)
			);
		}
		return index;
	}

	public String previousProfile(String profile) {
		int profileIndex = profileIndex(profile);
		return profileIndex <= 0 ? getBaseProfile() : supportedProfiles.get(profileIndex - 1);
	}

	public String propertyName(String alias) {
		return alias + ".version";
	}

	public VersionedEntry getReferenceableEntry(String alias) {
		return referenceableEntries.get(alias);
	}

	public List<VersionedEntry> getCatalogVersionEntries() {
		List<VersionedEntry> entries = new ArrayList<VersionedEntry>();
		entries.addAll(values.values());
		entries.addAll(libraries.values());
		for (GradlePluginEntry plugin : gradlePlugins.values()) {
			if (plugin.hasVersions()) {
				entries.add(plugin);
			}
		}
		return entries;
	}

	public List<LibraryEntry> getCatalogLibraries() {
		return new ArrayList<LibraryEntry>(libraries.values());
	}

	public List<GradlePluginEntry> getVersionedGradlePlugins() {
		List<GradlePluginEntry> entries = new ArrayList<GradlePluginEntry>();
		for (GradlePluginEntry plugin : gradlePlugins.values()) {
			if (plugin.hasVersions()) {
				entries.add(plugin);
			}
		}
		return entries;
	}

	public List<VersionedEntry> getPomPropertyEntries() {
		List<VersionedEntry> entries = new ArrayList<VersionedEntry>();
		entries.addAll(values.values());
		entries.addAll(libraries.values());
		entries.addAll(mavenPlugins.values());
		return entries;
	}

	public List<LibraryEntry> getBaseManagedLibraries() {
		List<LibraryEntry> managed = new ArrayList<LibraryEntry>();
		String baseProfile = getBaseProfile();
		for (LibraryEntry library : libraries.values()) {
			if (library.resolveVersion(this, baseProfile) != null) {
				managed.add(library);
			}
		}
		return managed;
	}

	public List<LibraryEntry> getManagedLibrariesForProfile(String profile) {
		List<LibraryEntry> managed = new ArrayList<LibraryEntry>();
		if (profile.equals(getBaseProfile())) {
			return managed;
		}
		String previousProfile = previousProfile(profile);
		for (LibraryEntry library : libraries.values()) {
			if (library.resolveVersion(this, getBaseProfile()) != null) {
				continue;
			}
			String previousVersion = library.resolveVersion(this, previousProfile);
			String currentVersion = library.resolveVersion(this, profile);
			if (previousVersion == null && currentVersion != null) {
				managed.add(library);
			}
		}
		return managed;
	}

	public List<MavenPluginEntry> getBaseMavenPlugins() {
		return new ArrayList<MavenPluginEntry>(mavenPlugins.values());
	}

	public Map<String, String> resolveVersions(List<? extends VersionedEntry> entries, String profile) {
		profileIndex(profile);
		Map<String, String> resolved = new LinkedHashMap<String, String>();
		for (VersionedEntry entry : entries) {
			String value = entry.resolveVersion(this, profile);
			if (value != null) {
				resolved.put(entry.getAlias(), value);
			}
		}
		return resolved;
	}

	public Map<String, String> resolveChangedVersions(List<? extends VersionedEntry> entries, String profile) {
		profileIndex(profile);
		if (profile.equals(getBaseProfile())) {
			return new LinkedHashMap<String, String>();
		}

		String previousProfile = previousProfile(profile);
		Map<String, String> changed = new LinkedHashMap<String, String>();
		for (VersionedEntry entry : entries) {
			String currentValue = entry.resolveVersion(this, profile);
			String previousValue = entry.resolveVersion(this, previousProfile);
			if (currentValue != null && !currentValue.equals(previousValue)) {
				changed.put(entry.getAlias(), currentValue);
			}
		}
		return changed;
	}

	public void validate() {
		if (revision == null || revision.isEmpty()) {
			throw new IllegalArgumentException("Missing revision");
		}
		if (javaVersion == null || javaVersion.isEmpty()) {
			throw new IllegalArgumentException("Missing metadata java");
		}
		if (supportedProfiles.isEmpty()) {
			throw new IllegalArgumentException("Missing metadata profiles");
		}

		validateProfiles();
		validateMavenProperties();
		referenceableEntries.clear();

		Map<String, String> aliases = new LinkedHashMap<String, String>();
		collectAliases(values.values(), aliases);
		collectAliases(libraries.values(), aliases);
		collectAliases(mavenPlugins.values(), aliases);
		collectAliases(gradlePlugins.values(), aliases);

		prepareEntries(values.values());
		prepareEntries(libraries.values());
		prepareEntries(mavenPlugins.values());
		prepareEntries(gradlePlugins.values());

		validateEntries(values.values());
		validateEntries(libraries.values());
		validateEntries(mavenPlugins.values());
		validateEntries(gradlePlugins.values());

		validateResolvableEntries(values.values());
		validateResolvableEntries(libraries.values());
		validateResolvableEntries(mavenPlugins.values());
		validateResolvableEntries(gradlePlugins.values());
	}

	private void validateProfiles() {
		for (String profile : supportedProfiles) {
			if (!JAVA_PROFILE_PATTERN.matcher(profile).matches()) {
				throw new IllegalArgumentException(
					"Unsupported build profile naming '" + profile + "'. Expected metadata profile 'java<version>'."
				);
			}
		}
	}

	private void validateMavenProperties() {
		for (Map.Entry<String, String> entry : mavenProperties.entrySet()) {
			if (entry.getKey() == null || entry.getKey().trim().isEmpty()) {
				throw new IllegalArgumentException("Maven property key must not be blank");
			}
			if (entry.getValue() == null || entry.getValue().isEmpty()) {
				throw new IllegalArgumentException("Maven property '" + entry.getKey() + "' must not be blank");
			}
		}
	}

	private void collectAliases(Iterable<? extends MetadataEntry> entries, Map<String, String> aliases) {
		for (MetadataEntry entry : entries) {
			String existingType = aliases.put(entry.getAlias(), entry.getClass().getSimpleName());
			if (existingType != null) {
				throw new IllegalArgumentException("Duplicate metadata alias '" + entry.getAlias() + "'");
			}
			if (entry instanceof VersionedEntry) {
				VersionedEntry versioned = (VersionedEntry) entry;
				if (versioned.isReferenceable()) {
					referenceableEntries.put(versioned.getAlias(), versioned);
				}
			}
		}
	}

	private void prepareEntries(Iterable<? extends MetadataEntry> entries) {
		for (MetadataEntry entry : entries) {
			if (entry instanceof VersionedEntry) {
				((VersionedEntry) entry).prepare(this);
			}
		}
	}

	private void validateEntries(Iterable<? extends MetadataEntry> entries) {
		for (MetadataEntry entry : entries) {
			entry.validate(this);
		}
	}

	private void validateResolvableEntries(Iterable<? extends VersionedEntry> entries) {
		for (VersionedEntry entry : entries) {
			for (String profile : supportedProfiles) {
				entry.resolveVersion(this, profile);
			}
		}
	}
}

abstract class MetadataEntry {
	private final String alias;

	MetadataEntry(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public abstract void validate(BuildMetadataModel model);
}

abstract class VersionedEntry extends MetadataEntry {
	private static final String BASE_PROFILE_MARKER = "__base__";

	private final Map<String, VersionExpression> versions = new LinkedHashMap<String, VersionExpression>();
	private boolean strictVersion;

	VersionedEntry(String alias) {
		super(alias);
	}

	public Map<String, VersionExpression> getVersions() {
		return versions;
	}

	public boolean isStrictVersion() {
		return strictVersion;
	}

	public void setStrictVersion(boolean strictVersion) {
		this.strictVersion = strictVersion;
	}

	public void putVersionExpression(String profile, VersionExpression expression) {
		versions.put(profile, expression);
	}

	public void putLiteralVersion(String profile, String version) {
		putVersionExpression(profile, new RawVersionExpression(version));
	}

	public void putBaseVersionExpression(VersionExpression expression) {
		versions.put(BASE_PROFILE_MARKER, expression);
	}

	public boolean hasVersions() {
		return !versions.isEmpty();
	}

	public String resolveVersion(BuildMetadataModel model, String profile) {
		model.profileIndex(profile);
		return new VersionResolutionContext().resolveEntry(model, this, profile);
	}

	String resolveVersion(BuildMetadataModel model, String profile, VersionResolutionContext context) {
		model.profileIndex(profile);
		return context.resolveEntry(model, this, profile);
	}

	void prepare(BuildMetadataModel model) {
		normalizeBaseProfile(model);
		normalizeExpressions(model);
	}

	protected boolean allowEmptyVersions() {
		return false;
	}

	protected boolean allowAliasReferences() {
		return false;
	}

	protected boolean isReferenceable() {
		return false;
	}

	String resolveSelectedExpression(BuildMetadataModel model, String profile, VersionResolutionContext context) {
		int targetIndex = model.profileIndex(profile);
		VersionExpression selected = null;
		for (int index = 0; index <= targetIndex; index++) {
			String candidateProfile = model.getSupportedProfiles().get(index);
			if (versions.containsKey(candidateProfile)) {
				selected = versions.get(candidateProfile);
			}
		}
		if (selected == null) {
			return null;
		}
		return selected.resolve(model, profile, context);
	}

	@Override
	public void validate(BuildMetadataModel model) {
		if (!allowEmptyVersions() && versions.isEmpty()) {
			throw new IllegalArgumentException("Missing versions for alias '" + getAlias() + "'");
		}
		for (Map.Entry<String, VersionExpression> entry : versions.entrySet()) {
			model.profileIndex(entry.getKey());
			entry.getValue().validate(model, this);
		}
	}

	private void normalizeBaseProfile(BuildMetadataModel model) {
		if (!versions.containsKey(BASE_PROFILE_MARKER)) {
			return;
		}
		String baseProfile = model.getBaseProfile();
		if (versions.containsKey(baseProfile)) {
			throw new IllegalArgumentException(
				"Duplicate base profile version declaration for alias '" + getAlias() + "'"
			);
		}
		VersionExpression baseExpression = versions.remove(BASE_PROFILE_MARKER);
		Map<String, VersionExpression> normalized = new LinkedHashMap<String, VersionExpression>();
		normalized.put(baseProfile, baseExpression);
		normalized.putAll(versions);
		versions.clear();
		versions.putAll(normalized);
	}

	private void normalizeExpressions(BuildMetadataModel model) {
		for (Map.Entry<String, VersionExpression> entry : versions.entrySet()) {
			VersionExpression expression = entry.getValue();
			if (!(expression instanceof RawVersionExpression)) {
				continue;
			}
			entry.setValue(normalizeRawExpression(model, (RawVersionExpression) expression));
		}
	}

	private VersionExpression normalizeRawExpression(BuildMetadataModel model, RawVersionExpression expression) {
		if (allowAliasReferences() && model.getReferenceableEntry(expression.getRawValue()) != null) {
			return new AliasReferenceVersionExpression(expression.getRawValue());
		}
		return new LiteralVersionExpression(expression.getRawValue());
	}
}

final class ValueEntry extends VersionedEntry {
	ValueEntry(String alias) {
		super(alias);
	}
}

abstract class CoordinateEntry extends VersionedEntry {
	CoordinateEntry(String alias) {
		super(alias);
	}

	public String getGroupId() {
		return splitAlias()[0];
	}

	public String getArtifactId() {
		return splitAlias()[1];
	}

	public String getCoordinate() {
		return getGroupId() + ":" + getArtifactId();
	}

	@Override
	protected boolean allowAliasReferences() {
		return true;
	}

	@Override
	protected boolean isReferenceable() {
		return true;
	}

	private String[] splitAlias() {
		String alias = getAlias();
		int separatorIndex = alias.indexOf('_');
		if (separatorIndex <= 0 || separatorIndex >= alias.length() - 1) {
			throw new IllegalArgumentException(
				"Unsupported exact alias '" + alias + "'. Expected quoted key 'groupId_artifactId'."
			);
		}
		return new String[] { alias.substring(0, separatorIndex), alias.substring(separatorIndex + 1) };
	}

	@Override
	public void validate(BuildMetadataModel model) {
		super.validate(model);
		getCoordinate();
	}
}

final class LibraryEntry extends CoordinateEntry {
	private String scope;

	LibraryEntry(String alias) {
		super(alias);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public void validate(BuildMetadataModel model) {
		super.validate(model);
		if (scope != null && !scope.isEmpty()
			&& !"compile".equals(scope)
			&& !"provided".equals(scope)
			&& !"runtime".equals(scope)
			&& !"test".equals(scope)
			&& !"system".equals(scope)
			&& !"import".equals(scope)) {
			throw new IllegalArgumentException(
				"Unsupported Maven dependency scope '" + scope + "' for alias '" + getAlias() + "'"
			);
		}
	}
}

final class MavenPluginEntry extends CoordinateEntry {
	MavenPluginEntry(String alias) {
		super(alias);
	}

	@Override
	public void validate(BuildMetadataModel model) {
		super.validate(model);
		if (!getVersions().containsKey(model.getBaseProfile())) {
			throw new IllegalArgumentException(
				"Maven plugin alias '" + getAlias() + "' must declare a version for base profile '" + model.getBaseProfile() + "'"
			);
		}
	}
}

final class GradlePluginEntry extends VersionedEntry {
	GradlePluginEntry(String alias) {
		super(alias);
	}

	public String getPluginId() {
		return getAlias();
	}

	@Override
	protected boolean allowEmptyVersions() {
		return true;
	}

	@Override
	protected boolean allowAliasReferences() {
		return true;
	}

	@Override
	protected boolean isReferenceable() {
		return true;
	}
}

abstract class VersionExpression {
	public abstract String resolve(BuildMetadataModel model, String profile, VersionResolutionContext context);

	public abstract void validate(BuildMetadataModel model, VersionedEntry owner);
}

final class RawVersionExpression extends VersionExpression {
	private final String rawValue;

	RawVersionExpression(String rawValue) {
		this.rawValue = rawValue;
	}

	public String getRawValue() {
		return rawValue;
	}

	@Override
	public String resolve(BuildMetadataModel model, String profile, VersionResolutionContext context) {
		throw new IllegalStateException("Unresolved raw version expression '" + rawValue + "'");
	}

	@Override
	public void validate(BuildMetadataModel model, VersionedEntry owner) {
		throw new IllegalStateException(
			"Unresolved raw version expression '" + rawValue + "' for alias '" + owner.getAlias() + "'"
		);
	}
}

final class LiteralVersionExpression extends VersionExpression {
	private final String value;

	LiteralVersionExpression(String value) {
		this.value = value;
	}

	@Override
	public String resolve(BuildMetadataModel model, String profile, VersionResolutionContext context) {
		return value;
	}

	@Override
	public void validate(BuildMetadataModel model, VersionedEntry owner) {
	}
}

final class AliasReferenceVersionExpression extends VersionExpression {
	private final String alias;

	AliasReferenceVersionExpression(String alias) {
		this.alias = alias;
	}

	@Override
	public String resolve(BuildMetadataModel model, String profile, VersionResolutionContext context) {
		VersionedEntry target = model.getReferenceableEntry(alias);
		if (target == null) {
			return null;
		}
		return target.resolveVersion(model, profile, context);
	}

	@Override
	public void validate(BuildMetadataModel model, VersionedEntry owner) {
		if (model.getReferenceableEntry(alias) == null) {
			throw new IllegalArgumentException(
				"Unknown referenced alias '" + alias + "' for alias '" + owner.getAlias() + "'"
			);
		}
	}
}

final class VersionResolutionContext {
	private final Deque<String> stack = new ArrayDeque<String>();

	public String resolveEntry(BuildMetadataModel model, VersionedEntry entry, String profile) {
		String key = entry.getAlias() + "@" + profile;
		if (stack.contains(key)) {
			StringBuilder cycle = new StringBuilder();
			for (String item : stack) {
				if (cycle.length() > 0) {
					cycle.append(" -> ");
				}
				cycle.append(item);
			}
			if (cycle.length() > 0) {
				cycle.append(" -> ");
			}
			cycle.append(key);
			throw new IllegalArgumentException("Circular version reference detected: " + cycle.toString());
		}

		stack.addLast(key);
		try {
			return entry.resolveSelectedExpression(model, profile, this);
		} finally {
			stack.removeLast();
		}
	}
}
