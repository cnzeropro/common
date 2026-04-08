package org.zero.common.core.support.query.render;

import org.springframework.util.CollectionUtils;
import org.zero.common.core.support.query.AggregateFunction;
import org.zero.common.core.support.query.AtomicPredicate;
import org.zero.common.core.support.query.FieldDescriptor;
import org.zero.common.core.support.query.Logic;
import org.zero.common.core.support.query.MetricSpec;
import org.zero.common.core.support.query.OperatorRegistry;
import org.zero.common.core.support.query.PredicateGroup;
import org.zero.common.core.support.query.PredicateNode;
import org.zero.common.core.support.query.QueryMode;
import org.zero.common.core.support.query.QuerySchema;
import org.zero.common.core.support.query.QuerySpec;
import org.zero.common.core.support.query.QueryValidator;
import org.zero.common.core.support.query.SortSpec;
import org.zero.common.core.support.query.render.sql.BetweenSqlOperatorHandler;
import org.zero.common.core.support.query.render.sql.BinaryComparisonSqlOperatorHandler;
import org.zero.common.core.support.query.render.sql.InSqlOperatorHandler;
import org.zero.common.core.support.query.render.sql.LikeSqlOperatorHandler;
import org.zero.common.core.support.query.render.sql.NullCheckSqlOperatorHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL 查询渲染器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class SqlQueryRenderer implements QueryRenderer<SqlFragment> {
	private final QueryValidator queryValidator;
	private final Map<String, OperatorHandler<SqlFragment>> handlerMap;

	public SqlQueryRenderer(OperatorRegistry operatorRegistry) {
		this(operatorRegistry, defaultHandlers());
	}

	public SqlQueryRenderer(OperatorRegistry operatorRegistry, Collection<? extends OperatorHandler<SqlFragment>> handlers) {
		this.queryValidator = new QueryValidator(operatorRegistry);
		this.handlerMap = new LinkedHashMap<>();
		for (OperatorHandler<SqlFragment> handler : handlers) {
			handlerMap.put(handler.operatorCode(), handler);
		}
	}

	private static List<OperatorHandler<SqlFragment>> defaultHandlers() {
		return Arrays.asList(
			new BinaryComparisonSqlOperatorHandler("eq", "%s = ?"),
			new BinaryComparisonSqlOperatorHandler("ne", "%s <> ?"),
			new BinaryComparisonSqlOperatorHandler("gt", "%s > ?"),
			new BinaryComparisonSqlOperatorHandler("ge", "%s >= ?"),
			new BinaryComparisonSqlOperatorHandler("lt", "%s < ?"),
			new BinaryComparisonSqlOperatorHandler("le", "%s <= ?"),
			new InSqlOperatorHandler(),
			new BetweenSqlOperatorHandler(),
			new NullCheckSqlOperatorHandler("isnull", "%s IS NULL"),
			new NullCheckSqlOperatorHandler("isnotnull", "%s IS NOT NULL"),
			new LikeSqlOperatorHandler("contains", true, true),
			new LikeSqlOperatorHandler("startswith", false, true),
			new LikeSqlOperatorHandler("endswith", true, false)
		);
	}

	@Override
	public SqlFragment render(QuerySpec querySpec, QuerySchema querySchema) {
		queryValidator.validate(querySpec, querySchema);
		Map<String, MetricRenderMetadata> metricMetadata = buildMetricMetadata(querySpec, querySchema);
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ").append(renderSelectClause(querySpec, querySchema, metricMetadata));
		sql.append(" FROM ").append(querySchema.getFromClause());

		appendPredicateClause(sql, params, " WHERE ", querySpec.getWhere(), querySchema, Collections.emptyMap());

		if (querySpec.getMode() == QueryMode.REPORT && !CollectionUtils.isEmpty(querySpec.getDimensions())) {
			sql.append(" GROUP BY ")
				.append(querySpec.getDimensions().stream()
					.map(dimension -> querySchema.requireField(dimension).getColumnExpression())
					.collect(Collectors.joining(", ")));
		}

		appendPredicateClause(sql, params, " HAVING ", querySpec.getHaving(), querySchema, metricMetadata);
		appendOrderByClause(sql, querySpec, querySchema, metricMetadata);

		if (querySpec.getPage() != null) {
			sql.append(" LIMIT ").append(querySpec.getPage().getSize());
			sql.append(" OFFSET ").append(querySpec.getPage().getOffset());
		}

		return new SqlFragment(sql.toString(), params);
	}

	private void appendPredicateClause(StringBuilder sql,
	                                   List<Object> params,
	                                   String prefix,
	                                   PredicateNode predicateNode,
	                                   QuerySchema querySchema,
	                                   Map<String, MetricRenderMetadata> metricMetadata) {
		SqlFragment sqlFragment = renderPredicate(predicateNode, querySchema, metricMetadata);
		if (!sqlFragment.isEmpty()) {
			sql.append(prefix).append(sqlFragment.getSql());
			params.addAll(sqlFragment.getParams());
		}
	}

	private void appendOrderByClause(StringBuilder sql,
	                                 QuerySpec querySpec,
	                                 QuerySchema querySchema,
	                                 Map<String, MetricRenderMetadata> metricMetadata) {
		if (CollectionUtils.isEmpty(querySpec.getSorts())) {
			return;
		}
		String orderByClause = querySpec.getSorts().stream()
			.map(sortSpec -> renderSortExpression(sortSpec, querySchema, metricMetadata))
			.collect(Collectors.joining(", "));
		sql.append(" ORDER BY ").append(orderByClause);
	}

	private String renderSortExpression(SortSpec sortSpec,
	                                    QuerySchema querySchema,
	                                    Map<String, MetricRenderMetadata> metricMetadata) {
		if (metricMetadata.containsKey(sortSpec.getField())) {
			return sortSpec.getField() + " " + sortSpec.getDirection().name();
		}
		FieldDescriptor fieldDescriptor = querySchema.requireField(sortSpec.getField());
		return fieldDescriptor.getColumnExpression() + " " + sortSpec.getDirection().name();
	}

	private String renderSelectClause(QuerySpec querySpec,
	                                  QuerySchema querySchema,
	                                  Map<String, MetricRenderMetadata> metricMetadata) {
		if (querySpec.getMode() == QueryMode.REPORT) {
			List<String> projections = new ArrayList<>();
			for (String dimension : querySpec.getDimensions()) {
				FieldDescriptor fieldDescriptor = querySchema.requireField(dimension);
				projections.add(fieldDescriptor.getColumnExpression() + " AS " + dimension);
			}
			for (MetricSpec metric : querySpec.getMetrics()) {
				MetricRenderMetadata metadata = metricMetadata.get(metric.getAlias());
				projections.add(metadata.getSelectExpression());
			}
			return String.join(", ", projections);
		}
		if (CollectionUtils.isEmpty(querySpec.getFields())) {
			return "*";
		}
		return querySpec.getFields().stream()
			.map(field -> {
				FieldDescriptor fieldDescriptor = querySchema.requireField(field);
				return fieldDescriptor.getColumnExpression() + " AS " + field;
			})
			.collect(Collectors.joining(", "));
	}

	private SqlFragment renderPredicate(PredicateNode predicateNode,
	                                    QuerySchema querySchema,
	                                    Map<String, MetricRenderMetadata> metricMetadata) {
		if (predicateNode == null) {
			return SqlFragment.empty();
		}
		if (predicateNode instanceof AtomicPredicate) {
			return renderAtomicPredicate((AtomicPredicate) predicateNode, querySchema, metricMetadata);
		}
		PredicateGroup predicateGroup = (PredicateGroup) predicateNode;
		List<SqlFragment> fragments = new ArrayList<>();
		for (PredicateNode child : predicateGroup.getChildren()) {
			SqlFragment sqlFragment = renderPredicate(child, querySchema, metricMetadata);
			if (!sqlFragment.isEmpty()) {
				fragments.add(sqlFragment);
			}
		}
		if (fragments.isEmpty()) {
			return SqlFragment.empty();
		}
		List<Object> params = new ArrayList<>();
		String joiner = predicateGroup.getLogic() == Logic.OR ? " OR " : " AND ";
		String sql = fragments.stream()
			.map(fragment -> {
				params.addAll(fragment.getParams());
				return "(" + fragment.getSql() + ")";
			})
			.collect(Collectors.joining(joiner));
		return new SqlFragment(sql, params);
	}

	private SqlFragment renderAtomicPredicate(AtomicPredicate predicate,
	                                          QuerySchema querySchema,
	                                          Map<String, MetricRenderMetadata> metricMetadata) {
		FieldDescriptor fieldDescriptor = resolveFieldDescriptor(predicate.getField(), querySchema, metricMetadata);
		OperatorHandler<SqlFragment> handler = handlerMap.get(predicate.getOperator());
		if (handler == null) {
			throw new IllegalArgumentException(String.format("Unsupported operator handler: %s", predicate.getOperator()));
		}
		List<Object> values = predicate.getValues() == null ? Collections.emptyList() : predicate.getValues();
		return handler.render(fieldDescriptor, values);
	}

	private FieldDescriptor resolveFieldDescriptor(String field,
	                                               QuerySchema querySchema,
	                                               Map<String, MetricRenderMetadata> metricMetadata) {
		MetricRenderMetadata metadata = metricMetadata.get(field);
		if (metadata != null) {
			return metadata.getHavingDescriptor();
		}
		return querySchema.requireField(field);
	}

	private Map<String, MetricRenderMetadata> buildMetricMetadata(QuerySpec querySpec, QuerySchema querySchema) {
		if (querySpec.getMode() != QueryMode.REPORT || CollectionUtils.isEmpty(querySpec.getMetrics())) {
			return Collections.emptyMap();
		}
		Map<String, MetricRenderMetadata> metadataMap = new LinkedHashMap<>();
		for (MetricSpec metric : querySpec.getMetrics()) {
			FieldDescriptor fieldDescriptor = querySchema.requireField(metric.getField());
			String aggregateExpression = metric.getFunction().name() + "(" + fieldDescriptor.getColumnExpression() + ")";
			String selectExpression = aggregateExpression + " AS " + metric.getAlias();
			Class<?> metricType = fieldDescriptor.getJavaType();
			if (metric.getFunction() == AggregateFunction.COUNT) {
				metricType = Long.class;
			} else if (metric.getFunction() == AggregateFunction.AVG) {
				metricType = BigDecimal.class;
			}
			FieldDescriptor havingDescriptor = FieldDescriptor.builder()
				.key(metric.getAlias())
				.javaType(metricType)
				.columnExpression(aggregateExpression)
				.sortable(true)
				.build();
			metadataMap.put(metric.getAlias(), new MetricRenderMetadata(selectExpression, havingDescriptor));
		}
		return metadataMap;
	}

	private static class MetricRenderMetadata {
		private final String selectExpression;
		private final FieldDescriptor havingDescriptor;

		private MetricRenderMetadata(String selectExpression, FieldDescriptor havingDescriptor) {
			this.selectExpression = selectExpression;
			this.havingDescriptor = havingDescriptor;
		}

		public String getSelectExpression() {
			return selectExpression;
		}

		public FieldDescriptor getHavingDescriptor() {
			return havingDescriptor;
		}
	}
}
