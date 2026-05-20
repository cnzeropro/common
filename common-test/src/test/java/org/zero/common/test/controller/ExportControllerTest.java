package org.zero.common.test.controller;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
class ExportControllerTest {
	private final ExportController controller = new ExportController();

	@Test
	void e1ShouldReturnConfiguredFilePath() {
		String path = controller.e1();

		assertTrue(path.endsWith("新建 文本文档.txt"));
	}

	@Test
	void e2ShouldReturnRowsForExcelExport() {
		List<Map<String, Object>> rows = controller.e2();

		assertEquals(3, rows.size());
		assertEquals("hello", rows.get(0).get("标题1"));
		assertFalse(rows.get(1).isEmpty());
	}

	@Test
	void e3ShouldReturnArchiveSources() {
		String[] sources = controller.e3();

		assertEquals(2, sources.length);
		assertTrue(sources[0].endsWith("新建 文本文档.txt"));
	}
}
