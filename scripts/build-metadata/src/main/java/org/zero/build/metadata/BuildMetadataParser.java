package org.zero.build.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Build metadata parser - 解析 build-metadata.toml 的精简 TOML 子集。
 */
public final class BuildMetadataParser {
	private BuildMetadataParser() {
	}

	public static BuildMetadataModel parse(File file) {
		BuildMetadataModel model = new BuildMetadataModel();
		String currentSection = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
			String rawLine;
			while ((rawLine = reader.readLine()) != null) {
				String line = stripInlineComment(rawLine).trim();
				if (line.isEmpty()) {
					continue;
				}

				if (line.startsWith("[") && line.endsWith("]")) {
					currentSection = line.substring(1, line.length() - 1).trim();
					continue;
				}

				int separatorIndex = findTopLevelSeparator(line, '=');
				if (separatorIndex < 0 || currentSection == null) {
					continue;
				}

				String key = line.substring(0, separatorIndex).trim();
				String value = line.substring(separatorIndex + 1).trim();
				parseProperty(model, currentSection, key, value);
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to parse build metadata: " + file, ex);
		}

		model.validate();
		return model;
	}

	private static void parseProperty(BuildMetadataModel model, String section, String rawKey, String rawValue) {
		if ("metadata".equals(section)) {
			parseMetadata(model, rawKey, rawValue);
			return;
		}
		if ("properties.maven".equals(section)) {
			parseMavenProperty(model, rawKey, rawValue);
			return;
		}
		if ("values".equals(section)) {
			parseValueEntry(model, rawKey, rawValue);
			return;
		}
		if ("libraries".equals(section)) {
			parseLibraryEntry(model, rawKey, rawValue);
			return;
		}
		if ("plugins.maven".equals(section)) {
			parseMavenPluginEntry(model, rawKey, rawValue);
			return;
		}
		if ("plugins.gradle".equals(section)) {
			parseGradlePluginEntry(model, rawKey, rawValue);
			return;
		}

		throw new IllegalArgumentException("Unsupported build metadata section '" + section + "'");
	}

	private static void parseMetadata(BuildMetadataModel model, String rawKey, String rawValue) {
		String key = parseKey(rawKey);
		if ("revision".equals(key)) {
			model.setRevision(unquote(rawValue));
			return;
		}
		if ("java".equals(key)) {
			model.setJavaVersion(unquote(rawValue));
			return;
		}
		if ("profiles".equals(key)) {
			model.setSupportedProfiles(parseArray(rawValue));
			return;
		}
		throw new IllegalArgumentException("Unsupported metadata key '" + key + "'");
	}

	private static void parseMavenProperty(BuildMetadataModel model, String rawKey, String rawValue) {
		String trimmed = rawValue.trim();
		if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
			throw new IllegalArgumentException("Unsupported Maven property value '" + rawValue + "'");
		}
		model.putMavenProperty(parseKey(rawKey), unquote(rawValue));
	}

	private static void parseValueEntry(BuildMetadataModel model, String rawKey, String rawValue) {
		ValueEntry entry = model.getOrCreateValue(parseKey(rawKey));
		Map<String, String> table = parseInlineTable(rawValue);
		for (Map.Entry<String, String> item : table.entrySet()) {
			entry.putLiteralVersion(item.getKey(), item.getValue());
		}
	}

	private static void parseLibraryEntry(BuildMetadataModel model, String rawKey, String rawValue) {
		LibraryEntry entry = model.getOrCreateLibrary(parseKey(rawKey));
		parseReferenceableEntry(entry, rawValue, true);
	}

	private static void parseMavenPluginEntry(BuildMetadataModel model, String rawKey, String rawValue) {
		MavenPluginEntry entry = model.getOrCreateMavenPlugin(parseKey(rawKey));
		parseReferenceableEntry(entry, rawValue, false);
	}

	private static void parseGradlePluginEntry(BuildMetadataModel model, String rawKey, String rawValue) {
		GradlePluginEntry entry = model.getOrCreateGradlePlugin(parseKey(rawKey));
		parseReferenceableEntry(entry, rawValue, false);
	}

	private static void parseReferenceableEntry(VersionedEntry entry, String rawValue, boolean allowScope) {
		String trimmed = rawValue.trim();
		if (!trimmed.startsWith("{")) {
			entry.putBaseVersionExpression(new RawVersionExpression(unquote(trimmed)));
			return;
		}

		Map<String, String> table = parseInlineTable(rawValue);
		for (Map.Entry<String, String> item : table.entrySet()) {
			if ("strict".equals(item.getKey())) {
				entry.setStrictVersion(parseBoolean(item.getValue(), entry.getAlias(), item.getKey()));
				continue;
			}
			if ("scope".equals(item.getKey())) {
				if (allowScope && entry instanceof LibraryEntry) {
					((LibraryEntry) entry).setScope(item.getValue());
					continue;
				}
				throw new IllegalArgumentException("Unsupported key 'scope' for alias '" + entry.getAlias() + "'");
			}
			entry.putVersionExpression(item.getKey(), new RawVersionExpression(item.getValue()));
		}
	}

	private static boolean parseBoolean(String rawValue, String alias, String key) {
		if ("true".equalsIgnoreCase(rawValue)) {
			return true;
		}
		if ("false".equalsIgnoreCase(rawValue)) {
			return false;
		}
		throw new IllegalArgumentException(
			"Unsupported boolean value '" + rawValue + "' for key '" + key + "' on alias '" + alias + "'"
		);
	}

	private static String parseKey(String rawKey) {
		return unquote(rawKey.trim());
	}

	private static List<String> parseArray(String rawValue) {
		String trimmed = rawValue.trim();
		if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
			throw new IllegalArgumentException("Unsupported array value '" + rawValue + "'");
		}

		String body = trimmed.substring(1, trimmed.length() - 1).trim();
		List<String> values = new ArrayList<String>();
		if (body.isEmpty()) {
			return values;
		}

		for (String token : splitTopLevel(body, ',')) {
			values.add(unquote(token.trim()));
		}
		return values;
	}

	private static Map<String, String> parseInlineTable(String rawValue) {
		String trimmed = rawValue.trim();
		if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
			throw new IllegalArgumentException("Unsupported inline table value '" + rawValue + "'");
		}

		String body = trimmed.substring(1, trimmed.length() - 1).trim();
		Map<String, String> values = new LinkedHashMap<String, String>();
		if (body.isEmpty()) {
			return values;
		}

		for (String token : splitTopLevel(body, ',')) {
			int separatorIndex = findTopLevelSeparator(token, '=');
			if (separatorIndex < 0) {
				throw new IllegalArgumentException("Unsupported inline table token '" + token + "'");
			}
			String key = parseKey(token.substring(0, separatorIndex));
			String value = unquote(token.substring(separatorIndex + 1).trim());
			values.put(key, value);
		}
		return values;
	}

	private static List<String> splitTopLevel(String text, char delimiter) {
		List<String> tokens = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		boolean inQuote = false;
		int braceDepth = 0;
		int bracketDepth = 0;
		for (int index = 0; index < text.length(); index++) {
			char ch = text.charAt(index);
			if (ch == '"' && !isEscaped(text, index)) {
				inQuote = !inQuote;
				current.append(ch);
				continue;
			}
			if (!inQuote) {
				if (ch == '{') {
					braceDepth++;
				} else if (ch == '}') {
					braceDepth--;
				} else if (ch == '[') {
					bracketDepth++;
				} else if (ch == ']') {
					bracketDepth--;
				} else if (ch == delimiter && braceDepth == 0 && bracketDepth == 0) {
					tokens.add(current.toString().trim());
					current.setLength(0);
					continue;
				}
			}
			current.append(ch);
		}
		if (current.length() > 0) {
			tokens.add(current.toString().trim());
		}
		return tokens;
	}

	private static int findTopLevelSeparator(String text, char separator) {
		boolean inQuote = false;
		int braceDepth = 0;
		int bracketDepth = 0;
		for (int index = 0; index < text.length(); index++) {
			char ch = text.charAt(index);
			if (ch == '"' && !isEscaped(text, index)) {
				inQuote = !inQuote;
				continue;
			}
			if (inQuote) {
				continue;
			}
			if (ch == '{') {
				braceDepth++;
				continue;
			}
			if (ch == '}') {
				braceDepth--;
				continue;
			}
			if (ch == '[') {
				bracketDepth++;
				continue;
			}
			if (ch == ']') {
				bracketDepth--;
				continue;
			}
			if (ch == separator && braceDepth == 0 && bracketDepth == 0) {
				return index;
			}
		}
		return -1;
	}

	private static String stripInlineComment(String rawLine) {
		boolean inQuote = false;
		for (int index = 0; index < rawLine.length(); index++) {
			char ch = rawLine.charAt(index);
			if (ch == '"' && !isEscaped(rawLine, index)) {
				inQuote = !inQuote;
				continue;
			}
			if (!inQuote && ch == '#') {
				return rawLine.substring(0, index);
			}
		}
		return rawLine;
	}

	private static boolean isEscaped(String text, int index) {
		int slashCount = 0;
		for (int cursor = index - 1; cursor >= 0 && text.charAt(cursor) == '\\'; cursor--) {
			slashCount++;
		}
		return slashCount % 2 == 1;
	}

	private static String unquote(String value) {
		String trimmed = value.trim();
		if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
			return trimmed.substring(1, trimmed.length() - 1);
		}
		return trimmed;
	}
}
