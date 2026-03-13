package org.zero.common.core.support.http.header;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/4
 */

class CookieValueTest {
	@Test
	void test() {
		CookieValue cookieValue = CookieValue.parse("name=value; name2=value2; name3=value3");
		System.out.println(cookieValue);
	}
}
