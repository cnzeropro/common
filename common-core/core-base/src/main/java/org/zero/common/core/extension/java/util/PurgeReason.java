package org.zero.common.core.extension.java.util;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
public enum PurgeReason {
	/**
	 * 驱逐
	 */
	EVICTION,
	/**
	 * 过期
	 */
	EXPIRY,
	/**
	 * 回收
	 */
	RECLAMATION,
	/**
	 * 删除
	 */
	EXPLICIT,
	;
}
