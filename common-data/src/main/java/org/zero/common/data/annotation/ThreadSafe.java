package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 指示被标注的类或接口是线程安全的。
 * <p>
 * 标注在类型或包上，声明该类型的实例可以安全地被多个线程并发访问，无需额外的外部同步。
 * 与 {@link NotThreadSafe} 互为反向声明。
 * <p>
 * 该注解仅作为文档和静态分析标记，不改变运行时行为。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, PACKAGE})
@Documented
public @interface ThreadSafe {
}
