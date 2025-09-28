package org.zero.common.core.util.java.sql;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
@Log
public class JdbcUtil {
	/**
	 * 获取数据库连接
	 *
	 * @param connectionInformation 数据库连接信息
	 * @return 数据库连接
	 */
	@SneakyThrows
	public static Connection getConnection(BaseConnectionInformation connectionInformation) {
		// 在JDBC 4.0（Java SE 6）之前，需要显式加载驱动程序类
		ClassLoaderUtil.loadClassOpt(connectionInformation.getDriverClassName(), true);
		// 创建 Connection
		Connection connection = DriverManager.getConnection(connectionInformation.getBaseUrl(), connectionInformation.getMergedProperties());
		String catalog = connectionInformation.getCatalog();
		if (Objects.nonNull(catalog)) {
			connection.setCatalog(catalog);
		}
		String schema = connectionInformation.getSchema();
		if (Objects.nonNull(schema)) {
			connection.setSchema(schema);
		}
		return connection;
	}

	/**
	 * 获取数据库元数据
	 *
	 * @param connection 数据库连接
	 * @return 数据库元数据
	 */
	@SneakyThrows
	public static DatabaseMetadata getDatabaseMetadata(Connection connection) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		String databaseProductName = databaseMetaData.getDatabaseProductName();
		String databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
		int databaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
		int databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
		String driverName = databaseMetaData.getDriverName();
		String driverVersion = databaseMetaData.getDriverVersion();
		int driverMajorVersion = databaseMetaData.getDriverMajorVersion();
		int driverMinorVersion = databaseMetaData.getDriverMinorVersion();
		boolean readOnly = databaseMetaData.isReadOnly();
		return DatabaseMetadata.builder()
			.metaData(databaseMetaData)
			.productName(databaseProductName)
			.productVersion(databaseProductVersion)
			.majorVersion(databaseMajorVersion)
			.minorVersion(databaseMinorVersion)
			.driverName(driverName)
			.driverVersion(driverVersion)
			.driverMajorVersion(driverMajorVersion)
			.driverMinorVersion(driverMinorVersion)
			.readOnly(readOnly)
			.build();
	}

	/**
	 * 获取目录
	 *
	 * @param connection 数据库连接
	 * @return 目录
	 */
	@SneakyThrows
	public static Collection<String> getCatalogs(Connection connection) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet catalogResultSet = databaseMetaData.getCatalogs();
		Collection<String> catalogs = new ArrayList<>();
		while (catalogResultSet.next()) {
			String tableCat = getString(catalogResultSet, "TABLE_CAT");
			catalogs.add(tableCat);
		}
		catalogResultSet.close();
		return catalogs;
	}

	/**
	 * 获取模式元数据
	 *
	 * @param connection 数据库连接
	 * @return 模式元数据
	 */
	@SneakyThrows
	public static Collection<SchemaMetadata> getSchemaMetadata(Connection connection) {
		return getSchemaMetadata(connection, "%");
	}

	/**
	 * 获取当前连接的模式元数据
	 *
	 * @param connection 数据库连接
	 * @return 模式元数据
	 */
	@SneakyThrows
	public static Optional<SchemaMetadata> getCurrentSchemaMetadataOpt(Connection connection) {
		return getSchemaMetadata(connection, connection.getCatalog(), connection.getSchema()).stream().findFirst();
	}

	/**
	 * 获取模式元数据
	 *
	 * @param connection    数据库连接
	 * @param schemaPattern 模式表达式
	 * @return 模式元数据
	 */
	@SneakyThrows
	public static Collection<SchemaMetadata> getSchemaMetadata(Connection connection, String schemaPattern) {
		return getSchemaMetadata(connection, connection.getCatalog(), schemaPattern);
	}

	/**
	 * 获取模式元数据
	 *
	 * @param connection    数据库连接
	 * @param catalog       目录
	 * @param schemaPattern 模式表达式
	 * @return 模式元数据
	 */
	@SneakyThrows
	public static Collection<SchemaMetadata> getSchemaMetadata(Connection connection, String catalog, String schemaPattern) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet schemaResultSet = databaseMetaData.getSchemas(catalog, schemaPattern);
		Collection<SchemaMetadata> schemaMetadataSet = new ArrayList<>();
		while (schemaResultSet.next()) {
			String tableCatalog = getString(schemaResultSet, "TABLE_CATALOG");
			String tableSchem = getString(schemaResultSet, "TABLE_SCHEM");
			SchemaMetadata schemaMetadata = SchemaMetadata.builder()
				.catalog(tableCatalog)
				.name(tableSchem)
				.build();
			schemaMetadataSet.add(schemaMetadata);
		}
		schemaResultSet.close();
		return schemaMetadataSet;
	}

	/**
	 * 获取表元数据
	 *
	 * @param connection 数据库连接
	 * @return 表元数据
	 */
	@SneakyThrows
	public static Collection<TableMetadata> getTableMetadata(Connection connection) {
		return getTableMetadata(connection, "%");
	}

	/**
	 * 获取表元数据
	 *
	 * @param connection       数据库连接
	 * @param tableNamePattern 表名表达式
	 * @return 表元数据
	 */
	@SneakyThrows
	public static Collection<TableMetadata> getTableMetadata(Connection connection, String tableNamePattern) {
		return getTableMetadata(connection, connection.getSchema(), tableNamePattern);
	}

	/**
	 * 获取表元数据
	 *
	 * @param connection       数据库连接
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @return 表元数据
	 */
	@SneakyThrows
	public static Collection<TableMetadata> getTableMetadata(Connection connection, String schemaPattern, String tableNamePattern) {
		Collection<String> tableTypes = getTableTypes(connection);
		return getTableMetadata(connection, schemaPattern, tableNamePattern, tableTypes.toArray(new String[0]));
	}

	/**
	 * 获取表元数据
	 *
	 * @param connection       数据库连接
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @param tableTypes       表类型
	 * @return 表元数据
	 */
	@SneakyThrows
	public static Collection<TableMetadata> getTableMetadata(Connection connection,
															 String schemaPattern, String tableNamePattern,
															 String... tableTypes) {
		return getTableMetadata(connection, connection.getCatalog(), schemaPattern, tableNamePattern, tableTypes);
	}

	/**
	 * 获取表元数据
	 *
	 * @param connection       数据库连接
	 * @param catalog          目录
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @param tableTypes       表类型
	 * @return 表元数据
	 */
	@SneakyThrows
	public static Collection<TableMetadata> getTableMetadata(Connection connection,
															 String catalog,
															 String schemaPattern, String tableNamePattern,
															 String... tableTypes) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		@Cleanup ResultSet tableResultSet = databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, tableTypes);
		Collection<TableMetadata> tableMetadataSet = new ArrayList<>();
		while (tableResultSet.next()) {
			String tableCat = getString(tableResultSet, "TABLE_CAT");
			String tableSchem = getString(tableResultSet, "TABLE_SCHEM");
			String tableName = getString(tableResultSet, "TABLE_NAME");
			String tableType = getString(tableResultSet, "TABLE_TYPE");
			String remarks = getString(tableResultSet, "REMARKS");
			String typeCat = getString(tableResultSet, "TYPE_CAT");
			String typeSchem = getString(tableResultSet, "TYPE_SCHEM");
			String typeName = getString(tableResultSet, "TYPE_NAME");
			String selfReferencingColName = getString(tableResultSet, "SELF_REFERENCING_COL_NAME");
			String refGeneration = getString(tableResultSet, "REF_GENERATION");
			TableMetadata tableMetadata = TableMetadata.builder()
				.catalog(tableCat)
				.schema(tableSchem)
				.name(tableName)
				.type(tableType)
				.remark(remarks)
				.typeCatalog(typeCat)
				.typeSchema(typeSchem)
				.typeName(typeName)
				.selfReferencingColumnName(selfReferencingColName)
				.referenceGeneration(refGeneration)
				.build();
			tableMetadataSet.add(tableMetadata);
		}
		tableResultSet.close();
		return tableMetadataSet;
	}

	/**
	 * 获取表类型
	 *
	 * @param connection 数据库连接
	 * @return 表类型
	 */
	@SneakyThrows
	public static Collection<String> getTableTypes(Connection connection) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet tableTypeResultSet = databaseMetaData.getTableTypes();
		Collection<String> tableTypes = new ArrayList<>();
		while (tableTypeResultSet.next()) {
			String tableType = getString(tableTypeResultSet, "TABLE_TYPE");
			tableTypes.add(tableType);
		}
		tableTypeResultSet.close();
		return tableTypes;
	}

	/**
	 * 获取列元数据
	 *
	 * @param connection       数据库连接
	 * @param tableNamePattern 表名表达式
	 * @return 列元数据
	 */
	@SneakyThrows
	public static Collection<ColumnMetadata> getColumnMetadata(Connection connection, String tableNamePattern) {
		return getColumnMetadata(connection, connection.getSchema(), tableNamePattern);
	}

	/**
	 * 获取列元数据
	 *
	 * @param connection       数据库连接
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @return 列元数据
	 */
	public static Collection<ColumnMetadata> getColumnMetadata(Connection connection, String schemaPattern, String tableNamePattern) {
		return getColumnMetadata(connection, schemaPattern, tableNamePattern, "%");
	}

	/**
	 * 获取列元数据
	 *
	 * @param connection       数据库连接
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @param columnNamePatten 列名表达式
	 * @return 列元数据
	 */
	@SneakyThrows
	public static Collection<ColumnMetadata> getColumnMetadata(Connection connection,
															   String schemaPattern, String tableNamePattern, String columnNamePatten) {
		return getColumnMetadata(connection, connection.getCatalog(), schemaPattern, tableNamePattern, columnNamePatten);
	}

	/**
	 * 获取列元数据
	 *
	 * @param connection       数据库连接
	 * @param catalog          目录
	 * @param schemaPattern    模式表达式
	 * @param tableNamePattern 表名表达式
	 * @param columnNamePatten 列名表达式
	 * @return 列元数据
	 */
	@SneakyThrows
	public static Collection<ColumnMetadata> getColumnMetadata(Connection connection,
															   String catalog,
															   String schemaPattern, String tableNamePattern, String columnNamePatten) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet columnResultSet = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePatten);
		Collection<ColumnMetadata> columnMetadataSet = new ArrayList<>();
		while (columnResultSet.next()) {
			String tableCat = getString(columnResultSet, "TABLE_CAT");
			String tableSchem = getString(columnResultSet, "TABLE_SCHEM");
			String tableName = getString(columnResultSet, "TABLE_NAME");
			String columnName = getString(columnResultSet, "COLUMN_NAME");
			Integer dataType = getInteger(columnResultSet, "DATA_TYPE");
			String typeName = getString(columnResultSet, "TYPE_NAME");
			Integer columnSize = getInteger(columnResultSet, "COLUMN_SIZE");
			Integer bufferLength = getInteger(columnResultSet, "BUFFER_LENGTH");
			Integer decimalDigits = getInteger(columnResultSet, "DECIMAL_DIGITS");
			Integer numPrecRadix = getInteger(columnResultSet, "NUM_PREC_RADIX");
			Integer nullable = getInteger(columnResultSet, "NULLABLE");
			String remarks = getString(columnResultSet, "REMARKS");
			String columnDef = getString(columnResultSet, "COLUMN_DEF");
			Integer sqlDataType = getInteger(columnResultSet, "SQL_DATA_TYPE");
			Integer sqlDatetimeSub = getInteger(columnResultSet, "SQL_DATETIME_SUB");
			Integer charOctetLength = getInteger(columnResultSet, "CHAR_OCTET_LENGTH");
			Integer ordinalPosition = getInteger(columnResultSet, "ORDINAL_POSITION");
			String isNullable = getString(columnResultSet, "IS_NULLABLE");
			String scopeCatalog = getString(columnResultSet, "SCOPE_CATALOG");
			String scopeSchema = getString(columnResultSet, "SCOPE_SCHEMA");
			String scopeTable = getString(columnResultSet, "SCOPE_TABLE");
			Short sourceDataType = getShort(columnResultSet, "SOURCE_DATA_TYPE");
			String isAutoincrement = getString(columnResultSet, "IS_AUTOINCREMENT");
			String isGeneratedcolumn = getString(columnResultSet, "IS_GENERATEDCOLUMN");

			ColumnMetadata columnMetadata = ColumnMetadata.builder()
				.catalog(tableCat)
				.schema(tableSchem)
				.table(tableName)
				.name(columnName)
				.dataType(dataType)
				.typeName(typeName)
				.size(columnSize)
				.decimalPlaces(decimalDigits)
				.numberPrecisionRadix(numPrecRadix)
				.nullableType(NullableType.from(nullable))
				.nullable(toBoolean(isNullable))
				.defaultValue(columnDef)
				.remark(remarks)
				.ordinalPosition(ordinalPosition)
				.charOctetLength(charOctetLength)
				.scopeCatalog(scopeCatalog)
				.scopeSchema(scopeSchema)
				.scopeTable(scopeTable)
				.sourceDataType(sourceDataType)
				.autoIncrement(toBoolean(isAutoincrement))
				.generatedColumn(toBoolean(isGeneratedcolumn))
				.build();
			columnMetadataSet.add(columnMetadata);
		}
		columnResultSet.close();
		return columnMetadataSet;
	}

	/**
	 * 获取主键元数据
	 *
	 * @param connection 数据库连接
	 * @param table      表
	 * @return 主键元数据
	 */
	@SneakyThrows
	public static Collection<PrimaryKeyMetadata> getPrimaryKeyMetadata(Connection connection, String table) {
		return getPrimaryKeyMetadata(connection, connection.getSchema(), table);
	}

	/**
	 * 获取主键元数据
	 *
	 * @param connection 数据库连接
	 * @param schema     模式
	 * @param table      表
	 * @return 主键元数据
	 */
	@SneakyThrows
	public static Collection<PrimaryKeyMetadata> getPrimaryKeyMetadata(Connection connection, String schema, String table) {
		return getPrimaryKeyMetadata(connection, connection.getCatalog(), schema, table);
	}

	/**
	 * 获取主键元数据
	 *
	 * @param connection 数据库连接
	 * @param catalog    目录
	 * @param schema     模式
	 * @param table      表
	 * @return 主键元数据
	 */
	@SneakyThrows
	public static Collection<PrimaryKeyMetadata> getPrimaryKeyMetadata(Connection connection, String catalog, String schema, String table) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(catalog, schema, table);
		Collection<PrimaryKeyMetadata> primaryKeyMetadataSet = new ArrayList<>();
		while (primaryKeyResultSet.next()) {
			String tableCat = getString(primaryKeyResultSet, "TABLE_CAT");
			String tableSchem = getString(primaryKeyResultSet, "TABLE_SCHEM");
			String tableName = getString(primaryKeyResultSet, "TABLE_NAME");
			String columnName = getString(primaryKeyResultSet, "COLUMN_NAME");
			String pkName = getString(primaryKeyResultSet, "PK_NAME");
			Short keySeq = getShort(primaryKeyResultSet, "KEY_SEQ");
			PrimaryKeyMetadata primaryKeyMetadata = PrimaryKeyMetadata.builder()
				.catalog(tableCat)
				.schema(tableSchem)
				.table(tableName)
				.column(columnName)
				.name(pkName)
				.keySequence(keySeq)
				.build();
			primaryKeyMetadataSet.add(primaryKeyMetadata);
		}
		primaryKeyResultSet.close();
		return primaryKeyMetadataSet;
	}

	/**
	 * 获取索引元数据
	 *
	 * @param connection 数据库连接
	 * @param table      表
	 * @return 索引元数据
	 */
	@SneakyThrows
	public static Collection<IndexMetadata> getIndexMetadata(Connection connection, String table) {
		return getIndexMetadata(connection, connection.getSchema(), table);
	}

	/**
	 * 获取索引元数据
	 *
	 * @param connection 数据库连接
	 * @param schema     模式
	 * @param table      表
	 * @return 索引元数据
	 */
	@SneakyThrows
	public static Collection<IndexMetadata> getIndexMetadata(Connection connection, String schema, String table) {
		return getIndexMetadata(connection, connection.getCatalog(), schema, table);
	}

	/**
	 * 获取索引元数据
	 *
	 * @param connection 数据库连接
	 * @param catalog    目录
	 * @param schema     模式
	 * @param table      表
	 * @return 索引元数据
	 */
	@SneakyThrows
	public static Collection<IndexMetadata> getIndexMetadata(Connection connection, String catalog, String schema, String table) {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet indexResultSet = databaseMetaData.getIndexInfo(catalog, schema, table, false, false);
		Collection<IndexMetadata> indexMetadataSet = new ArrayList<>();
		while (indexResultSet.next()) {
			String tableCat = getString(indexResultSet, "TABLE_CAT");
			String tableSchem = getString(indexResultSet, "TABLE_SCHEM");
			String tableName = getString(indexResultSet, "TABLE_NAME");
			String indexName = getString(indexResultSet, "INDEX_NAME");
			Boolean nonUnique = getBoolean(indexResultSet, "NON_UNIQUE");
			String indexQualifier = getString(indexResultSet, "INDEX_QUALIFIER");
			Short type = getShort(indexResultSet, "TYPE");
			Short ordinalPosition = getShort(indexResultSet, "ORDINAL_POSITION");
			String columnName = getString(indexResultSet, "COLUMN_NAME");
			String ascOrDesc = getString(indexResultSet, "ASC_OR_DESC");
			Long cardinality = getLong(indexResultSet, "CARDINALITY");
			Long pages = getLong(indexResultSet, "PAGES");
			String filterCondition = getString(indexResultSet, "FILTER_CONDITION");
			IndexMetadata indexMetadata = IndexMetadata.builder()
				.catalog(tableCat)
				.schema(tableSchem)
				.table(tableName)
				.name(indexName)
				.nonUnique(nonUnique)
				.indexQualifier(indexQualifier)
				.type(IndexType.from(type))
				.ordinalPosition(ordinalPosition)
				.column(columnName)
				.sort(Sort.from(ascOrDesc))
				.cardinality(cardinality)
				.pages(pages)
				.filterCondition(filterCondition)
				.build();
			indexMetadataSet.add(indexMetadata);
		}
		indexResultSet.close();
		return indexMetadataSet;
	}

	protected static String getString(ResultSet resultSet, String columnName) {
		try {
			return resultSet.getString(columnName);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error getting column '" + columnName + "' from result set", e);
			return null;
		}
	}

	protected static Long getLong(ResultSet resultSet, String columnName) {
		try {
			return resultSet.getLong(columnName);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error getting column '" + columnName + "' from result set", e);
			return null;
		}
	}

	protected static Integer getInteger(ResultSet resultSet, String columnName) {
		try {
			return resultSet.getInt(columnName);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error getting column '" + columnName + "' from result set", e);
			return null;
		}
	}

	protected static Short getShort(ResultSet resultSet, String columnName) {
		try {
			return resultSet.getShort(columnName);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error getting column '" + columnName + "' from result set", e);
			return null;
		}
	}

	protected static Boolean getBoolean(ResultSet resultSet, String columnName) {
		try {
			return resultSet.getBoolean(columnName);
		} catch (SQLException e) {
			log.log(Level.WARNING, "Error getting column '" + columnName + "' from result set", e);
			return null;
		}
	}


	protected static Boolean toBoolean(String value) {
		if ("YES".equalsIgnoreCase(value)) {
			return Boolean.TRUE;
		}
		if ("NO".equalsIgnoreCase(value)) {
			return Boolean.FALSE;
		}
		return null;
	}

	protected JdbcUtil() {
		throw new UnsupportedOperationException();
	}
}
