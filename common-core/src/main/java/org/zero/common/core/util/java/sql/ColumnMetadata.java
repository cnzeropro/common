package org.zero.common.core.util.java.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class ColumnMetadata implements Serializable {
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
	private String name;
	/**
	 * 数据类型
	 *
	 * @see java.sql.Types
	 */
	private Integer dataType;
	/**
	 * 数据类型名称
	 */
	private String typeName;
	/**
	 * 列大小
	 * <p>
	 * 对于数值数据，是最大精度；对于字符数据，是字符长度；对于二进制数据，是字节数
	 */
	private Integer size;
	/**
	 * 小数位数
	 * <p>
	 * 不适用时为 {@code null}
	 */
	private Integer decimalPlaces;
	/**
	 * 基数
	 * <p>
	 * 通常为 10 或 2，表示精度的基数
	 */
	private Integer numberPrecisionRadix;
	/**
	 * 是否允许为 NULL
	 */
	private NullableType nullableType;
	/**
	 * 是否允许为 NULL
	 */
	private Boolean nullable;
	/**
	 * 列默认值
	 */
	private String defaultValue;
	/**
	 * 列注释
	 * <p>
	 * 可能为 {@code null}
	 */
	private String remark;
	/**
	 * 列在表中的顺序（从 1 开始）
	 */
	private Integer ordinalPosition;
	/**
	 * 是否是自动递增
	 */
	private Boolean autoIncrement;
	/**
	 * 是否是生成列
	 */
	private Boolean generatedColumn;
	/**
	 * 字符类型的最大字节数
	 */
	private Integer charOctetLength;

	/**
	 * 源类型（对 DISTINCT 或用户生成的 REF 类型）
	 *
	 * @see java.sql.Types
	 */
	private Short sourceDataType;
	/**
	 * 引用属性的表的目录
	 * <p>
	 * 如果 {@code DATA_TYPE} 不是 {@code REF} 则为 {@code null}
	 */
	private String scopeCatalog;
	/**
	 * 引用属性的表的模式
	 * <p>
	 * 如果 {@code DATA_TYPE} 不是 {@code REF} 则为 {@code null}
	 */
	private String scopeSchema;
	/**
	 * 引用属性的表名
	 * <p>
	 * 如果 {@code DATA_TYPE} 不是 {@code REF} 则为 {@code null}
	 */
	private String scopeTable;
}
