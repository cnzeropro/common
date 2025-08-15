package org.zero.common.core.support.jwt;

import org.zero.common.core.extension.java.lang.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
public abstract class JwtBaseBuilder<B extends JwtBaseBuilder<B>> implements Builder<String, JwtBaseBuilder<B>> {
	public static final String DEFAULT_ISSUER = "system";
	public static final String DEFAULT_SUBJECT = "auth";

	/**
	 * 唯一标识
	 */
	public String id = UUID.randomUUID().toString();
	/**
	 * 签发者
	 */
	protected String issuer = DEFAULT_ISSUER;
	/***
	 * 接收者
	 */
	protected Collection<String> audiences = new ArrayList<>();
	/**
	 * 主题
	 */
	protected String subject = DEFAULT_SUBJECT;
	/**
	 * 头部
	 */
	protected Map<String, Object> headers = new LinkedHashMap<>();
	/**
	 * 载荷
	 */
	protected Map<String, Object> payloads = new LinkedHashMap<>();
	/**
	 * 生效时间（Not Before）
	 */
	protected Instant effectiveTime = Instant.now();
	/**
	 * 过期时间
	 */
	protected Instant expirationTime;

	public B id(String id) {
		this.id = id;
		return self();
	}

	public B issuer(String issuer) {
		this.issuer = issuer;
		return self();
	}

	public B audiences(Collection<String> audiences) {
		this.audiences.addAll(audiences);
		return self();
	}

	public B audience(String audience) {
		this.audiences.add(audience);
		return self();
	}

	public B subject(String subject) {
		this.subject = subject;
		return self();
	}

	public B headers(Map<String, Object> headers) {
		this.headers.putAll(headers);
		return self();
	}

	public B header(String key, Object value) {
		this.headers.put(key, value);
		return self();
	}

	public B payloads(Map<String, Object> payloads) {
		this.payloads.putAll(payloads);
		return self();
	}

	public B payload(String key, Object value) {
		this.payloads.put(key, value);
		return self();
	}

	public B effectiveTime(Instant effectiveTime) {
		this.effectiveTime = effectiveTime;
		return self();
	}

	public B expirationTime(Instant expirationTime) {
		this.expirationTime = expirationTime;
		return self();
	}

	@SuppressWarnings("unchecked")
	@Override
	public B self() {
		return (B) this;
	}
}
