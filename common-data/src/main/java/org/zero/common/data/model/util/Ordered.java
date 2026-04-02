package org.zero.common.data.model.util;

/**
 * 排序接口，用于定义实现类的执行/加载顺序。
 *
 * <p>值越大优先级越高（越先执行），值越小优先级越低（越后执行）。
 * 可用于集合排序、拦截器链、插件加载等需要顺序控制的场景。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public interface Ordered {

	/** 默认排序值 */
	int DEFAULT_ORDER = 0;

	/** 最高优先级 */
	int HIGHEST_ORDER = Integer.MAX_VALUE;

	/** 最低优先级 */
	int LOWEST_ORDER = Integer.MIN_VALUE;

	/**
	 * 获取排序值。
	 *
	 * @return 排序值，默认为 {@link #DEFAULT_ORDER}
	 */
	default int order() {
		return DEFAULT_ORDER;
	}
}
