package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/16
 */
class ByteArrayOutputStreamWriterTest {
	@Test
	void shouldEncodeMultiByteCharactersAcrossStringAndCharApis() {
		ByteArrayOutputStreamWriter writer = new ByteArrayOutputStreamWriter(StandardCharsets.UTF_8);

		writer.write("世界");
		writer.append('你');
		writer.print('好');

		assertArrayEquals("世界你好".getBytes(StandardCharsets.UTF_8), writer.toByteArray());
		assertEquals("世界你好", writer.toString());
	}

	@Test
	void shouldUseDefaultNullValueForStringApis() {
		ByteArrayOutputStreamWriter writer = new ByteArrayOutputStreamWriter();

		writer.write((String) null);
		writer.print((String) null);

		assertEquals("nullnull", writer.toString());
	}

	@Test
	void shouldUseCustomNullValueForStringApis() {
		ByteArrayOutputStreamWriter writer = new ByteArrayOutputStreamWriter(StandardCharsets.UTF_8, "<null>");

		writer.write((String) null);
		writer.print((String) null);

		assertEquals("<null><null>", writer.toString());
	}

	@Test
	void shouldUseNullDefaultAsSourceForSubstringWrite() {
		ByteArrayOutputStreamWriter writer = new ByteArrayOutputStreamWriter(StandardCharsets.UTF_8, "<null>");

		writer.write((String) null, 1, 4);
		writer.append(null, 1, 5);

		assertEquals("nullnull", writer.toString());
	}
}
