package org.zero.common.core.util.java.sql;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/22
 */
class JdbcUtilTest {
	private static DatabaseMetaData databaseMetaData() {
		return proxy(DatabaseMetaData.class, (proxy, method, args) -> {
			String name = method.getName();
			if ("getDatabaseProductName".equals(name)) {
				return "ZeroDB";
			}
			if ("getDatabaseProductVersion".equals(name)) {
				return "1.0";
			}
			if ("getDatabaseMajorVersion".equals(name) || "getDriverMajorVersion".equals(name)) {
				return 1;
			}
			if ("getDatabaseMinorVersion".equals(name) || "getDriverMinorVersion".equals(name)) {
				return 0;
			}
			if ("getDriverName".equals(name)) {
				return "Zero JDBC Driver";
			}
			if ("getDriverVersion".equals(name)) {
				return "1.0";
			}
			if ("isReadOnly".equals(name)) {
				return false;
			}
			if ("getCatalogs".equals(name)) {
				return resultSet(row("TABLE_CAT", "catalog"));
			}
			if ("getSchemas".equals(name)) {
				return resultSet(row("TABLE_CATALOG", "catalog", "TABLE_SCHEM", "schema"));
			}
			if ("getTableTypes".equals(name)) {
				return resultSet(row("TABLE_TYPE", "TABLE"));
			}
			if ("getTables".equals(name)) {
				return resultSet(row(
						"TABLE_CAT", "catalog",
						"TABLE_SCHEM", "schema",
						"TABLE_NAME", "demo_table",
						"TABLE_TYPE", "TABLE",
						"REMARKS", "demo table",
						"TYPE_CAT", null,
						"TYPE_SCHEM", null,
						"TYPE_NAME", null,
						"SELF_REFERENCING_COL_NAME", null,
						"REF_GENERATION", null
				));
			}
			if ("getColumns".equals(name)) {
				return resultSet(row(
						"TABLE_CAT", "catalog",
						"TABLE_SCHEM", "schema",
						"TABLE_NAME", "demo_table",
						"COLUMN_NAME", "id",
						"DATA_TYPE", Types.INTEGER,
						"TYPE_NAME", "INTEGER",
						"COLUMN_SIZE", 10,
						"BUFFER_LENGTH", 0,
						"DECIMAL_DIGITS", 0,
						"NUM_PREC_RADIX", 10,
						"NULLABLE", DatabaseMetaData.columnNoNulls,
						"REMARKS", "primary key",
						"COLUMN_DEF", null,
						"SQL_DATA_TYPE", 0,
						"SQL_DATETIME_SUB", 0,
						"CHAR_OCTET_LENGTH", 0,
						"ORDINAL_POSITION", 1,
						"IS_NULLABLE", "NO",
						"SCOPE_CATALOG", null,
						"SCOPE_SCHEMA", null,
						"SCOPE_TABLE", null,
						"SOURCE_DATA_TYPE", null,
						"IS_AUTOINCREMENT", "YES",
						"IS_GENERATEDCOLUMN", "NO"
				));
			}
			if ("getPrimaryKeys".equals(name)) {
				return resultSet(row(
						"TABLE_CAT", "catalog",
						"TABLE_SCHEM", "schema",
						"TABLE_NAME", "demo_table",
						"COLUMN_NAME", "id",
						"PK_NAME", "PRIMARY",
						"KEY_SEQ", (short) 1
				));
			}
			if ("getIndexInfo".equals(name)) {
				return resultSet(row(
						"TABLE_CAT", "catalog",
						"TABLE_SCHEM", "schema",
						"TABLE_NAME", "demo_table",
						"INDEX_NAME", "idx_demo_name",
						"NON_UNIQUE", true,
						"INDEX_QUALIFIER", "catalog",
						"TYPE", DatabaseMetaData.tableIndexOther,
						"ORDINAL_POSITION", (short) 1,
						"COLUMN_NAME", "name",
						"ASC_OR_DESC", "A",
						"CARDINALITY", 10L,
						"PAGES", 1L,
						"FILTER_CONDITION", null
				));
			}
			return defaultValue(method.getReturnType());
		});
	}

	private static Connection connection(DatabaseMetaData databaseMetaData) {
		return proxy(Connection.class, new InvocationHandler() {
			private String catalog = "catalog";
			private String schema = "schema";

			@Override
			public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
				String name = method.getName();
				if ("getMetaData".equals(name)) {
					return databaseMetaData;
				}
				if ("getCatalog".equals(name)) {
					return catalog;
				}
				if ("setCatalog".equals(name)) {
					catalog = (String) args[0];
					return null;
				}
				if ("getSchema".equals(name)) {
					return schema;
				}
				if ("setSchema".equals(name)) {
					schema = (String) args[0];
					return null;
				}
				return defaultValue(method.getReturnType());
			}
		});
	}

	@SafeVarargs
	private static ResultSet resultSet(Map<String, Object>... rows) {
		List<Map<String, Object>> rowList = Arrays.asList(rows);
		return proxy(ResultSet.class, new InvocationHandler() {
			private int index = -1;

			@Override
			public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
				String name = method.getName();
				if ("next".equals(name)) {
					index++;
					return index < rowList.size();
				}
				if ("close".equals(name)) {
					return null;
				}
				if ("getString".equals(name)) {
					Object value = value(args[0]);
					return value == null ? null : value.toString();
				}
				if ("getInt".equals(name)) {
					Object value = value(args[0]);
					return value == null ? 0 : ((Number) value).intValue();
				}
				if ("getLong".equals(name)) {
					Object value = value(args[0]);
					return value == null ? 0L : ((Number) value).longValue();
				}
				if ("getShort".equals(name)) {
					Object value = value(args[0]);
					return value == null ? (short) 0 : ((Number) value).shortValue();
				}
				if ("getBoolean".equals(name)) {
					Object value = value(args[0]);
					return value != null && (Boolean) value;
				}
				return defaultValue(method.getReturnType());
			}

			private Object value(Object column) {
				return rowList.get(index).get(String.valueOf(column));
			}
		});
	}

	private static Map<String, Object> row(Object... keyValues) {
		Map<String, Object> row = new java.util.LinkedHashMap<>();
		for (int i = 0; i < keyValues.length; i += 2) {
			row.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
		}
		return row;
	}

	private static <T> T only(Collection<T> values) {
		assertEquals(1, values.size());
		return values.iterator().next();
	}

	@SuppressWarnings("unchecked")
	private static <T> T proxy(Class<T> type, InvocationHandler invocationHandler) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, invocationHandler);
	}

	private static Object defaultValue(Class<?> returnType) {
		if (returnType == Void.TYPE) {
			return null;
		}
		if (returnType == Boolean.TYPE) {
			return false;
		}
		if (returnType == Byte.TYPE) {
			return (byte) 0;
		}
		if (returnType == Short.TYPE) {
			return (short) 0;
		}
		if (returnType == Integer.TYPE) {
			return 0;
		}
		if (returnType == Long.TYPE) {
			return 0L;
		}
		if (returnType == Float.TYPE) {
			return 0F;
		}
		if (returnType == Double.TYPE) {
			return 0D;
		}
		if (returnType == Character.TYPE) {
			return (char) 0;
		}
		return null;
	}

	@Test
	void getConnectionShouldUseDriverManagerAndMergedProperties() throws SQLException {
		Connection connection = connection(databaseMetaData());
		FakeDriver driver = new FakeDriver(connection);
		DriverManager.registerDriver(driver);
		try {
			Properties properties = new Properties();
			properties.setProperty("useUnicode", "true");
			BaseConnectionInformation connectionInformation = MySQLConnectionInformation.builder()
					.hostname("localhost")
					.databaseName("demo")
					.username("zero")
					.password("secret")
					.driverClassName(FakeDriver.class.getName())
					.properties(properties)
					.build();

			Connection actual = JdbcUtil.getConnection(connectionInformation);

			assertSame(connection, actual);
			assertEquals("jdbc:mysql://localhost:3306/demo", driver.url);
			assertEquals("zero", driver.properties.getProperty("user"));
			assertEquals("secret", driver.properties.getProperty("password"));
			assertEquals("true", driver.properties.getProperty("useUnicode"));
		} finally {
			DriverManager.deregisterDriver(driver);
		}
	}

	@Test
	void metadataMethodsShouldReadDatabaseCatalogSchemaAndTableInformation() {
		Connection connection = connection(databaseMetaData());

		DatabaseMetadata databaseMetadata = JdbcUtil.getDatabaseMetadata(connection);
		Collection<String> catalogs = JdbcUtil.getCatalogs(connection);
		SchemaMetadata schemaMetadata = only(JdbcUtil.getSchemaMetadata(connection));
		TableMetadata tableMetadata = only(JdbcUtil.getTableMetadata(connection));

		assertEquals("ZeroDB", databaseMetadata.getProductName());
		assertEquals("1.0", databaseMetadata.getProductVersion());
		assertEquals(Collections.singletonList("catalog"), new ArrayList<>(catalogs));
		assertEquals("catalog", schemaMetadata.getCatalog());
		assertEquals("schema", schemaMetadata.getName());
		assertEquals("demo_table", tableMetadata.getName());
		assertEquals("TABLE", tableMetadata.getType());
	}

	@Test
	void metadataMethodsShouldReadColumnPrimaryKeyAndIndexInformation() {
		Connection connection = connection(databaseMetaData());

		ColumnMetadata columnMetadata = only(JdbcUtil.getColumnMetadata(connection, "demo_table"));
		PrimaryKeyMetadata primaryKeyMetadata = only(JdbcUtil.getPrimaryKeyMetadata(connection, "demo_table"));
		IndexMetadata indexMetadata = only(JdbcUtil.getIndexMetadata(connection, "demo_table"));

		assertEquals("id", columnMetadata.getName());
		assertEquals(Integer.valueOf(Types.INTEGER), columnMetadata.getDataType());
		assertEquals(NullableType.NOT_NULL, columnMetadata.getNullableType());
		assertEquals(Boolean.FALSE, columnMetadata.getNullable());
		assertEquals("PRIMARY", primaryKeyMetadata.getName());
		assertEquals("id", primaryKeyMetadata.getColumn());
		assertEquals("idx_demo_name", indexMetadata.getName());
		assertEquals(IndexType.OTHER, indexMetadata.getType());
		assertEquals(Sort.ASCENDING, indexMetadata.getSort());
	}

	public static class FakeDriver implements Driver {
		private final Connection connection;
		private String url;
		private Properties properties;

		FakeDriver(Connection connection) {
			this.connection = connection;
		}

		@Override
		public Connection connect(String url, Properties info) {
			this.url = url;
			this.properties = info;
			return connection;
		}

		@Override
		public boolean acceptsURL(String url) {
			return url != null && url.startsWith("jdbc:mysql:");
		}

		@Override
		public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
			return new DriverPropertyInfo[0];
		}

		@Override
		public int getMajorVersion() {
			return 1;
		}

		@Override
		public int getMinorVersion() {
			return 0;
		}

		@Override
		public boolean jdbcCompliant() {
			return false;
		}

		@Override
		public Logger getParentLogger() {
			return Logger.getGlobal();
		}
	}
}
