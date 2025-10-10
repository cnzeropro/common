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
public class OracleConnectionInformation implements BaseOracleConnectionInformation {
	/**
	 * 数据库主机名。如：127.0.0.1
	 */
	@Builder.Default
	private String hostname = "127.0.0.1";
	/**
	 * 数据库端口。如：1521
	 */
	@Builder.Default
	private Integer port = 1521;
	/**
	 * 服务名。如：test
	 */
	private String serverName;
	/**
	 * 数据库模式。如：sys
	 */
	private String schema;

	/**
	 * 数据库用户名。如：sys
	 */
	private String username;
	/**
	 * 数据库密码。如：abc123
	 */
	private String password;

	/**
	 * 连接参数
	 */
	private Properties properties;
}
