package org.zero.common.core.extension.common.data.model.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过 {@link org.zero.common.data.model.view.Result} 包装。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/26
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SkipResultWrapping {
}
