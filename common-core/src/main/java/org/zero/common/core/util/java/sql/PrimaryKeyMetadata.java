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
public class PrimaryKeyMetadata implements Serializable {
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
	 */
	private String column;

	/**
	 * 主键名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String name;
	/**
	 * 主键列的序列号
	 */
	private Short keySequence;
}
