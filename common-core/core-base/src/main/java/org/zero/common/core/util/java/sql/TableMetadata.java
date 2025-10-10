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
public class TableMetadata implements Serializable {
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
	private String name;
	/**
	 * 表类型
	 * <p>
	 * 如：TABLE、VIEW 等
	 */
	private String type;
	/**
	 * 表注释
	 * <p>
	 * 可能为 {@code null}
	 */
	private String remark;

	/**
	 * 类型目录
	 * <p>
	 * 可能为 {@code null}
	 */
	private String typeCatalog;
	/**
	 * 类型模式
	 * <p>
	 * 可能为 {@code null}
	 */
	private String typeSchema;
	/**
	 * 类型名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String typeName;

	/**
	 * 自引用列名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String selfReferencingColumnName;
	/**
	 * 引用生成方式
	 * <p>
	 * 可能为 {@code null}
	 * <p>
	 * 如：SYSTEM、USER、DERIVED
	 */
	private String referenceGeneration;
}
