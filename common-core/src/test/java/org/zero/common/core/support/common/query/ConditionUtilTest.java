package org.zero.common.core.support.common.query;

import org.junit.jupiter.api.Test;
import org.zero.common.core.support.common.query.sql.ComparisonOperator;
import org.zero.common.data.model.query.Condition;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
class ConditionUtilTest {
    Condition condition = Condition.create("name", ComparisonOperator.IN, "1,2");

    @Test
    void getSql() {
        String sqlSnippet = ConditionUtil.getSqlSnippet(condition);
        System.out.println(sqlSnippet);
    }

    @Test
    void getPrecompiledSql() {
        String precompiledSnippet = ConditionUtil.getPrecompiledSnippet(condition);
        System.out.println(precompiledSnippet);
    }

    @Test
    void getFormattedParam() {
        Object formattedParam = ConditionUtil.getFormattedParam(condition);
        System.out.println(formattedParam);
    }
}