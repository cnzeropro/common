package org.zero.build.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Metadata file support - 统一封装元数据生成器共享的文件读写与渲染辅助逻辑。
 */
final class MetadataFileSupport {
	private MetadataFileSupport() {
	}

	static String join(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < lines.size(); index++) {
			if (index > 0) {
				builder.append("\n - ");
			}
			builder.append(lines.get(index));
		}
		return builder.toString();
	}

	static String joinCommaSeparated(List<String> values) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < values.size(); index++) {
			if (index > 0) {
				builder.append(',');
			}
			builder.append(values.get(index));
		}
		return builder.toString();
	}

	static String joinLines(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < lines.size(); index++) {
			if (index > 0) {
				builder.append('\n');
			}
			builder.append(lines.get(index));
		}
		builder.append('\n');
		return builder.toString();
	}

	static String trimTrailingNewline(String text) {
		if (text.endsWith("\n")) {
			return text.substring(0, text.length() - 1);
		}
		return text;
	}

	static String readUtf8(File file) {
		try {
			return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read file: " + file, ex);
		}
	}

	static List<String> readLines(File file) {
		try {
			return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read file: " + file, ex);
		}
	}

	static void writeUtf8(File file, String content) {
		try {
			Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to write file: " + file, ex);
		}
	}

	static void writeIfChanged(File file, String content, BuildMetadataLogger logger) {
		String normalizedContent = content.replace("\r\n", "\n");
		if (file.exists()) {
			String current = readUtf8(file).replace("\r\n", "\n");
			if (current.equals(normalizedContent)) {
				logger.info("skip", file);
				return;
			}
		}
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		writeUtf8(file, normalizedContent);
		logger.info("write", file);
	}

	static void assertMatches(File file, String expected, List<String> mismatches, BuildMetadataLogger logger) {
		logger.info("check", file);
		if (!file.exists()) {
			mismatches.add("Missing file: " + file);
			logger.info("mismatch", file);
			return;
		}
		String actual = readUtf8(file);
		if (!normalize(actual).equals(normalize(expected))) {
			mismatches.add("Out-of-sync file: " + file);
			logger.info("mismatch", file);
		}
	}

	static void deleteIfExists(File file, BuildMetadataLogger logger) {
		boolean existed = file.exists();
		if (existed && !file.delete()) {
			throw new IllegalStateException("Failed to delete file: " + file);
		}
		if (existed) {
			logger.info("delete", file);
		}
	}

	static String replaceFirst(String text, Pattern pattern, String replacement) {
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find()) {
			return text;
		}
		return text.substring(0, matcher.start(2)) + replacement + text.substring(matcher.end(2));
	}

	static String renderMavenRepositoryComment(String indent, String coordinate) {
		String[] gav = coordinate.split(":", 2);
		return indent + "<!-- https://mvnrepository.com/artifact/" + gav[0] + "/" + gav[1] + " -->\n";
	}

	static String renderTomlString(String value) {
		return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	static String renderJsonString(String value) {
		return "\"" + value
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\b", "\\b")
			.replace("\f", "\\f")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t") + "\"";
	}

	static String catalogAlias(String alias) {
		return alias.replace('.', '-');
	}

	static String normalize(String text) {
		return text.replace("\r\n", "\n").trim();
	}
}
