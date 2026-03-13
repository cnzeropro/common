package org.zero.common.core.support.http.header;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/4
 */
class ForwardedValueTest {
	@Test
	void test() {
		ForwardedValue forwardedValue = ForwardedValue.parse("for=192.168.1.1:4567; proto=https;by=\"[2001:db8:cafe::17]:4711\",for=192.0.2.60; proto=http; by=\"[2001:db8:cafe::17]\"");
		System.out.println(forwardedValue);
	}
}
