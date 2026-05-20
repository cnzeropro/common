package org.zero.common.core.support.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.zero.common.core.extension.java.io.ChunkedByteBuffer;
import org.zero.common.core.support.http.header.RangeValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/6
 */
class FileRangeSplitterTest {
	@TempDir
	Path tempDir;

	@Test
	void getChunkedBufferShouldReadRequestedRanges() throws IOException {
		Path file = tempDir.resolve("range.bin");
		Files.write(file, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		RangeValue rangeValue = RangeValue.parse("bytes=0-2,3-5,6-");
		FileRangeSplitter splitter = new FileRangeSplitter(rangeValue, file.toFile(), 4);

		FileRangeSplitter.RangeChunkedBuffer chunkedBuffer = splitter.getChunkedBuffer();
		Map<RangeValue.Range, ChunkedByteBuffer> buffers = chunkedBuffer.getBuffers();
		List<ChunkedByteBuffer> values = new ArrayList<>(buffers.values());

		assertArrayEquals(new byte[]{0, 1, 2}, values.get(0).get(3));
		assertArrayEquals(new byte[]{3, 4, 5}, values.get(1).get(3));
		assertArrayEquals(new byte[]{6, 7, 8, 9}, values.get(2).get(4));
	}
}
