package org.zero.common.core.support.jwt;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
class JjwtUtilTest {

	@Test
	void sign() {
		String sign = JjwtUtil.sign("1", "123456", Constant.ACCESS_EXPIRE_TIME);
		System.out.println(sign);
	}
}