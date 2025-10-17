package org.zero.common.core.support.bean.map;

import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.data.constant.StringPool;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 默认的 Bean 评估器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class RegexBeanEvaluator implements BeanEvaluator {
	/**
	 * 默认的 Bean 类名正则表达式模板
	 * <p>
	 * 注意：此处未限制层级数量，因此可能会出现过多层级导致性能问题。<br>
	 * 如有需要可以尝试使用：<pre>{@code
	 * public static final String DEFAULT_REGEX_TEMPLATE = "^(?:\\w+\\.){0,10}(%s)(?:\\.\\w+){0,10}$";
	 * }</pre>
	 * 此方案将：
	 * <ul>
	 *   <li>限制包路径和后缀的层级不超过 10 层</li>
	 *   <li>避免因过长路径导致的正则回溯性能问题</li>
	 * </ul>
	 */
	public static final String DEFAULT_REGEX_TEMPLATE = "^(\\w+\\.)*(%s)(\\.\\w+)*$";
	/**
	 * 默认的 Bean 类包层级名
	 */
	public static final String[] DEFAULT_PACKAGE_LEVEL_NAMES = {"model", "entity", "domain", "pojo"};
	/**
	 * 默认的 Bean 类名正则表达式
	 */
	public static final String DEFAULT_REGEX = String.format(DEFAULT_REGEX_TEMPLATE, ArrayUtil.join(DEFAULT_PACKAGE_LEVEL_NAMES, "|"));

	protected final Pattern[] patterns;

	protected RegexBeanEvaluator(Pattern... patterns) {
		this.patterns = patterns;
	}

	public static RegexBeanEvaluator of(Pattern... patterns) {
		return new RegexBeanEvaluator(patterns);
	}

	public static RegexBeanEvaluator ofRegexps(String... regexps) {
		Pattern[] patterns = ArrayUtil.map(regexps, Pattern::compile, Pattern.class);
		return of(patterns);
	}

	public static RegexBeanEvaluator ofBeanPackageLevelNames(String... beanPackageLevelNames) {
		String beanPackageLevelNamesJoined = String.join(StringPool.OR, beanPackageLevelNames);
		String regexp = String.format(DEFAULT_REGEX_TEMPLATE, beanPackageLevelNamesJoined);
		return ofRegexps(regexp);
	}

	@Override
	public boolean evaluate(Object object) {
		if (Objects.isNull(object)) {
			return false;
		}
		Class<?> clazz = object.getClass();
		return ClassUtil.isClassWithRegex(clazz, patterns);
	}
}
