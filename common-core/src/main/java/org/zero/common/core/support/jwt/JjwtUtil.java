package org.zero.common.core.support.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

import static org.zero.common.core.support.jwt.Constant.USER_ID;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
public class JjwtUtil {
	public static String sign(Serializable userId, String password, long expireTime) {
		return sign(userId, Keys.password(password.toCharArray()), expireTime);
	}

	public static String sign(Serializable userId, Key key, long expireTime) {
		return new Builder().userId(userId).key(key).expirationTime(Instant.now().plusMillis(expireTime)).build();
	}

	public static boolean verify(String jws, String password) {
		return verify(jws, Keys.password(password.toCharArray()));
	}

	public static boolean verify(String jws, SecretKey key) {
		try {
			getJws(jws, key);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getUserId(String jws, String password) {
		return getPayload(jws, password).get(USER_ID, String.class);
	}

	public static String getUserId(String jws, SecretKey key) {
		return getPayload(jws, key).get(USER_ID, String.class);
	}

	public static JwsHeader getHeader(String jws, String password) {
		return getJws(jws, password).getHeader();
	}

	public static JwsHeader getHeader(String jws, SecretKey key) {
		return getJws(jws, key).getHeader();
	}

	public static Claims getPayload(String jws, String password) {
		return getJws(jws, password).getPayload();
	}

	public static Claims getPayload(String jws, SecretKey key) {
		return getJws(jws, key).getPayload();
	}

	public static Jws<Claims> getJws(String jws, String password) {
		return getJws(jws, Keys.password(password.toCharArray()));
	}

	public static Jws<Claims> getJws(String jws, SecretKey key) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(jws);
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Builder extends JwtBaseBuilder<Builder> {
		Key key;

		public Builder expirationTime(Date expirationTime) {
			return this.expirationTime(expirationTime.toInstant());
		}

		public Builder expirationTime(ZonedDateTime expirationTime) {
			return this.expirationTime(expirationTime.toInstant());
		}

		public Builder expirationTime(LocalDateTime expirationTime) {
			return this.expirationTime(expirationTime.atZone(ZoneId.systemDefault()));
		}

		public Builder password(String password) {
			return this.key(Keys.password(password.toCharArray()));
		}

		public Builder userId(Serializable userId) {
			this.payload(USER_ID, userId);
			return this;
		}

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
			if (Objects.nonNull(key)) {
				jwtBuilder.signWith(key);
			}
			return jwtBuilder.compact();
		}
	}
}
