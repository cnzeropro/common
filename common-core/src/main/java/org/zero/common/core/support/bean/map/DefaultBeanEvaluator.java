package org.zero.common.core.support.bean.map;

import org.zero.common.core.util.hutool.core.bean.BeanUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class DefaultBeanEvaluator implements BeanEvaluator {
    protected final String judgmentRegex;

    public DefaultBeanEvaluator() {
        this(BeanUtil.DEFAULT_REGEX);
    }

    public DefaultBeanEvaluator(String judgmentRegex) {
        this.judgmentRegex = judgmentRegex;
    }

    @Override
    public boolean evaluate(Object object) {
        return BeanUtil.isBean(object, judgmentRegex) || BeanUtil.isJavaStrictBean(object);
    }
}
