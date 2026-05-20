package org.zero.common.core.support.jwt;

import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/8/15
 */
class JavaJwtUtilTest {

	@Test
	void signShouldCreateVerifiableToken() {
		String password = "123456";
		String token = JavaJwtUtil.sign("zero", password, Constant.ACCESS_EXPIRE_TIME);

		assertTrue(JavaJwtUtil.verify(token, password));
	}

	@Test
	void getUserShouldReadUserClaim() {
		String password = "123456";
		String token = JavaJwtUtil.sign("zero", password, Constant.ACCESS_EXPIRE_TIME);

		Claim user = JavaJwtUtil.getUser(token, password);

		assertEquals("zero", user.asString());
	}
}
