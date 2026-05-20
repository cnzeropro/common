package org.zero.common.core.support.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.JweHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecretKeyAlgorithm;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
class JjwtUtilTest {
	@Test
	void signShouldCreateVerifiableToken() {
		String password = "123456";
		String token = JjwtUtil.sign("zero", password, Constant.ACCESS_EXPIRE_TIME);

		assertTrue(JjwtUtil.verify(token, password));
	}

	@Test
	void getUserShouldReadUserClaim() {
		String password = "123456";
		String token = JjwtUtil.sign("zero", password, Constant.ACCESS_EXPIRE_TIME);

		Serializable user = JjwtUtil.getUser(token, password);

		assertEquals("zero", user);
	}

	@Test
	void buildShouldCreateEncryptedToken() {
		SecretKeyAlgorithm a256kw = Jwts.KEY.A256KW;
		SecretKey secretKey = a256kw.key().build();
		String token = JjwtUtil.<SecretKey>builder()
				.user("zero")
			.expirationTime(Constant.ACCESS_EXPIRE_TIME)
			.key(secretKey)
			.jwtType(JjwtUtil.JwtType.JWE)
			.keyAlgorithm(a256kw)
			.aeadAlgorithm(Jwts.ENC.A256GCM)
			.build();

		Jwe<Claims> claimsJwe = Jwts.parser()
			.decryptWith(secretKey)
			.build()
			.parseEncryptedClaims(token);

		JweHeader header = claimsJwe.getHeader();
		Claims payload = claimsJwe.getPayload();

		assertEquals("A256KW", header.getAlgorithm());
		assertEquals("zero", payload.get(Constant.USER, String.class));
	}
}
