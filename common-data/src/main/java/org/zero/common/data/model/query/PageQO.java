package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 前端分页列表查询参数对象，两种使用方式：
 * <ul>
 *     <li>直接使用：直接用于承接前端传入参数</li>
 *     <li>继承使用：查询实体继承它并进行扩展</li>
 * </ul>
 * <p>
 * 页码从 1 开始（{@link #number} 默认值为 {@value #DEFAULT_NUMBER}），每页大小默认为 {@value #DEFAULT_SIZE} 条。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2021/01/05
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageQO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认页码字符串形式，便于外部化配置或注解默认值引用
	 */
	public static final String DEFAULT_NUMBER_STR = "1";

	/**
	 * 默认页码
	 */
	public static final int DEFAULT_NUMBER = Integer.parseInt(DEFAULT_NUMBER_STR);

	/**
	 * 默认每页大小字符串形式，便于外部化配置或注解默认值引用
	 */
	public static final String DEFAULT_SIZE_STR = "10";

	/**
	 * 默认每页大小
	 */
	public static final int DEFAULT_SIZE = Integer.parseInt(DEFAULT_SIZE_STR);

	/**
	 * 页码，从 1 开始，默认 {@value #DEFAULT_NUMBER}
	 */
	@Builder.Default
	private long number = DEFAULT_NUMBER;

	/**
	 * 每页大小，默认 {@value #DEFAULT_SIZE}
	 */
	@Builder.Default
	private long size = DEFAULT_SIZE;
}
