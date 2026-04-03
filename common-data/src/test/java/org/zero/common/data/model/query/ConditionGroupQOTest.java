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
class ConditionGroupQOTest {

	@Test
	void noArgsConstructorShouldUseAndLogicAndEmptyChildren() {
		ConditionGroupQO groupQO = new ConditionGroupQO();

		assertAll(
			() -> assertEquals(ConditionGroupQO.Logic.AND, groupQO.getLogic()),
			() -> assertTrue(groupQO.getChildren().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedChildren() {
		List<PredicateQO> children = Arrays.asList(
			new ConditionQO("name", "eq", Arrays.<Object>asList("alice")),
			new ConditionQO("age", "gte", Arrays.<Object>asList(18))
		);
		ConditionGroupQO groupQO = new ConditionGroupQO(ConditionGroupQO.Logic.OR, children);

		assertAll(
			() -> assertEquals(ConditionGroupQO.Logic.OR, groupQO.getLogic()),
			() -> assertSame(children, groupQO.getChildren())
		);
	}
}
