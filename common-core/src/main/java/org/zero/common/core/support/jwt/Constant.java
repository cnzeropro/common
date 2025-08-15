package org.zero.common.core.support.jwt;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/31
 */
public interface Constant {
	long ACCESS_EXPIRE_TIME = 1000L * 60 * 30;
	long REFRESH_EXPIRE_TIME = 2 * ACCESS_EXPIRE_TIME;
	String CACHE_PREFIX = "system:user:token";
	String USER_ID = "userId";
}
