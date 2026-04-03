package org.zero.common.core.support.query;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zero.common.data.model.query.ConditionGroupQO;
import org.zero.common.data.model.query.ConditionQO;
import org.zero.common.data.model.query.MetricQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.query.PredicateQO;
import org.zero.common.data.model.query.QueryQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.query.SortQO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 将外部查询对象编译为统一查询规范。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class QueryCompiler {
	public QuerySpec compile(QueryQO queryQO) {
		if (queryQO == null) {
			throw new IllegalArgumentException("QueryQO must not be null");
		}
		return QuerySpec.of(
			QueryMode.SEARCH,
			compilePredicate(queryQO.getWhere()),
			null,
			compileSorts(queryQO.getSorts()),
			normalizeTextList(queryQO.getFields()),
			Collections.emptyList(),
			Collections.emptyList(),
			PageSpec.of(queryQO.getPageNum(), queryQO.getPageSize())
		);
	}

	public QuerySpec compile(ReportQO reportQO) {
		if (reportQO == null) {
			throw new IllegalArgumentException("ReportQO must not be null");
		}
		return QuerySpec.of(
			QueryMode.REPORT,
			compilePredicate(reportQO.getWhere()),
			compilePredicate(reportQO.getHaving()),
			compileSorts(reportQO.getSorts()),
			Collections.emptyList(),
			normalizeTextList(reportQO.getDimensions()),
			compileMetrics(reportQO.getMetrics()),
			PageSpec.of(reportQO.getPageNum(), reportQO.getPageSize())
		);
	}

	public <T extends PageQO> QuerySpec compile(T queryObject, BusinessQueryAdapter<T> adapter) {
		if (queryObject == null) {
			throw new IllegalArgumentException("Business query object must not be null");
		}
		if (adapter == null) {
			throw new IllegalArgumentException("Business query adapter must not be null");
		}
		QuerySpec querySpec = adapter.adapt(queryObject);
		if (querySpec == null) {
			throw new IllegalArgumentException("Business query adapter must not return null");
		}
		return querySpec;
	}

	private List<SortSpec> compileSorts(List<SortQO> sortQOS) {
		if (CollectionUtils.isEmpty(sortQOS)) {
			return Collections.emptyList();
		}
		List<SortSpec> sortSpecs = new ArrayList<>(sortQOS.size());
		for (SortQO sortQO : sortQOS) {
			if (sortQO == null) {
				continue;
			}
			sortSpecs.add(SortSpec.of(
				requireText(sortQO.getField(), "Sort field must not be blank"),
				sortQO.getDirection() == null ? SortDirection.ASC : SortDirection.valueOf(sortQO.getDirection().name())
			));
		}
		return sortSpecs;
	}

	private List<String> normalizeTextList(List<String> values) {
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		return values.stream()
			.map(value -> requireText(value, "Field value must not be blank"))
			.collect(Collectors.toList());
	}

	private List<MetricSpec> compileMetrics(List<MetricQO> metricQOS) {
		if (CollectionUtils.isEmpty(metricQOS)) {
			return Collections.emptyList();
		}
		List<MetricSpec> metricSpecs = new ArrayList<>(metricQOS.size());
		for (MetricQO metricQO : metricQOS) {
			if (metricQO == null) {
				continue;
			}
			String field = requireText(metricQO.getField(), "Metric field must not be blank");
			String functionCode = requireText(metricQO.getFunction(), "Metric function must not be blank");
			String alias = StringUtils.hasText(metricQO.getAlias())
				? metricQO.getAlias().trim()
				: functionCode.toLowerCase(Locale.ENGLISH) + "_" + field;
			metricSpecs.add(MetricSpec.of(field, AggregateFunction.fromCode(functionCode), alias));
		}
		return metricSpecs;
	}

	private PredicateNode compilePredicate(PredicateQO predicateQO) {
		if (predicateQO == null) {
			return null;
		}
		if (predicateQO instanceof ConditionQO) {
			ConditionQO conditionQO = (ConditionQO) predicateQO;
			List<Object> values = conditionQO.getValues() == null ? Collections.emptyList() : new ArrayList<>(conditionQO.getValues());
			return AtomicPredicate.of(
				requireText(conditionQO.getField(), "Condition field must not be blank"),
				normalizeCode(conditionQO.getOperator(), "Condition operator must not be blank"),
				values
			);
		}
		if (predicateQO instanceof ConditionGroupQO) {
			ConditionGroupQO groupQO = (ConditionGroupQO) predicateQO;
			List<PredicateNode> children = new ArrayList<>();
			if (!CollectionUtils.isEmpty(groupQO.getChildren())) {
				for (PredicateQO child : groupQO.getChildren()) {
					children.add(compilePredicate(child));
				}
			}
			return PredicateGroup.of(fromGroupLogic(groupQO.getLogic()), children);
		}
		throw new IllegalArgumentException(String.format("Unsupported predicate type: %s", predicateQO.getClass().getName()));
	}

	private String requireText(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}

	private String normalizeCode(String value, String message) {
		return requireText(value, message).toLowerCase(Locale.ENGLISH);
	}

	private Logic fromGroupLogic(ConditionGroupQO.Logic logic) {
		return logic == null ? Logic.AND : Logic.valueOf(logic.name());
	}
}
