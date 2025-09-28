package org.zero.common.core.util.java.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/22
 */
class JdbcUtilTest {
	BaseConnectionInformation connectionInformation;

	@BeforeEach
	void setUp() {
		Properties properties = new Properties();
		// MySQL
		properties.setProperty("characterEncoding", "UTF-8");
		properties.setProperty("serverTimezone", "Asia/Shanghai");
		properties.setProperty("useUnicode", "true");
		properties.setProperty("useSSL", "false");
		properties.setProperty("tinyInt1isBit", "false");
		properties.setProperty("allowPublicKeyRetrieval", "true");
		properties.setProperty("allowMultiQueries", "true");
		connectionInformation = MySQLConnectionInformation.builder()
			.hostname("192.168.0.233")
			.username("root")
			.password("123.com")
			.databaseName("rongan")
			.driverClassName("com.mysql.cj.jdbc.Driver")
			.properties(properties)
			.build();

		//  Oracle
		// connectionInformation = OracleConnectionInformation.builder()
		// 	.hostname("192.168.0.197")
		// 	.port(1522)
		// 	.username("rongan")
		// 	.password("123.com")
		// 	.serverName("helowin")
		// 	.properties(properties)
		// 	.build();
	}

	@Test
	void getConnection() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		System.out.println(connection);
	}

	@Test
	void getDatabaseMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		DatabaseMetadata databaseMetadata = JdbcUtil.getDatabaseMetadata(connection);
		System.out.println(databaseMetadata);
	}

	@Test
	void getCatalogs() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<String> catalogs = JdbcUtil.getCatalogs(connection);
		for (String catalog : catalogs) {
			System.out.println(catalog);
		}
	}

	@Test
	void getSchemaMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<SchemaMetadata> schemaMetadata = JdbcUtil.getSchemaMetadata(connection);
		for (SchemaMetadata metadata : schemaMetadata) {
			System.out.println(metadata);
		}
	}

	@Test
	void getTableMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<TableMetadata> tableMetadata = JdbcUtil.getTableMetadata(connection);
		for (TableMetadata metadata : tableMetadata) {
			System.out.println(metadata);
		}
	}

	@Test
	void getColumnMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<ColumnMetadata> columnMetadata = JdbcUtil.getColumnMetadata(connection, "basic_party");
		for (ColumnMetadata metadata : columnMetadata) {
			System.out.println(metadata);
		}
	}

	@Test
	void getPrimaryKeyMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<PrimaryKeyMetadata> primaryKeyMetadata = JdbcUtil.getPrimaryKeyMetadata(connection, "basic_party");
		for (PrimaryKeyMetadata metadata : primaryKeyMetadata) {
			System.out.println(metadata);
		}
	}

	@Test
	void getIndexMetadata() {
		Connection connection = JdbcUtil.getConnection(connectionInformation);
		Collection<IndexMetadata> indexMetadata = JdbcUtil.getIndexMetadata(connection, "basic_party");
		for (IndexMetadata metadata : indexMetadata) {
			System.out.println(metadata);
		}
	}
}