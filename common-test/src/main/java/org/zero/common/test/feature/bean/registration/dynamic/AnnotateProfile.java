package org.zero.common.test.feature.bean.registration.dynamic;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 注解 {@linkplain Profile @Profile}
 * <p>
 * 本质上还是使用 {@link org.springframework.context.annotation.Condition} 功能，参见：{@link org.springframework.context.annotation.ProfileCondition}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Profile("dev")
@Component
class AnnotateProfile {
}
