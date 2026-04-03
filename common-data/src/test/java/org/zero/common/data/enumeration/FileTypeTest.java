package org.zero.common.data.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class FileTypeTest {

	@Test
	void shouldExposeExtensionAndMimeTypeConsistently() {
		assertAll(
			() -> assertEquals("png", FileType.PNG.getExtName()),
			() -> assertEquals("image/png", FileType.PNG.getMimeType()),
			() -> assertEquals(FileType.PNG.getMimeType(), FileType.PNG.getMediaType()),
			() -> assertEquals("pdf", FileType.PDF.getExtName()),
			() -> assertEquals("application/pdf", FileType.PDF.getContentType())
		);
	}
}
