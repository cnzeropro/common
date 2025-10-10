package org.zero.common.core.support.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.KeyAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static org.zero.common.core.support.jwt.Constant.USER;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
public class JjwtUtil {
	public static String sign(Serializable user, String password, long expireTime) {
		return sign(user, getKey(password), expireTime);
	}

	public static String sign(Serializable user, Key key, long expireTime) {
		return new Builder<>().user(user).key(key).expirationTime(Instant.now().plusMillis(expireTime)).build();
	}

	public static boolean verify(String token, String password) {
		return verify(token, getKey(password));
	}

	public static boolean verify(String token, SecretKey secretKey) {
		try {
			getJws(token, secretKey);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Serializable getUser(String token, String password) {
		return getPayloads(token, password).get(USER, Serializable.class);
	}

	public static Serializable getUser(String token, SecretKey secretKey) {
		return getPayloads(token, secretKey).get(USER, Serializable.class);
	}

	public static JwsHeader getHeaders(String token, String password) {
		return getJws(token, password).getHeader();
	}

	public static JwsHeader getHeaders(String token, SecretKey secretKey) {
		return getJws(token, secretKey).getHeader();
	}

	public static Claims getPayloads(String token, String password) {
		return getJws(token, password).getPayload();
	}

	public static Claims getPayloads(String token, SecretKey secretKey) {
		return getJws(token, secretKey).getPayload();
	}

	public static Jws<Claims> getJws(String token, String password) {
		return getJws(token, getKey(password));
	}

	public static Jws<Claims> getJws(String token, SecretKey secretKey) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
	}

	private static SecretKey getKey(String password) {
		byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			bytes = Arrays.copyOf(bytes, 32);
		}
		return Keys.hmacShaKeyFor(bytes);
	}

	public static <K extends Key> Builder<K> builder() {
		return new Builder<>();
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Builder<K extends Key> extends JwtBaseBuilder<Builder<K>> {
		K key;
		JwtType jwtType = JwtType.JWS;
		/**
		 * 密钥算法
		 * <p>
		 * 仅适用于 {@linkplain  JwtType#JWE JWE}
		 *
		 * @see Jwts.KEY
		 */
		KeyAlgorithm<? super K, ?> keyAlgorithm;
		/**
		 * AEAD 算法
		 * <p>
		 * 仅适用于 {@linkplain  JwtType#JWE JWE}
		 *
		 * @see Jwts.ENC
		 */
		AeadAlgorithm aeadAlgorithm;

		@Override
		public String build() {
			JwtBuilder jwtBuilder = Jwts.builder()
				// 唯一标识符
				.id(id)
				// 签发者
				.issuer(issuer)
				// 接收方
				.audience().add(audiences).and()
				// 主题
				.subject(subject)
				// 头部
				.header().add(headers).and()
				// 负载（content 和 claim 方法互斥）
				.claims(payloads)
				// 生效时间（之前不可用）
				.notBefore(Date.from(effectiveTime))
				// 签发时间
				.issuedAt(new Date());
			if (Objects.nonNull(expirationTime)) {
				// 过期时间
				jwtBuilder.expiration(Date.from(expirationTime));
			}
			if (jwtType == JwtType.JWS) {
				if (Objects.nonNull(key)) {
					jwtBuilder.signWith(key);
				}
			} else if (jwtType == JwtType.JWE) {
				if (Objects.nonNull(key)) {
					jwtBuilder.encryptWith(key, keyAlgorithm, aeadAlgorithm);
				}
			}
			return jwtBuilder.compact();
		}
	}

	public enum JwtType {
		JWS,
		JWE
	}
}
