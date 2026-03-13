package org.zero.common.core.support.http;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.io.ChunkedByteBuffer;
import org.zero.common.core.support.http.header.RangeValue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/6
 */
class FileRangeSplitterTest {
	@Test
	void test() throws IOException {
		RangeValue rangeValue = RangeValue.parse("bytes=0-1023,1024-8765432,8765433-9876543,9876544-");
		File file = Paths.get("C:\\App\\Portable\\FirPE\\FirPE-V2.0.3.exe").toFile();
		FileRangeSplitter splitter = new FileRangeSplitter(rangeValue, file);
		FileRangeSplitter.RangeChunkedBuffer chunkedBuffer = splitter.getChunkedBuffer();
		Map<RangeValue.Range, ChunkedByteBuffer> buffers = chunkedBuffer.getBuffers();
		OutputStream outputStream = Files.newOutputStream(Paths.get("C:\\App\\Portable\\FirPE\\FirPE-V2.0.3-bak.exe"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		for (ChunkedByteBuffer buffer : buffers.values()) {
			buffer.writeTo(outputStream);
		}
	}
}
