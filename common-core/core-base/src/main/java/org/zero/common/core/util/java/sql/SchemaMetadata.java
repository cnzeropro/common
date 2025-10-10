package org.zero.common.core.util.java.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/19
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class SchemaMetadata implements Serializable {
	/**
	 * 目录名
	 * <p>
	 * 可能为 {@code null}
	 */
	private String catalog;

	/**
	 * 模式名
	 */
	private String name;
}
