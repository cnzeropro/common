package org.zero.common.data.model.parameter;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.CacheControlInstruction;
import org.zero.common.data.enumeration.FileType;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/10
 */
class DownloadPOTest {

	@Test
	void noArgsConstructorShouldUseNoCacheByDefault() {
		DownloadPO downloadPO = new DownloadPO();

		assertAll(
			() -> assertNull(downloadPO.getFileType()),
			() -> assertNull(downloadPO.getFileName()),
			() -> assertSame(CacheControlInstruction.NO_CACHE, downloadPO.getCacheStrategy())
		);
	}

	@Test
	void twoArgsConstructorShouldKeepFileFieldsAndDefaultNoCache() {
		DownloadPO downloadPO = new DownloadPO(FileType.PDF, "report.pdf");

		assertAll(
			() -> assertSame(FileType.PDF, downloadPO.getFileType()),
			() -> assertEquals("report.pdf", downloadPO.getFileName()),
			() -> assertSame(CacheControlInstruction.NO_CACHE, downloadPO.getCacheStrategy())
		);
	}

	@Test
	void allArgsConstructorShouldAllowExplicitCacheStrategy() {
		DownloadPO downloadPO = new DownloadPO(FileType.PNG, "avatar.png", CacheControlInstruction.NO_STORE);

		assertAll(
			() -> assertSame(FileType.PNG, downloadPO.getFileType()),
			() -> assertEquals("avatar.png", downloadPO.getFileName()),
			() -> assertSame(CacheControlInstruction.NO_STORE, downloadPO.getCacheStrategy())
		);
	}
}
