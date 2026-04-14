package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 验证 Java 9 多版本测试任务优先读取 java9 资源。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
class ResourceMultiReleaseTest {

	@Test
	void shouldResolveJava9MainAndTestResources() throws IOException {
		Assertions.assertEquals("java9-main-resource", readResource("multi-release/main-resource.txt"));
		Assertions.assertEquals("java9-test-resource", readResource("multi-release/test-resource.txt"));
	}

	private String readResource(String resourcePath) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
		Assertions.assertNotNull(inputStream, "Missing resource: " + resourcePath);
		try (InputStream resourceStream = inputStream) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int length;
			while ((length = resourceStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
			return new String(outputStream.toByteArray(), StandardCharsets.UTF_8).trim();
		}
	}
}
