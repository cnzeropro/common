package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class FilterGroupQOTest {

	@Test
	void noArgsConstructorShouldUseAndLogicAndEmptyChildren() {
		FilterGroupQO filterGroupQO = new FilterGroupQO();

		assertAll(
				() -> assertEquals(FilterGroupQO.Logic.AND, filterGroupQO.getLogic()),
				() -> assertTrue(filterGroupQO.getChildren().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedChildren() {
		List<FilterQO> children = Arrays.asList(
				new FilterConditionQO("name", "eq", Arrays.<Object>asList("alice")),
				new FilterConditionQO("age", "gte", Arrays.<Object>asList(18))
		);
		FilterGroupQO filterGroupQO = new FilterGroupQO(FilterGroupQO.Logic.OR, children);

		assertAll(
				() -> assertEquals(FilterGroupQO.Logic.OR, filterGroupQO.getLogic()),
				() -> assertSame(children, filterGroupQO.getChildren())
		);
	}
}
