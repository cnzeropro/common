package org.zero.common.core.util.java.sql;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Properties;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MySQLConnectionInformation implements BaseMySQLConnectionInformation {
	/**
	 * 数据库主机名。如：127.0.0.1
	 */
	@Builder.Default
	private String hostname = "127.0.0.1";
	/**
	 * 数据库端口。如：3306
	 */
	@Builder.Default
	private Integer port = 3306;
	/**
	 * 数据库名称。如：test
	 */
	private String databaseName;

	/**
	 * 数据库用户名。如：root
	 */
	@Builder.Default
	private String username = "root";
	/**
	 * 数据库密码。如：abc123
	 */
	private String password;

	/**
	 * 驱动类名。如：com.mysql.jdbc.Driver
	 */
	@Builder.Default
	private String driverClassName = "com.mysql.cj.jdbc.Driver";

	/**
	 * 连接参数
	 */
	private Properties properties;

}
