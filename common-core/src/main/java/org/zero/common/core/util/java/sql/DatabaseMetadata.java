package org.zero.common.core.util.java.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.sql.DatabaseMetaData;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/19
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class DatabaseMetadata implements Serializable {
	private transient DatabaseMetaData metaData;
	private String productName;
	private String productVersion;
	private int majorVersion;
	private int minorVersion;
	private String driverName;
	private String driverVersion;
	private int driverMajorVersion;
	private int driverMinorVersion;
	private boolean readOnly;
}
