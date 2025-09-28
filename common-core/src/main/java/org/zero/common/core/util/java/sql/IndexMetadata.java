package org.zero.common.core.util.java.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/17
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IndexMetadata implements Serializable {
	/**
	 * 目录名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String catalog;
	/**
	 * 模式名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String schema;
	/**
	 * 表名
	 */
	private String table;
	/**
	 * 列名
	 * <p>
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时为 {@code null}
	 */
	private String column;

	/**
	 * 索引名
	 * <p>
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时为 {@code null}
	 */
	private String name;
	/**
	 * 索引类型
	 */
	private IndexType type;
	/**
	 * 是否非唯一索引
	 * <p>
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时为 {@code false}
	 */
	private Boolean nonUnique;

	/**
	 * 列在索引中的序列
	 * <p>
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时为 0
	 */
	private Short ordinalPosition;
	/**
	 * 列排序规则
	 * <p>
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时为 {@code null}
	 */
	private Sort sort;
	/**
	 * 索引目录
	 */
	private String indexQualifier;
	/**
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时，表示表中的行数；否则表示索引中唯一值的数量
	 */
	private Long cardinality;
	/**
	 * 当 {@code TYPE} 为 {@code tableIndexStatistic} 时，表示表使用的页面数；否则表示当前索引使用的页面数
	 */
	private Long pages;
	/**
	 * 过滤条件
	 * <p>
	 * 可能为 {@code null}
	 */
	private String filterCondition;
}
