package org.zero.common.core.support.bean.map;

import org.zero.common.data.constant.StringPool;

import static org.zero.common.core.util.hutool.core.bean.BeanUtil.DEFAULT_REGEX_TEMPLATE;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class ObjectConfig {
    public static final String DEFAULT_PREFIX = StringPool.DOLLAR;

    protected Object object;
    protected String prefix = DEFAULT_PREFIX;

    protected String levelSeparator = StringPool.DOT;
    protected String multivariableLeft = StringPool.SQUARE_LEFT;
    protected String multivariableRight = StringPool.SQUARE_RIGHT;
    protected boolean ignoreNull = true;
    protected BeanEvaluator beanEvaluator = new DefaultBeanEvaluator();

    protected ObjectConfig(Object object) {
        this.object = object;
    }

    public static ObjectConfig of(Object object) {
        return new ObjectConfig(object);
    }

    public ObjectConfig prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ObjectConfig levelSeparator(String levelSeparator) {
        this.levelSeparator = levelSeparator;
        return this;
    }

    public ObjectConfig multivariable(String multivariableLeft, String multivariableRight) {
        this.multivariableLeft = multivariableLeft;
        this.multivariableRight = multivariableRight;
        return this;
    }

    public ObjectConfig ignoreNull() {
        return this.ignoreNull(true);
    }

    public ObjectConfig ignoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    public ObjectConfig beanEvaluator(BeanEvaluator beanEvaluator) {
        this.beanEvaluator = beanEvaluator;
        return this;
    }

    public ObjectConfig beanJudgmentRegex(String beanJudgmentRegex) {
        BeanEvaluator beanEvaluator = new DefaultBeanEvaluator(beanJudgmentRegex);
        return this.beanEvaluator(beanEvaluator);
    }

    public ObjectConfig beanPackageLevelName(String beanPackageLevelName) {
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, beanPackageLevelName);
        return this.beanJudgmentRegex(regex);
    }

    public ObjectConfig beanPackageLevelNames(String... beanPackageLevelNames) {
        String beanPackageLevelNamesJoined = String.join(StringPool.OR, beanPackageLevelNames);
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, beanPackageLevelNamesJoined);
        return this.beanJudgmentRegex(regex);
    }
}
