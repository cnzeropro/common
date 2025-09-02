package org.zero.common.core.support.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static org.zero.common.core.support.jwt.Constant.USER;


/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
@UtilityClass
public class JavaJwtUtil {
	public static String sign(Serializable user, String password, long expireTime) {
		return sign(user, Algorithm.HMAC512(password), expireTime);
	}

	public static String sign(Serializable user, Algorithm algorithm, long expireTime) {
		return new Builder()
			.user(user)
			.algorithm(algorithm)
			.expirationTime(Instant.now().plusMillis(expireTime))
			.build();
	}

	public static boolean verify(String token, String password) {
		return verify(token, Algorithm.HMAC512(password));
	}

	public static boolean verify(String token, Algorithm algorithm) {
		try {
			getJwt(token, algorithm);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Claim getUser(String token, String password) {
		return getUser(token, Algorithm.HMAC512(password));
	}

	public static Claim getUser(String token, Algorithm algorithm) {
		return getPayloads(token, algorithm).get(USER);
	}

	public static Claim getHeader(String token, String password, String claimName) {
		return getHeader(token, Algorithm.HMAC512(password), claimName);
	}

	public static Claim getHeader(String token, Algorithm algorithm, String claimName) {
		return getJwt(token, algorithm).getHeaderClaim(claimName);
	}

	public static Map<String, Claim> getPayloads(String token, String password) {
		return getPayloads(token, Algorithm.HMAC512(password));
	}

	public static Map<String, Claim> getPayloads(String token, Algorithm algorithm) {
		return getJwt(token, algorithm).getClaims();
	}

	public static DecodedJWT getJwt(String token, String password) {
		return getJwt(token, Algorithm.HMAC512(password));
	}

	public static DecodedJWT getJwt(String token, Algorithm algorithm) {
		return JWT.require(algorithm).build().verify(token);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Builder extends JwtBaseBuilder<Builder> {
		/**
		 * 签名算法。默认：无。建议设置
		 */
		Algorithm algorithm = Algorithm.none();

		@Override
		public String build() {
			JWTCreator.Builder builder = JWT.create()
				// 唯一标识符
				.withJWTId(id)
				// 签发者
				.withIssuer(issuer)
				// 接收方
				.withAudience(audiences.toArray(new String[0]))
				// 主题
				.withSubject(subject)
				// 头部
				.withHeader(headers)
				// 负载
				.withPayload(payloads)
				// 生效时间（之前不可用）
				.withNotBefore(effectiveTime)
				// 签发时间
				.withIssuedAt(Instant.now());
			if (Objects.nonNull(expirationTime)) {
				builder.withExpiresAt(expirationTime);
			}
			return builder.sign(algorithm);
		}
	}
}
