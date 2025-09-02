package org.zero.common.core.support.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.JweHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecretKeyAlgorithm;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
class JjwtUtilTest {
	@Test
	void sign() {
		String password = "123456";
		String token = JjwtUtil.sign(1423543645645L, password, Constant.ACCESS_EXPIRE_TIME);
		System.out.println(token);
	}

	@Test
	void verify() {
		String password = "123456";
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzY2NjMTU5Zi0xNWI1LTQ5YTQtOTBjYi0xMjY4ZjhkMzExNTYiLCJpc3MiOiJzeXN0ZW0iLCJzdWIiOiJhdXRoIiwidXNlciI6MTQyMzU0MzY0NTY0NSwibmJmIjoxNzU2Nzc1OTE1LCJpYXQiOjE3NTY3NzU5MTUsImV4cCI6MTc1Njc3NzcxNX0.hKpmxvsgRinCMARiWdDCxrHlES0E0MlYnWZxWCWroHs";
		boolean verify = JjwtUtil.verify(token, password);
		System.out.println(verify);
	}

	@Test
	void getUser() {
		String password = "123456";
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzY2NjMTU5Zi0xNWI1LTQ5YTQtOTBjYi0xMjY4ZjhkMzExNTYiLCJpc3MiOiJzeXN0ZW0iLCJzdWIiOiJhdXRoIiwidXNlciI6MTQyMzU0MzY0NTY0NSwibmJmIjoxNzU2Nzc1OTE1LCJpYXQiOjE3NTY3NzU5MTUsImV4cCI6MTc1Njc3NzcxNX0.hKpmxvsgRinCMARiWdDCxrHlES0E0MlYnWZxWCWroHs";
		Serializable user = JjwtUtil.getUser(token, password);
		System.out.println(user);
	}

	@Test
	void build() {
		SecretKeyAlgorithm a256kw = Jwts.KEY.A256KW;
		SecretKey secretKey = a256kw.key().build();
		String token = JjwtUtil.<SecretKey>builder()
			.user(15677568754353645L)
			.expirationTime(Constant.ACCESS_EXPIRE_TIME)
			.key(secretKey)
			.jwtType(JjwtUtil.JwtType.JWE)
			.keyAlgorithm(a256kw)
			.aeadAlgorithm(Jwts.ENC.A256GCM)
			.build();
		System.out.println(token);

		Jwe<Claims> claimsJwe = Jwts.parser()
			.decryptWith(secretKey)
			.build()
			.parseEncryptedClaims(token);

		JweHeader header = claimsJwe.getHeader();
		System.out.println(header);

		Claims payload = claimsJwe.getPayload();
		System.out.println(payload);
	}
}