package org.zero.common.core.support.query;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询规范校验器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class QueryValidator {
	private final OperatorRegistry operatorRegistry;

	public QueryValidator(OperatorRegistry operatorRegistry) {
		if (operatorRegistry == null) {
			throw new IllegalArgumentException("Operator registry must not be null");
		}
		this.operatorRegistry = operatorRegistry;
	}

	public void validate(QuerySpec querySpec, QuerySchema querySchema) {
		if (querySpec == null) {
			throw new IllegalArgumentException("Query spec must not be null");
		}
		if (querySchema == null) {
			throw new IllegalArgumentException("Query schema must not be null");
		}

		validatePage(querySpec.getPage());
		Map<String, FieldDescriptor> metricAliases = querySpec.getMode() == QueryMode.REPORT
			? validateReportShape(querySpec, querySchema)
			: Collections.emptyMap();

		if (querySpec.getMode() == QueryMode.SEARCH) {
			validateSelectedFields(querySpec.getFields(), querySchema);
			if (querySpec.getHaving() != null) {
				throw new IllegalArgumentException("Search query does not support having");
			}
		}

		validatePredicate(querySpec.getWhere(), querySchema, Collections.emptyMap());
		validatePredicate(querySpec.getHaving(), querySchema, metricAliases);
		validateSorts(querySpec.getSorts(), querySchema, metricAliases, querySpec.getMode() == QueryMode.REPORT);
	}

	private void validatePage(PageSpec pageSpec) {
		if (pageSpec == null) {
			throw new IllegalArgumentException("Page spec must not be null");
		}
		if (pageSpec.getPageNum() <= 0L) {
			throw new IllegalArgumentException("Page number must be greater than 0");
		}
		if (pageSpec.getPageSize() <= 0L) {
			throw new IllegalArgumentException("Page size must be greater than 0");
		}
	}

	private void validateSelectedFields(List<String> fields, QuerySchema querySchema) {
		if (CollectionUtils.isEmpty(fields)) {
			return;
		}
		for (String field : fields) {
			querySchema.requireField(field);
		}
	}

	private Map<String, FieldDescriptor> validateReportShape(QuerySpec querySpec, QuerySchema querySchema) {
		Map<String, FieldDescriptor> metricAliases = new LinkedHashMap<>();
		if (CollectionUtils.isEmpty(querySpec.getDimensions()) && CollectionUtils.isEmpty(querySpec.getMetrics())) {
			throw new IllegalArgumentException("Report query requires at least one dimension or metric");
		}
		for (String dimension : querySpec.getDimensions()) {
			FieldDescriptor fieldDescriptor = querySchema.requireField(dimension);
			if (!fieldDescriptor.isGroupable()) {
				throw new IllegalArgumentException(String.format("Field [%s] does not support grouping", dimension));
			}
		}
		for (MetricSpec metric : querySpec.getMetrics()) {
			FieldDescriptor sourceField = querySchema.requireField(metric.getField());
			if (!sourceField.isAggregatable()) {
				throw new IllegalArgumentException(String.format("Field [%s] does not support aggregation", metric.getField()));
			}
			if (!StringUtils.hasText(metric.getAlias())) {
				throw new IllegalArgumentException("Metric alias must not be blank");
			}
			if (metricAliases.containsKey(metric.getAlias())) {
				throw new IllegalArgumentException(String.format("Duplicate metric alias: %s", metric.getAlias()));
			}
			if (querySchema.getField(metric.getAlias()) != null) {
				throw new IllegalArgumentException(String.format("Metric alias [%s] conflicts with schema field", metric.getAlias()));
			}
			metricAliases.put(metric.getAlias(), buildMetricAliasDescriptor(sourceField, metric));
		}
		return metricAliases;
	}

	private FieldDescriptor buildMetricAliasDescriptor(FieldDescriptor sourceField, MetricSpec metricSpec) {
		Class<?> javaType = sourceField.getJavaType();
		if (metricSpec.getFunction() == AggregateFunction.COUNT) {
			javaType = Long.class;
		} else if (metricSpec.getFunction() == AggregateFunction.AVG) {
			javaType = BigDecimal.class;
		}
		return FieldDescriptor.builder()
			.key(metricSpec.getAlias())
			.javaType(javaType)
			.columnExpression(metricSpec.getAlias())
			.sortable(true)
			.groupable(false)
			.aggregatable(false)
			.build();
	}

	private void validateSorts(List<SortSpec> sortSpecs,
	                           QuerySchema querySchema,
	                           Map<String, FieldDescriptor> metricAliases,
	                           boolean allowMetricAlias) {
		if (CollectionUtils.isEmpty(sortSpecs)) {
			return;
		}
		for (SortSpec sortSpec : sortSpecs) {
			FieldDescriptor fieldDescriptor = resolveFieldDescriptor(sortSpec.getField(), querySchema, metricAliases, allowMetricAlias);
			if (!fieldDescriptor.isSortable()) {
				throw new IllegalArgumentException(String.format("Field [%s] does not support sorting", sortSpec.getField()));
			}
		}
	}

	private void validatePredicate(PredicateNode predicateNode,
	                               QuerySchema querySchema,
	                               Map<String, FieldDescriptor> aliases) {
		if (predicateNode == null) {
			return;
		}
		if (predicateNode instanceof AtomicPredicate) {
			validateAtomicPredicate((AtomicPredicate) predicateNode, querySchema, aliases);
			return;
		}
		if (predicateNode instanceof PredicateGroup) {
			PredicateGroup predicateGroup = (PredicateGroup) predicateNode;
			if (CollectionUtils.isEmpty(predicateGroup.getChildren())) {
				throw new IllegalArgumentException("Predicate group must contain children");
			}
			for (PredicateNode child : predicateGroup.getChildren()) {
				validatePredicate(child, querySchema, aliases);
			}
			return;
		}
		throw new IllegalArgumentException(String.format("Unsupported predicate node: %s", predicateNode.getClass().getName()));
	}

	private void validateAtomicPredicate(AtomicPredicate predicate,
	                                     QuerySchema querySchema,
	                                     Map<String, FieldDescriptor> aliases) {
		FieldDescriptor fieldDescriptor = resolveFieldDescriptor(predicate.getField(), querySchema, aliases, true);
		OperatorDefinition operatorDefinition = operatorRegistry.require(predicate.getOperator());
		List<Object> values = predicate.getValues() == null ? Collections.emptyList() : predicate.getValues();
		operatorDefinition.validateValueCount(values.size());
		if (!fieldDescriptor.supportsOperator(predicate.getOperator())) {
			throw new IllegalArgumentException(String.format("Field [%s] does not support operator [%s]", predicate.getField(), predicate.getOperator()));
		}
		if (!operatorDefinition.supports(fieldDescriptor)) {
			throw new IllegalArgumentException(String.format("Operator [%s] does not support field [%s]", predicate.getOperator(), predicate.getField()));
		}
	}

	private FieldDescriptor resolveFieldDescriptor(String field,
	                                               QuerySchema querySchema,
	                                               Map<String, FieldDescriptor> aliases,
	                                               boolean allowMetricAlias) {
		if (!StringUtils.hasText(field)) {
			throw new IllegalArgumentException("Field must not be blank");
		}
		if (allowMetricAlias && aliases.containsKey(field)) {
			return aliases.get(field);
		}
		return querySchema.requireField(field);
	}
}
