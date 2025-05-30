package org.zero.common.test.feature.bean.registration.dynamic;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Conditional(AnnotateConditional.CustomCondition.class)
@Component
class AnnotateConditional {
    static class CustomCondition extends SpringBootCondition {
         static final ConditionMessage.Builder message = ConditionMessage.forCondition("Monitor");

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String monitorEnable = context.getEnvironment().getProperty("monitor.enable");
            if ("true".equals(monitorEnable) || "on".equals(monitorEnable)) {
                return ConditionOutcome.match(message.available("enable"));
            } else if ("false".equals(monitorEnable) || "off".equals(monitorEnable)) {
                return ConditionOutcome.noMatch(message.because("disable"));
            }
            return ConditionOutcome.noMatch(message.because("non match"));
        }
    }
}
