package org.zero.common.data.model.qo;

import org.junit.jupiter.api.Test;

/**
 * @author zero
 * @since 2024/7/26
 */
class ConditionTest {
    Condition condition = Condition.create("name", Condition.Operator.IN, "1,2");

    @Test
    void getSql() {
        String sql = condition.getSql();
        System.out.println(sql);
    }

    @Test
    void getPrecompiledSql() {
        String precompiledSql = condition.getPrecompiledSql();
        System.out.println(precompiledSql);
    }

    @Test
    void getFormattedParam() {
        Object formattedParam = condition.getFormattedParam();
        System.out.println(formattedParam);
    }
}