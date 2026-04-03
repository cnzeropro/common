package org.zero.common.core.support.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 默认操作符注册中心。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class SimpleOperatorRegistry implements OperatorRegistry {
	private final Map<String, OperatorDefinition> operatorMap;

	public SimpleOperatorRegistry(Collection<? extends OperatorDefinition> definitions) {
		this.operatorMap = new LinkedHashMap<>();
		if (definitions != null) {
			for (OperatorDefinition definition : definitions) {
				operatorMap.put(normalizeCode(definition.code()), definition);
			}
		}
	}

	public static SimpleOperatorRegistry defaultRegistry() {
		return new SimpleOperatorRegistry(Arrays.asList(
			SimpleOperatorDefinition.builder().code("eq").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("ne").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("gt").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("ge").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("lt").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("le").minValues(1).maxValues(1).build(),
			SimpleOperatorDefinition.builder().code("in").minValues(1).maxValues(-1).build(),
			SimpleOperatorDefinition.builder().code("between").minValues(2).maxValues(2).build(),
			SimpleOperatorDefinition.builder().code("isnull").minValues(0).maxValues(0).build(),
			SimpleOperatorDefinition.builder().code("isnotnull").minValues(0).maxValues(0).build(),
			SimpleOperatorDefinition.builder().code("contains").minValues(1).maxValues(1)
				.supportPredicate(fieldDescriptor -> fieldDescriptor.getJavaType() != null
					&& String.class.isAssignableFrom(fieldDescriptor.getJavaType())).build(),
			SimpleOperatorDefinition.builder().code("startswith").minValues(1).maxValues(1)
				.supportPredicate(fieldDescriptor -> fieldDescriptor.getJavaType() != null
					&& String.class.isAssignableFrom(fieldDescriptor.getJavaType())).build(),
			SimpleOperatorDefinition.builder().code("endswith").minValues(1).maxValues(1)
				.supportPredicate(fieldDescriptor -> fieldDescriptor.getJavaType() != null
					&& String.class.isAssignableFrom(fieldDescriptor.getJavaType())).build()
		));
	}

	@Override
	public OperatorDefinition getOperator(String code) {
		if (code == null) {
			return null;
		}
		return operatorMap.get(normalizeCode(code));
	}

	@Override
	public Collection<OperatorDefinition> getOperators() {
		return Collections.unmodifiableCollection(operatorMap.values());
	}

	private String normalizeCode(String code) {
		return code.trim().toLowerCase(Locale.ENGLISH);
	}
}
