package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.Duration;
import java.util.LinkedHashMap;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/2
 */
class TtlMapTest {
	@Test
	void test() {
		ConcurrentTtlMap<String, Serializable> map = ConcurrentTtlMap.<String, Serializable>builder()
			.storage(new LinkedHashMap<>())
			.listener((key, value, reason) -> System.out.println("Purge [" + key + " -> " + value + "] from map, reason: " + reason + ", thread: " + Thread.currentThread().getName()))
			.build();
		map.put("1", "1", Duration.ZERO);
		map.put("2", "2", Duration.ofSeconds(3));
		map.put("3", "3");
		map.put("4", "4");
		map.put("5", "5");
		map.put("6", "6");
		map.put("7", "7");
		map.put("8", "8");
		map.put("9", "9");
		map.put("10", "10");
		System.out.println(map);
	}
}
