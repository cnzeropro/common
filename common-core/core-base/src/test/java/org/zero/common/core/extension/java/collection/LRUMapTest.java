package org.zero.common.core.extension.java.collection;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.LRUMap;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/16
 */
class LRUMapTest {

	@Test
	void shouldEvictLeastRecentlyUsedEntry() {
		Map<String, String> map = LRUMap.create(true, 3);
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");

		map.get("1");
		map.put("4", "4");

		assertEquals(3, map.size());
		assertTrue(map.containsKey("1"));
		assertNull(map.get("2"));
		assertFalse(map.containsKey("2"));
		assertEquals("3", map.get("3"));
		assertEquals("4", map.get("4"));
	}
}
