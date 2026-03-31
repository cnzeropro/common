package org.zero.common.data.format;

import cn.hutool.core.text.StrFormatter;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/27
 */
class HutoolMapMessageFormatterTest {

	@Test
	void hutoolMapMessageFormatterShouldFormatMapTemplate() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", "zero");
		String message = HutoolMapMessageFormatter.INSTANCE.format(new StringBuilder("user {name}"), Locale.CHINA, map);

		assertEquals("user zero", message);
	}

	@Test
	void hutoolMapMessageFormatterShouldUseHutoolBehaviorWhenIgnoreNullIsTrue() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", "zero");
		map.put("value", null);
		CharSequence template = new StringBuffer("user {name} value {value}");

		String message = HutoolMapMessageFormatter.INSTANCE.format(template, map, true);

		assertEquals(StrFormatter.format(template, map, true), message);
	}

	@Test
	void hutoolMapMessageFormatterShouldUseHutoolBehaviorWhenIgnoreNullIsFalse() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", "zero");
		map.put("value", null);
		CharSequence template = new StringBuffer("user {name} value {value}");
		String message = HutoolMapMessageFormatter.INSTANCE.format(template, map, false);

		assertEquals(StrFormatter.format(template, map, false), message);
	}

	@Test
	void hutoolMapMessageFormatterShouldIgnoreLocaleForMapFormatting() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name", "zero");
		CharSequence template = new StringBuilder("user {name}");
		String message = HutoolMapMessageFormatter.INSTANCE.format(template, Locale.GERMANY, map);

		assertEquals(StrFormatter.format(template, map, true), message);
	}
}
